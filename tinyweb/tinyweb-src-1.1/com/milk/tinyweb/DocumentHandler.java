// Copyright (c) 2000-2001 Dan Bornstein, danfuzz@milk.com. All rights 
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

package com.milk.tinyweb;

/**
 * Interface for serving a hierarchy of documents. Instances of this
 * interface get registered with a {@link TinyWebServer} at a particular
 * path, and, when a request comes that mentions the path as a prefix, then
 * the instance of this interface gets called to return the actual document
 * response.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public interface DocumentHandler
{
    /**
     * Return the document which corresponds to the given partial path. The
     * given query string, if non-<code>null</code>, is the query part of
     * the component which referred to this instance, and will come in
     * uninterpreted (that is, no escapes will have been expanded). The
     * partial path is everything after the prefix which was matched to get
     * to this instance, and <i>won't</i> start with a slash
     * (<code>"/"</code>). The path will actually be canonicalized in
     * general, and so won't ever contain two slashes in a row or a path
     * component of either <code>"."</code> or <code>".."</code>, but
     * it will <i>not</i> have undergone <code>%</code> expansion.
     * The path will be passed as <code>null</code> if there were no
     * additional path components after the path that led to this
     * handler. The full request object is provided for cases where
     * the partial path is insufficient (e.g., it allows one to access
     * the entity body on <code>POST</code> requests).
     *
     * @param partialPath null-ok; the partial path to the document
     * @param query null-ok; the query string associated with the
     * path that got to this handler
     * @param request non-null; the full request object which triggered
     * the call to this method
     * @return null-ok; the document corresponding to the partial path
     * or <code>null</code> if there is no applicable document 
     */
    public Document handleRequest (String query, String partialPath,
				   HttpRequest request);

    /**
     * Put a new document or handler at the given partial path. If there is
     * already a document at the indicated path, then it should be
     * replaced. The partial path should <i>not</i> start with a slash.
     * Individual instances are allowed to throw an exception if they don't
     * support this sort of incremental modification of the hierarchy they
     * manage. This is used by a server to build up its document hierarchy;
     * it is <i>not</i> called in response to a PUT request. ({@link
     * TinyWebServer} doesn't support PUT requests.)
     *
     * @param partialPath non-null; the partial path to the document
     * @param doc non-null; the document or handler to store 
     */
    public void putDocument (String partialPath, DocumentHandler doc);
}
