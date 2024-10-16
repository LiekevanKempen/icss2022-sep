grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';




//--- PARSER: ---
stylesheet: (stylerule | variableAssignment)* EOF;
stylerule: (tagSelector | idSelector | classSelector) OPEN_BRACE (declaration | ifClause)* CLOSE_BRACE;
idSelector: ID_IDENT;
classSelector: CLASS_IDENT;
tagSelector: LOWER_IDENT;
declaration: property COLON (expression | colorLiteral | boolLiteral)  SEMICOLON;

ifClause: IF BOX_BRACKET_OPEN (id | boolLiteral) BOX_BRACKET_CLOSE OPEN_BRACE (declaration | ifClause)* CLOSE_BRACE elseClause?;
elseClause: ELSE OPEN_BRACE (declaration | ifClause)* CLOSE_BRACE;


expression  : expression MUL expression #multiplyOperation
            | expression MIN expression #subtractOperation
            | expression PLUS expression #addOperation
            | (pixelLiteral | percentageLiteral | scalarLiteral | id) #operationLiterals;

property: 'width' | 'height' | 'color' | 'background-color';
variableAssignment: id ASSIGNMENT_OPERATOR (pixelLiteral | percentageLiteral | scalarLiteral | colorLiteral | boolLiteral) SEMICOLON;

colorLiteral: COLOR;
pixelLiteral: PIXELSIZE;
percentageLiteral: PERCENTAGE;




id: CAPITAL_IDENT;
scalarLiteral: SCALAR ;
boolLiteral: TRUE | FALSE;



