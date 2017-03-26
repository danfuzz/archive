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
import antlr.TokenStreamException;
import com.milk.stu.iface.Names;
import com.milk.stu.iface.StuNode;
import com.milk.stu.node.LiteralNode;
import com.milk.stu.node.TemplateNode;
import com.milk.stu.node.VarNode;
import java.util.ArrayList;

/**
 * Parser for template files. This class just has a static method
 * that takes a {@link StutLexer} and returns the template file parsed
 * from it.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class StutParser
{
    /** non-null; the variable node which names the string concatenation
     * function */
    static public final VarNode VAR_strcat = new VarNode (Names.FN_strcat);



    // ------------------------------------------------------------------------
    // constructor

    /**
     * This class is uninstantiable.
     */
    private StutParser ()
    {
	// this space intentionally left blank
    }



    // ------------------------------------------------------------------------
    // static public methods

    /**
     * Parse the template from the given lexer. The result is set to
     * apply the default template function, namely <code>strcat</code>,
     * but that may be altered by using {@link TemplateNode#withFunction}.
     *
     * @param lexer non-null; lexer to parse from
     * @return non-null; the parsed form
     */
    static public TemplateNode parse (StutLexer lexer)
	throws TokenStreamException
    {
	ArrayList components = new ArrayList ();
	StringBuffer literalText = new StringBuffer ();

	boolean done = false;
	while (! done)
	{
	    Token t = lexer.nextToken ();

	    int ttype = t.getType ();

	    switch (ttype)
	    {
		case StutLexer.EOF:
		{
		    done = true;
		    break;
		}
		case StutLexer.LITERAL_TEXT:
		{
		    literalText.append (t.getText ());
		    break;
		}
		case StutLexer.TEMPLATE_BLOCK:
		{
		    if (literalText.length () != 0)
		    {
			String str = literalText.toString ();
			components.add (LiteralNode.stringLit (str));
			literalText.setLength (0);
		    }
			
		    String text = t.getText ();
		    components.add (Parser.parseScript (text));
		    break;
		}
		default:
		{
		    throw new RuntimeException ("shouldn't happen: " +
						"unknown stut type: " + 
						ttype);
		}
	    }
	}

	// add the trailing bit of literal text, if any
	if (literalText.length () != 0)
	{
	    String str = literalText.toString ();
	    components.add (LiteralNode.stringLit (str));
	}

	int sz = components.size ();
	StuNode[] carr = (StuNode[]) components.toArray (new StuNode[sz]);
	return new TemplateNode (VAR_strcat, carr);
    }
}
