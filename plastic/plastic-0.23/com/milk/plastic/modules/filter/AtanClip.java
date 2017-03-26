package com.milk.plastic.modules.filter;

import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.Template;
import com.milk.plastic.util.BaseFilter;
import com.milk.plastic.util.FilterFactory;
import java.util.Map;

/**
 * Atan clipping module. For each input <code>in</code>, it produces an
 * output which has been clipped to the range [-1..1], but instead of
 * simply hard-clipping at -1 and 1, it actually just passes inputs
 * through the filter function <code>atan (in*(pi/2)) / (pi/2)</code>.
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
final public class AtanClip
extends BaseFilter
{
    /** the handy constant PI/2 */
    private static final double HALF_PI = Math.PI / 2.0;

    /** short name */
    private static final String SHORT_NAME = "AtanClip";

    /** factory that creates instances of this class */
    public static final Factory FACTORY = 
	new FilterFactory (SHORT_NAME, AtanClip.class);

    // ------------------------------------------------------------------------
    // constructor
    
    /**
     * Construct an instance.
     *
     * @param factory the factory which spawned this instance
     * @param template the full template for this instance
     * @param args the arguments which this instance was constructed from 
     */
    public AtanClip (Factory factory, Template template, Map args)
    {
	super (factory, template, args);
    }

    // ------------------------------------------------------------------------
    // public methods the superclass requires us to implement

    // interface's comment suffices
    public void tick ()
    {
	myOutPort.setDouble (
            Math.atan (myInPort.getDouble () * HALF_PI) / HALF_PI);
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
