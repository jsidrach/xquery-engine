// XQuery grammar
grammar XQuery;
import XPath;

// XQuery
xq
    : Variable                                                                 # xqVariable
    | StringConstant                                                           # xqConstant
    | ap                                                                       # xqAbsolutePath
    | '(' xq ')'                                                               # xqParentheses
    | xq '/' rp                                                                # xqChildren
    | xq '//' rp                                                               # xqAll
    | xq ',' xq                                                                # xqPair
    | '<' Identifier '>' '{' xq '}' '</' Identifier '>'                        # xqTag
    | 'join' '(' xq ',' xq ',' tagList ',' tagList ')'                         # xqJoin
    | letClause xq                                                             # xqLet
    | forClause letClause? whereClause? returnClause                           # xqFLWR
    ;

// For Clause
forClause
    : 'for' Variable 'in' xq (',' Variable 'in' xq)*
    ;

// Let Clause
letClause
    : 'let' Variable ':=' xq (',' Variable ':=' xq)*
    ;

// Where Clause
whereClause
    : 'where' cond
    ;

// Return Clause
returnClause
    : 'return' xq
    ;

// Tag List
tagList
    : '[' (Identifier (',' Identifier)*)? ']'
    ;

// Condition
cond
    : xq ('=' | 'eq') xq                                                       # condValueEquality
    | xq ('==' | 'is') xq                                                      # condIdentityEquality
    | 'empty' '(' xq ')'                                                       # condEmpty
    | 'some' Variable 'in' xq (',' Variable 'in' xq)* 'satisfies' cond         # condSome
    | '(' cond ')'                                                             # condParentheses
    | cond 'and' cond                                                          # condAnd
    | cond 'or' cond                                                           # condOr
    | 'not' cond                                                               # condNot
    ;

// Variable Name
Variable: '$' Identifier;