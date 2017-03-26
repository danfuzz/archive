// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.command;

/**
 * This exception is thrown when <code>Command.run()</code> is
 * called, but the command in question is disabled.
 *
 * @see Command#run
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class DisabledCommandException
extends RuntimeException
{
    /** the command that was disabled */
    private final Command myCommand;

    /** the argument it was run with */
    private final Object myArgument;

    /**
     * Construct a <code>DisabledCommandException</code>.
     *
     * @param command the command that was disabled
     * @param argument the argument it was run with
     */
    public DisabledCommandException (Command command, Object argument)
    {
	super ("Disabled command (" + command + 
	       ") asked to run with argument: " + argument);

	myCommand = command;
	myArgument = argument;
    }

    /**
     * Get the command.
     *
     * @return the command
     */
    public Command getCommand ()
    {
	return myCommand;
    }

    /**
     * Get the argument.
     *
     * @return the argument
     */
    public Object getArgument ()
    {
	return myArgument;
    }
}
