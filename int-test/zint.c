/*
 * Copyright 2013 Dan Bornstein.
 * Licensed AS IS and WITHOUT WARRANTY under the Apache License,
 * Version 2.0. Details: <http://www.apache.org/licenses/LICENSE-2.0>
 */

/*
 * `zint` Utilities
 *
 * Notes on the arithmetic operations:
 *
 * With regard to detecting overflow, C compilers are allowed to optimize
 * overflowing operations in surprising ways. Therefore, the usual
 * recommendation for portable code is to detect overflow by inspection of
 * operands, not results. The choices for what to detect here are informed by,
 * but not exactly the same as, the CERT "AIR" recommendations.
 *
 * With regard to division and modulo, this file implements both
 * traditional truncating division and Euclidean division, each with its
 * corresponding modulo operation. These are implemented to assume the C99
 * definition of remainder, in particular that the sign of the result is
 * the same as the sign of the dividend (the left-hand argument).
 *
 * References:
 *
 * * [As-If Infinitely Ranged Integer Model, Second
 *   Edition](http://www.cert.org/archive/pdf/10tn008.pdf)
 *
 * * [Division and Modulus for Computer
 *   Scientists](http://legacy.cs.uu.nl/daan/download/papers/divmodnote.pdf)
 *
 * * [The Euclidean Definition of the Functions div and
 *   mod](https://biblio.ugent.be/publication/314490/file/452146.pdf)
 *
 * * [Modulo operation
 *   (Wikipedia)](http://en.wikipedia.org/wiki/Modulo_operation)
 *
 * * [Safe IOP Library](https://code.google.com/p/safe-iop/)
 */

#include "zint.h"


/*
 * Private Definitions
 */

/**
 * Common check for all divide and remainder functions.
 */
static bool canDivide(zint x, zint y) {
    if (y == 0) {
        // Divide by zero.
        return false;
    }

    if ((x == ZINT_MIN) && (y == -1)) {
        // Overflow: `-ZINT_MIN` is not representable as a `zint`.
        return false;
    }

    return true;
}


/*
 * Exported Definitions
 */

/* Documented in header. */
bool zintAbs(zint *result, zint x) {
    if (x == ZINT_MIN) {
        return false;
    }

    if (result != NULL) {
        *result = (x < 0) ? -x : x;
    }

    return true;
}

/* Documented in header. */
bool zintAdd(zint *result, zint x, zint y) {
    // If the signs are opposite or either argument is zero, then overflow
    // is impossible. The two clauses here are for the same-sign-and-not-zero
    // cases. Each one is of the form: Given a {positive, negative} `y`,
    // what is the {largest, smallest} `x` that won't overflow?
    if (((y > 0) && (x > (ZINT_MAX - y))) ||
        ((y < 0) && (x < (ZINT_MIN - y)))) {
        return false;
    }

    if (result != NULL) {
        *result = x + y;
    }

    return true;
}

/* Documented in header. */
extern bool zintAnd(zint *result, zint x, zint y);

/* Documented in header. */
bool zintBit(zint *result, zint x, zint y) {
    if (y < 0) {
        return false;
    }

    if (result != NULL) {
        if (y >= ZINT_BITS) {
            y = ZINT_BITS - 1;
        }

        *result = (x >> y) & 1;
    }

    return true;
}

/* Documented in header. */
bool zintBitSize(zint *result, zint value) {
    if (value < 0) {
        value = ~value;
    }

    // "Binary-search" style implementation. Many compilers have a
    // built-in "count leading zeroes" function, but we're aiming
    // for portability here.

    zint res = 1; // +1 in that we want size, not zero-based bit number.
    uint64_t uv = (uint64_t) value; // Use `uint` to account for `-ZINT_MAX`.

#if ZINT_BITS > 32
    if (uv >= ((zint) 1 << 32)) { res += 32; uv >>= 32; }
#endif
#if ZINT_BITS > 16
    if (uv >= ((zint) 1 << 16)) { res += 16; uv >>= 16; }
#endif
#if ZINT_BITS > 8
    if (uv >= ((zint) 1 << 8))  { res +=  8; uv >>=  8; }
#endif
    if (uv >= ((zint) 1 << 4))  { res +=  4; uv >>=  4; }
    if (uv >= ((zint) 1 << 2))  { res +=  2; uv >>=  2; }
    if (uv >= ((zint) 1 << 1))  { res +=  1; uv >>=  1; }
    res += uv;

    if (result != NULL) {
        *result = res;
    }

    return true;
}

/* Documented in header. */
bool zintDiv(zint *result, zint x, zint y) {
    if (!canDivide(x, y)) {
        return false;
    }

    if (result != NULL) {
        *result = x / y;
    }

    return true;
}

/* Documented in header. */
bool zintDivEu(zint *result, zint x, zint y) {
    if (!canDivide(x, y)) {
        return false;
    }

    if (result != NULL) {
        zint quo = x / y;
        zint rem = x % y;
        if (rem < 0) {
            if (y > 0) { quo--; }
            else       { quo++; }
        }
        *result = quo;
    }

    return true;
}

/* Documented in header. */
bool zintMod(zint *result, zint x, zint y) {
    if (!canDivide(x, y)) {
        return false;
    }

    if (result != NULL) {
        *result = x % y;
    }

    return true;
}

/* Documented in header. */
bool zintModEu(zint *result, zint x, zint y) {
    if (!canDivide(x, y)) {
        return false;
    }

    if (result != NULL) {
        zint rem = x % y;
        if (rem < 0) {
            if (y > 0) { rem += y; }
            else       { rem -= y; }
        }
        *result = rem;
    }

    return true;
}

/* Documented in header. */
bool zintMul(zint *result, zint x, zint y) {
    // This is broken down by sign of the arguments, with zeros getting
    // an easy pass-through.

    if (x > 0) {
        if (y > 0) {
            // Both arguments are positive.
            if (x > (ZINT_MAX / y)) {
                return false;
            }
        } else if (y < 0) {
            // `x` is positive, and `y` is negative.
            if (y < (ZINT_MIN / x)) {
                return false;
            }
        }
    } else if (x < 0) {
        if (y > 0) {
            // `x` is negative, and `y` is positive.
            if (x < (ZINT_MIN / y)) {
                return false;
            }
        } else if (y < 0) {
            // Both arguments are negative.
            if (y < (ZINT_MAX / x)) {
                return false;
            }
        }
    }

    if (result != NULL) {
        *result = x * y;
    }

    return true;
}

/* Documented in header. */
bool zintNeg(zint *result, zint x) {
    if (x == ZINT_MIN) {
        return false;
    }

    if (result != NULL) {
        *result = -x;
    }

    return true;
}

/* Documented in header. */
extern bool zintNot(zint *result, zint x);

/* Documented in header. */
extern bool zintOr(zint *result, zint x, zint y);

/* Documented in header. */
extern bool zintSign(zint *result, zint x);

/* Documented in header. */
bool zintShl(zint *result, zint x, zint y) {
    zint res;

    if ((x == 0) || (y == 0)) {
        res = x;
    } else if (y > 0) {
        // Left shift (and `x` is non-zero). There's definite loss of
        // bits if `y` is more than the size of an int. With a potentially
        // in-range `y`, we have to check based on the sign of `x`, ensuring
        // that a positive `x` is small enough or a negative `x` is large
        // enough that the shift couldn't lose its top significant bit.
        if ((y >= ZINT_BITS) ||
            ((x > 0) && (x > (ZINT_MAX >> y))) ||
            ((x < 0) && (x < (ZINT_MIN >> y)))) {
            return false;
        }
        res = x << y;
    } else {
        // Right shift. It's always safe, but we have to behave specially
        // when `y <= -ZINT_BITS`, as C99 leaves it undefined when the
        // right-hand side is greater than the number of bits in the type
        // in question.
        //
        // Also, note that strictly speaking, this isn't portable for
        // negative numbers, as the C99 standard does not say what right shift
        // on negative numbers means. In practice, right shift on negative
        // numbers means what it usually means for twos-complement; the
        // weaseliness of the spec is apparently just to allow for the
        // possibility ones-complement integer implementations.
        if (y <= -ZINT_BITS) {
            res = x >> (ZINT_BITS - 1);
        } else {
            res = x >> -y;
        }
    }

    if (result != NULL) {
        *result = res;
    }

    return true;
}

/* Documented in header. */
bool zintShr(zint *result, zint x, zint y) {
    // We just define this in terms of `zintShl`, but note the test to
    // deal with the possibility of passing `ZINT_MIN` for `y`.
    return zintShl(result, x, (y <= -ZINT_BITS) ? ZINT_BITS : -y);
}

/* Documented in header. */
bool zintSub(zint *result, zint x, zint y) {
    // Note: This can't be written as `zintAdd(x, -y)`, because of the
    // asymmetry of twos-complement integers. That is, that would fail if
    // `y == ZINT_MIN`.

    // Overflow can only happen when the two arguments are of opposite sign.
    // The two halves of the test here are equivalent, with each part
    // being a test of the limit of possible `x`s given `y`.
    if (((y < 0) && (x > (ZINT_MAX + y))) ||
        ((y > 0) && (x < (ZINT_MIN + y)))) {
        return false;
    }

    if (result != NULL) {
        *result = x - y;
    }

    return true;
}

/* Documented in header. */
extern bool zintXor(zint *result, zint x, zint y);
