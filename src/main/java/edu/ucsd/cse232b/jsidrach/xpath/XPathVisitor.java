package edu.ucsd.cse232b.jsidrach.xpath;

import edu.ucsd.cse232b.jsidrach.antlr.XPathBaseVisitor;
import edu.ucsd.cse232b.jsidrach.antlr.XPathParser;
import org.w3c.dom.Node;

import java.util.LinkedList;

/**
 * XPathVisitor - Visitor for the context tree generated by ANTLR4
 * <p>
 * The traversal of the context tree has to be done manually, recursively calling visit(ctx)<br>
 * Initially, the root of the grammar is invoked<br>
 * Each method modifies the current list of nodes (nodes) and returns it<br>
 * </p>
 */
public class XPathVisitor extends XPathBaseVisitor<LinkedList<Node>> {

    /**
     * Current list of nodes
     */
    private LinkedList<Node> nodes;

    /**
     * Public constructor - Initializes the current list of nodes to an empty linked list
     */
    public XPathVisitor() {
        this.nodes = new LinkedList<>();
    }

    /**
     * Absolute path (children)
     * <pre>
     * [doc(FileName)/rp]
     *   → [rp](root(FileName))
     * </pre>
     *
     * @param ctx Current parse tree context
     * @return List of nodes resulting of the traversal of the relative path starting from the root of the document
     */
    @Override
    public LinkedList<Node> visitApChildren(XPathParser.ApChildrenContext ctx) {
        visit(ctx.doc());
        this.nodes = XPathEvaluator.unique(visit(ctx.rp()));
        return this.nodes;
    }

    /**
     * Absolute path (all)
     * <pre>
     * [doc(FileName)//rp]
     *   → [.//rp](root(FileName))
     * </pre>
     *
     * @param ctx Current parse tree context
     * @return List of nodes resulting of the traversal of the relative path starting at any node in the document
     */
    @Override
    public LinkedList<Node> visitApAll(XPathParser.ApAllContext ctx) {
        this.nodes = XPathEvaluator.descendantsOrSelves(visit(ctx.doc()));
        this.nodes = XPathEvaluator.unique(visit(ctx.rp()));
        return this.nodes;
    }

    /**
     * Absolute path (doc)
     * <pre>
     * [doc(FileName)]
     *   → root(FileName)
     * </pre>
     *
     * @param ctx Current parse tree context
     * @return Singleton list containing the root element of the XML document
     */
    @Override
    public LinkedList<Node> visitApDoc(XPathParser.ApDocContext ctx) {
        this.nodes = XPathEvaluator.root(ctx.StringConstant().getText());
        return this.nodes;
    }

    /**
     * Relative path (tag)
     * <pre>
     * [Identifier](n)
     *   → { c | c ← [∗](n) if tag(c) = Identifier }
     * </pre>
     *
     * @param ctx Current parse tree context
     * @return List of children nodes that have the given identifier
     */
    @Override
    public LinkedList<Node> visitRpTag(XPathParser.RpTagContext ctx) {
        LinkedList<Node> nodes = new LinkedList<>();
        String tag = ctx.Identifier().getText();
        for (Node n : this.nodes) {
            LinkedList<Node> children = XPathEvaluator.children(n);
            for (Node c : children) {
                if (XPathEvaluator.tag(c).equals(tag)) {
                    nodes.add(c);
                }
            }
        }
        this.nodes = nodes;
        return this.nodes;
    }

    /**
     * Relative path (wildcard)
     * <pre>
     * [∗](n)
     *   → children(n)
     * </pre>
     *
     * @param ctx Current parse tree context
     * @return List of children nodes
     */
    @Override
    public LinkedList<Node> visitRpWildcard(XPathParser.RpWildcardContext ctx) {
        LinkedList<Node> nodes = new LinkedList<>();
        for (Node n : this.nodes) {
            nodes.addAll(XPathEvaluator.children(n));
        }
        this.nodes = nodes;
        return this.nodes;
    }

    /**
     * Relative path (current)
     * <pre>
     * [.](n)
     *   → { n }
     * </pre>
     *
     * @param ctx Current parse tree context
     * @return Current list of nodes
     */
    @Override
    public LinkedList<Node> visitRpCurrent(XPathParser.RpCurrentContext ctx) {
        return this.nodes;
    }

    /**
     * Relative path (parent)
     * <pre>
     * [..](n)
     *   → parent(n)
     * </pre>
     *
     * @param ctx Current parse tree context
     * @return List of parent nodes
     */
    @Override
    public LinkedList<Node> visitRpParent(XPathParser.RpParentContext ctx) {
        LinkedList<Node> nodes = new LinkedList<>();
        for (Node n : this.nodes) {
            nodes.addAll(XPathEvaluator.parent(n));
        }
        this.nodes = nodes;
        return this.nodes;
    }

    /**
     * Relative path (text)
     * <pre>
     * [text()](n)
     *   → txt(n)
     * </pre>
     *
     * @param ctx Current parse tree context
     * @return List of text nodes
     */
    @Override
    public LinkedList<Node> visitRpText(XPathParser.RpTextContext ctx) {
        LinkedList<Node> nodes = new LinkedList<>();
        for (Node n : this.nodes) {
            nodes.addAll(XPathEvaluator.txt(n));
        }
        this.nodes = nodes;
        return this.nodes;
    }

    /**
     * Relative path (attribute)
     * <pre>
     * [@Identifier](n)
     *   → attrib(n, Identifier)
     * </pre>
     *
     * @param ctx Current parse tree context
     * @return List of attribute nodes that have the given attribute name
     */
    @Override
    public LinkedList<Node> visitRpAttribute(XPathParser.RpAttributeContext ctx) {
        LinkedList<Node> nodes = new LinkedList<>();
        String attId = ctx.Identifier().getText();
        for (Node n : this.nodes) {
            nodes.addAll(XPathEvaluator.attrib(n, attId));
        }
        this.nodes = nodes;
        return this.nodes;
    }

    /**
     * Relative path (parentheses)
     * <pre>
     * [(rp)](n)
     *   → [rp](n)
     * </pre>
     *
     * @param ctx Current parse tree context
     * @return List of nodes returned by the relative path inside the parentheses
     */
    @Override
    public LinkedList<Node> visitRpParentheses(XPathParser.RpParenthesesContext ctx) {
        return visit(ctx.rp());
    }

    /**
     * Relative path (children)
     * <pre>
     * [rp_1/rp_2](n)
     *   → unique({ y | x ← [rp_1](n), y ← [rp_2](x) })
     * </pre>
     *
     * @param ctx Current parse tree context
     * @return List of distinct nodes obtained by the first relative path concatenated with the second relative path
     */
    @Override
    public LinkedList<Node> visitRpChildren(XPathParser.RpChildrenContext ctx) {
        visit(ctx.rp(0));
        this.nodes = XPathEvaluator.unique(visit(ctx.rp(1)));
        return this.nodes;
    }

    /**
     * Relative path (all)
     * <pre>
     * [rp_1//rp_2](n)
     *   → unique([rp_1/rp_2](n), [rp_1/∗//rp_2](n))
     * </pre>
     *
     * @param ctx Current parse tree context
     * @return List of distinct nodes obtained by the first relative path concatenated with the second relative path,
     * union the list of nodes obtained by the first relative path concatenated with the second relative path,
     * skipping any number of descendants
     */
    @Override
    public LinkedList<Node> visitRpAll(XPathParser.RpAllContext ctx) {
        this.nodes = XPathEvaluator.descendantsOrSelves(visit(ctx.rp(0)));
        this.nodes = XPathEvaluator.unique(visit(ctx.rp(1)));
        return this.nodes;
    }

    /**
     * Relative path (filter)
     * <pre>
     * [rp[f]](n)
     *   → { x | x ← [rp](n) if [f](x) }
     * </pre>
     *
     * @param ctx Current parse tree context
     * @return List of nodes by preserving only the relative paths that satisfy the filter
     */
    @Override
    public LinkedList<Node> visitRpFilter(XPathParser.RpFilterContext ctx) {
        LinkedList<Node> nodes = new LinkedList<>();
        LinkedList<Node> rp = visit(ctx.rp());
        for (Node n : rp) {
            this.nodes = XPathEvaluator.singleton(n);
            if (!visit(ctx.f()).isEmpty()) {
                nodes.add(n);
            }

        }
        this.nodes = nodes;
        return this.nodes;
    }

    /**
     * Relative path (pair)
     * <pre>
     * [rp_1, rp_2](n)
     *   → [rp_1](n), [rp_2](n)
     * </pre>
     *
     * @param ctx Current parse tree context
     * @return List of nodes resulting of the union of the lists of nodes produced by both relative paths
     */
    @Override
    public LinkedList<Node> visitRpPair(XPathParser.RpPairContext ctx) {
        LinkedList<Node> nodes = new LinkedList<>();
        LinkedList<Node> original = this.nodes;
        nodes.addAll(visit(ctx.rp(0)));
        this.nodes = original;
        nodes.addAll(visit(ctx.rp(1)));
        this.nodes = nodes;
        return this.nodes;
    }

    /**
     * Filter (relative path)
     * <pre>
     * [rp](n)
     *   → [rp](n) ≠ { }
     * </pre>
     * Note: filter functions should not change the current list of nodes
     *
     * @param ctx Current parse tree context
     * @return Current list of nodes if the relative path is not empty
     * - an empty list otherwise
     */
    @Override
    public LinkedList<Node> visitFRelativePath(XPathParser.FRelativePathContext ctx) {
        LinkedList<Node> nodes = this.nodes;
        LinkedList<Node> filter = visit(ctx.rp());
        this.nodes = nodes;
        return filter;
    }

    /**
     * Filter (value equality)
     * <pre>
     * [rp_1 = rp_2](n)
     * [rp_1 eq rp_2](n)
     *   → ∃ x ∈ [rp_1](n) ∃ y ∈ [rp_2](n) / x eq y
     * </pre>
     * Note: filter functions should not change the current list of nodes
     *
     * @param ctx Current parse tree context
     * @return Current list of nodes if there exists some x in the first relative path
     * and some y in the second relative path that are equal
     * - an empty list otherwise
     */
    @Override
    public LinkedList<Node> visitFValueEquality(XPathParser.FValueEqualityContext ctx) {
        LinkedList<Node> nodes = this.nodes;
        LinkedList<Node> l = visit(ctx.rp(0));
        this.nodes = nodes;
        LinkedList<Node> r = visit(ctx.rp(1));
        this.nodes = nodes;
        if (XPathEvaluator.existsEqual(l, r)) {
            return this.nodes;
        }
        return new LinkedList<>();
    }

    /**
     * Filter (identity equality)
     * <pre>
     * [rp_1 == rp_2](n)
     * [rp_1 is rp_2](n)
     *   → ∃ x ∈ [rp_1](n) ∃ y ∈ [rp_2](n) / x is y
     * </pre>
     * Note: filter functions should not change the current list of nodes
     *
     * @param ctx Current parse tree context
     * @return Current list of nodes if there exists some x in the first relative path
     * and some y in the second relative path that reference the same node
     * - an empty list otherwise
     */
    @Override
    public LinkedList<Node> visitFIdentityEquality(XPathParser.FIdentityEqualityContext ctx) {
        LinkedList<Node> nodes = this.nodes;
        LinkedList<Node> l = visit(ctx.rp(0));
        this.nodes = nodes;
        LinkedList<Node> r = visit(ctx.rp(1));
        this.nodes = nodes;
        if (XPathEvaluator.existsSame(l, r)) {
            return this.nodes;
        }
        return new LinkedList<>();
    }

    /**
     * Filter (parentheses)
     * <pre>
     * [(f)](n)
     *   → [f](n)
     * </pre>
     * Note: filter functions should not change the current list of nodes
     *
     * @param ctx Current parse tree context
     * @return List of nodes returned by the filter inside the parentheses
     */
    @Override
    public LinkedList<Node> visitFParentheses(XPathParser.FParenthesesContext ctx) {
        return visit(ctx.f());
    }

    /**
     * Filter (and)
     * <pre>
     * [f_1 and f_2](n)
     *   → [f_1](n) ∧ [f_2](n)
     * </pre>
     * Note: filter functions should not change the current list of nodes
     *
     * @param ctx Current parse tree context
     * @return Current list of nodes if both of the filters evaluate to true
     * - an empty list otherwise
     */
    @Override
    public LinkedList<Node> visitFAnd(XPathParser.FAndContext ctx) {
        if ((visit(ctx.f(0)).isEmpty()) || (visit(ctx.f(1)).isEmpty())) {
            return new LinkedList<>();
        }
        return this.nodes;
    }

    /**
     * Filter (or)
     * <pre>
     * [f_1 or f_2](n)
     *   → [f_1](n) ∨ [f_2](n)
     * </pre>
     * Note: filter functions should not change the current list of nodes
     *
     * @param ctx Current parse tree context
     * @return Current list of nodes if any of the filters evaluates to true
     * - an empty list otherwise
     */
    @Override
    public LinkedList<Node> visitFOr(XPathParser.FOrContext ctx) {
        if ((visit(ctx.f(0)).isEmpty()) && (visit(ctx.f(1)).isEmpty())) {
            return new LinkedList<>();
        }
        return this.nodes;
    }

    /**
     * Filter (not)
     * <pre>
     * [not f](n)
     *   → ¬[f](n)
     * </pre>
     * Note: filter functions should not change the current list of nodes
     *
     * @param ctx Current parse tree context
     * @return Current list of nodes if the filter evaluates to false
     * - an empty list otherwise
     */
    @Override
    public LinkedList<Node> visitFNot(XPathParser.FNotContext ctx) {
        if (visit(ctx.f()).isEmpty()) {
            return this.nodes;
        }
        return new LinkedList<>();
    }
}
