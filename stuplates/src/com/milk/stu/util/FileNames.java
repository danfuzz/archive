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

import com.milk.stu.iface.Names;

/**
 * Utility methods for dealing with standard file names/extensions.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class FileNames
{
    // ------------------------------------------------------------------------
    // constructor

    /**
     * This class is uninstantiable.
     */
    private FileNames ()
    {
	// this space intentionally left blank
    }



    // ------------------------------------------------------------------------
    // static public methods

    /**
     * Return the output file name associated with the given template file
     * name, but only if the name is in fact a tempate file name. Otherwise,
     * return <code>null</code>
     *
     * @param name non-null; the file name in question
     * @return null-ok; the associated output file name if <code>name</code>
     * names a template file, or <code>null</code>
     */
    static public String stutOutputName (String name)
    {
	if (! name.endsWith (Names.STUT_EXTENSION))
	{
	    return null;
	}

	int len = name.length () - Names.STUT_EXTENSION.length ();
	return name.substring (0, len);
    }

    /**
     * Return the output file name associated with the given script file
     * name, but only if the name is in fact a script file name. Otherwise,
     * return <code>null</code>
     *
     * @param name non-null; the file name in question
     * @return null-ok; the associated output file name if <code>name</code>
     * names a script file, or <code>null</code>
     */
    static public String stuOutputName (String name)
    {
	if (! name.endsWith (Names.STU_EXTENSION))
	{
	    return null;
	}

	int len = name.length () - Names.STU_EXTENSION.length ();
	return name.substring (0, len);
    }
}
