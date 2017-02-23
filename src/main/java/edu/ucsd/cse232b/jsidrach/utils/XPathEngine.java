package edu.ucsd.cse232b.jsidrach.utils;

import edu.ucsd.cse232b.jsidrach.antlr.XPathLexer;
import edu.ucsd.cse232b.jsidrach.antlr.XPathParser;
import edu.ucsd.cse232b.jsidrach.xpath.XPathVisitor;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.w3c.dom.Node;

import java.io.FileInputStream;
import java.util.LinkedList;

/**
 * XPathEngine - Utility functions to deal with XPathVisitor
 */
public class XPathEngine {

    /**
     * Executes a XPath query given an ANTLRInputStream
     *
     * @param ANTLRInput Input query
     * @return List of result nodes
     */
    public static LinkedList<Node> Query(ANTLRInputStream ANTLRInput) {
        XPathLexer xPathLexer = new XPathLexer(ANTLRInput);
        CommonTokenStream tokens = new CommonTokenStream(xPathLexer);
        XPathParser xPathParser = new XPathParser(tokens);
        // Parse using ap (Absolute Path) as root rule
        ParseTree xPathTree = xPathParser.ap();
        XPathVisitor xPathVisitor = new XPathVisitor();
        return xPathVisitor.visit(xPathTree);
    }

    /**
     * Executes a XPath query given a file containing it
     *
     * @param file File containing the XPath query
     * @return List of result nodes
     * @throws Exception Exception if file is not found
     */
    public static LinkedList<Node> Query(FileInputStream file) throws Exception {
        return Query(new ANTLRInputStream(file));
    }

    /**
     * Executes a XPath query
     *
     * @param query XPath query string
     * @return List of result nodes
     */
    public static LinkedList<Node> Query(String query) {
        return Query(new ANTLRInputStream(query));
    }
}
