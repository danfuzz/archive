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

package com.milk.tinyweb.testing;

import com.milk.tinyweb.URLUtils;
import junit.framework.TestCase;

/**
 * Test cases for the class {@link URLUtils}.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public final class TestURLUtils
extends TestCase
{
    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param name the name of the test, passed to the test framework
     */
    public TestURLUtils (String name)
    {
	super (name);
    }



    // ------------------------------------------------------------------------
    // public test methods

    /**
     * Test the method {@link URLUtils#parseFirstComponent}.
     */
    public void testParseFirstComponent ()
    {
	// should throw if passed null
	try
	{
	    URLUtils.parseFirstComponent (null);
	    fail ("parseFirstComponent() failed to fail");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	String[] result = URLUtils.parseFirstComponent ("");
	assertEquals ("", result[0]);
	assertEquals (null, result[1]);
	assertEquals (null, result[2]);

	result = URLUtils.parseFirstComponent ("/foo");
	assertEquals ("", result[0]);
	assertEquals (null, result[1]);
	assertEquals ("foo", result[2]);

	result = URLUtils.parseFirstComponent ("?");
	assertEquals ("", result[0]);
	assertEquals ("", result[1]);
	assertEquals (null, result[2]);

	result = URLUtils.parseFirstComponent ("?/");
	assertEquals ("", result[0]);
	assertEquals ("", result[1]);
	assertEquals ("", result[2]);

	result = URLUtils.parseFirstComponent ("?abc");
	assertEquals ("", result[0]);
	assertEquals ("abc", result[1]);
	assertEquals (null, result[2]);

	result = URLUtils.parseFirstComponent ("/");
	assertEquals ("", result[0]);
	assertEquals (null, result[1]);
	assertEquals ("", result[2]);

	result = URLUtils.parseFirstComponent ("?def/");
	assertEquals ("", result[0]);
	assertEquals ("def", result[1]);
	assertEquals ("", result[2]);

	result = URLUtils.parseFirstComponent ("zubzub");
	assertEquals ("zubzub", result[0]);
	assertEquals (null, result[1]);
	assertEquals (null, result[2]);

	result = URLUtils.parseFirstComponent ("z%xfoo");
	assertNull (result);

	result = URLUtils.parseFirstComponent ("foo%5a%42%41%4C%4C/spork?x");
	assertEquals ("fooZBALL", result[0]);
	assertEquals (null, result[1]);
	assertEquals ("spork?x", result[2]);

	result = URLUtils.parseFirstComponent ("/spork%2a");
	assertEquals ("", result[0]);
	assertEquals (null, result[1]);
	assertEquals ("spork%2a", result[2]);

	result = URLUtils.parseFirstComponent ("foo%2Fbar/baz");
	assertEquals ("foo/bar", result[0]);
	assertEquals (null, result[1]);
	assertEquals ("baz", result[2]);
    }

    /**
     * Test the method {@link URLUtils#expandPercent}.
     */
    public void testExpandPercent ()
    {
	// test some error cases
	assertNull (URLUtils.expandPercent ("%"));
	assertNull (URLUtils.expandPercent ("%1"));
	assertNull (URLUtils.expandPercent ("%z"));
	assertNull (URLUtils.expandPercent ("%z#"));
	assertNull (URLUtils.expandPercent ("%9z"));
	assertNull (URLUtils.expandPercent ("q%"));
	assertNull (URLUtils.expandPercent ("w%1"));
	assertNull (URLUtils.expandPercent ("e%z"));
	assertNull (URLUtils.expandPercent ("r%z#"));
	assertNull (URLUtils.expandPercent ("t%9z"));

	// the rest should work

	assertSame ("foobar", URLUtils.expandPercent ("foobar"));

	for (int c1 = 0; c1 < 16; c1++)
	{
	    for (int c2 = 0; c2 < 16; c2++)
	    {
		String hex = 
		    "%" + 
		    Integer.toString (c1, 16) + 
		    Integer.toString (c2, 16);
		String s = "" + (char) (c1 * 16 + c2);

		assertEquals (s, URLUtils.expandPercent (hex));
		assertEquals (s, URLUtils.expandPercent (hex.toLowerCase ()));
		
		hex += '0';
		s += '0';

		assertEquals (s, URLUtils.expandPercent (hex));
		assertEquals (s, URLUtils.expandPercent (hex.toLowerCase ()));

		hex = '3' + hex;
		s = '3' + s;

		assertEquals (s, URLUtils.expandPercent (hex));
		assertEquals (s, URLUtils.expandPercent (hex.toLowerCase ()));
	    }
	}
    }

    /**
     * Test the method {@link URLUtils#interpretHexChar(char,char)}.
     */
    public void testInterpretHexChar1 ()
    {
	// test all the good cases

	for (int c1 = 0; c1 < 16; c1++)
	{
	    for (int c2 = 0; c2 < 16; c2++)
	    {
		char lc1 = Integer.toString (c1, 16).charAt (0);
		char lc2 = Integer.toString (c2, 16).charAt (0);
		char uc1 = Character.toUpperCase (lc1);
		char uc2 = Character.toUpperCase (lc2);
		char res = (char) (c1 * 16 + c2);

		assertEquals (res, URLUtils.interpretHexChar (lc1, lc2));
		assertEquals (res, URLUtils.interpretHexChar (lc1, uc2));
		assertEquals (res, URLUtils.interpretHexChar (uc1, lc2));
		assertEquals (res, URLUtils.interpretHexChar (uc1, uc2));
	    }
	}

	// test some error cases

	assertEquals ('\uffff', URLUtils.interpretHexChar ('z', 'g'));
	assertEquals ('\uffff', URLUtils.interpretHexChar ('z', 'a'));
	assertEquals ('\uffff', URLUtils.interpretHexChar ('z', 'F'));
	assertEquals ('\uffff', URLUtils.interpretHexChar ('z', '2'));
	assertEquals ('\uffff', URLUtils.interpretHexChar ('f', 'g'));
	assertEquals ('\uffff', URLUtils.interpretHexChar ('C', 'g'));
	assertEquals ('\uffff', URLUtils.interpretHexChar ('9', 'g'));
    }

    /**
     * Test the method {@link URLUtils#interpretHexChar(String,int)}.
     */
    public void testInterpretHexChar2 ()
    {
	// test some error cases

	try
	{
	    URLUtils.interpretHexChar (null, 0);
	    fail ("interpretHexChar() failed to fail (1)");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	assertEquals ('\uffff', URLUtils.interpretHexChar ("", 0));
	assertEquals ('\uffff', URLUtils.interpretHexChar ("", -1));
	assertEquals ('\uffff', URLUtils.interpretHexChar ("1", 0));
	assertEquals ('\uffff', URLUtils.interpretHexChar ("123", 2));
	assertEquals ('\uffff', URLUtils.interpretHexChar ("123", 3));

	// the rest should work; note: no need to do an exhaustive
	// test since the method uses the other interpretHexChar, and
	// that one *is* exhaustively tested

	assertEquals ('D', URLUtils.interpretHexChar ("44", 0));
	assertEquals ('a', URLUtils.interpretHexChar ("612", 0));
	assertEquals ('n', URLUtils.interpretHexChar ("b6e", 1));
	assertEquals ('f', URLUtils.interpretHexChar ("xy66", 2));
	assertEquals ('u', URLUtils.interpretHexChar ("1752", 1));
	assertEquals ('z', URLUtils.interpretHexChar ("9b7AFF", 2));
	assertEquals ('z', URLUtils.interpretHexChar ("!!!7a!!!", 3));
    }

    /**
     * Test the method {@link URLUtils#escapePathComponent}.
     */
    public void testEscapePathComponent ()
    {
	// should throw if passed null

	try
	{
	    URLUtils.escapePathComponent (null);
	    fail ("escapePathComponent() failed to fail");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	// the no-transformation-needed case

	assertSame ("blort", URLUtils.escapePathComponent ("blort"));

	// the transformation-needed cases

	String safeChars = 
	    "0123456789:@&=+$,abcdefghijklmnopqrstuvwxyz" +
	    "-_.!~*'()ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	for (char c = 0; c < 256; c++)
	{
	    if (safeChars.indexOf (c) != -1)
	    {
		continue;
	    }

	    String s = "" + c;
	    String res = URLUtils.escapePathComponent (s);

	    assertEquals (3, res.length ());
	    assertEquals ('%', res.charAt (0));
	    assertEquals (c, URLUtils.interpretHexChar (res, 1));

	    s = "foo" + s;
	    res = URLUtils.escapePathComponent (s);

	    assertEquals (6, res.length ());
	    assertEquals ('%', res.charAt (3));
	    assertEquals (c, URLUtils.interpretHexChar (res, 4));

	    s += c;
	    res = URLUtils.escapePathComponent (s);

	    assertEquals (9, res.length ());
	    assertEquals ('%', res.charAt (3));
	    assertEquals (c, URLUtils.interpretHexChar (res, 4));
	    assertEquals ('%', res.charAt (6));
	    assertEquals (c, URLUtils.interpretHexChar (res, 7));

	    s += "bar";
	    res = URLUtils.escapePathComponent (s);

	    assertEquals (12, res.length ());
	    assertEquals ('%', res.charAt (3));
	    assertEquals (c, URLUtils.interpretHexChar (res, 4));
	    assertEquals ('%', res.charAt (6));
	    assertEquals (c, URLUtils.interpretHexChar (res, 7));
	}
    }

    /**
     * Test the method {@link URLUtils#escapeConservatively}.
     */
    public void testEscapeConservatively ()
    {
	// should throw if passed null

	try
	{
	    URLUtils.escapeConservatively (null);
	    fail ("escapeConservatively() failed to fail");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	// the no-transformation-needed case

	assertSame ("blort", URLUtils.escapeConservatively ("blort"));

	// the transformation-needed cases

	String safeChars = 
	    "0123456789abcdefghijklmnopqrstuvwxyz" +
	    "-_.!~*'()ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	for (char c = 0; c < 256; c++)
	{
	    if (safeChars.indexOf (c) != -1)
	    {
		continue;
	    }

	    String s = "" + c;
	    String res = URLUtils.escapeConservatively (s);

	    assertEquals (3, res.length ());
	    assertEquals ('%', res.charAt (0));
	    assertEquals (c, URLUtils.interpretHexChar (res, 1));

	    s = "foo" + s;
	    res = URLUtils.escapeConservatively (s);

	    assertEquals (6, res.length ());
	    assertEquals ('%', res.charAt (3));
	    assertEquals (c, URLUtils.interpretHexChar (res, 4));

	    s += c;
	    res = URLUtils.escapeConservatively (s);

	    assertEquals (9, res.length ());
	    assertEquals ('%', res.charAt (3));
	    assertEquals (c, URLUtils.interpretHexChar (res, 4));
	    assertEquals ('%', res.charAt (6));
	    assertEquals (c, URLUtils.interpretHexChar (res, 7));

	    s += "bar";
	    res = URLUtils.escapeConservatively (s);

	    assertEquals (12, res.length ());
	    assertEquals ('%', res.charAt (3));
	    assertEquals (c, URLUtils.interpretHexChar (res, 4));
	    assertEquals ('%', res.charAt (6));
	    assertEquals (c, URLUtils.interpretHexChar (res, 7));
	}
    }

    /**
     * Test the method {@link URLUtils#isPossiblyReserved}.
     */
    public void testIsPossiblyReserved ()
    {
	String safeChars = 
	    "0123456789abcdefghijklmnopqrstuvwxyz" +
	    "-_.!~*'()ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	for (char c = 0; c < 256; c++)
	{
	    boolean result = URLUtils.isPossiblyReserved (c);
	    if (safeChars.indexOf (c) == -1)
	    {
		assertTrue (result);
	    }
	    else
	    {
		assertTrue (! result);
	    }
	}
    }

    /**
     * Test the method {@link URLUtils#queryGet}.
     */
    public void testQueryGet ()
    {
	// should fail if either argument is null

	try
	{
	    URLUtils.queryGet (null, null);
	    fail ("queryGet() failed to fail (1)");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	try
	{
	    URLUtils.queryGet ("foo", null);
	    fail ("queryGet() failed to fail (2)");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	try
	{
	    URLUtils.queryGet (null, "bar");
	    fail ("queryGet() failed to fail (3)");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	// the rest should succeed

	assertNull (URLUtils.queryGet ("", "x"));
	assertNull (URLUtils.queryGet ("%", "x"));
	assertNull (URLUtils.queryGet ("x=%", "x"));
	assertNull (URLUtils.queryGet ("x=%1", "x"));
	assertNull (URLUtils.queryGet ("z=", "x"));
	assertEquals ("", URLUtils.queryGet ("blort=", "blort"));
	assertEquals ("zorch1", URLUtils.queryGet ("poo=zorch1", "poo"));
	assertEquals ("zorch2", URLUtils.queryGet ("p%6fo=zorch2", "poo"));
	assertEquals ("zorch3", URLUtils.queryGet ("poo=zorc%683", "poo"));
	assertEquals ("%foo", 
		      URLUtils.queryGet ("x=y&xoo=zz&xo=%25foo", "xo"));
	assertEquals ("p&a", 
		      URLUtils.queryGet ("z%26=p%26a&zorch=blot", "z&"));
    }

    /**
     * Test the method {@link URLUtils#queryGetLong}.
     */
    public void testQueryGetLong ()
    {
	// no need to do all the weird cases, since the method relies
	// on queryGet(), and that one *is* tested fairly extensively

	assertEquals (123L, URLUtils.queryGetLong ("x=123", "x", 5L));
	assertEquals (-7L, URLUtils.queryGetLong ("x=-7", "x", 100L));
	assertEquals (55L, URLUtils.queryGetLong ("x=z", "x", 55L));
	assertEquals (550L, URLUtils.queryGetLong ("foo=1", "x", 550L));
    }

    /**
     * Test the method {@link URLUtils#queryGetBoolean}.
     */
    public void testQueryGetBoolean ()
    {
	// no need to do all the weird cases, since the method relies
	// on queryGet(), and that one *is* tested fairly extensively

	assertTrue (! URLUtils.queryGetBoolean ("x=FALSE", "x", true));
	assertTrue (! URLUtils.queryGetBoolean ("x=fAlsE", "x", true));
	assertTrue (! URLUtils.queryGetBoolean ("x=false", "x", true));
	assertTrue (! URLUtils.queryGetBoolean ("x=0", "x", true));
	assertTrue (URLUtils.queryGetBoolean ("x=1", "x", false));
	assertTrue (URLUtils.queryGetBoolean ("x=true", "x", false));
	assertTrue (URLUtils.queryGetBoolean ("x=blort", "x", false));
    }

    /**
     * Test the method {@link URLUtils#canonicalPath}.
     */
    public void testCanonicalPath ()
    {
	// should fail if the argument is null

	try
	{
	    URLUtils.canonicalPath (null);
	    fail ("canonicalPath() failed to fail");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	// the rest should succeed

	assertNull (URLUtils.canonicalPath ("/../x"));
	assertNull (URLUtils.canonicalPath ("../x"));
	assertNull (URLUtils.canonicalPath ("/./../x"));
	assertNull (URLUtils.canonicalPath ("/x/../.."));
	assertNull (URLUtils.canonicalPath ("/x/../../"));
	assertEquals ("/", URLUtils.canonicalPath (""));
	assertEquals ("/boo", URLUtils.canonicalPath ("boo"));
	assertEquals ("/spaz", URLUtils.canonicalPath ("/./spaz"));
	assertEquals ("/spaz", URLUtils.canonicalPath ("/spaz/."));
	assertEquals ("/spaz/mat/ic", 
		      URLUtils.canonicalPath ("/spaz/./mat/ic"));
	assertEquals ("/spaz/mat/ic/", 
		      URLUtils.canonicalPath ("//spaz///mat////ic/////"));
	assertEquals ("/foo/bar1", 
		      URLUtils.canonicalPath ("/foo/zip/../bar1"));
	assertEquals ("/foo/bar2", 
		      URLUtils.canonicalPath ("/foo/zip/../bar2"));
	assertEquals ("/foo/bar3", 
		      URLUtils.canonicalPath ("/foo/zip/./../bar3"));
	assertEquals ("/foo/bar4", 
		      URLUtils.canonicalPath ("/foo/zip/.//../bar4"));
	assertEquals ("/foo/bar5", 
		      URLUtils.canonicalPath ("/foo/zip/.///../bar5"));
    }
}
