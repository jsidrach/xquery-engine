#!/bin/bash

echo "Executing XQuery query contained in xquery-input.txt: "
cat "xquery-input.txt"
echo "Storing results in xquery-output.txt:"
java -jar xquery.jar xquery-input.txt > xquery-output.xml
cat xquery-output.xml
