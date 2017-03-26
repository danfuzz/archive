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

package com.milk.stu.xml;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

/**
 * The Stupid XML parser. It knows how to read from a reader and return
 * a fragment of all the content it finds.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public final class XmlParser
{
    /** constant for {@link #myPeek} indicating that the EOF has been
     * reached */
    private static final int PEEK_EOF = -1;

    /** constant for {@link #myPeek} indicating that there is no current
     * peek character */
    private static final int PEEK_NONE = -2;

    /** non-null; the reader to read from */
    private final Reader myReader;

    /** currently peeked character or <code>PEEK_EOF</code> for EOF or
     * <code>PEEK_NONE</code> for no-currently-peeked-character */
    private int myPeek;

    /** line number of the next character to be read */
    private int myPeekLine;

    /** column number of the next character to be read */
    private int myPeekColumn;

    /** line number of the most recently read character */
    private int myReadLine;

    /** column number of the most recently read character */
    private int myReadColumn;



    // ------------------------------------------------------------------------
    // static public methods

    /**
     * Parse from the given reader.
     *
     * @param reader non-null; the reader to read from
     * @return non-null; the parsed fragment
     */
    static public Fragment parse (Reader reader)
    {
	XmlParser p = new XmlParser (reader);
	return p.fragment (null);
    }



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance. This is private; use the static methods on
     * this class.
     *
     * @param reader non-null; the reader to read from
     */
    public XmlParser (Reader reader)
    {
	if (reader == null)
	{
	    throw new NullPointerException ("reader == null");
	}

	myReader = reader;
	myPeek = PEEK_NONE;
	myPeekLine = 1;
	myPeekColumn = 1;
	myReadLine = -1;
	myReadColumn = -1;
    }



    // ------------------------------------------------------------------------
    // private instance methods

    /**
     * Peek at the next character.
     *
     * @return the peeked character or <code>PEEK_EOF</code>
     */
    private int peek ()
    {
	if (myPeek == PEEK_NONE)
	{
	    try
	    {
		myPeek = myReader.read ();
	    }
	    catch (IOException ex)
	    {
		throw new XmlParseException (ex, myPeekLine, myPeekColumn,
					     myPeekLine, myPeekColumn);
	    }
	}

	return myPeek;
    }

    /**
     * Read the next character, updating line and column information as well.
     *
     * @return the character or <code>PEEK_EOF</code>
     */
    private int read ()
    {
	int c = peek ();

	if (c != PEEK_EOF)
	{
	    myReadLine = myPeekLine;
	    myReadColumn = myPeekColumn;

	    myPeek = PEEK_NONE;
	    if (c == '\r')
	    {
		int c2 = peek ();
		if (c2 != '\n')
		{
		    myPeekLine++;
		    myPeekColumn = 1;
		}
	    }
	    else if (c == '\n')
	    {
		myPeekLine++;
		myPeekColumn = 1;
	    }
	    else if (c >= ' ')
	    {
		myPeekColumn++;
	    }
	}

	return c;
    }

    /**
     * Read past any whitespace.
     */
    private void skipWhitespace ()
    {
	for (;;)
	{
	    int c = peek ();
	    if ((c == PEEK_EOF) ||
		! Character.isWhitespace ((char) c))
	    {
		break;
	    }
	    read ();
	}
    }

    /**
     * Read a string consisting of all the non-whitespace characters until
     * the next whitespace, angle bracket (<code>'&lt'</code> or
     * <code>'&gt;'</code>), slash (<code>'/'</code>), or EOF.
     *
     * @return non-null; the so-read string
     */
    private String readUntilWhitespaceOrAngleOrSlash ()
    {
	StringBuffer sb = new StringBuffer ();

	for (;;)
	{
	    int c = peek ();
	    if ((c == PEEK_EOF) ||
		(c == '<') ||
		(c == '>') ||
		(c == '/') ||
		Character.isWhitespace ((char) c))
	    {
		break;
	    }
	    sb.append ((char) c);
	    read ();
	}

	return sb.toString ();
    }

    /**
     * Read a string consisting of all the characters until a double quote,
     * angle bracket (<code>'&lt'</code> or <code>'&gt;'</code>) or EOF.
     *
     * @return non-null; the so-read string 
     */
    private String readUntilQuoteOrAngle ()
    {
	StringBuffer sb = new StringBuffer ();

	for (;;)
	{
	    int c = peek ();
	    if ((c == PEEK_EOF) ||
		(c == '<') ||
		(c == '>') ||
		(c == '\"'))
	    {
		break;
	    }
	    sb.append ((char) c);
	    read ();
	}

	return sb.toString ();
    }

    /**
     * Parse a fragment, possibly ending with the named end tag.
     *
     * @param endName null-ok; an end tag to expect, or <code>null</code>
     * to just end at the EOF
     * @return non-null; the parsed fragment
     */
    private Fragment fragment (String endName)
    {
	ArrayList elems = new ArrayList ();

	for (;;)
	{
	    int c = peek ();
	    if (c == PEEK_EOF)
	    {
		if (endName != null)
		{
		    throw new XmlParseException ("unexpected eof; " +
						 "expected </" + endName + ">",
						 myPeekLine, myPeekColumn,
						 myPeekLine, myPeekColumn);
		}
		break;
	    }
	    else if (c == '<')
	    {
		XmlNode one = tagOrComment (endName);
		if (one == null)
		{
		    break;
		}
		elems.add (one);
	    }
	    else if (c == '>')
	    {
		read ();
		throw new XmlParseException ("unexpected character '>'", 
					     myReadLine, myReadColumn,
					     myPeekLine, myPeekColumn);
	    }
	    else if (c == '&')
	    {
		elems.add (entity ());
	    }
	    else
	    {
		elems.add (text (false));
	    }
	}

	return new Fragment (elems);
    }

    /**
     * Parse and return a run of text. The run ends with the start of a tag
     * or entity or with the EOF, or with <code>'&gt;'</code> (which is
     * illegal and will be flagged as an error at a different point in the
     * code). The <code>inAttribute</code> flag specifies whether this is
     * attribute text; if so, the run will end before a double quote
     * character (<code>"</code>).
     *
     * @return non-null; the parsed text node 
     */
    private TextNode text (boolean inAttribute)
    {
	StringBuffer sb = new StringBuffer ();
	int startLine = myPeekLine;
	int startColumn = myPeekColumn;

	for (;;)
	{
	    int c = peek ();
	    if ((c == PEEK_EOF) || (c == '<') || (c == '>') || (c == '&') ||
		((c == '\"') && inAttribute))
	    {
		break;
	    }
	    sb.append ((char) c);
	    read ();
	}

	return new TextNode (startLine, startColumn, myPeekLine, myPeekColumn,
			     sb.toString ());
    }

    /**
     * Parse an entity. The entity starts with <code>'&'</code> and ends with
     * <code>';'</code>, and no angle brackets (<code>'&lt;'</code> or
     * <code>'&gt;'</code>) may appear between them.
     *
     * @return non-null; the parsed entity node
     */
    private EntityNode entity ()
    {
	StringBuffer sb = new StringBuffer ();
	int startLine = myPeekLine;
	int startColumn = myPeekColumn;

	if (read () != '&')
	{
	    // check in caller should prevent this
	    throw new RuntimeException ("shouldn't happen");
	}

	for (;;)
	{
	    int c = read ();

	    if (c == PEEK_EOF)
	    {
		throw new XmlParseException ("unexpected eof", 
					     startLine, startColumn,
					     myPeekLine, myPeekColumn);
	    }

	    if ((c == '<') || (c == '>'))
	    {
		throw new XmlParseException ("unexpected character '" + 
					     (char) c + "'", 
					     startLine, startColumn,
					     myPeekLine, myPeekColumn);
	    }

	    if (c == ';')
	    {
		break;
	    }

	    sb.append ((char) c);
	}

	return new EntityNode (startLine, startColumn, 
			       myPeekLine, myPeekColumn,
			       sb.toString ());
    }

    /**
     * Parse one of the following: a tag, including the attributes and
     * children and end tag, if any; the end tag with the given name (if
     * passed as <code>non-null</code>; or a comment.
     * 
     * @param endName null-ok; the name of an end tag to match, or
     * <code>null</code> to not accept any possible end tag
     * @return null-ok; the parsed tag or comment node, or <code>null</code>
     * to indicate successful parsing of the named end tag
     */
    public XmlNode tagOrComment (String endName)
    {
	int startLine = myPeekLine;
	int startColumn = myPeekColumn;

	if (read () != '<')
	{
	    // check in caller should prevent this
	    throw new RuntimeException ("shouldn't happen");
	}

	skipWhitespace ();

	if (peek () == '/')
	{
	    // deal with an end tag

	    read ();
	    String name = readUntilWhitespaceOrAngleOrSlash ();
	    skipWhitespace ();

	    if (read () != '>')
	    {
		throw new XmlParseException ("expected '>'", 
					     startLine, startColumn,
					     myReadLine, myReadColumn);
	    }

	    if (endName == null)
	    {
		throw new XmlParseException ("unexpected end tag </" + name +
					     ">",
					     startLine, startColumn,
					     myReadLine, myReadColumn);
	    }

	    if (endName.equals (name))
	    {
		// successfully parsed the named end tag
		return null;
	    }

	    throw new XmlParseException ("unexpected end tag </" + name +
					 ">; expected </" + endName + ">",
					 startLine, startColumn,
					 myReadLine, myReadColumn);
	}

	String name = readUntilWhitespaceOrAngleOrSlash ();
	
	if (name.startsWith ("!--"))
	{
	    // finish reading comment; it's a butt-simple state machine;
	    // substring(3) so that the "!--" isn't part of the comment
	    // text
	    StringBuffer sb = new StringBuffer (name.substring (3));
	    int state = 0;

	    for (;;)
	    {
		int c = read ();
		if (c == '-')
		{
		    if (state == 0) state = 1; else state = 2;
		}
		else if ((c == '>') && (state == 2))
		{
		    break;
		}
		else
		{
		    state = 0;
		}
		sb.append ((char) c);
	    }

	    // get rid of the last two dashes
	    sb.setLength (sb.length () - 2);

	    return new CommentNode (startLine, startColumn, 
				    myPeekLine, myPeekColumn,
				    sb.toString ());
	}

	// parse the attributes

	boolean gotClose = false;
	AttributeSet attribs = new AttributeSet ();
	for (;;)
	{
	    int c = peek ();
	    if (c == '>')
	    {
		read ();
		break;
	    }
	    else if (c == '/')
	    {
		read ();
		skipWhitespace ();
		if (read () != '>')
		{
		    throw new XmlParseException ("expected character '>'",
						 startLine, startColumn,
						 myReadLine, myReadColumn);
		}
		gotClose = true;
		break;
	    }
	    else if (c == '<')
	    {
		throw new XmlParseException ("unexpected character '<'",
					     startLine, startColumn,
					     myPeekLine, myPeekColumn);
	    }
	    else if (c == PEEK_EOF)
	    {
		throw new XmlParseException ("unexpected eof", 
					     startLine, startColumn,
					     myPeekLine, myPeekColumn);
	    }

	    Attribute attrib = attribute ();
	    if (attribs.put (attrib) != null)
	    {
		throw new XmlParseException ("duplicate attribute name \"" +
					     attrib.getName () + "\"",
					     startLine, startColumn,
					     myReadLine, myReadColumn);
	    }
	}

	Fragment body = gotClose ? new Fragment () : fragment (name);

	return new TagNode (startLine, startColumn,
			    myReadLine, myReadColumn,
			    name, attribs, body);
    }

    /**
     * Read an attribute pair.
     *
     * @return non-null; the attribute
     */
    private Attribute attribute ()
    {
	skipWhitespace ();

	StringBuffer sb = new StringBuffer ();
	int startLine = myPeekLine;
	int startColumn = myPeekColumn;

	for (;;)
	{
	    int c = peek ();
	    if ((c == PEEK_EOF) || 
		(c == '<') || 
		(c == '>') || 
		(c == '=') ||
		Character.isWhitespace ((char) c))
	    {
		break;
	    }

	    sb.append ((char) c);
	    read ();
	}

	String name = sb.toString ();
	if (name.length () == 0)
	{
	    throw new XmlParseException ("expected attribute",
					 startLine, startColumn,
					 startLine, startColumn);
	}

	skipWhitespace ();

	if (read () != '=')
	{
	    throw new XmlParseException ("expected character '='",
					 startLine, startColumn,
					 myReadLine, myReadColumn);
	}

	skipWhitespace ();

	if (read () != '\"')
	{
	    throw new XmlParseException ("expected character '\\\"'",
					 startLine, startColumn,
					 myReadLine, myReadColumn);
	}

	ArrayList elems = new ArrayList ();
	for (;;)
	{
	    int c = peek ();
	    if (c == PEEK_EOF)
	    {
		throw new XmlParseException ("unexpected eof", 
					     startLine, startColumn,
					     myPeekLine, myPeekColumn);
	    }
	    else if (c == '\"')
	    {
		read ();
		break;
	    }
	    else if ((c == '<') || (c == '>'))
	    {
		read ();
		throw new XmlParseException ("unexpected character '" + 
					     (char) c + "'", 
					     myReadLine, myReadColumn,
					     myPeekLine, myPeekColumn);
	    }
	    else if (c == '&')
	    {
		elems.add (entity ());
	    }
	    else
	    {
		elems.add (text (true));
	    }
	}

	Fragment value = new Fragment (elems);

	skipWhitespace ();
	return new Attribute (name, value);
    }
}

