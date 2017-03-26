// Copyright (c) 2005 Dan Bornstein, danfuzz@milk.com. All rights 
// reserved, except as follows:
// 
// Permission is hereby granted, free of charge, to any person obtaining a
// copy of this software and associated documentation files (the "Software"),
// to deal in the Software without restriction, including without limitation
// the rights to use, copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons to whom the
// Software is furnished to do so, subject to the condition that the above
// copyright notice and this permission notice shall be included in all copies
// or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// DEALINGS IN THE SOFTWARE.

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.TreeMap;

/**
 * Source-level treeshaker for Java. Point it at one or more library
 * directories and one or more source files, and it will tell you which
 * files in the library are actually required or optionally copy those files
 * to the destination directory of your choice.
 *
 * <p>Note that this tool makes no attempt to find anything but static
 * references to classes in the source text. That is, it doesn't attempt
 * to follow any sort of reflection (<code>Class.forName()</code>, etc.).</p>
 *
 * <p>This was originally written to be used with the
 * <a href="http://www.antlr.org/">ANTLR</a> library, since for some reason
 * the ANTLR folks don't separate the tool from the libraries required by
 * the tool's output.</p>
 *
 * @version 1.0
 * @author Dan Bornstein, danfuzz@milk.com
 */ 
public class SourceShake
{
    static HashMap sLibs;
    static TreeSet sToWalk;
    static TreeSet sWalked;
    static TreeMap sLibsRequired;

    static public void main(String[] args)
    {
	String destDir = null;
	sLibs = new HashMap();
	sToWalk = new TreeSet();
	sWalked = new TreeSet();
	sLibsRequired = new TreeMap();
	boolean trouble = false;

	for (int i = 0; i < args.length; i++) {
	    String arg = args[i];
	    if (arg.startsWith("--lib=")) {
		String name = arg.substring(arg.indexOf('=') + 1);
		getLibs(name);
	    }
	    else if (arg.startsWith("--dest=")) {
		destDir = arg.substring(arg.indexOf('=') + 1);
	    }
	    else if (arg.startsWith("--")) {
		System.err.println("unknown option: " + arg);
		trouble = true;
	    }
	    else {
		sToWalk.add(args[i]);
	    }
	}

	if (trouble) {
	    System.err.println("usage: SourceShake --lib=<libDir> " +
			       "[--dest=<destDir>] <src> ...");
	    System.exit(1);
	}

	doit();

	if (destDir == null) {
	    printRequired();
	}
	else {
	    copyRequiredTo(destDir);
	}
    }

    static public void doit()
    {
	for (;;) {
	    int sz = sToWalk.size();
	    if (sz == 0) {
		break;
	    }
	    String one = (String) sToWalk.first();
	    sToWalk.remove(one);
	    if (! sWalked.contains(one)) {
		sWalked.add(one);
		int nameAt = one.lastIndexOf(File.separatorChar) + 1;
		System.err.println("processing " + one.substring(nameAt) +
				   "...");
		walkFile(one);
	    }
	}
    }

    static private void getLibs(String dir)
    {
	File dirf = new File(dir);
	getLibs0(dirf.getPath().length() + 1, dirf);
    }

    static private void getLibs0(int baseLen, File dir)
    {
	File[] files = dir.listFiles();

	for (int i = 0; i < files.length; i++) {
	    File one = files[i];
	    if (one.isDirectory()) {
		getLibs0(baseLen, one);
	    }
	    else if (one.getName().endsWith(".java")) {
		String path = one.getPath();
		String name = path.substring(baseLen, path.length() - 5);
		name = name.replace(File.separatorChar, '.');
		sLibs.put(name, path);
	    }
	}
    }

    static private void walkFile(String path)
    {
	String contents = readFile(path, true);
	String pkg = "";

	int pkgAt = contents.indexOf("package ");
	if (pkgAt != -1) {
	    int pkgEnd = contents.indexOf(';', pkgAt);
	    pkg = contents.substring(pkgAt + 8, pkgEnd).trim() + '.';
	}

	for (Iterator i = sLibs.keySet().iterator(); i.hasNext(); /*i*/) {
	    String key = (String) i.next();
	    String seek = key;

	    if ((pkg != "") &&
		key.startsWith(pkg) &&
		(key.indexOf('.', pkg.length()) == -1))
	    {
		seek = key.substring(pkg.length());
	    }

	    int at = 0;
	    for (;;) {
		int found = contents.indexOf(seek, at);
		if (found == -1) {
		    break;
		}

		int after = found + seek.length();
		char ac = (after == contents.length()) ? ' ' : 
			   contents.charAt(after);
		if (! Character.isJavaIdentifierPart(ac)) {
		    String val = (String) sLibs.get(key);
		    sToWalk.add(val);
		    sLibsRequired.put(key, val);
		    i.remove();
		    break;
		}

		at = found + 1;
	    }
	}
    }

    static private String readFile(String path, boolean strip)
    {
	String s;

	try {
	    File f = new File(path);
	    int len = (int) f.length();
	    char[] carr = new char[len];
	    FileInputStream fis = new FileInputStream(path);
	    InputStreamReader isr = new InputStreamReader(fis, "iso-8859-1");
	    int at = 0;
	    while (at < carr.length) {
		int amt = isr.read(carr, at, carr.length - at);
		if (amt <= 0) {
		    break;
		}
		at += amt;
	    }
	    s = new String(carr, 0, at);
	}
	catch (IOException ex) {
	    System.err.println("couldn't read: " + path);
	    ex.printStackTrace();
	    return "";
	}

	if (! strip) {
	    return s;
	}

	StringBuffer sb = new StringBuffer();
	int len = s.length();

	// strip out comments and quotes; yeah it's ironic that this was
	// generated directly by a human
	int mode = 0;
	for (int i = 0; i < len; i++) {
	    char c1 = s.charAt(i);
	    char c2 = (i != (len - 1)) ? s.charAt(i + 1) : 0;
	    boolean addIt = true;
	    switch (mode) {
		case 0: {
		    if ((c1 == '/') && (c2 == '/')) {
			addIt = false;
			mode = 1;
			break;
		    }
		    if ((c1 == '/') && (c2 == '*')) {
			addIt = false;
			mode = 2;
			break;
		    }
		    if (c1 == '\'') {
			mode = 3;
			break;
		    }
		    if (c1 == '\"') {
			mode = 4;
			break;
		    }
		    if (! (((c1 >= 'A') && (c1 <= 'Z')) ||
			   ((c1 >= 'a') && (c1 <= 'z')) ||
			   ((c1 >= '0') && (c1 <= '9')) ||
			   (c1 == '_') || (c1 == '$') || (c1 == '.') ||
			   (c1 == ';')))
		    {
			c1 = ' ';
		    }

		    break;
		}
		case 1: {
		    if (c1 == '\n') {
			mode = 0;
			break;
		    }
		    addIt = false;
		    break;
		}
		case 2: {
		    if ((c1 == '*') && (c2 == '/')) {
			c1 = ' ';
			i++;
			mode = 0;
			break;
		    }
		    addIt = false;
		    break;
		}
		case 3: {
		    if (c1 == '\'') {
			mode = 0;
			break;
		    }
		    if (c1 == '\\') {
			mode = 31;
			addIt = false;
			break;
		    }
		    addIt = false;
		    break;
		}
		case 31: {
		    addIt = false;
		    mode = 3;
		    break;
		}
		case 4: {
		    if (c1 == '\"') {
			mode = 0;
			break;
		    }
		    if (c1 == '\\') {
			mode = 41;
			addIt = false;
			break;
		    }
		    addIt = false;
		    break;
		}
		case 41: {
		    addIt = false;
		    mode = 4;
		    break;
		}
	    }

	    if (addIt) {
		sb.append(c1);
	    }
	}

	s = sb.toString();

	return s;
    }

    static private void printRequired()
    {
	for (Iterator i = sLibsRequired.keySet().iterator();
	     i.hasNext();
	     /*i*/)
	{
	    System.err.println(i.next());
	}
    }

    static private void copyRequiredTo(String dir)
    {
	for (Iterator i = sLibsRequired.entrySet().iterator();
	     i.hasNext();
	     /*i*/)
	{
	    Map.Entry e = (Map.Entry) i.next();
	    String key = (String) e.getKey();
	    String val = (String) e.getValue();
	    String dest = dir + File.separatorChar + 
		key.replace('.', File.separatorChar) + ".java";
	    File destFile = new File(dest);
	    destFile.getParentFile().mkdirs();

	    String contents = readFile(val, false);

	    try {
		FileWriter fw = new FileWriter(destFile);
		fw.write(contents);
		fw.close();
	    }
	    catch (IOException ex) {
		System.err.println("couldn't write: " + dest);
		ex.printStackTrace();
	    }
	}
    }
}
