package com.milk.plastic.iface;

import com.milk.plastic.ports.Port;
import java.util.Map;

/**
 * Description of a set of named parameters. Instances of this class are
 * used for several reasons, including specifying the possible arguments
 * that may be passed to a {@link Factory} for instantiating a object
 * and indicating the sets of input and output connections available on a
 * module. Instances of this class are immutable, so it is safe, for
 * example, for a <code>Factory</code> to make an instance and keep
 * it in a static variable and hand it out directly to all who ask.
 *
 * <p>An individual argument spec consists of a name for that argument
 * (a string) and a type for that argument. The type is a <code>Class</code>
 * which indicates the valid range of values for the field in question.</p>
 *
 * @see Template
 * @see Factory#getObjectTemplate
 * @see Factory#getFactoryTemplate
 * @see Factory#make
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
final public class Template
{
    /** the names of the arguments */
    private String[] myNames;

    /** the corresponding type restrictions for the arguments */
    private Class[] myTypes;

    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct an instance.
     *
     * @param names array of names of the arguments
     * @param types array of corresponding types for the arguments
     */
    public Template (String[] names, Class[] types)
    {
	if (names == null)
	{
	    throw new IllegalArgumentException ("names = null");
	}

	if (types == null)
	{
	    throw new IllegalArgumentException ("types = null");
	}

	if (names.length != types.length)
	{
	    throw new IllegalArgumentException (
                "names.length != types.length");
	}

	// we make separate arrays from the constructor params to make sure
	// the arrays are private, since we can't guarantee that the client
	// won't nefariously tweak them after this constructor completes;
	// also, we're interested in interning all the Strings, and maybe
	// the client doesn't want that to happen to their copy of the
	// names array (it could happen).
	myNames = new String[names.length];
	myTypes = (Class[]) types.clone ();

	for (int i = 0; i < names.length; i++)
	{
	    if (names[i] == null)
	    {
		throw new IllegalArgumentException ("names[" + i + "] = null");
	    }

	    if (types[i] == null)
	    {
		throw new IllegalArgumentException ("types[" + i + "] = null");
	    }

	    myNames[i] = names[i].intern ();

	    for (int j = 0; j < i; j++)
	    {
		if (myNames[i] == myNames[j])
		{
		    throw new IllegalArgumentException (
                        "names[" + j + "] = names[" + i + "]");
		}
	    }
	}
    }

    // ------------------------------------------------------------------------
    // public instance methods

    /**
     * Return the string form of this object.
     *
     * @return the string form
     */
    public String toString ()
    {
	StringBuffer sb = new StringBuffer ();
	sb.append ("Template[");

	for (int i = 0; i < myNames.length; i++)
	{
	    if (i != 0)
	    {
		sb.append ("; ");
	    }

	    sb.append (myNames[i]);
	    sb.append (": ");
	    sb.append (myTypes[i].getName ());
	}

	sb.append ("]");
	return sb.toString ();
    }

    /**
     * Get the count of specs.
     *
     * @return the count of argument specs.
     */
    public int getCount ()
    {
	return myNames.length;
    }

    /**
     * Get the index of the element with the given name. This throws
     * an exception if the name is not found.
     *
     * @param name non-null; the name to look up
     * @return the index of the name
     */
    public int indexOf (String name)
    {
	if (name == null)
	{
	    throw new IllegalArgumentException ("name = null");
	}

	name = name.intern ();

	for (int i = 0; i < myNames.length; i++)
	{
	    if (myNames[i] == name)
	    {
		return i;
	    }
	}

	throw new PlasticException ("Name (" + name + ") not found in " +
				    "template " + this);
    }

    /**
     * Get the <code>n</code>th name.
     *
     * @param n which name to get
     * @return the name of the <code>n</code>th argument
     */
    public String getName (int n)
    {
	if ((n < 0) || (n >= myNames.length))
	{
	    throw new IllegalArgumentException ("n = " + n);
	}

	return myNames[n];
    }

    /**
     * Get the <code>n</code>th type.
     *
     * @param n which type to get
     * @return the type of the <code>n</code>th argument
     */
    public Class getType (int n)
    {
	if ((n < 0) || (n >= myNames.length))
	{
	    throw new IllegalArgumentException ("n = " + n);
	}

	return myTypes[n];
    }

    /**
     * Check a map of arguments against this template, ignoring entries
     * in the map that don't correspond to fields in the template.
     * Return normally if all is well, or throw an exception if there
     * is a problem.
     *
     * @param args the arguments to check
     * @exception PlasticException thrown if the check fails
     */
    public void checkArgsAllowExtras (Map args)
    {
	for (int i = 0; i < myNames.length; i++)
	{
	    Object arg = args.get (myNames[i]);
	    if (arg == null)
	    {
		throw new PlasticException ("Missing argument (" + myNames[i] +
					    ") for template: " + this);
	    }

	    if (! myTypes[i].isInstance (arg))
	    {
		throw new PlasticException ("Argument type mismatch for " +
					    myNames[i] + "; got " +
					    arg.getClass ().getName () + 
					    "; template: " + this);
	    }
	}
    }

    /**
     * Check a map of arguments against this template, and complain if
     * there are entries in the map that don't correspond to fields in the
     * template. Return normally if all is well, or throw an exception if
     * there is a problem.
     *
     * @param args the arguments to check
     * @exception PlasticException thrown if the check fails 
     */
    public void checkArgsNoExtras (Map args)
    {
	checkArgsAllowExtras (args);

	if (args.size () != myNames.length)
	{
	    throw new PlasticException ("Extra arguments detected; args: " +
					args + "; template: " + this);
	}
    }

    /**
     * Return a new template that is identical to this instance, except
     * that it does not contain any fields whose types are of interface
     * {@link Port}.
     *
     * @return the appropriately constructed instance
     */
    public Template withNoPorts ()
    {
	int count = 0;
	for (int i = 0; i < myTypes.length; i++)
	{
	    if (! Port.class.isAssignableFrom (myTypes[i]))
	    {
		count++;
	    }
	}

	String[] names = new String[count];
	Class[] types = new Class[count];
	int at = 0;

	for (int i = 0; i < myNames.length; i++)
	{
	    if (! Port.class.isAssignableFrom (myTypes[i]))
	    {
		names[at] = myNames[i];
		types[at] = myTypes[i];
		at++;
	    }
	}

	return new Template (names, types);
    }

    /**
     * Return a new template that is identical to this instance, except
     * that the given additional elements are present, appended to the
     * end of the original template.
     *
     * @param names the extra names
     * @param types the corresponding types
     */
    public Template withMore (String[] names, Class[] types)
    {
	if (names == null)
	{
	    throw new IllegalArgumentException ("names = null");
	}

	if (types == null)
	{
	    throw new IllegalArgumentException ("types = null");
	}

	if (names.length != types.length)
	{
	    throw new IllegalArgumentException (
                "names.length != types.length");
	}

	int origLen = myNames.length;
	String[] n2 = new String[origLen + names.length];
	Class[] t2 = new Class[n2.length];

	System.arraycopy (myNames, 0, n2, 0, origLen);
	System.arraycopy (myTypes, 0, t2, 0, origLen);
	System.arraycopy (names, 0, n2, origLen, names.length);
	System.arraycopy (types, 0, t2, origLen, names.length);

	return new Template (n2, t2);
    }
}
