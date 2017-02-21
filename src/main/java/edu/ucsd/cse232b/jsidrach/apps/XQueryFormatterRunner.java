package edu.ucsd.cse232b.jsidrach.apps;

import edu.ucsd.cse232b.jsidrach.utils.IO;

import java.io.FileInputStream;

/**
 * XQueryFormatterRunner - Command line utility for XQueryFormatter
 * <p>
 * The executable takes one argument only,
 * the name of the file containing the XQuery query
 * </p>
 */
public class XQueryFormatterRunner {
    public static void main(String[] args) {
        // Check number of arguments
        if (args.length != 1) {
            System.out.println("Invalid number of arguments");
            System.out.println("java -jar <jar_file> <xquery_query>");
            return;
        }
        // Print the formatted xquery query
        try {
            FileInputStream input = new FileInputStream(args[0]);
            String formattedQuery = IO.XQueryFormattedQuery(input);
            System.out.print(formattedQuery);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
