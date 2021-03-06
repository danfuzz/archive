package com.milk.plastic.modules.wave;

import com.milk.plastic.iface.Environment;

/**
 * Where all the wave-generator factories are instantiated. This class
 * simply houses a static method that can add the bindings to a given
 * environment.
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
final public class WaveFactories
{
    // ------------------------------------------------------------------------
    // constructors

    /**
     * This class is uninstantiable.
     */
    private WaveFactories ()
    {
	// this space intentionally left blank
    }

    // ------------------------------------------------------------------------
    // static public methods

    /**
     * Add an instance of each of the core factories to the given environment.
     *
     * @param env the environment to add bindings to
     */
    static public void bind (Environment env)
    {
	env.bind ("FixedPulseWave",    FixedPulseWave.FACTORY);
	env.bind ("FixedRandomWave",   FixedRandomWave.FACTORY);
	env.bind ("FixedSawWave",      FixedSawWave.FACTORY);
	env.bind ("FixedSineWave",     FixedSineWave.FACTORY);
	env.bind ("FixedSquareWave",   FixedSquareWave.FACTORY);
	env.bind ("FixedTriangleWave", FixedTriangleWave.FACTORY);
	env.bind ("SawWave",           new SawWaveFactory ());
    }
}
