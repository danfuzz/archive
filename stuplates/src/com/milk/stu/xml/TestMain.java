package com.milk.stu.xml;

import java.io.FileReader;
import java.io.OutputStreamWriter;

public class TestMain
{
    static public void main (String[] args)
    throws Exception
    {
	try
	{
	    FileReader fr = new FileReader (args[0]);
	    Fragment frag = XmlParser.parse (fr);
	    OutputStreamWriter osw = new OutputStreamWriter (System.out);
	    frag.writeTo (osw);
	    osw.flush ();
	}
	catch (XmlParseException ex)
	{
	    System.err.println (ex.getSourceRange ());
	    ex.printStackTrace ();
	}
    }
}
