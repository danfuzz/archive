// Copyright (c) 2000-2001 Dan Bornstein, danfuzz@milk.com. All rights 
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

package com.milk.tinyweb;

/**
 * This class merely holds a number of useful static
 * functions to deal with HTML.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public final class HTMLUtils
{
    // ------------------------------------------------------------------------
    // constructors

    /**
     * This class is uninstantiable.
     */
    private HTMLUtils ()
    {
	// this space intentionally left blank
    }

    // ------------------------------------------------------------------------
    // static public methods

    /**
     * Return the special HTML string to encode the given character
     * literally in HTML, or <code>null</code> if the character isn't special.
     *
     * @param orig the original character
     * @return null-ok; the special string encoding, or <code>null</code>
     * if there is none 
     */
    static public String literalCharToHtml (char orig)
    {
	switch (orig)
	{
	    case '\"': return "&quot;";
	    case '&':  return "&amp;";
	    case '<':  return "&lt;";
	    case '>':  return "&gt;";
	    default:   return null;
	}
    }

    /**
     * Convert the special HTML characters in a string into their
     * ampersand-escaped forms, so the string may be included
     * literally in HTML output, including the quoted values inside
     * tags. If no modifications need to take place, this may end up
     * returning just the original string.
     *
     * @param orig non-null; the original string
     * @return non-null; the munged string 
     */
    static public String literalToHtml (String orig)
    {
	int len = orig.length ();
	StringBuffer result = new StringBuffer (len * 2);
	boolean any = false;

	for (int i = 0; i < len; i++)
	{
	    char c = orig.charAt (i);
	    String special = literalCharToHtml (c);
	    if (special != null)
	    {
		any = true;
		result.append (special);
	    }
	    else
	    {
		result.append (c);
	    }
	}

	if (any)
	{
	    return result.toString ();
	}
	else
	{
	    return orig;
	}
    }

    /**
     * Return the special HTML string to encode the given character
     * literally in HTML, including using <code>"&lt;br&gt;"</code> to
     * encode newlines, or <code>null</code> if the character isn't
     * special.
     *
     * @param orig the original character
     * @return null-ok; the special string encoding, or <code>null</code>
     * if there is none 
     */
    static public String literalCharToHtmlWithBr (char orig)
    {
	switch (orig)
	{
	    case '\"': return "&quot;";
	    case '&':  return "&amp;";
	    case '<':  return "&lt;";
	    case '>':  return "&gt;";
	    case '\n': return "<br>\n";
	    default:   return null;
	}
    }

    /**
     * Convert the special HTML characters in a string into their
     * ampersand-escaped forms, including using <code>"&lt;br&gt;"</code>
     * to encode newlines, so the string may be included literally in HTML
     * output, including the quoted values inside tags. If no modifications
     * need to take place, this may end up returning just the original
     * string.
     *
     * @param orig non-null; the original string
     * @return non-null; the munged string 
     */
    static public String literalToHtmlWithBr (String orig)
    {
	int len = orig.length ();
	StringBuffer result = new StringBuffer (len * 2);
	boolean any = false;

	for (int i = 0; i < len; i++)
	{
	    char c = orig.charAt (i);
	    String special = literalCharToHtmlWithBr (c);
	    if (special != null)
	    {
		any = true;
		result.append (special);
	    }
	    else
	    {
		result.append (c);
	    }
	}

	if (any)
	{
	    return result.toString ();
	}
	else
	{
	    return orig;
	}
    }
}
