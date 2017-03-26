// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.uberchat.irc;

import com.milk.uberchat.iface.ChatSystem;
import com.milk.uberchat.iface.ChatSystemTemplate;
import com.milk.objed.CheckboxEditor;
import com.milk.objed.Editable;
import com.milk.objed.Editor;
import com.milk.objed.FieldValueEditor;
import com.milk.objed.FixedFieldsEditor;
import com.milk.objed.IntegerTextEditor;
import com.milk.objed.PublicFields;
import com.milk.objed.StringTextEditor;
import com.milk.objed.ValueEditor;

/**
 * This is the template class for <code>IRCSystem</code>s.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public class IRCSystemTemplate
implements ChatSystemTemplate, Editable
{
    /** the system name */
    public String myName;

    /** the host to connect to */
    public String myHost;

    /** the port to connect to */
    public int myPort;

    /** the chat userid to use */
    public String myUserid;

    /** the password to use */
    public String myPassword;

    /** the real name to use */
    public String myRealName;

    /** the user name to use */
    public String myUserName;

    /** the verbose ctcp flag */
    public boolean myVerboseCtcp;

    /** the auto-join flag */
    public boolean myAutoJoin;

    /** null-ok; the editor that edits us, if any */
    private FixedFieldsEditor myEditor;

    /**
     * Construct a new template with default values.
     */
    public IRCSystemTemplate ()
    {
	myName       = "irc";
	myHost       = "irc.dnalounge.com";
	myPort       = 6667;
	myUserid     = "";
	myPassword   = "";
	myRealName   = "";
	myUserName   = "";
	myVerboseCtcp = true;
	myAutoJoin    = true;

	myEditor = null;
    }

    /**
     * Make a copy of this template.
     *
     * @return the copy
     */
    public ChatSystemTemplate copy ()
    {
	IRCSystemTemplate result = new IRCSystemTemplate ();
	result.myName       = myName;
	result.myHost       = myHost;
	result.myPort       = myPort;
	result.myUserid     = myUserid;
	result.myPassword   = myPassword;
	result.myRealName   = myRealName;
	result.myUserName   = myUserName;
	result.myVerboseCtcp = myVerboseCtcp;
	result.myAutoJoin    = myAutoJoin;
	return result;
    }

    /**
     * Turn this template into an actual <code>ChatSystem</code>.
     *
     * @return a <code>ChatSystem</code> for this template
     */
    public ChatSystem makeSystem ()
    {
	return new IRCSystem (this);
    }

    /**
     * Get an editor for this template.
     *
     * @return an editor for this template
     */
    public Editor getEditor ()
    {
	if (myEditor == null)
	{
	    ValueEditor[] fields = new ValueEditor[9];
	    IntegerTextEditor ite;
	    StringTextEditor ste;
	    CheckboxEditor cbe;

	    fields[0] = 
		new FieldValueEditor (
                    "system name",
		    "the name of the system as it will appear in menus, etc.",
		    true, false, this, "myName");
	    fields[0] = new StringTextEditor (fields[0]);

	    fields[1] = 
		new FieldValueEditor (
                    "host name",
		    "the name of the host to connect to",
		    true, false, this, "myHost");
	    fields[1] = new StringTextEditor (fields[1]);

	    fields[2] = 
		new FieldValueEditor (
                    "host port",
		    "the port number to connect to on the host",
		    true, false, this, "myPort");
	    fields[2] = ite = new IntegerTextEditor (fields[2]);
	    ite.setValueRestrictions (0, 65535);

	    fields[3] = 
		new FieldValueEditor (
                    "irc userid",
		    "the userid to login as; this is the name attached to " +
		    "all the messages you send",
		    true, false, this, "myUserid");
	    fields[3] = new StringTextEditor (fields[3]);

	    fields[4] = 
		new FieldValueEditor (
                    "password",
		    "the password to login with; this may be blank if your " +
		    "userid isn't registered with the host",
		    true, false, this, "myPassword");
	    fields[4] = ste = new StringTextEditor (fields[4]);
	    ste.setHidden (true);

	    fields[5] = 
		new FieldValueEditor (
                    "real name",
		    "your real (human) name",
		    true, false, this, "myRealName");
	    fields[5] = new StringTextEditor (fields[5]);

	    fields[6] = 
		new FieldValueEditor (
                    "email userid",
		    "your email userid, i.e., the userid part of your email " +
		    "address",
		    true, false, this, "myUserName");
	    fields[6] = new StringTextEditor (fields[6]);

	    fields[7] = 
		new FieldValueEditor (
                    "verbose CTCP",
		    "if on, CTCP messages are very visible to you",
		    true, false, this, "myVerboseCtcp");
	    fields[7] = cbe = new CheckboxEditor (fields[7]);
	    cbe.setCheckboxText ("Make CTCP messages very visible.");

	    fields[8] = 
		new FieldValueEditor (
                    "auto-join",
		    "if on, you will automatically join channels that " +
		    "you are invited to",
		    true, false, this, "myAutoJoin");
	    fields[8] = cbe = new CheckboxEditor (fields[8]);
	    cbe.setCheckboxText ("Automatically join channels that you are " +
				 "invited to.");

	    myEditor = 
		new FixedFieldsEditor (
                    "IRC System Description",
		    "Describe the parameters for an IRC system.",
		    fields);
	}

	return myEditor;
    }
}
