// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.uberchat.icb;

import com.milk.uberchat.iface.ChatSystem;
import com.milk.uberchat.iface.ChatSystemTemplate;
import com.milk.objed.Editable;
import com.milk.objed.Editor;
import com.milk.objed.FieldValueEditor;
import com.milk.objed.FixedFieldsEditor;
import com.milk.objed.IntegerTextEditor;
import com.milk.objed.PublicFields;
import com.milk.objed.StringTextEditor;
import com.milk.objed.ValueEditor;

/**
 * This is the template class for <code>ICBSystem</code>s.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public class ICBSystemTemplate
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

    /** the initial channel to connect to */
    public String myInitialChannel;

    /** the email userid to use */
    public String myEmail;

    /** null-ok; the editor that edits us, if any */
    private FixedFieldsEditor myEditor;

    /**
     * Construct a new template with default values.
     */
    public ICBSystemTemplate ()
    {
	myName           = "icb";
	myHost           = "default.icb.net";
	myPort           = 7326;
	myUserid         = "";
	myPassword       = "";
	myInitialChannel = "";
	myEmail          = "";

	myEditor = null;
    }

    /**
     * Make a copy of this template.
     *
     * @return the copy
     */
    public ChatSystemTemplate copy ()
    {
	ICBSystemTemplate result = new ICBSystemTemplate ();
	result.myName           = myName;
	result.myHost           = myHost;
	result.myPort           = myPort;
	result.myUserid         = myUserid;
	result.myPassword       = myPassword;
	result.myInitialChannel = myInitialChannel;
	result.myEmail          = myEmail;
	return result;
    }

    /**
     * Turn this template into an actual <code>ChatSystem</code>.
     *
     * @return a <code>ChatSystem</code> for this template
     */
    public ChatSystem makeSystem ()
    {
	return new ICBSystem (this);
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
	    ValueEditor[] fields = new ValueEditor[7];
	    IntegerTextEditor ite;
	    StringTextEditor ste;

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
                    "icb userid",
		    "the userid to login as; this is the name attached to " +
		    "all the messages you send",
		    true, false, this, "myUserid");
	    fields[3] = ste = new StringTextEditor (fields[3]);
	    ste.setLengthRestrictions (0, 12, 12);

	    fields[4] = 
		new FieldValueEditor (
                    "password",
		    "the password to login with; this may be blank if your " +
		    "icb userid isn't registered with the host",
		    true, false, this, "myPassword");
	    fields[4] = ste = new StringTextEditor (fields[4]);
	    ste.setHidden (true);

	    fields[5] = 
		new FieldValueEditor (
                    "initial channel",
		    "the initial channel to join; this may be left blank",
		    true, false, this, "myInitialChannel");
	    fields[5] = ste = new StringTextEditor (fields[5]);
	    ste.setLengthRestrictions (0, 12, 12);

	    fields[6] = 
		new FieldValueEditor (
                    "email name",
		    "your email name; this should be just the userid part " +
		    "of your email address; " +
		    "(that is, do not include \"@whatever.blah\")",
		    true, false, this, "myEmail");
	    fields[6] = ste = new StringTextEditor (fields[6]);
	    ste.setLengthRestrictions (0, 12, 12);

	    myEditor = 
		new FixedFieldsEditor (
                    "ICB System Description",
		    "Describe the parameters for an ICB system.",
		    fields);
	}

	return myEditor;
    }
}
