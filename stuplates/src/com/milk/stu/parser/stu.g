// Copyright (c) 2002 Dan Bornstein, danfuzz@milk.com. All rights 
// reserved, except as follows:
// 
// Permission is hereby granted, free of charge, to any person obtaining a
// copy of this software and associated documentation files (the "Software"),
// to deal in the Software without restriction, including without limitation
// the rights to use, copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons to whom the
// Software is furnished to do so, subject to the condition that the above
// copyright notice and this permission notice shall be included in all copies
// or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// DEALINGS IN THE SOFTWARE.

header
{
    package com.milk.stu.parser;
}



// ----------------------------------------------------------------------------
// the parser

class StuParser extends Parser;

options
{
    exportVocab = Stu;
    buildAST = true;
    k = 2;
}

tokens
{
    APPLY_FUNC;      // represents function appliction
    APPLY_METH;      // represents method appliction
    ID_QUASI;        // an identifiered quasiliteral
    AREF;            // represents array-like reference
    MAKE_LIST;       // represents an inline list contruction
    MAKE_MAP;        // represents an inline map contruction
    PARAM_LIST;      // used for parameter list to functions
    QUALIFIED_EXPR;  // an identifier-qualified expression
    BLOCK_EXPR;      // a block expression
}

// start rule for parsing a file; just a list of statements
parseFile:
    statementList
    EOF!
;

// a statement list surrounded by curly braces
blockExpression:
    OCURLY! statementList CCURLY!
;

// an unadorned list of statements
statementList:
    statement (SEMI! statement)*
    { #statementList = #([BLOCK_EXPR, "BLOCK_EXPR"], statementList); }
;

// statement
statement:
    (EOF | CCURLY | SEMI) => // empty
|
    (~(IF | LOOP | OCURLY)) => expression
|
    (IF) => ifExpression statement
|
    (LOOP) => loopExpression statement
|
    (OCURLY) => blockExpression statement
|
    (DEF defIdentifier ASSIGN) => defVariable
|
    (DEF defIdentifier OPAREN) => defFunction statement
|
    (DEF defIdentifier (EOF | CCURLY | SEMI)) => defVariableNull
;

// a variable def
defVariable:
    DEF^ defIdentifier ASSIGN! expression
;

// a null variable def
defVariableNull:
    DEF^ defIdentifier
    { #defVariableNull.addChild (#[NULL, "NULL"]); }
;

// a function def
defFunction:
    DEF^ id:defIdentifier pl:paramList! bexp:blockExpression!
    { AST fn = #([FN, "FN"], astFactory.dupTree (id), pl, bexp);
      #defFunction.addChild (fn);
    }
;



// expressions
//
// precedence table, loosest to tightest binding:
//    := (ASSIGN) =~ (ASSIGN_MATCH)
//    || (BOOL_OR)
//    && (BOOL_AND)
//    == (EQ) != (NE)
//    < (LT) > (GT) <= (LE) >= (GE)
//    & (AND) | (OR) ^ (XOR)
//    << (LSHIFT) >> (RSHIFT)
//    binary- (SUB) + (ADD)
//    * (MUL) / (DIV) % (REMAINDER) %% (MOD)
//    ** (POW)
//    unary- (SUB) unary+ (ADD) ! (NOT) ~ (INVERT)
//    "x(...)" (APPLY_FUNC) "x[...]" (AREF) . (DOT / APPLY_METH) 
//    "(...)" (paren group)

expression:
    assignExpression
;

assignExpression:
    orExpression
    ((ASSIGN^ | ASSIGN_MATCH^) assignExpression)?
;

orExpression:
    andExpression
    (BOOL_OR^ andExpression)*
;

andExpression:
    equalityExpression
    (BOOL_AND^ equalityExpression)*
;

equalityExpression:
    relationalExpression
    ((EQ^ | NE^) relationalExpression)*
;

relationalExpression:
    logicalExpression
    ((LT^ | GT^ | LE^ | GE^) logicalExpression)*
;

logicalExpression:
    shiftExpression
    ((AND^ | OR^ | XOR^) shiftExpression)*
;

shiftExpression:
    additiveExpression
    ((LSHIFT^ | RSHIFT^) additiveExpression)*
;

additiveExpression:
    multiplicativeExpression
    ((ADD^ | SUB^) multiplicativeExpression)*
;

multiplicativeExpression:
    powerExpression
    ((MUL^ | DIV^ | REMAINDER^ | MOD^) powerExpression)*
;

powerExpression:
    unaryExpression
    (POW^ unaryExpression)*
;

unaryExpression:
    (ADD^ | SUB^ | NOT^ | INVERT^) unaryExpression
|
    postfixExpression
;

postfixExpression:
    simpleExpression
    (
        dot:DOT^ IDENTIFIER
        (options {greedy=true;}: 
            OPAREN! argList CPAREN! 
            { #dot.setType(APPLY_METH); }
        )?
    |
        opar:OPAREN^ 
        { #opar.setType(APPLY_FUNC); #opar.setText ("APPLY_FUNC"); }
        argList
        CPAREN!
    |
        osq:OSQUARE^ { #osq.setType(AREF); #osq.setText ("AREF"); }
        argList
        CSQUARE!
    )*
;

// simple expression
simpleExpression:
    IDENTIFIER
|
    TRUE
|
    FALSE
|
    INFINITY
|
    NAN
|
    NULL
|
    LITERAL_INTEGER
|
    LITERAL_FLOAT
|
    LITERAL_STRING
|
    LITERAL_URI
|
    QUASILITERAL_STRING
|!
    id:IDENTIFIER quasi:QUASILITERAL_STRING
    { #simpleExpression = #([ID_QUASI, "ID_QUASI"], id, quasi); }
|
    specialFunctionName
|
    parenExpression
|
    collectionExpression
|
    breakExpression
|
    continueExpression
|
    returnExpression
|
    anonymousFunction
|
    ifExpression
|
    loopExpression
|
    blockExpression
;

// parenthesized expression
parenExpression:
    OPAREN! expression CPAREN!
|
    opar:OPAREN^ COLON! id:IDENTIFIER COLON! expression CPAREN!
    { #opar.setType (QUALIFIED_EXPR);
      #opar.setText ("QUALIFIED_EXPR");
    }
;

// collection construction expression
collectionExpression:
    osq1:OSQUARE^ mapOrList CSQUARE!
    { // determine if it's a map or a list, and complain if it's neither/both
      boolean isMap = false;
      boolean isList = false;
      AST child = #osq1.getFirstChild ();
      while (child != null)
      {
          if (child.getType () == COLON) isMap = true;
          else isList = true;
          child = child.getNextSibling ();
      }
      if (isMap)
      {
          if (isList)
          {
              throw new SemanticException ("malformed list/map");
          }
          #osq1.setType (MAKE_MAP); 
          #osq1.setText ("MAKE_MAP");
      }
      else
      {      
          #osq1.setType (MAKE_LIST); 
          #osq1.setText ("MAKE_LIST");
      }
    }
|
    osq2:OSQUARE^ COLON! CSQUARE!
    { #osq2.setType (MAKE_MAP); 
      #osq2.setText ("MAKE_MAP"); }
|!
    GET
    { #collectionExpression = #([MAKE_LIST, "MAKE_LIST"]); }
;

// return expression
returnExpression:
    RETURN^ (options {greedy=true;}: parenExpression)?
;

// break expression
breakExpression:
    BREAK^ (id:IDENTIFIER)?
    (options {greedy=true;}: OPAREN! ex:parenExpression CPAREN)?
    // note: CPAREN isn't !'ed so that we can tell there's an expression
    // by its existence (differentiates between just-identifier and
    // just-expression forms)
;

// continue expression
continueExpression:
    CONTINUE^ (id:IDENTIFIER)?
;

// possibly-empty argument list (no parens)
argList:
    (expression (COMMA! expression)*)?
;

// possibly-empty list of expressions or key:value pairs (no parens)
mapOrList:
    (mapOrListElement (COMMA! mapOrListElement)*)?
;

// key:value pair or just an expression
mapOrListElement:
    expression (COLON^ expression)?
;

// anonymous function
anonymousFunction:
    FN^ (defIdentifier)? paramList blockExpression
;

paramList:
    opar:OPAREN^
    (IDENTIFIER (COMMA! IDENTIFIER)*)?
    CPAREN!
    { #opar.setType (PARAM_LIST); }
;

// if expression
ifExpression:
    IF^ 
    parenExpression
    blockExpression
    (elseClause)?
;

elseClause:
    ELSE! 
    (options {greedy=true;}: (ifExpression | blockExpression))
;

// loop expression
loopExpression:
    LOOP^ (IDENTIFIER)? blockExpression
;

// special function name
specialFunctionName:
    FNAME^ specialIdentifier
;

// ways to name a variable to be defined
defIdentifier:
    IDENTIFIER
|!
    si:specialIdentifier
    { #defIdentifier = #([FNAME, "FNAME"], si); }
;

specialIdentifier:
    XML_ENTITY 
|
    LITERAL_URI
|
    LITERAL_STRING 
| 
    (COLON! IDENTIFIER COLON!)?
    ( ADD | SUB | MUL | POW | DIV | REMAINDER | MOD | AND | OR | XOR
    | INVERT | LSHIFT | RSHIFT | NOT | EQ | NE | LT | GT | LE | GE
    | GET )
// xxx deal with break and continue names
;



// ----------------------------------------------------------------------------
// the lexer

class StuLexer extends Lexer;

options 
{
    exportVocab = Stu;
    testLiterals = false;
    k = 3;
    charVocabulary = '\0'..'\377';
}

tokens
{
    // reserved words
    BREAK = "break";
    CONTINUE = "continue";
    DEF = "def";
    ELSE = "else";
    FALSE = "false";
    FN = "fn";
    FNAME = "fname";
    IF = "if";
    INFINITY = "Infinity";
    LOOP = "loop";
    NULL = "null";
    NAN = "NaN";
    RETURN = "return";
    TRUE = "true";
}



// operators

ASSIGN:    ":=";
ASSIGN_MATCH: "=~";
ADD:       '+';
SUB:       '-';
MUL:       '*';
POW:       "**";
DIV:       '/';
REMAINDER: '%';
MOD:       "%%";
AND:       '&';
OR:        '|';
XOR:       '^';
INVERT:    '~';
// LSHIFT << defined as part of LITERAL_URI
RSHIFT:    ">>";
NOT:       '!';
EQ:        "==";
NE:        "!=";
// LT < defined as part of LITERAL_URI
GT:        '>';
LE:        "<=";
GE:        ">=";

BOOL_AND:  "&&";
BOOL_OR:   "||";



// syntactic particles

OPAREN:   '(';
CPAREN:   ')';
OCURLY:   '{';
CCURLY:   '}';
OSQUARE:  '[';
CSQUARE:  ']';
GET:      "[]";
COMMA:    ',';
SEMI:     ';';
COLON:    ':';



// lexical noise

// whitespace
WS!: 
    ( ' ' | '\t' )
    { $setType(Token.SKIP); }
;

// newline, optionally preceded by a comment
NEWLINE!:
    ('#' (~('\n'|'\r'))*)?
    ( "\r\n" | '\r' | '\n' ) 
    { newline ();
      $setType(Token.SKIP); }
;



// significant nontrivial tokens

// identifiers
IDENTIFIER
    options { testLiterals = true; }
:
    (ALPHA | '_' | '$' )
    (ALPHA | '_' | '$' | DIGIT)*
;

// an xml entity; the result text does not include the '&' and ';' around it
XML_ENTITY:
 '&'! XML_NAME ';'!
;

// a valid xml name; see the xml spec
protected
XML_NAME:
    (ALPHA | '_' | ':')
    (ALPHA | '_' | ':' | '.' | '-' | DIGIT)*
;

// uri-like thing (see rfc 2396, though this rule is very lenient in its
// interpretation), and an out for < and <<
LITERAL_URI:
    ('<' ALPHA (URI_CHAR)* '>') => '<'! ALPHA (URI_CHAR)* '>'!
|
    ("<<") => "<<"
    { $setType(LSHIFT); }
|
    '<' 
    { $setType(LT); }
;

protected
URI_CHAR:
    ~('\u0000' .. ' ' | '<' | '>')
;

protected
ALPHA:
    'a'..'z'
|   'A'..'Z'
;

// numeric literal and, as a special case, the dot ('.') token
LITERAL_NUMBER_OR_DOT:
    '.'
    { $setType(DOT); }
|
    HEX_INT
    { $setType(LITERAL_INTEGER); }
|
    FLOAT_STARTING_WITH_DOT
    { $setType(LITERAL_FLOAT); }
|
    orig:NUMBER_STARTING_WITH_DIGIT
    { String txt = orig.getText ();
      if ((txt.indexOf ('.') == -1) && 
          (txt.indexOf ('e') == -1) && 
          (txt.indexOf ('E') == -1))
      {
        $setType(LITERAL_INTEGER);
      }
      else
      {
        $setType(LITERAL_FLOAT);
      }
    }
;
 
// floating point literal that starts with a dot
protected
FLOAT_STARTING_WITH_DOT:
    '.' 
    (DIGIT)+ 
    (EXPONENT)?
;

// number (integer or floating point) that starts with a digit
protected
NUMBER_STARTING_WITH_DIGIT:
    (DIGIT)+ 
    ('.' (DIGIT)+)?
    (EXPONENT)?
;

// hexadecimal integer literal
protected
HEX_INT:
    '0' 
    ('x' | 'X') 
    (HEX_DIGIT)+
;

// decimal digit
protected
DIGIT:
    '0'..'9'
;

// hexadecimal digit
protected
HEX_DIGIT:
    DIGIT
| 'A'..'F' 
| 'a'..'f'
;

// exponent spec for floating point
protected
EXPONENT:
    ('e' | 'E') 
    ('+' | '-')? 
    (DIGIT)+
;

// string literals
LITERAL_STRING:
    '\"'! (STRING_NL | CHAR_ESC | ~('\"' | '\\' | '\r' | '\n') )* '\"'!
|
    '\''! (STRING_NL | CHAR_ESC | ~('\'' | '\\' | '\r' | '\n') )* '\''!
;

// quasiliteral string
QUASILITERAL_STRING:
    '`'! (STRING_NL | CHAR_ESC | ~('`' | '\\' | '\r' | '\n' ) )* '`'!
;

// escape sequence inside a string literal (or quasiliteral); this is
// similar to Java, except the only accepted numeric form is the Unicode
// escape
protected
CHAR_ESC!:
    '\\'
    ( 'n'  { $setText("\n"); }
    | 'r'  { $setText("\r"); }
    | 't'  { $setText("\t"); }
    | 'b'  { $setText("\b"); }
    | 'f'  { $setText("\f"); }
    | 'u' digs:FOUR_HEX_DIGITS
      { String dstr = digs.getText ();
        char[] carr = { (char) Integer.parseInt (dstr, 16) };
        String txt = new String (carr);
        $setText(txt);
      }
    | ('\r' ~('\n')) => '\r' (options {greedy=true;}: (' ' | '\t'))*
      { newline(); }
    | ('\r')? '\n' (options {greedy=true;}: (' ' | '\t'))*
      { newline(); }
    | ch:~('n' | 'r' | 't' | 'b' | 'f' | 'u' | '\r' | '\n')
      { $setText(ch); }
    )
;

// newline directly inside a string literal (or quasiliteral)
protected
STRING_NL:
    ('\r' ~('\n')) => '\r' (options {greedy=true;}: (' '! | '\t'!))*
    { newline(); }
|
    ('\r')? '\n' (options {greedy=true;}: (' '! | '\t'!))*
    { newline(); }
;

// four hex digits, used for a Unicode escape
protected
FOUR_HEX_DIGITS:
    HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
;
