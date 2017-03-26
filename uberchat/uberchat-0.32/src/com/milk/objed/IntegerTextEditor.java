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

/**
 * This is an editor for integer values as text.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class IntegerTextEditor
extends TextEditor
{
    /** the minimum value */
    private int myMinimumValue;

    /** the maximum value */
    private int myMaximumValue;

    /**
     * Construct an <code>IntegerTextEditor</code> with no value
     * restrictions. If such restrictions are desired,
     * use the <code>setValueRestrictions()</code> method.
     *
     * @param target the target for getting and setting 
     */
    public IntegerTextEditor (ValueEditor target)
    {
	super (target);
	setValueRestrictions (Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Set the minimum and maximum allowed values of the field. Use
     * <code>Integer.MIN_VALUE</code> and/or <code>Integer.MAX_VALUE</code>
     * for the obvious purposes.
     *
     * @param minimumValue the minimum value for the field
     * @param maximumValue the maximum value for the field
     * @exception IllegalArgumentException thrown if <code>minimumValue</code>
     * is greater than <code>maximumValue</code>
     */
    public void setValueRestrictions (int minimumValue, int maximumValue)
    {
	if (minimumValue > maximumValue)
	{
	    throw new IllegalArgumentException (
                "Bad parameters to setValueRestrictions: " + 
		minimumValue + ", " + maximumValue);
	}

	myMinimumValue = minimumValue;
	myMaximumValue = maximumValue;

	int minlen = (myMinimumValue >= 0) 
	    ? 1 
	    : Integer.toString (myMinimumValue).length ();
	int maxlen = Integer.toString (myMaximumValue).length ();
	maxlen = Math.max (minlen, maxlen);
	setLengths (0, maxlen, maxlen);
    }

    /**
     * Return the minimum allowed value for the field.
     *
     * @return the minimum allowed value
     */
    public int getMinimumValue ()
    {
	return myMinimumValue;
    }

    /**
     * Return the maximum allowed value for the field.
     *
     * @return the maximum allowed value
     */
    public int getMaximumValue ()
    {
	return myMaximumValue;
    }
    
    // ------------------------------------------------------------------------
    // Protected methods that this class overrides

    /**
     * Turn the given value into its text form. In this case, we just
     * do a <code>toString()</code> on the argument.
     *
     * @param value the value to process
     * @return the text form of the value
     * @exception BadValueException if the value is inappropriate 
     */
    protected String valueToText (Object value)
    throws BadValueException
    {
	return value.toString ();
    }

    /**
     * Turn the given text into its value form. In this case, we attempt
     * to parse it as an integer. If that fails, or if the resultant
     * value is out of range, we throw an exception.
     *
     * @param text the text to process
     * @return the value form of the text
     * @exception BadValueException if the value is inappropriate 
     */
    protected Object textToValue (String text)
    throws BadValueException
    {
	int ival;

	try
	{
	    ival = Integer.parseInt (text);
	}
	catch (NumberFormatException ex)
	{
	    throw new BadValueException (text, this, 
					 "The value is not an integer.");
	}

	if ((ival < myMinimumValue) || (ival > myMaximumValue))
	{
	    String reason;
	    if (myMinimumValue == Integer.MIN_VALUE)
	    {
		reason = "The value must be at most " + myMaximumValue + ".";
	    }
	    else if (myMaximumValue == Integer.MAX_VALUE)
	    {
		reason = "The value must be at least " + myMinimumValue + ".";
	    }
	    else
	    {
		reason = "The value must be between " + myMinimumValue + 
		    " and " + myMaximumValue + " (inclusive).";
	    }
	    throw new BadValueException (text, this, reason);
	}

	return new Integer (ival);
    }
}
