package com.milk.plastic.modules.wave;

import com.milk.plastic.ports.DoublePort;
import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.Template;
import com.milk.plastic.util.BaseFixedWave;
import com.milk.plastic.util.FixedWaveFactory;
import java.util.Map;

/**
 * Fixed pulse wave module. These are modules which provide a single output
 * of a wave of a given wavelength, consisting of two given values
 * alternated between in proportion to the given pulse width. This is a
 * standard fixed wave module, so most of its parameters are described by
 * {@link FixedWaveFactory}. It additionally adds this parameter:
 *
 * <dl>
 * <dt><code>width: double</code></dt>
 * <dd>The width of the pulse as a fraction [0..1] of the total
 * wavelength.</dd>
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
final public class FixedPulseWave
extends BaseFixedWave
{
    /** short name */
    private static final String SHORT_NAME = "FixedPulseWave";

    /** factory that creates instances of this class */
    public static final Factory FACTORY = 
	new FixedWaveFactory (SHORT_NAME, FixedPulseWave.class,
			      new String[] { "width" },
			      new Class[] { Double.class });

    /** the index after which to use <code>v1</code>; that is, the
     * pulse width times the wavelength */
    private double mySwitchIndex;

    /** current index into wave */
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
    public FixedPulseWave (Factory factory, Template template, Map args)
    {
	super (factory, template, args);

	double width = ((Double) args.get ("width")).doubleValue ();

	mySwitchIndex = width * myWlen;
    }

    // ------------------------------------------------------------------------
    // public methods the superclass requires us to implement

    // interface's comment suffices
    public void tick ()
    {
	// we use local copies for the sake of efficiency
	double wlen = myWlen;
	double index = myIndex;

	myOutPort.setDouble ((index < mySwitchIndex) ? myV0 : myV1);

	index++;
	if (index >= wlen)
	{
	    myIndex = index - wlen;
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
	myIndex = myPhase * myWlen;
    }

    // superclass's comment suffices
    protected void reset1 ()
    {
	// no need to do anything
    }
}

