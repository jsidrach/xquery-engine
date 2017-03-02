package edu.ucsd.cse232b.jsidrach.xquery.optimizer;

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
 *   := 'for' v_1 'in' path_1, ..., v_n 'in' path_n
 *      'where' cond
 *      'return' return
 * return
 *   := var
 *    | return ',' return
 *    | '&lt;' n '&gt;' return '&lt;/' n '&lt;'
 *    | path
 * path
 *   := ('doc(' FileName ')' | var) ('/' | '//') rp
 * cond
 *   := (var | StringLiteral) ('eq' | '=') (var | StringLiteral)
 *    | cond 'and' cond
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
     * Public constructor - Initializes the variables
     */
    public XQueryOptimizer() {
    }

    // TODO: Work in phases (state machine)
    // TODO: Overwrite rules that are related to the optimization/rewriting process
}
