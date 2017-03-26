package com.milk.plastic.runner;

import antlr.collections.AST;
import com.milk.plastic.iface.Environment;
import com.milk.plastic.modules.Factories;
import com.milk.plastic.parser.PlasticAST;
import com.milk.plastic.util.AudioUtils;
import java.io.FileInputStream;

/**
 * Main harness for running. This knows how to take the name of a
 * file as input and parse and then run the <code>Main</code> object
 * defined in that file.
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
final public class Main
{
    // ------------------------------------------------------------------------
    // constructors

    /**
     * This class is uninstantiable.
     */
    private Main ()
    {
	// this space intentionally left blank
    }

    // ------------------------------------------------------------------------
    // static public methods

    /**
     * Load and play a single file.
     */
    static public void loadAndPlay (String fileName)
    {
	Environment env = new Environment ();
	Factories.bindCore (env);

	Runnable mainObj;

	System.err.println ("Loading...");

	try
	{
	    FileInputStream fis = new FileInputStream (fileName);

	    AST ast = PlasticAST.parseInput (fis);
	    env = ASTInterpreter.bind (ast, env);

	    mainObj = (Runnable) env.get ("main");
	}
	catch (Exception ex)
	{
	    System.err.println ("Trouble loading file:");
	    ex.printStackTrace ();
	    return;
	}

	System.err.println ("Running...");
	mainObj.run ();
	System.err.println ("Done.");
    }

    /**
     * Run the show.
     */
    static public void main (String[] args)
    {
	if (args.length != 1)
	{
	    System.err.println ("args: <file-to-run>");
	    System.exit (1);
	}

	loadAndPlay (args[0]);

	AudioUtils.closeAll ();

	// cuz the audio system keeps a non-daemon thread around
	System.exit (0);
    }
}


