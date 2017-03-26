import antlr.collections.AST;
import java.io.FileInputStream;

/**
 * The main for this example. It accepts the name of a file as a commandline
 * argument, and will interpret the contents of the file.
 *
 * <p>This file is in the public domain.</p>
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public class Adder
{
    /**
     * The main method. See the header comment for more details.
     *
     * @param args the commandline arguments
     */
    static public void main (String[] args)
        throws Exception
    {
        // the name of the file to read
        String fileName = args[0];

        // construct the special shared input state that is needed
        // in order to annotate ExtentTokens properly
        ExtentLexerSharedInputState lsis = 
            new ExtentLexerSharedInputState (fileName);

        // construct the lexer
        AddLexer lex = new AddLexer (lsis);

        // tell the lexer the token class that we want
        lex.setTokenObjectClass ("ValueExtentToken");

        // construct the parser
        AddParser par = new AddParser (lex);

        // tell the parser the AST class that we want
        par.setASTNodeType("TokenAST");

        // construct the interpreter (which is a TreeParser)
        AddInterpreter aint = new AddInterpreter ();

        // parse the file
        par.topLevel ();

        // get the tree that resulted from parsing
        AST ast = par.getAST ();

        // interpret the tree
        aint.topLevel (ast);
    }
}
