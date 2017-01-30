package edu.ucsd.cse232b.jsidrach.utils;

import edu.ucsd.cse232b.jsidrach.xpath.XPathVisitor;
import edu.ucsd.cse232b.jsidrach.xpath.parser.XPathLexer;
import edu.ucsd.cse232b.jsidrach.xpath.parser.XPathParser;
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
import java.util.List;

/**
 * IO - Utility functions to deal with Input/Output
 */
public class IO {

    /**
     * Executes a XPath query given an ANTLRInputStream
     *
     * @param ANTLRInput Input query
     * @return List of result nodes
     */
    private static List<Node> XPathQuery(ANTLRInputStream ANTLRInput) {
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
    public static List<Node> XPathQuery(FileInputStream file) throws Exception {
        return XPathQuery(new ANTLRInputStream(file));
    }

    /**
     * Executes a XPath query
     *
     * @param query XPath query string
     * @return List of result nodes
     */
    public static List<Node> XPathQuery(String query) {
        return XPathQuery(new ANTLRInputStream(query));
    }

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
     * @param ns List of nodes
     * @return String containing the XML plaintext representations of the nodes (without a common root)
     * @throws Exception Internal error
     */
    public static String NodesToString(List<Node> ns) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        // Set indentation to two spaces
        tf.setAttribute("indent-number", 2);
        Transformer ts = tf.newTransformer();
        // XML document
        ts.setOutputProperty(OutputKeys.METHOD, "xml");
        // Encoding
        ts.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        // Do not include root node
        ts.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        // Indent results
        ts.setOutputProperty(OutputKeys.INDENT, "yes");
        // Join the output of each node
        String output = "<!-- Number of nodes: " + ns.size() + " -->\n";
        for (int i = 0; i < ns.size(); ++i) {
            output += "<!-- Node #" + (i + 1) + " -->\n";
            output += IO.NodeToString(ns.get(i), ts) + "\n";
        }
        return output;
    }
}
