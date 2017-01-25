package edu.ucsd.cse232b.jsidrach.xpath;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

/**
 * XPathEvaluator - Collection of functions to evaluate an XPath expression
 * <p>
 * All methods are static and public, to be accessed by the XPathEvaluator
 * </p>
 */
public class XPathEvaluator {

    /**
     * Reads a XML file and loads its content into a list of nodes
     *
     * @param fn Name of the XML file (relative to the executable's current path)
     * @return Root of the XML tree corresponding to the loaded document, as a node list
     * @throws FileNotFoundException Invalid file name
     */
    static List<Node> root(String fn) throws FileNotFoundException {
        return null;
    }

    /**
     * Returns the list of children of an element node
     *
     * @param n Node
     * @return List of node's children, ordered according to the document order
     */
    public static List<Node> children(Node n) {
        NodeList nl = n.getChildNodes();
        List<Node> lln = new LinkedList<>();
        for (int i = 0; i < nl.getLength(); ++i) {
            lln.add(nl.item(i));
        }
        return lln;
    }

    /**
     * Returns the parent of a node
     *
     * @param n Node
     * @return Singleton list containing the parent of the element node, if it has a parent - an empty list otherwise
     */
    public static List<Node> parent(Node n) {
        List<Node> pl = new LinkedList<>();
        Node p = n.getParentNode();
        if (p != null) {
            pl.add(p);
        }
        return pl;
    }

    /**
     * Returns the tag labeling an element node
     *
     * @param n Node
     * @return Tag labeling the element node
     */
    public static String tag(Node n) {
        return n.getNodeName();
    }

    /**
     * Returns the text node associated to an element node
     *
     * @param n Node
     * @return Text node associated to the element node
     */
    public static Node txt(Node n) {
        NodeList nl = n.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            if (nl.item(i).getNodeType() == Node.TEXT_NODE) {
                return nl.item(i);
            }
        }
        return null;
    }

    /**
     * TODO
     *
     * @param n Node
     * @param attName Node's attribute name
     * @return TODO
     */
    public static Node attrib(Node n, String attName) {
        NamedNodeMap atts = n.getAttributes();
        if ((atts != null) && (atts.getNamedItem(attName) != null)) {
            return n;
        }
        return null;
    }
}
