// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.uberchat.event;

import com.milk.uberchat.SpeechKinds;
import com.milk.uberchat.iface.ChatUser;

/**
 * This class holds the details of a <code>userBroadcast</code> event.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public final class UserBroadcastDetails
{
    /** the user that did the broadcast */
    private ChatUser myUser;

    /** the kind of message that was broadcast
     * @see com.milk.uberchat.SpeechKinds */
    private String myKind;

    /** the text that was broadcast */
    private String myText;

    /** the user's name/nick combo at the time the message was sent */
    private String myNameNickCombo;

    /** the message string as text, or null if not yet calculated */
    private String myMessageString;

    /** the speaker part of the message string, or null if not yet
     * calculated. */
    private String mySpeakerString;

    /**
     * Make a <code>UserBroadcastDetails</code>.
     *
     * @see com.milk.uberchat.SpeechKinds
     *
     * @param user the source user of the message
     * @param kind the kind of message (see <code>SpeechKinds</code>)
     * @param text the text of the message
     */
    public UserBroadcastDetails (ChatUser user, String kind, String text)
    {
	myUser = user;
	myKind = kind.intern ();
	myText = text;
	myNameNickCombo = myUser.getNameNickCombo ();
	myMessageString = null;
	mySpeakerString = null;
    }

    /**
     * We just return a labeled version of the message string.
     *
     * @return the string form of this object
     */
    public String toString ()
    {
	return "{UserBroadcastDetails: " + getMessageString () + "}";
    }

    /**
     * Get the speaker string for this message, suitable for printing out
     * for the user.
     *
     * @return the speaker string
     */
    public String getSpeakerString ()
    {
	if (mySpeakerString == null)
	{
	    StringBuffer s = new StringBuffer ();
	    s.append (myNameNickCombo);

	    if (myKind == SpeechKinds.SAYS)
	    {
		s.append (": ");
	    }
	    else if (myKind == SpeechKinds.ME)
	    {
		s.append (' ');
	    }
	    else if (myKind == SpeechKinds.MY)
	    {
		s.append ("'s ");
	    }
	    else if (myKind == SpeechKinds.ECHOES)
	    {
		s.insert (0, '(');
		s.append ("): ");
	    }
	    else if (myKind == SpeechKinds.BEEP)
	    {
		s.append (" [beep]");
		if (myText.length () != 0)
		{
		    s.append (": ");
		}
	    }
	    else
	    {
		s.append (' ');
		s.append (myKind);
		s.append (": ");
	    }

	    mySpeakerString = s.toString ();
	}

	return mySpeakerString;
    }

    /**
     * Get a simple text string representing the message, suitable
     * for printing out for the user.
     *
     * @return the message string
     */
    public String getMessageString ()
    {
	if (myMessageString == null)
	{
	    myMessageString = getSpeakerString () + myText;
	}

	return myMessageString;
    }

    /**
     * Get the user of the message.
     *
     * @return the user of the message
     */
    public ChatUser getUser ()
    {
	return myUser;
    }

    /**
     * Get the kind of the message.
     *
     * @see com.milk.uberchat.SpeechKinds
     *
     * @return the kind of the message
     */
    public String getKind ()
    {
	return myKind;
    }

    /**
     * Get the main text of the message.
     *
     * @return the main text of the message
     */
    public String getText ()
    {
	return myText;
    }

    /**
     * Get the name/nick combo of the user of the message at the time this
     * object was created.
     *
     * @return the name/nick combo 
     */
    public String getNameNickCombo ()
    {
	return myNameNickCombo;
    }
}
