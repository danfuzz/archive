// Copyright (c) 2000-2001 Dan Bornstein, danfuzz@milk.com. All rights 
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

package com.milk.tinyweb;

/**
 * Interface to receive calls about logging information coming from a
 * {@link TinyWebServer}.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public interface TinyWebLogger
{
    /**
     * Log a simple debugging message. Note that this method won't generally
     * get called unless debugging code is enabled elsewhere.
     *
     * @param msg non-null; the debug message to log
     */
    public void debug (String msg);

    /**
     * Log an error message.
     *
     * @param msg non-null; the error message to log
     */
    public void error (String msg);

    /**
     * Log an error message and associated exception.
     *
     * @param msg non-null; the error message to log
     * @param ex non-null; the associated exception
     */
    public void error (String msg, Throwable ex);
}
