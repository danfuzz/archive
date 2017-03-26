package com.milk.plastic.iface;

import java.util.Map;

/**
 * An active module. Instances of this interface are typically produced via
 * calls to {@link Factory#make}. Modules have some number of inputs and
 * outputs, and their job is to, when asked, produce a fresh set of outputs
 * based on the values currently at their inputs. The way this works is
 * that output ports are created internally to a module, but it is handed
 * its oinput ports. An external entity constructs the modules and hooks
 * them up by calling {@link #setInputPort} on the appropriate modules,
 * handing them ports received from previous calls to {@link
 * #getOutputPort}. Once the network has been set up, over and over, the
 * system calls {@link #tick} on each module in an appropriate order (the
 * modules will have been sorted topologically so that modules are ticked
 * before the modules they provide output to). The entire network should
 * have no explicit "input holes;" that is, all inputs come from some other
 * module's outputs (so, for example, the way that "real world" input gets
 * into the system is effectively that a module internally generates an
 * output). Note that "output holes" are allowed; that is, it's okay if a
 * particular output never gets hooked up to any inputs. Also, there must
 * be no explicit cycles in the network. (The way that feedback works is to
 * have two modules, one on the source side and one on the sink side,
 * introducing an explicit propagation delay and hence breaking the cycle.)
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
public interface Module
{
    /**
     * Get the factory which spawned this module.
     *
     * @return the factory
     */
    public Factory getFactory ();

    /**
     * Get the arguments which were used to instantiate this module. In
     * general, the return value should be unrelated to the actual inner
     * workings of the module (that is, modifying the return value
     * shouldn't cause the module to change its behavior, including, for
     * example, changing what it returns from a subsequent call to this
     * method) or should simply appear to be unmodifiable.
     *
     * @return the arguments 
     */
    public Map getArguments ();

    /**
     * Get the template describing this module's fields, including its
     * arguments, inputs and outputs.
     *
     * @return the template
     */
    public Template getTemplate ();

    /**
     * Get the <code>n</code>th field of this module, where the number
     * is based on the order given in the template for this instance.
     *
     * @param n which field to get
     * @return the <code>n</code>th field
     */
    public Object getField (int n);

    /**
     * Get the named field of this module.
     *
     * @param name the name of the field to get
     * @return the so-named field
     */
    public Object getField (String name);

    /**
     * Tell this instance to bind its input ports to the appropriate output
     * ports of other modules. This will get called before the first time
     * {@link #tick} is called on this instance.
     */
    public void bind ();

    /**
     * Run a single iteration of this module. This means taking the
     * combination of the current values present at the input ports with
     * the current internal state of the module and both producing a
     * fresh set of outputs and updating the internal state.
     */
    public void tick ();

    /**
     * Reset this module to the state it was in immediately after
     * instantiation.
     */
    public void reset ();
}
