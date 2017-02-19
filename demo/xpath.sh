#!/bin/bash

echo "Executing XPath query contained in xpath-input.txt"
echo "---"
cat "xpath-input.txt"
echo "---"
echo "Storing results in xpath-output.txt"
echo "---"
java -jar xpath.jar xpath-input.txt > xpath-output.xml
cat xpath-output.xml
