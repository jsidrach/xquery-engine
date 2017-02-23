package edu.ucsd.cse232b.jsidrach.apps;

import edu.ucsd.cse232b.jsidrach.utils.XQueryFormatterEngine;

import java.io.FileInputStream;

/**
 * FormatterXQuery - Command line utility for FormatterXQuery
 * <p>
 * The executable takes one argument only,
 * the name of the file containing the XQuery query
 * </p>
 */
public class FormatterXQuery {
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
            System.out.print(XQueryFormatterEngine.Format(input));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
