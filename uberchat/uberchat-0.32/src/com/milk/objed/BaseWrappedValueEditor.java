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
import com.milk.util.BadValueException;
import com.milk.util.BaseEvent;
import com.milk.util.ImmutableException;
import com.milk.util.ListenerList;
import java.util.EventListener;

/**
 * This is a base class which implements <code>ValueEditor</code> to defer
 * to some other target editor. Among other things, it provides call-backs
 * for when events get fired from the target. Also, it implements the
 * <code>Editable</code> interface to refer to itself, as opposed to
 * creating an editor for the innards of the object itself. By default,
 * most of the <code>Editor</code> methods just defer to the target, except
 * for <code>{add,remove}Listener()</code>, which operate locally to make
 * the events work out right. The <code>ValueEditor</code> methods may be
 * overridden to do special processing on the value.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public abstract class BaseWrappedValueEditor
implements ValueEditor, Editable
{
    /** the target editor */
    private ValueEditor myTarget;

    /** the listener which listens to the target */
    private TargetListener myListener;

    /** the list of listeners to this object */
    private ListenerList myListeners;

    /**
     * Construct a <code>BaseWrappedValueEditor</code>.
     *
     * @param target the target to wrap around
     */
    protected BaseWrappedValueEditor (ValueEditor target)
    {
	myTarget = target;
	myListener = new TargetListener ();
	myListeners = new ListenerList ();
	myTarget.addListener (myListener);
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
	
	return '{' + className.substring (lastDot + 1) + ' ' + getLabel () + 
	    '}';
    }

    // ------------------------------------------------------------------------
    // ValueEditor interface methods

    /**
     * Get the current value from this editor. This implementation just
     * defers to the target, but subclasses may override this to do
     * interesting processing (and should use <code>super.getValue()</code>
     * to actually get the underlying value from the target).
     *
     * @return the current value
     * @exception BadValueException thrown if the value is bad in some
     * way 
     */
    public Object getValue ()
    {
	return myTarget.getValue ();
    }

    /**
     * Set a new value for the editor. This implementation just
     * defers to the target, but subclasses may override this to do
     * interesting processing (and should use <code>super.setValue()</code>
     * to actually set the underlying value in the target).
     *
     * @param value the new value
     * @exception BadValueException thrown if the value is inappropriate
     * for this editor
     * @exception ImmutableException thrown if the editor is in fact
     * immutable
     */
    public void setValue (Object value)
    throws BadValueException, ImmutableException
    {
	myTarget.setValue (value);
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
     * Get a short human-oriented label/description for the object this
     * editor is editing. The label should be suitable as a label next to
     * this editor, and shouldn't ever change during the lifetime of this
     * editor.
     *
     * @return the label 
     */
    public final String getLabel ()
    {
	return myTarget.getLabel ();
    }

    /**
     * Get a full human-oriented description for the object this editor is
     * editing. The description should be suitable for tip-text or the
     * like. It's okay for the description to change, and if it does, a
     * <code>descriptionChanged</code> event will be sent to any registered
     * listeners.
     *
     * @return the full description 
     */
    public final String getDescription ()
    {
	return myTarget.getDescription ();
    }

    /**
     * Return true if this editor is mutable. That is, if this method
     * returns true, then edits can actually happen on this object. If
     * false, this object is for display only. If the mutability of this
     * object changes, a <code>mutabilityChanged</code> event will be sent
     * to any registered listeners.
     *
     * @return true if this editor is mutable 
     */
    public final boolean isMutable ()
    {
	return myTarget.isMutable ();
    }

    /**
     * Ask this editor to update its internal state. In this case,
     * it merely passes the request to the target editor.
     */
    public final void update ()
    {
	myTarget.update ();
    }

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

    // ------------------------------------------------------------------------
    // Private helper classes

    /**
     * This class is used as a listener on the target, in order to cause
     * events that it sends out to be sent out from this (outer) object
     * too.
     */
    private class TargetListener
    implements EditorListener
    {
	public void valueChanged (EditorEvent event)
	{
	    // we can't just resend the event with the same value
	    // since subclass's getValue() method might filter it
	    // in some way
	    Object value;
	    try
	    {
		value = getValue ();
	    }
	    catch (BadValueException ex)
	    {
		// value isn't currently valid, so don't bother
		// broadcasting
		return;
	    }
	    broadcast (EditorEvent.valueChanged (BaseWrappedValueEditor.this, 
						 value));
	}
	
	public void descriptionChanged (EditorEvent event)
	{
	    broadcast (event.withNewSource (BaseWrappedValueEditor.this));
	}
	
	public void mutabilityChanged (EditorEvent event)
	{
	    broadcast (event.withNewSource (BaseWrappedValueEditor.this));
	}

	public void fieldEvent (EditorEvent event)
	{
	    broadcast (event.withNewSource (BaseWrappedValueEditor.this));
	}
	
	public void fieldAdded (EditorEvent event)
	{
	    broadcast (event.withNewSource (BaseWrappedValueEditor.this));
	}
	
	public void fieldRemoved (EditorEvent event)
	{
	    broadcast (event.withNewSource (BaseWrappedValueEditor.this));
	}
    }
}
