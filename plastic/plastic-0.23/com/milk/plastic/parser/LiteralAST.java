package com.milk.plastic.parser;

import antlr.collections.AST;
import antlr.CommonAST;
import antlr.CommonToken;
import antlr.Token;

/**
 * AST type to hold a literal value (manifest constant in source).
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
public class LiteralAST
extends CommonAST
implements PlasticTokenTypes
{
    /** the value of the literal */
    private Object myValue;

    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct a cloned instance.
     *
     * @param ast the ast to base the instance on.
     */
    public LiteralAST (LiteralAST ast)
    {
	super (new CommonToken (LITERAL, ast.getText ()));
	myValue = ast.myValue;
    }

    /**
     * Construct an instance.
     *
     * @param value the value to base the instance on.
     */
    public LiteralAST (Object value)
    {
	super (new CommonToken (LITERAL, value.toString ()));
	myValue = value;
    }

    /**
     * Construct an instance.
     *
     * @param token the token to base the instance on.
     */
    public LiteralAST (Token token)
    {
	super (token);
	setType (LITERAL);
	if (token instanceof LiteralToken)
	{
	    myValue = ((LiteralToken) token).getValue ();
	}
	else
	{
	    String text = token.getText ();
	    if (text.equals ("true"))
	    {
		myValue = Boolean.TRUE;
	    }
	    else if (text.equals ("false"))
	    {
		myValue = Boolean.FALSE;
	    }
	    else
	    {
		throw new RuntimeException ("unknown literal: " + token);
	    }
	}
    }

    // ------------------------------------------------------------------------
    // public instance methods

    /**
     * Get the literal value.
     *
     * @return the value
     */
    public Object getValue ()
    {
	return myValue;
    }
}
