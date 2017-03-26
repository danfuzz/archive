package com.milk.plastic.modules.math;

import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.Template;
import com.milk.plastic.util.BaseFilter;
import com.milk.plastic.util.FilterFactory;
import java.util.Map;

/**
 * Log module. For each input <code>in</code>, it produces the
 * output <code>log (in)</code>, that is, the natural log of the
 * given number.
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
final public class Log
extends BaseFilter
{
    /** short name */
    private static final String SHORT_NAME = "Log";

    /** factory that creates instances of this class */
    public static final Factory FACTORY = 
	new FilterFactory (SHORT_NAME, Log.class);

    // ------------------------------------------------------------------------
    // constructor
    
    /**
     * Construct an instance.
     *
     * @param factory the factory which spawned this instance
     * @param template the full template for this instance
     * @param args the arguments which this instance was constructed from 
     */
    public Log (Factory factory, Template template, Map args)
    {
	super (factory, template, args);
    }

    // ------------------------------------------------------------------------
    // public methods the superclass requires us to implement

    // interface's comment suffices
    public void tick ()
    {
	myOutPort.setDouble (Math.log (myInPort.getDouble ()));
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
