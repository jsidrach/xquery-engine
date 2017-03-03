package edu.ucsd.cse232b.jsidrach.xquery.optimizer;

import edu.ucsd.cse232b.jsidrach.antlr.XQueryParser;

/**
 * XQueryOptimizer - optimizes xquery queries by rewriting FLWR as joins whenever possible
 * <p><b>Prerequisites</b></p>
 * <ul>
 * <li>Only queries in the subset of xquery described below are optimized</li>
 * <li>All variable names must be unique (taken care of in XQueryVarsRenamer)</li>
 * </ul>
 * <p>
 * XQuery subset grammar
 * (any rule not explicitly defined behaves identically as in the general XQuery grammar)
 * </p>
 * <pre>
 * XQuery
 *   → 'for' v_1 'in' path_1, ..., v_n 'in' path_n
 *     'where' cond
 *     'return' return
 * return
 *   → var
 *   | return ',' return
 *   | '&lt;' n '&gt;' '{' return '}' '&lt;/' n '&lt;'
 *   | path
 * path
 *   → (('doc' '(' StringConstant ')') | var) ('/' | '//') rp
 * cond
 *   → (var | StringConstant) ('eq' | '=') (var | StringConstant)
 *   | cond 'and' cond
 * </pre>
 * Instead of creating a new grammar/visitor for the optimizer,
 * the query is optimized only if its AST conforms to the following restrictions
 * <ul>
 * <li>The root query is a for let where clause (FLWR)</li>
 * <li>The FLWR clause does not contain a let clause</li>
 * <li>The for clause sub queries only contain paths</li>
 * <li>The where clause only contains equality tests using variables and string literals,
 * joined by the boolean and operator</li>
 * <li>The return clause only contains variables, paths, tags and the concatenation of two return clauses</li>
 * <li>Paths are defined as document or variable, then '/' or '//', then a relative path</li>
 * </ul>
 */
public class XQueryOptimizer extends XQuerySerializer {

    // TODO: Document
    private enum State {
        INITIAL, CHECK_FOR, CHECK_WHERE, CHECK_RETURN, OPTIMIZING
    }

    // TODO: Document
    private class Info {
        boolean optimizable = true;
        State state = State.INITIAL;
    }

    // TODO: Document
    private Info info;

    /**
     * Public constructor - Initializes the variables
     */
    public XQueryOptimizer() {
        info = new Info();
    }

    /*
     * Optimization related rules
     */

    /**
     * XQuery (for let while return - FLWR)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree
     */
    @Override
    public String visitXqFLWR(XQueryParser.XqFLWRContext ctx) {
        // TODO: Backup, rethink initial state
        if (info.state != State.INITIAL) {
            info.optimizable = false;
        }
        // Check that the query can be optimized
        info.state = State.CHECK_FOR;
        visit(ctx.forClause());
        if (!info.optimizable) {
            return super.visit(ctx);
        }
        if (ctx.letClause() != null) {
            info.optimizable = false;
            return super.visit(ctx);
        }
        info.state = State.CHECK_WHERE;
        if (ctx.whereClause() != null) {
            visit(ctx.whereClause());
        }
        if (!info.optimizable) {
            return super.visit(ctx);
        }
        info.state = State.CHECK_RETURN;
        visit(ctx.returnClause());
        if (!info.optimizable) {
            return super.visit(ctx);
        }
        // Optimize the query
        info.state = State.OPTIMIZING;
        // TODO
        return super.visit(ctx);
    }

    /**
     * XQuery - FLWR (for)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree
     */
    @Override
    public String visitForClause(XQueryParser.ForClauseContext ctx) {
        // TODO
        return super.visit(ctx);
    }

    /**
     * XQuery - FLWR (where)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree
     */
    @Override
    public String visitWhereClause(XQueryParser.WhereClauseContext ctx) {
        // TODO
        return super.visit(ctx);
    }

    /**
     * XQuery - FLWR (return)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree
     */
    @Override
    public String visitReturnClause(XQueryParser.ReturnClauseContext ctx) {
        // TODO
        return super.visit(ctx);
    }

    /*
     * Unconditionally valid rules for optimized queries
     */

    /**
     * XQuery (variable)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree
     */
    @Override
    public String visitXqVariable(XQueryParser.XqVariableContext ctx) {
        String suffix = "";
        if (info.state == State.OPTIMIZING) {
            suffix = "/*";
        }
        return super.visit(ctx) + suffix;
    }

    /**
     * XQuery - Condition (value equality)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree
     */
    @Override
    public String visitCondValueEquality(XQueryParser.CondValueEqualityContext ctx) {
        // TODO
        return super.visit(ctx);
    }

    /*
     * Conditionally invalid rules for optimized queries
     */

    /**
     * XQuery (constant)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree
     */
    @Override
    public String visitXqConstant(XQueryParser.XqConstantContext ctx) {
        if (info.state == State.CHECK_FOR) {
            info.optimizable = false;
        }
        return super.visit(ctx);
    }

    /**
     * XQuery (absolute path)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree
     */
    @Override
    public String visitXqAbsolutePath(XQueryParser.XqAbsolutePathContext ctx) {
        if (info.state == State.CHECK_WHERE) {
            info.optimizable = false;
        }
        return super.visit(ctx);
    }

    /**
     * XQuery (pair)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree
     */
    @Override
    public String visitXqPair(XQueryParser.XqPairContext ctx) {
        if ((info.state == State.CHECK_FOR) || (info.state == State.CHECK_WHERE)) {
            info.optimizable = false;
        }
        return super.visit(ctx);
    }

    /**
     * XQuery (children)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree
     */
    @Override
    public String visitXqChildren(XQueryParser.XqChildrenContext ctx) {
        if (info.state == State.CHECK_WHERE) {
            info.optimizable = false;
        }
        return super.visit(ctx);
    }

    /**
     * XQuery (all)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree
     */
    @Override
    public String visitXqAll(XQueryParser.XqAllContext ctx) {
        if (info.state == State.CHECK_WHERE) {
            info.optimizable = false;
        }
        return super.visit(ctx);
    }

    /**
     * XQuery (tag)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree
     */
    @Override
    public String visitXqTag(XQueryParser.XqTagContext ctx) {
        if ((info.state == State.CHECK_FOR) || (info.state == State.CHECK_WHERE)) {
            info.optimizable = false;
        }
        return super.visit(ctx);
    }

    /*
     * Unconditionally invalid rules for optimized queries
     */

    /**
     * XQuery (join)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree
     */
    @Override
    public String visitXqJoin(XQueryParser.XqJoinContext ctx) {
        info.optimizable = false;
        return super.visit(ctx);
    }

    /**
     * XQuery (let)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree
     */
    @Override
    public String visitXqLet(XQueryParser.XqLetContext ctx) {
        info.optimizable = false;
        return super.visit(ctx);
    }

    /**
     * XQuery - Condition (identity equality)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree
     */
    @Override
    public String visitCondIdentityEquality(XQueryParser.CondIdentityEqualityContext ctx) {
        info.optimizable = false;
        return super.visit(ctx);
    }

    /**
     * XQuery - Condition (empty)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree
     */
    @Override
    public String visitCondEmpty(XQueryParser.CondEmptyContext ctx) {
        info.optimizable = false;
        return super.visit(ctx);
    }

    /**
     * XQuery - Condition (some)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree
     */
    @Override
    public String visitCondSome(XQueryParser.CondSomeContext ctx) {
        info.optimizable = false;
        return super.visit(ctx);
    }

    /**
     * XQuery - Condition (or)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree
     */
    @Override
    public String visitCondOr(XQueryParser.CondOrContext ctx) {
        info.optimizable = false;
        return super.visit(ctx);
    }

    /**
     * XQuery - Condition (not)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree
     */
    @Override
    public String visitCondNot(XQueryParser.CondNotContext ctx) {
        info.optimizable = false;
        return super.visit(ctx);
    }
}
