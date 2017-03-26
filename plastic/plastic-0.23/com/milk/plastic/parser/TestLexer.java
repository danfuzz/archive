package com.milk.plastic.parser;

import antlr.Token;
import java.io.FileInputStream;

/**
 * Test of the lexer.
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
public class TestLexer
{
    static public void main (String[] args)
	throws Exception
    {
	PlasticLexer lex = 
	    new PlasticLexer (new FileInputStream (args[0]));
	for (;;)
	{
	    Token t = lex.nextToken ();
	    System.err.println ("=== " + t);
	    if (t.getType () == PlasticTokenTypes.EOF)
	    {
		break;
	    }
	}
    }
}
