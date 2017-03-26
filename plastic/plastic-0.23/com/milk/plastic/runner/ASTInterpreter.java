package com.milk.plastic.runner;

import antlr.collections.AST;
import com.milk.plastic.iface.Environment;
import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.FieldRef;
import com.milk.plastic.iface.NameRef;
import com.milk.plastic.iface.PlasticException;
import com.milk.plastic.iface.Ref;
import com.milk.plastic.parser.LiteralAST;
import com.milk.plastic.parser.PlasticTokenTypes;
import java.util.HashMap;

/**
 * Interpreter for AST input. The input handed to this class must have
 * already been validated (that is, this class doesn't do much in the
 * way of checking for bad data).
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
final public class ASTInterpreter
    implements PlasticTokenTypes
{
    // ------------------------------------------------------------------------
    // constructors

    /**
     * This class is uninstantiable.
     */
    private ASTInterpreter ()
    {
	// this space intentionally left blank
    }

    // ------------------------------------------------------------------------
    // public static methods

    /**
     * Interpret the given AST into a set of bindings. The given AST
     * should be of the TOPLEVEL type.
     *
     * @param tree the tree to interpret
     * @param parent the parent environment
     * @return an environment with the bindings that resulted from the
     * interpretation
     */
    static public Environment bind (AST tree, Environment parent)
    {
	if (tree.getType () != TOPLEVEL)
	{
	    throw new RuntimeException ("not a top-level tree: " + tree);
	}

	Environment result = new Environment (parent);

	AST child = tree.getFirstChild ();
	while (child != null)
	{
	    AST c1 = child.getFirstChild ();
	    String name = c1.getText ();
	    c1 = c1.getNextSibling ();
	    Object value = evalExpression (c1, result);
	    result.bind (name, value);
	    child = child.getNextSibling ();
	}

	return result;
    }

    // ------------------------------------------------------------------------
    // private static methods

    /**
     * Helper function to evaluate an expression tree.
     *
     * @param tree the tree to evaluate
     * @param env the environment to evaluate in
     * @return the result of evaluation
     */
    static private Object evalExpression (AST tree, Environment env)
    {
	switch (tree.getType ())
	{
	    case APPLY:
	    {
		return evalApply (tree, env);
	    }
	    case CLOSURE:    
	    {
		throw new RuntimeException ("no closures...yet");
	    }
	    case IDENTIFIER: 
	    {
		return new NameRef (env, tree.getText ());
	    }
	    case IOTA:
	    {
		throw new RuntimeException ("no iotas...yet");
	    }
	    case LITERAL:
	    {
		return ((LiteralAST) tree).getValue ();
	    }
	    case REF:
	    {
		AST child = tree.getFirstChild ();
		NameRef nr = (NameRef) evalExpression (child, env);
		child = child.getNextSibling ();
		return new FieldRef (env, nr.getName (), child.getText ());
	    }
	    default:
	    {
		throw new RuntimeException ("unknown expression type");
	    }
	}
    }

    /**
     * Helper function to evaluate an apply tree.
     *
     * @param tree the tree to evaluate
     * @param env the environment to evaluate in
     * @return the result of evaluation
     */
    static private Object evalApply (AST tree, Environment env)
    {
	AST child = tree.getFirstChild ();

	Object factoryObj = evalExpression (child, env);
	while (factoryObj instanceof Ref)
	{
	    factoryObj = ((Ref) factoryObj).resolve ();
	}

	Factory factory;
	try
	{
	    factory = (Factory) factoryObj;
	}
	catch (RuntimeException ex)
	{
	    throw new PlasticException ("name doesn't refer to a factory: " +
					child.getText (), ex);
	}

	// extract all the args into a map
	HashMap args = new HashMap (20);

	for (;;)
	{
	    child = child.getNextSibling ();
	    if (child == null)
	    {
		break;
	    }

	    AST c1 = child.getFirstChild ();
	    if (c1.getType () == IOTA)
	    {
		throw new RuntimeException ("iota not supported...yet");
	    }

	    String name = c1.getText ();
	    Object value = evalExpression (c1.getNextSibling (), env);
	    args.put (name, value);
	}

	// instantiate the object
	return factory.make (args);
    }
}
