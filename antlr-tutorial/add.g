// Grammar file for the Adder tutorial.
//
// This is a demonstration of using tokens as the labels on ASTs and
// placing extent information (that is, start *and* end positions) in tokens,
// primarily (in this case) to use the information during error processing.
//
// This file is in the public domain.
//
// Author: Dan Bornstein, danfuzz@milk.com

class AddInterpreter extends TreeParser;
 
options 
{
    exportVocab = Add;
}

tokens
{
    STRINGIFY;
    NUMBERIFY;
}

{
    /**
     * Report an error whose extent is over the given AST.
     *
     * @param msg non-null; the error message
     * @param ast non-null; the AST that is in error
     */
    private void errorWithExtent (String msg, TokenAST ast)
    {
        ExtentToken errExtent = new ExtentToken ();
        errExtent.setFromTokenAST (ast);
        System.err.println (ErrorFormatter.formatError (errExtent, msg));
    }
}
 
 
// start rule for parser
topLevel: 
    #(tl:TOPLEVEL (expr)*)
    {
        // iterate over all the expressions, printing their values, one
        // per line
        TokenAST a = (TokenAST) tl.getFirstChild ();
        while (a != null)
        {
            ValueExtentToken tok = (ValueExtentToken) a.getToken ();
            Object v = tok.getValue ();
            if (v == null)
            {
                System.out.println ("(error)");
            }
            else if (v instanceof String)
            {
                System.out.println ("\"" + v + "\"");
            }
            else
            {
                System.out.println (tok.getValue ());
            }
            a = (TokenAST) a.getNextSibling ();
        }
    }
;

// interpret an expression node 
expr:
    #(sres:STRINGIFY s:expr)
    {
        ValueExtentToken stok = (ValueExtentToken) 
            ((TokenAST) s).getToken ();
        Object val = stok.getValue ();

        if (val == null)
        {
            // error in the subexpressions; propogate the null
            // implicitly
        }
        else if (val instanceof Integer)
        {
            // stringify has been applied to a number; store the stringified
            // value in the value slot of the "$" token
            ValueExtentToken srestok = (ValueExtentToken) 
                ((TokenAST) sres).getToken ();
            srestok.setValue (val.toString ());
        }
        else
        {
            // stringify has been applied to a string; generate an error
            errorWithExtent ("Can only stringify numbers.", (TokenAST) sres);
        }
    }
|
    #(nres:NUMBERIFY n:expr)
    {
        ValueExtentToken ntok = (ValueExtentToken) 
            ((TokenAST) n).getToken ();
        Object val = ntok.getValue ();

        if (val == null)
        {
            // error in the subexpressions; propogate the null
            // implicitly
        }
        else if (val instanceof String)
        {
            // numberify has been applied to a string; store the parsed
            // number in the value slot of the "#" token
            ValueExtentToken nrestok = (ValueExtentToken) 
                ((TokenAST) nres).getToken ();
            nrestok.setValue (new Integer ((String) val));
        }
        else
        {
            // numberify has been applied to a number; generate an error
            errorWithExtent ("Can only numberify strings.", (TokenAST) nres);
        }
    }
|
    #(ares:ADD a1:expr a2:expr)
    { 
        ValueExtentToken a1tok = (ValueExtentToken) 
            ((TokenAST) a1).getToken ();
        ValueExtentToken a2tok = (ValueExtentToken) 
            ((TokenAST) a2).getToken ();
        ValueExtentToken arestok = (ValueExtentToken) 
            ((TokenAST) ares).getToken ();
        Object v1 = a1tok.getValue ();
        Object v2 = a2tok.getValue ();

        if ((v1 == null) || (v2 == null))
        {
            // error in one of the subexpressions; propogate the null
            // implicitly
        }
        else if (v1.getClass () != v2.getClass ())
        {
            // add has been applied to arguments of differing type; generate
            // an error
            errorWithExtent ("Type mismatch for add operator.", 
                             (TokenAST) ares);
        }
        else if (v1 instanceof Integer)
        {
            // add has been applied to two numbers; store the sum in
            // the value slot of the "+" token
            int sum = ((Integer) v1).intValue () + ((Integer) v2).intValue ();
            arestok.setValue (new Integer (sum));
        }
        else
        {
            // add has been applied to two strings; store the concatenation
            // in the value slot of the "+" token
            arestok.setValue ((String) v1 + (String) v2);
        }
    }
|
    nlit:NUMBER_LITERAL
    {
        // interpret the text of a number literal, storing the parsed
        // integer in the value slot of the token
        ValueExtentToken tok = (ValueExtentToken) 
            ((TokenAST) nlit).getToken ();
        tok.setValue (new Integer (tok.getText ()));
    }
|
    slit:STRING_LITERAL
    {
        // copy the text of a string literal into the value slot of the 
        // token
        ValueExtentToken tok = (ValueExtentToken) 
            ((TokenAST) slit).getToken ();
        tok.setValue (tok.getText ());
    }
;



// ----------------------------------------------------------------------------
// the tree-building parser

class AddParser extends Parser;

options
{
    exportVocab = Add;
    k = 2;
    buildAST = true;
}

// start rule for the file parser
topLevel:
    (expr DELIM!)*
    EOF!
    { #topLevel = #([TOPLEVEL, "TOPLEVEL"], topLevel); }
;

// an expression node
expr:
    unaryExpr (ADD^ unaryExpr)*
;

// a unary expression node or simple literal
unaryExpr:
    OPAREN! expr CPAREN!
|
    STRINGIFY^ unaryExpr
|
    NUMBERIFY^ unaryExpr
|
    STRING_LITERAL
|
    NUMBER_LITERAL
;



// ----------------------------------------------------------------------------
// the lexer

class AddLexer extends Lexer;

options 
{
    exportVocab = Add;
    testLiterals = false;
    k = 2;
    charVocabulary = '\3'..'\377';
}

{
    /**
     * Construct a token of the given type, augmenting it with end position
     * and file name information based on the shared input state of the
     * instance.
     *
     * @param t the token type for the result
     * @return non-null; the newly-constructed token 
     */
    protected Token makeToken (int t)
    {
        ExtentToken tok = (ExtentToken) super.makeToken (t);
        ((ExtentLexerSharedInputState) inputState).annotate (tok);
        return tok;
    }
}

ADD:       '+';
STRINGIFY: '$';
NUMBERIFY: '#';
DELIM:     ';';
OPAREN:    '(';
CPAREN:    ')';

// whitespace
WS: 
    ( ' '
    | '\t'
    | ( "\r\n" | '\r' | '\n' ) 
      { newline (); }
    )
    { $setType(Token.SKIP); }
;

// a numeric literal
NUMBER_LITERAL:
    ('-')? (DIGIT)+
;

// decimal digit
protected
DIGIT:
    '0'..'9'
;

// string literals
STRING_LITERAL:
    '\"'!
    (CHAR_ESC | ~('\"'|'\\') )* 
    '\"'!
;

// escape sequence inside a string literal
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
    )
;
