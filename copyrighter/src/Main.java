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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Main for copyrighter tool.
 */
public final class Main {
    /** processing options */
    public final Options options;

    /** all the possible headers */
    public final ArrayList<Header> headers;
    
    /**
     * Runs!
     */
    static public void main(String[] args) throws Exception {
        Options opts = new Options();
        ArrayList<String> files = new ArrayList<String>();
        boolean optsDone = false;

        for (String arg : args) {
            if (optsDone) {
                files.add(arg);
            } else {
                if (!arg.startsWith("--")) {
                    optsDone = true;
                    files.add(arg);
                } else if (arg.equals("--")) {
                    optsDone = true;
                } else if (arg.equals("--update-match")) {
                    opts.updateMatch = true;
                } else if (arg.equals("--force-update")) {
                    opts.forceUpdate = true;
                } else if (arg.equals("--add-new")) {
                    opts.addNew = true;
                } else if (arg.equals("--warn-unrecognized")) {
                    opts.warnUnrecognized = true;
                } else if (arg.startsWith("--headers-dir=")) {
                    opts.headersDir = arg.substring(arg.indexOf('=') + 1);
                } else if (arg.startsWith("--max-lines=")) {
                    String value = arg.substring(arg.indexOf('=') + 1);
                    opts.maxLines = Integer.parseInt(value);
                } else {
                    System.err.println("Unknown option: " + arg);
                }
            }
        }

        if (files.size() == 0) {
            files.add(".");
        }

        opts.files = files.toArray(new String[0]);

        Main main = new Main(opts);
        main.readHeaders();
        main.processFiles();
    }

    /**
     * Constructs an instance.
     */
    public Main(Options options) {
        this.options = options;
        this.headers = new ArrayList<Header>();
    }

    /**
     * Reads all the headers.
     */
    private void readHeaders() throws IOException {
        readHeadersIn(new File(options.headersDir));

        if (headers.size() == 0) {
            System.err.println("no header files found");
            System.exit(1);
        }
    }

    /**
     * Reads all the headers in a given directory.
     */
    private void readHeadersIn(File dir) throws IOException {
        File[] files = dir.listFiles();

        for (File f : files) {
            if (! f.canRead()) {
                continue;
            }

            if (f.isDirectory()) {
                readHeadersIn(f);
            } else if (f.isFile()) {
                String name = f.getName();
                if (name.endsWith(".txt") && !name.startsWith("README")) {
                    Header header = Header.read(f);
                    headers.add(header);
                }
            }
        }
    }

    /**
     * Processes all the files.
     */
    private void processFiles() throws IOException {
        for (String name : options.files) {
            processOne(new File(name));
        }
    }

    /**
     * Processes the given file or directory.
     */
    private void processOne(File file) throws IOException {
        if (! file.canRead()) {
            return;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                processOne(f);
            }
        } else if (file.isFile() && file.canWrite()) {
            System.err.println("processing: " + file);
            processFile(file);
        }
    }

    /**
     * Processes the given file.
     */
    private void processFile(File file) throws IOException {
        String[] lines = Utils.readFile(file, options.maxLines);
        String[] newLines;

        if (lines == null) {
            System.err.println("ignoring overly huge file");
            return;
        }

        if (isCopyrighted(lines)) {
            newLines = processCopyrightedFile(file, lines);
        } else {
            newLines = processUncopyrightedFile(file, lines);
        }
        
        if (lines != newLines) {
            Utils.writeFile(file, newLines);
        }
    }

    /**
     * Processes an uncopyrighted file, returning the new version (if any).
     */
    private String[] processUncopyrightedFile(File file, String[] lines) {
        if (! options.addNew) {
            System.err.println("warning: no header found");
            return lines;
        }

        String path = file.getAbsolutePath();
        Header best = null;
        for (Header h : headers) {
            if (h.nameMatches(path)) {
                if ((best == null) || h.isMoreSpecificThan(best, path)) {
                    best = h;
                }
            }
        }

        if (best == null) {
            if (options.warnUnrecognized) {
                System.err.println("warning: no suitable header found");
            }
            return lines;
        }

        lines = Utils.insert(lines, 0, best.getHeaderLines());
        return lines;
    }

    /**
     * Processes a copyrighted file, returning the new version (if any).
     */
    private String[] processCopyrightedFile(File file, String[] lines) {
        Match[] matches = calculateMatches(file, lines);
        Match best = bestMatch(matches);

        if ((best == null) || ! best.isGoodMatch()) {
            if (options.warnUnrecognized) {
                System.err.println("warning: no good header match found (" +
                        "best is " + best + ")");
            }
        } else if (options.updateMatch) {
            if (options.forceUpdate ||
                    best.getHeader().getGenerallyUpdate()) {
                lines = best.replace(lines);
            }
        }

        return lines;
    }
            
    /**
     * Get a match score for each of the headers.
     */
    private Match[] calculateMatches(File file, String[] lines) {
        String path = file.getAbsolutePath();
        ArrayList<Match> result = new ArrayList<Match>();

        for (Header header : headers) {
            if (header.nameMatches(path)) {
                result.add(Match.match(lines, header));
            }
        }

        return (Match[]) result.toArray(new Match[0]);
    }

    /**
     * Figures out the best match of the bunch.
     */
    private static Match bestMatch(Match[] matches) {
        int length = matches.length;
        Match sofar = null;

        for (Match m : matches) {
            if ((sofar == null) || (sofar.compareTo(m) < 0)) {
                sofar = m;
            }
        }

        return sofar;
    }

    /**
     * Returns whether the word "copyright" or "license" appears in the
     * first 100 lines of the given file (given as an array of lines).
     */
    private static boolean isCopyrighted(String[] lines) {
        return Utils.match(lines, 100, "[Cc]opyright") ||
            Utils.match(lines, 100, "[Ll]icense");
    }
}
