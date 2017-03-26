package com.milk.plastic.modules.audio;

import com.milk.plastic.ports.DoublePort;
import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.PlasticException;
import com.milk.plastic.iface.Ref;
import com.milk.plastic.iface.Template;
import com.milk.plastic.util.BaseModule;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * Module for live audio with two channels of 16 bits per sample.
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
final public class AudioFileInStereo
extends BaseModule
{
    /** the ratio of inputs to outputs */
    private double myRate;

    /** the name of the file to read */
    private String myFileName;

    /** first output port */
    private DoublePort myOutPort0;

    /** second output port */
    private DoublePort myOutPort1;

    /** null-ok; stream to read */
    private AudioInputStream myStream;

    /** null-ok; buffer to read into */
    private byte[] myBuffer;

    /** index into current sample */
    private double myIndex;

    // ------------------------------------------------------------------------
    // constructor
    
    /**
     * Construct an instance.
     *
     * @param factory the factory which spawned this instance
     * @param template the full template for this instance
     * @param args the arguments which this instance was constructed from 
     */
    public AudioFileInStereo (Factory factory, Template template, Map args)
    {
	super (factory, template, args);

	myRate = ((Double) args.get ("rate")).doubleValue ();
	myFileName = (String) args.get ("fileName");

	myOutPort0 = (DoublePort) getField ("out_0");
	myOutPort1 = (DoublePort) getField ("out_1");
    }

    // ------------------------------------------------------------------------
    // public methods the superclass requires us to implement

    // interface's comment suffices
    public void tick ()
    {
	byte[] buf = myBuffer;

	double index = myIndex;
	while (index >= 1.0)
	{
	    index--;
	    try
	    {
		myStream.read (buf, 0, 4);
	    }
	    catch (IOException ex)
	    {
		throw new PlasticException ("Trouble reading stream", ex);
	    }
	}

	myIndex = index + myRate;

	int value0 = ((int) buf[0] & 0xff) + (((int) buf[1]) << 8);
	int value1 = ((int) buf[2] & 0xff) + (((int) buf[3]) << 8);

	myOutPort0.setDouble ((double) (value0 / 32767.0));
	myOutPort1.setDouble ((double) (value1 / 32767.0));
    }

    // ------------------------------------------------------------------------
    // protected methods the superclass requires us to implement

    // superclass's comment suffices
    protected void bind1 ()
    {
	AudioInputStream ais;

	// just open the file
	try
	{
	    ais = AudioSystem.getAudioInputStream (new File (myFileName));
	}
	catch (Exception ex)
	{
	    throw new PlasticException ("Unable to open audio file: " + 
					myFileName,
					ex);
	}

	// try to coerce it to a format we like
	try
	{
	    AudioFormat already = ais.getFormat ();
	    AudioFormat format = 
		new AudioFormat (already.getSampleRate (),
				 16, 2, true, false);
	    myStream = AudioSystem.getAudioInputStream (format, ais);
	}
	catch (Exception ex)
	{
	    throw new PlasticException ("Unable to convert audio file: " +
					myFileName + " / " + ais.getFormat ());
	}

	myBuffer = new byte[4];
	myIndex = 1.0; // force a read in the first tick
    }

    // superclass's comment suffices
    protected void reset1 ()
    {
	if (myStream != null)
	{
	    try
	    {
		myStream.close ();
	    }
	    catch (IOException ex)
	    {
		throw new PlasticException ("Trouble closing stream", ex);
	    }
	    
	    myStream = null;
	}

	myBuffer = null;
    }
}
