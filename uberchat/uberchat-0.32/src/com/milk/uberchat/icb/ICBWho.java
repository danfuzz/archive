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

import java.text.DateFormat;
import java.util.Date;

/**
 * This class merely holds data representing a "who" line.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
/*package*/ final class ICBWho
{
    /** format to use for <code>toString</code> */
    static private final DateFormat TheDateFormat = 
	DateFormat.getDateTimeInstance ();

    /** true if this user is moderator */
    public final boolean myIsModerator;

    /** the userid */
    public final String myUserid;

    /** the idle-since time as standard Java time value */
    public final long myIdleTime;

    /** the login time as standard Java time value */
    public final long myLoginTime;

    /** the email userid */
    public final String myEmail;

    /** the client hostname */
    public final String myHost;

    /** true if the userid is registered with the system */
    public final boolean myIsRegistered;

    /** string form */
    private String myToString;

    /**
     * Construct an <code>ICBWho</code>
     *
     * @param isModerator true if this user is the moderator
     * @param userid the userid
     * @param idleTime the idle time in seconds
     * @param loginTime the login time as seconds since Jan 1, 1970 GMT
     * @param email the email userid
     * @param host the client hostname
     * @param isRegistered true if this userid is registered with the system
     */
    public ICBWho (boolean isModerator, String userid, int idleTime,
		   long loginTime, String email, String host,
		   boolean isRegistered)
    {
	myIsModerator = isModerator;
	myUserid = userid;
	myIdleTime = System.currentTimeMillis () - (idleTime * 1000);
	myLoginTime = loginTime * 1000;
	myEmail = email;
	myHost = host;
	myIsRegistered = isRegistered;
	myToString = null;
    }

    /**
     * Make a nice string out of this mess.
     *
     * @return the nice string
     */
    public String toString ()
    {
	if (myToString == null)
	{
	    StringBuffer sb = new StringBuffer ();
	    sb.append ("{ICBWho ");
	    sb.append (myUserid);
	    sb.append (' ');
	    sb.append (myEmail);
	    sb.append ('@');
	    sb.append (myHost);
	    sb.append ("; idle since ");
	    sb.append (TheDateFormat.format (new Date (myIdleTime)));
	    sb.append ("; login at ");
	    sb.append (TheDateFormat.format (new Date (myLoginTime)));
	    if (myIsModerator)
	    {
		sb.append ("; moderator");
	    }
	    if (myIsRegistered)
	    {
		sb.append ("; registered");
	    }
	    sb.append ('}');
	    myToString = sb.toString ();
	}
	return myToString;
    }
}
