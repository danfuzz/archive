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

import java.util.NoSuchElementException;

/**
 * <code>PriorityQueue</code> is just an implementation of the classic
 * priority queue data structure. It stores elements which must be
 * orderable with respect to each other (via some <code>Orderer</code>).
 * The queue can yield the stored elements in order. This class doesn't do
 * any synchronization, so if instances are accessed from distinct threads,
 * care should be taken when inserting and removing elements from the
 * instance.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public class PriorityQueue
{
    /** the current capacity of the underlying array */
    private int myMaxSize;

    /** the number of elements actually in the queue right now */
    private int myCurSize;

    /** the array of elements */
    private Object[] myElements;

    /** the <code>Orderer</code> to use to compare elements */
    private Orderer myOrderer;

    /**
     * Construct an empty <code>PriorityQueue</code>, with the given
     * initial capacity and the given <code>Orderer</code>.
     *
     * @param initalCapacity the initial capacity
     * @param orderer the orderer to use 
     */
    public PriorityQueue (int initialCapacity, Orderer orderer) 
    {
        myMaxSize = initialCapacity;
	myOrderer = orderer;
        myElements = new Object[initialCapacity];
        myCurSize = 0;
    }

    /**
     * Construct an empty <code>PriorityQueue</code>, with the given
     * initial capacity and the default <code>Orderer</code> (the
     * <code>StandardOrderer</code>).
     *
     * @param initalCapacity the initial capacity 
     */
    public PriorityQueue (int initialCapacity) 
    {
	this (initialCapacity, StandardOrderer.TheOne);
    }

    /**
     * Construct an empty <code>PriorityQueue</code>, with the default
     * initial capacity--namely, 100 elements--and the default
     * <code>Orderer</code> (the <code>StandardOrderer</code>). 
     */
    public PriorityQueue () 
    {
        this (100, StandardOrderer.TheOne);
    }
    
    /**
     * Insert a new element. It is okay to have equal-ordered elements,
     * but the order of retrieval of such elements is arbitrary.
     *
     * @param obj the element to add
     */ 
    public void insert (Object obj) 
    {
        if (myCurSize == myMaxSize) 
	{
            // we must grow the array of elements;
            // grow by 3/2 to avoid pathological allocation wastage
            int newSize = (myMaxSize * 3) / 2;
            Object newElements[] = new Object[newSize];
            System.arraycopy (myElements, 0, newElements, 0, myMaxSize);
            myElements = newElements;
            myMaxSize = newSize;
        }
        
        // start by assuming the element is the last one, and
        // then swap elements down if it turns out that the
        // assumption is false (classic heap-as-complete-binary-tree
        // implementation).
        int i = myCurSize;
        for (;;) 
	{
            if (i == 0) 
	    {
                // we've hit the top of the heap
                break;
            }
            
            int parent = (i + 1) / 2 - 1;

            if (myOrderer.compare (myElements[parent], obj) < 0) 
	    {
                // the heap invariant is now satisfied
                break;
            }
            
            myElements[i] = myElements[parent];
            i = parent;
        }
        
        myElements[i] = obj;
        myCurSize++;
    }
    
    /**
     * Peek at the first element in the queue.
     *
     * @return the first element
     * @exception NoSuchElementException thrown if the queue is empty
     */
    public Object peekFirst ()
	throws NoSuchElementException 
    {
        if (myCurSize == 0) 
	{
            throw new NoSuchElementException ("Empty queue");
        }

        return myElements[0];
    }
    
    /**
     * Remove and return the first value in the queue. 
     *
     * @return the (former) first element
     * @exception NoSuchElementException thrown if the queue is empty
     */
    public Object removeFirst ()
	throws NoSuchElementException 
    {
        if (myCurSize == 0) 
	{
            throw new NoSuchElementException ("Empty queue");
        }
        
        Object result = myElements[0];

	if (myCurSize == 1)
	{
	    // it was the only element. easy! just set sizes etc.
	    myCurSize = 0;
	    myElements[0] = null; // gc-friendly
	    return result;
	}

	// move the formerly-last element to the top and fix the
	// heap condition
	// (classic heap-as-complete-binary-tree implementation).
        myCurSize--;
	myElements[0] = myElements[myCurSize];
	myElements[myCurSize] = null; // gc-friendly
	fixHeap (0);

        return result;
    }

    /**
     * Remove the given element (tested with <code>==</code>) from the
     * queue. This does nothing if the element isn't in the queue to begin
     * with.
     *
     * @param obj the element to remove
     * @return true if the element was found and removed, false if the
     * element wasn't found 
     */
    public boolean remove (Object obj)
    {
	int at;
	for (at = 0; at < myCurSize; at++)
	{
	    if (myElements[at] == obj)
	    {
		break;
	    }
	}

	if (at == myCurSize)
	{
	    // specified element wasn't in the queue
	    return false;
	}

	if (myCurSize == 1)
	{
	    // it was the only element. easy! just set sizes etc.
	    myCurSize = 0;
	    myElements[0] = null; // gc-friendly
	    return true;
	}

	// we found the element. now assume that the formerly-last element
	// belongs in place of the one to be removed, and then swap elements
	// up if it turns out that the assumption is false
	// (classic heap-as-complete-binary-tree implementation).
        myCurSize--;
	myElements[at] = myElements[myCurSize];
	myElements[myCurSize] = null; // gc-friendly
	fixHeap (at);
	return true;
    }

    /**
     * Helper method to fix the heap condition, starting at the given
     * index, where an element <i>may be</i> (but is not necessarily) out of
     * place.
     *
     * @param at where to start monkeying
     */
    private void fixHeap (int at)
    {
	// first get the element to be moved down
	Object element = myElements[at];

	// now, starting with the specified "at", see if the element there
	// is "less" than both of its kids. if not, swap the lesser of the
	// kids, and repeat with the new-possibly-bad subtree
	for (;;) 
	{
	    int kid1 = (at + 1) * 2;
	    int kid = kid1 - 1;
	    
	    if (kid >= myCurSize) 
	    {
		// we've hit the bottom of the heap
		break;
	    }
	    
	    // always compare to the "least" of the two kids
	    if (   (kid1 < myCurSize)
		&& (myOrderer.compare (myElements[kid], myElements[kid1]) 
		    > 0))
	    {
		kid = kid1;
	    }
	    
	    if (myOrderer.compare (myElements[kid], element) > 0) 
	    {
		// the heap condition is now satisfied
		break;
	    }
	    
	    myElements[at] = myElements[kid];
	    at = kid;
	}
	
	// store the former last-element in its proper place
	myElements[at] = element;
    }
    
    /**
     * Get the current size of the queue.
     *
     * @return the current size of the queue
     */
    public int size () 
    {
        return myCurSize;
    }
}
