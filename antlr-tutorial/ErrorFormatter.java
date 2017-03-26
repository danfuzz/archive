import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class just has a static method that knows how to display
 * formatted error messages that include extent information. This
 * class doesn't do anything particularly sensible with tabs, and it
 * assumes that lines end with either <code>"\r"</code>, <code>"\n"</code>, 
 * or <code>"\r\n"</code>.
 *
 * <p>This file is in the public domain.</p>
 *
 * @author Dan Bornstein, danfuzz@milk.com 
 */
public final class ErrorFormatter
{
    // ------------------------------------------------------------------------
    // constructors
    
    /**
     * This class is uninstantiable.
     */
    private ErrorFormatter ()
    {
        // this space intentionally left blank
    }

    // ------------------------------------------------------------------------
    // public static methods

    /**
     * Return an error string. This includes an extent string (see
     * {@link #extentString}, an error message, and a snippet from
     * the file indicating the text in question, if possible. The returned
     * message will contain newlines at the end of every line.
     *
     * @param tok non-null; a token containing the extent information
     * @param msg non-null; the error message
     * @return non-null; the formatted message
     */
    static public String formatError (ExtentToken tok, String msg)
    {
        StringBuffer sb = new StringBuffer (200);

        sb.append (tok.extentString ());
        sb.append (": ");
        sb.append (msg);
        sb.append ('\n');
        
        try
        {
            sb.append (extentFromFile (tok));
        }
        catch (IOException ex)
        {
            // ignore it; we just won't be able to show the source
            // of the error
        }

        return sb.toString ();
    }

    // ------------------------------------------------------------------------
    // static private methods

    /**
     * Return a string consisting of the extent from the file,
     * as indicated by the given token.
     *
     * @param tok non-null; the token to use
     * @return the formatted string
     */
    static private String extentFromFile (ExtentToken tok)
        throws IOException
    {
        int line = tok.getLine ();
        int endLine = tok.getEndLine ();
        int col = tok.getColumn ();
        int endCol = tok.getEndColumn ();

        StringBuffer sb = new StringBuffer (200);

        FileReader fr = new FileReader (tok.getFileName ());
        BufferedReader br = new BufferedReader (fr);

        // read and discard up to the line that the error is on
        int atLine = 1;
        while (atLine != line)
        {
            skipLine (br);
            atLine++;
        }
                
        // now append the line(s) in error to the output, along with lines
        // containing dashes marking the extent
        while ((atLine >= line) && (atLine <= endLine))
        {
            if ((atLine > line) && (atLine < endLine))
            {
                // don't bother with the middle part of a huge extent
                if (atLine == (line + 1))
                {
                    sb.append ("...\n");
                }
                skipLine (br);
                atLine++;
                continue;
            }

            String s = br.readLine ();
            if (s == null)
            {
                break;
            }

            sb.append (s);
            sb.append ('\n');
                
            int dashCount = s.length ();

            if (atLine == endLine)
            {
                dashCount = (endCol - 1);
            }

            if (atLine == line)
            {
                for (int i = 1; i < col; i++)
                {
                    sb.append (' ');
                }
                dashCount -= (col - 1);
            }
            
            while (dashCount > 0)
            {
                sb.append ('-');
                dashCount--;
            }
            sb.append ('\n');
            atLine++;
        }                   

        fr.close ();

        return sb.toString ();
    }

    /**
     * Skip a full line from a reader.
     *
     * @param r non-null; the reader to skip a line of
     */
    static private void skipLine (BufferedReader r)
        throws IOException
    {
        for (;;)
        {
            int c = r.read ();
            if ((c == -1) || (c == '\n'))
            {
                break;
            }
            else if (c == '\r')
            {
                r.mark (1);
                if (r.read () != '\n')
                {
                    r.reset ();
                }
            }
        }
    }
}
