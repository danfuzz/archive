package com.milk.plastic.modules.wave;

import com.milk.plastic.ports.DoublePort;
import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.Template;
import com.milk.plastic.util.BaseFixedWave;
import com.milk.plastic.util.FixedWaveFactory;
import java.util.Map;
import java.util.Random;

/**
 * Fixed random wave module. These modules provide output which is evenly
 * distributed between two extrema, where a given value is output for a
 * wavelength's worth at a time. This is akin to the traditional "sample
 * and hold" functionality of synthesizers. For fractional wavelengths,
 * extra outputs are output occasionally to approximate the proper
 * wavelength. This may result in obvious quantization noise on simple
 * signals. This is a standard fixed wave module, so its base parameters are
 * described by {@link FixedWaveFactory}. It takes one additional parameter:
 *
 * <dl>
 * <dt>seed: int</dt>
 * <dd>The randomization seed to use.</dd>
 * </dl>
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
final public class FixedRandomWave
extends BaseFixedWave
{
    /** short name */
    private static final String SHORT_NAME = "FixedRandomWave";

    /** factory that creates instances of this class */
    public static final Factory FACTORY = 
	new FixedWaveFactory (SHORT_NAME, FixedRandomWave.class,
			      new String[] { "seed" },
			      new Class[] { Integer.class });

    /** seed for the generator */
    private int mySeed;

    /** amount to multiply each output by */
    private double myMul;

    /** null-ok; actual random number generator, initialized by
     * {@link #bind1} */
    private Random myRandom;

    /** index into the output (i.e., count of outputs of the current value) */
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
    public FixedRandomWave (Factory factory, Template template, Map args)
    {
	super (factory, template, args);

	mySeed = ((Integer) args.get ("seed")).intValue ();
	myMul = (myV1 - myV0);
    }

    // ------------------------------------------------------------------------
    // public methods the superclass requires us to implement

    // interface's comment suffices
    public void tick ()
    {
	double index = myIndex;
	double wlen = myWlen;

	if (index >= wlen)
	{
	    myOutPort.setDouble (myRandom.nextDouble () * myMul + myV0);
	    myIndex = index - wlen + 1.0;
	}
	else
	{
	    myIndex = index + 1.0;
	}
    }

    // ------------------------------------------------------------------------
    // protected methods the superclass requires us to implement

    // superclass's comment suffices
    protected void bind1 ()
    {
	myIndex = myWlen * myPhase;
	if (myIndex < myWlen)
	{
	    // guarantee a new output will occur with the first sample
	    myIndex += myWlen;
	}
	myRandom = new Random (mySeed);
    }

    // superclass's comment suffices
    protected void reset1 ()
    {
	myRandom = null;
    }
}

