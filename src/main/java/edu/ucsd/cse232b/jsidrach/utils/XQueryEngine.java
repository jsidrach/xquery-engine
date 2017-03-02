package edu.ucsd.cse232b.jsidrach.utils;

import edu.ucsd.cse232b.jsidrach.antlr.XQueryLexer;
import edu.ucsd.cse232b.jsidrach.antlr.XQueryParser;
import edu.ucsd.cse232b.jsidrach.xquery.XQueryVisitor;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.w3c.dom.Node;

import java.io.FileInputStream;
import java.util.LinkedList;

/**
 * XQueryEngine - Utility functions to deal with XQueryVisitor
 */
public class XQueryEngine {

    /**
     * Executes a XQuery query given an ANTLRInputStream
     *
     * @param ANTLRInput Input query
     * @param verbose    Flag to output log messages
     * @return List of result nodes
     */
    public static LinkedList<Node> Query(ANTLRInputStream ANTLRInput, boolean verbose) throws Exception {
        XQueryLexer xQueryLexer = new XQueryLexer(ANTLRInput);
        CommonTokenStream tokens = new CommonTokenStream(xQueryLexer);
        XQueryParser xQueryParser = new XQueryParser(tokens);
        // Parse using xq (XQuery) as root rule
        ParseTree xQueryTree = xQueryParser.xq();
        XQueryVisitor xQueryVisitor = new XQueryVisitor(verbose);
        return xQueryVisitor.visit(xQueryTree);
    }

    /**
     * Executes a XQuery query given a file containing it
     *
     * @param file    File containing the XQuery query
     * @param verbose Flag to output log messages
     * @return List of result nodes
     * @throws Exception Exception if file is not found
     */
    public static LinkedList<Node> Query(FileInputStream file, boolean verbose) throws Exception {
        return Query(new ANTLRInputStream(file), verbose);
    }

    /**
     * Executes a XQuery query
     *
     * @param query   XQuery query string
     * @param verbose Flag to output log messages
     * @return List of result nodes
     */
    public static LinkedList<Node> Query(String query, boolean verbose) throws Exception {
        return Query(new ANTLRInputStream(query), verbose);
    }
}
