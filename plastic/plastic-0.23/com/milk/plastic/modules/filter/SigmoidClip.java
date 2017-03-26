package com.milk.plastic.modules.filter;

import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.Template;
import com.milk.plastic.util.BaseFilter;
import com.milk.plastic.util.FilterFactory;
import java.util.Map;

/**
 * Sigmoid clipping module. For each input <code>in</code>, it produces an
 * output which has been clipped to the range [-1..1], but instead of
 * simply hard-clipping at -1 and 1, it passes inputs through a sigmoid
 * function with the given <i>a</i> factor. This is a standard form filter
 * module, so its base parameters are described by {@link FilterFactory}.
 * It takes one additional parameter:
 *
 * <dl>
 * <dt>a: double</dt>
 * <dd>The <i>a</i> factor for the sigmoid. Smaller values of <i>a</i> make
 * for a gentler slope. Larger values make the output look increasingly
 * like a square wave. Useful values for <i>a</i> are in the range [0.25 ..
 * 4].</dd>
 * </dl>
 *
 * <p>This is the definition for a sigmoid function:</p>
 *
 * <table align=center cellpadding=0 cellspacing=0>
 * <tr>
 *   <td align=center valign=center rowspan=3><nobr><i>f(x)</i> = </nobr></td>
 *   <td align=center>1</td>
 * </tr>
 * <tr><td align=center><hr width=40></td></tr>
 * <tr><td align=center><nobr>1 + e<sup>-<i>ax</i></sup></nobr></td></tr>
 * </table>
 *
 * <p>As stated, it produces values in the range [0..1], so to turn it into
 * a filter, it's scaled and biased so as to be centered on the value
 * 0.</p>
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
final public class SigmoidClip
extends BaseFilter
{
    /** short name */
    private static final String SHORT_NAME = "SigmoidClip";

    /** factory that creates instances of this class */
    public static final Factory FACTORY = 
	new FilterFactory (SHORT_NAME, SigmoidClip.class,
			   new String[] { "a" },
			   new Class[] { Double.class });

    /** the negative of the <i>a</i> factor of the sigmoid function */
    private double myNegA;

    // ------------------------------------------------------------------------
    // constructor
    
    /**
     * Construct an instance.
     *
     * @param factory the factory which spawned this instance
     * @param template the full template for this instance
     * @param args the arguments which this instance was constructed from 
     */
    public SigmoidClip (Factory factory, Template template, Map args)
    {
	super (factory, template, args);
	myNegA = - ((Double) args.get ("a")).doubleValue ();
    }

    // ------------------------------------------------------------------------
    // public methods the superclass requires us to implement

    // interface's comment suffices
    public void tick ()
    {
	// note: possible optimization would be to turn myNegA into
	// myNegADividedBy2, folding in the halving of the original value

	// add one and divide by 2 to map [-1..1] to [0..1]
	double value = (myInPort.getDouble () + 1.0) / 2.0;

	value = (1.0 / ( 1.0 + Math.exp (myNegA * value)));

	// reverse the adjustment to map [0..1] back to [-1..1]
	myOutPort.setDouble (value * 2.0 - 1.0);
    }

    // ------------------------------------------------------------------------
    // protected methods the superclass requires us to implement

    // superclass's comment suffices
    protected void bind2 ()
    {
	// nothing to do
    }

    // superclass's comment suffices
    protected void reset2 ()
    {
	// nothing to do
    }
}
