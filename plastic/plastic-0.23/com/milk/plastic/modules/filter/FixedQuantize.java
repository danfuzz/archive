package com.milk.plastic.modules.filter;

import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.Ref;
import com.milk.plastic.iface.Template;
import com.milk.plastic.ports.DoublePort;
import com.milk.plastic.util.BaseFilter;
import com.milk.plastic.util.FilterFactory;
import java.util.Map;

/**
 * Fixed quantization filter module. Modules of this class take a single
 * input and quantize it to steps of a given size, centered on 0.0.
 * This is a standard form filter module, so its base parameters are
 * described by {@link FilterFactory}. It takes one additional parameter:
 *
 * <dl>
 * <dt>step: double</dt>
 * <dd>The size of each quantized step. Output values are always integral
 * multiples of this number.</dd>
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
final public class FixedQuantize
extends BaseFilter
{
    /** short name */
    private static final String SHORT_NAME = "FixedQuantize";

    /** factory that creates instances of this class */
    public static final Factory FACTORY = 
	new FilterFactory (SHORT_NAME, FixedQuantize.class,
			   new String[] { "step" },
			   new Class[] { Double.class });

    /** quantization step size */
    private double myStep;

    // ------------------------------------------------------------------------
    // constructor
    
    /**
     * Construct an instance.
     *
     * @param factory the factory which spawned this instance
     * @param template the full template for this instance
     * @param args the arguments which this instance was constructed from 
     */
    public FixedQuantize (Factory factory, Template template, Map args)
    {
	super (factory, template, args);
	myStep = ((Double) args.get ("step")).doubleValue ();
    }

    // ------------------------------------------------------------------------
    // public methods the superclass requires us to implement

    // interface's comment suffices
    public void tick ()
    {
	// efficiency dictates that we get a local copy of this
	double step = myStep;

	myOutPort.setDouble (Math.floor (myInPort.getDouble () / step) * step);
    }

    // ------------------------------------------------------------------------
    // protected methods the superclass requires us to implement

    // superclass's comment suffices
    protected void bind2 ()
    {
	// no need to do anything
    }

    // superclass's comment suffices
    protected void reset2 ()
    {
	// no need to do anything
    }
}
