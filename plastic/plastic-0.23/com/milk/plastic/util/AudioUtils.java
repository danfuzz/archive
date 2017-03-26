package com.milk.plastic.util;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;

/**
 * Collection of helper methods for dealing with audio.
 *
 * <p>Copyright (C) 2000, Dan Bornstein, danfuzz@milk.com. All Rights 
 * Reserved. (Shrill TV degreaser.)</p>
 * 
 * <p>This file is part of the MILK Kodebase. The contents of this file are
 * subject to the MILK Kodebase Public License; you may not use this file
 * except in compliance with the License. A copy of the MILK Kodebase Public
 * License has been included with this distribution, and may be found in the
 * file named "LICENSE.html". You may also be able to obtain a copy of the
 * License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!</p>
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class AudioUtils
{
    // ------------------------------------------------------------------------
    // constructor

    /**
     * This class is uninstantiable.
     */
    private AudioUtils ()
    {
	// this space intentionally left blank
    }

    // ------------------------------------------------------------------------
    // static public methods

    /**
     * Close all audio mixers. If you don't do this, the vm may hang around
     * forever. (Actually, circa the year 2000, it will do so even if you
     * do call this.) 
     */
    static public void closeAll ()
    {
	Mixer.Info[] mi = AudioSystem.getMixerInfo ();
	for (int i = 0; i < mi.length; i++)
	{
	    Mixer m = AudioSystem.getMixer (mi[i]);
	    m.close ();
	}
    }
}
