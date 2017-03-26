// Copyright (c) 2002 Dan Bornstein, danfuzz@milk.com. All rights 
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

package com.milk.stu.parser;

import antlr.Token;
import antlr.collections.AST;
import com.milk.stu.iface.Identifier;
import com.milk.stu.iface.Names;
import com.milk.stu.iface.StuNode;
import com.milk.stu.node.AndNode;
import com.milk.stu.node.ApplyNode;
import com.milk.stu.node.ApplyMethodNode;
import com.milk.stu.node.AssignMatchNode;
import com.milk.stu.node.AssignNode;
import com.milk.stu.node.DefineNode;
import com.milk.stu.node.FunctionNode;
import com.milk.stu.node.IfNode;
import com.milk.stu.node.LiteralNode;
import com.milk.stu.node.LoopNode;
import com.milk.stu.node.OrNode;
import com.milk.stu.node.RefNode;
import com.milk.stu.node.SequenceNode;
import com.milk.stu.node.TemplateNode;
import com.milk.stu.node.VarNode;
import com.milk.stu.util.VarNames;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * This class simply has static methods to translate ANTLR ASTs into
 * nodes that are native to this system.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public class StuMaker
    implements StuTokenTypes
{
    /** non-null; the variable node which names the list-making function */
    static private final VarNode VAR_makeList = 
	new VarNode (Names.FN_makeList);

    /** non-null; the variable node which names the map-making function */
    static private final VarNode VAR_makeMap = new VarNode (Names.FN_makeMap);

    /** non-null; the variable node which names the simple pattern matching
     * function */
    static private final VarNode VAR_simpleMatch = 
	new VarNode (Names.FN_simpleMatch);

    /** non-null; the variable node for the return function */
    static private final VarNode VAR_return = new VarNode (Names.VAR_return);

    /** non-null; the variable node which names the function used for array
     * get */
    static private final VarNode OP_GET_VAR = 
	new VarNode (VarNames.operatorFunctionName (Names.OP_GET, null));

    /** non-null; for tokens that represent functions, their token number
     * indexed into this table returns the name of the function */
    static private String[] theOpFuncs = new String[85];

    static
    {
	theOpFuncs[ADD]       = Names.OP_ADD;
	theOpFuncs[AND]       = Names.OP_AND;
	theOpFuncs[DIV]       = Names.OP_DIV;
	theOpFuncs[EQ]        = Names.OP_EQ;
	theOpFuncs[GE]        = Names.OP_GE;
	theOpFuncs[GET]       = Names.OP_GET;
	theOpFuncs[GT]        = Names.OP_GT;
	theOpFuncs[INVERT]    = Names.OP_INVERT;
	theOpFuncs[LE]        = Names.OP_LE;
	theOpFuncs[LSHIFT]    = Names.OP_LSHIFT;
	theOpFuncs[LT]        = Names.OP_LT;
	theOpFuncs[MOD]       = Names.OP_MOD;
	theOpFuncs[MUL]       = Names.OP_MUL;
	theOpFuncs[NE]        = Names.OP_NE;
	theOpFuncs[NOT]       = Names.OP_NOT;
	theOpFuncs[OR]        = Names.OP_OR;
	theOpFuncs[POW]       = Names.OP_POW;
	theOpFuncs[REMAINDER] = Names.OP_REMAINDER;
	theOpFuncs[RSHIFT]    = Names.OP_RSHIFT;
	theOpFuncs[SUB]       = Names.OP_SUB;
	theOpFuncs[XOR]       = Names.OP_XOR;
    }



    // ------------------------------------------------------------------------
    // constructor

    /**
     * This class is uninstantiable.
     */
    private StuMaker ()
    {
	// this space intentionally left blank
    }



    // ------------------------------------------------------------------------
    // static public methods

    /**
     * Make a {@link SequenceNode} from an appropriate AST. This will throw
     * if the given AST turns out to be bad.
     *
     * @param orig non-null; the AST to process
     * @return non-null; the sequence node
     */
    static public SequenceNode makeSequence (AST orig)
    {
	SequenceNode result = (SequenceNode) make (orig);

	if (result == null)
	{
	    throw new NullPointerException ("null result");
	}

	return result;
    }

    /**
     * Make a node from the given AST.
     *
     * @param orig null-ok; the AST to process
     * @return null-ok; the node form
     */
    static public StuNode make (AST orig)
    {
	return make (orig, null);
    }



    // ------------------------------------------------------------------------
    // static private methods

    /**
     * Make a node from the given AST using the given operator
     * qualification.
     *
     * @param orig null-ok; the AST to process
     * @param qual null-ok; the operator qualification
     * @return null-ok; the node form
     */
    static public StuNode make (AST orig, String qual)
    {
	if (orig == null)
	{
	    return null;
	}

	AST child1 = orig.getFirstChild ();
	AST child2 = (child1 == null) ? null : child1.getNextSibling ();
	AST child3 = (child2 == null) ? null : child2.getNextSibling ();

	int type = orig.getType ();
	if (theOpFuncs[type] != null)
	{
	    String fname;
	    if ((child2 == null) && (type == SUB))
	    {
		// special case
		fname = Names.OP_NEG;
	    }
	    else if ((child2 == null) && (type == ADD))
	    {
		// the other special case
		fname = Names.OP_IDENTITY;
	    }
	    else
	    {
		fname = theOpFuncs[type];
	    }

	    Identifier id = VarNames.operatorFunctionName (fname, qual);
	    return new ApplyNode (new VarNode (id),
				  make (child1, qual), 
				  make (child2, qual));
	}

	switch (type)
	{
	    case APPLY_FUNC:
	    {
		StuNode func = make (child1, qual);
		StuNode[] args = makeList (child2);
		return new ApplyNode (func, args);
	    }
	    case APPLY_METH:
	    {
		StuNode target = make (child1, qual);
		String name = child2.getText ();
		StuNode[] args = makeList (child3);
		return new ApplyMethodNode (target, name, args);
	    }
	    case AREF:
	    {
		StuNode[] args = makeList (child1);
		return new ApplyNode (OP_GET_VAR, args);
	    }
	    case ASSIGN:
	    {
		StuNode loc = make (child1, qual);
		StuNode value = make (child2, qual);
		return new AssignNode (loc, value);
	    }
	    case ASSIGN_MATCH:
	    {
		// a little hacky, but it works: the left hand side is
		// parsed as a TemplateNode, and the bits are extracted out
		// of it and adjusted to become an AssignMatchNode
		TemplateNode loc = (TemplateNode) make (child1, qual);
		StuNode value = make (child2, qual);
		VarNode func;
		if (child1.getType () == ID_QUASI)
		{
		    String fname = 
			((VarNode) loc.getFunction ()).getName ().getName ();
		    fname = fname.substring (Names.TEMPLATE_PREFIX.length ());
		    fname = Names.PATTERN_PREFIX + fname;
		    func = new VarNode (fname);
		}
		else
		{
		    func = VAR_simpleMatch;
		}
		
		return new AssignMatchNode (func, loc.getArguments (), value);
	    }
	    case BLOCK_EXPR:
	    {
		StuNode[] stats = makeList (child1);
		return new SequenceNode (stats);
	    }
	    case BOOL_AND:
	    {
		StuNode lhs = make (child1, qual);
		StuNode rhs = make (child2, qual);
		return new AndNode (lhs, rhs);
	    }
	    case BOOL_OR:
	    {
		StuNode lhs = make (child1, qual);
		StuNode rhs = make (child2, qual);
		return new OrNode (lhs, rhs);
	    }
  	    case BREAK:
	    {
		// one of break or break ident or break (expr) or break
		// ident (expr)
		Identifier name = Names.VAR_break;
		StuNode expr = null;
		if (child3 != null)
		{
		    // break ident (expr)
		    name = VarNames.breakName (child1.getText ());
		    expr = make (child2, qual);
		}
		else if (child2 != null)
		{
		    // break (expr)
		    expr = make (child1, qual);
		}
		else if (child1 != null)
		{
		    // break name
		    name = VarNames.breakName (child1.getText ());
		}

		return new ApplyNode (new VarNode (name), expr);
	    }
  	    case CONTINUE:
	    {
		// either continue or continue ident
		Identifier name = Names.VAR_continue;
		if (child1 != null)
		{
		    name = VarNames.continueName (child1.getText ());
		}
		return new ApplyNode (new VarNode (name));
	    }
	    case DEF:
	    {
		VarNode var = (VarNode) make (child1, qual);
		StuNode value = make (child2, qual);
		return new DefineNode (var.getName (), value);
	    }
	    case DOT:
	    {
		StuNode obj = make (child1, qual);
		String name = child2.getText ();
		return new RefNode (obj, name);
	    }
	    case FALSE:
	    {
		return LiteralNode.FALSE;
	    }
	    case FN:
	    {
		// either fn(name, params, statements)
		// or fn(params, statements)
		String selfName = null;
		String[] paramNames;
		StuNode statements;
		if (child3 != null)
		{
		    selfName = 
			((VarNode) make (child1, qual)).getName ().getName ();
		    paramNames = makeParamNames (child2);
		    statements = make (child3, null);
		}
		else
		{
		    paramNames = makeParamNames (child1);
		    statements = make (child2, null);
		}
		checkFunctionDeclaration (selfName, paramNames);
		return new FunctionNode (selfName, paramNames, statements);
	    }
	    case FNAME:
	    {
		int type1 = child1.getType ();
		String fname = theOpFuncs[type1];
		Identifier id = null;
		if (fname != null)
		{
		    id = VarNames.operatorFunctionName (fname, null);
		}
		else
		{
		    String text = child1.getText ();
		    switch (type1)
		    {
			case IDENTIFIER:
			    if (text.equals ("default"))
			    {
				text = null;
			    }
			    int type2 = child2.getType ();
			    fname = theOpFuncs[type2];
			    id = VarNames.operatorFunctionName (fname, text);
			    break;
			case LITERAL_URI: 
			    id = VarNames.tagName (text);
			    break;
			case XML_ENTITY: 
			    id = VarNames.entityName (text);
			    break;
			case LITERAL_STRING:
			    id = Identifier.intern (text);
			    break;
		    }
		}
		if (id == null)
		{
		    throw new RuntimeException ("unknown ast type for " +
						"fname: " + type);
		}
		return new VarNode (id);
	    }
	    case IDENTIFIER:
	    {
		String name = orig.getText ();
		return new VarNode (name);
	    }
	    case ID_QUASI:
	    {
		String id = child1.getText ();
		String pattern = child2.getText ();
		VarNode func = new VarNode (Names.TEMPLATE_PREFIX + id);
		TemplateNode result = Parser.parseTemplate (pattern);
		return result.withFunction (func);
	    }
  	    case IF:
	    {
		// either if(test, thenPart, elsePart)
		// or if(test, thenPart)
		StuNode test = make (child1, null);
		StuNode thenPart = make (child2, null);
		if (child3 != null)
		{
		    StuNode elsePart = make (child3);
		    return new IfNode (test, thenPart, elsePart);
		}
		else
		{
		    return new IfNode (test, thenPart);
		}
	    }
	    case INFINITY:
	    {
		return LiteralNode.INFINITY;
	    }
	    case LITERAL_FLOAT:
	    {
		String value = orig.getText ();
		return LiteralNode.floatLit (value);
	    }
	    case LITERAL_INTEGER:
	    {
		String value = orig.getText ();
		return LiteralNode.intLit (value);
	    }
	    case LITERAL_STRING:
	    {
		String value = orig.getText ();
		return LiteralNode.stringLit (value);
	    }
	    case LITERAL_URI:
	    {
		String text = orig.getText ();
		int colonAt = text.indexOf (':');
		if (colonAt == -1)
		{
		    throw new RuntimeException ("malformed uri: " + text);
		}
		URI uri = URI.create (text);
		LiteralNode uriLit = LiteralNode.uriLit (uri);
		VarNode func = 
		    new VarNode (VarNames.uriName (uri.getScheme ()));
		return new ApplyNode (func, uriLit);
	    }
	    case LOOP:
	    {
		// either loop(name, statements)
		// or loop(statements)
		String selfName = null;
		StuNode statements;
		if (child2 != null)
		{
		    selfName = child1.getText ();
		    statements = make (child2, null);
		}
		else
		{
		    statements = make (child1, null);
		}
		return new LoopNode (selfName, statements);
	    }
	    case MAKE_LIST:
	    {
		StuNode[] args = makeList (child1);
		return new ApplyNode (VAR_makeList, args);
	    }
	    case MAKE_MAP:
	    {
		StuNode[] args = makeMap (child1);
		return new ApplyNode (VAR_makeMap, args);
	    }
	    case NAN:
	    {
		return LiteralNode.NAN;
	    }
	    case NULL:
	    {
		return LiteralNode.NULL;
	    }
	    case QUALIFIED_EXPR:
	    {
		String subQual = child1.getText ();
		if (subQual.equals ("default"))
		{
		    subQual = null;
		}

		return make (child2, subQual);
	    }
	    case QUASILITERAL_STRING:
	    {
		return Parser.parseTemplate (orig.getText ());
	    }
  	    case RETURN:
	    {
		// return or return (expr)
		StuNode value = make (child1, qual);
		return new ApplyNode (VAR_return, value);
	    }
	    case TRUE:
	    {
		return LiteralNode.TRUE;
	    }
	    default:
	    {
		throw new RuntimeException ("unknown ast type: " + type);
	    }
	}
    }

    /**
     * Make an array of nodes from a list-like AST.
     *
     * @param orig null-ok; the original AST
     * @return non-null; array of nodes that the original represented
     */
    static private StuNode[] makeList (AST orig)
    {
	ArrayList result = new ArrayList ();
	
	while (orig != null)
	{
	    result.add (make (orig, null));
	    orig = orig.getNextSibling ();
	}

	return (StuNode[]) result.toArray (new StuNode[result.size ()]);
    }

    /**
     * Make an array of nodes from a map-like AST.
     *
     * @param orig null-ok; the original AST
     * @return non-null; array of nodes that the original represented
     */
    static private StuNode[] makeMap (AST orig)
    {
	ArrayList result = new ArrayList ();
	
	while (orig != null)
	{
	    AST child = orig.getFirstChild ();
	    result.add (make (child, null));
	    child = child.getNextSibling ();
	    result.add (make (child, null));
	    orig = orig.getNextSibling ();
	}

	return (StuNode[]) result.toArray (new StuNode[result.size ()]);
    }

    /**
     * Make an array of strings from a PARAM_LIST node.
     *
     * @param orig non-null; the original AST
     * @return non-null; array of names mentioned in the original
     */
    static private String[] makeParamNames (AST orig)
    {
	ArrayList result = new ArrayList ();

	orig = orig.getFirstChild ();
	while (orig != null)
	{
	    result.add (orig.getText ());
	    orig = orig.getNextSibling ();
	}

	return (String[]) result.toArray (new String[result.size ()]);
    }

    /**
     * Check to make sure that there are no duplicate names in a 
     * function declaration. This will throw if there's a problem.
     *
     * @param funcName null-ok; the name of the function, if any
     * @param paramNames non-null; the names of the parameters
     */
    static private void checkFunctionDeclaration (String funcName, 
						  String[] paramNames)
    {
	HashSet s = new HashSet ();
	int expectedSize = paramNames.length;

	if (funcName != null)
	{
	    s.add (funcName);
	    expectedSize++;
	}

	for (int i = 0; i < paramNames.length; i++)
	{
	    s.add (paramNames[i]);
	}

	if (s.size () != expectedSize)
	{
	    throw new RuntimeException ("duplicate name in function " +
					"declaration");
	}
    }
}
