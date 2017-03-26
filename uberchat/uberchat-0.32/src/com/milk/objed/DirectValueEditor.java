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

/**
 * This class is a <code>ValueEditor</code> whose managed value is directly
 * held in the object. It knows how to generically ensure that the value is
 * always of a certain class or interface. It also provides public methods
 * to set the description and mutability of the object.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class DirectValueEditor
extends BaseEditor
implements ValueEditor
{
    /** the current value */
    private Object myValue;

    /** the class/interface that the value must implement */
    private Class myRestriction;

    /** whether (true) or not (false) null is a valid value */
    private boolean myAllowNull;

    /**
     * Construct a <code>DirectValueEditor</code>.
     *
     * @param label the label
     * @param description the initial description
     * @param mutability the initial mutability
     * @param value null-ok; the initial value
     * @param restriction null-ok; if non-null, the class/interface that
     * the value must be an instance of
     * @param allowNull whether (true) or not (false) null is a valid value
     * @exception BadValueException thrown if the value as specified doesn't
     * meet the restrictions as specified
     */
    public DirectValueEditor (String label, String description, 
			      boolean mutability, Object value,
			      Class restriction, boolean allowNull)
    throws BadValueException
    {
	super (label, description, mutability);
	myRestriction = (restriction == null) ? Object.class : restriction;
	myAllowNull = allowNull;
	setValue (value);
    }

    /**
     * Construct a <code>DirectValueEditor</code> with no restrictions
     * on class or null-ness.
     *
     * @param label the label
     * @param description the initial description
     * @param mutability the initial mutability
     * @param value null-ok; the initial value
     */
    public DirectValueEditor (String label, String description, 
			      boolean mutability, Object value)
    throws BadValueException
    {
	this (label, description, mutability, value, null, true);
    }

    /**
     * Construct a <code>DirectValueEditor</code> with a null initial
     * value and no restrictions on class or null-ness.
     *
     * @param label the label
     * @param description the initial description
     * @param mutability the initial mutability
     */
    public DirectValueEditor (String label, String description, 
			      boolean mutability)
    throws BadValueException
    {
	this (label, description, mutability, null);
    }

    /**
     * Change the description of this editor.
     *
     * @param description the new description
     */
    public void setDescription (String description)
    {
	descriptionChanged (description);
    }

    /**
     * Change the mutability of this editor.
     *
     * @param mutability the new mutability
     */
    public void setMutability (boolean mutability)
    {
	mutabilityChanged (mutability);
    }

    // ------------------------------------------------------------------------
    // ValueEditor interface methods

    /**
     * Get the current value from this editor.
     *
     * @return the current value
     */
    public final Object getValue ()
    {
	return myValue;
    }

    /**
     * Set a new value for the editor. If restrictions were specified on
     * construction, then those restrictions are checked before actually
     * setting the value. If the value actually did change, then a
     * <code>valueChanged</code> event is sent to all listeners.
     *
     * @param value the new value
     * @exception BadValueException thrown if the value is inappropriate
     * for this editor
     * @exception ImmutableException thrown if the editor is in fact
     * immutable 
     */
    public final void setValue (Object value)
    throws BadValueException
    {
	if (! isMutable ())
	{
	    throw new ImmutableException (this);
	}

	if ((value == null) && !myAllowNull)
	{
	    throw new BadValueException (value, this, 
					 "Null is not an acceptable value.");
	}

	if (! myRestriction.isInstance (value))
	{
	    String name = myRestriction.getClass ().getName ();
	    name = name.substring (name.lastIndexOf ('.') + 1);
	    throw new BadValueException (
                value, this, "The value is not an instance of " + name + ".");
	}

	if (value == myValue)
	{
	    // don't bother if the value didn't really change
	    return;
	}

	myValue = value;
	broadcast (EditorEvent.valueChanged (this, value));
    }

    // ------------------------------------------------------------------------
    // Editor interface methods

    /**
     * Ask this editor to update its internal state. In this case,
     * it does nothing.
     */
    public void update ()
    {
	// this space intentionally left blank
    }
}
