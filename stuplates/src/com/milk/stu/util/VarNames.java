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

package com.milk.stu.util;

import com.milk.stu.iface.Identifier;
import com.milk.stu.iface.Names;

/**
 * Utility methods for dealing with standard variable names / prefixes.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class VarNames
{
    // ------------------------------------------------------------------------
    // constructor

    /**
     * This class is uninstantiable.
     */
    private VarNames ()
    {
	// this space intentionally left blank
    }



    // ------------------------------------------------------------------------
    // static public methods

    /**
     * Get the function name corresponding to the given operator with the
     * given operator qualifier. If the qualifier is <code>null</code>, then
     * get the name with the default qualification.
     *
     * @param name non-null; the operator name
     * @param qualifier null-ok; the operator qualifier
     * @return non-null; the corresponding function name
     */
    static public Identifier operatorFunctionName (String name, 
						   String qualifier)
    {
	if (name == null)
	{
	    throw new NullPointerException ("name == null");
	}

	if (qualifier == null)
	{
	    return Identifier.intern (Names.OP_PREFIX + name);
	}

	return Identifier.intern (Names.OP_PREFIX + qualifier + '_' + name);
    }

    /**
     * Get the name corresponding to the named break for the given loop
     * name.
     *
     * @param name non-null; the name of the loop
     * @return non-null; the corresponding named break name
     */
    static public Identifier breakName (String name)
    {
	if (name == null)
	{
	    throw new NullPointerException ("name == null");
	}

	return Identifier.intern (Names.BREAK_PREFIX + name);
    }

    /**
     * Get the name corresponding to the named continue for the given loop
     * name. The result is always an interned string.
     *
     * @param name non-null; the name of the loop
     * @return non-null; the corresponding named continue name
     */
    static public Identifier continueName (String name)
    {
	if (name == null)
	{
	    throw new NullPointerException ("name == null");
	}

	return Identifier.intern (Names.CONTINUE_PREFIX + name);
    }

    /**
     * Get the name corresponding to the named return for the given function
     * name.
     *
     * @param name non-null; the name of the function
     * @return non-null; the corresponding named return name
     */
    static public Identifier returnName (String name)
    {
	return breakName (name);
    }

    /**
     * Get the name for the URI resolution function for the given scheme.
     *
     * @param name non-null; the URI scheme
     * @return non-null; the corresponding URI resolution function name
     */
    static public Identifier uriName (String name)
    {
	return Identifier.intern (Names.URI_PREFIX + name);
    }

    /**
     * Get the name for the XML tag expansion function for the given tag.
     *
     * @param name non-null; the tag name
     * @return non-null; the corresponding function name
     */
    static public Identifier tagName (String name)
    {
	return Identifier.intern (Names.TAG_PREFIX + name);
    }

    /**
     * Get the name for the XML entity expansion function for the given tag.
     *
     * @param name non-null; the entity name
     * @return non-null; the corresponding function name
     */
    static public Identifier entityName (String name)
    {
	return Identifier.intern (Names.ENTITY_PREFIX + name);
    }
}
