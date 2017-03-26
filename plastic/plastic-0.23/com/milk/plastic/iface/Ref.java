package com.milk.plastic.iface;

/**
 * Lazy Reference to an object, which may be resolved in a given
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
public interface Ref
{
    /**
     * Return the type restriction of this instance. If this instance
     * doesn't impose a restriction, then this returns
     * <code>Object.class</code>.
     *
     * @return the type restriction 
     */
    public Class getType ();

    /**
     * Return a clone of this instance, except that the type restriction
     * is altered to be the given one.
     *
     * @param type the new type restriction
     * @return the so-modified instance
     */
    public Ref withType (Class type);

    /**
     * Resolve this instance to the object it refers to. This should throw
     * an exception if the resolution cannot be done (as opposed to, e.g.,
     * returning <code>null</code>).
     *
     * @return the object specified by this instance
     * @exception PlasticException thrown if the resolution can't be
     * done 
     */
    public Object resolve ();
}
