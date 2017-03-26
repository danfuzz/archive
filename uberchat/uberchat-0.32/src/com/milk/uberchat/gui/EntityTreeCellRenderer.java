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

import com.milk.uberchat.iface.ChatEntity;
import com.milk.uberchat.iface.ChatChannel;
import com.milk.uberchat.iface.ChatSystem;
import com.milk.uberchat.iface.ChatUser;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * This is the TreeCellRenderer to use for rendering ChatEntities in trees.
 * It does its business by overriding getTreeCellRendererComponent to set
 * its text in better way than the default would do.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. */
public class EntityTreeCellRenderer
extends DefaultTreeCellRenderer
{
    /**
     * Override of the superclass constructor to set the icons appropriately.
     */
    public EntityTreeCellRenderer ()
    {
	setClosedIcon (null);
	setOpenIcon (null);
	setLeafIcon (null);
    }

    /**
     * Override of the superclass method to set the text to something
     * better.
     */
    public Component getTreeCellRendererComponent (
        JTree tree, Object value, boolean sel, boolean expanded,
	boolean leaf, int row, boolean hasFocus)
    {
	super.getTreeCellRendererComponent (
            tree, value, sel, expanded, leaf, row, hasFocus);
	if (value instanceof DefaultMutableTreeNode)
	{
	    value = ((DefaultMutableTreeNode) value).getUserObject ();
	}

	// BUG: there should be a ChatEntity method to return what the
	// stuff below does (getConciseDescription??)
	if (value instanceof ChatUser)
	{
	    ChatUser u = (ChatUser) value;
	    String text = u.getName ();
	    String nick = u.getNickname ();
	    if (   (nick.length () != 0)
		&& (! nick.equals (text)))
	    {
		text += '/' + nick;
	    }
	    setText (text);
	}
	else if (value instanceof ChatChannel)
	{
	    ChatChannel c = (ChatChannel) value;
	    String text = c.getName ();
	    String topic = c.getTopic ();
	    if (topic.length () != 0)
	    {
		text += ": " + topic;
	    }
	    switch (c.getJoinedState ())
	    {
		case ChatChannel.JOINED:  text += " [joined]";  break;
		case ChatChannel.JOINING: text += " [joining]"; break;
		case ChatChannel.LEAVING: text += " [leaving]"; break;
	    }
	    setText (text);
	}
	else if (value instanceof ChatSystem)
	{
	    ChatSystem s = (ChatSystem) value;
	    setText (s.getDescription ());
	}
	else if (value instanceof ChatEntity)
	{
	    setText (((ChatEntity) value).getName ());
	}

	return this;
    }
}
