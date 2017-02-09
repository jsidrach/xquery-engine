package edu.ucsd.cse232b.jsidrach.xquery;

import edu.ucsd.cse232b.jsidrach.xpath.XPathEvaluator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
    public Node makeElem(String tag, List<Node> nodes) {
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
}
