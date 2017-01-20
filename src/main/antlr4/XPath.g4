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
    : ('doc(' | 'document(') FileName ')'
    ;

// Relative Path
rp
    : tag                                        # rpTag
    | '*'                                        # rpWildcard
    | '.'                                        # rpCurrent
    | '..'                                       # rpParent
    | 'text()'                                   # rpText
    | '@' att                                    # rpAttribute
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

// Attribute
att
    : Identifier
    ;

// Tag
tag
    : Identifier
    ;

// File Name
FileName: '"' + ((Letter | Digit | [_,. /-])+) + '"';

// Identifier
Identifier: Letter (Letter | Digit)*;

// Basic Fragments
fragment Letter: [a-zA-Z];

fragment Digit: [0-9];

// Ignore White Space
WhiteSpace: [ \n\t\r] -> skip;
