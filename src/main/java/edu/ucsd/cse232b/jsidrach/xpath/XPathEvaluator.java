package edu.ucsd.cse232b.jsidrach.xpath;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * XPathEvaluator - Collection of functions to evaluate an XPath expression
 * <p>
 * All methods are static and public, to be accessed by XPathVisitor
 * </p>
 */
public class XPathEvaluator {

    /**
     * Reads a XML file and loads its content into a list of nodes
     *
     * @param fn Name of the XML file (relative to the executable's current path)
     * @return Root of the XML tree corresponding to the loaded document, as a node list
     */
    static List<Node> root(String fn) {
        List<Node> nodes = new LinkedList<>();
        try {
            // Remove quotes (first and last character)
            File xmlFile = new File(fn.substring(1, fn.length() - 1));
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            // Ignore non-relevant whitespace
            docFactory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(xmlFile);
            // Normalize document
            doc.getDocumentElement().normalize();
            nodes.add(doc.getDocumentElement());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nodes;
    }

    /**
     * Returns a list of nodes without duplicates
     *
     * @param nodes List of nodes with possible duplicates
     * @return List of nodes without duplicates
     */
    static List<Node> unique(List<Node> nodes) {
        List<Node> uNodes = new LinkedList<>();
        for (Node n : nodes) {
            if (!uNodes.contains(n)) {
                uNodes.add(n);
            }
        }
        return uNodes;
    }

    /**
     * Returns the list of children of an element node
     *
     * @param n Node
     * @return List of node's children, ordered according to the document order
     */
    public static List<Node> children(Node n) {
        NodeList children = n.getChildNodes();
        List<Node> nodes = new LinkedList<>();
        for (int i = 0; i < children.getLength(); ++i) {
            nodes.add(children.item(i));
        }
        return nodes;
    }

    /**
     * Returns the parent of a node
     *
     * @param n Node
     * @return Singleton list containing the parent of the element node, if it has a parent
     * - an empty list otherwise
     */
    public static List<Node> parent(Node n) {
        List<Node> nodes = new LinkedList<>();
        Node p = n.getParentNode();
        if (p != null) {
            nodes.add(p);
        }
        return nodes;
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
     * @return Singleton list containing the text node associated to the element node, if it has text
     * - an empty list otherwise
     */
    public static List<Node> txt(Node n) {
        List<Node> nodes = new LinkedList<>();
        List<Node> children = XPathEvaluator.children(n);
        for (Node c : children) {
            if ((c.getNodeType() == Node.TEXT_NODE) && (c.getTextContent() != null)) {
                nodes.add(c);
                return nodes;
            }
        }
        return nodes;
    }

    /**
     * Returns the attribute with the given name of the node
     *
     * @param n       Node
     * @param attName Node's attribute name
     * @return Singleton list containing the attribute of the node, if it has such attribute
     * - an empty list otherwise
     */
    public static List<Node> attrib(Node n, String attName) {
        List<Node> nodes = new LinkedList<>();
        if (n.getNodeType() != Node.ELEMENT_NODE) {
            return nodes;
        }
        Node att = ((Element) n).getAttributeNode(attName);
        if (att != null) {
            nodes.add(att);
        }
        return nodes;
    }
}
