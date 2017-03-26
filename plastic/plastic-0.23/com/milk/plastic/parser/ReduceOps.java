package com.milk.plastic.parser;

import antlr.collections.AST;
import antlr.CommonAST;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Tree manipulator which reduces nodes whose heads are ops to
 * simple apply nodes. It also sets the type of any literal node
 * to be simply LITERAL and will reduce constant expressions to
 * LITERALS. Fimally, it turns one-argument INPUT forms
 * into the standard two-argument form (adding an iota as the
 * last argument).
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
/*package*/ class ReduceOps
    implements PlasticTokenTypes
{
    // ------------------------------------------------------------------------
    // constructors

    /**
     * This class is uninstantiable.
     */
    private ReduceOps ()
    {
	// this space intentionally left blank
    }

    // ------------------------------------------------------------------------
    // public static methods

    /**
     * Reduce the given tree.
     *
     * @param orig the tree to reduce
     * @return the reduced form
     */
    static public AST reduce (AST orig)
    {
	int origType = orig.getType ();

	if (origType == LITERAL)
	{
	    // need to do this to break the sibling relationship,
	    // since callers will be building up a new tree
	    return new LiteralAST ((LiteralAST) orig);
	}

	String func = null;

	switch (origType)
	{
            case AND:       func = "and"; break;
            case DIVIDE:    func = "div"; break;
            case EQ:        func = "eq";  break;
            case GE:        func = "ge";  break;
            case GT:        func = "gt";  break;
            case LE:        func = "le";  break;
            case LT:        func = "lt";  break;
            case MINUS:     func = "sub"; break;
            case NE:        func = "ne";  break;
            case NOT:       func = "not"; break;
            case OR:        func = "or";  break;
            case PLUS:      func = "add"; break;
            case POW:       func = "pow"; break;
            case REMAINDER: func = "rem"; break;
            case TIMES:     func = "mul"; break;
            case XOR:       func = "xor"; break;
	}

	if (func == null)
	{
	    CommonAST result = new CommonAST ();
	    result.setType (origType);
	    result.setText (orig.getText ());

	    AST child = orig.getFirstChild ();
	    while (child != null)
	    {
		result.addChild (reduce (child));
		child = child.getNextSibling ();
	    }

	    if ((PlasticAST.astLength (orig) == 1) &&
		(origType == INPUT))
	    {
		// add an iota to a single-argument input
		CommonAST iotaChild = new CommonAST ();
		iotaChild.setType (IOTA);
		iotaChild.setText ("#");
		result.addChild (iotaChild);
	    }
	    
	    return result;
	}

	// collect a list of reduced children; note if all of the
	// children are literals, saying that we can fold away this
	// application
	ArrayList reducedKids = new ArrayList (2);
	AST child = orig.getFirstChild ();
	boolean canFold = true;
	while (child != null)
	{
	    AST red = reduce (child);
	    if (red.getType () != LITERAL)
	    {
		canFold = false;
	    }

	    reducedKids.add (red);
	    child = child.getNextSibling ();
	}

	if (! canFold)
	{
	    // can't fold away a constant expression; spit out an
	    // application
	    CommonAST result = new CommonAST ();
	    result.setType (APPLY);
	    result.setText ("APPLY");
	    CommonAST funcChild = new CommonAST ();
	    funcChild.setType (IDENTIFIER);
	    funcChild.setText (func);
	    result.addChild (funcChild);

	    Iterator i = reducedKids.iterator ();
	    while (i.hasNext ())
	    {
		AST one = (AST) i.next ();
		CommonAST bindChild = new CommonAST ();
		bindChild.setType (BIND);
		bindChild.setText ("BIND");
		CommonAST iotaChild = new CommonAST ();
		iotaChild.setType (IOTA);
		iotaChild.setText ("#");
		bindChild.addChild (iotaChild);
		bindChild.addChild (one);
		result.addChild (bindChild);
	    }
	
	    return result;
	}

	return foldExpression (origType, reducedKids);
    }

    // ------------------------------------------------------------------------
    // private static methods

    /**
     * Helper to reduce a constant expression.
     *
     * @param funcType the token type reprenting the function to apply
     * @param args the arguments to the function, in the form of LITERAL
     * tokens
     * @return the reduced form
     */
    static private AST foldExpression (int funcType, ArrayList args)
    {
	// turn the arguments into an array of wrapped primitives
	int argCount = args.size ();
	Object[] argArr = new Object[argCount];
	int stringCount = 0;
	int booleanCount = 0;
	int integerCount = 0;
	int floatCount = 0;
	for (int i = 0; i < argCount; i++)
	{
	    Object one = ((LiteralAST) args.get (i)).getValue ();
	    argArr[i] = one;
	    if (one instanceof String)
	    {
		stringCount++;
	    }
	    else if (one instanceof Boolean)
	    {
		booleanCount++;
	    }
	    else if (one instanceof Integer)
	    {
		integerCount++;
	    }
	    else if (one instanceof Double)
	    {
		floatCount++;
	    }
	}

	if ((stringCount != 0) ||
	    ((booleanCount != 0) && ((integerCount + floatCount) != 0)))
	{
	    throw new RuntimeException ("bad constant expression: " +
					args);
	}

	if ((integerCount != 0) && (floatCount != 0))
	{
	    // there's a mix of integer and float; coerce the integers up
	    for (int i = 0; i < argCount; i++)
	    {
		if (argArr[i] instanceof Integer)
		{
		    Integer orig = (Integer) argArr[i];
		    argArr[i] = new Double (orig.intValue ());
		}
	    }
	}

	Object result;

	switch (funcType)
	{
            case AND:       result = applyAnd       (argArr); break;
            case OR:        result = applyOr        (argArr); break;
            case XOR:       result = applyXor       (argArr); break;
            case NOT:       result = applyNot       (argArr); break;
            case PLUS:      result = applyPlus      (argArr); break;
            case MINUS:     result = applyMinus     (argArr); break;
            case TIMES:     result = applyTimes     (argArr); break;
            case DIVIDE:    result = applyDivide    (argArr); break;
            case REMAINDER: result = applyRemainder (argArr); break;
            case POW:       result = applyPow       (argArr); break;
            case EQ:        result = applyEq        (argArr); break;
            case NE:        result = applyNe        (argArr); break;
            case GE:        result = applyGe        (argArr); break;
            case GT:        result = applyGt        (argArr); break;
            case LE:        result = applyLe        (argArr); break;
            case LT:        result = applyLt        (argArr); break;
	    default:
	    {
		throw new RuntimeException ("bad function to foldExpression");
	    }
	}

	return new LiteralAST (result);
    }

    /**
     * Apply the AND function.
     *
     * @param args the arguments to the function
     * @return the result
     */
    static private Object applyAnd (Object[] args)
    {
	if (args[0] instanceof Boolean)
	{
	    boolean result = true;
	    for (int i = 0; i < args.length; i++)
	    {
		result = result && ((Boolean) args[i]).booleanValue ();
	    }
	    return result ? Boolean.TRUE : Boolean.FALSE;
	}
	else if (args[0] instanceof Integer)
	{
	    int result = ~0;
	    for (int i = 0; i < args.length; i++)
	    {
		result = result & ((Integer) args[i]).intValue ();
	    }
	    return new Integer (result);
	}
	else
	{
	    throw new RuntimeException ("bad arguments to &");
	}
    }

    /**
     * Apply the OR function.
     *
     * @param args the arguments to the function
     * @return the result
     */
    static private Object applyOr (Object[] args)
    {
	if (args[0] instanceof Boolean)
	{
	    boolean result = false;
	    for (int i = 0; i < args.length; i++)
	    {
		result = result || ((Boolean) args[i]).booleanValue ();
	    }
	    return result ? Boolean.TRUE : Boolean.FALSE;
	}
	else if (args[0] instanceof Integer)
	{
	    int result = 0;
	    for (int i = 0; i < args.length; i++)
	    {
		result = result | ((Integer) args[i]).intValue ();
	    }
	    return new Integer (result);
	}
	else
	{
	    throw new RuntimeException ("bad arguments to |");
	}
    }

    /**
     * Apply the XOR function.
     *
     * @param args the arguments to the function
     * @return the result
     */
    static private Object applyXor (Object[] args)
    {
	if (args[0] instanceof Boolean)
	{
	    boolean result = false;
	    for (int i = 0; i < args.length; i++)
	    {
		if (((Boolean) args[i]).booleanValue ())
		{
		    result = !result;
		}
	    }
	    return result ? Boolean.TRUE : Boolean.FALSE;
	}
	else if (args[0] instanceof Integer)
	{
	    int result = 0;
	    for (int i = 0; i < args.length; i++)
	    {
		result = result ^ ((Integer) args[i]).intValue ();
	    }
	    return new Integer (result);
	}
	else
	{
	    throw new RuntimeException ("bad arguments to ^");
	}
    }

    /**
     * Apply the NOT function.
     *
     * @param args the arguments to the function
     * @return the result
     */
    static private Object applyNot (Object[] args)
    {
	if (args[0] instanceof Boolean)
	{
	    boolean result = !((Boolean) args[0]).booleanValue ();
	    return result ? Boolean.TRUE : Boolean.FALSE;
	}
	else if (args[0] instanceof Integer)
	{
	    int result = ~((Integer) args[0]).intValue ();
	    return new Integer (result);
	}
	else
	{
	    throw new RuntimeException ("bad arguments to !");
	}
    }

    /**
     * Apply the PLUS function.
     *
     * @param args the arguments to the function
     * @return the result
     */
    static private Object applyPlus (Object[] args)
    {
	if (args[0] instanceof Double)
	{
	    double result = 0.0;
	    for (int i = 0; i < args.length; i++)
	    {
		result += ((Double) args[i]).doubleValue ();
	    }
	    return new Double (result);
	}
	else if (args[0] instanceof Integer)
	{
	    int result = 0;
	    for (int i = 0; i < args.length; i++)
	    {
		result += ((Integer) args[i]).intValue ();
	    }
	    return new Integer (result);
	}
	else
	{
	    throw new RuntimeException ("bad arguments to +");
	}
    }

    /**
     * Apply the MINUS function.
     *
     * @param args the arguments to the function
     * @return the result
     */
    static private Object applyMinus (Object[] args)
    {
	if (args[0] instanceof Double)
	{
	    double result = ((Double) args[0]).doubleValue ();
	    if (args.length == 1)
	    {
		result = -result;
	    }
	    else
	    {
		for (int i = 1; i < args.length; i++)
		{
		    result -= ((Double) args[i]).doubleValue ();
		}
	    }
	    return new Double (result);
	}
	else if (args[0] instanceof Integer)
	{
	    int result = ((Integer) args[0]).intValue ();
	    if (args.length == 1)
	    {
		result = -result;
	    }
	    else
	    {
		for (int i = 1; i < args.length; i++)
		{
		    result -= ((Integer) args[i]).intValue ();
		}
	    }
	    return new Integer (result);
	}
	else
	{
	    throw new RuntimeException ("bad arguments to -");
	}
    }

    /**
     * Apply the TIMES function.
     *
     * @param args the arguments to the function
     * @return the result
     */
    static private Object applyTimes (Object[] args)
    {
	if (args[0] instanceof Double)
	{
	    // FIXME: there are probably NaN/Infinity issues with this
	    double result = 1.0;
	    for (int i = 0; i < args.length; i++)
	    {
		result *= ((Double) args[i]).doubleValue ();
	    }
	    return new Double (result);
	}
	else if (args[0] instanceof Integer)
	{
	    int result = 1;
	    for (int i = 0; i < args.length; i++)
	    {
		result *= ((Integer) args[i]).intValue ();
	    }
	    return new Integer (result);
	}
	else
	{
	    throw new RuntimeException ("bad arguments to *");
	}
    }

    /**
     * Apply the DIVIDE function.
     *
     * @param args the arguments to the function
     * @return the result
     */
    static private Object applyDivide (Object[] args)
    {
	if (args[0] instanceof Double)
	{
	    double result = ((Double) args[0]).doubleValue ();
	    if (args.length == 1)
	    {
		result = 1.0 / result;
	    }
	    else
	    {
		for (int i = 1; i < args.length; i++)
		{
		    result /= ((Double) args[i]).doubleValue ();
		}
	    }
	    return new Double (result);
	}
	else if (args[0] instanceof Integer)
	{
	    int result = ((Integer) args[0]).intValue ();
	    if (args.length == 1)
	    {
		result = 1 / result;
	    }
	    else
	    {
		for (int i = 1; i < args.length; i++)
		{
		    result /= ((Integer) args[i]).intValue ();
		}
	    }
	    return new Integer (result);
	}
	else
	{
	    throw new RuntimeException ("bad arguments to /");
	}
    }

    /**
     * Apply the REMAINDER function.
     *
     * @param args the arguments to the function
     * @return the result
     */
    static private Object applyRemainder (Object[] args)
    {
	if (args[0] instanceof Double)
	{
	    double result = ((Double) args[0]).doubleValue ();
	    if (args.length == 1)
	    {
		result = 1.0 % result;
	    }
	    else
	    {
		for (int i = 1; i < args.length; i++)
		{
		    result %= ((Double) args[i]).doubleValue ();
		}
	    }
	    return new Double (result);
	}
	else if (args[0] instanceof Integer)
	{
	    int result = ((Integer) args[0]).intValue ();
	    if (args.length == 1)
	    {
		result = 1 % result;
	    }
	    else
	    {
		for (int i = 1; i < args.length; i++)
		{
		    result %= ((Integer) args[i]).intValue ();
		}
	    }
	    return new Integer (result);
	}
	else
	{
	    throw new RuntimeException ("bad arguments to /");
	}
    }

    /**
     * Apply the POW function.
     *
     * @param args the arguments to the function
     * @return the result
     */
    static private Object applyPow (Object[] args)
    {
	if (args[0] instanceof Double)
	{
	    double result = ((Double) args[0]).doubleValue ();
	    for (int i = 1; i < args.length; i++)
	    {
		result = Math.pow (result, ((Double) args[i]).doubleValue ());
	    }
	    return new Double (result);
	}
	else if (args[0] instanceof Integer)
	{
	    int result = ((Integer) args[0]).intValue ();
	    for (int i = 1; i < args.length; i++)
	    {
		result = 
		    (int) Math.pow (result, ((Double) args[i]).doubleValue ());
	    }
	    return new Integer (result);
	}
	else
	{
	    throw new RuntimeException ("bad arguments to /");
	}
    }

    /**
     * Apply the EQ function.
     *
     * @param args the arguments to the function
     * @return the result
     */
    static private Object applyEq (Object[] args)
    {
	if (args[0] instanceof Double)
	{
	    double last = ((Double) args[0]).doubleValue ();
	    for (int i = 1; i < args.length; i++)
	    {
		double next = ((Double) args[0]).doubleValue ();
		if (last != next)
		{
		    return Boolean.FALSE;
		}
		next = last;
	    }
	    return Boolean.TRUE;
	}
	else if (args[0] instanceof Integer)
	{
	    int last = ((Integer) args[0]).intValue ();
	    for (int i = 1; i < args.length; i++)
	    {
		int next = ((Integer) args[0]).intValue ();
		if (last != next)
		{
		    return Boolean.FALSE;
		}
		next = last;
	    }
	    return Boolean.TRUE;
	}
	else if (args[0] instanceof Boolean)
	{
	    boolean first = ((Boolean) args[0]).booleanValue ();
	    for (int i = 1; i < args.length; i++)
	    {
		if (first != ((Boolean) args[0]).booleanValue ())
		{
		    return Boolean.FALSE;
		}
	    }
	    return Boolean.TRUE;
	}
	else
	{
	    throw new RuntimeException ("bad arguments to ==");
	}
    }

    /**
     * Apply the NE function.
     *
     * @param args the arguments to the function
     * @return the result
     */
    static private Object applyNe (Object[] args)
    {
	return (applyEq (args) == Boolean.FALSE) ? 
	    Boolean.TRUE : 
	    Boolean.FALSE;
    }

    /**
     * Apply the LT function.
     *
     * @param args the arguments to the function
     * @return the result
     */
    static private Object applyLt (Object[] args)
    {
	// note: !(x < y) is not the same as (x >= y), because of NaNs
	if (args[0] instanceof Double)
	{
	    double last = ((Double) args[0]).doubleValue ();
	    for (int i = 1; i < args.length; i++)
	    {
		double next = ((Double) args[0]).doubleValue ();
		if (! (last < next))
		{
		    return Boolean.FALSE;
		}
		next = last;
	    }
	    return Boolean.TRUE;
	}
	else if (args[0] instanceof Integer)
	{
	    int last = ((Integer) args[0]).intValue ();
	    for (int i = 1; i < args.length; i++)
	    {
		int next = ((Integer) args[0]).intValue ();
		if (! (last < next))
		{
		    return Boolean.FALSE;
		}
		next = last;
	    }
	    return Boolean.TRUE;
	}
	else
	{
	    throw new RuntimeException ("bad arguments to <");
	}
    }

    /**
     * Apply the GT function.
     *
     * @param args the arguments to the function
     * @return the result
     */
    static private Object applyGt (Object[] args)
    {
	// note: !(x < y) is not the same as (x >= y), because of NaNs
	if (args[0] instanceof Double)
	{
	    double last = ((Double) args[0]).doubleValue ();
	    for (int i = 1; i < args.length; i++)
	    {
		double next = ((Double) args[0]).doubleValue ();
		if (! (last > next))
		{
		    return Boolean.FALSE;
		}
		next = last;
	    }
	    return Boolean.TRUE;
	}
	else if (args[0] instanceof Integer)
	{
	    int last = ((Integer) args[0]).intValue ();
	    for (int i = 1; i < args.length; i++)
	    {
		int next = ((Integer) args[0]).intValue ();
		if (! (last > next))
		{
		    return Boolean.FALSE;
		}
		next = last;
	    }
	    return Boolean.TRUE;
	}
	else
	{
	    throw new RuntimeException ("bad arguments to >");
	}
    }

    /**
     * Apply the LE function.
     *
     * @param args the arguments to the function
     * @return the result
     */
    static private Object applyLe (Object[] args)
    {
	// note: !(x < y) is not the same as (x >= y), because of NaNs
	if (args[0] instanceof Double)
	{
	    double last = ((Double) args[0]).doubleValue ();
	    for (int i = 1; i < args.length; i++)
	    {
		double next = ((Double) args[0]).doubleValue ();
		if (! (last <= next))
		{
		    return Boolean.FALSE;
		}
		next = last;
	    }
	    return Boolean.TRUE;
	}
	else if (args[0] instanceof Integer)
	{
	    int last = ((Integer) args[0]).intValue ();
	    for (int i = 1; i < args.length; i++)
	    {
		int next = ((Integer) args[0]).intValue ();
		if (! (last <= next))
		{
		    return Boolean.FALSE;
		}
		next = last;
	    }
	    return Boolean.TRUE;
	}
	else
	{
	    throw new RuntimeException ("bad arguments to <=");
	}
    }

    /**
     * Apply the GE function.
     *
     * @param args the arguments to the function
     * @return the result
     */
    static private Object applyGe (Object[] args)
    {
	// note: !(x < y) is not the same as (x >= y), because of NaNs
	if (args[0] instanceof Double)
	{
	    double last = ((Double) args[0]).doubleValue ();
	    for (int i = 1; i < args.length; i++)
	    {
		double next = ((Double) args[0]).doubleValue ();
		if (! (last >= next))
		{
		    return Boolean.FALSE;
		}
		next = last;
	    }
	    return Boolean.TRUE;
	}
	else if (args[0] instanceof Integer)
	{
	    int last = ((Integer) args[0]).intValue ();
	    for (int i = 1; i < args.length; i++)
	    {
		int next = ((Integer) args[0]).intValue ();
		if (! (last >= next))
		{
		    return Boolean.FALSE;
		}
		next = last;
	    }
	    return Boolean.TRUE;
	}
	else
	{
	    throw new RuntimeException ("bad arguments to >=");
	}
    }


}


