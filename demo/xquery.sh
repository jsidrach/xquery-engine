#!/bin/bash

echo "Executing XQuery query contained in xquery-input.txt"
echo "---"
cat "xquery-input.txt"
echo "---"
echo "Storing results in xquery-output.xml"
echo "---"
java -jar xquery.jar xquery-input.txt > xquery-output.xml
cat xquery-output.xml
