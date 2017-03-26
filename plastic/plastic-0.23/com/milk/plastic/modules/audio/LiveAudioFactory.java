package com.milk.plastic.modules.audio;

import com.milk.plastic.iface.FieldRef;
import com.milk.plastic.iface.Module;
import com.milk.plastic.iface.PlasticException;
import com.milk.plastic.iface.Template;
import com.milk.plastic.util.BaseFactory;
import java.util.Map;

/**
 * A factory for live audio modules. These modules take an arbitrary
 * number of inputs (limited by Java's sampled audio facilities) and
 * turn them into a stream of audio to be played back in real time.
 *
 * <p>The base arguments for this factory are:</p>
 *
 * <dl>
 * <dt><code>clock: integer</code></dt>
 * <dd>The number of samples of each input to consume per second of audio
 * output (that is, the sample rate in Hz).</dd>
 * <dt><code>bits: integer</code></dt>
 * <dd>The number of bits per output sample. The inputs are assumed to
 * be in the range [-1..1] and is scaled accordingly.</dd>
 * <dt><code>bufSize: integer</code></dt>
 * <dd>The number of samples of each input to buffer. A larger buffer
 * means smoother playing, but more latency before anything is heard.</dd>
 * <dt><code>count: integer</code></dt>
 * <dd>The number of inputs to take. The inputs are named simply
 * <code>in_<i>n</i></code>, for <code><i>n</i></code> starting at
 * <code>0</code>.</dd>
 * </dl>
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
final public class LiveAudioFactory
extends BaseFactory
{
    /** short name */
    private static final String SHORT_NAME = "LiveAudio";

    /** base template */
    private static final Template TEMPLATE =
	new Template (new String[] { "clock", "bits", "bufSize", "count" },
		      new Class[] { Integer.class, Integer.class, 
				    Integer.class, Integer.class });

    // ------------------------------------------------------------------------
    // constructor
    
    public LiveAudioFactory ()
    {
	super (SHORT_NAME, TEMPLATE);
    }

    // ------------------------------------------------------------------------
    // methods the superclass requires us to implement

    // superclass's comment suffices
    protected Template getObjectTemplate1 (Map args)
	throws PlasticException
    {
	int count = ((Integer) args.get ("count")).intValue ();

	String[] names = new String[count];
	Class[] types = new Class[count];

	for (int i = 0; i < count; i++)
	{
	    names[i] = "in_" + i;
	    types[i] = FieldRef.class;
	}

	return TEMPLATE.withMore (names, types);
    }

    // superclass's comment suffices
    protected Object make1 (Template template, Map args)
	throws PlasticException
    {
	int count = ((Integer) args.get ("count")).intValue ();
	int bits = ((Integer) args.get ("bits")).intValue ();

	if ((count == 1) && (bits == 16))
	{
	    return new LiveAudio16BitMono (this, template, args);
	}
	else if ((count == 2) && (bits == 16))
	{
	    return new LiveAudio16BitStereo (this, template, args);
	}
	else
	{
	    throw new PlasticException ("Unsupported LiveAudio " +
					"configuration (" + bits + "/" + 
					count + ").");
	}
    }
}

