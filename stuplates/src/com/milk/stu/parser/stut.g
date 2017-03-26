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
// the lexer

class StutLexer extends Lexer;

options 
{
    testLiterals = false;
    k = 3;
    charVocabulary = '\0'..'\377';
}

// the three possibilities

// literal text
LITERAL_TEXT:
    LITERAL_CHAR
|   LITERAL_ESCAPE_CHAR
|   CHAR_ESCAPE_SEQUENCE
|   FAKIE_ESCAPE
;

// a template replacement block or an ignored comment
TEMPLATE_BLOCK:
    ("${" (~('-') | . ~('-'))) => "${"! TEMPLATE_BODY "}"!
|!
    ("${--") => "${--" (options {greedy=false;}: COMMENT_TEXT)* "--}"
    { $setType(Token.SKIP); }
;



// helpers

protected
NEWLINE:
    ('\n' | '\r' (options {greedy=true;}: '\n')?)
    { newline (); }
;

protected
COMMENT_TEXT!:
    (~('\n'|'\r'))
|
    NEWLINE
;

// literal character
protected
LITERAL_CHAR:
    ~('$' | '\n' | '\r')
|
    NEWLINE
;

// escape character followed by something non-escapey (or just before EOF)
protected
FAKIE_ESCAPE:
    '$'
;

// escaped escape
protected
LITERAL_ESCAPE_CHAR!:
    "$$"
    { $setText("$"); }
;

// character escape
protected
CHAR_ESCAPE_SEQUENCE!:
    "$\\"
    ( NEWLINE (' ' | '\t')*
    | 'b'  { $setText("\b"); }
    | 'f'  { $setText("\f"); }
    | 'n'  { $setText("\n"); }
    | 'r'  { $setText("\r"); }
    | 't'  { $setText("\t"); }
    | 'u' digs:FOUR_HEX_DIGITS
      { String dstr = digs.getText ();
        char[] carr = { (char) Integer.parseInt (dstr, 16) };
        String txt = new String (carr);
        $setText(txt);
      }
    | anythingElse:~('\n'|'\r'|'b'|'f'|'n'|'r'|'t'|'u')
      { $setText(anythingElse); }
    )
;

// four hex digits, used for a Unicode escape
protected
FOUR_HEX_DIGITS:
    HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
;

// hexadecimal digit
protected
HEX_DIGIT:
    ( '0'..'9'
    | 'A'..'F' 
    | 'a'..'f' )
;

// a template block: this is the inside of a template escape
// (that is, it is surrounded by "${" and "}"), which consists of
// arbitrary text, except that quotes and brackets are matched into
// pairs in the expected way
protected
TEMPLATE_BODY:
    (TEMPL_BODY_PART)*
;

protected
TEMPL_BODY_PART:
    '\'' (TEMPL_STRING_CHAR1)* '\''
|
    '\"' (TEMPL_STRING_CHAR2)* '\"'
|
    '`' (TEMPL_STRING_CHAR3)* '`'
|
    '{' TEMPLATE_BODY '}'
|
    '#' (TEMPL_COMMENT_CHAR)* ('\r' | '\n')
|
    ~('\"' | '\'' | '`' | '#' | '{' | '}')
;

protected
TEMPL_STRING_CHAR1:
    TEMPL_STRING_ESCAPE_OR_NL
|
    ~('\\' | '\'' | '\r' | '\n')
;

protected
TEMPL_STRING_CHAR2:
    TEMPL_STRING_ESCAPE_OR_NL
|
    ~('\\' | '\"' | '\r' | '\n')
;

protected
TEMPL_STRING_CHAR3:
    TEMPL_STRING_ESCAPE_OR_NL
|
    ~('\\' | '`' | '\r' | '\n')
;

protected
TEMPL_STRING_ESCAPE_OR_NL:
    '\\' ~('\r' | '\n')
|
    '\\' NEWLINE
|
    NEWLINE
;

protected
TEMPL_COMMENT_CHAR:
    ~('\r' | '\n')
;
