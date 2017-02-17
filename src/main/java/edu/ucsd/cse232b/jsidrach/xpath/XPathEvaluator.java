package edu.ucsd.cse232b.jsidrach.xpath;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * XPathEvaluator - Collection of functions to evaluate a XPath expression
 * <p>
 * All methods are static and public, to be accessed by XPathVisitor
 * </p>
 */
public class XPathEvaluator {

    /**
     * Reads a XML file and loads its content into a list of nodes
     *
     * @param fn Name of the XML file (relative to the executable's current path)
     * @return Root of the XML tree corresponding to the loaded document, as a singleton list of nodes
     */
    public static LinkedList<Node> root(String fn) {
        LinkedList<Node> nodes = new LinkedList<>();
        try {
            // Remove quotes (first and last character)
            File xmlFile = new File(fn.substring(1, fn.length() - 1));
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            // Ignore non-relevant whitespace (only works if the XML has an associated DTD)
            docFactory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(xmlFile);
            // Normalize document
            doc.getDocumentElement().normalize();
            nodes.add(doc);
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
    public static LinkedList<Node> unique(List<Node> nodes) {
        LinkedList<Node> uNodes = new LinkedList<>();
        for (Node n : nodes) {
            if (!uNodes.contains(n)) {
                uNodes.add(n);
            }
        }
        return uNodes;
    }

    /**
     * Returns the list of children of a node
     *
     * @param n Node
     * @return List of node's children, ordered according to the document order
     */
    public static LinkedList<Node> children(Node n) {
        LinkedList<Node> nodes = new LinkedList<>();
        NodeList children = n.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            nodes.add(children.item(i));
        }
        return nodes;
    }

    /**
     * Returns a list containing the given nodes and all its descendants
     *
     * @param nodes Nodes
     * @return List of node's descendants and themselves, according to the document order
     */
    public static LinkedList<Node> descendantsOrSelves(List<Node> nodes) {
        LinkedList<Node> queue = new LinkedList<>();
        LinkedList<Node> ns = new LinkedList<>();
        ns.addAll(nodes);
        queue.addAll(nodes);
        while (!queue.isEmpty()) {
            Node c = queue.poll();
            LinkedList<Node> children = XPathEvaluator.children(c);
            ns.addAll(children);
            queue.addAll(children);
        }
        return ns;
    }

    /**
     * Returns the parent of a node
     *
     * @param n Node
     * @return Singleton list containing the parent of the element node, if it has a parent
     * - an empty list otherwise
     */
    public static LinkedList<Node> parent(Node n) {
        LinkedList<Node> nodes = new LinkedList<>();
        // Attribute node's parent is accessed differently
        Node p = (n.getNodeType() == Node.ATTRIBUTE_NODE) ?
                ((Attr) n).getOwnerElement()
                : n.getParentNode();
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
    public static LinkedList<Node> txt(Node n) {
        LinkedList<Node> nodes = new LinkedList<>();
        LinkedList<Node> children = XPathEvaluator.children(n);
        for (Node c : children) {
            if ((c.getNodeType() == Node.TEXT_NODE) && (c.getTextContent() != null) && (!c.getTextContent().isEmpty())) {
                nodes.add(c);
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
    public static LinkedList<Node> attrib(Node n, String attName) {
        LinkedList<Node> nodes = new LinkedList<>();
        if (n.getNodeType() != Node.ELEMENT_NODE) {
            return nodes;
        }
        Element e = (Element) n;
        if (!e.hasAttribute(attName)) {
            return nodes;
        }
        nodes.add(e.getAttributeNode(attName));
        return nodes;
    }

    /**
     * Creates a singleton list from a node
     *
     * @param node Node that will be contained in the singleton
     * @return Singleton containing the given node
     */
    public static LinkedList<Node> singleton(Node node) {
        LinkedList<Node> singleton = new LinkedList<>();
        if (node != null) {
            singleton.add(node);
        }
        return singleton;
    }
}
