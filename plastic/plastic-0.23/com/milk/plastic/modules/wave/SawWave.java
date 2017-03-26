package com.milk.plastic.modules.wave;

import com.milk.plastic.ports.DoublePort;
import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.Template;
import com.milk.plastic.util.BaseModule;
import java.util.Map;

/**
 * Saw wave module. 
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
final public class SawWave
extends BaseModule
{
    /** amount to multiply each output by */
    private double myMul;

    /** amount to add to each output */
    private double myAdd;

    /** output port */
    private DoublePort myOutPort;

    /** null-ok; the port for the wavelength, set in {@link #bind1} and
     * <code>null</code>ed out in {@link #reset1} */
    private DoublePort myWlenPort;

    /** current index */
    private double myIndex;

    // ------------------------------------------------------------------------
    // constructor
    
    /**
     * Construct an instance.
     *
     * @param factory the factory which spawned this instance
     * @param template the full template for this instance
     * @param args the arguments which this instance was constructed from 
     */
    public SawWave (Factory factory, Template template, Map args)
    {
	super (factory, template, args);

	double v0 = ((Double) args.get ("v0")).doubleValue ();
	double v1 = ((Double) args.get ("v1")).doubleValue ();

	myMul = (v1 - v0);
	myAdd = v0;

	myOutPort = (DoublePort) getField ("out");
    }

    // ------------------------------------------------------------------------
    // public methods the superclass requires us to implement

    // interface's comment suffices
    public void tick ()
    {
	// efficiency dictates we get a local copy of this
	double index = myIndex;

	myOutPort.setDouble (index * myMul + myAdd);
	index += (1.0 / myWlenPort.getDouble ());
	if (index > 1.0)
	{
	    myIndex = index - 1.0;
	}
	else
	{
	    myIndex = index;
	}
    }

    // ------------------------------------------------------------------------
    // protected methods the superclass requires us to implement

    // superclass's comment suffices
    protected void bind1 ()
    {
	myWlenPort = (DoublePort) getBoundObject ("in_wlen");
    }

    // superclass's comment suffices
    protected void reset1 ()
    {
	myWlenPort = null;
	myIndex = 0.0;
    }
}

