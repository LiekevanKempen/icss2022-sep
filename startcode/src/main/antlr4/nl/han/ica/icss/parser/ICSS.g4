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
stylerule: (tagSelector | idSelector | classSelector) OPEN_BRACE declaration* CLOSE_BRACE;
idSelector: ID_IDENT;
classSelector: CLASS_IDENT;
tagSelector: LOWER_IDENT;
declaration: property COLON (colorLiteral | pixelLiteral | percentageLiteral | id | expression) SEMICOLON;
property: 'width' | 'height' | 'color' | 'background-color';


colorLiteral: COLOR;
pixelLiteral: PIXELSIZE;
percentageLiteral: PERCENTAGE;
variableAssignment: id ASSIGNMENT_OPERATOR (colorLiteral | pixelLiteral | percentageLiteral | scalarLiteral | boolLiteral) SEMICOLON;
expression: expression ( multiply | plus | min ) expression* | (pixelLiteral | percentageLiteral | scalarLiteral) | id;

plus: PLUS;
min: MIN;
multiply: MUL;
id: CAPITAL_IDENT;
scalarLiteral: SCALAR ;
boolLiteral: TRUE | FALSE;



