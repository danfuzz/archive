package com.milk.plastic.modules.filter;

import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.Ref;
import com.milk.plastic.iface.Template;
import com.milk.plastic.ports.DoublePort;
import com.milk.plastic.util.BaseFilter;
import com.milk.plastic.util.FilterFactory;
import java.util.Map;

/**
 * Auto-bias module. Instances take a single input and adjust it so
 * that the average of all the outputs over a given number of ticks
 * is 0.0. This is a standard form filter module, so its
 * base parameters are described by {@link FilterFactory}. It takes one
 * additional parameter:
 *
 * <dl>
 * <dt><code>period: integer</code></dt>
 * <dd>The number of ticks to bias over.</dd>
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
final public class AutoBias
extends BaseFilter
{
    /** short name */
    private static final String SHORT_NAME = "AutoBias";

    /** factory that creates instances of this class */
    public static final Factory FACTORY = 
	new FilterFactory (SHORT_NAME, AutoBias.class,
			   new String[] { "period" },
			   new Class[] { Integer.class });

    /** period in ticks over which to perform adjustment */
    private int myPeriod;

    /** period as a floating point */
    private double myPeriodFP;

    /** null-ok; array containing the history of unmodified inputs */
    private double[] myBuf;

    /** current sum of inputs in the buffer */
    private double mySum;

    /** where in the buffer to place the next input value */
    private int myIndex;

    // ------------------------------------------------------------------------
    // constructor
    
    /**
     * Construct an instance.
     *
     * @param factory the factory which spawned this instance
     * @param template the full template for this instance
     * @param args the arguments which this instance was constructed from 
     */
    public AutoBias (Factory factory, Template template, Map args)
    {
	super (factory, template, args);

	myPeriod = ((Integer) args.get ("period")).intValue ();
	myPeriodFP = (double) myPeriod;
    }

    // ------------------------------------------------------------------------
    // public methods the superclass requires us to implement

    // interface's comment suffices
    public void tick ()
    {
	// use local copies for efficiency
	double sum = mySum;
	int index = myIndex;
	double[] buf = myBuf;

	double value = myInPort.getDouble ();

	myOutPort.setDouble (value - (sum / myPeriodFP));
	mySum = sum - buf[index] + value;
	buf[index] = value;

	myIndex = (index + 1) % myPeriod;
    }

    // ------------------------------------------------------------------------
    // protected methods the superclass requires us to implement

    // superclass's comment suffices
    protected void bind2 ()
    {
	myBuf = new double[myPeriod];
	mySum = 0.0;
	myIndex = 0;
    }

    // superclass's comment suffices
    protected void reset2 ()
    {
	myBuf = null;
    }
}
