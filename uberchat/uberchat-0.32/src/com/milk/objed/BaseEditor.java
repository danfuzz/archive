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
import com.milk.util.BaseEvent;
import com.milk.util.ListenerList;
import java.util.EventListener;

/**
 * This is a base class that handles a lot of stuff for editors.
 * Among other things, it manages the listener list. Also, it implements
 * the <code>Editable</code> interface to refer to itself, as opposed
 * to creating an editor for the innards of the object itself.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public abstract class BaseEditor
implements Editor, Editable
{
    /** the label for this editor */
    private String myLabel;

    /** the description for this editor */
    private String myDescription;

    /** the mutibility state of this editor */
    private boolean myMutability;

    /** the list of listeners */
    private ListenerList myListeners;

    /**
     * Construct a <code>BaseEditor</code>.
     *
     * @param label the label
     * @param description the (initial) description
     * @param mutability the (initial) mutability
     */
    public BaseEditor (String label, String description, boolean mutability)
    {
	myLabel = label;
	myDescription = description;
	myMutability = mutability;
	myListeners = new ListenerList ();
    }

    /**
     * Get the string form of this object. In this case, it returns a
     * string of the form <code>"{<i>ClassName</i> <i>label</i>}"</code>.
     *
     * @return the string form
     */
    public String toString ()
    {
	String className = getClass ().getName ();
	int lastDot = className.lastIndexOf ('.');
	
	return '{' + className.substring (lastDot + 1) + ' ' + myLabel + '}';
    }

    // ------------------------------------------------------------------------
    // Editor interface methods

    /**
     * Add a listener to this object.
     *
     * @param listener the listener to add
     */
    public final void addListener (EventListener listener)
    {
	myListeners.add (listener);
    }

    /**
     * Remove a listener from this object that was previously added with
     * <code>addListener()</code>.
     *
     * @param listener the listener to remove
     */
    public final void removeListener (EventListener listener)
    {
	myListeners.remove (listener);
    }

    /**
     * Get the label for this editor.
     *
     * @return the label 
     */
    public final String getLabel ()
    {
	return myLabel;
    }

    /**
     * Get the description for this editor.
     *
     * @return the full description
     */
    public final String getDescription ()
    {
	return myDescription;
    }

    /**
     * Return the mutability of this editor.
     *
     * @return true if this editor is mutable 
     */
    public final boolean isMutable ()
    {
	return myMutability;
    }

    /**
     * Ask this editor to update its internal state. Subclasses must
     * override this to do something appropriate.
     */
    public abstract void update ();

    // ------------------------------------------------------------------------
    // Editable interface methods

    /**
     * Get an <code>Editor</code> for this object. In this case, it
     * returns <code>this</code>.
     *
     * @return an <code>Editor</code> for this object
     */
    public final Editor getEditor ()
    {
	return this;
    }

    // ------------------------------------------------------------------------
    // Protected helper methods

    /**
     * Broadcast an event to the listeners of this object.
     *
     * @param event the event to broadcast
     */
    protected final void broadcast (BaseEvent event)
    {
	myListeners.checkedBroadcast (event);
    }

    /**
     * Change the description of this editor. This handles sending out
     * of appropriate events and changing the internal state of the editor.
     *
     * @param description the new description
     */
    protected final void descriptionChanged (String description)
    {
	if (description.equals (myDescription))
	{
	    // don't bother if the description didn't really change
	    return;
	}

	myDescription = description;
	broadcast (EditorEvent.descriptionChanged (this, description));
    }

    /**
     * Change the mutability of this editor. This handles sending out
     * of appropriate events and changing the internal state of the editor.
     *
     * @param mutability the new mutability
     */
    protected final void mutabilityChanged (boolean mutability)
    {
	if (mutability == myMutability)
	{
	    // don't bother if it didn't really change
	    return;
	}

	myMutability = mutability;
	broadcast (EditorEvent.mutabilityChanged (this, mutability));
    }
}
