package edu.ucsd.cse232b.jsidrach.xquery.optimizer;

/**
 * XQueryOptimizer - optimizes xquery queries by rewriting FLWR as joins whenever possible
 * <p>
 * Prerequisite: all variable names must be unique
 * </p>
 */
public class XQueryOptimizer extends XQuerySerializer {

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

    // TODO: Overwrite rules that are related to the optimization/rewriting process
}
