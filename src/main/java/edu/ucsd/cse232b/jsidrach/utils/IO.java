package edu.ucsd.cse232b.jsidrach.utils;

import edu.ucsd.cse232b.jsidrach.antlr.XPathLexer;
import edu.ucsd.cse232b.jsidrach.antlr.XPathParser;
import edu.ucsd.cse232b.jsidrach.antlr.XQueryLexer;
import edu.ucsd.cse232b.jsidrach.antlr.XQueryParser;
import edu.ucsd.cse232b.jsidrach.xpath.XPathVisitor;
import edu.ucsd.cse232b.jsidrach.xquery.XQueryOptimizer;
import edu.ucsd.cse232b.jsidrach.xquery.XQueryVisitor;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.w3c.dom.Node;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

/**
 * IO - Utility functions to deal with Input/Output
 */
public class IO {

    /**
     * Transforms a node into its XML string representation
     *
     * @param n  Node
     * @param ts Transformer from node to string
     * @return String containing the XML plaintext representation of the node
     * @throws Exception Internal error
     */
    private static String NodeToString(Node n, Transformer ts) throws Exception {
        // Serialize attribute nodes manually
        if (n.getNodeType() == Node.ATTRIBUTE_NODE) {
            return "@" + n.toString().trim();
        }
        // Standard method
        StringWriter buffer = new StringWriter();
        ts.transform(new DOMSource(n), new StreamResult(buffer));
        return buffer.toString().trim();
    }

    /**
     * Transforms a list of nodes into their XML string representation
     *
     * @param ns      List of nodes
     * @param verbose Flag to print a comment with the number of output nodes and before each one
     * @return String containing the XML plaintext representations of the nodes (without a common root)
     * @throws Exception Internal error
     */
    public static String NodesToString(List<Node> ns, boolean verbose) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        // Set indentation to two spaces
        tf.setAttribute("indent-number", 2);
        Transformer ts = tf.newTransformer();
        // Standard XML document
        ts.setOutputProperty(OutputKeys.METHOD, "xml");
        ts.setOutputProperty(OutputKeys.STANDALONE, "yes");
        // Encoding
        ts.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        // Indent results
        ts.setOutputProperty(OutputKeys.INDENT, "yes");
        // Do not include xml declaration if there is more than one root node
        if (verbose || (ns.size() != 1)) {
            ts.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        } else {
            ts.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        }
        // Join the output of each node
        String output = "";
        if (verbose) {
            output += "<!-- Number of nodes: " + ns.size() + " -->\n";
        }
        for (int i = 0; i < ns.size(); ++i) {
            if (verbose) {
                output += "<!-- Node #" + (i + 1) + " -->\n";
            }
            output += IO.NodeToString(ns.get(i), ts) + "\n";
        }
        return output;
    }


    /**
     * Executes a XPath query given an ANTLRInputStream
     *
     * @param ANTLRInput Input query
     * @return List of result nodes
     */
    private static LinkedList<Node> XPathQuery(ANTLRInputStream ANTLRInput) {
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
    public static LinkedList<Node> XPathQuery(FileInputStream file) throws Exception {
        return XPathQuery(new ANTLRInputStream(file));
    }

    /**
     * Executes a XPath query
     *
     * @param query XPath query string
     * @return List of result nodes
     */
    public static LinkedList<Node> XPathQuery(String query) {
        return XPathQuery(new ANTLRInputStream(query));
    }

    /**
     * Executes a XQuery query given an ANTLRInputStream
     *
     * @param ANTLRInput Input query
     * @return List of result nodes
     */
    private static LinkedList<Node> XQueryQuery(ANTLRInputStream ANTLRInput) throws Exception {
        XQueryLexer xQueryLexer = new XQueryLexer(ANTLRInput);
        CommonTokenStream tokens = new CommonTokenStream(xQueryLexer);
        XQueryParser xQueryParser = new XQueryParser(tokens);
        // Parse using xq (XQuery) as root rule
        ParseTree xQueryTree = xQueryParser.xq();
        XQueryVisitor xQueryVisitor = new XQueryVisitor();
        return xQueryVisitor.visit(xQueryTree);
    }

    /**
     * Executes a XQuery query given a file containing it
     *
     * @param file File containing the XQuery query
     * @return List of result nodes
     * @throws Exception Exception if file is not found
     */
    public static LinkedList<Node> XQueryQuery(FileInputStream file) throws Exception {
        return XQueryQuery(new ANTLRInputStream(file));
    }

    /**
     * Executes a XQuery query
     *
     * @param query XQuery query string
     * @return List of result nodes
     */
    public static LinkedList<Node> XQueryQuery(String query) throws Exception {
        return XQueryQuery(new ANTLRInputStream(query));
    }

    /**
     * Executes a XQuery optimized query given an ANTLRInputStream
     *
     * @param ANTLRInput Input query
     * @return Rewritten query
     */
    private static String XQueryOptimizedQuery(ANTLRInputStream ANTLRInput) throws Exception {
        XQueryLexer xQueryLexer = new XQueryLexer(ANTLRInput);
        CommonTokenStream tokens = new CommonTokenStream(xQueryLexer);
        XQueryParser xQueryParser = new XQueryParser(tokens);
        // Parse using xq (XQuery) as root rule
        ParseTree xQueryTree = xQueryParser.xq();
        // Optimize query
        XQueryOptimizer xQueryVisitor = new XQueryOptimizer();
        return xQueryVisitor.visit(xQueryTree);
    }

    /**
     * Executes a XQuery optimized query given a file containing it
     *
     * @param file File containing the XQuery query
     * @return Rewritten query
     * @throws Exception Exception if file is not found
     */
    public static String XQueryOptimizedQuery(FileInputStream file) throws Exception {
        return XQueryOptimizedQuery(new ANTLRInputStream(file));
    }

    /**
     * Executes a XQuery optimized query
     *
     * @param query XQuery query string
     * @return Rewritten query
     */
    public static String XQueryOptimizedQuery(String query) throws Exception {
        return XQueryOptimizedQuery(new ANTLRInputStream(query));
    }
}
