package com.milk.plastic.modules.audio;

import com.milk.plastic.iface.Module;
import com.milk.plastic.iface.PlasticException;
import com.milk.plastic.iface.Template;
import com.milk.plastic.ports.DoublePort;
import com.milk.plastic.util.BaseFactory;
import java.util.Map;

/**
 * A factory for generating output based on the data in an audio file.
 * These modules take no input ports and produce one or two outputs,
 * depending on the input file.
 *
 * <p>The base arguments for this factory are:</p>
 *
 * <dl>
 * <dt><code>fileName: string</code></dt>
 * <dd>The name of the file to read.</dd>
 * <dt><code>rate: double</code></dt>
 * <dd>The rate to produce output. 1.0 means one output per frame in the
 * input file. Larger numbers mean to skip some input frames, and smaller
 * numbers mean to duplicate some.</dd>
 * <dt><code>count: integer</code></dt>
 * <dd>The number of channels of output. If the number doesn't match the
 * format of the file, it may or may not be possible to convert it,
 * depending on the whims of the Java audio library.</dd>
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
final public class AudioFileInFactory
extends BaseFactory
{
    /** short name */
    private static final String SHORT_NAME = "AudioFileIn";

    /** base template */
    private static final Template TEMPLATE =
	new Template (new String[] { "rate", "fileName", "count" },
		      new Class[] { Double.class, String.class, 
				    Integer.class });

    // ------------------------------------------------------------------------
    // constructor
    
    public AudioFileInFactory ()
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
	    names[i] = "out_" + i;
	    types[i] = DoublePort.class;
	}

	return TEMPLATE.withMore (names, types);
    }

    // superclass's comment suffices
    protected Object make1 (Template template, Map args)
	throws PlasticException
    {
	int count = ((Integer) args.get ("count")).intValue ();

	if (count == 1)
	{
	    return new AudioFileInMono (this, template, args);
	}
	else if (count == 2)
	{
	    return new AudioFileInStereo (this, template, args);
	}
	else
	{
	    throw new PlasticException ("Unsupported AudioFileIn " +
					"configuration (" + count + 
					" channels).");
	}	    
    }
}

