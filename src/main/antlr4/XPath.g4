// XPath grammar
grammar XPath;

@header {
package edu.ucsd.cse232b.jsidrach.xpath.parser;
}

// Absolute path
ap
    : doc '/' rp                                 # apChildren
    | doc '//' rp                                # apAll
    ;

// Document
doc
    : 'doc(' FILENAME ')'                      # apDoc
    ;

// Relative Path
rp
    : IDENTIFIER                                 # rpTag
    | '*'                                        # rpWildcard
    | '.'                                        # rpCurrent
    | '..'                                       # rpParent
    | 'text()'                                   # rpText
    | '@' IDENTIFIER                             # rpAttribute
    | '(' rp ')'                                 # rpParentheses
    | rp '/' rp                                  # rpChildren
    | rp '//' rp                                 # rpAll
    | rp '[' f ']'                               # rpFilter
    | rp ',' rp                                  # rpPair
    ;

// Path Filter
f
    : rp                                         # fRelativePath
    | rp ('=' | 'eq') rp                         # fValueEquality
    | rp ('==' | 'is') rp                        # fIdentityEquality
    | '(' f ')'                                  # fParentheses
    | f 'and' f                                  # fAnd
    | f 'or' f                                   # fOr
    | 'not' f                                    # fNot
    ;

// File Name
FILENAME: '"' + ([a-zA-Z0-9_,. /-]+) + '"';

// Identifier
IDENTIFIER: LETTER (LETTER | DIGIT)*;

// Basic Fragments
fragment LETTER: [a-zA-Z];

fragment DIGIT: [0-9];

// Ignore White Space
WHITESPACE: [ \n\t\r]+ -> skip;
