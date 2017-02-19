package edu.ucsd.cse232b.jsidrach.xquery;

import edu.ucsd.cse232b.jsidrach.utils.IO;

import java.io.FileInputStream;

/**
 * XQueryOptimizedRunner - Command line utility for XQuery
 * <p>
 * The executable takes one argument only,
 * the name of the file containing the XQuery query
 * </p>
 */
public class XQueryOptimizedRunner {
    public static void main(String[] args) {
        // Check number of arguments
        if (args.length != 1) {
            System.out.println("Invalid number of arguments");
            System.out.println("java -jar <jar_file> <xquery_query>");
            return;
        }

        // Print the result of executing the xquery optimized query into stdout,
        // and the intermediate rewritten query into stderr
        try {
            FileInputStream input = new FileInputStream(args[0]);
            String rewrittenQuery = IO.XQueryOptimizedQuery(input);
            System.err.print(rewrittenQuery);
            System.out.print(IO.NodesToString(IO.XQueryQuery(rewrittenQuery), false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
