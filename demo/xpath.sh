#!/bin/bash

echo "Executing XPath query contained in xpath-input.txt: "
cat "xpath-input.txt"
echo "Storing results in xpath-output.txt..."
java -jar xpath.jar xpath-input.txt > xpath-output.xml
