package com.milk.plastic.ports;

/**
 * A port for a value of type <code>double</code>. For the generic
 * {@link Port} protocol, it uses instances of {@link Double}.
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
final public class DoublePort
implements Port
{
    /** the current value of the port */
    private double myValue;

    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct an instance. The initial value in the port is <code>0</code>.
     */
    public DoublePort ()
    {
	myValue = 0.0;
    }

    // ------------------------------------------------------------------------
    // public instance methods

    // interface's comment suffices
    public String getString ()
    {
	return Double.toString (myValue);
    }

    // interface's comment suffices
    public Object getValue ()
    {
	return new Double (myValue);
    }

    // interface's comment suffices
    public void setValue (Object value)
    {
	if (value instanceof DoublePort)
	{
	    myValue = ((DoublePort) value).myValue;
	}
	else
	{
	    myValue = ((Double) value).doubleValue ();
	}
    }

    /**
     * Get the value as a primitive <code>double</code>.
     *
     * @return the value
     */
    public double getDouble ()
    {
	return myValue;
    }

    /**
     * Set the value from a primitive <code>double</code>.
     *
     * @param value the new value
     */
    public void setDouble (double value)
    {
	myValue = value;
    }
}
