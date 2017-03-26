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

package com.milk.stu.data;

import com.milk.stu.iface.Identifier;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Bundle of a class object with other useful metadata.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class ClassBundle
{
    /** non-null; map from classes to instances of this class */
    static private final HashMap theBundleMap = new HashMap ();

    /** non-null; array of bundles corresponding to instance methods on
     * the class <code>java.lang.Class</code> */
    static private final MethodBundle[] theClassInstanceMeths;

    /** non-null; the class in question */
    private Class myClass;

    /** non-null; mapping from names to arrays of method bundles */
    private HashMap myMethods;

    /** non-null; mapping from names to arrays of class method bundles 
     * (including static methods on the class and instance methods on
     * <code>java.lang.Class</code>) */
    private HashMap myClassMethods;

    /** version number of this instance; incremented whenever a method is
     * added */
    private int myVersion;

    /** null-ok; parent bundle, if any; this is only <code>null</code> for
     * the bundle associated with <code>java.lang.Object</code> */
    private ClassBundle myParent;

    /** last known version number of the parent; used to trigger updates */
    private int myParentVersion;

    static
    {
	// set up theClassInstanceMeths
	Method[] meths = Class.class.getMethods ();
	theClassInstanceMeths = new MethodBundle[meths.length];

	for (int i = 0; i < meths.length; i++)
	{
	    Method one = meths[i];
	    Identifier name = Identifier.intern (one.getName ());
	    MethodBundle bundle = new DirectMethodBundle (one);

	    theClassInstanceMeths[i] = bundle;
	}
    }



    // ------------------------------------------------------------------------
    // static public methods

    /**
     * Get the bundle associated with the given class.
     *
     * @param clazz non-null; the class to look up
     * @return non-null; the corresponding instance
     */
    static public ClassBundle get (Class clazz)
    {
	if (clazz == null)
	{
	    throw new NullPointerException ("clazz == null");
	}

	ClassBundle cb = (ClassBundle) theBundleMap.get (clazz);

	if (cb == null)
	{
	    cb = new ClassBundle (clazz);
	    theBundleMap.put (clazz, cb);
	}

	return cb;
    }


    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance. This is private; use {@link #get}.
     *
     * @param clazz non-null; the class in question
     */
    private ClassBundle (Class clazz)
    {
	if (clazz == null)
	{
	    throw new NullPointerException ("clazz == null");
	}

	myClass = clazz;
	myMethods = new HashMap ();
	myClassMethods = new HashMap ();
	myVersion = 0;

	if (clazz == Object.class)
	{
	    myParent = null;
	    myParentVersion = 0;

	    // add all the instance methods on Class to the static method
	    // map; this will get inherited by all subclasses in the else
	    // clause immediately below
	    for (int i = 0; i < theClassInstanceMeths.length; i++)
	    {
		MethodBundle one = theClassInstanceMeths[i];
		addToMap (one, one.getName (), myClassMethods);
	    }
	}
	else
	{
	    myParent = get (clazz.getSuperclass ());
	    myParentVersion = -1; // force update
	    update ();
	}

	// add all the static and instance methods directly declared on the
	// class to the maps
	Method[] meths = clazz.getDeclaredMethods ();
	for (int i = 0; i < meths.length; i++)
	{
	    Method one = meths[i];
	    int mods = one.getModifiers ();
	    if (! Modifier.isPublic (mods))
	    {
		// not public
		continue;
	    }

	    Identifier name = Identifier.intern (one.getName ());
	    MethodBundle bundle = new DirectMethodBundle (one);

	    addToMap (bundle, name, myMethods);

	    if (Modifier.isStatic (mods))
	    {
		addToMap (bundle, name, myClassMethods);
	    }
	}

	// sort all the arrays
	sortBundles (myMethods);
	sortBundles (myClassMethods);
    }



    // ------------------------------------------------------------------------
    // public instance methods

    /**
     * Get the version number of this instance. This number is incremented
     * whenever a method is added to this instance (either explicitly, or
     * implicitly when its parent changes).
     *
     * @return the version number
     */
    public int getVersion ()
    {
	update ();
	return myVersion;
    }

    /**
     * Get the array of method bundles for the static and instance methods
     * with the given name. This will throw an exception if there are no
     * applicable methods.
     *
     * @param name non-null; the method name
     * @return the corresponding array of method bundles
     */
    public MethodBundle[] getMethods (Identifier name)
    {
	if (name == null)
	{
	    throw new NullPointerException ("name == null");
	}

	update ();

	MethodBundle[] result = (MethodBundle[]) myMethods.get (name);

	if (result != null)
	{
	    return result;
	}

	throw new IllegalArgumentException ("no (static or instance) " +
					    "method named: " + name);
    }

    /**
     * Get the array of method bundles for the class methods with the given
     * name. This consists of both the static methods and the instance
     * methods of the class <code>java.lang.Class</code> with the given
     * name. This will throw an exception if there are no applicable
     * methods.
     *
     * @param name non-null; the method name
     * @return the corresponding array of method bundles
     */
    public MethodBundle[] getClassMethods (Identifier name)
    {
	if (name == null)
	{
	    throw new NullPointerException ("name == null");
	}

	update ();

	MethodBundle[] result = (MethodBundle[]) myClassMethods.get (name);

	if (result != null)
	{
	    return result;
	}

	throw new IllegalArgumentException ("no static method named: " + name);
    }



    // ------------------------------------------------------------------------
    // private instance methods

    /**
     * Update the version number and integrate new parent methods, if
     * necessary.
     */
    private void update ()
    {
	if (myParent != null)
	{
	    int pv = myParent.getVersion ();
	    if (myParentVersion != pv)
	    {
		myParent.addAllMethodsTo (myMethods);
		myParent.addAllClassMethodsTo (myClassMethods);
		sortBundles (myMethods);
		sortBundles (myClassMethods);
		myVersion++;
		myParentVersion = pv;
	    }
	}
    }

    /**
     * Add all the methods of this instance to the given map.
     *
     * @param map non-null; the map to add to
     */
    private void addAllMethodsTo (HashMap map)
    {
	Iterator i = myMethods.entrySet ().iterator ();
	while (i.hasNext ())
	{
	    Map.Entry one = (Map.Entry) i.next ();
	    Identifier name = (Identifier) one.getKey ();
	    MethodBundle[] mbs = (MethodBundle[]) one.getValue ();
	    for (int j = 0; j < mbs.length; j++)
	    {
		addToMap (mbs[j], name, map);
	    }
	}
    }

    /**
     * Add all the class methods of this instance to the given map.
     *
     * @param map non-null; the map to add to
     */
    private void addAllClassMethodsTo (HashMap map)
    {
	Iterator i = myClassMethods.entrySet ().iterator ();
	while (i.hasNext ())
	{
	    Map.Entry one = (Map.Entry) i.next ();
	    Identifier name = (Identifier) one.getKey ();
	    MethodBundle[] mbs = (MethodBundle[]) one.getValue ();
	    for (int j = 0; j < mbs.length; j++)
	    {
		addToMap (mbs[j], name, map);
	    }
	}
    }



    // ------------------------------------------------------------------------
    // static private methods

    /**
     * Add the given bundle with the given name to the given map.
     *
     * @param bundle non-null; the bundle to add
     * @param name non-null; the name to add to
     * @param map non-null; the map to add to
     */
    static private void addToMap (MethodBundle bundle, Identifier name,
				  HashMap map)
    {
	MethodBundle[] barr = (MethodBundle[]) map.get (name);

	if (barr == null)
	{
	    barr = new MethodBundle[] { bundle };
	}
	else
	{
	    for (int i = 0; i < barr.length; i++)
	    {
		if (barr[i].equals (bundle))
		{
		    // already in bundle
		    return;
		}
	    }

	    MethodBundle[] newBarr = (MethodBundle[])
		new MethodBundle[barr.length + 1];
	    System.arraycopy (barr, 0, newBarr, 0, barr.length);
	    newBarr[barr.length] = bundle;
	    barr = newBarr;
	}
	map.put (name, barr);
    }

    /**
     * Sort the values of the given map, which should be arrays of
     * {@link MethodBundle} objects.
     *
     * @param map non-null; the map whose values are to be sorted
     */
    static private void sortBundles (HashMap map)
    {
	Iterator i = map.values ().iterator ();
	while (i.hasNext ())
	{
	    MethodBundle[] one = (MethodBundle[]) i.next ();
	    Arrays.sort (one);
	}
    }
}
