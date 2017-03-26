package com.milk.plastic.util;

import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.Ref;
import com.milk.plastic.iface.Template;
import com.milk.plastic.ports.DoublePort;
import com.milk.plastic.util.BaseModule;
import java.util.Map;

/**
 * Base functionality for filter modules. See {@link FilterFactory} for
 * more information about these.
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
abstract public class BaseFilter
extends BaseModule
{
    /** the output port to use. This is initialized by the superclass's
     * constructor. */
    protected final DoublePort myOutPort;

    /** null-ok; the input to sample, set by {@link #bind} and
     * <code>null</code>ed out by {@link #reset} */
    protected DoublePort myInPort;

    // ------------------------------------------------------------------------
    // constructor
    
    /**
     * Construct an instance.
     *
     * @param factory the factory which spawned this instance
     * @param template the full template for this instance
     * @param args the arguments which this instance was constructed from 
     */
    protected BaseFilter (Factory factory, Template template, Map args)
    {
	super (factory, template, args);

	// augment the type restriction on the input
	Ref newRef = ((Ref) getField ("in")).withType (DoublePort.class);
	setField ("in", newRef);

	// get the sole output
	myOutPort = (DoublePort) getField ("out");
    }

    // ------------------------------------------------------------------------
    // public methods that subclasses must implement

    // interface's comment suffices
    abstract public void tick ();

    // ------------------------------------------------------------------------
    // protected methods that must be overridden by subclasses

    /**
     * Subclass's chance to perform <code>bind</code>-time operations.
     * This is called by {@link #bind1} after the input port has been
     * bound.
     */
    abstract protected void bind2 ();

    /**
     * Subclass's chance to perform <code>reset</code>-time operations.
     * This is called by {@link #reset1} after it does its own resetting.
     */
    abstract protected void reset2 ();

    // ------------------------------------------------------------------------
    // protected methods the superclass requires us to implement

    // superclass's comment suffices
    protected final void bind1 ()
    {
	myInPort = (DoublePort) getBoundObject ("in");
	bind2 ();
    }

    // superclass's comment suffices
    protected final void reset1 ()
    {
	myInPort = null;
	reset2 ();
    }
}
