package com.milk.plastic.util;

import com.milk.plastic.ports.DoublePort;
import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.Template;
import com.milk.plastic.util.BaseModule;
import com.milk.plastic.util.FixedWaveFactory;
import java.util.Map;

/**
 * Base functionality for fixed wave modules. See {@link FixedWaveFactory}
 * for details about them.
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
abstract public class BaseFixedWave
extends BaseModule
{
    /** first extremum */
    protected final double myV0;

    /** second extremum */
    protected final double myV1;

    /** the phase offset; it is always in the range [0..1] */
    protected final double myPhase;

    /** the wavelength */
    protected final double myWlen;

    /** the output port */
    protected final DoublePort myOutPort;

    // ------------------------------------------------------------------------
    // constructor
    
    /**
     * Construct an instance.
     *
     * @param factory the factory which spawned this instance
     * @param template the full template for this instance
     * @param args the arguments which this instance was constructed from 
     */
    protected BaseFixedWave (Factory factory, Template template, Map args)
    {
	super (factory, template, args);

	myV0 = ((Double) args.get ("v0")).doubleValue ();
	myV1 = ((Double) args.get ("v1")).doubleValue ();
	myWlen = ((Double) args.get ("wlen")).doubleValue ();

	double phase = ((Double) args.get ("phase")).doubleValue () % 1.0;
	myPhase = (phase >= 0.0) ? phase : (phase + 1.0);

	myOutPort = (DoublePort) getField ("out");
    }

    // ------------------------------------------------------------------------
    // public methods that must be overridden by subclasses

    // part of interface
    abstract public void tick ();

    // ------------------------------------------------------------------------
    // protected methods that must be overridden by subclasses

    // superclass's comment suffices
    abstract protected void bind1 ();

    // superclass's comment suffices
    abstract protected void reset1 ();
}

