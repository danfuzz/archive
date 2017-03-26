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
 * This is an editor for string values as text. It has an option to
 * always intern the strings that it passes in or out.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class StringTextEditor
extends TextEditor
{
    /** true if the value should always be interned */
    private boolean myDoIntern;

    /**
     * Construct a <code>StringTextEditor</code> with no length
     * restrictions or preference. If such restrictions are desired,
     * use the <code>setLengthRestrictions()</code> method. If
     * values should be interned, use <code>setIntern()</code>.
     *
     * @param target the target for getting and setting 
     */
    public StringTextEditor (ValueEditor target)
    {
	super (target);
	myDoIntern = false;
    }

    /**
     * Set the minimum, maximum, and preferred lengths for this text
     * editor.
     * @see TextEditor
     *
     * @param minimumLength the minimum allowed textual length of the editor
     * @param maximumLength the maximum allowed textual length of the editor
     * @param preferredLength the preferred presentation length of the editor 
     * @exception IllegalArgumentException thrown if the lengths do not
     * abide by the restrictions as specified in the description of the
     * <code>TextEditor</code> class
     */
    public void setLengthRestrictions (int minimumLength, int maximumLength, 
				       int preferredLength)
    {
	setLengths (minimumLength, maximumLength, preferredLength);
    }

    /**
     * Set whether or not all values coming or going get interned.
     *
     * @param doIntern true if values should be interned
     */
    public void setIntern (boolean doIntern)
    {
	myDoIntern = doIntern;
    }
    
    // ------------------------------------------------------------------------
    // Protected methods that this class overrides

    /**
     * Turn the given value into its text form. In this case, we just
     * pass straight through, merely checking the cast, and doing
     * the intern if appropriate.
     *
     * @param value the value to process
     * @return the text form of the value
     * @exception BadValueException if the value is inappropriate 
     */
    protected String valueToText (Object value)
    {
	String result;
	try
	{
	    result = (String) value;
	}
	catch (ClassCastException ex)
	{
	    throw new BadValueException (value, this,
					 "The value must be a string.");
	}

	if (myDoIntern)
	{
	    result = result.intern ();
	}
	return result;
    }

    /**
     * Turn the given text into its value form. In this case, we just
     * return the argument directly, doing an intern, if appropriate.
     *
     * @param text the text to process
     * @return the value form of the text
     * @exception BadValueException if the value is inappropriate 
     */
    protected Object textToValue (String text)
    {
	return myDoIntern ? text.intern () : text;
    }
}
