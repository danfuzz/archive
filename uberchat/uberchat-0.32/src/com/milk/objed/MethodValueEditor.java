// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.objed;

import com.milk.objed.event.EditorEvent;
import com.milk.util.BadValueException;
import com.milk.util.BugInClientException;
import com.milk.util.ImmutableException;
import com.milk.util.ShouldntHappenException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This is a <code>ValueEditor</code> which uses reflection to access
 * a getter and setter method which collectively act like a field on a
 * given target.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class MethodValueEditor
extends BaseEditor
implements ValueEditor
{
    /** an empty array, used for <code>Method.invoke</code> */
    private static final Object[] TheEmptyArray = new Object[0];

    /** the ultimate target */
    private Object myTarget;

    /** the getter object */
    private Method myGetter;

    /** the setter object */
    private Method mySetter;

    /** an array of one object, used to hold a single value to pass as
     * the argument to the setter, also used to synchronize on */
    private Object[] myLittleArray;

    /** the last value returned from the getter */
    private Object myLastValue;

    /**
     * Construct a <code>MethodValueEditor</code> to access the given named
     * methods of the given object. The methods are named in the standard
     * way, that is, by prepending <code>"get"</code> or <code>"set"</code>
     * and capitalizing the first letter of the given name. If the set
     * variant doesn't exist, then the "field" is assumed to be immutable.
     * Note that the getter method gets invoked during the creation of this
     * object. Also note that if there are multiple methods of the right
     * names which match the form factors for getter or setter methods,
     * this class will likely get very confused. In such cases, one should
     * use the constructor which explicitly takes <code>Method</code>
     * objects.
     *
     * @param label the label
     * @param description the description
     * @param target the object to access
     * @param name the base name of the methods
     * @exception IllegalArgumentException thrown if the target doesn't have
     * at least a public getter method with appropriate names 
     */
    public MethodValueEditor (String label, String description, 
			      Object target, String name)
    {
	this (label, description, target, findMethods (target, name));
    }

    /**
     * Constructor that takes the getter and setter as an array, just a
     * convenience to make the code a little prettier. It would have been
     * nice to just use a local variable in the name-based constructor, but
     * it would have had to been done before the call to
     * <code>this()</code>, and Java doesn't allow that. Java just sucks
     * that way sometimes.
     *
     * @param label the label
     * @param description the description
     * @param target the object to access
     * @param methods the array of two methods, a getter and a setter 
     */
    private MethodValueEditor (String label, String description, Object target,
			       Method[] methods)
    {
	this (label, description, target, methods[0], methods[1]);
    }

    /**
     * Construct a <code>MethodValueEditor</code> to access the explicitly
     * given getter and setter methods of the given object. If the setter
     * is passed as null, it is taken to mean that the field is immutable.
     * Note that the getter method gets invoked during the creation of this
     * object.
     *
     * @param label the label
     * @param description the description
     * @param target the object to access
     * @param getter non-null; the getter method
     * @param setter null-ok; the setter method
     * @exception IllegalArgumentException thrown if the getter or
     * doesn't seem to respond properly or if the setter's form factor
     * isn't right (e.g., it either takes more than one argument or it
     * returns void)
     */
    public MethodValueEditor (String label, String description, Object target, 
			      Method getter, Method setter)
    {
	super (label, description, (setter != null));
	myTarget = target;
	myGetter = getter;
	mySetter = setter;

	try
	{
	    myLastValue = myGetter.invoke (myTarget, TheEmptyArray);
	}
	catch (Exception ex)
	{
	    throwIllegal (target, "getter", getter.getName ());
	}

	if (mySetter == null)
	{
	    mutabilityChanged (false);
	}
	else
	{
	    myLittleArray = new Object[1];
	    if (   (mySetter.getReturnType () == Void.TYPE)
	        || (mySetter.getParameterTypes ().length != 1))
	    {
		throwIllegal (target, "setter", setter.getName ());
	    }
	}
    }

    // ------------------------------------------------------------------------
    // ValueEditor interface methods

    /**
     * Get the value from the target.
     *
     * @return the current value
     * @exception BadValueException thrown if the value is bad in some
     * way
     */
    public Object getValue ()
    throws BadValueException
    {
	try
	{
	    Object value = myGetter.invoke (myTarget, TheEmptyArray);
	    if (   (value != myLastValue)
		&& !value.equals (myLastValue))
	    {
		myLastValue = value;
		broadcast (EditorEvent.valueChanged (this, value));
	    }
	    return value;
	}
	catch (InvocationTargetException ex)
	{
	    Throwable targex = ex.getTargetException ();
	    if (targex instanceof RuntimeException)
	    {
		throw ((RuntimeException) targex);
	    }
	    throw new BugInClientException (
                "Client of MethodValueEditor threw exception.", targex);
	}
	catch (Exception ex)
	{
	    throw new ShouldntHappenException (
                "Unexpected exception during call to getValue().", ex);
	}
    }

    /**
     * Set the value in the target.
     *
     * @param value the new value
     * @exception BadValueException thrown if the value is inappropriate
     * for this editor
     * @exception ImmutableException thrown if the editor is in fact
     * immutable
     */
    public void setValue (Object value)
    throws BadValueException, ImmutableException
    {
	if (mySetter == null)
	{
	    throw new ImmutableException (this);
	}

	try
	{
	    // must synch on the array to avoid trampling on it by
	    // multiple threads attemping to set the value at once
	    synchronized (myLittleArray)
	    {
		myLittleArray[0] = value;
		mySetter.invoke (myTarget, myLittleArray);
	    }
	}
	catch (InvocationTargetException ex)
	{
	    Throwable targex = ex.getTargetException ();
	    if (targex instanceof RuntimeException)
	    {
		throw ((RuntimeException) targex);
	    }
	    throw new BugInClientException (
                "Client of MethodValueEditor threw exception.", targex);
	}
	catch (Exception ex)
	{
	    throw new ShouldntHappenException (
                "Unexpected exception during call to setValue().", ex);
	}

	// do a getValue() to make valueChanged event happen if appropriate
	getValue ();
    }

    // ------------------------------------------------------------------------
    // Editor interface methods

    /**
     * Ask this editor to update its internal state. In this case,
     * it does a <code>getValue()</code>, which may in fact cause
     * a <code>valueChanged</code> event to get sent.
     */
    public void update ()
    {
	getValue ();
    }

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * Given an object and a base method name, return the <code>Method</code>
     * objects for accessing that "field" on the given object, or throw
     * an <code>IllegalArgumentException</code> (a nice
     * <code>RuntimeException</code>--down with compiler-enforced exception
     * checking!) if there's a problem with the name. 
     *
     * @param target the object to access
     * @param name the base name
     * @return the array of <code>{ getter, setter }</code>
     * @exception IllegalArgumentException thrown if there's a problem
     * finding the methods
     */
    private static Method[] findMethods (Object target, String name)
    {
	Method[] result = new Method[2];
	String upName = 
	    Character.toUpperCase (name.charAt (0)) +
	    name.substring (1);
	String getterName = ("get" + upName).intern ();
	String setterName = ("set" + upName).intern ();

	try
	{
	    // since we're nice about not making the client specify the
	    // type, we have to do a search through all the methods to
	    // find a matching one. Java doesn't make it particularly
	    // easy. It just sucks that way sometimes.
	    Method[] all = target.getClass ().getMethods ();
	    for (int i = 0; i < all.length; i++)
	    {
		String nm = all[i].getName ();
		int args = all[i].getParameterTypes ().length;
		boolean voidRet = (all[i].getReturnType () == Void.TYPE);
		if (   (nm == getterName)
		    && (args == 0)
		    && (! voidRet))
		{
		    result[0] = all[i];
		}
		else if (   (nm == setterName)
			 && (args == 1)
			 && voidRet)
		{
		    result[1] = all[i];
		}
	    }
	}
	catch (Exception ex)
	{
	    throwIllegal (target, "base", name);
	}

	if (result[0] == null)
	{
	    throwIllegal (target, "base", name);
	}

	return result;
    }

    /**
     * Throw and <code>IllegalArgumentException</code> about the given
     * target and method not matching.
     *
     * @param target the object to access
     * @param kind the kind of method (getter or setter)
     * @param name the name of the method
     * @exception IllegalArgumentException thrown by definition
     */
    private static void throwIllegal (Object target, String kind, String name)
    {
	throw new IllegalArgumentException (
	    "Target (" + target + ") and " + kind + " method (" + name + 
	    ") don't match.");
    }
}
