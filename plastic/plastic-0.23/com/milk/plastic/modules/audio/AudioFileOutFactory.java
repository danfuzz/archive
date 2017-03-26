package com.milk.plastic.modules.audio;

import com.milk.plastic.iface.FieldRef;
import com.milk.plastic.iface.Module;
import com.milk.plastic.iface.PlasticException;
import com.milk.plastic.iface.Template;
import com.milk.plastic.util.BaseFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * A factory for accpeting input to be placed into an audio file.
 * These modules take one or two input ports and produce no in-model outputs.
 *
 * <p>The base arguments for this factory are:</p>
 *
 * <dl>
 * <dt><code>fileName: string</code></dt>
 * <dd>The name of the file to write.</dd>
 * <dt><code>clock: double</code></dt>
 * <dd>The declared rate of the output file in samples per second.</dd>
 * <dt><code>bits: integer</code></dt>
 * <dd>The number of bits per sample for the output.</dd>
 * <dt><code>count: integer</code></dt>
 * <dd>The number of channels of input.</dd>
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
final public class AudioFileOutFactory
extends BaseFactory
{
    /** short name */
    private static final String SHORT_NAME = "AudioFileOut";

    /** base template */
    private static final Template TEMPLATE =
	new Template (new String[] { "clock", "fileName", "count", "bits" },
		      new Class[] { Double.class, String.class, 
				    Integer.class, Integer.class });

    // ------------------------------------------------------------------------
    // constructor
    
    public AudioFileOutFactory ()
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

	if (bits == 8)
	{
	    if (count == 1)
	    {
		return new AudioFileOut8BitMono (this, template, args);
	    }
	    else if (count == 2)
	    {
		return new AudioFileOut8BitStereo (this, template, args);
	    }
	}
	else if (bits == 16)
	{
	    if (count == 1)
	    {
		return new AudioFileOut16BitMono (this, template, args);
	    }
	    else if (count == 2)
	    {
		return new AudioFileOut16BitStereo (this, template, args);
	    }
	}

	throw new PlasticException ("Unsupported AudioFileOut " +
				    "configuration (" + count + 
				    " channels of " + bits + 
				    " bits per sample).");
    }



    // ------------------------------------------------------------------------
    // static public methods

    /**
     * Convert a temporary file of raw sample values into a file with
     * a proper audio format. When done, the temporary file is removed.
     *
     * @param tempFile the temporary file
     * @param outFile the final output file
     * @param clock the output sample rate (samples per second)
     * @param bits the number of bits per sample (typically 8 or 16)
     * @param channels the number of channels (typically 1 or 2)
     */
    static public void makeFinalFile (File tempFile, File outFile,
				      double clock, int bits, int channels)
    {
	AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

	String fileName = outFile.getName ();
	if (fileName.endsWith (".aiff"))
	{
	    fileType = AudioFileFormat.Type.AIFF;
	}
	else if (fileName.endsWith (".aifc"))
	{
	    fileType = AudioFileFormat.Type.AIFC;
	}
	else if (fileName.endsWith (".au"))
	{
	    fileType = AudioFileFormat.Type.AU;
	}
	else if (fileName.endsWith (".snd"))
	{
	    fileType = AudioFileFormat.Type.SND;
	}

	try
	{
	    AudioFormat fmt = 
		new AudioFormat ((float) clock, bits, channels, true, false);
	    long frameCount = tempFile.length () / fmt.getFrameSize ();
	    FileInputStream fis = new FileInputStream (tempFile);
	    AudioInputStream ais = new AudioInputStream (fis, fmt, frameCount);
	    AudioSystem.write (ais, fileType, outFile);
	}
	catch (IOException ex)
	{
	    throw new PlasticException ("trouble writing audio file: " + 
					outFile,
					ex);
	}
    
	tempFile.delete ();
    }
}

