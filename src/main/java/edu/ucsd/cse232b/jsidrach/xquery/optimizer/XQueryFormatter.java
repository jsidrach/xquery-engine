package edu.ucsd.cse232b.jsidrach.xquery.optimizer;

import edu.ucsd.cse232b.jsidrach.antlr.XQueryParser;

/**
 * XQueryFormatter - formats (prettifies) xquery queries
 */
public class XQueryFormatter extends XQuerySerializer {

    /**
     * Current level of indentation
     */
    private int level;

    /**
     * Spaces per level
     */
    private int spacesPerLevel;

    /**
     * Extra spaces
     */
    private int extraSpaces;

    /**
     * Maximum number of characters a subquery can have before printing it into separate lines
     */
    private int maxInline;

    /**
     * Public constructor - Initializes the variables
     */
    public XQueryFormatter() {
        this.level = 0;
        this.spacesPerLevel = 2;
        this.extraSpaces = 0;
        this.maxInline = 20;
    }

    /**
     * Indents a string at the current level
     *
     * @param s String to be indented
     * @return String Indented string
     */
    private String indent(String s) {
        String q = "";
        int spaces = this.spacesPerLevel * this.level + this.extraSpaces;
        for (int i = 0; i < spaces; ++i) {
            q += " ";
        }
        q += s;
        return q;
    }

    /**
     * Creates a new line at the current level
     *
     * @param s String with the contents of the line
     * @return Indented string, with a new line at the end
     */
    private String line(String s) {
        return indent(s + System.lineSeparator());
    }

    /**
     * Trims the left whitespace of the string
     *
     * @param s String to be trimmed from the left
     * @return String with the left whitespace removed
     */
    private String lTrim(String s) {
        int i = 0;
        while ((i < s.length()) && (Character.isWhitespace(s.charAt(i)))) {
            ++i;
        }
        return s.substring(i, s.length());
    }

    /**
     * Trims the right whitespace of the string
     *
     * @param s String to be trimmed from the right
     * @return String with the right whitespace removed
     */
    private String rTrim(String s) {
        int i = s.length() - 1;
        while ((i >= 0) && (Character.isWhitespace(s.charAt(i)))) {
            --i;
        }
        return s.substring(0, i + 1);
    }

    /*
     * XQuery - Root Rules
     */

    /**
     * XQuery (variable)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitXqVariable(XQueryParser.XqVariableContext ctx) {
        return line(ctx.Variable().getText());
    }

    /**
     * XQuery (constant)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitXqConstant(XQueryParser.XqConstantContext ctx) {
        return line(ctx.StringConstant().getText());
    }

    /**
     * XQuery (absolute path)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitXqAbsolutePath(XQueryParser.XqAbsolutePathContext ctx) {
        return line(visit(ctx.ap()));
    }

    /**
     * XQuery (parentheses)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitXqParentheses(XQueryParser.XqParenthesesContext ctx) {
        String q = "";
        q += indent("(");
        ++this.extraSpaces;
        q += lTrim(visit(ctx.xq()));
        --this.extraSpaces;
        q = rTrim(q) + ")" + System.lineSeparator();
        return q;
    }

    /**
     * XQuery (pair)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitXqPair(XQueryParser.XqPairContext ctx) {
        String q = "";
        q += visit(ctx.xq(0));
        q = rTrim(q) + "," + System.lineSeparator();
        q += visit(ctx.xq(1));
        return q;
    }

    /**
     * XQuery (children)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitXqChildren(XQueryParser.XqChildrenContext ctx) {
        String q = "";
        q += visit(ctx.xq());
        q = rTrim(q) + "/";
        q += visit(ctx.rp()) + System.lineSeparator();
        return q;
    }

    /**
     * XQuery (all)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitXqAll(XQueryParser.XqAllContext ctx) {
        String q = "";
        q += visit(ctx.xq());
        q = rTrim(q) + "//";
        q += visit(ctx.rp()) + System.lineSeparator();
        return q;
    }

    /**
     * XQuery (tag)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitXqTag(XQueryParser.XqTagContext ctx) {
        String q = "";
        q += line("<" + ctx.Identifier(0).getText() + ">{");
        ++level;
        String xq = visit(ctx.xq());
        --level;
        String endTag = "}</" + ctx.Identifier(1).getText() + ">";
        if (xq.trim().length() <= this.maxInline) {
            q = rTrim(q) + xq.trim() + lTrim(endTag);
        } else {
            q += rTrim(xq) + System.lineSeparator() + line(endTag);
        }
        return q;
    }

    /**
     * XQuery (join)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitXqJoin(XQueryParser.XqJoinContext ctx) {
        String q = "";
        String join = "join(";
        q += indent(join);
        this.extraSpaces += join.length();
        q += visit(ctx.xq(0)).trim();
        q += "," + System.lineSeparator();
        q += visit(ctx.xq(1));
        q = rTrim(q) + "," + System.lineSeparator();
        q += visit(ctx.tagList(0));
        q = rTrim(q) + "," + System.lineSeparator();
        q += visit(ctx.tagList(1));
        q = rTrim(q) + ")" + System.lineSeparator();
        this.extraSpaces -= join.length();
        return q;
    }

    /**
     * XQuery (let)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitXqLet(XQueryParser.XqLetContext ctx) {
        String q = "";
        q += visit(ctx.letClause());
        ++level;
        q += visit(ctx.xq());
        --level;
        return q;
    }

    /**
     * XQuery (for let while return - FLWR)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
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
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitForClause(XQueryParser.ForClauseContext ctx) {
        String q = "";
        String f = "for ";
        q += indent(f);
        this.extraSpaces += f.length();
        for (int i = 0; i < ctx.Variable().size(); ++i) {
            String s = ctx.Variable(i).getText() + " in ";
            if (i != 0) {
                q += indent(s);
            } else {
                q += s;
            }
            this.extraSpaces += s.length();
            q += visit(ctx.xq(i)).trim();
            if (i != (ctx.Variable().size() - 1)) {
                q += ",";
            }
            q += System.lineSeparator();
            this.extraSpaces -= s.length();
        }
        this.extraSpaces -= f.length();
        return q;
    }

    /**
     * XQuery - FLWR (let)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitLetClause(XQueryParser.LetClauseContext ctx) {
        String q = "";
        String let = "let ";
        q += indent(let);
        this.extraSpaces += let.length();
        for (int i = 0; i < ctx.Variable().size(); ++i) {
            String s = ctx.Variable(i).getText() + " := ";
            if (i != 0) {
                q += indent(s);
            } else {
                q += s;
            }
            this.extraSpaces += s.length();
            q += visit(ctx.xq(i)).trim();
            if (i != (ctx.Variable().size() - 1)) {
                q += ",";
            }
            q += System.lineSeparator();
            this.extraSpaces -= s.length();
        }
        this.extraSpaces -= let.length();
        return q;
    }

    /**
     * XQuery - FLWR (where)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitWhereClause(XQueryParser.WhereClauseContext ctx) {
        String q = "";
        String where = "where ";
        q += indent(where);
        this.extraSpaces += where.length();
        q += lTrim(visit(ctx.cond()));
        this.extraSpaces -= where.length();
        return q;
    }

    /**
     * XQuery - FLWR (return)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitReturnClause(XQueryParser.ReturnClauseContext ctx) {
        String q = "";
        String r = "return ";
        q += indent(r);
        this.extraSpaces += r.length();
        q += lTrim(visit(ctx.xq()));
        this.extraSpaces -= r.length();
        return q;
    }

    /*
     * XQuery - Join
     */

    /**
     * XQuery - Join (tagList)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitTagList(XQueryParser.TagListContext ctx) {
        String q = "";
        q += "[";
        for (int i = 0; i < ctx.Identifier().size(); ++i) {
            q += ctx.Identifier(i).getText();
            if (i != (ctx.Identifier().size() - 1)) {
                q += ", ";
            }
        }
        q += "]";
        return line(q);
    }

    /*
     * XQuery - Condition
     */

    /**
     * XQuery - Condition (value equality)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitCondValueEquality(XQueryParser.CondValueEqualityContext ctx) {
        String q = "";
        String eq = "= ";
        this.extraSpaces += eq.length();
        String xq1 = visit(ctx.xq(0));
        String xq2 = visit(ctx.xq(1));
        this.extraSpaces -= eq.length();
        if ((xq1.trim().length() + xq2.trim().length()) <= this.maxInline) {
            q += rTrim(xq1) + " " + eq + lTrim(xq2);
        } else {
            q += xq1 + indent(eq) + lTrim(xq2);
        }
        return q;
    }

    /**
     * XQuery - Condition (identity equality)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitCondIdentityEquality(XQueryParser.CondIdentityEqualityContext ctx) {
        String q = "";
        String is = "== ";
        this.extraSpaces += is.length();
        String xq1 = visit(ctx.xq(0));
        String xq2 = visit(ctx.xq(1));
        this.extraSpaces -= is.length();
        if ((xq1.trim().length() + xq2.trim().length()) <= this.maxInline) {
            q += rTrim(xq1) + " " + is + lTrim(xq2);
        } else {
            q += xq1 + indent(is) + lTrim(xq2);
        }
        return q;
    }

    /**
     * XQuery - Condition (empty)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitCondEmpty(XQueryParser.CondEmptyContext ctx) {
        String q = "";
        String empty = "empty(";
        q += indent(empty);
        this.extraSpaces += empty.length();
        q += visit(ctx.xq()).trim();
        q += ")" + System.lineSeparator();
        this.extraSpaces -= empty.length();
        return q;
    }

    /**
     * XQuery - Condition (some)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitCondSome(XQueryParser.CondSomeContext ctx) {
        String q = "";
        String some = "some ";
        q += indent(some);
        this.extraSpaces += some.length();
        for (int i = 0; i < ctx.Variable().size(); ++i) {
            String s = ctx.Variable(i).getText() + " in ";
            if (i != 0) {
                q += indent(s);
            } else {
                q += s;
            }
            this.extraSpaces += s.length();
            q += visit(ctx.xq(i)).trim();
            if (i != (ctx.Variable().size() - 1)) {
                q += ",";
            }
            q += System.lineSeparator();
            this.extraSpaces -= s.length();
        }
        this.extraSpaces -= some.length();
        String satisfies = "satisfies ";
        q += indent(satisfies);
        this.extraSpaces += satisfies.length();
        q += lTrim(visit(ctx.cond()));
        this.extraSpaces -= satisfies.length();
        return q;
    }

    /**
     * XQuery - Condition (parentheses)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitCondParentheses(XQueryParser.CondParenthesesContext ctx) {
        String q = "";
        q += indent("(");
        ++this.extraSpaces;
        q += lTrim(visit(ctx.cond()));
        --this.extraSpaces;
        q = rTrim(q) + ")";
        return q;
    }

    /**
     * XQuery - Condition (and)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitCondAnd(XQueryParser.CondAndContext ctx) {
        String q = "";
        String and = "and ";
        this.extraSpaces += and.length();
        q += visit(ctx.cond(0));
        this.extraSpaces -= and.length();
        q += indent(and);
        this.extraSpaces += and.length();
        q += lTrim(visit(ctx.cond(1)));
        this.extraSpaces -= and.length();
        return q;
    }

    /**
     * XQuery - Condition (or)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitCondOr(XQueryParser.CondOrContext ctx) {
        String q = "";
        String or = "or ";
        this.extraSpaces += or.length();
        q += visit(ctx.cond(0));
        this.extraSpaces -= or.length();
        q += indent(or);
        this.extraSpaces += or.length();
        q += lTrim(visit(ctx.cond(1)));
        this.extraSpaces -= or.length();
        return q;
    }

    /**
     * XQuery - Condition (not)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitCondNot(XQueryParser.CondNotContext ctx) {
        String q = "";
        String not = "not ";
        q += indent(not);
        this.extraSpaces += not.length();
        q += lTrim(visit(ctx.cond()));
        this.extraSpaces -= not.length();
        return q;
    }

    /*
     * XPath - Absolute Path
     */

    /**
     * XPath - Absolute path (children)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitApChildren(XQueryParser.ApChildrenContext ctx) {
        return visit(ctx.doc()) + "/" + visit(ctx.rp());
    }

    /**
     * XPath - Absolute path (all)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitApAll(XQueryParser.ApAllContext ctx) {
        return visit(ctx.doc()) + "//" + visit(ctx.rp());
    }

    /**
     * XPath - Absolute path (doc)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
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
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitRpTag(XQueryParser.RpTagContext ctx) {
        return ctx.Identifier().getText();
    }

    /**
     * XPath - Relative path (wildcard)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitRpWildcard(XQueryParser.RpWildcardContext ctx) {
        return "*";
    }

    /**
     * XPath - Relative path (current)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitRpCurrent(XQueryParser.RpCurrentContext ctx) {
        return ".";
    }

    /**
     * XPath - Relative path (parent)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitRpParent(XQueryParser.RpParentContext ctx) {
        return "..";
    }

    /**
     * XPath - Relative path (text)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitRpText(XQueryParser.RpTextContext ctx) {
        return "text()";
    }

    /**
     * XPath - Relative path (attribute)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitRpAttribute(XQueryParser.RpAttributeContext ctx) {
        return "@" + ctx.Identifier().getText();
    }

    /**
     * XPath - Relative path (parentheses)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitRpParentheses(XQueryParser.RpParenthesesContext ctx) {
        return "(" + visit(ctx.rp()) + ")";
    }

    /**
     * XPath - Relative path (children)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitRpChildren(XQueryParser.RpChildrenContext ctx) {
        return visit(ctx.rp(0)) + "/" + visit(ctx.rp(1));
    }

    /**
     * XPath - Relative path (all)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitRpAll(XQueryParser.RpAllContext ctx) {
        return visit(ctx.rp(0)) + "//" + visit(ctx.rp(1));
    }

    /**
     * XPath - Relative path (filter)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitRpFilter(XQueryParser.RpFilterContext ctx) {
        return visit(ctx.rp()) + "[" + visit(ctx.f()) + "]";
    }

    /**
     * XPath - Relative path (pair)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitRpPair(XQueryParser.RpPairContext ctx) {
        return visit(ctx.rp(0)) + ", " + visit(ctx.rp(1));
    }

    /*
     * XPath - Filter
     */

    /**
     * XPath - Filter (relative path)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitFRelativePath(XQueryParser.FRelativePathContext ctx) {
        return visit(ctx.rp());
    }

    /**
     * XPath - Filter (value equality)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitFValueEquality(XQueryParser.FValueEqualityContext ctx) {
        return visit(ctx.rp(0)) + " = " + visit(ctx.rp(1));
    }

    /**
     * XPath - Filter (identity equality)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitFIdentityEquality(XQueryParser.FIdentityEqualityContext ctx) {
        return visit(ctx.rp(0)) + " == " + visit(ctx.rp(1));
    }

    /**
     * XPath - Filter (parentheses)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitFParentheses(XQueryParser.FParenthesesContext ctx) {
        return "(" + visit(ctx.f()) + ")";
    }

    /**
     * XPath - Filter (and)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitFAnd(XQueryParser.FAndContext ctx) {
        return visit(ctx.f(0)) + " and " + visit(ctx.f(1));
    }

    /**
     * XPath - Filter (or)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitFOr(XQueryParser.FOrContext ctx) {
        return visit(ctx.f(0)) + " or " + visit(ctx.f(1));
    }

    /**
     * XPath - Filter (not)
     *
     * @param ctx Current parse tree context
     * @return Formatted string representation of the abstract syntax tree
     */
    @Override
    public String visitFNot(XQueryParser.FNotContext ctx) {
        return "not " + visit(ctx.f());
    }
}
