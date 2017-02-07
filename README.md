# CSE 232B Project - Team \#3

## Build

Requires gradle:
```bash
gradle build
```

This will run all tests, compile and place the fat jars into the folder ```demo/```, and generate the javadoc documentation

## Milestone \#1

* Run XPath:
  ```bash
  cd demo/
  ```
  Save the XPath query in ```xpath-input.txt```

  To run the XPath query, execute
  ```bash
  ./xpath.sh
  ```
  The resulting list of nodes will be stored in ```xpath-output.xml``` and displayed in the terminal

## Milestone \#2

* Run XQuery:
  ```bash
  cd demo/
  ```
  Save the XQuery query in ```xquery-input.txt```

  To run the XQuery query, execute
  ```bash
  ./xquery.sh
  ```
  The resulting document will be stored in ```xquery-output.xml``` and displayed in the terminal
