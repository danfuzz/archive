package com.milk.plastic.modules.wave;

import com.milk.plastic.ports.DoublePort;
import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.Template;
import com.milk.plastic.util.BaseFixedWave;
import com.milk.plastic.util.FixedWaveFactory;
import java.util.Map;

/**
 * Fixed sine wave module. These are modules which provide a single output
 * of a sine wave of a given wavelength, with the two given values as the
 * extrema. This is a standard fixed wave module, so its parameters are
 * described by {@link FixedWaveFactory}.
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
final public class FixedSineWave
extends BaseFixedWave
{
    /** the handy constant PI * 2 */
    static private final double PI2 = Math.PI * 2;

    /** short name */
    private static final String SHORT_NAME = "FixedSineWave";

    /** factory that creates instances of this class */
    public static final Factory FACTORY = 
	new FixedWaveFactory (SHORT_NAME, FixedSineWave.class);

    /** amount to multiply each output by */
    private double myMul;

    /** amount to add to each output */
    private double myAdd;

    /** amount to add to the index per tick */
    private double myIncrement;

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
    public FixedSineWave (Factory factory, Template template, Map args)
    {
	super (factory, template, args);

	myMul = (myV1 - myV0) / 2;
	myAdd = (myV1 + myV0) / 2;
	myIncrement = PI2 / myWlen;
    }

    // ------------------------------------------------------------------------
    // public methods the superclass requires us to implement

    // interface's comment suffices
    public void tick ()
    {
	// efficiency dictates we get a local copy of this
	double index = myIndex;

	myOutPort.setDouble (Math.sin (index) * myMul + myAdd);
	index += myIncrement;
	if (index < PI2)
	{
	    myIndex = index;
	}
	else
	{
	    myIndex = index - PI2;
	}
    }

    // ------------------------------------------------------------------------
    // protected methods the superclass requires us to implement

    // superclass's comment suffices
    protected void bind1 ()
    {
	myIndex = PI2 * myPhase;
    }

    // superclass's comment suffices
    protected void reset1 ()
    {
	// no need to do anything
    }
}

