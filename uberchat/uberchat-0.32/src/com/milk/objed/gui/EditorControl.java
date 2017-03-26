// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.objed.gui;

import com.milk.gui.JFrame;
import com.milk.objed.Editable;
import com.milk.objed.Editor;
import com.milk.objed.UneditableException;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.util.Hashtable;

/**
 * <p>This object is the major focus of external interaction. It knows how
 * to hook up <code>Editor</code>s with the GUI elements of this package.
 * When asked to display a particular editor of class
 * <code><i>package.Class</i></code>, it will turn around and attempt to
 * find a class called
 * <code><i>package</i>.gui.<i>Class</i>Component</code>. If the class
 * exists, it tries to call a static method:</p>
 * 
 * <blockquote><code>static public EditorComponent makeComponent (Editor
 * editor, EditorControl control);</code></blockquote>
 *
 * to make the actual component. If any of that fails, it goes to the name
 * of the superclass and tries again. If it hits <code>Object</code> and it
 * still hasn't found a component, it resorts to a bare-bones display
 * component.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class EditorControl
{
    /** table mapping editors to the frames they are in */
    private Hashtable myFrames;

    /** the listener which listens to all the frames */
    private MyWindowListener myWindowListener;

    /** the array of classes used for method lookup */
    private static final Class[] TheArgTypes =
	new Class[] { Editor.class, EditorControl.class };

    /**
     * Construct a new <code>EditorControl</code> object.
     */
    public EditorControl ()
    {
	myFrames = new Hashtable ();
	myWindowListener = new MyWindowListener ();
    }

    /**
     * Get an editor for a given object or throw an
     * <code>UneditableException</code>. Right now, this attempts to cast
     * the object to <code>Editable</code> and then asks it for its editor.
     * Eventually, this may do smart things to allow for
     * non-explicitly-<code>Editable</code> objects to be edited.
     *
     * @param obj the object to edit
     * @return an editor for the object
     * @exception UneditableException thrown if the object cannot be edited 
     */
    public Editor edit (Object obj)
    {
	Editable eobj;

	try
	{
	    eobj = (Editable) obj;
	}
	catch (ClassCastException ex)
	{
	    throw new UneditableException (obj);
	}

	return eobj.getEditor ();
    }

    /**
     * Display the given editor. If it is already being displayed, move
     * it to the front.
     *
     * @param editor the editor to display
     */
    public void display (Editor editor)
    {
	JFrame frame = (JFrame) myFrames.get (editor);
	if (frame != null)
	{
	    frame.toFront ();
	    return;
	}

	frame = EditorPanel.makeFrame (makeComponent (editor));
	frame.addWindowListener (myWindowListener);
	myFrames.put (editor, frame);
	frame.setVisible (true);
    }

    /**
     * Given an <code>Editor</code> return an <code>EditorComponent</code>
     * to interact with it. It does all the steps outlined in the class
     * description.
     *
     * @param editor the editor to interact with
     * @return an editor component that interacts with the given editor
     */
    public EditorComponent makeComponent (Editor editor)
    {
	Class classToTry = editor.getClass ();
	while (classToTry != Object.class)
	{
	    String fullName = classToTry.getName ();
	    int lastDot = fullName.lastIndexOf ('.');
	    String className = fullName.substring (lastDot + 1);
	    String packageName = 
	        (lastDot == -1) ? "" : fullName.substring (0, lastDot + 1);
	    fullName = packageName + "gui." + className + "Component";

	    try
	    {
		Class cls = Class.forName (fullName);
		Method meth = cls.getMethod ("makeComponent", TheArgTypes);
		return (EditorComponent) 
		    meth.invoke (null, new Object[] { editor, this });
	    }
	    catch (Exception ex)
	    {
		// catch it and then just fall through to try the next
		// level in the class hierarchy
	    }

	    classToTry = classToTry.getSuperclass ();
	}

	return new DefaultEditorComponent (editor);
    }

    // ------------------------------------------------------------------------
    // Private helper classes

    /**
     * An instance of this class is used as a listener for all the frames
     * spawned by this (outer) object.
     */
    private class MyWindowListener
    extends WindowAdapter
    {
	public void windowClosing (WindowEvent e)
	{
	    // remove this window from the table
	    JFrame frame = (JFrame) e.getWindow ();
	    Container content = frame.getContentPane ();
	    if (content instanceof EditorComponent)
	    {
		myFrames.remove (((EditorComponent) content).getEditor ());
	    }
	    else
	    {
		throw new RuntimeException ("### Unexpected content: " + 
					    content);
	    }
	}
    }
}
