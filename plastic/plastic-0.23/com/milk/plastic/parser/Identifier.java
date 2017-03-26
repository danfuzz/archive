package com.milk.plastic.parser;

import antlr.CommonToken;
import antlr.Token;

/**
 * Class for identifier tokens.
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
final public class Identifier
extends CommonToken
{
    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct an instance.
     *
     * @param orig a token representing the unparsed text
     */
    public Identifier (Token orig)
    {
	setType (PlasticTokenTypes.IDENTIFIER);
	setText (orig.getText ().intern ());
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
	return "ident[" + superString;
    }

    /**
     * Get the identifier value. It is always an interned string.
     *
     * @return the identifier value
     */
    public String getIdentifier ()
    {
	return getText ();
    }
}
