// XQuery grammar
grammar XQuery;
import XPath;

// XQuery
xq
    : var                                                            # xqVar
    | StringConstant                                                 # xqConstant
    | ap                                                             # xqAbsolutePath
    | '(' xq ')'                                                     # xqParentheses
    | xq ',' xq                                                      # xqPair
    | xq '/' rp                                                      # xqChildren
    | xq '//' rp                                                     # xqAll
    | '<' Identifier '>{' xq '}</' Identifier '>'                    # xqTag
    | forClause letClause? whereClause? returnClause                 # xqFLWR
    | letClause xq                                                   # xqLet
    ;

// For Clause
forClause
    : 'for' var 'in' xq (',' var 'in' xq)*                           # for
    ;

// Let Clause
letClause
    : 'let' var ':=' xq (',' var ':=' xq)*                           # let
    ;

// Where Clause
whereClause
    : 'where' cond                                                   # where
    ;

// Return Clause
returnClause
    : 'return' xq                                                    # return
    ;

// Condition
cond
    : xq ('=' | 'eq') xq                                             # condValueEquality
    | xq ('==' | 'is') xq                                            # condIdentityEquality
    | 'empty(' xq ')'                                                # condEmpty
    | 'some' var 'in' xq (',' var 'in' xq)* 'satisfies' cond         # condSome
    | '(' cond ')'                                                   # condParentheses
    | cond 'and' cond                                                # condAnd
    | cond 'or' cond                                                 # condOr
    | 'not' cond                                                     # condNot
    ;

// Variable Name
var
    : '$' Identifier                                                 # variable
    ;