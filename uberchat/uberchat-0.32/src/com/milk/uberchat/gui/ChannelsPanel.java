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

import com.milk.command.gui.JCommandableTree;
import com.milk.objed.gui.EditorControl;
import com.milk.uberchat.event.ChannelHolderEvent;
import com.milk.uberchat.event.ChannelHolderListener;
import com.milk.uberchat.event.EntityEvent;
import com.milk.uberchat.event.EntityListener;
import com.milk.uberchat.event.LocusEvent;
import com.milk.uberchat.event.LocusListener;
import com.milk.uberchat.iface.ChatChannel;
import com.milk.uberchat.iface.ChatEntity;
import com.milk.uberchat.iface.ChatIdentity;
import com.milk.uberchat.iface.ChatLocus;
import com.milk.uberchat.iface.ChatUser;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Enumeration;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * This is a panel which keeps track of all the active channels for
 * a particular identity, with sub-trees for the users in each channel.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class ChannelsPanel
extends JScrollPane
implements ContainsTree, ChannelHolderListener, LocusListener, EntityListener
{
    /** the current identity to track */
    private ChatIdentity myIdentity;

    /** the window this pane is in */
    private Window myWindow;

    /** the tree of channels and users */
    private DefaultMutableTreeNode myChannels;

    /** the tree model object */
    private DefaultTreeModel myChanModel;

    /** the (visible) tree component */
    private JCommandableTree myTree;

    /**
     * Create a ChannelsPanel, initially listening to no identity.
     *
     * @param editorControl the editor control to use for popup menus
     */
    public ChannelsPanel (EditorControl editorControl)
    {
	myIdentity = null;
	myWindow = null;

	// the tree and its model object
	myChannels = new DefaultMutableTreeNode ();
	myChanModel = new DefaultTreeModel (myChannels);

	// the tree display object
	myTree = new JCommandableTree (myChanModel);
	myTree.setEditorControl (editorControl);
	myTree.setLargeModel (true);
	myTree.setRootVisible (false);
	myTree.setShowsRootHandles (true);
	myTree.setVisibleRowCount (8);
	myTree.getSelectionModel ().setSelectionMode (
            TreeSelectionModel.SINGLE_TREE_SELECTION);
	myTree.setCellRenderer (new EntityTreeCellRenderer ());
	myTree.setRowHeight (15); // BUG--fix me
	//myTree.setMinimumSize (new Dimension (300, 50)); // BUG--fix me
	setMinimumSize (new Dimension (300, 50)); // BUG--fix me

	// tell the scrollpane about the tree
	setViewportView (myTree);
    }

    /**
     * Just a simple string form.
     *
     * @return the string form
     */
    public String toString ()
    {
	return "{ChannelsPanel " + myIdentity + "}";
    }

    /**
     * Change (or set for the first time) the ChatIdentity that this
     * ChannelsPanel should reflect.
     *
     * @param identity the identity to display
     */
    public void setIdentity (ChatIdentity identity)
    {
	if (identity == myIdentity)
	{
	    // easy out
	    myTree.clearSelection ();
	    return;
	}

	if (myIdentity != null)
	{
	    myIdentity.removeUberListener (this);
	}

	myIdentity = identity;

	// reset the tree and then prepare for it to be populated
	myChannels = new DefaultMutableTreeNode ();
	myChanModel.setRoot (myChannels);
	myTree.clearSelection ();

	if (identity != null)
	{
	    identity.addUberListener (this);
	}
    }

    /**
     * Tell this pane that it's now in a window. It should only ever
     * be called once.
     *
     * @param window the window that this pane is in
     */
    public void setWindow (Window window)
    {
	if (myWindow != null)
	{
	    throw new RuntimeException ("Window already set.");
	}

	myWindow = window;

	// listen for the window to be closed
	window.addWindowListener (
	    new WindowAdapter ()
	    {
		public void windowClosing (WindowEvent e)
		{
		    panelClosing ();
		}
	    });
    }

    // ------------------------------------------------------------------------
    // ContainsTree interface methods

    /**
     * Add a TreeSelectionListener to listen to our embedded tree.
     *
     * @param listener the listener to add
     */
    public void addTreeSelectionListener (TreeSelectionListener listener)
    {
	myTree.addTreeSelectionListener (listener);
    }

    /**
     * Remove a TreeSelectionListener to listen from our embedded tree.
     *
     * @param listener the listener to remove
     */
    public void removeTreeSelectionListener (TreeSelectionListener listener)
    {
	myTree.removeTreeSelectionListener (listener);
    }

    /**
     * Clear the user selection, if any.
     */
    public void clearSelection ()
    {
	myTree.clearSelection ();
    }

    // ------------------------------------------------------------------------
    // ChannelHolderListener methods

    /**
     * This is called when a channel is added.
     *
     * @param event the ChannelHolderEvent commemorating the moment
     */
    public void channelAdded (ChannelHolderEvent event)
    {
	if (! (event.getChannelHolder () instanceof ChatIdentity))
	{
	    // only care if it's being added at the identity level
	    return;
	}

	ChatChannel channel = event.getChannel ();
	DefaultMutableTreeNode newNode = 
	    insertNodeAlphabetically (myChannels, channel);
	myTree.makeVisible (new TreePath (
            new Object[] { myChannels, newNode }));
    }

    /**
     * This is called when a channel is removed.
     *
     * @param event the ChannelHolderEvent commemorating the moment
     */
    public void channelRemoved (ChannelHolderEvent event)
    {
	if (! (event.getChannelHolder () instanceof ChatIdentity))
	{
	    // only care if it's being removed at the identity level
	    return;
	}

	removeNode (myChannels, event.getChannel ());
    }

    // ------------------------------------------------------------------------
    // LocusListener methods

    /**
     * This is called when a user joins a locus.
     *
     * @param event the event commemorating the moment
     */
    public void userAdded (LocusEvent event)
    {
	ChatLocus loc = event.getLocus ();
	if (! (loc instanceof ChatChannel))
	{
	    // ignore it unless it's for a channel
	    return;
	}

	ChatUser user = event.getUser ();
	ChatChannel chan = (ChatChannel) loc;

	DefaultMutableTreeNode chanNode = getChannelNode (chan);
	DefaultMutableTreeNode newNode = 
	    insertNodeAlphabetically (chanNode, user);
    }

    /**
     * This is called when a user leaves a locus.
     *
     * @param event the event commemorating the moment
     */
    public void userRemoved (LocusEvent event)
    {
	ChatLocus loc = event.getLocus ();
	if (! (loc instanceof ChatChannel))
	{
	    // ignore it unless it's for a channel
	    return;
	}

	ChatUser user = event.getUser ();
	ChatChannel chan = (ChatChannel) loc;

	DefaultMutableTreeNode chanNode = getChannelNode (chan);
	removeNode (chanNode, user);
    }

    // ------------------------------------------------------------------------
    // EntityListener methods

    /**
     * This is called when the description changed.
     *
     * @param event the event commemorating the moment
     */
    public void descriptionChanged (EntityEvent event)
    {
	// BUG--this could lead to stuff being out of sort order
	ChatEntity e = event.getEntity ();
	if (e instanceof ChatChannel)
	{
	    // description of channel node (top-level) changed
	    DefaultMutableTreeNode node = getChannelNode ((ChatChannel) e);
	    myChanModel.nodeChanged (node);
	}
	else if (e instanceof ChatUser)
	{
	    // description of user node (second-level) changed
	    nodesChanged (e);
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

    // ------------------------------------------------------------------------
    // Tree node helper methods

    /**
     * Find all the nodes with the given object as a user object and
     * inform the model that those nodes have changed.
     *
     * @param obj the object to find
     */
    private void nodesChanged (Object obj)
    {
	Enumeration enum = myChannels.breadthFirstEnumeration ();
	while (enum.hasMoreElements ())
	{
	    DefaultMutableTreeNode n = 
		((DefaultMutableTreeNode) enum.nextElement ());
	    if (n.getUserObject () == obj)
	    {
		myChanModel.nodeChanged (n);
	    }
	}
    }

    /**
     * Return the node for the given channel, creating it if it doesn't
     * already exist.
     *
     * @param channel the channel to find
     * @return the node for the channel
     */
    private DefaultMutableTreeNode getChannelNode (ChatChannel channel)
    {
	int sz = myChannels.getChildCount ();

	for (int at = 0; at < sz; at++)
	{
	    DefaultMutableTreeNode child = 
		(DefaultMutableTreeNode) myChannels.getChildAt (at);

	    if (child.getUserObject () == channel)
	    {
		return child;
	    }
	}

	// it wasn't found; gotta make it
	return insertNodeAlphabetically (myChannels, channel);
    }

    /**
     * Add a new leaf to the given node, inserting such that the list of
     * children is sorted alphabetically.
     *
     * @param node the node to add to
     * @param item the item to add
     * @return the new leaf node
     */
    private DefaultMutableTreeNode insertNodeAlphabetically (
        DefaultMutableTreeNode node,
	ChatEntity item)
    {
	String itemName = item.getName ().toLowerCase ();
	int sz = node.getChildCount ();
	int at;

	for (at = 0; at < sz; at++)
	{
	    ChatEntity child = 
		(ChatEntity) ((DefaultMutableTreeNode) node.getChildAt (at)).
		getUserObject ();
	    String childName = child.getName ().toLowerCase ();

	    if (itemName.compareTo (childName) < 0)
	    {
		break;
	    }
	}

	DefaultMutableTreeNode newNode = new DefaultMutableTreeNode (item);
	myChanModel.insertNodeInto (newNode, node, at);
	return newNode;
    }

    /**
     * Remove the given child from the given node.
     *
     * @param node the node to remove from
     * @param item the item to remove
     */
    private void removeNode (DefaultMutableTreeNode node,
			     ChatEntity item)
    {
	int sz = node.getChildCount ();

	for (int at = 0; at < sz; at++)
	{
	    DefaultMutableTreeNode child = 
		(DefaultMutableTreeNode) node.getChildAt (at);
	    Object childObj = child.getUserObject ();

	    if (childObj == item)
	    {
		myChanModel.removeNodeFromParent (child);
		return;
	    }
	}
    }

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * This is called via a WindowListener when the window that this panel
     * is in is getting closed. It makes the panel stop getting
     * EntityEvents. 
     */
    private void panelClosing ()
    {
	if (myIdentity != null)
	{
	    myIdentity.removeUberListener (this);
	}
    }
}
