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
import com.milk.util.ImmutableException;
import com.milk.util.ShouldntHappenException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * This is a <code>ValueEditor</code> which uses reflection to access
 * a particular public field of a target object.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class FieldValueEditor
extends BaseEditor
implements ValueEditor
{
    /** the ultimate target */
    private Object myTarget;

    /** the field object */
    private Field myField;

    /** whether (true) or not (false) null is a valid value */
    private boolean myAllowNull;

    /** the last known value for the field */
    private Object myLastValue;

    /**
     * Construct a <code>FieldValueEditor</code> to access the given named
     * field of the given object. If <code>mutability</code> is passed as
     * false, then the field is considered immutable, even if the
     * underlying field is not in fact <code>final</code>.
     *
     * @param label the label
     * @param description the description
     * @param mutability the mutability
     * @param allowNull whether (true) or not (false) null is a valid value
     * @param target the object to access
     * @param field the name of the field
     * @exception IllegalArgumentException thrown if the target doesn't have
     * a public field with the given name 
     */
    public FieldValueEditor (String label, String description, 
			     boolean mutability, boolean allowNull, 
			     Object target, String field)
    {
	this (label, description, mutability, allowNull,
	      target, findField (target, field));
    }

    /**
     * Construct a <code>FieldValueEditor</code> to access the given
     * <code>Field</code> of the given object. If <code>mutability</code>
     * is passed as false, then the field is considered immutable, even if
     * the underlying field is not in fact <code>final</code>.
     *
     * @param label the label
     * @param description the description
     * @param mutability the mutability
     * @param allowNull whether (true) or not (false) null is a valid value
     * @param target the object to access
     * @param field the <code>Field</code> to use to access the target
     * @exception IllegalArgumentException thrown if the target and
     * field don't match 
     */
    public FieldValueEditor (String label, String description, 
			     boolean mutability, boolean allowNull,
			     Object target, Field field)
    {
	super (label, description,
	       (mutability && ! Modifier.isFinal (field.getModifiers ())));

	myTarget = target;
	myField = field;
	myAllowNull = allowNull;

	try
	{
	    myLastValue = myField.get (myTarget);
	}
	catch (IllegalAccessException ex)
	{
	    throwIllegal (target, field.getName ());
	}
    }

    // ------------------------------------------------------------------------
    // ValueEditor interface methods

    /**
     * Get the value from the target.
     *
     * @return the current value
     */
    public Object getValue ()
    {
	try
	{
	    Object value = myField.get (myTarget);
	    if ((value == null) && (! myAllowNull))
	    {
		throw new BadValueException (value, this,
					     "Null is not a valid value.");
	    }
	    if (   (value != myLastValue)
		&& !value.equals (myLastValue))
	    {
		myLastValue = value;
		broadcast (EditorEvent.valueChanged (this, value));
	    }
	    return value;
	}
	catch (RuntimeException ex)
	{
	    throw ex;
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
	if (! isMutable ())
	{
	    throw new ImmutableException (this);
	}

	if ((value == null) && (! myAllowNull))
	{
	    throw new BadValueException (value, this,
					 "Null is not a valid value.");
	}

	try
	{
	    myField.set (myTarget, value);
	}
	catch (IllegalArgumentException ex)
	{
	    throw new BadValueException (value, this,
					 "The value is of the wrong type.");
	}
	catch (RuntimeException ex)
	{
	    throw ex;
	}
	catch (Exception ex)
	{
	    throw new ShouldntHappenException (
                "Unexpected exception during call to setValue().", ex);
	}

	// do a getValue() to get the valueChanged event to be fired,
	// if appropriate
	getValue ();
    }

    // ------------------------------------------------------------------------
    // Editor interface methods

    /**
     * Ask this editor to update its internal state. In this case,
     * it does a <code>getValue()</code>, which may in fact cause
     * a <code>valueChanged</code> event get sent.
     */
    public void update ()
    {
	getValue ();
    }

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * Given an object and a field name, return the <code>Field</code>
     * object for accessing that named field on the given object, or throw
     * an <code>IllegalArgumentException</code> (a nice
     * <code>RuntimeException</code>--down with compiler-enforced exception
     * checking!) if there's a problem with the field. 
     *
     * @param target the object to access
     * @param field the name of the field
     * @return the appropriate <code>Field</code> object
     * @exception IllegalArgumentException thrown if there's a problem
     * finding the field
     */
    private static Field findField (Object target, String field)
    {
	try
	{
	    return target.getClass ().getField (field);
	}
	catch (Exception ex)
	{
	    return throwIllegal (target, field);
	}
    }

    /**
     * Throw an <code>IllegalArgumentException</code> about the given
     * target and field not matching.
     *
     * @param target the object to access
     * @param field the name of the field
     * @return nothing, ever
     * @exception IllegalArgumentException thrown by definition
     */
    private static Field throwIllegal (Object target, String field)
    {
	throw new IllegalArgumentException (
	    "Target (" + target + ") and field (" + field + ") don't match.");
    }
}
