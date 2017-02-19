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
     * Current string representation of the abstract syntax tree
     */
    private String query;

    /**
     * Current level of indentation
     */
    private int level;

    /**
     * Spaces per level
     */
    private int spacesPerLevel;

    /**
     * Flag to check if the current FLWR expression can be rewritten
     */
    private boolean rewriteFLWR;

    /**
     * Public constructor - Initializes the variables
     */
    public XQueryOptimizer() {
        this.query = "";
        this.level = 0;
        this.spacesPerLevel = 2;
        this.rewriteFLWR = false;
    }

    /**
     * Appends a query string to the output
     *
     * @param query   Query string to be appended to the output
     * @param inline  Flag to ignore the indentation
     * @param newline Flag to add a new line at the end of the query string
     */
    private void output(String query, boolean inline, boolean newline) {
        if (!inline) {
            int spaces = this.spacesPerLevel * this.level;
            for (int i = 0; i < spaces; ++i) {
                this.query += " ";
            }
        }
        this.query += query;
        if (newline) {
            this.query += "\n";
        }
    }

    /**
     * Appends a query string to the output, with indentation and adding a new line at the end of the query string
     *
     * @param query Query string to be appended to the output
     */
    private void line(String query) {
        this.output(query, false, true);
    }

    /**
     * Appends a query string to the output, without indentation and without a new line at the end of the query string
     *
     * @param query Query string to be appended to the output
     */
    private void inline(String query) {
        this.output(query, true, false);
    }

    /**
     * Trims the right whitespace of the current query string
     */
    private void rTrim() {
        int i = this.query.length() - 1;
        while ((i >= 0) && (Character.isWhitespace(this.query.charAt(i)))) {
            i--;
        }
        this.query = this.query.substring(0, i + 1);
    }

    /*
     * XQuery - Root Rules
     */

    /**
     * XQuery (variable)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitXqVariable(XQueryParser.XqVariableContext ctx) {
        line(ctx.Variable().getText());
        return this.query;
    }

    /**
     * XQuery (constant)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitXqConstant(XQueryParser.XqConstantContext ctx) {
        line(ctx.StringConstant().getText());
        return this.query;
    }

    /**
     * XQuery (absolute path)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitXqAbsolutePath(XQueryParser.XqAbsolutePathContext ctx) {
        output("", false, false);
        visit(ctx.ap());
        inline("\n");
        return this.query;
    }

    /**
     * XQuery (parentheses)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitXqParentheses(XQueryParser.XqParenthesesContext ctx) {
        line("(");
        ++level;
        visit(ctx.xq());
        --level;
        line(")");
        return this.query;
    }

    /**
     * XQuery (pair)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitXqPair(XQueryParser.XqPairContext ctx) {
        visit(ctx.xq(0));
        rTrim();
        output(",", true, true);
        visit(ctx.xq(1));
        return this.query;
    }

    /**
     * XQuery (children)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitXqChildren(XQueryParser.XqChildrenContext ctx) {
        visit(ctx.xq());
        rTrim();
        ++level;
        output("/", true, false);
        visit(ctx.rp());
        inline("\n");
        --level;
        return this.query;
    }

    /**
     * XQuery (all)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitXqAll(XQueryParser.XqAllContext ctx) {
        visit(ctx.xq());
        rTrim();
        ++level;
        output("//", true, false);
        visit(ctx.rp());
        inline("\n");
        --level;
        return this.query;
    }

    /**
     * XQuery (tag)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitXqTag(XQueryParser.XqTagContext ctx) {
        line("<" + ctx.Identifier(0).getText() + ">{");
        ++level;
        visit(ctx.xq());
        --level;
        line("}</" + ctx.Identifier(0).getText() + ">");
        return this.query;
    }

    /**
     * XQuery (join)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitXqJoin(XQueryParser.XqJoinContext ctx) {
        line("join");
        line("(");
        ++level;
        visit(ctx.xq(0));
        --level;
        rTrim();
        output(",", true, true);
        ++level;
        visit(ctx.xq(1));
        --level;
        rTrim();
        output(",", true, true);
        ++level;
        visit(ctx.tagList(0));
        --level;
        rTrim();
        output(",", true, true);
        ++level;
        visit(ctx.tagList(1));
        --level;
        line(")");
        return this.query;
    }

    /**
     * XQuery (let)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitXqLet(XQueryParser.XqLetContext ctx) {
        visit(ctx.letClause());
        ++level;
        visit(ctx.xq());
        --level;
        return this.query;
    }

    /**
     * XQuery (for let while return - FLWR)
     * TODO: Decide whether we can optimize this FLWR or not
     * TODO: Use rewriteFLWR and save previous value of query and rewriteFLRW)
     * TODO: Create new string if it can be optimized
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitXqFLWR(XQueryParser.XqFLWRContext ctx) {
        visit(ctx.forClause());
        if (ctx.letClause() != null) {
            visit(ctx.letClause());
        }
        if (ctx.whereClause() != null) {
            visit(ctx.whereClause());
        }
        visit(ctx.returnClause());
        return this.query;
    }

    /*
     * XQuery - For Let Where Return (FLWR)
     */

    /**
     * XQuery - FLWR (for)
     * TODO
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitForClause(XQueryParser.ForClauseContext ctx) {
        line("for");
        ++level;
        for (int i = 0; i < ctx.Variable().size(); ++i) {
            line(ctx.Variable(i).getText() + " in ");
            ++level;
            visit(ctx.xq(i));
            if (i != (ctx.Variable().size() - 1)) {
                rTrim();
                output(",", true, true);
            }
            --level;
        }
        --level;
        return this.query;
    }

    /**
     * XQuery - FLWR (let)
     * TODO
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitLetClause(XQueryParser.LetClauseContext ctx) {
        line("let");
        ++level;
        for (int i = 0; i < ctx.Variable().size(); ++i) {
            line(ctx.Variable(i).getText() + " := ");
            ++level;
            visit(ctx.xq(i));
            if (i != (ctx.Variable().size() - 1)) {
                rTrim();
                output(",", true, true);
            }
            --level;
        }
        --level;
        return this.query;
    }

    /**
     * XQuery - FLWR (where)
     * TODO
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitWhereClause(XQueryParser.WhereClauseContext ctx) {
        line("where");
        ++level;
        visit(ctx.cond());
        --level;
        return this.query;
    }

    /**
     * XQuery - FLWR (return)
     * TODO
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitReturnClause(XQueryParser.ReturnClauseContext ctx) {
        line("return");
        ++level;
        visit(ctx.xq());
        --level;
        return this.query;
    }

    /*
     * XQuery - Join
     */

    /**
     * XQuery - Join (tagList)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitTagList(XQueryParser.TagListContext ctx) {
        String tagList = "[";
        for (int i = 0; i < ctx.Identifier().size(); ++i) {
            tagList += ctx.Identifier(i).getText();
            if (i != (ctx.Identifier().size() - 1)) {
                tagList += ", ";
            }
        }
        tagList += "]";
        line(tagList);
        return this.query;
    }

    /*
     * XQuery - Condition
     */

    /**
     * XQuery - Condition (value equality)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitCondValueEquality(XQueryParser.CondValueEqualityContext ctx) {
        visit(ctx.xq(0));
        line("=");
        visit(ctx.xq(1));
        return this.query;
    }

    /**
     * XQuery - Condition (identity equality)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitCondIdentityEquality(XQueryParser.CondIdentityEqualityContext ctx) {
        visit(ctx.xq(0));
        line("==");
        visit(ctx.xq(1));
        return this.query;
    }

    /**
     * XQuery - Condition (empty)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitCondEmpty(XQueryParser.CondEmptyContext ctx) {
        line("empty");
        line("(");
        ++level;
        visit(ctx.xq());
        --level;
        line(")");
        return this.query;
    }

    /**
     * XQuery - Condition (some)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitCondSome(XQueryParser.CondSomeContext ctx) {
        line("some");
        ++level;
        for (int i = 0; i < ctx.Variable().size(); ++i) {
            line(ctx.Variable(i).getText() + " in ");
            ++level;
            visit(ctx.xq(i));
            if (i != (ctx.Variable().size() - 1)) {
                rTrim();
                output(",", true, true);
            }
            --level;
        }
        --level;
        line("satisfies");
        ++level;
        visit(ctx.cond());
        --level;
        return this.query;
    }

    /**
     * XQuery - Condition (parentheses)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitCondParentheses(XQueryParser.CondParenthesesContext ctx) {
        line("(");
        ++level;
        visit(ctx.cond());
        --level;
        line(")");
        return this.query;
    }

    /**
     * XQuery - Condition (and)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitCondAnd(XQueryParser.CondAndContext ctx) {
        visit(ctx.cond(0));
        line("or");
        visit(ctx.cond(1));
        return this.query;
    }

    /**
     * XQuery - Condition (or)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitCondOr(XQueryParser.CondOrContext ctx) {
        visit(ctx.cond(0));
        line("or");
        visit(ctx.cond(1));
        return this.query;
    }

    /**
     * XQuery - Condition (not)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitCondNot(XQueryParser.CondNotContext ctx) {
        line("not");
        visit(ctx.cond());
        return this.query;
    }

    /*
     * XPath - Absolute Path
     */

    /**
     * XPath - Absolute path (children)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitApChildren(XQueryParser.ApChildrenContext ctx) {
        visit(ctx.doc());
        inline("/");
        visit(ctx.rp());
        return this.query;
    }

    /**
     * XPath - Absolute path (all)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitApAll(XQueryParser.ApAllContext ctx) {
        visit(ctx.doc());
        inline("//");
        visit(ctx.rp());
        return this.query;
    }

    /**
     * XPath - Absolute path (doc)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitApDoc(XQueryParser.ApDocContext ctx) {
        inline("doc(" + ctx.StringConstant().getText() + ")");
        return this.query;
    }

    /*
     * XPath - Relative Path
     */

    /**
     * XPath - Relative path (tag)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitRpTag(XQueryParser.RpTagContext ctx) {
        inline(ctx.Identifier().getText());
        return this.query;
    }

    /**
     * XPath - Relative path (wildcard)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitRpWildcard(XQueryParser.RpWildcardContext ctx) {
        inline("*");
        return this.query;
    }

    /**
     * XPath - Relative path (current)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitRpCurrent(XQueryParser.RpCurrentContext ctx) {
        inline(".");
        return this.query;
    }

    /**
     * XPath - Relative path (parent)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitRpParent(XQueryParser.RpParentContext ctx) {
        inline("..");
        return this.query;
    }

    /**
     * XPath - Relative path (text)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitRpText(XQueryParser.RpTextContext ctx) {
        inline("text()");
        return this.query;
    }

    /**
     * XPath - Relative path (attribute)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitRpAttribute(XQueryParser.RpAttributeContext ctx) {
        inline("@" + ctx.Identifier().getText());
        return this.query;
    }

    /**
     * XPath - Relative path (parentheses)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitRpParentheses(XQueryParser.RpParenthesesContext ctx) {
        inline("(");
        visit(ctx.rp());
        inline(")");
        return this.query;
    }

    /**
     * XPath - Relative path (children)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitRpChildren(XQueryParser.RpChildrenContext ctx) {
        visit(ctx.rp(0));
        inline("/");
        visit(ctx.rp(1));
        return this.query;
    }

    /**
     * XPath - Relative path (all)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitRpAll(XQueryParser.RpAllContext ctx) {
        visit(ctx.rp(0));
        inline("//");
        visit(ctx.rp(1));
        return this.query;
    }

    /**
     * XPath - Relative path (filter)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitRpFilter(XQueryParser.RpFilterContext ctx) {
        visit(ctx.rp());
        inline("[");
        visit(ctx.f());
        inline("]");
        return this.query;
    }

    /**
     * XPath - Relative path (pair)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitRpPair(XQueryParser.RpPairContext ctx) {
        inline("(");
        visit(ctx.rp(0));
        inline(", ");
        visit(ctx.rp(1));
        inline(")");
        return this.query;
    }

    /*
     * XPath - Filter
     */

    /**
     * XPath - Filter (relative path)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitFRelativePath(XQueryParser.FRelativePathContext ctx) {
        visit(ctx.rp());
        return this.query;
    }

    /**
     * XPath - Filter (value equality)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitFValueEquality(XQueryParser.FValueEqualityContext ctx) {
        visit(ctx.rp(0));
        inline(" = ");
        visit(ctx.rp(1));
        return this.query;
    }

    /**
     * XPath - Filter (identity equality)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitFIdentityEquality(XQueryParser.FIdentityEqualityContext ctx) {
        visit(ctx.rp(0));
        inline(" == ");
        visit(ctx.rp(1));
        return this.query;
    }

    /**
     * XPath - Filter (parentheses)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitFParentheses(XQueryParser.FParenthesesContext ctx) {
        inline("(");
        visit(ctx.f());
        inline(")");
        return this.query;
    }

    /**
     * XPath - Filter (and)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitFAnd(XQueryParser.FAndContext ctx) {
        visit(ctx.f(0));
        inline(" and ");
        visit(ctx.f(1));
        return this.query;
    }

    /**
     * XPath - Filter (or)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitFOr(XQueryParser.FOrContext ctx) {
        visit(ctx.f(0));
        inline(" or ");
        visit(ctx.f(1));
        return this.query;
    }

    /**
     * XPath - Filter (not)
     *
     * @param ctx Current parse tree context
     * @return String representation of the current abstract syntax tree
     */
    @Override
    public String visitFNot(XQueryParser.FNotContext ctx) {
        inline(" not ");
        visit(ctx.f());
        return this.query;
    }
}
