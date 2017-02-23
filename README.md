# CSE 232B Project - Team \#3

## Build

Requires gradle:
```bash
gradle build
```

This will run all tests, compile and place the fat jars into sub-folders of ```demo/```, and generate the javadoc documentation

## Milestone \#1

* Run XPath:
  ```bash
  cd demo/xpath/
  ```
  Save the XPath query in ```input.txt```

  To run the XPath query, execute
  ```bash
  ./xpath.sh
  ```
  The resulting list of nodes will be stored in ```output.xml``` and displayed in the terminal

## Milestone \#2

* Run XQuery:
  ```bash
  cd demo/xquery/
  ```
  Save the XQuery query in ```input.txt```

  To run the XQuery query, execute
  ```bash
  ./xquery.sh
  ```
  The resulting document will be stored in ```output.xml``` and displayed in the terminal

## Milestone \#3

* Run optimized XQuery:
  ```bash
  cd demo/optimized-xquery/
  ```
  Save the XQuery query in ```input.txt```

  To run the XQuery query, execute
  ```bash
  ./optimized-xquery.sh
  ```
  The intermediate rewritten queries will be stored in ```rewritten.txt```
  
  The resulting document will be stored in ```output.xml``` and displayed in the terminal

## Extra

* Run XQuery formatter:
  ```bash
  cd demo/formatter/
  ```
  Save the XQuery query in ```input.txt```

  To format the XQuery query, execute
  ```bash
  ./formatter.sh
  ```
  The formatted query will be stored in ```output.txt``` and displayed in the terminal