/*
 * Copyright 2013 Dan Bornstein.
 * Licensed AS IS and WITHOUT WARRANTY under the Apache License,
 * Version 2.0. Details: <http://www.apache.org/licenses/LICENSE-2.0>
 */

#include "zint.h"

#include <stdio.h>


/** Prototype for unary ops. */
typedef bool unaryFunc(zint *result, zint x);

/** Prototype for binary ops. */
typedef bool binaryFunc(zint *result, zint x, zint y);

/*
 * Conservative implementations of the operations.
 */

static bool fixResult(zint *result, long longResult) {
    zint zr = (zint) longResult;

    if (zr == longResult) {
        if (result != NULL) {
            *result = zr;
        }
        return true;
    } else {
        return false;
    }
}

static bool cAbs(zint *result, zint x) {
    long lx = x;
    return fixResult(result, (lx < 0) ? -x : x);
}

static bool cAdd(zint *result, zint x, zint y) {
    return fixResult(result, (long) x + (long) y);
}

static bool cAnd(zint *result, zint x, zint y) {
    return fixResult(result, (long) x & (long) y);
}

static bool cBitSize(zint *result, zint x) {
    long lx = x;

    if (lx < 0) {
        lx = ~lx;
    }

    long found = -1;
    for (long n = 0; n < ZINT_BITS; n++) {
        if ((lx & (1 << n)) != 0) {
            found = n;
        }
    }

    return fixResult(result, found + 2);
}

static bool cBit(zint *result, zint x, zint y) {
    long lx = x;
    long ly = y;

    if (y < 0) {
        return false;
    } else if (y >= ZINT_BITS) {
        y = ZINT_BITS - 1;
    }

    return fixResult(result, (x >> y) & 1);
}

static bool cDiv(zint *result, zint x, zint y) {
    if (y == 0) {
        return false;
    }

    return fixResult(result, (long) x / (long) y);
}

static bool cDivEu(zint *result, zint x, zint y) {
    if (y == 0) {
        return false;
    }

    long lx = x;
    long ly = y;
    long quo = lx / ly;
    long rem = lx % ly;
    if (rem < 0) {
        if (y > 0) { quo--; }
        else       { quo++; }
    }

    return fixResult(result, quo);
}

static bool cMod(zint *result, zint x, zint y) {
    if (!cDiv(NULL, x, y)) {
        return false;
    }

    return fixResult(result, (long) x % (long) y);
}

static bool cModEu(zint *result, zint x, zint y) {
    if (!cDivEu(NULL, x, y)) {
        return false;
    }

    long lx = x;
    long ly = y;

    zint rem = lx % ly;
    if (rem < 0) {
        if (y > 0) { rem += ly; }
        else       { rem -= ly; }
    }

    return fixResult(result, rem);
}

static bool cMul(zint *result, zint x, zint y) {
    return fixResult(result, (long) x * (long) y);
}

static bool cNeg(zint *result, zint x) {
    return fixResult(result, -(long) x);
}

static bool cNot(zint *result, zint x) {
    return fixResult(result, ~(long) x);
}

static bool cOr(zint *result, zint x, zint y) {
    return fixResult(result, (long) x | (long) y);
}

static bool cSign(zint *result, zint x) {
    return fixResult(result, (x == 0) ? 0 : ((x < 0) ? -1 : 1));
}

static bool cShl(zint *result, zint x, zint y) {
    long lx = x;
    long ly = y;
    long res;

    if (ly < 0) {
        ly = -ly;
        if (ly > ZINT_BITS) {
            ly = ZINT_BITS;
        }
        res = lx >> ly;
    } else {
        if (ly > ZINT_BITS) {
            ly = ZINT_BITS;
        }
        res = lx << ly;
    }

    return fixResult(result, res);
}

static bool cShr(zint *result, zint x, zint y) {
    if (y == ZINT_MIN) {
        y++;
    }

    return cShl(result, x, -y);
}

static bool cSub(zint *result, zint x, zint y) {
    return fixResult(result, (long) x - (long) y);
}

static bool cXor(zint *result, zint x, zint y) {
    return fixResult(result, (long) x ^ (long) y);
}


/*
 * Test harness
 */

/**
 * Compares the results from a pair of unary functions.
 */
static void testUnary(const char *name, unaryFunc cons, unaryFunc tricky) {
    printf("Testing %s...\n", name);

    for (long x = 0; x < 0x100; x++) {
        zint resC;
        zint resT;
        bool succC = cons(&resC, x);
        bool succT = tricky(&resT, x);

        if (succC != succT) {
            printf("%s: success disagreement on %ld: %d %d\n",
                name, x, succC, succT);
            continue;
        } else if (!succC) {
            // Both failed.
        } else if (resC != resT) {
            printf("%s: result disagreement on %ld: %d %d\n",
                name, x, resC, resT);
        }
    }
}

/**
 * Compares the results from a pair of binary functions.
 */
static void testBinary(const char *name, binaryFunc cons, binaryFunc tricky) {
    printf("Testing %s...\n", name);

    for (long n = 0; n < 0x10000; n++) {
        zint x = (zint) (n >> 8);
        zint y = (zint) n;
        zint resC;
        zint resT;
        bool succC = cons(&resC, x, y);
        bool succT = tricky(&resT, x, y);

        if (succC != succT) {
            printf("%s: success disagreement on %d %d: %d %d\n",
                name, x, y, succC, succT);
            continue;
        } else if (!succC) {
            // Both failed.
        } else if (resC != resT) {
            printf("%s: result disagreement on %d %d: %d %d\n",
                name, x, y, resC, resT);
        }
    }
}

int main(int argc, char **argv) {
    testUnary("abs", cAbs, zintAbs);
    testUnary("bitSize", cBitSize, zintBitSize);
    testUnary("neg", cNeg, zintNeg);
    testUnary("not", cNot, zintNot);
    testUnary("sign", cSign, zintSign);

    testBinary("add", cAdd, zintAdd);
    testBinary("and", cAnd, zintAnd);
    testBinary("bit", cBit, zintBit);
    testBinary("div", cDiv, zintDiv);
    testBinary("divEu", cDivEu, zintDivEu);
    testBinary("mod", cMod, zintMod);
    testBinary("modEu", cModEu, zintModEu);
    testBinary("mul", cMul, zintMul);
    testBinary("or", cOr, zintOr);
    testBinary("shl", cShl, zintShl);
    testBinary("shr", cShr, zintShr);
    testBinary("sub", cSub, zintSub);
    testBinary("xor", cXor, zintXor);
}
