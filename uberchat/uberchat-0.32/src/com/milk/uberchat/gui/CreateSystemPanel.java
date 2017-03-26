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
import com.milk.objed.gui.EditorPanel;
import com.milk.uberchat.iface.ChatSystem;
import com.milk.uberchat.iface.ChatSystemTemplate;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import javax.swing.Box;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * This is a panel which has fill-in fields for the parameters of
 * a connection, along with a "create" button.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class CreateSystemPanel
extends EditorPanel
{
    private MasterControl myMaster;
    private ChatSystemTemplate myTemplate;
    private JFrame myFrame;

    /**
     * Create a <code>CreateSystemPanel</code> for the given chat system
     * class name. 
     */
    public CreateSystemPanel (MasterControl master, 
			      ChatSystemTemplate template,
			      JFrame frame) 
    {
	super (master.getEditorControl ().
	           makeComponent (template.getEditor ()),
	       frame);
	
	myMaster = master;
	myTemplate = template;
	myFrame = frame;

	JPanel panel = (JPanel) getViewport ().getView ();

	panel.add (Box.createVerticalStrut (4));
	panel.add (Box.createVerticalGlue ());

	Box buttonBox = Box.createHorizontalBox ();
	panel.add (buttonBox);
	JButton createButton = new JButton ("Create");
        createButton.setMnemonic ('c');
	buttonBox.add (Box.createHorizontalGlue ());
	buttonBox.add (createButton);
	buttonBox.add (Box.createHorizontalGlue ());

        // register a listener for the button
	CreateButtonListener listener = new CreateButtonListener ();
	createButton.addActionListener (listener);
    }

    /**
     * Create and show a frame with a <code>CreateSystemPanel</code> in it.
     *
     * @param master the master control for the app
     * @param template the template to user
     */
    public static void makeFrame (MasterControl master, 
				  ChatSystemTemplate template) 
    {
	try
	{
	    JFrame frame = new JFrame ();
	    CreateSystemPanel panel = 
		new CreateSystemPanel (master, template, frame);
	    frame.setDefaultCloseOperation (frame.DISPOSE_ON_CLOSE);
	    frame.pack ();
	    frame.setVisible (true);
	}
	catch (Exception ex)
	{
	    JOptionPane.showMessageDialog (
	        null,
		"Trouble creating panel:\n" + ex.getMessage (),
		"UberChat",
		JOptionPane.ERROR_MESSAGE);
	    return;
	}
    }

    // ------------------------------------------------------------------------
    // Private helper classes

    /** This is an ActionListener that listens to the "create" button. */
    private class CreateButtonListener 
    extends AbstractAction 
    {
	public void actionPerformed (ActionEvent e) 
	{
	    if (myFrame != null)
	    {
		myFrame.dispose ();
	    }

	    ChatSystem sys;

	    try
	    {
		sys = myTemplate.makeSystem ();
	    }
	    catch (Exception ex)
	    {
		JOptionPane.showMessageDialog (
                    null,
		    "Trouble instantiating system:\n" + ex.getMessage (),
		    "UberChat",
		    JOptionPane.ERROR_MESSAGE);
		ex.printStackTrace ();
		return;
	    }

	    myMaster.newSystem (sys);
	}
    }

}
