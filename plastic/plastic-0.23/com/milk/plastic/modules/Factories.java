package com.milk.plastic.modules;

import com.milk.plastic.iface.Environment;
import com.milk.plastic.modules.audio.AudioFactories;
import com.milk.plastic.modules.base.BaseFactories;
import com.milk.plastic.modules.filter.FilterFactories;
import com.milk.plastic.modules.math.MathFactories;
import com.milk.plastic.modules.wave.WaveFactories;
import com.milk.plastic.ports.DoublePort;

/**
 * Where all the core factories are instantiated. This class simply houses
 * a static method that can add the core factory bindings to a given
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
final public class Factories
{
    // ------------------------------------------------------------------------
    // constructors

    /**
     * This class is uninstantiable.
     */
    private Factories ()
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
    static public void bindCore (Environment env)
    {
	// type names
	env.bind ("DoublePort", DoublePort.class);

	// subpackages
	AudioFactories.bind (env);
	BaseFactories.bind (env);
	FilterFactories.bind (env);
	MathFactories.bind (env);
	WaveFactories.bind (env);

	// factories
	env.bind ("Pan", Pan.FACTORY);
    }
}
