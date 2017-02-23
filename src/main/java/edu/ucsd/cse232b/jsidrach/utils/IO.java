package edu.ucsd.cse232b.jsidrach.utils;

import org.w3c.dom.Node;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
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
}
