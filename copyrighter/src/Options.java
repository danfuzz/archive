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
 * Options for running the tool.
 */
public final class Options {
    /** whether to update copyrights that fuzzily match one of the templates */
    public boolean updateMatch = false;

    /**
     * whether to force an update even for headers that indicate they
     * aren't generally used for updates
     */
    public boolean forceUpdate = false;

    /** whether to add copyrights to files that don't have them */
    public boolean addNew = false;
    
    /** whether to warn when an unrecognized copyright is found */
    public boolean warnUnrecognized = false;

    /** maximum number of lines a file may have */
    public int maxLines = 10000;

    /** directory containing all the copyright headers */
    public String headersDir = ".";

    /** files or directories to process */
    public String[] files = null;
}
