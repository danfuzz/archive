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
import com.milk.uberchat.event.SystemEvent;
import com.milk.uberchat.event.SystemListener;
import com.milk.uberchat.iface.ChatEntity;
import com.milk.uberchat.iface.ChatIdentity;
import com.milk.uberchat.iface.ChatSystem;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * This is a panel which keeps track of all the active identities.
 * When an identity is selected, that fact is transmitted to the MainPanel
 * which this panel is contained within.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class IdentitiesPanel
extends JScrollPane
implements ContainsTree, SystemListener, EntityListener
{
    /** the window this pane is in */
    private Window myWindow;

    /** the tree of systems and identities */
    private DefaultMutableTreeNode myIdentities;

    /** the tree model object */
    private DefaultTreeModel myIdentModel;

    /** the (visible) tree component */
    private JCommandableTree myTree;

    /**
     * Create an IdentitiesPanel, initially empty.
     *
     * @param editorControl the editor control to use
     */
    public IdentitiesPanel (EditorControl editorControl)
    {
	myWindow = null;

	// the tree and its model object
	myIdentities = new DefaultMutableTreeNode ();
	myIdentModel = new DefaultTreeModel (myIdentities);

	// the tree display object
	myTree = new JCommandableTree (myIdentModel);
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

	// tell the super about the list
	setViewportView (myTree);
    }

    /**
     * Just a simple string form.
     *
     * @return the string form
     */
    public String toString ()
    {
	return "{IdentitiesPanel}";
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

    /**
     * This is called when a system is created.
     *
     * @param system the system that was created
     */
    public void systemCreated (ChatSystem system)
    {
	DefaultMutableTreeNode newNode = 
	    insertNodeAlphabetically (myIdentities, system);
	myTree.makeVisible (new TreePath (
            new Object[] { myIdentities, newNode }));
	system.addUberListener (this);
    }

    /**
     * This is called when a system is destroyed.
     *
     * @param system the system that was destroyed
     */
    public void systemDestroyed (ChatSystem system)
    {
	system.removeUberListener (this);
	removeNode (myIdentities, system);
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
	if (e instanceof ChatSystem)
	{
	    // description of system node (top-level) changed
	    DefaultMutableTreeNode node = getParentNode ((ChatSystem) e);
	    myIdentModel.nodeChanged (node);
	}
	else if (e instanceof ChatIdentity)
	{
	    // description of identity node (second-level) changed
	    ChatSystem sys = ((ChatIdentity) e).getTargetSystem ();
	    DefaultMutableTreeNode parent = getParentNode (sys);
	    DefaultMutableTreeNode node = findNode (parent, e);
	    if (node != null)
	    {
		myIdentModel.nodeChanged (node);
	    }
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
    // SystemListener methods

    /**
     * This is called when an identity is added.
     *
     * @param event the SystemEvent commemorating the moment
     */
    public void identityAdded (SystemEvent event)
    {
	ChatIdentity ident = event.getIdentity ();
	ChatSystem sys = ident.getTargetSystem ();
	DefaultMutableTreeNode sysNode = getParentNode (sys);
	DefaultMutableTreeNode newNode = 
	    insertNodeAlphabetically (sysNode, ident);
	myTree.makeVisible (new TreePath ( 
	    new Object[] { myIdentities, sysNode, newNode }));
    }

    /**
     * This is called when an identity is removed.
     *
     * @param event the SystemEvent commemorating the moment
     */
    public void identityRemoved (SystemEvent event)
    {
	ChatIdentity ident = event.getIdentity ();
	ChatSystem sys = ident.getTargetSystem ();
	DefaultMutableTreeNode sysNode = getParentNode (sys);
	removeNode (sysNode, ident);
    }

    /**
     * This is called when a system is connected.
     *
     * @param event the SystemEvent commemorating the moment
     */
    public void systemConnected (SystemEvent event)
    {
	// we don't care
    }

    /**
     * This is called when a system is connecting.
     *
     * @param event the SystemEvent commemorating the moment
     */
    public void systemConnecting (SystemEvent event)
    {
	// we don't care
    }

    /**
     * This is called when a system is disconnected.
     *
     * @param event the SystemEvent commemorating the moment
     */
    public void systemDisconnected (SystemEvent event)
    {
	// we don't care
    }

    /**
     * This is called when a system is disconnecting.
     *
     * @param event the SystemEvent commemorating the moment
     */
    public void systemDisconnecting (SystemEvent event)
    {
	// we don't care
    }

    // ------------------------------------------------------------------------
    // Tree node helper methods

    /**
     * Find the node directly under the given node whose user object
     * matches the one given.
     * 
     * @param node the node to search
     * @param userObj the user object to look up
     * @return the node under the given one with the given user object,
     * or null if not found
     */
    private DefaultMutableTreeNode findNode (DefaultMutableTreeNode node,
					     Object userObj)
    {
	int sz = node.getChildCount ();

	for (int at = 0; at < sz; at++)
	{
	    DefaultMutableTreeNode child = 
		(DefaultMutableTreeNode) node.getChildAt (at);

	    if (child.getUserObject () == userObj)
	    {
		return child;
	    }
	}

	return null;
    }

    /**
     * Return the node for the given system, creating it if it doesn't
     * already exist.
     *
     * @param system the system to find
     * @return the node for the system */
    private DefaultMutableTreeNode getParentNode (ChatSystem system)
    {
	DefaultMutableTreeNode result = findNode (myIdentities, system);
	
	if (result == null)
	{
	    // it wasn't found; gotta make it
	    result = insertNodeAlphabetically (myIdentities, system);
	}

	return result;
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
	myIdentModel.insertNodeInto (newNode, node, at);
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
		myIdentModel.removeNodeFromParent (child);
		return;
	    }
	}
    }

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * This is called via a WindowListener when the window that this panel
     * is in is getting closed. It makes the panel stop getting
     * chat-based events.
     */
    private void panelClosing ()
    {
	int sz = myIdentities.getChildCount ();
	for (int i = 0; i < sz; i++)
	{
	    ChatSystem sys = (ChatSystem) 
		((DefaultMutableTreeNode) myIdentities.getChildAt (i)).
		getUserObject ();
	    sys.removeListener (this);
	}
    }
}
