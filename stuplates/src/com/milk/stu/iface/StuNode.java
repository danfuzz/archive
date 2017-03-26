// Copyright (c) 2002 Dan Bornstein, danfuzz@milk.com. All rights 
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

package com.milk.stu.iface;

/**
 * A node in an evaluable parse tree.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public interface StuNode
{
    /**
     * Evaluate this instance in the given environment.
     *
     * @param environment non-null; the environment
     * @return null-ok; arbitrary result of evaluation
     */
    public Object eval (Environment environment);

    /**
     * Evaluate this instance in the given environment to yield a slot.
     * A Node may respond to a call to this method by throwing an exception
     * to indicate that it does not denote a slot.
     *
     * @param environment non-null; the environment
     * @return non-null; the denoted slot
     */
    public Slot evalSlot (Environment environment);

    /**
     * Evaluate this instance in the given environment and return the chars
     * form of the result. If the result of the raw {@link #eval} is
     * <code>null</code>, then the result of this method is an empty
     * character sequence (e.g., <code>""</code>, not <code>null</code>).
     * In all other cases, this is just like calling
     * <code>toString()</code> on the result of the raw
     * <code>eval()</code>, except that results that are already character
     * sequences are not coerced to strings.
     *
     * @param environment non-null; the environment
     * @return non-null; the result of evaluation, in chars form 
     */
    public CharSequence evalToChars (Environment environment);

    /**
     * Evaluate this instance "on the outside," that is, without making an
     * inner environment, if applicable. This is in fact only applicable to
     * sequence nodes, which occasionally get "chained together" in some
     * outer environment. For all other nodes, this is the same as just a
     * plain old {@link #eval}.
     *
     * @param environment non-null; the environment to evaluate in
     * @return null-ok; arbitrary result of evaluation 
     */
    public Object evalOutside (Environment environment);

    /**
     * Evaluate this instance "on the outside" to yield a slot. The results
     * are analogous to {@link #evalSlot}, except that the local variable
     * semantics are those of {@link #evalOutside} and not {@link #eval}.
     *
     * @param environment non-null; the environment
     * @return non-null; the denoted slot
     */
    public Slot evalOutsideSlot (Environment environment);

    /**
     * Evaluate this instance "on the outside" and return the chars
     * form of the result. The results are analogous to {@link #evalToChars},
     * except that it is {@link #evalOutside} which is called and not
     * {@link #eval}.
     *
     * @param environment non-null; the environment
     * @return non-null; the result of evaluation, in chars form
     */
    public CharSequence evalOutsideToChars (Environment environment);
}
