// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.util.test;

import com.milk.util.Orderable;
import com.milk.util.PriorityQueue;

public class PriorityQueueTest
{
    static public void main (String[] args)
    {
	for (int i = 0; i < 10; i++)
	{
	    doTrial ();
	    System.out.println ("######################################");
	}
    }

    static private void doTrial ()
    {
	PriorityQueue pq = new PriorityQueue ();
	Elem[] elems = new Elem[(int) ((Math.random () * 40) + 12)];
	for (int i = 0; i < elems.length; i++)
	{
	    elems[i] = new Elem ((int) (Math.random () * 100));
	}

	System.out.println ("----------");

	for (int i = 0; i < elems.length; i++)
	{
	    pq.insert (elems[i]);
	}

	System.out.println ("----------");

	for (int i = 0; i < 5; i++)
	{
	    System.out.println ("remove " + elems[i] + ": " + 
				pq.remove (elems[i]));
	}

	System.out.println ("----------");

	for (int i = 0; i < 5; i++)
	{
	    System.out.println ("remove " + elems[i] + ": " + 
				pq.remove (elems[i]));
	}

	System.out.println ("----------");

	for (int i = 5; i < elems.length; i++)
	{
	    System.out.println ("removeFirst: " + pq.removeFirst ());
	}
    }
}

class Elem
implements Orderable
{
    int val;

    public Elem (int v)
    {
	val = v;
	System.out.println ("new elem: " + this);
    }

    public String toString ()
    {
	return "" + val;
    }

    public int compare (Object other)
    {
	int val2 = ((Elem) other).val;

	if (val < val2)
	{
	    return -1;
	}
	if (val > val2)
	{
	    return 1;
	}

	return 0;
    }
}
