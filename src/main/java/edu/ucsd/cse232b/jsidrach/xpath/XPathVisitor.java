package edu.ucsd.cse232b.jsidrach.xpath;

import edu.ucsd.cse232b.jsidrach.xpath.parser.XPathBaseVisitor;
import edu.ucsd.cse232b.jsidrach.xpath.parser.XPathParser;
import org.w3c.dom.Node;

import java.util.LinkedList;
import java.util.List;

/**
 * XPathVisitor - Visitor for the context tree generated by ANTLR4
 * <p>
 * The traversal of the context tree has to be done manually, recursively calling visit(ctx)<br>
 * Initially, the root of the grammar is invoked<br>
 * Each method modifies the current list of nodes (nodes) and returns it<br>
 * </p>
 */
public class XPathVisitor extends XPathBaseVisitor<List<Node>> {

    /**
     * Current list of nodes
     */
    private List<Node> nodes;

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
     * @return TODO
     */
    @Override
    public List<Node> visitApChildren(XPathParser.ApChildrenContext ctx) {
        visit(ctx.doc());
        visit(ctx.rp());
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
     * @return TODO
     */
    @Override
    public List<Node> visitApAll(XPathParser.ApAllContext ctx) {
        visit(ctx.doc());
        List<Node> nodes = new LinkedList<>();
        LinkedList<Node> q = new LinkedList<>();
        nodes.addAll(this.nodes);
        q.addAll(this.nodes);
        while (!q.isEmpty()) {
            Node n = q.poll();
            List<Node> children = XPathEvaluator.children(n);
            nodes.addAll(children);
            q.addAll(children);
        }
        this.nodes = nodes;
        visit(ctx.rp());
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
     * @return TODO
     */
    @Override
    public List<Node> visitApDoc(XPathParser.ApDocContext ctx) {
        this.nodes = XPathEvaluator.root(ctx.FileName().getText());
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
     * @return TODO
     */
    @Override
    public List<Node> visitRpTag(XPathParser.RpTagContext ctx) {
        String tag = ctx.Identifier().getText();
        List<Node> nodes = new LinkedList<>();
        for (Node n : this.nodes) {
            List<Node> children = XPathEvaluator.children(n);
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
     * @return TODO
     */
    @Override
    public List<Node> visitRpWildcard(XPathParser.RpWildcardContext ctx) {
        List<Node> nodes = new LinkedList<>();
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
     * @return TODO
     */
    @Override
    public List<Node> visitRpCurrent(XPathParser.RpCurrentContext ctx) {
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
     * @return TODO
     */
    @Override
    public List<Node> visitRpParent(XPathParser.RpParentContext ctx) {
        List<Node> nodes = new LinkedList<>();
        for (Node n : this.nodes) {
            nodes.addAll(XPathEvaluator.parent(n));
        }
        this.nodes = XPathEvaluator.unique(nodes);
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
     * @return TODO
     */
    @Override
    public List<Node> visitRpText(XPathParser.RpTextContext ctx) {
        List<Node> nodes = new LinkedList<>();
        for (Node n : this.nodes) {
            Node text = XPathEvaluator.txt(n);
            if (text != null) {
                nodes.add(text);
            }
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
     * @return TODO
     */
    @Override
    public List<Node> visitRpAttribute(XPathParser.RpAttributeContext ctx) {
        List<Node> nodes = new LinkedList<>();
        for (Node n : this.nodes) {
            nodes.addAll(XPathEvaluator.attrib(n, ctx.Identifier().getText()));
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
     * @return TODO
     */
    @Override
    public List<Node> visitRpParentheses(XPathParser.RpParenthesesContext ctx) {
        return visit(ctx.rp());
    }

    /**
     * Relative path (children)
     * <pre>
     * [rp1/rp2](n)
     *   → unique({ y | x ← [rp1](n), y ← [rp2](x) })
     * </pre>
     *
     * @param ctx Current parse tree context
     * @return TODO
     */
    @Override
    public List<Node> visitRpChildren(XPathParser.RpChildrenContext ctx) {
        // TODO: Review
        List<Node> nodes = new LinkedList<>();
        List<Node> children = visit(ctx.rp(0));
        for (Node c : children) {
            this.nodes = new LinkedList<>();
            this.nodes.add(c);
            nodes.addAll(visit(ctx.rp(1)));
        }
        this.nodes = XPathEvaluator.unique(nodes);
        return this.nodes;
    }

    /**
     * Relative path (all)
     * <pre>
     * [rp1//rp2](n)
     *   → unique([rp1/rp2](n), [rp1/∗//rp2](n))
     * </pre>
     *
     * @param ctx Current parse tree context
     * @return TODO
     */
    @Override
    public List<Node> visitRpAll(XPathParser.RpAllContext ctx) {
        // TODO: fix, review all...
        List<Node> nodes = new LinkedList<>();
        for (Node n : this.nodes) {
            nodes.addAll(XPathEvaluator.children(n));
        }
        this.nodes = XPathEvaluator.unique(nodes);
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
     * @return TODO
     */
    @Override
    public List<Node> visitRpFilter(XPathParser.RpFilterContext ctx) {
        List<Node> nodes = new LinkedList<>();
        List<Node> rp = visit(ctx.rp());
        for (Node n : rp) {
            this.nodes = new LinkedList<>();
            this.nodes.add(n);
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
     * [rp1, rp2](n)
     *   → [rp1](n), [rp2](n)
     * </pre>
     *
     * @param ctx Current parse tree context
     * @return TODO
     */
    @Override
    public List<Node> visitRpPair(XPathParser.RpPairContext ctx) {
        List<Node> original = this.nodes;
        List<Node> nodes = new LinkedList<>();
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
     * @return TODO
     */
    @Override
    public List<Node> visitFRelativePath(XPathParser.FRelativePathContext ctx) {
        List<Node> nodes = this.nodes;
        List<Node> filter = visit(ctx.rp());
        this.nodes = nodes;
        return filter;
    }

    /**
     * Filter (value equality)
     * <pre>
     * [rp1 = rp2](n)
     * [rp1 eq rp2](n)
     *   → ∃ x ∈ [rp1](n) ∃ y ∈ [rp2](n) / x eq y
     * </pre>
     * Note: filter functions should not change the current list of nodes
     *
     * @param ctx Current parse tree context
     * @return TODO
     */
    @Override
    public List<Node> visitFValueEquality(XPathParser.FValueEqualityContext ctx) {
        List<Node> nodes = this.nodes;
        List<Node> l = visit(ctx.rp(0));
        this.nodes = nodes;
        List<Node> r = visit(ctx.rp(1));
        this.nodes = nodes;
        for (Node nl : l) {
            for (Node nr : r) {
                if (nl.isEqualNode(nr)) {
                    return this.nodes;
                }
            }
        }
        return new LinkedList<>();
    }

    /**
     * Filter (identity equality)
     * <pre>
     * [rp1 == rp2](n)
     * [rp1 is rp2](n)
     *   → ∃ x ∈ [rp1](n) ∃ y ∈ [rp2](n) / x is y
     * </pre>
     * Note: filter functions should not change the current list of nodes
     *
     * @param ctx Current parse tree context
     * @return TODO
     */
    @Override
    public List<Node> visitFIdentityEquality(XPathParser.FIdentityEqualityContext ctx) {
        List<Node> nodes = this.nodes;
        List<Node> l = visit(ctx.rp(0));
        this.nodes = nodes;
        List<Node> r = visit(ctx.rp(1));
        this.nodes = nodes;
        for (Node nl : l) {
            for (Node nr : r) {
                if (nl.isSameNode(nr)) {
                    return this.nodes;
                }
            }
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
     * @return TODO
     */
    @Override
    public List<Node> visitFParentheses(XPathParser.FParenthesesContext ctx) {
        return visit(ctx.f());
    }

    /**
     * Filter (and)
     * <pre>
     * [f1 and f2](n)
     *   → [f1](n) ∧ [f2](n)
     * </pre>
     * Note: filter functions should not change the current list of nodes
     *
     * @param ctx Current parse tree context
     * @return TODO
     */
    @Override
    public List<Node> visitFAnd(XPathParser.FAndContext ctx) {
        if ((visit(ctx.f(0)).isEmpty()) || (visit(ctx.f(1)).isEmpty())) {
            return new LinkedList<>();
        }
        return this.nodes;
    }

    /**
     * Filter (or)
     * <pre>
     * [f1 or f2](n)
     *   → [f1](n) ∨ [f2](n)
     * </pre>
     * Note: filter functions should not change the current list of nodes
     *
     * @param ctx Current parse tree context
     * @return TODO
     */
    @Override
    public List<Node> visitFOr(XPathParser.FOrContext ctx) {
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
     * @return TODO
     */
    @Override
    public List<Node> visitFNot(XPathParser.FNotContext ctx) {
        if (visit(ctx.f()).isEmpty()) {
            return this.nodes;
        }
        return new LinkedList<>();
    }
}
