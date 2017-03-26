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
 * This is an implementation of a <code>Table</code> as a flat array.
 * No hashing, linear search, etc. It's generally useful for small tables,
 * but performace will no doubt be unacceptable for big tables.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class FlatTable
implements Table
{
    /** the object to use to mark that a particular key-value pair
     * is actually stored as a <code>TableElement</code> */
    private static final Object TheElementMarker = new Object ();

    /** the key predicate to use */
    private ObjectPredicate myKeyPredicate;

    /** the value predicate to use */
    private ObjectPredicate myValuePredicate;

    /** the key comparator to use */
    private Comparator myKeyComparator;

    /** the array of keys and values, stored as pairs of either
     * key-then-value or marker-then-element */
    private Object[] myElements;

    /** the current capacity in elements (i.e.,
     * <code>myElements.size()/2</code>) */
    private int myCapacity;

    /** the current element count */
    private int myCount;

    /**
     * Construct an initially-empty <code>FlatTable</code>. The key and
     * value predicates are, by default, both
     * <code>TruePredicate.TheOne</code>, and the key comparator is, by
     * default, <code>EqComparator.TheOne</code>.
     */
    public FlatTable ()
    {
	myElements = null;
	myCapacity = 0;
	myCount = 0;
	myKeyPredicate = TruePredicate.TheOne;
	myValuePredicate = TruePredicate.TheOne;
	myKeyComparator = EqComparator.TheOne;
    }

    /**
     * Set the key predicate for this table. Note that this will <i>not</i>
     * cause keys already in the table to be suddenly, mysteriously
     * rejected. Beware of changing the predicate in an already-populated
     * table.
     *
     * @param pred the new predicate
     */
    public void setKeyPredicate (ObjectPredicate pred)
    {
	myKeyPredicate = pred;
    }

    /**
     * Set the value predicate for this table. Note that this will
     * <i>not</i> cause values already in the table to be suddenly,
     * mysteriously rejected. Beware of changing the predicate in an
     * already-populated table.
     *
     * @param pred the new predicate 
     */
    public void setValuePredicate (ObjectPredicate pred)
    {
	myValuePredicate = pred;
    }

    /**
     * Set the key comparator for this table. Note that will <i>not</i>
     * cause elements already in the table whose keys suddenly become
     * considered equal to be suddenly, mysteriously merged. Beware of
     * changing the comparator in an already-populated table.
     *
     * @param comp the new comparator
     */
    public void setKeyComparator (Comparator comp)
    {
	myKeyComparator = comp;
    }

    /**
     * Set the capacity of the table to be at least the given number of
     * elements. If the table already has more than that, then this does
     * nothing.
     *
     * @param capacity the target capacity
     */
    public void setCapacity (int capacity)
    {
	if (myCount < capacity)
	{
	    Object[] newElements = new Object[capacity * 2];
	    if (myCount > 0)
	    {
		System.arraycopy (myElements, 0, newElements, 0, myCount * 2);
	    }
	    myElements = newElements;
	    myCapacity = capacity * 2;
	}
    }

    // ------------------------------------------------------------------------
    // Table interface methods

    /**
     * Return the predicate which is used to test keys for validity.
     *
     * @return the key predicate 
     */
    public ObjectPredicate getKeyPredicate ()
    {
	return myKeyPredicate;
    }

    /**
     * Return the predicate which is used to test values for validity. If
     * the value predicate returns true for a particular object, then it is
     * okay to use that object as a value in this table.
     *
     * @return the key predicate 
     */
    public ObjectPredicate getValuePredicate ()
    {
	return myValuePredicate;
    }

    /**
     * Return the comparator which is used to test keys for equality.
     *
     * @return the key comparator
     */
    public Comparator getKeyComparator ()
    {
	return myKeyComparator;
    }

    /**
     * Get the size of the table, that is, the number of key-value
     * mappings the table contains.
     *
     * @return the size of the table
     */
    public int size ()
    {
	return myCount;
    }

    /**
     * Return true if this table is empty. This is generally equivalent
     * to <code>size() == 0</code>.
     *
     * @return true if the table is empty
     */
    public boolean isEmpty ()
    {
	return (myCount == 0);
    }

    /**
     * Return true if the given key is in the table.
     *
     * @param key the key
     * @return true if the key is in the table
     */
    public boolean containsKey (Object key)
    {
	return (findKey (key) != -1);
    }

    /**
     * Get the value for the given key, or return the given
     * <code>notFound</code> value if there is no such key.
     *
     * @param key the key to look up
     * @param notFound the value to return if the key isn't found
     * @return either the value associated with the key or the
     * <code>notFound</code> value if there is no such key 
     */
    public Object get (Object key, Object notFound)
    {
	int nth = findKey (key);

	return (nth == -1) ? notFound : nthValue (nth);
    }

    /**
     * Get a key-value mapping object indicating the actual mapping stored
     * in the table, or return null if there is no such key.
     *
     * @param key the key to look up
     * @return null-ok; either the key-value mapping or null if there is
     * no such key
     */
    public TableElement getElement (Object key)
    {
	int nth = findKey (key);

	return (nth == -1) ? null : nthElement (nth);
    }

    /**
     * Get an <code>Enumeration</code> of all the elements of the table.
     * The enumeration consists only of <code>TableElement</code> objects.
     *
     * @return the enumeration
     */
    public Enumeration elements ()
    {
	return new MyEnumeration ();
    }

    /**
     * Map a key to a value. If the key already mapped to a value, then
     * replace that mapping.
     *
     * @param key the key
     * @param value the value 
     * @return true if the key was found and replaced, false if the
     * operation put a new binding in the table
     */
    public boolean put (Object key, Object value)
    {
	if (! myKeyPredicate.test (key))
	{
	    throw new BadKeyException (key, this);
	}

	if (! myValuePredicate.test (value))
	{
	    throw new BadValueException (value, this);
	}

	int nth = findKey (key);

	if (nth != -1)
	{
	    nth *= 2;
	    myElements[nth] = key;
	    myElements[nth + 1] = value;
	    return true;
	}

	if (myCount == myCapacity)
	{
	    // must grow table; grow by 3/2 to avoid pathological power-of-2
	    // size lossage
	    int newCapacity = (myCount < 10) ? 10 : (myCount * 3 / 2);
	    setCapacity (newCapacity);
	}

	nth = myCount * 2;
	myElements[nth] = key;
	myElements[nth + 1] = value;
	myCount++;
	return false;
    }

    /**
     * Remove a key (and associated value) from the table. If the
     * key wasn't in the table, this doesn't affect the table.
     *
     * @param key the key to remove
     * @return true if the key was found and removed, false if the
     * key wasn't in the table
     */
    public boolean remove (Object key)
    {
	int nth = findKey (key);

	if (nth == -1)
	{
	    return false;
	}

	// if it wasn't the last element, move the current last element
	// into the hole
	int last = (myCount - 1) * 2;
	if (nth != (myCount - 1))
	{
	    int to = nth * 2;
	    myElements[to] = myElements[last];
	    myElements[to + 1] = myElements[last + 1];
	}

	// null out the last element, for gc-friendliness
	myElements[last] = null;
	myElements[last + 1] = null;

	myCount--;
	return true;
    }

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * Find an already-existing key.
     *
     * @param key the key to find
     * @return the index of the key, or -1 if it wasn't found
     */
    private int findKey (Object key)
    {
	for (int i = 0; i < myCount; i++)
	{
	    if (myKeyComparator.compareEquals (nthKey (i), key))
	    {
		return i;
	    }
	}

	return -1;
    }

    /**
     * Get the <code>nth</code> key.
     *
     * @param nth the index to get
     * @return the key at that index
     */
    private Object nthKey (int nth)
    {
	nth *= 2;
	Object result = myElements[nth];
	if (result == TheElementMarker)
	{
	    result = ((StdTableElement) myElements[nth + 1]).getKey ();
	}
	return result;
    }

    /**
     * Get the <code>nth</code> value.
     *
     * @param nth the index to get
     * @return the value at that index
     */
    private Object nthValue (int nth)
    {
	nth *= 2;
	Object key = myElements[nth];
	Object result = myElements[nth + 1];
	if (key == TheElementMarker)
	{
	    result = ((StdTableElement) result).getValue ();
	}

	return result;
    }

    /**
     * Get the <code>nth</code> element.
     *
     * @param nth the index to get
     * @return the element at that index
     */
    private TableElement nthElement (int nth)
    {
	nth *= 2;
	Object key = myElements[nth];
	Object value = myElements[nth + 1];
	TableElement result;
	if (key != TheElementMarker)
	{
	    myElements[nth] = TheElementMarker;
	    myElements[nth + 1] = result = new StdTableElement (key, value);
	}
	else
	{
	    result = (StdTableElement) value;
	}

	return result;
    }

    // ------------------------------------------------------------------------
    // Private helper classes

    /**
     * This is the class used for enumerations handed out to the world
     * via the <code>elements()</code> method.
     */
    private class MyEnumeration
    implements Enumeration
    {
	int myNth = 0;

	public boolean hasMoreElements ()
	{
	    return (myNth < myCount);
	}

	public Object nextElement ()
	{
	    Object result = nthElement (myNth);
	    myNth++;
	    return result;
	}
    }
}
