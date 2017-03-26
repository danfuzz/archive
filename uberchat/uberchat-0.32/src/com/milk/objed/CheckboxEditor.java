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

import com.milk.util.BadValueException;
import com.milk.util.ImmutableException;

/**
 * <p>This class is an editor which wraps around a
 * <code>ValueEditor</code>, only allowing for getting and setting as a
 * <code>Boolean</code>, intended to be presented as a checkbox. The underlying value isn't necessarily a
 * <code>Boolean</code>, though. One may set the values to send to the
 * underlying editor for true and false.</p>
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class CheckboxEditor
extends BaseWrappedValueEditor
{
    /** the value to use for true */
    private Object myTrueValue;

    /** the value to use for false */
    private Object myFalseValue;

    /** the associated checkbox text (to be presented next to the
     * checkbox */
    private String myCheckboxText;

    /**
     * Construct a <code>CheckboxEditor</code>. It is initially set to have 
     * <code>Boolean.TRUE</code> and <code>Boolean.FALSE</code> be the
     * true and false values (respectively), and it has no initial
     * checkbox text.
     *
     * @param target the target for getting and setting 
     */
    public CheckboxEditor (ValueEditor target)
    {
	super (target);
	myTrueValue = Boolean.TRUE;
	myFalseValue = Boolean.FALSE;
	myCheckboxText = "";
    }
	
    // ------------------------------------------------------------------------
    // Public methods

    /**
     * Get the checkbox text. It returns <code>""</code> (and not null)
     * for empty text.
     *
     * @return non-null; the checkbox text
     */
    public final String getCheckboxText ()
    {
	return myCheckboxText;
    }

    /**
     * Set the checkbox text.
     *
     * @param text null-ok; the new checkbox text
     */
    public final void setCheckboxText (String text)
    {
	myCheckboxText = (text == null) ? "" : text;
    }

    /**
     * Set the true and false values.
     *
     * @param trueValue the true value
     * @param falseValue the false value
     */
    public final void setValues (Object trueValue, Object falseValue)
    {
	myTrueValue = trueValue;
	myFalseValue = falseValue;
    }

    // ------------------------------------------------------------------------
    // ValueEditor interface methods

    /**
     * Get the text value of this editor. This will only ever return
     * <code>Boolean</code> values. If the underlying value is <code>==</code>
     * or <code>equals()</code> to the specified false value (via
     * <code>setValues()</code>), then it returns <code>Boolean.FALSE</code>;
     * otherwise it returns <code>Boolean.TRUE</code>.
     *
     * @return the value
     * @exception BadValueException thrown if the value is bad in some
     * way
     */
    public final Object getValue ()
    throws BadValueException
    {
	Object value = super.getValue ();

	if (value == myFalseValue)
	{
	    return Boolean.FALSE;
	}
	else if ((myFalseValue != null) && (myFalseValue.equals (value)))
	{
	    return Boolean.FALSE;
	}

	return Boolean.TRUE;
    }

    /**
     * Set the text value of this editor. This will only ever accept
     * <code>Boolean</code> values. It translates them into the specified
     * true or false underlying value (specified via <code>setValues()</code>).
     *
     * @param value the new value
     * @exception BadValueException thrown if the value is inappropriate
     * for this editor
     * @exception ImmutableException thrown if the editor is in fact
     * immutable 
     */
    public final void setValue (Object value)
    throws BadValueException, ImmutableException
    {
	boolean bool;
	try
	{
	    bool = ((Boolean) value).booleanValue ();
	}
	catch (ClassCastException ex)
	{
	    throw new BadValueException (value, this,
					 "Value must be a Boolean.");
	}

	super.setValue (bool ? myTrueValue : myFalseValue);
    }
}
