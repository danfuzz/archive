package com.milk.stut;

import com.milk.stu.builtins.Builtins;
import com.milk.stu.iface.Environment;
import com.milk.stu.iface.StuNode;
import com.milk.stu.util.FileNames;
import com.milk.stu.util.FileUtils;
import com.milk.stu.parser.Parser;
import java.io.File;
import java.io.OutputStream;

public class Main
{
    static public void main (String[] args)
	throws Exception
    {
	Environment stdEnv = Builtins.makeStandardEnvironment ();

	String output = "-";
	String source = "-";
	boolean outputIsFile = true;
        String fileType = null;
        boolean forceStut = false;
	boolean problems = false;
	int profileEval = 0;

	for (int i = 0; i < args.length; i++)
	{
	    String a = args[i];
	    if (a.startsWith ("--output-dir="))
	    {
		output = a.substring (a.indexOf ('=') + 1);
		outputIsFile = false;
	    }
	    else if (a.startsWith ("--output-file="))
	    {
		output = a.substring (a.indexOf ('=') + 1);
		outputIsFile = true;
	    }
	    else if (a.startsWith ("--source="))
	    {
		source = a.substring (a.indexOf ('=') + 1);
	    }
	    else if (a.startsWith ("--type="))
	    {
		fileType = a.substring (a.indexOf ('=') + 1);
	    }
	    else if (a.startsWith ("--profile-eval="))
	    {
		profileEval = 
		    Integer.parseInt (a.substring (a.indexOf ('=') + 1));
	    }
	    else
	    {
		System.err.println ("unknown option: " + a);
		problems = true;
	    }
	}

	if (problems)
	{
	    System.exit (1);
	}

	File sourceFile = new File (source);

	if (sourceFile.isDirectory ())
	{	
	    StutProcessor sp = 
		new StutProcessor (source, output, outputIsFile, stdEnv);
	    sp.run ();
	}
	else
	{
	    StuNode node;
	    String outName = null;
	    String stutOut = FileNames.stutOutputName (source);
	    String stuOut = FileNames.stuOutputName (source);

	    if (fileType == null)
	    {
		if (stutOut != null)
		{
		    fileType = "stut";
		    outName = stutOut;
		}
		else if (stuOut != null)
		{
		    fileType = "stu";
		    outName = stuOut;
		}
		else
		{
		    System.err.println ("don't know how to interpret file: " +
					source);
		    System.exit (1);
		    // to shut the compiler up about var defs
		    throw new Error (); 
		}
	    }

	    if (fileType.equals ("stut"))
	    {
		node = Parser.parseTemplate (sourceFile);
	    }
	    else if (fileType.equals ("stu"))
	    {
		node = Parser.parseScript (sourceFile);
	    }
	    else
	    {
		System.err.println ("don't know how about type: " + fileType);
		System.exit (1);
		throw new Error (); // to shut the compiler up about var defs
	    }

	    if (profileEval > 0)
	    {
		System.err.print ("Repeating for profile: ");
		for (int i = 1; i < profileEval; i++)
		{
		    System.err.print ('.');
		    node.evalOutsideToChars (stdEnv);
		}
		System.err.println ('.');
	    }

	    CharSequence result = node.evalOutsideToChars (stdEnv);
	    
	    File outputFile = new File (output);

	    if (! outputIsFile)
	    {
		if (outName == null)
		{
		    System.err.println ("don't know how to name output");
		    System.exit (1);
		}

		outputFile = new File (outputFile, outName);
		FileUtils.makeDirsFor (outputFile);
	    }

	    FileUtils.writeFile (result, "UTF-8", outputFile);
	}
    }
}
