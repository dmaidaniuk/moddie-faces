/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 ModdieFaces.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.moddiefaces.core;

import org.moddiefaces.persistence.entity.GroupEntity;

import java.io.Serializable;
import java.util.List;

/**
 * This interface represents the link between your content and the outside world. 
 * It is where the content is retrieved from /written to the data store.
 * 
 * @since 0.1
 * @author Dmytro Maidaniuk 
 */
public interface ContentManager extends Serializable {
    /**
     * Load a list of all namespaces.
     *
     * @return List of all of the {@link Namespace} objects.
     */
    public List<Namespace> loadAllNamespaces();

    /**
     * Load a list of all namespaces under and including <code>namespace</code>.
     * @param namespace The {@link Namespace} object to search under.
     *
     * @return List of all of the {@link Namespace} objects under and including <code>namespace</code>.
     */
    public List<Namespace> loadNamespace(Namespace namespace);

    /**
     * Save a namespace.
     *
     * @param namespace The namespace to save.
     */
    public void saveNamespace(Namespace namespace);

    /**
     * Delete a namespace.
     *
     * @param namespace The namespace to delete.
     */
    public void deleteNamespace(Namespace namespace);

    public List<Content> loadAllContent();
    public List<Content> loadContent(Namespace namespace);
    public Content loadContent(Namespace namespace, String name);
    public void saveContent(Content content);
    public void deleteContent(Content content);

    public List<Script> loadAllScripts();
    public List<Script> loadScript(Namespace namespace);
    public Script loadScript(Namespace namespace, String name);
    public void saveScript(Script script);
    public void deleteScript(Script script);

    public List<Style> loadAllStyles();
    public List<Style> loadStyle(Namespace namespace);
    public Style loadStyle(Namespace namespace, String name);
    public void saveStyle(Style style);
    public void deleteStyle(Style style);

    public void saveGroup(GroupEntity group);
    public List<Namespace> loadChildNamespaces(Namespace namespace);

    public List<String> getAllGroups();
}
