package com.milk.plastic.modules.base;

import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.PlasticException;
import com.milk.plastic.iface.Ref;
import com.milk.plastic.iface.Template;
import com.milk.plastic.ports.Port;
import com.milk.plastic.util.BaseModule;
import java.util.Map;

/**
 * Null module. Null modules take an arbitrary number of
 * inputs and simply pass them through to the corresponding outputs.
 * This module exists primarily as an educational example, but is also
 * used for the degenerate (<code>count = 1</code>) products of
 * {@link MulFactory} and {@link SumFactory}.
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
final public class Null
extends BaseModule
{
    /** count of inputs and outputs */
    private int myCount;

    /** type of the inputs and outputs */
    private Class myType;

    /** non-null; array of bound output ports */
    private Port[] myOutputs;

    /** null-ok; array of bound input ports, set in {@link #bind1} and
     * <code>null</code>ed out in {@link #reset1} */
    private Port[] myInputs;

    // ------------------------------------------------------------------------
    // constructor
    
    /**
     * Construct an instance.
     *
     * @param factory the factory which spawned this instance
     * @param template the full template for this instance
     * @param args the arguments which this instance was constructed from 
     * @exception PlasticException thrown if there was trouble instantiating
     * any of the blank fields
     */
    public Null (Factory factory, Template template, Map args)
	throws PlasticException
    {
	super (factory, template, args);

	myCount = ((Integer) args.get ("count")).intValue ();
	myType = (Class) ((Ref) args.get ("type")).resolve ();
	myOutputs = new Port[myCount];

	// get the outputs and augment the type restriction on the inputs
	for (int i = 0; i < myCount; i++)
	{
	    myOutputs[i] = (Port) getField ("out_" + i);
	    String inName = "in_" + i;
	    Ref newRef = ((Ref) getField (inName)).withType (myType);
	    setField (inName, newRef);
	}
    }

    // ------------------------------------------------------------------------
    // public methods the superclass requires us to implement

    // interface's comment suffices
    public void tick ()
    {
	for (int i = 0; i < myCount; i++)
	{
	    myOutputs[i].setValue (myInputs[i]);
	}
    }

    // ------------------------------------------------------------------------
    // protected methods the superclass requires us to implement

    // superclass's comment suffices
    protected void bind1 ()
    {
	myInputs = new Port[myCount];
	for (int i = 0; i < myCount; i++)
	{
	    myInputs[i] = (Port) getBoundObject ("in_" + i);
	}
    }

    // superclass's comment suffices
    protected void reset1 ()
    {
	myInputs = null;
    }
}

