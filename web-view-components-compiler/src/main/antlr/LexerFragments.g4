lexer grammar LexerFragments;

fragment
NL : '\n' | '\r\n' ;

fragment
WS : [ \t] ;

fragment
NLWS : NL | WS ;

fragment
TWO_DASH : '--' ;

fragment
THREE_DASH : '---' ;

fragment
FS : '/' ;

fragment
BS : '\\' ;

fragment
LT : '<' ;

fragment
GT : '>' ;

fragment
LP : '(' ;

fragment
RP : ')' ;

fragment
DQ : '"' ;

fragment
SQ : '\'' ;

fragment
STAR : '*' ;

fragment
LEFT_CURLY : '{' ;

fragment
RIGHT_CURLY : '}' ;

fragment
DOLLAR : '$' ;

fragment
DOT : '.' ;

fragment
MINUS : '-' ;

fragment
QUESTION : '?' ;

fragment
EQ : '=' ;

fragment
PERCENT : '%' ;

fragment
BANG : '!' ;
