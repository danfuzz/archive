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

import java.util.TreeMap;

/**
 * This class just contains helper static methods to deal with URLs.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class URLUtils
{
    /** digit characters to use for <code>%</code> encoding */
    static private final char[] theHexDigits = 
        { '0', '1', '2', '3', '4', '5', '6', '7',
	  '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    // ------------------------------------------------------------------------
    // constructors

    /**
     * This class is uninstantiable.
     */
    private URLUtils ()
    {
	// this space intentionally left blank
    }

    // ------------------------------------------------------------------------
    // static public methods

    /**
     * Split the given URL path into three pieces, the first path
     * component, the first query component, and the remainder. This is
     * returned as an array of three strings, in the order given in the
     * previous sentence. The first path component will have had
     * <code>%</code> expansion done on it, but the other strings will not
     * have been processed in any way. If there is only one path component,
     * then the remainder is returned as <code>null</code>. If there is no
     * first query component, then the query component is returned as
     * <code>null</code>. The empty string (<code>""</code>) is interpreted
     * as an empty (not <code>null</code>) first component, a
     * <code>null</code> query, and a <code>null</code> remainder. If there
     * is a problem with interpreting the string (for example, it contains
     * an invalid <code>%</code> form), then this will return
     * <code>null</code>.
     *
     * @param path non-null; the path to parse
     * @return null-ok; an array consisting of the first path component,
     * the first query component, and the remainder, in that order, or
     * <code>null</code> if there was a problem parsing the path 
     */
    static public String[] parseFirstComponent (String path)
    {
	// separate out the first component part
	int slashAt = path.indexOf ('/');

	String component;
	String remainder;

	if (slashAt == -1)
	{
	    component = path;
	    remainder = null;
	}
	else
	{
	    component = path.substring (0, slashAt);
	    remainder = path.substring (slashAt + 1);
	}

	// split the first component into the path part and the query part
	// (if any)

	String pathPart;
	String queryPart;
	int quesAt = component.indexOf ('?');
	if (quesAt == -1)
	{
	    pathPart = component;
	    queryPart = null;
	}
	else
	{
	    pathPart = component.substring (0, quesAt);
	    queryPart = component.substring (quesAt + 1);
	}

	// interpret % sequences in the path part
	pathPart = expandPercent (pathPart);
	if (pathPart == null)
	{
	    // it was a bad path
	    return null;
	}

	// cons up the result
	return new String[] { pathPart, queryPart, remainder };
    }

    /**
     * Expand <code>%</code> forms in the given string. Return
     * <code>null</code> if the string is invalid. This is guaranteed
     * to return the original string (the identical object) if there
     * is no need to perform any expansion.
     *
     * @param orig non-null; the original string
     * @return null-ok; the expanded form or <code>null</code> if the
     * original was invalid
     */
    static public String expandPercent (String orig)
    {
	int at = 0;
	for (;;)
	{
	    int percAt = orig.indexOf ('%', at);
	    if (percAt == -1)
	    {
		break;
	    }

	    char replacement = interpretHexChar (orig, percAt + 1);

	    if (replacement == '\uffff')
	    {
		// bad character in % form
		return null;
	    }

	    orig = 
		orig.substring (0, percAt) + 
		replacement +
		orig.substring (percAt + 3);
	    at = percAt + 1;
	}

	return orig;
    }

    /**
     * Turn a pair of characters representing a hexadecimal constant
     * into the indicated character. This will return <code>'\uffff'</code>
     * if the pair is invalid (i.e., contains non-hex characters).
     *
     * @param c1 the first character
     * @param c2 the second character
     * @return the translated character
     */
    static public char interpretHexChar (char c1, char c2)
    {
	char result;

	if ((c1 >= '0' && c1 <= '9'))
	{
	    result = (char) (c1 - '0');
	}
	else if ((c1 >= 'a' && c1 <= 'f'))
	{
	    result = (char) (c1 - 'a' + 10);
	}
	else if ((c1 >= 'A' && c1 <= 'F'))
	{
	    result = (char) (c1 - 'A' + 10);
	}
	else
	{
	    // bad character
	    return '\uffff';
	}
	
	result *= 16;
	
	if ((c2 >= '0' && c2 <= '9'))
	{
	    result += (char) (c2 - '0');
	}
	else if ((c2 >= 'a' && c2 <= 'f'))
	{
	    result += (char) (c2 - 'a' + 10);
	    }
	else if ((c2 >= 'A' && c2 <= 'F'))
	{
	    result += (char) (c2 - 'A' + 10);
	}
	else
	{
	    // bad character
	    return '\uffff';
	}

	return result;
    }

    /**
     * Turn a pair of characters representing a hexadecimal constant
     * located in a given string into the character they represent.
     * This will return <code>'\uffff'</code>
     * if the pair is invalid (i.e., contains non-hex characters or
     * is incomplete).
     *
     * @param string non-null; the string containing the characters
     * @param offset the offset into the string for the first character
     * @return the translated character
     */
    static public char interpretHexChar (String string, int offset)
    {
	char c1;
	char c2;
	
	try
	{
	    c1 = string.charAt (offset);
	    c2 = string.charAt (offset + 1);
	}
	catch (IndexOutOfBoundsException ex)
	{
	    // need at least two characters after the percent,
	    // so this isn't a valid % form
	    return '\uffff';
	}

	return interpretHexChar (c1, c2);
    }

    /**
     * Return the given string with <code>%</code> sequences replacing the
     * characters that are unsafe to use in a path component. A path
     * component is the part of a URL which names a single layer of
     * hierarchy, and does <i>not</i> include query parameters. If no
     * transformation needs to be done, then the original string is
     * returned as-is. See RFC2396 for a discussion about which characters
     * are reserved in this context.
     *
     * @param orig non-null; the original string
     * @return non-null; the transformed string 
     */
    static public String escapePathComponent (String orig)
    {
	StringBuffer result = null;
	int len = orig.length ();

	for (int i = 0; i < len; i++)
	{
	    char c = orig.charAt (i);
	    if ((! isPossiblyReserved (c))
		|| (c == ':')
		|| (c == '@')
		|| (c == '&')
		|| (c == '=')
		|| (c == '+')
		|| (c == '$')
		|| (c == ','))
	    {
		// these are all okay as-is
		if (result != null)
		{
		    result.append (c);
		}
	    }
	    else
	    {
		if (result == null)
		{
		    // delay initializing result until we know it's needed
		    result = new StringBuffer (len * 2);
		    result.append (orig.substring (0, i));
		}

		if (c > '\u00ff')
		{
		    throw new RuntimeException (
                        "Cannot encode non-ISO-8859-1 characters");
		}

		int d1 = (c >> 4) & 0x0f;
		int d2 = c & 0x0f;

		result.append ('%');
		result.append (theHexDigits[d1]);
		result.append (theHexDigits[d2]);
	    }
	}

	if (result == null)
	{
	    // didn't need to make any changes
	    return orig;
	}
	else
	{
	    return result.toString ();
	}
    }

    /**
     * Return the given string with <code>%</code> sequences replacing the
     * characters that are unsafe to use in a piece of a URL, being
     * maximally conservative. This method is particularly useful for
     * encoding query keys. If no transformation needs to be done, then the
     * original string is returned as-is. See RFC2396 for a discussion
     * about which characters are reserved in this context.
     *
     * @param orig non-null; the original string
     * @return non-null; the transformed string 
     */
    static public String escapeConservatively (String orig)
    {
	StringBuffer result = null;
	int len = orig.length ();

	for (int i = 0; i < len; i++)
	{
	    char c = orig.charAt (i);
	    if (! isPossiblyReserved (c))
	    {
		// these are all okay as-is
		if (result != null)
		{
		    result.append (c);
		}
	    }
	    else
	    {
		if (result == null)
		{
		    // delay initializing result until we know it's needed
		    result = new StringBuffer (len * 2);
		    result.append (orig.substring (0, i));
		}

		if (c > '\u00ff')
		{
		    throw new RuntimeException (
                        "Cannot encode non-ISO-8859-1 characters");
		}

		int d1 = (c >> 4) & 0x0f;
		int d2 = c & 0x0f;

		result.append ('%');
		result.append (theHexDigits[d1]);
		result.append (theHexDigits[d2]);
	    }
	}

	if (result == null)
	{
	    // didn't need to make any changes
	    return orig;
	}
	else
	{
	    return result.toString ();
	}
    }

    /**
     * Return whether the given character is possibly a reserved URI
     * character. That is, it returns <code>false</code> if there is no URI
     * context in which the character is reserved and <code>true</code> if
     * there is at least one context in which it <i>is</i> reserved. See
     * RFC2396 for further information.
     *
     * @param c the character to test
     * @return <code>true</code> if it is possibly reserved or
     * <code>false</code> if it is necessarily unreserved
     */
    static public boolean isPossiblyReserved (char c)
    {
	if (((c >= '0') && (c <= '9'))
	    || ((c >= 'a') && (c <= 'z'))
	    || ((c >= 'A') && (c <= 'Z'))
	    || (c == '-')
	    || (c == '_')
	    || (c == '.')
	    || (c == '!')
	    || (c == '~')
	    || (c == '*')
	    || (c == '\'')
	    || (c == '(')
	    || (c == ')'))
	{
	    return false;
	}

	return true;
    }

    /**
     * Get the first query parameter in the given query string, returning
     * an array of three elements: the key, the value, and the remainder of
     * the string. If there is only one query parameter, then the remainder
     * is returned as <code>null</code>. The key and value are
     * <code>%</code>-expanded, and the key is interned, but the remainder
     * is left untouched. If the string is empty, then the return value is
     * an array of three <code>null</code>s. If there is a problem with
     * interpreting the string (for example, it contains an invalid
     * <code>%</code> form), then this will return <code>null</code>.
     *
     * @param query non-null; the query to parse
     * @return null-ok; an array consisting of the first query key, the
     * first query value, and the remainder, in that order, or
     * <code>null</code> if there was a problem parsing the path 
     */
    static public String[] parseFirstQueryParam (String query)
    {
	int len = query.length ();
	int equalsAt = -1;
	int at = 0;

	while (at < len)
	{
	    char c = query.charAt (at);
	    if (c == '=')
	    {
		equalsAt = at;
		break;
	    }
	    at++;
	}

	if (equalsAt == -1)
	{
	    return null;
	}

	at++;

	while (at < len)
	{
	    char c = query.charAt (at);
	    if (c == '&')
	    {
		break;
	    }
	    at++;
	}

	String key = query.substring (0, equalsAt);
	String value = query.substring (equalsAt + 1, at);
	String rest;

	// note: order is important wrt the following three statements
	key = key.replace ('+', ' ');
	key = expandPercent (key);
	key = key.intern ();

	// note: order is important wrt the following two statements
	value = value.replace ('+', ' ');
	value = expandPercent (value);

	if (at >= (len - 1)) 
	{
	    rest = null;
	}
	else
	{
	    rest = query.substring (at + 1);
	}

	return new String[] { key, value, rest };
    }

    /**
     * Parse the given query string, returning a map of keys to values.
     * If a given key appears more than once in the query, then the value
     * in the map is an array of the parsed values; otherwise the value
     * is just a simple <code>String</code>. If there is trouble parsing
     * the query, then this returns <code>null</code>.
     * 
     * @param query non-null; the query string to parse
     * @return null-ok; a map of key/value(s) pairs, or <code>null</code>
     * if there was trouble parsing the query
     */
    static public TreeMap parseQueryString (String query)
    {
	TreeMap result = new TreeMap ();

	while (query != null)
	{
	    String[] one = parseFirstQueryParam (query);
	    if (one == null)
	    {
		return null;
	    }

	    Object already = result.put (one[0], one[1]);

	    if (already instanceof String)
	    {
		result.put (one[0], new String[] { (String) already, one[1] });
	    }
	    else if (already instanceof String[])
	    {
		String[] alreds = (String[]) already;
		String[] newa = new String[alreds.length + 1];
		System.arraycopy (alreds, 0, newa, 0, alreds.length);
		newa[alreds.length] = one[1];
		result.put (one[0], newa);
	    }

	    query = one[2];
	}

	return result;
    }

    /**
     * Get the first query parameter in the given query string for the
     * given key. The return value will have undergone translation to
     * get rid of <code>%</code> forms and other special characters.
     * This will return <code>null</code> if there was no such
     * key in the string.
     *
     * <p>A nice thing about this method over using {@link
     * #parseQueryString} (or the equivalent
     * <code>javax.servlet.http.HttpUtils.parseQueryString</code> in the
     * normal servlet package) is that this method avoids all allocation
     * except for the returned value.</p>
     *
     * @param query non-null; the query string to investigate
     * @param key non-null; the key to look for
     * @return null-ok; the first value associated with the given key in
     * the query string, or <code>null</code> if there was no such key 
     */
    static public String queryGet (String query, String key)
    {
	int len = query.length ();
	int keyLen = key.length ();
	int at = 0;
	int inKey;
	boolean found = false;

	while (at < len)
	{
	    // try to match the current key
	    inKey = 0;
	    while (at < len)
	    {
		char c = query.charAt (at);
		if (c == '=')
		{
		    // end of key in the query; may have a match
		    at++;
		    found = (inKey == keyLen);
		    break;
		}
		else if (c == '+')
		{
		    c = ' ';
		}
		else if (c == '%')
		{
		    at++;
		    c = interpretHexChar (query, at);
		    if (c == '\uffff')
		    {
			// bad % form
			break;
		    }
		    at++;
		}

		if ((inKey >= keyLen) || (c != key.charAt (inKey)))
		{
		    // not a match for the key
		    break;
		}

		inKey++;
		at++;
	    }

	    if (found)
	    {
		// found a match, extract the value
		int startAt = at;
		while (at < len)
		{
		    if (query.charAt (at) == '&')
		    {
			break;
		    }
		    at++;
		}
		String result = query.substring (startAt, at);

		// note: order is important wrt the following two statements
		result = result.replace ('+', ' ');
		result = expandPercent (result);

		return result;
	    }

	    // no match yet; skip to the start of the next param, if any
	    while (at < len)
	    {
		if (query.charAt (at) == '&')
		{
		    break;
		}
		at++;
	    }

	    // skip the '&' itself
	    at++;
	}

	// the key wasn't found
	return null;
    }

    /**
     * Get a query parameter as an <code>int</code> value, or return
     * the given default value if the parameter isn't present or is
     * invalid.
     *
     * @see #queryGet
     * @param query non-null; the query string to investigate
     * @param key non-null; the key to look for
     * @param dflt the default value to return if the key isn't found
     * or the value is invalid
     */
    static public int queryGetInt (String query, String key, int dflt)
    {
	String value = queryGet (query, key);
	if (value == null)
	{
	    return dflt;
	}

	try
	{
	    return Integer.parseInt (value);
	}
	catch (NumberFormatException ex)
	{
	    return dflt;
	}
    }

    /**
     * Get a query parameter as a <code>long</code> value, or return
     * the given default value if the parameter isn't present or is
     * invalid.
     *
     * @see #queryGet
     * @param query non-null; the query string to investigate
     * @param key non-null; the key to look for
     * @param dflt the default value to return if the key isn't found
     * or the value is invalid
     */
    static public long queryGetLong (String query, String key, long dflt)
    {
	String value = queryGet (query, key);
	if (value == null)
	{
	    return dflt;
	}

	try
	{
	    return Long.parseLong (value);
	}
	catch (NumberFormatException ex)
	{
	    return dflt;
	}
    }

    /**
     * Get a query parameter as a <code>boolean</code> value, or return
     * the given default value if the parameter isn't present.
     * The value is taken to be <code>false</code> if the value is
     * either <code>"false"</code> (case insensitively) or <code>"0"</code>,
     * and <code>true</code> otherwise.
     *
     * @see #queryGet
     * @param query non-null; the query string to investigate
     * @param key non-null; the key to look for
     * @param dflt the default value to return if the key isn't found
     */
    static public boolean queryGetBoolean (String query, String key, 
					   boolean dflt)
    {
	String value = queryGet (query, key);
	if (value == null)
	{
	    return dflt;
	}

	if (value.equals ("0")
	    || value.equalsIgnoreCase ("false"))
	{
	    return false;
	}
	
	return true;
    }

    /**
     * Canonicalize a request path, making sure it starts with a slash, and
     * getting rid of double slashes and components with the values
     * <code>"."</code> or <code>".."</code>. This will return
     * <code>null</code> if the given path is somehow perniciously
     * erroneous (such as referring to <code>".."</code> at the beginning
     * of the path).
     *
     * @param orig non-null; the original path
     * @return null-ok; the canonicalized form, or <code>null</code> if
     * the original is bogus in some way 
     */
    static public String canonicalPath (String orig)
    {
	if (orig.length () == 0)
	{
	    return "/";
	}

	if (orig.charAt (0) != '/')
	{
	    orig = '/' + orig;
	}

	for (;;)
	{
	    int foundAt = orig.indexOf ("//");
	    if (foundAt == -1)
	    {
		break;
	    }
	    orig = 
		orig.substring (0, foundAt) +
		orig.substring (foundAt + 1);
	}

	for (;;)
	{
	    int foundAt = orig.indexOf ("/./");
	    if (foundAt == -1)
	    {
		break;
	    }
	    orig = 
		orig.substring (0, foundAt) +
		orig.substring (foundAt + 2);
	}

	while (orig.endsWith ("/."))
	{
	    orig = orig.substring (0, orig.length () - 2);
	}

	for (;;)
	{
	    int foundAt = orig.indexOf ("/../");
	    if (foundAt == -1)
	    {
		break;
	    }

	    int prevSlash = orig.lastIndexOf ('/', foundAt - 1);
	    if (prevSlash == -1)
	    {
		// can't take .. at the beginning of the document
		return null;
	    }

	    orig = 
		orig.substring (0, prevSlash) +
		orig.substring (foundAt + 3);
	}

	while (orig.endsWith ("/.."))
	{
	    int prevSlash = orig.lastIndexOf ('/', orig.length () - 3);
	    if (prevSlash <= 0)
	    {
		// can't take .. at the beginning of the document
		return null;
	    }

	    orig = orig.substring (0, prevSlash);
	}

	return orig;
    }
}
