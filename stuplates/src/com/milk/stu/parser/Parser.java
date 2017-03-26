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

import antlr.collections.AST;
import antlr.ANTLRException;
import com.milk.stu.node.SequenceNode;
import com.milk.stu.node.TemplateNode;
import com.milk.stu.util.FileUtils;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

/**
 * Convenient static methods to parse various sorts of files and fragments.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class Parser
{
    // ------------------------------------------------------------------------
    // constructor

    /**
     * This class is uninstantiable.
     */
    private Parser ()
    {
	// this space intentionally left blank
    }



    // ------------------------------------------------------------------------
    // static public methods

    /**
     * Parse a script from the given stream. The return value is a {@link
     * SequenceNode} representing all of the statements in the stream.
     *
     * @param stream non-null; the stream to parse
     * @return non-null; the parsed script
     */
    static public SequenceNode parseScript (InputStream stream)
    {
	SequenceNode node;

	StuLexer lex = new StuLexer (stream);
	StuParser parse = new StuParser (lex);

	try
	{
	    parse.parseFile ();
	}
	catch (ANTLRException ex)
	{
	    throw new RuntimeException ("trouble parsing", ex);
	}
	    
	AST ast = parse.getAST ();
	return StuMaker.makeSequence (ast);
    }

    /**
     * Parse a script from the given reader. The return value is a {@link
     * SequenceNode} representing all of the statements in the stream.
     *
     * @param reader non-null; the reader to parse
     * @return non-null; the parsed script
     */
    static public SequenceNode parseScript (Reader reader)
    {
	SequenceNode node;

	StuLexer lex = new StuLexer (reader);
	StuParser parse = new StuParser (lex);
	    
	try
	{
	    parse.parseFile ();
	}
	catch (ANTLRException ex)
	{
	    throw new RuntimeException ("trouble parsing", ex);
	}

	AST ast = parse.getAST ();
	return StuMaker.makeSequence (ast);
    }

    /**
     * Parse the given script file. The return value is a {@link
     * SequenceNode} representing all of the statements in the file.
     *
     * @param file non-null; the file to parse
     * @return non-null; the parsed script
     */
    static public SequenceNode parseScript (File file)
    {
	InputStream stream = FileUtils.openInput (file);
	SequenceNode result = parseScript (stream);
	FileUtils.closeInput (stream);
	return result;
    }

    /**
     * Parse the given script source. The return value is a {@link
     * SequenceNode} representing all of the statements in the source string.
     *
     * @param source non-null; the source to parse
     * @return non-null; the parsed script
     */
    static public SequenceNode parseScript (String source)
    {
	StringReader stream = new StringReader (source);
	return parseScript (stream);
    }

    /**
     * Parse a template from the given stream. The return value is a {@link
     * TemplateNode} representing the template contents.
     *
     * @param stream non-null; the stream to parse
     * @return non-null; the parsed template
     */
    static public TemplateNode parseTemplate (InputStream stream)
    {
	StutLexer lex = new StutLexer (stream);
	
	try
	{
	    return StutParser.parse (lex);
	}
	catch (ANTLRException ex)
	{
	    throw new RuntimeException ("trouble parsing", ex);
	}
    }

    /**
     * Parse a template from the given reader. The return value is a {@link
     * TemplateNode} representing the template contents.
     *
     * @param stream non-null; the reader to parse
     * @return non-null; the parsed template
     */
    static public TemplateNode parseTemplate (Reader reader)
    {
	StutLexer lex = new StutLexer (reader);
	
	try
	{
	    return StutParser.parse (lex);
	}
	catch (ANTLRException ex)
	{
	    throw new RuntimeException ("trouble parsing", ex);
	}
    }

    /**
     * Parse the given template file. The return value is a {@link
     * TemplateNode} representing the template contents.
     *
     * @param file non-null; the file to parse
     * @return non-null; the parsed template
     */
    static public TemplateNode parseTemplate (File file)
    {
	InputStream stream = FileUtils.openInput (file);
	TemplateNode result = parseTemplate (stream);
	FileUtils.closeInput (stream);
	return result;
    }

    /**
     * Parse the given template source. The return value is a {@link
     * TemplateNode} representing the template contents.
     *
     * @param source non-null; the source to parse
     * @return non-null; the parsed template
     */
    static public TemplateNode parseTemplate (String source)
    {
	StringReader stream = new StringReader (source);
	return parseTemplate (stream);
    }
}
