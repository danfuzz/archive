package com.milk.plastic.util;

import com.milk.plastic.iface.Environment;
import com.milk.plastic.iface.FieldRef;
import com.milk.plastic.iface.Module;
import com.milk.plastic.iface.PlasticException;
import com.milk.plastic.iface.Template;
import java.util.ArrayList;
import java.util.Map;

/**
 * Control for a network of modules. Instances of this class are
 * constructed based on a set of sink modules, which are traversed
 * to derive a full network.
 *
 * <p>Once construction of an instance of this class is successful, it
 * may be used to tick the modules it controls, and it is guaranteed to
 * do so in an order consistent with instantaneous flow of data through
 * the network. (Corrolary: It throws an exception if it can't construct
 * the network such that that is true. The particular no-no to be aware
 * of is an explicit loop in the network, as opposed to one constructed
 * using a propagagation delay pair.)</p>
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
final public class ModuleNetwork
{
    /** array of the modules in the network in the order that they should
     * be ticked */
    private Module[] myModules;

    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct an instance.
     *
     * @param sinks the sinks in the network
     * @exception PlasticException thrown if there is a
     * problem with the specified network
     */
    public ModuleNetwork (Module[] sinks)
    {
	if (sinks == null)
	{
	    throw new IllegalArgumentException ("sinks = null");
	}

	myModules = moduleList (sinks);
    }

    // ------------------------------------------------------------------------
    // public instance methods

    /**
     * Tell all of the modules controlled by this network to bind their
     * ports.
     *
     * @see Module#bind
     */
    public void bind ()
    {
	for (int i = 0; i < myModules.length; i++)
	{
	    myModules[i].bind ();
	}
    }

    /**
     * Tell all of the modules controlled by this network to reset
     * themselves.
     *
     * @see Module#reset
     */
    public void reset ()
    {
	for (int i = 0; i < myModules.length; i++)
	{
	    myModules[i].reset ();
	}
    }

    /**
     * Tell all of the modules controlled by this network to tick.
     *
     * @see Module#tick
     */
    public void tick ()
    {
	// efficiency paranoia dictates that these be held in local
	// variables
	Module[] modules = myModules;
	int len = modules.length;

	for (int i = 0; i < len; i++)
	{
	    modules[i].tick ();
	}
    }
    
    // ------------------------------------------------------------------------
    // private static methods

    /**
     * Given a list of sinks, return a list of the transitive closure of
     * the modules eventually referenced via the sinks' inputs, sorted
     * topologically such that a referenced module always appears before
     * the modules that refer to it. If such a sort isn't possible, throw
     * an exception.
     *
     * @param sinks the array of sinks
     * @return an array of modules, appropriately sorted
     */
    static private Module[] moduleList (Module[] sinks)
    {
	// use the helper class ModuleList; iterate over the sinks, adding
	// each in turn; ModuleList.add throws an exception if it ever
	// notices a circularity
	ModuleList l = new ModuleList ();
	for (int i = 0; i < sinks.length; i++)
	{
	    l.add (sinks[i]);
	}

	return l.getList ();
    }

    // ------------------------------------------------------------------------
    // private static classes

    /**
     * Helper class for {@link #moduleList}. It maintains a list of
     * modules in sorted order and can have new ones added to it.
     */
    private static class ModuleList
    {
	/** the current list of modules, sorted so that modules are always
	 * listed before the modules that they depend on */
	private ArrayList myModules = new ArrayList ();

	/** the current stack of modules in the process of being added */
	private ArrayList myAdding = new ArrayList ();

	/**
	 * Add a new module and all its dependencies.
	 *
	 * @param module the module to add
	 */
	public void add (Module module)
	{
	    int alreadyAt = myModules.indexOf (module);
	    if (alreadyAt != -1)
	    {
		// easy out; the module's already known
		return;
	    }
	    
	    // verify that we're not already in the process of adding
	    // this module; if we are, that means there's a circularity
	    
	    if (myAdding.contains (module))
	    {
		StringBuffer sb = new StringBuffer ();
		sb.append ("Module circularity detected: ");
		sb.append (myAdding);
		throw new PlasticException (sb.toString ());
	    }

	    // add the module as close to the end of the list as possible
	    // (that is, insert it immediately before its first
	    // already-listed dependency, if any; recursively add any
	    // dependencies that weren't already listed

	    Template template = module.getTemplate ();
	    int tCount = template.getCount ();
	    int addAt = myModules.size ();

	    myAdding.add (module);

	    for (int i = 0; i < tCount; i++)
	    {
		Object one = module.getField (i);
		if (! (one instanceof FieldRef))
		{
		    // not a FieldRef, no need to do anything
		    continue;
		}

		FieldRef fr = (FieldRef) one;
		Module dep = (Module) fr.getModuleRef ().resolve ();
		int depAt = myModules.indexOf (dep);
		
		if (depAt == -1)
		{
		    add (dep);
		}
		else if (depAt < addAt)
		{
		    addAt = depAt;
		}
	    }

	    myModules.add (addAt, module);
	    myAdding.remove (myAdding.size () - 1);
	}

	/**
	 * Get the array of modules, sorted in the order of execution
	 * (which is the reverse of their order in the list as it was
	 * being built).
	 *
	 * @return the array of modules
	 */
	public Module[] getList ()
	{
	    int sz = myModules.size ();
	    Module[] result = new Module[sz];

	    for (int i = 0; i < sz; i++)
	    {
		result[i] = (Module) myModules.get (sz - i - 1);
	    }

	    return result;
	}
    }
}
