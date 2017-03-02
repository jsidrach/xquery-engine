package edu.ucsd.cse232b.jsidrach.apps;

import edu.ucsd.cse232b.jsidrach.utils.IO;
import edu.ucsd.cse232b.jsidrach.utils.XQueryEngine;

import java.io.FileInputStream;

/**
 * XQuery - Command line utility for XQueryEngine
 * <p>
 * The executable takes one argument only,
 * the name of the file containing the XQuery query
 * </p>
 */
public class XQuery {
    public static void main(String[] args) {
        // Check number of arguments
        if (args.length != 1) {
            System.out.println("Invalid number of arguments");
            System.out.println("java -jar <jar_file> <xquery_query>");
            return;
        }
        // Print the result of executing the xquery query
        try {
            FileInputStream input = new FileInputStream(args[0]);
            System.out.print(IO.NodesToString(XQueryEngine.Query(input, true), false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
