package edu.ucsd.cse232b.jsidrach.xquery.optimizer;

import edu.ucsd.cse232b.jsidrach.antlr.XQueryParser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

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

    /**
     * States of the optimizer
     * <ol>
     * <li>INITIAL - initial state</li>
     * <li>CHECK_FOR - validating that the for clause conforms to the xquery subset grammar</li>
     * <li>CHECK_WHERE - validating that the where clause conforms to the xquery subset grammar</li>
     * <li>CHECK_RETURN - validating that the return clause conforms to the xquery subset grammar</li>
     * <li>OPTIMIZE - optimizing the FLWR expression using join clauses</li>
     * </ol>
     */
    private enum State {
        INITIAL, CHECK_FOR, CHECK_WHERE, CHECK_RETURN, OPTIMIZE
    }

    /**
     * Metadata representation, to be used by the optimizer
     */
    private class Info {
        /**
         * Flag that indicates if the query is optimizable or not
         */
        boolean optimizable = true;

        /**
         * Current state of the optimizer
         */
        State state = State.INITIAL;

        /**
         * Map from variable names to the subqueries they represent
         */
        HashMap<String, String> vars = new HashMap<>();

        /**
         * Map for variable dependencies, where the every (child, parent) is a (key, value),
         * and the variables without dependencies (roots) have their parent set to null
         */
        HashMap<String, String> dependencies = new HashMap<>();

        /**
         * Map from root variables to all the variables that directly or indirectly depend on them
         */
        HashMap<String, LinkedList<String>> subqueries = new HashMap<>();

        /**
         * Map for value equalities, where for every equality in the form of (a = b),
         * the set of equalities of a contains b and the set of equalities of b contains a
         */
        HashMap<String, HashSet<String>> equalities = new HashMap<>();
    }

    /**
     * Current metadata
     */
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
        if ((info.state != State.INITIAL) || (!info.optimizable)) {
            info.optimizable = false;
            return super.visitXqFLWR(ctx);
        }
        // Check that the query can be optimized
        info.state = State.CHECK_FOR;
        visit(ctx.forClause());
        if (!info.optimizable) {
            return super.visitXqFLWR(ctx);
        }
        if (ctx.letClause() != null) {
            info.optimizable = false;
            return super.visitXqFLWR(ctx);
        }
        info.state = State.CHECK_WHERE;
        if (ctx.whereClause() != null) {
            visit(ctx.whereClause());
        }
        if (!info.optimizable) {
            return super.visitXqFLWR(ctx);
        }
        info.state = State.CHECK_RETURN;
        visit(ctx.returnClause());
        if (!info.optimizable) {
            return super.visitXqFLWR(ctx);
        }
        // Optimize the query
        info.state = State.OPTIMIZE;
        visit(ctx.forClause());
        if (ctx.whereClause() != null) {
            visit(ctx.whereClause());
        }
        String q = "";
        // TODO: algorithm to translate an FLWR clause into join clauses
        // Clear metadata
        info = new Info();
        return super.visitXqFLWR(ctx);
    }

    /**
     * XQuery - FLWR (for)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree
     */
    @Override
    public String visitForClause(XQueryParser.ForClauseContext ctx) {
        if (info.state != State.OPTIMIZE) {
            return super.visitForClause(ctx);
        }
        for (int i = 0; i < ctx.Variable().size(); ++i) {
            String varName = ctx.Variable(i).getText();
            String varQuery = visit(ctx.xq(i));
            // Add the subquery the variable traverses
            info.vars.put(varName, varQuery);
            // Variable with dependency
            if (varQuery.startsWith("$")) {
                String parent = varQuery.split("/")[0];
                info.dependencies.put(varName, parent);
                // Find root of the dependency relationship
                String root = parent;
                while (info.dependencies.get(root) != null) {
                    root = info.dependencies.get(root);
                }
                // Add this var to the group of variables that depend on the root
                info.subqueries.get(root).add(varName);
            }
            // Root variable
            else {
                info.dependencies.put(varName, null);
                LinkedList<String> vars = new LinkedList<>();
                vars.add(varName);
                info.subqueries.put(varName, vars);
            }
        }
        return super.visitForClause(ctx);
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
        if (info.state == State.OPTIMIZE) {
            suffix = "/*";
        }
        return super.visitXqVariable(ctx) + suffix;
    }

    /**
     * XQuery - Condition (value equality)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree
     */
    @Override
    public String visitCondValueEquality(XQueryParser.CondValueEqualityContext ctx) {
        if (info.state == State.OPTIMIZE) {
            String left = visit(ctx.xq(0));
            String right = visit(ctx.xq(1));
            info.equalities.getOrDefault(left, new HashSet<>()).add(right);
            info.equalities.getOrDefault(right, new HashSet<>()).add(left);
        }
        return super.visitCondValueEquality(ctx);
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
        return super.visitXqConstant(ctx);
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
        return super.visitXqAbsolutePath(ctx);
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
        return super.visitXqPair(ctx);
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
        return super.visitXqChildren(ctx);
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
        return super.visitXqAll(ctx);
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
        return super.visitXqTag(ctx);
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
        return super.visitXqJoin(ctx);
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
        return super.visitXqLet(ctx);
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
        return super.visitCondIdentityEquality(ctx);
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
        return super.visitCondEmpty(ctx);
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
        return super.visitCondSome(ctx);
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
        return super.visitCondOr(ctx);
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
        return super.visitCondNot(ctx);
    }
}
