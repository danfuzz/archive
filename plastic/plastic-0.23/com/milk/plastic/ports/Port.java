package com.milk.plastic.ports;

/**
 * Interface for all ports. The basic stipulation is that any port be
 * able to yield its value as an object and set its value from an object
 * (assuming the type is appropriate).
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
public interface Port
{
    /**
     * Get the string form of the value. This should be approximately
     * equivalent to <code>getValue().toString()</code>.
     *
     * @return the value as a string
     */
    public String getString ();

    /**
     * Get the value as an object.
     *
     * @return the value as an object
     */
    public Object getValue ();

    /**
     * Set the value from an object. This is allowed to throw an
     * arbitrary {@link RuntimeException} if the given object
     * can't be coerced to the type required by this instance.
     * Note particularly that a given instance should accept as
     * a value another instance of the same class, so that it can
     * efficiently grab the value from the other instance without
     * resorting to object wrapping.
     *
     * @param value the new value as an object
     */
    public void setValue (Object value);
}
