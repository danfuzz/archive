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

package com.milk.stu.builtins;

import com.milk.stu.data.StdEnvironment;
import com.milk.stu.data.MethodCaller;
import com.milk.stu.data.VarArgsMethodCaller;
import com.milk.stu.iface.Environment;
import com.milk.stu.iface.Identifier;
import com.milk.stu.iface.Names;
import com.milk.stu.util.CharSequenceList;
import com.milk.stu.util.VarNames;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Built-in functions, implemented as static methods, to be bound in the
 * default top-level environment.
 *
 * @author Dan Bornstein, danfuzz@milk.com 
 */
final public class Builtins
{
    // ------------------------------------------------------------------------
    // static public methods

    /**
     * Put bindings for all of the built-ins into the given environment.
     *
     * @param env non-null; the environment to set up
     */
    static public void putIn (Environment env)
    {
	if (env == null)
	{
	    throw new NullPointerException ("env == null");
	}

	// Java classes
	putJavaClass (env, "Class");
	putJavaClass (env, "Object");
	putJavaClass (env, "System");

	// other builtin classes
	BooleanBuiltins.putIn (env);
	DoubleBuiltins.putIn (env);
	IdBuiltins.putIn (env);
	IntBuiltins.putIn (env);
        StringBuiltins.putIn (env);

	// needs to come after the other op qualifiers since it grabs some
	// of their definitions from the environment
	DefaultBuiltins.putIn (env);

	// defined in this class
	putVarArgsFunc (env, Names.FN_makeList);
	putVarArgsFunc (env, Names.FN_makeMap);
	putVarArgsFunc (env, Names.FN_simpleMatch);
	putVarArgsFunc (env, Names.FN_strcat);
	putVarArgsFunc (env, Names.FN_quasiTemplate_rx);
	putFunc (env, VarNames.uriName ("import"));
	putFunc (env, VarNames.uriName ("file"));
    }

    /**
     * Make a standard environment containing appropriate bindings for
     * all the built-ins.
     *
     * @return non-null; such an environment
     */
    static public Environment makeStandardEnvironment ()
    {
	Environment env = new StdEnvironment (null, 200);
	putIn (env);
	return env;
    }



    // ------------------------------------------------------------------------
    // standard non-operator functions

    static public Class uri__import (URI uri)
	throws ClassNotFoundException
    {
	if ((! uri.isOpaque ())
	    || (uri.getFragment () != null))
	{
	    throw new RuntimeException ("invalid URI for import: " + uri);
	}
	
	String className = uri.getSchemeSpecificPart ();

	return Class.forName (className);
    }

    static public File uri__file (URI uri)
	throws URISyntaxException
    {
	boolean relative = false;

	if (uri.isOpaque ())
	{
	    // we do this so that the ssp gets parsed as a path
	    String ssp = uri.getRawSchemeSpecificPart ();
	    uri = new URI (uri.getScheme (),
			   "/" + uri.getSchemeSpecificPart (),
			   uri.getFragment ());
	    relative = true;
	}

	if ((uri.getFragment () != null)
	    || (uri.getAuthority () != null)
	    || (uri.getQuery () != null))
	{
	    throw new RuntimeException ("invalid URI for file: " + uri);
	}
	
	String path = uri.getPath ();

	if (relative)
	{
	    path = path.substring (1);
	}

	return new File (path);
    }

    static public ArrayList makeList (Object[] args)
    {
	ArrayList result = new ArrayList (args.length);

	for (int i = 0; i < args.length; i++)
	{
	    result.add (args[i]);
	}

	return result;
    }

    static public HashMap makeMap (Object[] args)
    {
	HashMap result = new HashMap (args.length);

	for (int i = 0; i < args.length; i += 2)
	{
	    result.put (args[i], args[i+1]);
	}

	return result;
    }

    static public int simpleMatch (Object[] args)
    {
	List assigns = (List) args[0];
	String value = args[1].toString ();
	int holesMatched = 0;

	for (int i = 2; i < args.length; i++)
	{
	    Object one = args[i];
	    if (one instanceof String)
	    {
		String oneStr = (String) one;
		if (! value.startsWith (oneStr))
		{
		    break;
		}
		value = value.substring (oneStr.length ());
	    }
	    else
	    {
		String matched = null;
		if ((i == (args.length - 1)) ||
		    (value.length () == 0))
		{
		    matched = value;
		    value = "";
		}
		else if (! (args[i+1] instanceof String))
		{
		    // match a single character if immediately followed
		    // by another hole (and not at the end of the value)
		    matched = value.substring (0, 1);
		    value = value.substring (1);
		}
		else
		{
		    String lookFor = (String) args[i+1];
		    int foundAt = value.indexOf (lookFor);
		    if (foundAt == -1)
		    {
			matched = value;
			value = "";
		    }
		    else
		    {
			matched = value.substring (0, foundAt);
			value = value.substring (foundAt);
		    }
		}
		assigns.set (holesMatched, matched);
		holesMatched++;
	    }
	}

	return holesMatched;
    }

    static public CharSequence strcat (Object[] args)
    {
	int len = args.length;
	CharSequenceList result = new CharSequenceList (len);

	for (int i = 0; i < len; i++)
	{
	    result.append (args[i]);
	}

	result.makeImmutable ();

	return result;
    }

    static public Pattern quasiTemplate__rx (Object[] args)
    {
	String pat = strcat (args).toString ();
	return Pattern.compile (pat);
    }



    // ------------------------------------------------------------------------
    // static private methods

    static private void putVarArgsFunc (Environment env, Identifier name)
    {
	Object value = 
	    VarArgsMethodCaller.makeStatic (Builtins.class, name.getName ());
	env.defineAlways (name, value);
    }

    static private void putFunc (Environment env, Identifier name)
    {
	Object value = 
	    MethodCaller.makeStatic (Builtins.class, name.getName ());
	env.defineAlways (name, value);
    }

    static private void putJavaClass (Environment env, String name)
    {
	Identifier id = Identifier.intern (name);

	try
	{
	    Class clazz = Class.forName ("java.lang." + name);
	    env.defineAlways (id, clazz);
	}
	catch (ClassNotFoundException ex)
	{
	    throw new RuntimeException (ex);
	}
    }
}
