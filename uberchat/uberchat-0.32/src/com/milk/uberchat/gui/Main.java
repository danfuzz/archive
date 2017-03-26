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

import com.milk.command.Command;
import com.milk.gui.JFrame;
import com.milk.gui.TextOutputPanel;
import com.milk.objed.gui.EditorControl;
import com.milk.uberchat.event.ErrorEvent;
import com.milk.uberchat.icb.ICBSystemTemplate;
import com.milk.uberchat.irc.IRCSystemTemplate;
import com.milk.uberchat.spacebar.SBSystemTemplate;
import com.milk.uberchat.iface.ChatChannel;
import com.milk.uberchat.iface.ChatChannelHolder;
import com.milk.uberchat.iface.ChatEntity;
import com.milk.uberchat.iface.ChatIdentity;
import com.milk.uberchat.iface.ChatLocus;
import com.milk.uberchat.iface.ChatSystem;
import com.milk.util.ShouldntHappenException;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;
import java.util.EventListener;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * This is the main body of the UI uberchat front end. It doesn't do much
 * more than hook up some basic messaging guts and pop up a control window.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class Main 
extends JPanel
implements ChatEntity
{
    private Window myWindow;
    private MasterControl myMaster;
    private IdentitiesPanel myIdentitiesPanel;
    private ChannelsPanel myChannelsPanel;
    private UsersPanel myUsersPanel;
    private ChatEntity mySelection;

    public Main (MasterControl master) 
    {
	myMaster = master;
	mySelection = null;

	// set up the layout
	GridBagLayout layout = new GridBagLayout ();
	GridBagConstraints con = new GridBagConstraints ();
	setLayout (layout);

	// create the buttons in their own little panel
	JPanel buttonPanel = new JPanel (new FlowLayout (FlowLayout.LEFT));
	con.gridx = 0;
	con.gridy = 0;
	con.anchor = con.WEST;
	con.fill = con.BOTH;
	layout.setConstraints (buttonPanel, con);
	add (buttonPanel);
	ConnectButtonListener listener = new ConnectButtonListener ();
	JButton b;

	b = new JButton ("ICB");
        b.setMnemonic ('i');
	b.setActionCommand ("icb");
	b.addActionListener (listener);
	buttonPanel.add (b);

	b = new JButton ("IRC");
        b.setMnemonic ('r');
	b.setActionCommand ("irc");
	b.addActionListener (listener);
	buttonPanel.add (b);

	b = new JButton ("spacebar");
        b.setMnemonic ('s');
	b.setActionCommand ("spacebar");
	b.addActionListener (listener);
	buttonPanel.add (b);

	buttonPanel = new JPanel (new FlowLayout (FlowLayout.LEFT));
	con.gridy = 1;
	layout.setConstraints (buttonPanel, con);
	add (buttonPanel);

	b = new JButton ("New chat window");
        b.setMnemonic ('w');
	b.setActionCommand ("new-chat");
	b.addActionListener (listener);
	buttonPanel.add (b);

	b = new JButton ("Functions");
        b.setMnemonic ('f');
	b.setActionCommand ("functions-window");
	b.addActionListener (listener);
	buttonPanel.add (b);

	EditorControl econt = myMaster.getEditorControl ();

	// an identities panel
	myIdentitiesPanel = new IdentitiesPanel (econt);
	myIdentitiesPanel.addTreeSelectionListener (
            new IdentSelectionListener ());
	master.setIdentitiesPanel (myIdentitiesPanel);

	// channels and users panels
	myChannelsPanel = new ChannelsPanel (econt);
	myUsersPanel = new UsersPanel (econt);
	myChannelsPanel.addTreeSelectionListener (
            new ChanUserSelectionListener (myUsersPanel));
	myUsersPanel.addTreeSelectionListener (
            new ChanUserSelectionListener (myChannelsPanel));

	// the channels and users get to share a split pane
	JSplitPane splitPane = 
	    new JSplitPane (JSplitPane.VERTICAL_SPLIT,
			    true,
			    myChannelsPanel,
			    myUsersPanel);

	// and the identities and the above share another one
	splitPane = 
	    new JSplitPane (JSplitPane.VERTICAL_SPLIT,
			    true,
			    myIdentitiesPanel,
			    splitPane);
	con.gridy = 2;
	con.weightx = 1;
	con.weighty = 1;
	layout.setConstraints (splitPane, con);
	add (splitPane);
    }

    /** 
     * Inform this panel that it's in a window.
     *
     * @param window the window that it's in
     */
    private void setWindow (Window window)
    {
	myWindow = window;
	myIdentitiesPanel.setWindow (window);
    }

    /** This is a TreeSelectionListener that listens to the tree buried
     * inside the identities panel. */
    private class IdentSelectionListener
    implements TreeSelectionListener
    {
	public void valueChanged (TreeSelectionEvent event)
	{
	    TreePath[] paths = event.getPaths ();
	    for (int i = 0; i < paths.length; i++)
	    {
		TreePath tp = paths[i];
		if (! event.isAddedPath (tp))
		{
		    continue;
		}

		DefaultMutableTreeNode node = 
		    (DefaultMutableTreeNode) tp.getLastPathComponent ();
		ChatEntity newSel = (ChatEntity) node.getUserObject ();
		mySelection = newSel;

		if (mySelection instanceof ChatIdentity)
		{
		    ChatIdentity ident = (ChatIdentity) mySelection;
		    myChannelsPanel.setIdentity (ident);
		    myUsersPanel.setIdentity (ident);
		}
		else
		{
		    myChannelsPanel.setIdentity (null);
		    myUsersPanel.setIdentity (null);
		}
	    }
	}
    }

    /** This is a TreeSelectionListener that listens to the trees buried
     * inside the channels and users panels. */
    private class ChanUserSelectionListener
    implements TreeSelectionListener
    {
	ContainsTree myToClear;

	public ChanUserSelectionListener (ContainsTree toClear)
	{
	    myToClear = toClear;
	}

	public void valueChanged (TreeSelectionEvent event)
	{
	    TreePath[] paths = event.getPaths ();
	    for (int i = 0; i < paths.length; i++)
	    {
		TreePath tp = paths[i];
		if (! event.isAddedPath (tp))
		{
		    continue;
		}

		DefaultMutableTreeNode node = 
		    (DefaultMutableTreeNode) tp.getLastPathComponent ();
		ChatEntity newSel = (ChatEntity) node.getUserObject ();
		mySelection = newSel;
		myIdentitiesPanel.clearSelection ();
		myToClear.clearSelection ();
	    }
	}
    }

    /** This is an ActionListener that listens to the buttons. */
    private class ConnectButtonListener 
    extends AbstractAction 
    {
	public void actionPerformed (ActionEvent e) 
	{
	    String action = e.getActionCommand ();

	    if (action == "new-chat")
	    {
		if (mySelection instanceof ChatLocus)
		{
		    LocusPanel.makeFrame (myMaster,
					  ((ChatLocus) mySelection));
		}
		else
		{
		    ErrorEvent.errorReport (
		        Main.this,
			"You must select a locus to interact with.").
			sendTo (myMaster);
		}
	    }
	    else if (action == "functions-window")
	    {
		if (mySelection instanceof ChatIdentity)
		{
		    FunctionsPanel.showFrame ((ChatIdentity) mySelection);
		}
		else
		{
		    ErrorEvent.errorReport (
		        Main.this,
			"You must select an identity to interact with.").
			sendTo (myMaster);
		}
	    }
	    else if (action == "icb")
	    {
		CreateSystemPanel.makeFrame (myMaster, 
					     new ICBSystemTemplate ());
	    }
	    else if (action == "irc")
	    {
		CreateSystemPanel.makeFrame (myMaster, 
					     new IRCSystemTemplate ());
	    }
	    else if (action == "spacebar")
	    {
		CreateSystemPanel.makeFrame (myMaster, 
					     new SBSystemTemplate ());
	    }
	    else
	    {
		ErrorEvent.errorReport (
		    Main.this,
		    "Weird action request: " + action).
		    sendTo (myMaster);
	    }
	}
    }

    public static void main (String args[]) 
    {
	MasterControl master = new MasterControl ();
	Main panel = new Main (master);
	
	JFrame frame = new JFrame ("UberChat");
	panel.setWindow (frame);
	frame.addWindowListener (new WindowAdapter () 
	{
	    public void windowClosing (WindowEvent e) 
	    {
		System.exit (0);
	    }
	});

	frame.getContentPane ().add ("Center", panel);
	frame.pack ();
	frame.setVisible (true);
	
	JFrame outFrame = TextOutputPanel.makeFrame ("Debugging Output");
	TextOutputPanel outpan = TextOutputPanel.getPanel (outFrame);
	PrintStream outstream = new PrintStream (outpan.getOutputStream ());
	System.setOut (outstream);
	System.setErr (outstream);
    }

    // ------------------------------------------------------------------------
    // ChatEntity interface methods
    // This interface is implemented merely so this object can be a source
    // of ErrorEvents.

    /**
     * Add a listener for this entity.
     *
     * @param listener the listener to add 
     */
    public void addListener (EventListener listener)
    {
	throw new ShouldntHappenException ("This method shouldn't be called.");
    }

    /**
     * Remove a listener from this entity that was previously added
     * with <code>addListener</code>.
     *
     * @param listener the listener to remove
     */
    public void removeListener (EventListener listener)
    {
	throw new ShouldntHappenException ("This method shouldn't be called.");
    }

    /**
     * Add a listener that listens to everything in this entity and in all
     * the entities rooted (directly or indirectly) this one.
     *
     * @param listener the listener to add 
     */
    public void addUberListener (EventListener listener)
    {
	throw new ShouldntHappenException ("This method shouldn't be called.");
    }

    /**
     * Remove a listener that was previously added with
     * <code>addUberListener</code>.
     *
     * @param listener the listener to remove
     */
    public void removeUberListener (EventListener listener)
    {
	throw new ShouldntHappenException ("This method shouldn't be called.");
    }

    /**
     * Get the user commands that may be used with this object.
     *
     * @return the array of commands
     */
    public Command[] getCommands ()
    {
	throw new ShouldntHappenException ("This method shouldn't be called.");
    }

    /**
     * Get the short descriptive name of this object.
     *
     * @return the name of the object 
     */
    public String getName ()
    {
	return "UberChat";
    }

    /**
     * Get the canonical form of the name of this object.
     */
    public String getCanonicalName ()
    {
	return "UberChat";
    }

    /**
     * Get a verbose string description of this object. It need not be
     * stable.
     *
     * @return the verbose string description
     */
    public String getDescription ()
    {
	return "UberChat";
    }
}
