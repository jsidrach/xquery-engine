package edu.ucsd.cse232b.jsidrach.apps;

import edu.ucsd.cse232b.jsidrach.utils.IO;
import edu.ucsd.cse232b.jsidrach.utils.XQueryEngine;
import edu.ucsd.cse232b.jsidrach.utils.XQueryOptimizerEngine;

import java.io.FileInputStream;

/**
 * XQueryOptimized - Command line utility for XQueryOptimizerEngine
 * <p>
 * The executable takes one argument only,
 * the name of the file containing the XQuery query
 * </p>
 */
public class XQueryOptimized {
    public static void main(String[] args) {
        // Check number of arguments
        if (args.length != 1) {
            System.out.println("Invalid number of arguments");
            System.out.println("java -jar <jar_file> <xquery_query>");
            return;
        }
        // Print the result of executing the xquery optimized query into stdout,
        // and the intermediate rewritten queries into stderr
        try {
            FileInputStream input = new FileInputStream(args[0]);
            String rewrittenQuery = XQueryOptimizerEngine.Optimize(input, true);
            System.out.print(IO.NodesToString(XQueryEngine.Query(rewrittenQuery), false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
