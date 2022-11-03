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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A match score, representing how close a header matches a file and how
 * many lines of the file seem to be part of the header.
 */
public final class Match implements Comparable<Match> {
    /** regex pattern for a single alphanumeric character */
    private static final Pattern ALPHANUMERIC_PATTERN =
        Pattern.compile("[A-Za-z0-9]");

    /** the header this represents a match for */
    private final Header header;
    
    /** ratio of matching to total characters */
    private final double characterRatio;

    /** ratio of matching to total alphanumerics */
    private final double alphanumericRatio;

    /**
     * ratio of characters left over to total characters in the header.
     * A leftover is a character in either the canonical or observed
     * header that doesn't match up with the other.
     */
    private final double leftoverRatio;

    /** number of lines matched */
    private final int lineCount;

    /** number of lines matched exactly in alphanumerics */
    private final int alphanumericLineCount;

    /**
     * Constructs an instance.
     */
    public Match(Header header, double characterRatio,
            double alphanumericRatio, double leftoverRatio, int lineCount,
            int alphanumericLineCount) {
        this.header = header;
        this.characterRatio = characterRatio;
        this.alphanumericRatio = alphanumericRatio;
        this.leftoverRatio = leftoverRatio;
        this.lineCount = lineCount;
        this.alphanumericLineCount = alphanumericLineCount;
    }

    /**
     * Constructs an instance which matches the given file contents with
     * the given header.
     */
    public static Match match(String[] lines, Header header) {
        AsciiMap sofar = header.getCharacters();
        int characterCount = sofar.getPositiveSum();
        int alphanumericCount = sofar.getAlphanumerics().getPositiveSum();
        int alphanumericLineCount = 0;
        boolean gotEndComment = false;

        // Match characters from the file with characters in the header.
        int at = 0;
        for (/*at*/; at < lines.length; at++) {
            String line = lines[at];

            if (line.trim().length() == 0) {
                // Totally blank lines end the header.
                break;
            }

            int before = sofar.getPositiveSum();

            if (before == 0) {
                // Can't possibly improve the match by including more lines.
                break;
            }

            if (header.hasMatchingLine(line)) {
                alphanumericLineCount++;
            }
            
            sofar.sub(line);
            int after = sofar.getPositiveSum();

            /*
             * As a heuristic lines that look like comment ends will
             * end the header match.
             */
            line = line.trim();
            if (line.equals("*/") || line.equals("-->")) {
                at++;
                gotEndComment = true;
                break;
            }
        }

        /*
         * As a heuristic, also include any lines consisting only of
         * non-alphanumerics, up to and including any number of blank
         * lines.
         */

        for (/*at*/; at < lines.length; at++) {
            String line = lines[at];

            if (line.length() == 0) {
                break;
            }

            if (gotEndComment) {
                line = line.trim();
                if (! (line.equals("*/") || line.equals("-->"))) {
                    break;
                }
            } else {
                Matcher matcher = ALPHANUMERIC_PATTERN.matcher(line);
                if (matcher.find()) {
                    break;
                }
            }
        }

        for (/*at*/; at < lines.length; at++) {
            if (lines[at].length() != 0) {
                break;
            }
        }

        // Now calculate the stats.
        
        int leftoverCount = sofar.getAbsoluteSum();
        double leftoverRatio = leftoverCount / (double) characterCount;

        int foundCount = characterCount - sofar.getPositiveSum();
        double characterRatio = foundCount / (double) characterCount;

        sofar = sofar.getAlphanumerics();
        foundCount = alphanumericCount - sofar.getPositiveSum();
        double alphanumericRatio = foundCount / (double) alphanumericCount;

        return new Match(header, characterRatio, alphanumericRatio,
                leftoverRatio, at, alphanumericLineCount);
    }

    /** {@inheritDoc} */
    public String toString() {
        return String.format("%s: %d %d %3.1f %3.1f %3.1f",
                header, lineCount, alphanumericLineCount, characterRatio * 100,
                alphanumericRatio * 100, leftoverRatio * 100);
    }

    /** {@inheritDoc} */
    public int hashCode() {
        double sum =
            characterRatio + alphanumericRatio + leftoverRatio + lineCount +
            alphanumericLineCount;
        return Float.floatToIntBits((float) sum);
    }

    /** {@inheritDoc} */
    public boolean equals(Object other) {
        if (! (other instanceof Match)) {
            return false;
        }

        Match m = (Match) other;

        return (lineCount == m.lineCount) &&
            (alphanumericLineCount == m.alphanumericLineCount) &&
            (leftoverRatio == m.leftoverRatio) &&
            (characterRatio == m.characterRatio) &&
            (alphanumericRatio == m.alphanumericRatio);
    }

    /** {@inheritDoc} */
    public int compareTo(Match other) {
        if (alphanumericLineCount > other.alphanumericLineCount) {
            return 1;
        } else if (alphanumericLineCount < other.alphanumericLineCount) {
            return -1;
        }

        if (characterRatio > other.characterRatio) {
            return 1;
        } else if (characterRatio < other.characterRatio) {
            return -1;
        }
        
        if (alphanumericRatio > other.alphanumericRatio) {
            return 1;
        } else if (alphanumericRatio < other.alphanumericRatio) {
            return -1;
        }

        // This is reversed, since a low leftover count is better.
        if (leftoverRatio > other.leftoverRatio) {
            return -1;
        } else if (leftoverRatio < other.leftoverRatio) {
            return 1;
        }
        
        if (lineCount > other.lineCount) {
            return 1;
        } else if (lineCount < other.lineCount) {
            return -1;
        }

        return 0;
    }

    /**
     * Gets the header associated with this instance.
     */
    public Header getHeader() {
        return header;
    }

    /**
     * Returns whether this is actually a "good" match, that is, good
     * enough to be close enough to the desired header to warrant
     * replacement.
     */
    public boolean isGoodMatch() {
        return (alphanumericRatio >= 0.9) &&
            (leftoverRatio <= 0.1);
    }

    /**
     * Performs header replacement in the given file (array of lines)
     * based on this match. This returns a new array; this doesn't
     * alter the given array.
     */
    public String[] replace(String[] lines) {
        int headerAt = 0; // This may eventually change.
        int yearIndex = header.getYearIndex();
        String existingYear = null;

        if (yearIndex >= 0) {
            existingYear = Utils.getYear(lines);
        }
        
        lines = Utils.replace(lines, headerAt, lineCount,
                header.getHeaderLines());

        if (existingYear != null) {
            lines[yearIndex] =
                Utils.replaceYear(lines[yearIndex], existingYear);
        }
        
        return lines;
    }
}
