// 
// Grammar for Plastic.
//
// Note, pieces of this were inspired by and/or cribbed from the public
// domain grammar for Java written by John Mitchell, Terence Parr, John
// Lilley, Scott Stanchfield, Markus Mohnen, and Peter Williams. As of this
// writing, it may be found here:
//     <http://www.antlr.org/grammars/java/java.g>
//
// Copyright (C) 2000, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!
//
// Author: Dan Bornstein, danfuzz@milk.com

header
{
    package com.milk.plastic.parser;
}



// ----------------------------------------------------------------------------
// the tree-building parser

class PlasticParser extends Parser;

options
{
    exportVocab = Plastic;
    k = 2;
    buildAST = true;
}

tokens
{
    APPLY;    // used for apply-like forms 
    CLOSURE;  // used for lists of closure statements
    REF;      // used for field reference
    TOPLEVEL; // used for lists of bindings at topLevel
    LITERAL;  // eventually becomes the type for all literal tokens

    LITERAL_INTEGER<AST=LiteralAST>;
    LITERAL_FLOAT<AST=LiteralAST>;
    LITERAL_STRING<AST=LiteralAST>;
    TRUE<AST=LiteralAST>;
    FALSE<AST=LiteralAST>;
}


// start rule for the file parser; a file consists of zero or more bindings
topLevel:
    (variableBinding)*
    EOF!
    { #topLevel = #([TOPLEVEL, "TOPLEVEL"], topLevel); }
;



// expressions
//
// precedence table, loosest to tightest binding:
//    & (AND) ^ (XOR) | (OR)
//    == (EQ) != (NE)
//    < (LT) > (GT) <= (LE) >= (GE)
//    binary- (MINUS) binary+ (PLUS)
//    * (TIMES) / (DIVIDE) % (REMAINDER)
//    ** (POW)
//    unary- (MINUS) unary+ (PLUS) ! (NOT)
//    . (DOT) '(...)' (APPLY)

expression:
    logicExpression
;

logicExpression:
    equalityExpression
    ((AND^ | OR^ | XOR^) equalityExpression)*
;

equalityExpression:
    inequalityExpression
    ((EQ^ | NE^) inequalityExpression)*
;

inequalityExpression:
    additiveExpression
    ((LT^ | GT^ | LE^ | GE^) additiveExpression)*
;

additiveExpression:
    multiplicativeExpression
    ((PLUS^ | MINUS^) multiplicativeExpression)*
;

multiplicativeExpression:
    powerExpression
    ((TIMES^ | DIVIDE^ | REMAINDER^) powerExpression)*
;

powerExpression:
    unaryExpression
    (POW^ unaryExpression)*
;

unaryExpression:
    MINUS^ mex:unaryExpression
|
    PLUS^ unaryExpression
|
    NOT^ unaryExpression
|
    postfixExpression
;

postfixExpression:
    ex:simpleExpression
    (!
        // field reference
        DOT id:IDENTIFIER
        { #postfixExpression = #([REF, "REF"], ex, id); }
    |!
        // apply form
        OPAREN! fb:functorBindings CPAREN!
        { #postfixExpression = #([APPLY, "APPLY"], ex, fb); }
    )*
;

// simple expression
simpleExpression:
    OPAREN! expression CPAREN!
|
    LITERAL_INTEGER
|
    LITERAL_FLOAT
|
    LITERAL_STRING
|
    TRUE
|
    FALSE
|
    IOTA
|
    IDENTIFIER
|
    closure
;




functorBindings:
    functorBinding (COMMA! functorBinding)*
;

functorBinding!:
    (id:IDENTIFIER COLON!)? ex:expression
    { if (id == null)
      {
          #functorBinding = #([BIND, "BIND"], [IOTA, "#"], ex);
      }
      else
      {
          #functorBinding = #([BIND, "BIND"], id, ex); 
      }
    }
;



// closure: set of hidden bindings, exporting a single value
closure:
    OCURLY! (closureStatement)* CCURLY!
    { #closure = #([CLOSURE, "CLOSURE"], closure); }
;

closureStatement:
    yieldStatement
|
    inStatement
|
    outStatement
|
    variableBinding
;

yieldStatement:
    YIELD^ expression SEMI!
    { #YIELD.setText ("YIELD"); }
;

inStatement:
    INPUT^ IDENTIFIER (BIND! expression)? SEMI!
;

outStatement:
    OUTPUT^ IDENTIFIER BIND! expression SEMI!
;



// variable binding (note, the toplevel is a sequence of these)
variableBinding:
    IDENTIFIER b:BIND^ expression SEMI!
    { #b.setText ("BIND"); }
;




    


// ----------------------------------------------------------------------------
// the lexer

class PlasticLexer extends Lexer;

options 
{
    exportVocab = Plastic;
    testLiterals = false;
    k = 2;
    charVocabulary = '\3'..'\377';
}

tokens
{
    // reserved words
    INPUT = "input";
    OUTPUT = "output";
    TRUE = "true";
    FALSE = "false";
}

// functor operators

PLUS:      '+';
MINUS:     '-';
TIMES:     '*';
POW:       "**";
DIVIDE:    '/';
REMAINDER: '%';
AND:       '&';
OR:        '|';
XOR:       '^';
NOT:       '!';
EQ:        "==";
NE:        "!=";
LT:        '<';
GT:        '>';
LE:        "<=";
GE:        ">=";



// syntactic particles

OPAREN: '(';
CPAREN: ')';
OCURLY: '{';
CCURLY: '}';
COMMA:  ',';
SEMI:   ';';
BIND:   ":=";
COLON:  ':';
YIELD:  "::";
IOTA:   '#'; // called iota from the standard linguistic usage

// note: '.' is defined as DOT within the rule LITERAL_NUMBER_OR_DOT



// lexical noise

// whitespace
WS: 
    ( ' '
    | '\t'
    | ( "\r\n" | '\r' | '\n' ) 
      { newline (); }
    )
    { $setType(Token.SKIP); }
;

// comments
COMMENT: 
    "//"
    (~('\n'|'\r'))* 
    ('\n'|'\r'('\n')?)
    { newline (); 
      $setType(Token.SKIP); }
;



// significant nontrivial tokens

// identifiers
IDENTIFIER
    options { testLiterals = true; }
:
    orig:IDENTIFIER_RAW
    { if (orig.getType () == IDENTIFIER_RAW)
      {
          // replace the token with an Identifier, but only if it hasn't
          // already been replaced with a reserved word token
          Identifier id = new Identifier (orig);
          $setToken(id);
      }
    }
;

// raw identifier
protected
IDENTIFIER_RAW
    options { testLiterals = true; }
:
    ('a'..'z' | 'A'..'Z' | '_')
    ('a'..'z' | 'A'..'Z' | '_' | '0'..'9')*
;

// numeric literal and, as a special case, the dot ('.') token
LITERAL_NUMBER_OR_DOT:
    '.'
    { $setType(DOT); }
|
    orig1:HEX_INT
    { LiteralInteger li = new LiteralInteger (orig1);
      $setToken(li);
    }
|
    orig2:FLOAT_STARTING_WITH_DOT
    { LiteralFloat lf = new LiteralFloat (orig2);
      $setToken(lf);
    }
|
    orig3:NUMBER_STARTING_WITH_DIGIT
    { String txt = orig3.getText ();
      Token t;
      if ((txt.indexOf ('.') == -1) && 
          (txt.indexOf ('e') == -1) && 
          (txt.indexOf ('E') == -1))
      {
          t = new LiteralInteger (orig3);
      }
      else
      {
          t = new LiteralFloat (orig3);
      }
      $setToken(t);
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
    ( DIGIT
    | 'A'..'F' 
    | 'a'..'f' )
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
    orig:LITERAL_STRING_RAW
    { LiteralString ls = new LiteralString (orig);
      $setToken(ls);
    }
;

// string literals
protected
LITERAL_STRING_RAW:
    '\"'!
    (CHAR_ESC | ~('\"'|'\\') )* 
    '\"'!
;

// escape sequence inside a string literal; this is similar to Java, except
// the only accepted numeric form is the Unicode escape
protected
CHAR_ESC:
    '\\'
    ( 'n'  { $setText("\n"); }
    | 'r'  { $setText("\r"); }
    | 't'  { $setText("\t"); }
    | 'b'  { $setText("\b"); }
    | 'f'  { $setText("\f"); }
    | '\"' { $setText("\""); }
    | '\'' { $setText("\'"); }
    | '\\' { $setText("\\"); }
    | 'u' digs:FOUR_HEX_DIGITS
      { String dstr = digs.getText ();
        char[] carr = { (char) Integer.parseInt (dstr, 16) };
        String txt = new String (carr);
        $setText(txt);
      }
    )
;

// four hex digits, used for a Unicode escape
protected
FOUR_HEX_DIGITS:
    HEX_DIGIT
    HEX_DIGIT
    HEX_DIGIT
    HEX_DIGIT
;
