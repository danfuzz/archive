package com.milk.plastic.parser;

import antlr.collections.AST;
import antlr.CommonAST;
import java.util.HashSet;

/**
 * Tree walker which does initial validation of the forms. In particular,
 * it makes sure that each variable at the toplevel or in a closure is only
 * defined once, and makes sure that each argument name in an apply is only
 * used once (except for iota, which may be used as many times as is
 * desired). It will also make sure that there is only one yield statement
 * in a closure, and, if yield is used then ins and outs aren't also used.
 * In addition, it rejects trees that contain unexpected nodes (such as
 * apply nodes directly inside the toplevel).
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
/*package*/ class Validate
implements PlasticTokenTypes
{
    // ------------------------------------------------------------------------
    // constructors

    /**
     * This class is uninstantiable.
     */
    private Validate ()
    {
	// this space intentionally left blank
    }

    // ------------------------------------------------------------------------
    // public static methods

    /**
     * Validate the given tree.
     *
     * @param tree the tree to validate
     */
    static public void validate (AST tree)
    {
	int type = tree.getType ();
	switch (type)
	{
	    case APPLY:      validateApply (tree);      break;
	    case BIND:       validateBind (tree);       break;
	    case CLOSURE:    validateClosure (tree);    break;
	    case IDENTIFIER: validateIdentifier (tree); break;
	    case INPUT:      validateInput (tree);      break;
	    case IOTA:       validateIota (tree);       break; 
	    case LITERAL:    validateLiteral (tree);    break; 
	    case OUTPUT:     validateOutput (tree);     break;
	    case REF:        validateRef (tree);        break;
	    case TOPLEVEL:   validateTopLevel (tree);   break;
	    case YIELD:      validateYield (tree);      break;
	    default:
	    {
		throw new RuntimeException ("Unexpected node: " + type);
	    }	
	}
    }

    // ------------------------------------------------------------------------
    // private static methods

    /**
     * Validate an APPLY form.
     *
     * @param tree the tree to validate
     */
    static private void validateApply (AST tree)
    {
	if (PlasticAST.astLength (tree) == 0)
	{
	    throw new RuntimeException ("bad apply; length = 0");
	}

	AST child = tree.getFirstChild ();
	validateExpression (child);

	for (;;)
	{
	    child = child.getNextSibling ();
	    if (child == null)
	    {
		break;
	    }

	    validateBind (child);
	}
    }
    
    /**
     * Validate a BIND form.
     *
     * @param tree the tree to validate
     */
    static private void validateBind (AST tree)
    {
	if (tree.getType () != BIND)
	{
	    throw new RuntimeException ("not a bind: " + tree);
	}

	if (PlasticAST.astLength (tree) != 2)
	{
	    throw new RuntimeException ("bad bind; length != 2");
	}

	AST child = tree.getFirstChild ();
	int type = child.getType ();
	if ((type != IDENTIFIER) && (type != IOTA))
	{
	    throw new RuntimeException ("bad bind; not an identifier: " +
					child.getText ());
	}

	validateExpression (child.getNextSibling ());
    }

    /**
     * Validate a CLOSURE form.
     *
     * @param tree the tree to validate
     */
    static private void validateClosure (AST tree)
    {
	int yieldCount = 0;
	int inputCount = 0;
	int outputCount = 0;
	HashSet names = new HashSet ();

	AST child = tree.getFirstChild ();
	while (child != null)
	{
	    int type = child.getType ();
	    String name = null;
	    switch (type)
	    {
	        case BIND: 
		{
		    validateBind (child);
		    AST c1 = child.getFirstChild ();
		    if (c1.getType () == IOTA)
		    {
			throw new RuntimeException ("misplaced iota: " + 
						    child);
		    }
		    name = c1.getText ();
		    break;
		}
	        case INPUT:
		{
		    validateInput (child);
		    name = child.getFirstChild ().getText ();
		    inputCount++;
		    break;
		}
	        case OUTPUT:
		{
		    validateOutput (child);
		    name = child.getFirstChild ().getText ();
		    outputCount++;
		    break;
		}
	        case YIELD:
		{
		    validateYield (child);
		    yieldCount++;
		    break;
		}
	        default:
		{
		    throw new RuntimeException ("bad closure statement: " +
						child);
		}
	    }

	    if (name != null)
	    {
		if (names.contains (name))
		{
		    throw new RuntimeException ("duplicate name: " + name);
		}
		names.add (name);
	    }

	    child = child.getNextSibling ();
	}

	if (yieldCount > 1)
	{
	    throw new RuntimeException ("too many yields: " + tree);
	}

	if ((yieldCount == 1) && ((outputCount + inputCount) != 0))
	{
	    throw new RuntimeException ("can't use yield and inputs/outputs " +
					"in the same closure: " + tree);
	}
    }

    /**
     * Validate an IDENTIFIER form.
     *
     * @param tree the tree to validate
     */
    static private void validateIdentifier (AST tree)
    {
	if (PlasticAST.astLength (tree) != 0)
	{
	    throw new RuntimeException ("bad identifier; length != 0");
	}
    }

    /**
     * Validate an INPUT form.
     *
     * @param tree the tree to validate
     */
    static private void validateInput (AST tree)
    {
	if (PlasticAST.astLength (tree) != 2)
	{
	    throw new RuntimeException ("bad input; length != 2");
	}

	AST child = tree.getFirstChild ();
	if (child.getType () != IDENTIFIER)
	{
	    throw new RuntimeException ("bad input; not an identifier: " +
					child.getText ());
	}

	validateExpression (child.getNextSibling ());
    }

    /**
     * Validate an IOTA form.
     *
     * @param tree the tree to validate
     */
    static private void validateIota (AST tree)
    {
	if (PlasticAST.astLength (tree) != 0)
	{
	    throw new RuntimeException ("bad iota; length != 0");
	}
    }

    /**
     * Validate a LITERAL form.
     *
     * @param tree the tree to validate
     */
    static private void validateLiteral (AST tree)
    {
	if (PlasticAST.astLength (tree) != 0)
	{
	    throw new RuntimeException ("bad literal; length != 0");
	}
    }

    /**
     * Validate an OUTPUT form.
     *
     * @param tree the tree to validate
     */
    static private void validateOutput (AST tree)
    {
	if (PlasticAST.astLength (tree) != 2)
	{
	    throw new RuntimeException ("bad output; length != 2");
	}

	AST child = tree.getFirstChild ();
	if (child.getType () != IDENTIFIER)
	{
	    throw new RuntimeException ("bad output; not an identifier: " +
					child.getText ());
	}

	validateExpression (child.getNextSibling ());
    }

    /**
     * Validate a REF form.
     *
     * @param tree the tree to validate
     */
    static private void validateRef (AST tree)
    {
	if (PlasticAST.astLength (tree) != 2)
	{
	    throw new RuntimeException ("bad ref; length != 2");
	}

	AST child = tree.getFirstChild ();
	validateExpression (child);
	child = child.getNextSibling ();

	if (child.getType () != IDENTIFIER)
	{
	    throw new RuntimeException ("bad ref; not an identifier: " +
					child.getText ());
	}
    }

    /**
     * Validate a TOPLEVEL form.
     *
     * @param tree the tree to validate
     */
    static private void validateTopLevel (AST tree)
    {
	HashSet names = new HashSet ();

	AST child = tree.getFirstChild ();
	while (child != null)
	{
	    if (child.getType () != BIND)
	    {
		throw new RuntimeException ("bad top-level statement: " +
					    child);
	    }

	    validateBind (child);

	    AST c1 = child.getFirstChild ();
	    if (c1.getType () == IOTA)
	    {
		throw new RuntimeException ("misplaced iota: " + 
					    child);
	    }

	    String name = c1.getText ();
	    if (names.contains (name))
	    {
		throw new RuntimeException ("duplicate name: " + name);
	    }
	    names.add (name);

	    child = child.getNextSibling ();
	}
    }

    /**
     * Validate a YIELD form.
     *
     * @param tree the tree to validate
     */
    static private void validateYield (AST tree)
    {
	if (PlasticAST.astLength (tree) != 1)
	{
	    throw new RuntimeException ("bad yield; length != 1");
	}

	AST child = tree.getFirstChild ();
	validateExpression (child);
    }

    /**
     * Validate an expression tree. Expressions may be of a number of
     * types.
     *
     * @param tree the tree to validate
     */
    static private void validateExpression (AST tree)
    {
	int type = tree.getType ();
	switch (type)
	{
	    case APPLY:      validateApply (tree);      break;
	    case CLOSURE:    validateClosure (tree);    break;
	    case IDENTIFIER: validateIdentifier (tree); break;
	    case IOTA:       validateIota (tree);       break; 
	    case LITERAL:    validateLiteral (tree);    break; 
	    case REF:        validateRef (tree);        break;
	    default:
	    {
		throw new RuntimeException ("bad expression type: " + type);
	    }	
	}
    }
}

