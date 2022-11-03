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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A single copyright header.
 */
public final class Header {
    /** id of the header (for human identification purposes) */
    private final String id;
    
    /** text of the header */
    private final String[] lines;

    /** text of the header but only alphanumerics, stored as a set */
    private final HashSet<String> lineSet;

    /**
     * line index of the year in the header, or <code>-1</code> if there is
     * none
     */
    private final int yearIndex;

    /** map of all ASCII-7 characters, with counts of each */
    private final AsciiMap characters;

    /**
     * filename pattern (regex) that identifies files that should have this
     * header
     */
    private final Pattern namePattern;

    /** whether to update headers that match this (may be overridden) */
    private final boolean generallyUpdate;

    /**
     * Constructs an instance.
     */
    public Header(String id, String[] lines, String namePattern,
            boolean generallyUpdate) {
        this.id = id;
        this.lines = lines;
        this.lineSet = new HashSet<String>();
        this.characters = new AsciiMap();
        this.generallyUpdate = generallyUpdate;

        namePattern = (namePattern != null) ? namePattern : ".";
        this.namePattern = Pattern.compile(namePattern);

        int yearIndex = -1;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String strippedLine = Utils.getAlphaNumerics(line);
            lineSet.add(strippedLine);
            characters.add(line);
            if ((yearIndex < 0) && Utils.hasYear(line)) {
                yearIndex = i;
            }
        }

        this.yearIndex = yearIndex;
    }

    /**
     * Constructs an instance by reading the given file.
     * 
     * @param fileName name of file to read
     * @return an appropriately-constructed instance
     */
    public static Header read(File file) throws IOException {
        FileReader reader = new FileReader(file);
        BufferedReader buf = new BufferedReader(reader);
        ArrayList<String> lines = new ArrayList<String>();
        String id = file.getName();
        String namePattern = null;
        boolean generallyUpdate = true;
        
        for (;;) {
            String line = buf.readLine();

            if ((line == null) || line.equals("<EOF>")) {
                break;
            }

            lines.add(line);
        }

        for (;;) {
            String line = buf.readLine();

            if (line == null) {
                break;
            }

            line = line.replaceFirst("^[ \t]*", "");
            line = line.replaceFirst("[ \t]*$", "");
            line = line.replaceFirst("^#.*$", "");

            if (line.length() == 0) {
                continue;
            }

            String[] keyValue = line.split("[ \t]*=[ \t]*", 2);
            if (keyValue.length != 2) {
                throw new IOException("Bad key-value line: " + line);
            }

            String key = keyValue[0];
            String value = keyValue[1];

            if (key.equals("name")) {
                namePattern = value;
            } else if (key.equals("id")) {
                id = value;
            } else if (key.equals("update")) {
                generallyUpdate = !value.equals("false");
            } else {
                throw new IOException("Unknown key: " + key);
            }
        }

        reader.close();

        // Make sure there's a blank line at the end of the header.
        if (lines.get(lines.size() - 1).length() != 0) {
            lines.add("");
        }

        return new Header(id, lines.toArray(new String[0]), namePattern,
                generallyUpdate);
    }

    /** {@inheritDoc} */
    public String toString() {
        return id;
    }

    /**
     * Gets a copy of the character map.
     */
    public AsciiMap getCharacters() {
        return new AsciiMap(characters);
    }

    /**
     * Gets whether the header contains a line with the same alphanumerics
     * as the given one.
     */
    public boolean hasMatchingLine(String line) {
        line = Utils.getAlphaNumerics(line);
        return lineSet.contains(line);
    }

    /**
     * Gets the lines of the header.
     */
    public String[] getHeaderLines() {
        return lines;
    }

    /**
     * Gets the year index or <code>-1</code> if there is none.
     */
    public int getYearIndex() {
        return yearIndex;
    }

    /**
     * Gets whether this header is generally to be updated when found.
     */
    public boolean getGenerallyUpdate() {
        return generallyUpdate;
    }

    /**
     * Returns whether or not the given name matches the pattern of this
     * header.
     */
    public boolean nameMatches(String name) {
        Matcher matcher = namePattern.matcher(name);
        return matcher.find();
    }

    /**
     * Returns whether this instance's name is more specific than the
     * given other one, in the context of matching the given name. It
     * does this by comparing the relative sizes of the matched text.
     */
    public boolean isMoreSpecificThan(Header other, String name) {
        Matcher thisMatch = namePattern.matcher(name);
        Matcher otherMatch = other.namePattern.matcher(name);

        if (!(thisMatch.find() && otherMatch.find())) {
            // This shouldn't happen.
            return false;
        }
        
        int thisLength = thisMatch.end() - thisMatch.start();
        int otherLength = otherMatch.end() - otherMatch.start();

        return (thisLength > otherLength);
    }
}
