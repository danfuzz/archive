package com.milk.plastic.ports;

/**
 * A port for a value of type <code>boolean</code>. For the generic
 * {@link Port} protocol, it uses instances of {@link Boolean}.
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
final public class BooleanPort
implements Port
{
    /** the current value of the port */
    private boolean myValue;

    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct an instance. The initial value in the port is
     * <code>false</code>.
     */
    public BooleanPort ()
    {
	myValue = false;
    }

    // ------------------------------------------------------------------------
    // public instance methods

    // interface's comment suffices
    public String getString ()
    {
	return myValue ? "true" : "false";
    }

    // interface's comment suffices
    public Object getValue ()
    {
	return myValue ? Boolean.TRUE : Boolean.FALSE;
    }

    // interface's comment suffices
    public void setValue (Object value)
    {
	if (value instanceof BooleanPort)
	{
	    myValue = ((BooleanPort) value).myValue;
	}
	else
	{
	    myValue = ((Boolean) value).booleanValue ();
	}
    }

    /**
     * Get the value as a primitive <code>boolean</code>.
     *
     * @return the value
     */
    public boolean getBoolean ()
    {
	return myValue;
    }

    /**
     * Set the value from a primitive <code>boolean</code>.
     *
     * @param value the new value
     */
    public void setBoolean (boolean value)
    {
	myValue = value;
    }
}
