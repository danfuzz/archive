package com.milk.plastic.modules.audio;

import com.milk.plastic.ports.DoublePort;
import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.PlasticException;
import com.milk.plastic.iface.Ref;
import com.milk.plastic.iface.Template;
import com.milk.plastic.util.BaseModule;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Module for output to an audio file with two channels of 8 bits per
 * sample.
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
final public class AudioFileOut8BitStereo
extends BaseModule
{
    /** the declared sample rate of the file */
    private double myClock;

    /** the name of the file to write */
    private String myFileName;

    /** first input port */
    private DoublePort myInPort0;

    /** second input port */
    private DoublePort myInPort1;

    /** null-ok; the temporary file that got all the raw output */
    private File myTempFile;

    /** null-ok; stream to write */
    private FileOutputStream myStream;

    /** null-ok; buffer to use for writes */
    private byte[] myBuffer;

    // ------------------------------------------------------------------------
    // constructor
    
    /**
     * Construct an instance.
     *
     * @param factory the factory which spawned this instance
     * @param template the full template for this instance
     * @param args the arguments which this instance was constructed from 
     */
    public AudioFileOut8BitStereo (Factory factory, Template template, 
				   Map args)
    {
	super (factory, template, args);

	myClock = ((Double) args.get ("clock")).doubleValue ();
	myFileName = (String) args.get ("fileName");
    }

    // ------------------------------------------------------------------------
    // public methods the superclass requires us to implement

    // interface's comment suffices
    public void tick ()
    {
	byte value0 = (byte) (myInPort0.getDouble () * 127);
	byte value1 = (byte) (myInPort1.getDouble () * 127);

	byte[] buf = myBuffer;
	buf[0] = (byte) value0;
	buf[1] = (byte) value1;

	try
	{
	    myStream.write (buf);
	}
	catch (IOException ex)
	{
	    throw new PlasticException ("Trouble writing stream", ex);
	}
    }

    // ------------------------------------------------------------------------
    // protected methods the superclass requires us to implement

    // superclass's comment suffices
    protected void bind1 ()
    {
	myInPort0 = (DoublePort) getBoundObject ("in_0");
	myInPort1 = (DoublePort) getBoundObject ("in_1");

	try
	{
	    // make a temp file to hold the raw output samples
	    myTempFile = File.createTempFile ("out", null);
	    myTempFile.deleteOnExit ();
	    myStream = new FileOutputStream (myTempFile);
	}
	catch (IOException ex)
	{
	    throw new PlasticException ("Unable to open temporary output file",
					ex);
	}

	myBuffer = new byte[2];
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
	myInPort0 = null;
	myInPort1 = null;

	// now convert the raw output into a real file
	AudioFileOutFactory.makeFinalFile (myTempFile,
					   new File (myFileName),
					   myClock,
					   8, 
					   2);
	myTempFile = null;
    }
}
