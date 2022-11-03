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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility methods.
 */
public final class Utils {
    /** maximum allowed file length for {@link #readFile} */
    private static final int MAX_FILE_SIZE = (1024 * 1024 * 3) / 2;
    
    /** regex pattern for a year or range thereof */
    private static final Pattern YEAR_PATTERN = 
        Pattern.compile("[0-9][0-9][0-9][---, 0-9]*[0-9]");

    /**
     * Reads the given file as a series of strings, one for each line.
     * This returns <code>null</code> if the file contains more than
     * the given maximum number of lines. It will also refuse to read
     * (returning <code>null</code>) files larger than 1.5 megs.
     */
    public static String[] readFile(File file, int maxLines)
            throws IOException {
        if (file.length() > MAX_FILE_SIZE) {
            return null;
        }
        
        FileReader reader = new FileReader(file);
        BufferedReader buf = new BufferedReader(reader);
        ArrayList<String> lines = new ArrayList<String>();

        try {
            for (;;) {
                String line = buf.readLine();
                
                if (line == null) {
                    break;
                }
                
                if (lines.size() >= maxLines) {
                    return null;
                }
                
                lines.add(line);
            }
        } finally {
            reader.close();
        }
        
        return lines.toArray(new String[0]);
    }

    /**
     * Writes the given file.
     */
    public static void writeFile(File file, String[] lines)
            throws IOException {
        FileWriter writer = new FileWriter(file);
        BufferedWriter buf = new BufferedWriter(writer);

        for (String line : lines) {
            buf.write(line);
            buf.write('\n');
        }

        buf.flush();
        writer.close();
    }

    /**
     * Returns whether the given regex is matched within the given number
     * of lines (elements of the array).
     */
    public static boolean match(String[] lines, int limit, String regex) {
        Pattern pattern = Pattern.compile(regex);

        if (limit > lines.length) {
            limit = lines.length;
        }

        for (int i = 0; i < limit; i++) {
            Matcher matcher = pattern.matcher(lines[i]);
            if (matcher.find()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Inserts the second list of lines into the first, at (just
     * before) a specific offset. This returns a new array of lines;
     * the arguments are unmodified.
     */
    public static String[] insert(String[] orig, int offset, String[] insert) {
        int origLength = orig.length;
        int insertLength = insert.length;
        String[] result = new String[origLength + insertLength];

        System.arraycopy(orig, 0, result, 0, offset);
        System.arraycopy(insert, 0, result, offset, insertLength);
        System.arraycopy(orig, offset, result, offset + insertLength,
                origLength - offset);

        return result;
    }

    /**
     * Deletes a range of lines from the given array. This returns a
     * new array of lines; the arguments are unmodified.
     */
    public static String[] delete(String[] orig, int offset, int length) {
        int origLength = orig.length;
        String[] result = new String[origLength - length];

        System.arraycopy(orig, 0, result, 0, offset);
        System.arraycopy(orig, offset + length, result, offset,
                origLength - offset - length);

        return result;
    }

    /**
     * Replaces a range of lines from the first array with the contents
     * of the second. This returns a new array of lines; the arguments
     * are unmodified.
     */
    public static String[] replace(String[] orig, int offset, int length,
            String[] replacement) {
        String[] result = delete(orig, offset, length);
        result = insert(result, offset, replacement);

        return result;
    }

    /**
     * Returns whether the given string contains a year.
     */
    public static boolean hasYear(String line) {
        return YEAR_PATTERN.matcher(line).find();
    }

    /**
     * Finds and return the first year in the given file. This will only
     * ever return a single year and not, e.g., a range or list of them.
     */
    public static String getYear(String[] lines) {
        for (String line : lines) {
            Matcher matcher = YEAR_PATTERN.matcher(line);
            if (matcher.find()) {
                int start = matcher.start();
                return line.substring(start, start + 4);
            }
        }

        return null;
    }

    /**
     * Replaces the year in the given string with the given new year.
     */
    public static String replaceYear(String orig, String newYear) {
        Matcher matcher = YEAR_PATTERN.matcher(orig);
        if (! matcher.find()) {
            return orig;
        }

        StringBuilder sb = new StringBuilder(orig.length());

        sb.append(orig.substring(0, matcher.start()));
        sb.append(newYear);
        sb.append(orig.substring(matcher.end()));

        return sb.toString();
    }

    /**
     * Returns only the alphanumeric characters of the given string,
     * in the same order as the original.
     */
    public static String getAlphaNumerics(String orig) {
        int length = orig.length();
        StringBuffer sb = new StringBuffer(length);

        for (int i = 0; i < length; i++) {
            char c = orig.charAt(i);
            if (((c >= '0') && (c <= '9')) ||
                    ((c >= 'a') && (c <= 'z')) ||
                    ((c >= 'A') && (c <= 'Z'))) {
                sb.append(c);
            }
        }

        return sb.toString();
    }
}
