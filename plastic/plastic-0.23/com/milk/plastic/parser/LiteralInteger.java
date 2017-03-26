package com.milk.plastic.parser;

import antlr.CommonToken;
import antlr.Token;

/**
 * Class for integer literal tokens.
 *
 * <p>Copyright (C) 2000, Dan Bornstein, danfuzz@milk.com. All Rights 
 * Reserved. (Shrill TV degreaser.)</p>
 * 
 * <p>This file is part of the MILK Kodebase. The contents of this file are
 * subject to the MILK Kodebase Public License; you may not use this file
 * except in compliance with the License. A copy of the MILK Kodebase Public
 * License has been included with this distribution, and may be found in the
 * file named "LICENSE.html". You may also be able to obtain a copy of the
 * License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!</p>
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class LiteralInteger
extends CommonToken
implements LiteralToken
{
    /** the parsed value */
    private int myValue;
    
    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct an instance.
     *
     * @param orig a token representing the unparsed text
     */
    public LiteralInteger (Token orig)
    {
	String text = orig.getText ();

	if (text.startsWith ("0x") ||
	    text.startsWith ("0X"))
	{
	    myValue = Integer.parseInt (text.substring (2), 16);
	}
	else
	{
	    myValue = Integer.parseInt (text);
	}

	setType (PlasticTokenTypes.LITERAL_INTEGER);
	setText (text);
	setLine (orig.getLine ());
	setColumn (orig.getColumn ());
    }	

    // ------------------------------------------------------------------------
    // public instance methods

    /**
     * Return the string form of this instance.
     *
     * @return the string form
     */
    public String toString ()
    {
	String superString = super.toString ().substring (1);
	return "integer[" + myValue + "," + superString;
    }

    /**
     * Get the integer value.
     *
     * @return the integer value
     */
    public int getInt ()
    {
	return myValue;
    }

    // from interface LiteralToken
    public Object getValue ()
    {
	return new Integer (myValue);
    }
}
