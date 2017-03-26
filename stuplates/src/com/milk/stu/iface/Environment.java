// Copyright (c) 2002 Dan Bornstein, danfuzz@milk.com. All rights 
// reserved, except as follows:
// 
// Permission is hereby granted, free of charge, to any person obtaining a
// copy of this software and associated documentation files (the "Software"),
// to deal in the Software without restriction, including without limitation
// the rights to use, copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons to whom the
// Software is furnished to do so, subject to the condition that the above
// copyright notice and this permission notice shall be included in all copies
// or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// DEALINGS IN THE SOFTWARE.

package com.milk.stu.iface;

import java.util.Map;

/**
 * Interface for environments (mappings from names to slots). Names passed
 * to methods in this interface <i>should</i> be interned. If not, there is
 * no guarantee that same names that happen to be different objects will
 * be considered equal.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public interface Environment
{
    /**
     * Define a new variable. This will throw if the name is already bound.
     *
     * @param name non-null; the name of the variable
     * @return non-null; the variable's slot
     */
    public Slot define (Identifier name);

    /**
     * Define the named variable if not already defined directly in this
     * instance, and assign it the given value. This always overrides a
     * definition in the parent scope, if any.
     *
     * @param name non-null; the name of the variable
     * @param value null-ok; the value for the variable 
     * @return non-null; the variable's slot
     */
    public Slot defineAlways (Identifier name, Object value);

    /**
     * Get the slot associated with the given variable name if it is
     * defined or <code>null</code> if the name is not bound.
     *
     * @param name non-null; the name of the variable
     * @return null-ok; the variable's slot or <code>null</code> if there
     * is no such variable
     */
    public Slot getSlotIfDefined (Identifier name);

    /**
     * Get the slot associated with the given variable name.
     * This will throw if the name is not bound.
     *
     * @param name non-null; the name of the variable
     * @return non-null; the variable's slot
     */
    public Slot getSlot (Identifier name);

    /**
     * Get the value in the slot associated with the given variable
     * name. This will throw if the name is not bound.
     *
     * @param name non-null; the name of the variable
     * @return null-ok; the variable's value
     */
    public Object getValue (Identifier name);

    /**
     * Make and return a new instance that is of the same class as this
     * one, but with an empty set of bindings and this instance as its parent.
     *
     * @return non-null; an appropriately-constructed instance
     */
    public Environment makeChild ();

    /**
     * Make and return a new instance that is of the same class as this
     * one, but with an empty set of bindings and this instance as its parent.
     *
     * @param initialCapacity >= 0; a hint about the expected number of
     * variable bindings that the new instance will contain
     * @return non-null; an appropriately-constructed instance
     */
    public Environment makeChild (int initialCapacity);

    /** 
     * Put the bindings for this instance, including bindings in this
     * instance's parent (if any), into the given map.
     *
     * @param map non-null; the map to put stuff in
     */
    public void putBindingsIn (Map map);
}
