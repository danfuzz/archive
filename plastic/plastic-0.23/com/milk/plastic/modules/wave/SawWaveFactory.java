package com.milk.plastic.modules.wave;

import com.milk.plastic.iface.FieldRef;
import com.milk.plastic.iface.Module;
import com.milk.plastic.iface.Template;
import com.milk.plastic.ports.DoublePort;
import com.milk.plastic.util.BaseFactory;
import java.util.Map;

/**
 * A factory for variable-wavelength saw wave modules. These are modules
 * which provide a single output of a sawtooth wave of a variable
 * wavelength, with the two given values as the extrema; outputs progress
 * smoothly from <code>v0</code> to <code>v1</code>.
 *
 * <p>The base arguments for this factory are:</p>
 *
 * <dl>
 * <dt><code>v0: double</code></dt>
 * <dd>The first extremum of the output.</dd>
 * <dt><code>v1: double</code></dt>
 * <dd>The second extremum of the output.</dd>
 * <dt><code>in_wlen: double port</code></dt>
 * <dd>The wavelength of the output.</dd>
 * </dl>
 *
 * <p>The resulting modules always have a single output port named
 * <code>out</code>.</p>
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
final public class SawWaveFactory
extends BaseFactory
{
    /** short name */
    private static final String SHORT_NAME = "SawWave";

    /** base template */
    private static final Template TEMPLATE =
	new Template (new String[] { "v0", "v1", "in_wlen" },
		      new Class[] { Double.class, Double.class, 
                                    FieldRef.class });

    /** full template */
    private static final Template FULL_TEMPLATE =
	TEMPLATE.withMore (new String[] { "out", },
			   new Class[] { DoublePort.class });

    // ------------------------------------------------------------------------
    // constructor
    
    public SawWaveFactory ()
    {
	super (SHORT_NAME, TEMPLATE);
    }

    // ------------------------------------------------------------------------
    // methods the superclass requires us to implement

    // superclass's comment suffices
    protected Template getObjectTemplate1 (Map args)
    {
	return FULL_TEMPLATE;
    }

    // superclass's comment suffices
    protected Object make1 (Template template, Map args)
    {
	return new SawWave (this, template, args);
    }
}

