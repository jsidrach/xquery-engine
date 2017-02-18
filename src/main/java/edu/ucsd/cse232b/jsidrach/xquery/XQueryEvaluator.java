package edu.ucsd.cse232b.jsidrach.xquery;

import edu.ucsd.cse232b.jsidrach.utils.IO;
import edu.ucsd.cse232b.jsidrach.xpath.XPathEvaluator;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.LinkedList;
import java.util.List;

/**
 * XQueryEvaluator - Collection of functions to evaluate a XQuery expression
 * <p>
 * All methods are public, to be accessed by XQueryVisitor
 * </p>
 */
public class XQueryEvaluator extends XPathEvaluator {

    /**
     * Document - Used to create nodes
     */
    private Document doc;

    /**
     * Public Constructor - Initializes the document
     *
     * @throws Exception Internal error
     */
    public XQueryEvaluator() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        this.doc = builder.newDocument();
    }

    /**
     * Makes a new element node given a tag and a list of nodes corresponding to its children
     *
     * @param tag   Tag of the new element node
     * @param nodes List of nodes that will be appended (as a copy) as children of the new element node
     * @return New element node with the given tag and all the given nodes as its children
     */
    public Node makeElem(String tag, LinkedList<Node> nodes) {
        Node elem = doc.createElement(tag);
        for (Node n : nodes) {
            elem.appendChild(doc.importNode(n, true));
        }
        return elem;
    }

    /**
     * Makes a new text node given its content
     *
     * @param content Text content of de node
     * @return New text node with the given content
     */
    public Node makeText(String content) {
        return doc.createTextNode(content);
    }

    /**
     * Calculates the key of a node - only depends on the values of the nodes in the tag list
     *
     * @param node Node to compute the key of
     * @param tags List of tags of the node the key depends on
     * @return Key of the node
     */
    public static String keyNodeTags(Node node, List<TerminalNode> tags) {
        String key = "";
        if (node.getNodeType() != Node.ELEMENT_NODE) {
            return key;
        }
        Element n = (Element) node;
        for (TerminalNode tag : tags) {
            Node tagNode = n.getElementsByTagName(tag.getText()).item(0);
            if (tagNode != null) {
                try {
                    key += IO.NodesToString(children(tagNode), false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return key;
    }
}
