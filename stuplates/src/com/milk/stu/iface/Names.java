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
 * Standard names for functions / variables. Note that it is not sufficient
 * just to change the values of these, as there are places where the names
 * are implicitly used (e.g., in defining builtins).
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public interface Names
{
    /** non-null; standard prefix for named break functions */
    static public final String BREAK_PREFIX = "break__";

    /** non-null; standard prefix for named continue functions */
    static public final String CONTINUE_PREFIX = "continue__";

    /** non-null; standard prefix for operator functions */
    static public final String OP_PREFIX = "op__";

    /** non-null; standard prefix for uri resolution functions */
    static public final String URI_PREFIX = "uri__";

    /** non-null; standard prefix for XML tag expansion functions */
    static public final String TAG_PREFIX = "tag__";

    /** non-null; standard prefix for XML entity expansion functions */
    static public final String ENTITY_PREFIX = "entity__";

    /** non-null; standard prefix for quasiliteral template functions */
    static public final String TEMPLATE_PREFIX = "quasiTemplate__";

    /** non-null; standard prefix for quasiliteral pattern functions */
    static public final String PATTERN_PREFIX = "quasiPattern";

    /** non-null; the name of the standard break function variable */
    static public final Identifier VAR_break = Identifier.intern ("break");

    /** non-null; the name of the standard continue function variable */
    static public final Identifier VAR_continue = 
	Identifier.intern ("continue");

    /** non-null; the name of the standard return function variable */
    static public final Identifier VAR_return = Identifier.intern ("return");

    /** non-null; the name of the environment self-reference variable */
    static public final Identifier VAR_thisEnv = Identifier.intern ("thisEnv");

    /** non-null; the name of the output path variable */
    static public final Identifier VAR_outputPath = 
	Identifier.intern ("outputPath");

    /** non-null; the name for the list-making function */
    static public final Identifier FN_makeList = 
	Identifier.intern ("makeList");

    /** non-null; the name for the map-making function */
    static public final Identifier FN_makeMap = Identifier.intern ("makeMap");

    /** non-null; the name for the simple pattern matching function */
    static public final Identifier FN_simpleMatch = 
	Identifier.intern ("simpleMatch");

    /** non-null; the name for the string concatenation function */
    static public final Identifier FN_strcat = Identifier.intern ("strcat");

    /** non-null; the name for the regex quasiliteral template function */
    static public final Identifier FN_quasiTemplate_rx = 
	Identifier.intern (TEMPLATE_PREFIX + "rx");

    /** non-null; the name for the regex quasiliteral pattern function */
    static public final Identifier FN_quasiPattern_rx = 
	Identifier.intern (PATTERN_PREFIX + "rx");

    /** non-null; the name for the add operator function */
    static public final String OP_ADD = "add";

    /** non-null; the name for the and operator function */
    static public final String OP_AND = "and";

    /** non-null; the name for the div operator function */
    static public final String OP_DIV = "div";

    /** non-null; the name for the eq operator function */
    static public final String OP_EQ = "eq";

    /** non-null; the name for the ge operator function */
    static public final String OP_GE = "ge";

    /** non-null; the name of the array(-like) get operator */
    static public final String OP_GET = "get";

    /** non-null; the name for the gt operator function */
    static public final String OP_GT = "gt";

    /** non-null; the name for the unary identity operator function */
    static public final String OP_IDENTITY = "identity";

    /** non-null; the name for the invert operator function */
    static public final String OP_INVERT = "invert";

    /** non-null; the name for the le operator function */
    static public final String OP_LE = "le";

    /** non-null; the name for the lshift operator function */
    static public final String OP_LSHIFT = "lshift";

    /** non-null; the name for the lt operator function */
    static public final String OP_LT = "lt";

    /** non-null; the name for the mod operator function */
    static public final String OP_MOD = "mod";

    /** non-null; the name for the mul operator function */
    static public final String OP_MUL = "mul";

    /** non-null; the name for the ne operator function */
    static public final String OP_NE = "ne";

    /** non-null; the name for the unary neg operator function */
    static public final String OP_NEG = "neg";

    /** non-null; the name for the not operator function */
    static public final String OP_NOT = "not";

    /** non-null; the name for the or operator function */
    static public final String OP_OR = "or";

    /** non-null; the name for the pow operator function */
    static public final String OP_POW = "pow";

    /** non-null; the name for the remainder operator function */
    static public final String OP_REMAINDER = "remainder";

    /** non-null; the name for the rshift operator function */
    static public final String OP_RSHIFT = "rshift";

    /** non-null; the name for the sub operator function */
    static public final String OP_SUB = "sub";

    /** non-null; the name for the xor operator function */
    static public final String OP_XOR = "xor";

    /** non-null; the standard extension for a template file */
    static public final String STUT_EXTENSION = ".stut";

    /** non-null; the standard extension for a script file */
    static public final String STU_EXTENSION = ".stu";
}
