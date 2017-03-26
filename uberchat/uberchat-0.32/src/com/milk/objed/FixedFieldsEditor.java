// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.objed;

import com.milk.objed.event.EditorEvent;
import com.milk.objed.event.EditorListener;
import java.util.Vector;

/**
 * This is an implementation of <code>FieldsEditor</code> which
 * keeps a fixed list of fields. It is considered immutable only if
 * all of its fields are immutable. It also has a public method to
 * change the description.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class FixedFieldsEditor
extends BaseFieldsEditor
{
    /** the array of fields */
    private Editor[] myFields;

    /** the listener to use on the fields */
    private EditorListener myListener;

    /** the count of the fields which are mutable */
    private int myMutableCount;

    /**
     * Construct a <code>FixedFieldsEditor</code> with the given
     * array of fields.
     *
     * @param label the label
     * @param description the initial description
     * @param fields the array of fields
     */
    public FixedFieldsEditor (String label, String description, 
			      Editor[] fields)
    {
	// note, we *really* want to call super after having figured out
	// the mutability, but Java won't let us do anything in a constructor
	// before the call to super(). It just sucks that way sometimes.
	super (label, description, true);
	setupVariables (fields);
    }

    /**
     * Construct a <code>FixedFieldsEditor</code> from a
     * <code>Vector</code> of sub-editors.
     *
     * @param label the label
     * @param description the initial description
     * @param fields the <code>Vector</code> of fields
     */
    public FixedFieldsEditor (String label, String description, Vector fields)
    {
	// see note above re: Java sucks.
	super (label, description, true);
	Editor[] orig = new Editor[fields.size ()];
	fields.copyInto (orig);
	setupVariables (orig);
    }

    // ------------------------------------------------------------------------
    // FieldsEditor interface methods

    /**
     * Return the array of <code>Editor</code>s that this object contains.
     * They are returned in the preferred order of presentation to a user.
     *
     * @return the array of fields
     */
    public final Editor[] getFields ()
    {
	// we can't trust the outside world to leave the array alone, hence
	// we must copy it. Java ought to have a way to specify constant
	// array, but it doesn't. It just sucks that way sometimes.
	Editor[] result = new Editor[myFields.length];
	System.arraycopy (myFields, 0, result, 0, myFields.length);
	return result;
    }

    /**
     * Return the count of fields that this object contains.
     *
     * @return the number of fields
     */
    public final int getFieldCount ()
    {
	return myFields.length;
    }

    /**
     * Return the field at the given index.
     *
     * @param idx the index
     * @return the field at that index
     */
    public final Editor getField (int idx)
    {
	return myFields[idx];
    }

    // ------------------------------------------------------------------------
    // Editor interface methods

    /**
     * Ask this editor to update its internal state. In this case, it
     * just passes the same request to each of its fields.
     */
    public final void update ()
    {
	for (int i = 0; i < myFields.length; i++)
	{
	    myFields[i].update ();
	}
    }

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * Set up all the instance variables, figure out mutability,
     * and add listeners so that events get sent out from this object
     * appropriately.
     *
     * @param fields the fields as passed in on construction
     */
    private void setupVariables (Editor[] fields)
    {
	// we can't trust the outside world not to muck with the fields
	// passed in; Java should have a way to specify constant arrays,
	// but it doesn't. It just sucks that way sometimes.
	myFields = new Editor[fields.length];
	System.arraycopy (fields, 0, myFields, 0, fields.length);

	myListener = new FieldsListener ();
	myMutableCount = 0;

	for (int i = 0; i < myFields.length; i++)
	{
	    // synchronize on the listener to avoid the sitch of a
	    // mutabilityChanged event coming in while we're trying
	    // to figure out the initial mutability count
	    synchronized (myListener)
	    {
		myFields[i].addListener (myListener);
		if (myFields[i].isMutable ())
		{
		    myMutableCount++;
		}
	    }
	}

	// see above comment
	synchronized (myListener)
	{
	    mutabilityChanged (myMutableCount != 0);
	}		
    }

    // ------------------------------------------------------------------------
    // Private classes

    /**
     * This class is used as a listener on all the fields, in order to
     * adjust this (outer) object's concept of its own mutability, and in
     * order to have it send out the right events.
     */
    private class FieldsListener
    implements EditorListener
    {
	public void valueChanged (EditorEvent event)
	{
	    broadcast (EditorEvent.fieldEvent (FixedFieldsEditor.this, event));
	}

	public void descriptionChanged (EditorEvent event)
	{
	    broadcast (EditorEvent.fieldEvent (FixedFieldsEditor.this, event));
	}
	
	public void mutabilityChanged (EditorEvent event)
	{
	    broadcast (EditorEvent.fieldEvent (FixedFieldsEditor.this, event));

	    // see comment in setupVariables as per synchronization
	    synchronized (this)
	    {
		boolean mutability = event.getMutability ();
		if (mutability)
		{
		    myMutableCount++;
		}
		else
		{
		    myMutableCount--;
		}
		FixedFieldsEditor.this.mutabilityChanged (myMutableCount != 0);
	    }
	}
	
	public void fieldEvent (EditorEvent event)
	{
	    broadcast (EditorEvent.fieldEvent (FixedFieldsEditor.this, event));
	}
	
	public void fieldAdded (EditorEvent event)
	{
	    broadcast (EditorEvent.fieldEvent (FixedFieldsEditor.this, event));
	}
	
	public void fieldRemoved (EditorEvent event)
	{
	    broadcast (EditorEvent.fieldEvent (FixedFieldsEditor.this, event));
	}
    }
}
