// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.uberchat.gui;

import com.milk.gui.AsEvent;
import com.milk.gui.JFrame;
import com.milk.uberchat.LocusTracker;
import com.milk.uberchat.SpeechKinds;
import com.milk.uberchat.event.EntityEvent;
import com.milk.uberchat.event.EntityListener;
import com.milk.uberchat.event.MessageEvent;
import com.milk.uberchat.event.MessageListener;
import com.milk.uberchat.event.UserBroadcastDetails;
import com.milk.uberchat.iface.ChatIdentity;
import com.milk.uberchat.iface.ChatLocus;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleContext;

/**
 * This is a panel which has all the guts for doing interaction in a
 * particular locus. In particular, it's got a bigass area for the messages
 * sent and received, an area for typing new messages, and some buttons
 * for controlling stuff.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class LocusPanel
extends JPanel
{
    private JFrame myFrame;
    private MasterControl myMaster;
    private ChatLocus myLocus;

    private MyEntityListener myEntityListener;
    private MyMessageListener myMessageListener;
    private boolean myIsAtBottom;
    private int myBottomIsAt;

    private DefaultStyledDocument myDocument;
    private JTextPane myDocumentPane;
    private JScrollPane myScrollPane;
    private JTextField myTextField;
    private JComboBox myKindMenu;
    private String myKind;

    private Style mySpeakerStyle;
    private Style mySpeechStyle;
    private Style mySystemBroadcastStyle;
    private Style mySystemPrivateStyle;
    private Style myOtherStyle;

    /**
     * Create a LocusPanel for the given locus.
     *
     * @param master the master control object to use
     * @param locus the locus for this pane
     */
    public LocusPanel (MasterControl master, ChatLocus locus) 
    {
	myMaster = master;
	myLocus = locus;
	myIsAtBottom = true;
	myBottomIsAt = 0;

	// set up the style variables
	StyleContext sc = master.getChatStyles ();
	mySpeakerStyle = sc.getStyle ("speaker");
	mySpeechStyle = sc.getStyle ("speech");
	mySystemBroadcastStyle = sc.getStyle ("system-broadcast");
	mySystemPrivateStyle = sc.getStyle ("system-private");
	myOtherStyle = sc.getStyle ("other");

	GridBagLayout layout = new GridBagLayout ();
	GridBagConstraints con = new GridBagConstraints ();
	setLayout (layout);
	con.gridx = 0;

	// the document which will contain all the chat dialog
	myDocument = new DefaultStyledDocument ();

	// the pane to hold the document
	myDocumentPane = new JTextPane (myDocument);
	myDocumentPane.setEditable (false);

	// the scrollpane to hold the document pane
	myScrollPane = new JScrollPane (myDocumentPane);
	myScrollPane.setPreferredSize (new Dimension (500, 280)); // BUG
	con.weightx = 1;
	con.weighty = 1;
	con.fill = con.BOTH;
	layout.setConstraints (myScrollPane, con);
	add (myScrollPane);
	myScrollPane.getViewport ().
	    addChangeListener (new MyChangeListener ());

	// the box for the buttons and menus
	Box controlBox = Box.createHorizontalBox ();
	con.weighty = 0;
	con.insets.top = 4;
	con.insets.left = 4;
	con.fill = con.NONE;
	con.anchor = con.WEST;
	layout.setConstraints (controlBox, con);
	add (controlBox);

	// the speech kind menu
	myKindMenu = new JComboBox ();
	myKindMenu.setEditable (true);
	myKindMenu.addItem (SpeechKinds.BEEP);
	myKindMenu.addItem (SpeechKinds.ECHOES);
	myKindMenu.addItem (SpeechKinds.ME);
	myKindMenu.addItem (SpeechKinds.MY);
	myKindMenu.addItem (SpeechKinds.SAYS);
	myKindMenu.addItem (SpeechKinds.SINGS);
	myKindMenu.addItem (SpeechKinds.THINKS);
	myKindMenu.setSelectedItem (SpeechKinds.SAYS);
	controlBox.add (myKindMenu);

	controlBox.add (Box.createHorizontalStrut (4));

	FieldListener listener = new FieldListener ();

	// the text field for typing messages
	myTextField = new JTextField (60);
	myTextField.setFont (new Font ("DialogInput", Font.PLAIN, 10));
	con.fill = con.HORIZONTAL;
	con.insets.left = 0;
	layout.setConstraints (myTextField, con);
	myTextField.addActionListener (listener);
	add (myTextField);

	// do the listener thing
	myEntityListener = new MyEntityListener ();
	myMessageListener = new MyMessageListener ();
	myMaster.getLocusTracker (myLocus).addListener (myMessageListener);
	locus.addListener (myEntityListener);
    }

    /**
     * Create and show a frame with a <code>LocusPanel</code> in it.
     *
     * @param master the master control object to use
     * @param locus the locus for this pane
     */
    public static void makeFrame (MasterControl master, ChatLocus locus) 
    {
	final LocusPanel panel = new LocusPanel (master, locus);
	
	JFrame frame = new JFrame (locus.getDescription ());
	frame.addWindowListener (
            new WindowAdapter ()
	    {
		public void windowClosing (WindowEvent e)
		{
		    panel.windowClosing ();
		}
	    });
	frame.setDefaultCloseOperation (frame.DISPOSE_ON_CLOSE);
	panel.myFrame = frame;

	frame.getContentPane ().add ("Center", panel);
	frame.pack ();
	frame.setVisible (true);
    }

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * Add a line of text to the document, possibly with a special
     * first-run character style.
     *
     * @param firstText null-ok; the first text to add
     * @param firstStyle null-ok; the character style for the first text
     * @param restText non-null; the (rest of the) text to add
     * @param mainStyle non-null; the main (paragraph) style for the text
     */
    private void addLine (String firstText, Style firstStyle,
			  String restText, Style mainStyle)
    {
	try
	{
	    int insertAt = myDocument.getLength ();
	    boolean addNL = (insertAt != 0);
	   
	    if (addNL)
	    {
		myDocument.insertString (insertAt, "\n", mainStyle);
		insertAt++;
	    }

	    if (firstText != null)
	    {
		myDocument.insertString (insertAt, firstText, firstStyle);
		insertAt += firstText.length ();
	    }
	    myDocument.insertString (insertAt, restText, mainStyle);
	    myDocument.setLogicalStyle (insertAt, mainStyle);
	}
	catch (BadLocationException ex)
	{
	    // not gonna happen
	}

	if (myFrame != null)
	{
	    AsEvent.setVisible (myFrame, true);
	}
    }

    /**
     * Speak in this locus. It grabs the values from the text field
     * and speech kind to do its business.
     */
    private void speak ()
    {
	String text = myTextField.getText ();
	String kind = myKindMenu.getSelectedItem ().toString ().intern ();

	myLocus.speak (kind, text);
	myTextField.setText ("");
    }

    /**
     * This is called via a WindowListener when the window that this panel
     * is in is getting closed. It makes the panel stop getting chat
     * messages.
     */
    private void windowClosing ()
    {
	myMaster.getLocusTracker (myLocus).removeListener (myMessageListener);
	myLocus.removeListener (myEntityListener);
    }

    // ------------------------------------------------------------------------
    // Private helper classes

    /** 
     * This is a <code>ChangeListener</code> that listens to the viewport,
     * so we can track scrolling pos etc. 
     */
    private class MyChangeListener
    implements ChangeListener
    {
	public void stateChanged (ChangeEvent e)
	{
	    JViewport vp = myScrollPane.getViewport ();
	    int viewHeight = myDocumentPane.getHeight ();
	    int extentHeight = vp.getHeight ();
	    int viewY = myDocumentPane.getY ();
	    if (myIsAtBottom && (viewHeight != myBottomIsAt))
	    {
		vp.setViewPosition (
                    new Point (0, viewHeight - extentHeight));
	    }
	    else
	    {
		myIsAtBottom = ((viewHeight + viewY) == extentHeight);
	    }
	    myBottomIsAt = viewHeight;
	}
    }

    /** This is an ActionListener that listens to the salient stuff in
     * this panel. */
    private class FieldListener 
    extends AbstractAction 
    {
	public void actionPerformed (ActionEvent e) 
	{
	    Object source = e.getSource ();
	    if (source == myTextField)
	    {
		speak ();
	    }
	    else
	    {
		JOptionPane.showMessageDialog (
                    null,
		    "Shouldn't happen: Got weird source object for action: " +
		    source,
		    "Shouldn't Happen",
		    JOptionPane.ERROR_MESSAGE);
	    }
	}
    }

    /**
     * This is the <code>EntityListener</code> that listens to the locus.
     */
    private class MyEntityListener
    implements EntityListener
    {
	/**
	 * This is called when the description changed.
	 *
	 * @param event the event commemorating the moment
	 */
	public void descriptionChanged (EntityEvent event)
	{
	    if (   (event.getEntity () == myLocus)
		&& (myFrame != null))
	    {
		myFrame.setTitle (event.getDescription ());
	    }
	}

	/**
	 * This is called when the name changed.
	 *
	 * @param event the event commemorating the moment
	 */
	public void nameChanged (EntityEvent event)
	{
	    // ignore it; we don't care
	}
    }

    /**
     * This is the <code>MessageListener</code> that listens to the
     * locus tracker.
     */
    private class MyMessageListener
    implements MessageListener
    {
	/**
	 * This is called when a user broadcasts a message in a locus.
	 *
	 * @param event the event commemorating the moment
	 */
	public void userBroadcast (MessageEvent event)
	{
	    if (event.getLocus () != myLocus)
	    {
		// ignore it if it's not for the current locus; this might
		// happen if the panel is in the process of switching loci
		return;
	    }

	    UserBroadcastDetails ubd = event.getDetails ();
	    addLine (ubd.getSpeakerString (), mySpeakerStyle,
		     ubd.getText (), mySpeechStyle);
	}

	/**
	 * This is called when the host system broadcasts a message in a locus.
	 *
	 * @param event the event commemorating the moment 
	 */
	public void systemBroadcast (MessageEvent event)
	{
	    if (event.getLocus () != myLocus)
	    {
		// ignore it if it's not for the current locus; this might
		// happen if the panel is in the process of switching loci
		return;
	    }
	    
	    addLine (null, null, event.getText (), mySystemBroadcastStyle);
	}

	/**
	 * This is called when the host system sends a private message
	 * to this client in a locus.
	 *
	 * @param event the event commemorating the moment 
	 */
	public void systemPrivate (MessageEvent event)
	{
	    if (event.getLocus () != myLocus)
	    {
		// ignore it if it's not for the current locus; this might
		// happen if the panel is in the process of switching loci
		return;
	    }

	    addLine (null, null, event.getText (), mySystemPrivateStyle);
	}
    }
}
