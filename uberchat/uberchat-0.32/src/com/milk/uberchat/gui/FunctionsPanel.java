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

import com.milk.gui.JFrame;
import com.milk.uberchat.SpeechKinds;
import com.milk.uberchat.event.EntityEvent;
import com.milk.uberchat.event.EntityListener;
import com.milk.uberchat.iface.ChatChannel;
import com.milk.uberchat.iface.ChatIdentity;
import com.milk.uberchat.iface.ChatLocus;
import com.milk.uberchat.iface.ChatUser;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * This class is a panel which allows one to perform some common
 * chat functions (sending messages, changing nicknames, setting topics).
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class FunctionsPanel
extends JPanel
{
    /** non-null; the identity to use */
    private ChatIdentity myIdentity;

    /** the first text field */
    private JTextField myText1;

    /** the second text field */
    private JTextField myText2;

    /** the label for the first text field */
    private JLabel myLabel1;

    /** the label for the second text field */
    private JLabel myLabel2;

    /** the user button */
    private JRadioButton myUserButton;

    /** the channel button */
    private JRadioButton myChannelButton;

    /** the nickname button */
    private JRadioButton myNicknameButton;

    /** the topic button */
    private JRadioButton myTopicButton;

    /** the raw send button */
    private JRadioButton myRawSendButton;

    /** the join/leave button */
    private JRadioButton myJoinLeaveButton;

    /**
     * Create a <code>FunctionsPanel</code>.
     *
     * @param identity non-null; the identity to use
     */
    public FunctionsPanel (ChatIdentity identity)
    {
	myIdentity = identity;

	Font font = new Font ("DialogInput", Font.PLAIN, 10); // BUG--pref!

	// set up the layout
	GridBagLayout layout = new GridBagLayout ();
	setLayout (layout);
	GridBagConstraints con = new GridBagConstraints ();
	con.insets.top = 2;
	con.insets.bottom = 2;

	// make the text fields and label them
	myText1 = new JTextField (60);
	myText1.setFont (font);
	con.fill = con.BOTH;
	con.gridx = 1;
	con.gridy = 0;
	con.anchor = con.NORTHWEST;
	con.insets.left = 2;
	con.insets.right = 20;
	layout.setConstraints (myText1, con);
	add (myText1);

	myLabel1 = new JLabel ("user");
	con.fill = con.NONE;
	con.insets.left = 20;
	con.insets.right = 2;
	con.gridx = 0;
	con.anchor = con.NORTHEAST;
	layout.setConstraints (myLabel1, con);
	myLabel1.setLabelFor (myText1);
	add (myLabel1);

	// make the text-to-send field and label it
	myText2 = new JTextField (60);
	myText2.setFont (font);
	con.fill = con.BOTH;
	con.gridx = 1;
	con.gridy = 1;
	con.anchor = con.NORTHWEST;
	con.insets.left = 2;
	con.insets.right = 20;
	layout.setConstraints (myText2, con);
	add (myText2);

	myLabel2 = new JLabel ("message");
	con.fill = con.NONE;
	con.insets.left = 20;
	con.insets.right = 2;
	con.gridx = 0;
	con.anchor = con.NORTHEAST;
	layout.setConstraints (myLabel2, con);
	myLabel2.setLabelFor (myText2);
	add (myLabel2);

	// make the label side maintain width even with label text changes
	con.gridy = 2;
	Component labelWidth = Box.createHorizontalStrut (70);
	layout.setConstraints (labelWidth, con);
	add (labelWidth);

	// make the channel/user radio buttons
	ButtonGroup group = new ButtonGroup ();

	Box buttonBox = Box.createHorizontalBox ();
	con.insets.left = 2;
	con.insets.right = 2;
	con.gridx = 1;
	con.gridy = 2;
	con.anchor = con.NORTHWEST;
	layout.setConstraints (buttonBox, con);
	add (buttonBox);

	myUserButton = new JRadioButton ("speak to user");
	myUserButton.setMnemonic ('u');
	myUserButton.setSelected (true);
	group.add (myUserButton);
	buttonBox.add (myUserButton);

	myChannelButton = new JRadioButton ("speak to channel");
	myChannelButton.setMnemonic ('c');
	group.add (myChannelButton);
	buttonBox.add (myChannelButton);

	myJoinLeaveButton = new JRadioButton ("join/leave a channel");
	myJoinLeaveButton.setMnemonic ('j');
	group.add (myJoinLeaveButton);
	buttonBox.add (myJoinLeaveButton);

	buttonBox = Box.createHorizontalBox ();
	con.gridy = 3;
	con.anchor = con.NORTHWEST;
	layout.setConstraints (buttonBox, con);
	add (buttonBox);

	myNicknameButton = new JRadioButton ("change nickname");
	myNicknameButton.setMnemonic ('n');
	group.add (myNicknameButton);
	buttonBox.add (myNicknameButton);

	myTopicButton = new JRadioButton ("change topic");
	myTopicButton.setMnemonic ('t');
	group.add (myTopicButton);
	buttonBox.add (myTopicButton);

	myRawSendButton = new JRadioButton ("raw send");
	myRawSendButton.setMnemonic ('r');
	group.add (myRawSendButton);
	buttonBox.add (myRawSendButton);

	// make the action listener
	MyActionListener mal = new MyActionListener ();
	myText2.addActionListener (mal);
	myUserButton.addActionListener (mal);
	myChannelButton.addActionListener (mal);
	myJoinLeaveButton.addActionListener (mal);
	myNicknameButton.addActionListener (mal);
	myTopicButton.addActionListener (mal);
	myRawSendButton.addActionListener (mal);
    }

    /**
     * Make and show a frame to show a <code>FunctionsPanel</code>.
     *
     * @param identity non-null; the identity to use
     */
    public static void showFrame (final ChatIdentity identity)
    {
	final ChatUser iu = identity.getIdentityUser ();
	FunctionsPanel panel = new FunctionsPanel (identity);
	final JFrame frame = new JFrame (makeFrameTitle (iu));
	frame.setDefaultCloseOperation (frame.DISPOSE_ON_CLOSE);

	final EntityListener el = new EntityListener ()
	{
	    public void descriptionChanged (EntityEvent event)
	    {
		frame.setTitle (makeFrameTitle (iu));
	    }

	    public void nameChanged (EntityEvent event)
	    {
		frame.setTitle (makeFrameTitle (iu));
	    }
	};

	iu.addListener (el);
	
	frame.addWindowListener (
            new WindowAdapter ()
	    {
		public void windowClosing (WindowEvent e)
		{
		    iu.removeListener (el);
		}
	    });

	frame.getContentPane ().add ("Center", new JScrollPane (panel));
	frame.pack ();
	frame.setVisible (true);
    }

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * Make a frame title for the given identity
     *
     * @param identityUser the identity user to query
     */
    private static String makeFrameTitle (ChatUser identityUser)
    {
	StringBuffer sb = new StringBuffer ();
	sb.append (identityUser.getNameNickCombo ());
	sb.append (" on ");
	sb.append (identityUser.getTargetSystem ().getName ());
	sb.append (": Common Functions");
	return sb.toString ();
    }

    /**
     * Perform the appropriate action depending on the button states.
     */
    private void perform ()
    {
	if (myUserButton.isSelected () || myChannelButton.isSelected ())
	{
	    speak ();
	}
	else if (myNicknameButton.isSelected ())
	{
	    changeNickname ();
	}
	else if (myTopicButton.isSelected ())
	{
	    changeTopic ();
	}
	else if (myRawSendButton.isSelected ())
	{
	    rawSend ();
	}
	else if (myJoinLeaveButton.isSelected ())
	{
	    joinLeave ();
	}
    }

    /**
     * Speak according to the fields of this object.
     */
    private void speak ()
    {
	String text = myText2.getText ();
	String kind = SpeechKinds.SAYS; // BUG--should be selectable
	String locName = myText1.getText ();
	boolean isUser = myUserButton.isSelected ();

	ChatLocus locus;
	if (myUserButton.isSelected ())
	{
	    locus = myIdentity.nameToUser (locName);
	}
	else
	{
	    locus = myIdentity.nameToChannel (locName);
	}

	if (locus == null)
	{
	    JOptionPane.showMessageDialog (
	        null,
		"Bad destination name: " + locName,
		"UberChat Error",
		JOptionPane.ERROR_MESSAGE);
	    return;
	}

	locus.speak (kind, text);
	myText2.setText ("");
    }

    /**
     * Do a raw send according to the value in the appropriate text
     * field.
     */
    private void rawSend ()
    {
	// BUG--no option for RAW_NO_NL
	String raw = myText2.getText ();
	myIdentity.speak (SpeechKinds.RAW, raw);
	myText2.setText ("");
    }

    /**
     * Do a join/leave channel according to the value in the appropriate text
     * field.
     */
    private void joinLeave ()
    {
	String chanName = myText2.getText ();
	ChatChannel chan = myIdentity.nameToChannel (chanName);

	if (chan == null)
	{
	    JOptionPane.showMessageDialog (
	        null,
		"Bad channel name: " + chanName,
		"UberChat Error",
		JOptionPane.ERROR_MESSAGE);
	    return;
	}

	if (chan.getJoinedState () == ChatChannel.JOINED)
	{
	    chan.leave ();
	}
	else
	{
	    chan.join ();
	}

	myText2.setText ("");
    }

    /**
     * Change the nickname according to the value in the appropriate text
     * field.
     */
    private void changeNickname ()
    {
	String nick = myText2.getText ();
	myIdentity.setNickname (nick);
	myText2.setText ("");
    }

    /**
     * Change the topic according to the fields of this object.
     */
    private void changeTopic ()
    {
	String topic = myText2.getText ();
	String chanName = myText1.getText ();

	ChatChannel chan = myIdentity.nameToChannel (chanName);

	if (chan == null)
	{
	    JOptionPane.showMessageDialog (
	        null,
		"Bad channel name: " + chanName,
		"UberChat Error",
		JOptionPane.ERROR_MESSAGE);
	    return;
	}

	chan.setTopic (topic);
	myText2.setText ("");
    }

    // ------------------------------------------------------------------------
    // Private helper classes

    /**
     * This is the action listener for the text-to-send field. It attempts
     * to send a message whenever activated.
     */
    private class MyActionListener
    extends AbstractAction
    {
	public void actionPerformed (ActionEvent e) 
	{
	    Object source = e.getSource ();
	    if (source == myText2)
	    {
		perform ();
	    }
	    else if (source == myUserButton)
	    {
		myLabel1.setText ("user");
		myLabel2.setText ("message");
		myText1.setEnabled (true);
	    }
	    else if (source == myChannelButton)
	    {
		myLabel1.setText ("channel");
		myLabel2.setText ("message");
		myText1.setEnabled (true);
	    }
	    else if (source == myNicknameButton)
	    {
		myLabel1.setText ("");
		myLabel2.setText ("nickname");
		myText1.setText ("");
		myText1.setEnabled (false);
	    }
	    else if (source == myTopicButton)
	    {
		myLabel1.setText ("channel");
		myLabel2.setText ("topic");
		myText1.setEnabled (true);
	    }
	    else if (source == myRawSendButton)
	    {
		myLabel1.setText ("");
		myLabel2.setText ("raw");
		myText1.setText ("");
		myText1.setEnabled (false);
	    }
	    else if (source == myJoinLeaveButton)
	    {
		myLabel1.setText ("");
		myLabel2.setText ("channel");
		myText1.setText ("");
		myText1.setEnabled (false);
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
}
    
