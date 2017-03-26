// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.util;

import java.util.Enumeration;

/**
 * A <code>Table</code> is a mapping from keys to values. How keys are
 * checked for equality is generally up to the particular kind of
 * <code>Table</code>.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public interface Table
{
    /**
     * Return the predicate which is used to test keys for validity. If the
     * key predicate returns true for a particular object, then it is okay
     * to use that object as a key in this table.
     *
     * @return the key predicate 
     */
    public ObjectPredicate getKeyPredicate ();

    /**
     * Return the predicate which is used to test values for validity. If
     * the value predicate returns true for a particular object, then it is
     * okay to use that object as a value in this table.
     *
     * @return the key predicate 
     */
    public ObjectPredicate getValuePredicate ();

    /**
     * Return the comparator which is used to test keys for equality. Note
     * that, when asked to compare two keys and one is already in the
     * table, the one in the table is always the first argument to
     * <code>Comparator.compareEquals()</code>.
     *
     * @return the key comparator 
     */
    public Comparator getKeyComparator ();

    /**
     * Get the size of the table, that is, the number of key-value
     * mappings the table contains.
     *
     * @return the size of the table
     */
    public int size ();

    /**
     * Return true if this table is empty. This is generally equivalent
     * to <code>size() == 0</code>.
     *
     * @return true if the table is empty
     */
    public boolean isEmpty ();

    /**
     * Return true if the given key is in the table.
     *
     * @param key the key
     * @return true if the key is in the table
     */
    public boolean containsKey (Object key);

    /**
     * Get the value for the given key, or return the given
     * <code>notFound</code> value if there is no such key.
     *
     * @param key the key to look up
     * @param notFound the value to return if the key isn't found
     * @return either the value associated with the key or the
     * <code>notFound</code> value if there is no such key 
     */
    public Object get (Object key, Object notFound);

    /**
     * Get a key-value mapping object indicating the actual mapping stored
     * in the table, or return null if there is no such key. The key
     * embodied in the returned <code>TableElement</code> may be different
     * from the given one because a table may use an operation other than
     * <code>==</code> to test for key equality.
     *
     * @param key the key to look up
     * @return null-ok; either the key-value mapping or null if there is
     * no such key
     */
    public TableElement getElement (Object key);

    /**
     * Get an <code>Enumeration</code> of all the elements of the table.
     * The enumeration consists only of <code>TableElement</code> objects.
     * Note that, although some particular tables may allow it, it is
     * generally a bad idea to modify a table while actively enumerating
     * it.
     *
     * @return the enumeration
     */
    public Enumeration elements ();

    /**
     * Map a key to a value. If the key already mapped to a value, then
     * replace that mapping.
     *
     * @param key the key
     * @param value the value 
     * @return true if the key was found and replaced, false if the
     * operation put a new binding in the table
     */
    public boolean put (Object key, Object value);

    /**
     * Remove a key (and associated value) from the table. If the
     * key wasn't in the table, this doesn't affect the table.
     *
     * @param key the key to remove
     * @return true if the key was found and removed, false if the
     * key wasn't in the table
     */
    public boolean remove (Object key);
}
