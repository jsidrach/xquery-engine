package edu.ucsd.cse232b.jsidrach.utils;

import edu.ucsd.cse232b.jsidrach.antlr.XQueryLexer;
import edu.ucsd.cse232b.jsidrach.antlr.XQueryParser;
import edu.ucsd.cse232b.jsidrach.xquery.optimizer.XQueryFormatter;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;

/**
 * XQueryFormatterEngine - Utility functions to deal with XQueryFormatter
 */
public class XQueryFormatterEngine {

    /**
     * Formats a XQuery query given an ANTLRInputStream
     *
     * @param ANTLRInput Input query
     * @return Formatted query
     */
    public static String Format(ANTLRInputStream ANTLRInput) throws Exception {
        XQueryLexer xQueryLexer = new XQueryLexer(ANTLRInput);
        CommonTokenStream tokens = new CommonTokenStream(xQueryLexer);
        XQueryParser xQueryParser = new XQueryParser(tokens);
        // Parse using xq (XQuery) as root rule
        ParseTree xQueryTree = xQueryParser.xq();
        // Format query
        XQueryFormatter xQueryFormatter = new XQueryFormatter();
        return xQueryFormatter.visit(xQueryTree);
    }

    /**
     * Formats a XQuery query given a file containing it
     *
     * @param file File containing the XQuery query
     * @return Formatted query
     * @throws Exception Exception if file is not found
     */
    public static String Format(FileInputStream file) throws Exception {
        return Format(new ANTLRInputStream(file));
    }

    /**
     * Formats a XQuery query
     *
     * @param query XQuery query string
     * @return Formatted query
     */
    public static String Format(String query) throws Exception {
        return Format(new ANTLRInputStream(query));
    }
}
