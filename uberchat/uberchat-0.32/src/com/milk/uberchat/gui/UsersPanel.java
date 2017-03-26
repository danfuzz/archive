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
 * This is a panel which keeps track of all the active users for
 * a particular identity, with sub-trees for the channels each is on
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class UsersPanel
extends JScrollPane
implements ContainsTree, LocusListener, EntityListener
{
    /** the current identity to track */
    private ChatIdentity myIdentity;

    /** the window this pane is in */
    private Window myWindow;

    /** the tree of channels and users */
    private DefaultMutableTreeNode myUsers;

    /** the tree model object */
    private DefaultTreeModel myUserModel;

    /** the (visible) tree component */
    private JCommandableTree myTree;

    /**
     * Create a UsersPanel, initially listening to no identity.
     *
     * @param editorControl the editor control to use for popup menus
     */
    public UsersPanel (EditorControl editorControl)
    {
	myIdentity = null;
	myWindow = null;

	// the tree and its model object
	myUsers = new DefaultMutableTreeNode ();
	myUserModel = new DefaultTreeModel (myUsers);

	// the tree display object
	myTree = new JCommandableTree (myUserModel);
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
	return "{UsersPanel " + myIdentity + "}";
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
	myUsers = new DefaultMutableTreeNode ();
	myUserModel.setRoot (myUsers);
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
    // LocusListener methods

    /**
     * This is called when a user enters a locus.
     *
     * @param event the event commemorating the moment
     */
    public void userAdded (LocusEvent event)
    {
	ChatLocus loc = event.getLocus ();
	ChatUser user = event.getUser ();
	if (loc.getTargetIdentity () != myIdentity)
	{
	    return;
	}

	if (loc == myIdentity)
	{
	    // it's a user logging on
	    DefaultMutableTreeNode newNode = 
	        insertNodeAlphabetically (myUsers, user);
	    myTree.makeVisible (new TreePath (
                new Object[] { myUsers, newNode }));
	}
	else if (loc instanceof ChatChannel)
	{
	    // it's a user joining a channel
	    DefaultMutableTreeNode userNode = getUserNode (user);
	    DefaultMutableTreeNode newNode = 
		insertNodeAlphabetically (userNode, loc);
	}
    }

    /**
     * This is called when a user leaves a locus.
     *
     * @param event the event commemorating the moment
     */
    public void userRemoved (LocusEvent event)
    {
	ChatLocus loc = event.getLocus ();
	ChatUser user = event.getUser ();
	if (loc.getTargetIdentity () != myIdentity)
	{
	    return;
	}

	if (loc == myIdentity)
	{
	    // it's a user logging out
	    removeNode (myUsers, user);
	}
	else if (loc instanceof ChatChannel)
	{
	    // it's a user leaving a channel
	    DefaultMutableTreeNode userNode = getUserNode (user);
	    removeNode (userNode, loc);
	}
    }

    // ------------------------------------------------------------------------
    // EntityListener methods

    /**
     * This is called when a description changes.
     *
     * @param event the event commemorating the moment
     */
    public void descriptionChanged (EntityEvent event)
    {
	// BUG--this could lead to items being out of sort order
	ChatEntity e = event.getEntity ();
	if (e instanceof ChatUser)
	{
	    // description of user node (top-level) changed
	    DefaultMutableTreeNode node = getUserNode ((ChatUser) e);
	    myUserModel.nodeChanged (node);
	}
	else if (e instanceof ChatChannel)
	{
	    // description of channel node (second-level) changed
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
	Enumeration enum = myUsers.breadthFirstEnumeration ();
	while (enum.hasMoreElements ())
	{
	    DefaultMutableTreeNode n = 
		((DefaultMutableTreeNode) enum.nextElement ());
	    if (n.getUserObject () == obj)
	    {
		myUserModel.nodeChanged (n);
	    }
	}
    }

    /**
     * Return the node for the given user, creating it if it doesn't
     * already exist.
     *
     * @param user the user to find
     * @return the node for the user
     */
    private DefaultMutableTreeNode getUserNode (ChatUser user)
    {
	int sz = myUsers.getChildCount ();

	for (int at = 0; at < sz; at++)
	{
	    DefaultMutableTreeNode child = 
		(DefaultMutableTreeNode) myUsers.getChildAt (at);

	    if (child.getUserObject () == user)
	    {
		return child;
	    }
	}

	// it wasn't found; gotta make it
	return insertNodeAlphabetically (myUsers, user);
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
	myUserModel.insertNodeInto (newNode, node, at);
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
		myUserModel.removeNodeFromParent (child);
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
