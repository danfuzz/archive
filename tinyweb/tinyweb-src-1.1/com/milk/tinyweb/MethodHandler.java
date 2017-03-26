// Copyright (c) 2000-2001 Dan Bornstein, danfuzz@milk.com. All rights 
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

package com.milk.tinyweb;

import com.milk.util.EmbeddedException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This is a document handler which delegates to a particular method
 * of a given object, whose signature (other than the name) should
 * be the same as {@link DocumentHandler#handleRequest}.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class MethodHandler
implements DocumentHandler
{
    /** type signature for the <code>handleRequest</code>-like methods */
    static private final Class[] theSignature = 
	new Class[] { String.class, String.class, HttpRequest.class };

    /** return type for the <code>handleRequest</code>-like methods */
    static private final Class theReturnType = Document.class;
    
    /** non-null; the target object to delegate to */
    private Object myTarget;

    /** non-null; the method to call on the target */
    private Method myMethod;



    // ------------------------------------------------------------------------
    // public static methods

    /**
     * Add all the handlers in the given object to the given server. The
     * object is scanned for all methods whose name begins with
     * <code>"handle"</code> and whose signature is appropriate to be a
     * handler. An instance of this class is created for each one, and is
     * added to the server with the name minus the <code>"handle"</code>
     * prefix and with the first character downcased. 
     *
     * @param server non-null; the server to add to
     * @param urlPrefix non-null; the prefix to append to each added
     * path; minimally, must be <code>"/"</code>, and should end with a
     * <code>"/"</code> unless you want to have a common prefix in the
     * last path component
     * @param urlSuffix non-null; the suffix to append to each added path
     * @param target non-null; the target object to scan for handlers
     */
    public static void putAll (TinyWebServer server, String urlPrefix, 
			       String urlSuffix, Object target)
    {
	if (server == null)
	{
	    throw new NullPointerException ("server == null");
	}

	if (urlPrefix == null)
	{
	    throw new NullPointerException ("urlPrefix == null");
	}

	if (urlSuffix == null)
	{
	    throw new NullPointerException ("urlSuffix == null");
	}

	if (target == null)
	{
	    throw new NullPointerException ("target == null");
	}

	Method[] meths = target.getClass ().getMethods ();

	outer:
	for (int i = 0; i < meths.length; i++)
	{
	    String name = meths[i].getName ();

	    if ((name.length () < 7) ||
		! name.startsWith ("handle") ||
		(meths[i].getReturnType () != theReturnType))
	    {
		continue;
	    }

	    Class[] types = meths[i].getParameterTypes ();

	    if (types.length != theSignature.length)
	    {
		continue;
	    }

	    for (int j = 0; j < theSignature.length; j++)
	    {
		if (types[j] != theSignature[j])
		{
		    continue outer;
		}
	    }
	    
	    char c1 = Character.toLowerCase (name.charAt (6));
	    String docName = urlPrefix + c1 + name.substring (7) + urlSuffix;

	    server.putDocument (docName, new MethodHandler (target, name));
	}
    }



    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct an instance.
     *
     * @param target non-null; the target object to delegate to
     * @param methodName non-null; the name of the method to call on
     * the target to respond to {@link DocumentHandler#handleRequest}
     */
    public MethodHandler (Object target, String methodName)
    {
	if (target == null)
	{
	    throw new NullPointerException ("target == null");
	}

	if (methodName == null)
	{
	    throw new NullPointerException ("methodName == null");
	}

	myTarget = target;

	Class cls = target.getClass ();

	try
	{
	    myMethod = cls.getMethod (methodName, theSignature);
	}
	catch (NoSuchMethodException ex)
	{
	    throw new IllegalArgumentException ("named method not found " +
						"with appropriate signature");
	}

	if (myMethod.getReturnType () != theReturnType)
	{
	    throw new IllegalArgumentException ("named method not found " +
						"with appropriate signature");
	}
    }

    /**
     * Construct an instance from a <code>Method</code> that is presumed
     * to have the right signature. This is private since outside sources
     * cannot be trusted to pass in a proper method. It's used by
     * {@link #putAll}.
     * 
     * @param target non-null; the target object to delegate to
     * @param method non-null; the method to call on
     * the target to respond to {@link DocumentHandler#handleRequest}
     */
    private MethodHandler (Object target, Method method)
    {
	if (target == null)
	{
	    throw new NullPointerException ("target == null");
	}

	if (method == null)
	{
	    throw new NullPointerException ("method == null");
	}

	myTarget = target;
	myMethod = method;
    }



    // ------------------------------------------------------------------------
    // public instance methods

    // interface's javadoc suffices
    public Document handleRequest (String query, String partialPath,
				   HttpRequest request)
    {
	try
	{
	    Object result = 
		myMethod.invoke (myTarget, 
				 new Object[] { query, partialPath, request });

	    return (Document) result;
	}
	catch (IllegalAccessException ex)
	{
	    throw new EmbeddedException (ex);
	}
	catch (InvocationTargetException ex)
	{
	    Throwable t = ex.getTargetException ();
	    if (t instanceof RuntimeException)
	    {
		throw (RuntimeException) t;
	    }
	    else
	    {
		throw new EmbeddedException (t);
	    }
	}
    }

    // interface's javadoc suffices
    public void putDocument (String partialPath, DocumentHandler doc)
    {
	throw new RuntimeException ("putDocument() not supported.");
    }
}
