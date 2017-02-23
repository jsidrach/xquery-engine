package edu.ucsd.cse232b.jsidrach.xquery.optimizer;

import edu.ucsd.cse232b.jsidrach.antlr.XQueryParser;

import java.util.HashMap;

/**
 * XQueryVarsRenamer - renames the variable names of xquery queries so that all are unique
 */
public class XQueryVarsRenamer extends XQuerySerializer {

    /**
     * Current map of variable renames
     */
    private HashMap<String, String> vars;

    /**
     * Name for undefined variables
     */
    private String undefinedVar;

    /**
     * Variable prefix
     */
    private String varPrefix;

    /**
     * Current variable number
     */
    private int varNum;

    /**
     * Public Constructor - Initializes the variables
     */
    public XQueryVarsRenamer() {
        this.vars = new HashMap<>();
        this.undefinedVar = "$Undefined";
        this.varPrefix = "$v";
        this.varNum = 0;
    }

    /**
     * Obtains the next unique variable name
     *
     * @return Next unique variable name
     */
    private String nextVarName() {
        ++varNum;
        return varPrefix + varNum;
    }

    /**
     * Obtains the equivalent unique variable name given a variable name
     *
     * @param var Old variable name
     * @return Unique variable name
     */
    private String getVarName(String var) {
        return vars.getOrDefault(var, undefinedVar);
    }

    /**
     * XQuery (variable)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree, with unique variable names
     */
    @Override
    public String visitXqVariable(XQueryParser.XqVariableContext ctx) {
        return getVarName(ctx.Variable().getText());
    }

    /**
     * XQuery (let)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree, with unique variable names
     */
    @Override
    public String visitXqLet(XQueryParser.XqLetContext ctx) {
        HashMap<String, String> vars = new HashMap<>(this.vars);
        String q = super.visitXqLet(ctx);
        this.vars = vars;
        return q;
    }

    /**
     * XQuery (for let while return - FLWR)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree, with unique variable names
     */
    @Override
    public String visitXqFLWR(XQueryParser.XqFLWRContext ctx) {
        HashMap<String, String> vars = new HashMap<>(this.vars);
        String q = super.visitXqFLWR(ctx);
        this.vars = vars;
        return q;
    }

    /**
     * XQuery - FLWR (for)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree, with unique variable names
     */
    @Override
    public String visitForClause(XQueryParser.ForClauseContext ctx) {
        String q = "";
        q += " for ";
        for (int i = 0; i < ctx.Variable().size(); ++i) {
            String v = nextVarName();
            String xq = visit(ctx.xq(i));
            vars.put(ctx.Variable(i).getText(), v);
            q += v + " in " + xq;
            if (i != (ctx.Variable().size() - 1)) {
                q += ",";
            }
        }
        return q;
    }

    /**
     * XQuery - FLWR (let)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree, with unique variable names
     */
    @Override
    public String visitLetClause(XQueryParser.LetClauseContext ctx) {
        String q = "";
        q += " let ";
        for (int i = 0; i < ctx.Variable().size(); ++i) {
            String v = nextVarName();
            String xq = visit(ctx.xq(i));
            vars.put(ctx.Variable(i).getText(), v);
            q += v + ":=" + xq;
            if (i != (ctx.Variable().size() - 1)) {
                q += ",";
            }
        }
        return q;
    }

    /**
     * XQuery - Condition (some)
     *
     * @param ctx Current parse tree context
     * @return String representation of the abstract syntax tree, with unique variable names
     */
    @Override
    public String visitCondSome(XQueryParser.CondSomeContext ctx) {
        HashMap<String, String> vars = new HashMap<>(this.vars);
        String q = "";
        q += " some ";
        for (int i = 0; i < ctx.Variable().size(); ++i) {
            String v = nextVarName();
            String xq = visit(ctx.xq(i));
            this.vars.put(ctx.Variable(i).getText(), v);
            q += v + " in " + xq;
            if (i != (ctx.Variable().size() - 1)) {
                q += ",";
            }
        }
        q += " satisfies " + visit(ctx.cond());
        this.vars = vars;
        return q;
    }
}
