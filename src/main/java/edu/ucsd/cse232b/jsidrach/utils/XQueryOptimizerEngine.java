package edu.ucsd.cse232b.jsidrach.utils;

import edu.ucsd.cse232b.jsidrach.antlr.XQueryLexer;
import edu.ucsd.cse232b.jsidrach.antlr.XQueryParser;
import edu.ucsd.cse232b.jsidrach.xquery.optimizer.XQueryOptimizer;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;

/**
 * TODO
 */
public class XQueryOptimizerEngine {

    /**
     * Optimizes a XQuery query given an ANTLRInputStream
     *
     * @param ANTLRInput Input query
     * @param verbose    Flag to output the intermediate rewritten queries to stderr
     * @return Optimized query
     */
    public static String Optimize(ANTLRInputStream ANTLRInput, boolean verbose) throws Exception {
        // Phase 1 - Rename variables
        String renamedQuery = XQueryVarsRenamerEngine.RenameVars(ANTLRInput);
        if (verbose) {
            System.err.println("Phase 1 - Variable Renamer");
            System.err.println("--------------------------");
            System.err.println(renamedQuery);
        }
        // Phase 2 - Optimize query
        XQueryLexer xQueryLexer = new XQueryLexer(new ANTLRInputStream(renamedQuery));
        CommonTokenStream tokens = new CommonTokenStream(xQueryLexer);
        XQueryParser xQueryParser = new XQueryParser(tokens);
        // Parse using xq (XQuery) as root rule
        ParseTree xQueryTree = xQueryParser.xq();
        XQueryOptimizer xQueryOptimizer = new XQueryOptimizer();
        String optimizedQuery = XQueryFormatterEngine.Format(xQueryOptimizer.visit(xQueryTree));
        if (verbose) {
            System.err.println("Phase 2 - Optimizer");
            System.err.println("-------------------");
            System.err.println(optimizedQuery);
        }
        return optimizedQuery;
    }

    /**
     * Optimizes a XQuery query given a file containing it
     *
     * @param file    File containing the XQuery query
     * @param verbose Flag to output the intermediate rewritten queries to stderr
     * @return Optimized query
     * @throws Exception Exception if file is not found
     */
    public static String Optimize(FileInputStream file, boolean verbose) throws Exception {
        return Optimize(new ANTLRInputStream(file), verbose);
    }

    /**
     * Optimizes a XQuery query
     *
     * @param query   XQuery query string
     * @param verbose Flag to output the intermediate rewritten queries to stderr
     * @return Optimized query
     */
    public static String Optimize(String query, boolean verbose) throws Exception {
        return Optimize(new ANTLRInputStream(query), verbose);
    }
}
