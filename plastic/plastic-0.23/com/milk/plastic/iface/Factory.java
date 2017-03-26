package com.milk.plastic.iface;

import java.util.Map;

/**
 * Interface for things that can instantiate objects.
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
public interface Factory
{
    /**
     * Return the short name for the type of objects produced by this instance.
     *
     * @return the short name 
     */
    public String getShortName ();

    /**
     * Return a description of the base instantiation parameters for this
     * factory. That is, this describes the possible base arguments to
     * {@link #make} and {@link #getInstantiationTemplate}.
     *
     * @return the template for instantiation 
     */
    public Template getFactoryTemplate ();

    /**
     * Given a set of base instantiation arguments, return the template for
     * the full set of instantiation arguments needed to pass to a call to
     * {@link #make}. The given arguments should conform to the template
     * returned by {@link #getFactoryTemplate}. The result should contain
     * the original base arguments, plus a set of fields for the extra
     * inputs that are allowed for the given set of base arguments.
     *
     * @param args the base instantiation arguments, as a <code>Map</code> from
     * names to argument values
     * @return a template for the full instantiation arguments
     * @exception PlasticException thrown if there is a problem
     * with the arguments 
     */
    public Template getInstantiationTemplate (Map args)
	throws PlasticException;

    /**
     * Given a set of base instantiation arguments, return the template for
     * the full set of fields on the object that would result from a call
     * to {@link #make}. The given arguments should conform to the template
     * returned by {@link #getInstantiationTemplate}. The result should
     * contain the original instantiation arguments, plus any extra to
     * represent output fields of the object. The way the output fields
     * are accessed is dependent on the actual class of the instances
     * produced by this factory.
     * 
     * @param args the instantiation arguments, as a <code>Map</code> from
     * names to argument values
     * @return the module configuration
     * @exception PlasticException thrown if there is a problem
     * with the arguments 
     */
    public Template getObjectTemplate (Map args)
	throws PlasticException;

    /**
     * Instantiate an object with the given arguments. The given arguments
     * should conform to the template returned by {@link
     * #getInstantiationTemplate}.
     *
     * @param args the instantiation arguments, as a <code>Map</code> from
     * names to argument values
     * @return an appropriately-instantiated module
     * @exception PlasticException thrown if there is a problem
     * with the instantiation 
     */
    public Object make (Map args)
	throws PlasticException;
}
