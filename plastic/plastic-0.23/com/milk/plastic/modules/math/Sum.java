package com.milk.plastic.modules.math;

import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.Ref;
import com.milk.plastic.iface.Template;
import com.milk.plastic.ports.DoublePort;
import com.milk.plastic.util.BaseModule;
import java.util.Map;

/**
 * Sum module. 
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
final public class Sum
extends BaseModule
{
    /** count of inputs */
    private int myCount;

    /** null-ok; array of bound input ports, set in {@link #bind1} and
     * <code>null</code>ed out in {@link #reset1} */
    private DoublePort[] myInPorts;

    /** output port */
    private DoublePort myOutPort;

    // ------------------------------------------------------------------------
    // constructor
    
    /**
     * Construct an instance.
     *
     * @param factory the factory which spawned this instance
     * @param template the full template for this instance
     * @param args the arguments which this instance was constructed from 
     */
    public Sum (Factory factory, Template template, Map args)
    {
	super (factory, template, args);

	myCount = ((Integer) args.get ("count")).intValue ();

	// augment the type restriction on the inputs
	for (int i = 0; i < myCount; i++)
	{
	    String inName = "in_" + i;
	    Ref newRef = ((Ref) getField (inName)).withType (DoublePort.class);
	    setField (inName, newRef);
	}

	// get the sole output
	myOutPort = (DoublePort) getField ("out");
    }

    // ------------------------------------------------------------------------
    // public methods the superclass requires us to implement

    // interface's comment suffices
    public void tick ()
    {
	// efficiency dictates that we get a local copy of this
	DoublePort[] inPorts = myInPorts;

	double result = inPorts[0].getDouble ();
	for (int i = myCount - 1; i > 0; i--)
	{
	    result += inPorts[i].getDouble ();
	}

	myOutPort.setDouble (result);
    }

    // ------------------------------------------------------------------------
    // protected methods the superclass requires us to implement

    // superclass's comment suffices
    protected void bind1 ()
    {
	myInPorts = new DoublePort[myCount];
	for (int i = 0; i < myCount; i++)
	{
	    myInPorts[i] = (DoublePort) getBoundObject ("in_" + i);
	}
    }

    // superclass's comment suffices
    protected void reset1 ()
    {
	myInPorts = null;
    }
}
