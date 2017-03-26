package com.milk.plastic.parser;

import antlr.collections.AST;
import com.milk.plastic.iface.PlasticException;
import java.io.InputStream;

/**
 * Useful static methods for helping out with doing tree (AST) parsing.
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
public final class PlasticAST
{
    // ------------------------------------------------------------------------
    // constructors

    /**
     * This class is uninstantiable.
     */
    private PlasticAST ()
    {
	// this space intentionally left blank
    }

    // ------------------------------------------------------------------------
    // public static methods

    /**
     * Parse the given input stream into an AST.
     *
     * @param input the stream to parse
     * @return a parsed and validated AST
     */
    static public AST parseInput (InputStream input)
    {
	AST ast;

	try
	{
	    PlasticLexer lex = new PlasticLexer (input);
	    PlasticParser par = new PlasticParser (lex);
	    
	    par.topLevel ();
	    ast = par.getAST ();
	    ast = ReduceOps.reduce (ast);
	    Validate.validate (ast);
	}
	catch (Exception ex)
	{
	    throw new PlasticException ("trouble parsing input", ex);
	}

	return ast;
    }

    /**
     * Return the number of children of the given AST.
     *
     * @param ast the ast to query
     * @return the number of children
     */
    static public int astLength (AST ast)
    {
	int result = 0;
	ast = ast.getFirstChild ();
	while (ast != null)
	{
	    result++;
	    ast = ast.getNextSibling ();
	}

	return result;
    }
}
