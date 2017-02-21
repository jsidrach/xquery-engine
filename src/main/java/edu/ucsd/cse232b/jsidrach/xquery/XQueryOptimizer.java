package edu.ucsd.cse232b.jsidrach.xquery;

import edu.ucsd.cse232b.jsidrach.antlr.XQueryParser;

/**
 * XQueryOptimizer - TODO
 * <p>
 * TODO
 * </p>
 */
public class XQueryOptimizer extends edu.ucsd.cse232b.jsidrach.antlr.XQueryBaseVisitor<String> {

    /**
     * Flag to check if the current FLWR expression can be rewritten
     */
    private boolean rewriteFLWR;

    /**
     * Public constructor - Initializes the variables
     */
    public XQueryOptimizer() {
        this.rewriteFLWR = false;
    }

    /*
     * XQuery - Root Rules
     */

    /**
     * XQuery (variable)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitXqVariable(XQueryParser.XqVariableContext ctx) {
        return ctx.Variable().getText();
    }

    /**
     * XQuery (constant)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitXqConstant(XQueryParser.XqConstantContext ctx) {
        return ctx.StringConstant().getText();
    }

    /**
     * XQuery (absolute path)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitXqAbsolutePath(XQueryParser.XqAbsolutePathContext ctx) {
        return visit(ctx.ap());
    }

    /**
     * XQuery (parentheses)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitXqParentheses(XQueryParser.XqParenthesesContext ctx) {
        return "(" + visit(ctx.xq()) + ")";
    }

    /**
     * XQuery (pair)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitXqPair(XQueryParser.XqPairContext ctx) {
        return visit(ctx.xq(0)) + "," + visit(ctx.xq(1));
    }

    /**
     * XQuery (children)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitXqChildren(XQueryParser.XqChildrenContext ctx) {
        return visit(ctx.xq()) + "/" + visit(ctx.rp());
    }

    /**
     * XQuery (all)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitXqAll(XQueryParser.XqAllContext ctx) {
        return visit(ctx.xq()) + "//" + visit(ctx.rp());
    }

    /**
     * XQuery (tag)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitXqTag(XQueryParser.XqTagContext ctx) {
        return "<" + ctx.Identifier(0).getText() + ">{" + visit(ctx.xq()) + "}</" + ctx.Identifier(0).getText() + ">";
    }

    /**
     * XQuery (join)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitXqJoin(XQueryParser.XqJoinContext ctx) {
        return " join("
                + visit(ctx.xq(0)) + "," + visit(ctx.xq(1)) + ","
                + visit(ctx.tagList(0)) + "," + visit(ctx.tagList(1))
                + ")";
    }

    /**
     * XQuery (let)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitXqLet(XQueryParser.XqLetContext ctx) {
        return visit(ctx.letClause()) + " " + visit(ctx.xq());
    }

    /**
     * XQuery (for let while return - FLWR)
     * TODO: Decide whether we can optimize this FLWR or not
     * TODO: Use rewriteFLWR and save previous value of query and rewriteFLRW)
     * TODO: Create new string if it can be optimized
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitXqFLWR(XQueryParser.XqFLWRContext ctx) {
        String q = "";
        q += visit(ctx.forClause());
        if (ctx.letClause() != null) {
            q += visit(ctx.letClause());
        }
        if (ctx.whereClause() != null) {
            q += visit(ctx.whereClause());
        }
        q += visit(ctx.returnClause());
        return q;
    }

    /*
     * XQuery - For Let Where Return (FLWR)
     */

    /**
     * XQuery - FLWR (for)
     * TODO
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitForClause(XQueryParser.ForClauseContext ctx) {
        String q = "";
        q += " for ";
        for (int i = 0; i < ctx.Variable().size(); ++i) {
            q += ctx.Variable(i).getText() + " in " + visit(ctx.xq(i));
            if (i != (ctx.Variable().size() - 1)) {
                q += ",";
            }
        }
        return q;
    }

    /**
     * XQuery - FLWR (let)
     * TODO
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitLetClause(XQueryParser.LetClauseContext ctx) {
        String q = "";
        q += " let ";
        for (int i = 0; i < ctx.Variable().size(); ++i) {
            q += ctx.Variable(i).getText() + ":=" + visit(ctx.xq(i));
            if (i != (ctx.Variable().size() - 1)) {
                q += ",";
            }
        }
        return q;
    }

    /**
     * XQuery - FLWR (where)
     * TODO
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitWhereClause(XQueryParser.WhereClauseContext ctx) {
        return " where " + visit(ctx.cond());
    }

    /**
     * XQuery - FLWR (return)
     * TODO
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitReturnClause(XQueryParser.ReturnClauseContext ctx) {
        return " return " + visit(ctx.xq());
    }

    /*
     * XQuery - Join
     */

    /**
     * XQuery - Join (tagList)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitTagList(XQueryParser.TagListContext ctx) {
        String q = "";
        q += "[";
        for (int i = 0; i < ctx.Identifier().size(); ++i) {
            q += ctx.Identifier(i).getText();
            if (i != (ctx.Identifier().size() - 1)) {
                q += ",";
            }
        }
        q += "]";
        return q;
    }

    /*
     * XQuery - Condition
     */

    /**
     * XQuery - Condition (value equality)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitCondValueEquality(XQueryParser.CondValueEqualityContext ctx) {
        return visit(ctx.xq(0)) + "=" + visit(ctx.xq(1));
    }

    /**
     * XQuery - Condition (identity equality)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitCondIdentityEquality(XQueryParser.CondIdentityEqualityContext ctx) {
        return visit(ctx.xq(0)) + "==" + visit(ctx.xq(1));
    }

    /**
     * XQuery - Condition (empty)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitCondEmpty(XQueryParser.CondEmptyContext ctx) {
        return " empty(" + visit(ctx.xq()) + ")";
    }

    /**
     * XQuery - Condition (some)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitCondSome(XQueryParser.CondSomeContext ctx) {
        String q = "";
        q += " some ";
        for (int i = 0; i < ctx.Variable().size(); ++i) {
            q += ctx.Variable(i).getText() + " in " + visit(ctx.xq(1));
            if (i != (ctx.Variable().size() - 1)) {
                q += ",";
            }
        }
        q += " satisfies " + visit(ctx.cond());
        return q;
    }

    /**
     * XQuery - Condition (parentheses)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitCondParentheses(XQueryParser.CondParenthesesContext ctx) {
        return "(" + visit(ctx.cond()) + ")";
    }

    /**
     * XQuery - Condition (and)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitCondAnd(XQueryParser.CondAndContext ctx) {
        return visit(ctx.cond(0)) + " and " + visit(ctx.cond(1));
    }

    /**
     * XQuery - Condition (or)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitCondOr(XQueryParser.CondOrContext ctx) {
        return visit(ctx.cond(0)) + " or " + visit(ctx.cond(1));
    }

    /**
     * XQuery - Condition (not)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitCondNot(XQueryParser.CondNotContext ctx) {
        return " not " + visit(ctx.cond());
    }

    /*
     * XPath - Absolute Path
     */

    /**
     * XPath - Absolute path (children)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitApChildren(XQueryParser.ApChildrenContext ctx) {
        return visit(ctx.doc()) + "/" + visit(ctx.rp());
    }

    /**
     * XPath - Absolute path (all)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitApAll(XQueryParser.ApAllContext ctx) {
        return visit(ctx.doc()) + "//" + visit(ctx.rp());
    }

    /**
     * XPath - Absolute path (doc)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitApDoc(XQueryParser.ApDocContext ctx) {
        return "doc(" + ctx.StringConstant().getText() + ")";
    }

    /*
     * XPath - Relative Path
     */

    /**
     * XPath - Relative path (tag)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitRpTag(XQueryParser.RpTagContext ctx) {
        return ctx.Identifier().getText();
    }

    /**
     * XPath - Relative path (wildcard)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitRpWildcard(XQueryParser.RpWildcardContext ctx) {
        return "*";
    }

    /**
     * XPath - Relative path (current)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitRpCurrent(XQueryParser.RpCurrentContext ctx) {
        return ".";
    }

    /**
     * XPath - Relative path (parent)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitRpParent(XQueryParser.RpParentContext ctx) {
        return "..";
    }

    /**
     * XPath - Relative path (text)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitRpText(XQueryParser.RpTextContext ctx) {
        return "text()";
    }

    /**
     * XPath - Relative path (attribute)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitRpAttribute(XQueryParser.RpAttributeContext ctx) {
        return "@" + ctx.Identifier().getText();
    }

    /**
     * XPath - Relative path (parentheses)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitRpParentheses(XQueryParser.RpParenthesesContext ctx) {
        return "(" + visit(ctx.rp()) + ")";
    }

    /**
     * XPath - Relative path (children)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitRpChildren(XQueryParser.RpChildrenContext ctx) {
        return visit(ctx.rp(0)) + "/" + visit(ctx.rp(1));
    }

    /**
     * XPath - Relative path (all)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitRpAll(XQueryParser.RpAllContext ctx) {
        return visit(ctx.rp(0)) + "//" + visit(ctx.rp(1));
    }

    /**
     * XPath - Relative path (filter)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitRpFilter(XQueryParser.RpFilterContext ctx) {
        return visit(ctx.rp()) + "[" + visit(ctx.f()) + "]";
    }

    /**
     * XPath - Relative path (pair)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitRpPair(XQueryParser.RpPairContext ctx) {
        return visit(ctx.rp(0)) + "," + visit(ctx.rp(1));
    }

    /*
     * XPath - Filter
     */

    /**
     * XPath - Filter (relative path)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitFRelativePath(XQueryParser.FRelativePathContext ctx) {
        return visit(ctx.rp());
    }

    /**
     * XPath - Filter (value equality)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitFValueEquality(XQueryParser.FValueEqualityContext ctx) {
        return visit(ctx.rp(0)) + "=" + visit(ctx.rp(1));
    }

    /**
     * XPath - Filter (identity equality)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitFIdentityEquality(XQueryParser.FIdentityEqualityContext ctx) {
        return visit(ctx.rp(0)) + "==" + visit(ctx.rp(1));
    }

    /**
     * XPath - Filter (parentheses)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitFParentheses(XQueryParser.FParenthesesContext ctx) {
        return "(" + visit(ctx.f()) + ")";
    }

    /**
     * XPath - Filter (and)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitFAnd(XQueryParser.FAndContext ctx) {
        return visit(ctx.f(0)) + " and " + visit(ctx.f(1));
    }

    /**
     * XPath - Filter (or)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitFOr(XQueryParser.FOrContext ctx) {
        return visit(ctx.f(0)) + " or " + visit(ctx.f(1));
    }

    /**
     * XPath - Filter (not)
     *
     * @param ctx Current parse tree context
     * @return String representation of the optimized abstract syntax tree
     */
    @Override
    public String visitFNot(XQueryParser.FNotContext ctx) {
        return " not " + visit(ctx.f());
    }
}
