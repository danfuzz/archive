/*
 * Copyright 2013 Dan Bornstein.
 * Licensed AS IS and WITHOUT WARRANTY under the Apache License,
 * Version 2.0. Details: <http://www.apache.org/licenses/LICENSE-2.0>
 */

#ifndef _ZINT_H_
#define _ZINT_H_

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

typedef int8_t zint;

enum {
    ZINT_MIN = INT8_MIN,
    ZINT_MAX = INT8_MAX,
    ZINT_BITS = sizeof(zint) * 8
};

/**
 * Performs `abs(x)` (unary absolute value), detecting overflow. Returns
 * a success flag, and stores the result in the indicated pointer if
 * non-`NULL`.
 *
 * **Note:** The only possible overflow case is `abs(ZINT_MIN)`.
 */
bool zintAbs(zint *result, zint x);

/**
 * Performs `x + y`, detecting overflow. Returns a success flag, and
 * stores the result in the indicated pointer if non-`NULL`.
 */
bool zintAdd(zint *result, zint x, zint y);

/**
 * Performs `x &&& y`. Returns `true`, and stores the result in the
 * indicated pointer if non-`NULL`. This function never fails; the success
 * flag is so that it can be used equivalently to the other similar functions
 * in this library.
 */
inline bool zintAnd(zint *result, zint x, zint y) {
    if (result != NULL) {
        *result = x & y;
    }

    return true;
}

/**
 * Gets the bit size (highest-order significant bit number, plus one)
 * of the given `zint`, assuming sign-extended representation. For example,
 * this is `1` for both `0` and `-1` (because both can be represented with
 * *just* a single sign bit).
 */
bool zintBitSize(zint *result, zint value);

/**
 * Performs bit extraction `(x >>> y) &&& 1`, detecting errors. Returns a
 * success flag, and stores the result in the indicated pointer if non-`NULL`.
 * For `y >= ZINT_BITS`, this returns the sign bit.
 *
 * **Note:** The only possible errors are when `y < 0`.
 */
bool zintBit(zint *result, zint x, zint y);

/**
 * Performs `x / y` (trucated division), detecting overflow and errors.
 * Returns a success flag, and stores the result in the indicated pointer
 * if non-`NULL`.
 *
 * **Note:** The only possible overflow case is `ZINT_MIN / -1`, and the
 * only other error is division by zero.
 */
bool zintDiv(zint *result, zint x, zint y);

/**
 * Performs `x // y` (Euclidean division), detecting overflow and errors.
 * Returns a success flag, and stores the result in the indicated pointer
 * if non-`NULL`.
 *
 * **Note:** The only possible overflow case is `ZINT_MIN / -1`, and the
 * only other error is division by zero.
 */
bool zintDivEu(zint *result, zint x, zint y);

/**
 * Performs `x % y` (that is, remainder after truncated division, with the
 * result sign matching `x`), detecting overflow. Returns a success flag, and
 * stores the result in the indicated pointer if non-`NULL`.
 *
 * **Note:** This only succeeds in cases that `x / y` succeeds, that is,
 * `ZINT_MIN % -1` fails.
 */
bool zintMod(zint *result, zint x, zint y);

/**
 * Performs `x %% y` (that is, remainder after Euclidean division, with the
 * result sign always positive), detecting overflow. Returns a success flag,
 * and stores the result in the indicated pointer if non-`NULL`.
 *
 * **Note:** This only succeeds in cases that `x // y` succeeds, that is,
 * `ZINT_MIN %% -1` fails.
 */
bool zintModEu(zint *result, zint x, zint y);

/**
 * Performs `x * y`, detecting overflow. Returns a success flag, and
 * stores the result in the indicated pointer if non-`NULL`.
 */
bool zintMul(zint *result, zint x, zint y);

/**
 * Performs `-x` (unary negation), detecting overflow. Returns a success flag,
 * and stores the result in the indicated pointer if non-`NULL`.
 *
 * **Note:** The only possible overflow case is `-ZINT_MIN`.
 */
bool zintNeg(zint *result, zint x);

/**
 * Performs `!!!x` (unary bitwise complement). Returns `true`,
 * and stores the result in the indicated pointer if non-`NULL`. This
 * function never fails; the success flag is so that it can be used
 * equivalently to the other similar functions in this library.
 */
inline bool zintNot(zint *result, zint x) {
    if (result != NULL) {
        *result = ~x;
    }

    return true;
}

/**
 * Performs `x ||| y`. Returns `true`, and stores the result in the
 * indicated pointer if non-`NULL`. This function never fails; the success
 * flag is so that it can be used equivalently to the other similar functions
 * in this library.
 */
inline bool zintOr(zint *result, zint x, zint y) {
    if (result != NULL) {
        *result = x | y;
    }

    return true;
}

/**
 * Performs `sign(x)`. Returns `true`, and stores the result in the
 * indicated pointer if non-`NULL`. This function never fails; the success
 * flag is so that it can be used equivalently to the other similar functions
 * in this library.
 */
inline bool zintSign(zint *result, zint x) {
    if (result != NULL) {
        *result = (x == 0) ? 0 : ((x < 0) ? -1 : 1);
    }

    return true;
}

/**
 * Performs `x <<< y`, detecting overflow (never losing high-order bits).
 * Returns a success flag, and stores the result in the indicated pointer
 * if non-`NULL`.
 *
 * **Note:** This defines `(x <<< -y) == (x >>> y)`.
 */
bool zintShl(zint *result, zint x, zint y);

/**
 * Performs `x >>> y`, detecting overflow (never losing high-order bits).
 * Returns a success flag, and stores the result in the indicated pointer
 * if non-`NULL`.
 *
 * **Note:** This defines `(x >>> -y) == (x <<< y)`.
 */
bool zintShr(zint *result, zint x, zint y);

/**
 * Performs `x - y`, detecting overflow. Returns a success flag, and
 * stores the result in the indicated pointer if non-`NULL`.
 */
bool zintSub(zint *result, zint x, zint y);

/**
 * Performs `x ^^^ y`. Returns `true`, and stores the result in the
 * indicated pointer if non-`NULL`. This function never fails; the success
 * flag is so that it can be used equivalently to the other similar functions
 * in this library.
 */
inline bool zintXor(zint *result, zint x, zint y) {
    if (result != NULL) {
        *result = x ^ y;
    }

    return true;
}

#endif
