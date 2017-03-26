package com.milk.plastic.modules.wave;

import com.milk.plastic.ports.DoublePort;
import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.Template;
import com.milk.plastic.util.BaseFixedWave;
import com.milk.plastic.util.FixedWaveFactory;
import java.util.Map;

/**
 * Fixed square wave module. These are modules which provide a single
 * output of a wave of a given wavelength, consisting of two given values
 * alternated between in two equal halves. For fractional wavelengths,
 * extra outputs of each of the two values are output occasionally to
 * approximate the proper wavelength. This may result in obvious
 * quantization noise on simple signals. This is a standard fixed wave
 * module, so its parameters are described by {@link FixedWaveFactory}.
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
final public class FixedSquareWave
extends BaseFixedWave
{
    /** short name */
    private static final String SHORT_NAME = "FixedSquareWave";

    /** factory that creates instances of this class */
    public static final Factory FACTORY = 
	new FixedWaveFactory (SHORT_NAME, FixedSquareWave.class);

    /** half wavelength (i.e., the number of times to output each value
     * in a row) */
    private double myHalfWlen;

    /** offset into the half-wave of v0 values (i.e., count of outputs) */
    private double myV0Offset;

    /** offset into the half-wave of v1 values (i.e., count of outputs) */
    private double myV1Offset;

    /** <code>true</code> if the <code>v0</code> should be output next */
    private boolean myUseV0;

    // ------------------------------------------------------------------------
    // constructor
    
    /**
     * Construct an instance.
     *
     * @param factory the factory which spawned this instance
     * @param template the full template for this instance
     * @param args the arguments which this instance was constructed from 
     */
    public FixedSquareWave (Factory factory, Template template, Map args)
    {
	super (factory, template, args);

	myHalfWlen = myWlen / 2.0;
    }

    // ------------------------------------------------------------------------
    // public methods the superclass requires us to implement

    // interface's comment suffices
    public void tick ()
    {
	// we use local copies for the sake of efficiency
	double halfWlen = myHalfWlen;
	double nextValue;

	// this arrangement guarantees that we insert extra outputs
	// evenly for both values; also note that we only call setDouble()
	// when the value changes

	if (myUseV0)
	{
	    nextValue = myV0Offset + 1.0;
	    if (nextValue < halfWlen)
	    {
		myV0Offset = nextValue;
	    }
	    else
	    {
		myV0Offset = nextValue - halfWlen;
		myUseV0 = false;
		myOutPort.setDouble (myV1);
	    }
	}
	else
	{
	    nextValue = myV1Offset + 1.0;
	    if (nextValue < halfWlen)
	    {
		myV1Offset = nextValue;
	    }
	    else
	    {
		myV1Offset = nextValue - halfWlen;
		myUseV0 = true;
		myOutPort.setDouble (myV0);
	    }
	}
    }

    // ------------------------------------------------------------------------
    // protected methods the superclass requires us to implement

    // superclass's comment suffices
    protected void bind1 ()
    {
	if (myPhase < 0.5)
	{
	    myV0Offset = myPhase * myHalfWlen * 2;
	    myV1Offset = myHalfWlen;
	    myUseV0 = false;
	}
	else
	{
	    myV0Offset = myHalfWlen;
	    myV1Offset = (myPhase - 0.5) * myHalfWlen * 2;
	    myUseV0 = true;
	}
    }

    // superclass's comment suffices
    protected void reset1 ()
    {
	// no need to do anything
    }
}

