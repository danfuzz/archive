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
 * text string. The underlying value isn't necessarily a
 * <code>String</code>; for example, <code>IntegerTextEditor</code> (a
 * subclass of this class) is for editing an <code>Integer</code> value as
 * text.</p>
 *
 * <p>This class knows about minimum, maximum, and preferred (for
 * presentation) lengths of the text. The following are restrictions on
 * those values: <code>maximumLength</code> and
 * <code>preferredLength</code> may be <code>NONE</code> to indicate that
 * there is no maximum or preferred length (respectively). It is illegal
 * for <code>maximumLength</code> to be smaller than
 * <code>minimumLength</code>. <code>preferredLength</code>, if not
 * <code>NONE</code>, must be between <code>minimumLength</code> and
 * <code>maximumLength</code>. Aside from the special value
 * <code>NONE</code>, all the parameters must be non-negative.</p>
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public abstract class TextEditor
extends BaseWrappedValueEditor
{
    /** constant to use for length to mean that there is no maximum or
     * preferred value */
    public static final int NONE = -1;

    /** the minimum length */
    private int myMinimumLength;

    /** the maximum length or <code>NONE</code> */
    private int myMaximumLength;

    /** the preferred length or <code>MOME</code> */
    private int myPreferredLength;

    /** whether (true) or not (false) the text should be hidden (as in
     * for password entry */
    private boolean myHideText;

    /**
     * Construct a <code>TextEditor</code>. It is initially set to have no
     * minimum, maximum, or preferred length, but that can be changed with
     * the <code>setLengths()</code> method. Also, if password-style
     * hidden text is needed, use <code>setHidden()</code>.
     *
     * @param target the target for getting and setting 
     */
    protected TextEditor (ValueEditor target)
    {
	super (target);
	myMinimumLength = 0;
	myMaximumLength = NONE;
	myPreferredLength = NONE;
	myHideText = false;
    }
	
    // ------------------------------------------------------------------------
    // Public methods

    /**
     * Get the minimum length that the text will ever be.
     *
     * @return the minimum length
     */
    public final int getMinimumLength ()
    {
	return myMinimumLength;
    }

    /**
     * Get the maximum length that the text will ever be. This returns
     * <code>TextEditor.NONE</code> if there is no maximum.
     *
     * @return the maximum length
     */
    public final int getMaximumLength ()
    {
	return myMaximumLength;
    }

    /**
     * Get the preferred length for a field displaying the text in this
     * editor. This returns <code>TextEditor.NONE</code> if there is no
     * preferred length.
     *
     * @return the preferred length
     */
    public final int getPreferredLength ()
    {
	return myPreferredLength;
    }

    /**
     * Get whether or not the text should be hidden, as in password entry.
     *
     * @return true if the text should be hidden
     */
    public final boolean getHidden ()
    {
	return myHideText;
    }

    /**
     * Set whether or not the text should be hidden, as in password entry.
     *
     * @param hideText true if the text should be hidden
     */
    public final void setHidden (boolean hideText)
    {
	myHideText = hideText;
    }

    // ------------------------------------------------------------------------
    // ValueEditor interface methods

    /**
     * Get the text value of this editor. This will only ever return
     * a string whose length is between <code>getMinimumLength()</code>
     * and <code>getMaximumLength</code>.
     *
     * @return the text value
     * @exception BadValueException thrown if the value is bad in some
     * way
     */
    public final Object getValue ()
    throws BadValueException
    {
	Object value = super.getValue ();
	String result = valueToText (value);
	int len = result.length ();
	if (   (len < myMinimumLength)
	    || ((len > myMaximumLength) && (myMaximumLength != NONE)))
	{
	    throwLengthException (value);
	}

	return result;
    }

    /**
     * Set the text value of this editor. This will only ever accept a
     * string as an argument, and only if the length is between
     * <code>getMinimumLength()</code> and <code>getMaximumLength</code>.
     *
     * @param text the new text value
     * @exception BadValueException thrown if the value is inappropriate
     * for this editor
     * @exception ImmutableException thrown if the editor is in fact
     * immutable 
     */
    public final void setValue (Object value)
    throws BadValueException, ImmutableException
    {
	if (! (value instanceof String))
	{
	    throw new BadValueException (value, this,
					 "Value must be a string.");
	}

	String text = (String) value;
	int len = text.length ();
	if (   (len < myMinimumLength)
	    || ((len > myMaximumLength) && (myMaximumLength != NONE)))
	{
	    throwLengthException (value);
	}

	super.setValue (textToValue (text));
    }

    // ------------------------------------------------------------------------
    // Protected helper methods
    
    /**
     * Set the minimum, maximum, and preferred lengths for this text
     * editor.
     *
     * @param minimumLength the minimum allowed textual length of the editor
     * @param maximumLength the maximum allowed textual length of the editor
     * @param preferredLength the preferred presentation length of the editor 
     * @exception IllegalArgumentException thrown if the lengths do not
     * abide by the restrictions as specified in the description of this
     * class
     */
    protected final void setLengths (int minimumLength, int maximumLength, 
				     int preferredLength)
    {
	if (minimumLength < 0)
	{
	    throw new IllegalArgumentException (
                "Bad value for minimumLength: " + minimumLength);
	}

	if (maximumLength == NONE)
	{
	    // it's okay
	}
	else if (   (maximumLength < 0)
		 || (maximumLength < minimumLength))
	{
	    throw new IllegalArgumentException (
                "Bad value for maximumLength: " + maximumLength);
	}

	if (preferredLength == NONE)
	{
	    // it's okay
	}
	else if (   (preferredLength < 0)
		 || (preferredLength < minimumLength)
		 || (   (preferredLength > maximumLength) 
		     && (maximumLength != NONE)))
	{
	    throw new IllegalArgumentException (
                "Bad value for preferredLength: " + preferredLength);
	}

	myMinimumLength = minimumLength;
	myMaximumLength = maximumLength;
	myPreferredLength = preferredLength;
    }

    // ------------------------------------------------------------------------
    // Protected methods that subclasses must override

    /**
     * Turn the given value into its text form. The value is on its way out
     * of a <code>getValue()</code>. It is okay to throw
     * <code>BadValueException</code> if the value is inappropriate.
     *
     * @param value the value to process
     * @return the text form of the value
     * @exception BadValueException if the value is inappropriate 
     */
    protected abstract String valueToText (Object value);

    /**
     * Turn the given text into its value form. The text is on its way into
     * a <code>setValue()</code>. It is okay to throw
     * <code>BadValueException</code> if the value is inappropriate.
     *
     * @param text the text to process
     * @return the value form of the text
     * @exception BadValueException if the value is inappropriate 
     */
    protected abstract Object textToValue (String text);

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * Throw an exception complaining about the length of the value.
     *
     * @param value the value to complain about
     * @exception BadValueException thrown by definition
     */
    private void throwLengthException (Object value)
    {
	String reason;
	if (myMinimumLength == 0)
	{
	    reason = "The value must be at most " + myMaximumLength +
		" characters in length.";
	}
	else if (myMaximumLength == NONE)
	{
	    reason = "The value must be at least " + myMinimumLength +
		" characters in length.";
	}
	else
	{
	    reason = "The value must be between " + myMinimumLength +
		" and " + myMaximumLength + 
		" characters in length (inclusive).";
	}
	
	throw new BadValueException (value, this, reason);
    }
}
