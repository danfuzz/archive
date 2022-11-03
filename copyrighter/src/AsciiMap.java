/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Map of ASCII-7 characters to counts.
 */
public final class AsciiMap {
    /** size of the map array */
    private static final int SIZE = 128;
    
    /** the map, per se */
    private final int[] counts;

    /**
     * Construct an instance.
     */
    public AsciiMap() {
        counts = new int[SIZE];
    }

    /**
     * Constructs an instance that is a copy of another.
     */
    public AsciiMap(AsciiMap original) {
        this();

        System.arraycopy(original.counts, 0, counts, 0, SIZE);
    }

    /**
     * Adds the given character.
     */
    public void add(char c) {
        if (c < SIZE) {
            counts[c]++;
        }
    }

    /**
     * Adds all the characters from the given string.
     */
    public void add(String string) {
        int length = string.length();
        for (int i = 0; i < length; i++) {
            add(string.charAt(i));
        }
    }

    /**
     * Subtracts the given character.
     */
    public void sub(char c) {
        if ((c < SIZE) && (counts[c] > 0)) {
            counts[c]--;
        }
    }

    /**
     * Subtracts all the characters from the given string.
     */
    public void sub(String string) {
        int length = string.length();
        for (int i = 0; i < length; i++) {
            sub(string.charAt(i));
        }
    }

    /**
     * Extracts the negative counts from this instance, resetting them to
     * zero, and returning a new instance holding corresponding
     * <i>positive</i> values.
     */
    public AsciiMap extractNegatives() {
        AsciiMap result = new AsciiMap();
        
        for (int i = 0; i < SIZE; i++) {
            int count = counts[i];
            if (count < 0) {
                result.counts[i] = -count;
                counts[i] = 0;
            }
        }

        return result;
    }

    /**
     * Gets a map that only contains the alphanumeric counts. This does
     * not alter the original instance.
     */
    public AsciiMap getAlphanumerics() {
        AsciiMap result = new AsciiMap();

        for (int i = '0'; i <= '9'; i++) {
            result.counts[i] = counts[i];
        }

        for (int i = 'a'; i <= 'z'; i++) {
            result.counts[i] = counts[i];
        }

        for (int i = 'A'; i <= 'Z'; i++) {
            result.counts[i] = counts[i];
        }

        return result;
    }

    /**
     * Gets the sum of all positive counts in the map.
     */
    public int getPositiveSum() {
        int result = 0;

        for (int i = 0; i < SIZE; i++) {
            int count = counts[i];
            if (count > 0) {
                result += count;
            }
        }

        return result;
    }

    /**
     * Gets the sum of the absolute values of all counts in the map.
     */
    public int getAbsoluteSum() {
        int result = 0;

        for (int i = 0; i < SIZE; i++) {
            int count = counts[i];
            result += (count > 0) ? count : -count;
        }

        return result;
    }
}
