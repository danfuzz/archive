// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.uberchat;

/**
 * This class merely holds string constants for use as the "kind" field
 * of speech messages and methods for mangling kind/text into a nicer
 * human form and for unmangling back to the two separate pieces.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998 Dan Bornstein, all rights reserved. 
 */
public final class SpeechKinds
{
    public static final String BEEP = "beep";
    public static final String ECHOES = "echoes";
    public static final String ME = "me";
    public static final String MY = "my";
    public static final String SAYS = "says";
    public static final String SINGS = "sings";
    public static final String THINKS = "thinks";
    public static final String SAYS_TO = "to ";

    /** speech kind for a raw send to the host system, NL-terminated if
        appropriate */
    public static final String RAW = "raw";

    /** speech kind for a raw send to the host system, no NL-termination */
    public static final String RAW_NO_NL = "raw-";

    /** This class is uninstantiable. */
    private SpeechKinds ()
    {
	// this space intentionally left blank
    }

    /**
     * Take a kind and a message and turn it into a nicer human form,
     * for systems that don't directly support particular kinds.
     *
     * @param kind the kind (should be interned)
     * @param text the message text
     * @return the better human form
     */
    static public String mangleSpeech (String kind, String text)
    {
	kind = kind.intern ();
	if (kind == SAYS)
	{
	    return text;
	}
	else if (kind == ECHOES)
	{
	    return "< " + text + " >";
	}
	else if (kind == ME)
	{
	    return "*" + text + "*";
	}
	else if (kind == MY)
	{
	    return "*'s " + text + "*";
	}
	else if (kind == SINGS)
	{
	    return "o/~ " + text;
	}
	else if (kind == THINKS)
	{
	    return ". o O ( " + text + " )";
	}
	else
	{
	    return "[*" + kind + "*] " + text;
	}
    }

    /**
     * Take a human text form and attempt to extract out a kind.
     *
     * @param orig the human text form
     * @return an array of two Strings, first is the kind, second is
     * the (possibly) modified text
     */
    static public String[] unmangleSpeech (String orig)
    {
	String[] result = new String[2];
	int len = orig.length ();

	if (orig.startsWith ("[*"))
	{
	    // explicit kind (hopefully)
	    int kindEnd = orig.indexOf ("*] ", 2);
	    if (kindEnd == -1)
	    {
		// nope, just normal we assume
		result[0] = SAYS;
		result[1] = orig;
	    }
	    else
	    {
		result[0] = orig.substring (2, kindEnd).intern ();
		result[1] = orig.substring (kindEnd + 3);
	    }
	}
	else if (orig.startsWith ("*'s ") && (orig.charAt (len - 1) == '*'))
	{
	    // "my" form
	    result[0] = MY;
	    result[1] = orig.substring (4, len - 1);
	}
	else if (   (len > 2) 
		 && (orig.charAt (0) == '*')
		 && (orig.charAt (len - 1) == '*'))
	{
	    // "me" form
	    result[0] = ME;
	    result[1] = orig.substring (1, len - 1);
	}
	else if (   (len > 4) 
		 && (orig.startsWith ("< "))
		 && (orig.endsWith (" >")))
	{
	    // "echoes" form
	    result[0] = ECHOES;
	    result[1] = orig.substring (2, len - 2);
	}
	else if (orig.startsWith ("o/~ "))
	{
	    // "sings" form
	    result[0] = SINGS;
	    result[1] = orig.substring (4);
	}
	else if (   (   orig.startsWith (". o O (")
		     && orig.endsWith (")"))
		 || (   orig.startsWith (". o O {")
		     && orig.endsWith ("}")))
	{
	    // "thinks" form
	    result[0] = THINKS;
	    result[1] = orig.substring (7, len - 1);
	    if (result[1].startsWith (" "))
	    {
		result[1] = result[1].substring (1);
	    }
	    if (result[1].endsWith (" "))
	    {
		result[1] = result[1].substring (0, result[1].length () - 1);
	    }
	}
	else
	{
	    // just normal text
	    result[0] = SAYS;
	    result[1] = orig;
	}

	return result;
    }
}
