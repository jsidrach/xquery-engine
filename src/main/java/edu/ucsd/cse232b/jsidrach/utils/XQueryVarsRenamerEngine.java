package edu.ucsd.cse232b.jsidrach.utils;

import edu.ucsd.cse232b.jsidrach.antlr.XQueryLexer;
import edu.ucsd.cse232b.jsidrach.antlr.XQueryParser;
import edu.ucsd.cse232b.jsidrach.xquery.optimizer.XQueryVarsRenamer;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;

/**
 * XQueryVarsRenamerEngine - Utility functions to deal with XQueryVarsRenamer
 */
public class XQueryVarsRenamerEngine {

    /**
     * Renames all variables of a XQuery query so they are all unique
     *
     * @param ANTLRInput Input query
     * @return Rewritten query with unique variable names
     */
    public static String RenameVars(ANTLRInputStream ANTLRInput) throws Exception {
        XQueryLexer xQueryLexer = new XQueryLexer(ANTLRInput);
        CommonTokenStream tokens = new CommonTokenStream(xQueryLexer);
        XQueryParser xQueryParser = new XQueryParser(tokens);
        // Parse using xq (XQuery) as root rule
        ParseTree xQueryTree = xQueryParser.xq();
        // Rename variables in the query
        XQueryVarsRenamer xQueryVarsRenamer = new XQueryVarsRenamer();
        return xQueryVarsRenamer.visit(xQueryTree);
    }

    /**
     * Renames all variables of a XQuery query so they are all unique
     *
     * @param file File containing the XQuery query
     * @return Rewritten query with unique variable names
     * @throws Exception Exception if file is not found
     */
    public static String RenameVars(FileInputStream file) throws Exception {
        return RenameVars(new ANTLRInputStream(file));
    }

    /**
     * Renames all variables of a XQuery query so they are all unique
     *
     * @param query XQuery query string
     * @return Rewritten query with unique variable names
     */
    public static String RenameVars(String query) throws Exception {
        return RenameVars(new ANTLRInputStream(query));
    }
}
