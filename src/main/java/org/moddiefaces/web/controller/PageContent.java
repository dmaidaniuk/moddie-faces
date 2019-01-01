/*
 * Copyright (c) 2011 Bill Reh.
 *
 * This file is part of Content Management Faces.
 *
 * Content Management Faces is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package org.moddiefaces.web.controller;

import org.moddiefaces.core.GroupPermissions;
import org.moddiefaces.core.Content;
import org.moddiefaces.core.Style;
import org.moddiefaces.core.BaseContent;
import org.moddiefaces.core.Namespace;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * User: billreh
 * Date: 10/11/11
 * Time: 8:43 PM
 */
@Slf4j
public class PageContent implements Serializable {
    
    private BaseContent theContent;
    private BaseContent contentToRemove;
    private List<BaseContent> namespaceContents = new ArrayList<>();
    private BaseContent baseContentToAdd;
    private Namespace namespaceToAdd;
    private Content contentToAdd;
    private Style styleToAdd;
    private boolean addingContent = false;
    private boolean addingStyle = false;
    private boolean addingNamespace = false;
    private boolean addingTopLevelNamespace = false;

    public PageContent() {
        log.info("New instance of PageContent created");
    }
    
    public BaseContent getBaseContent() {
        return theContent;
    }

    public Namespace getNamespace() {
        if(theContent instanceof Namespace)
            return (Namespace) theContent;
        else
            return theContent.getNamespace();
    }

    public Content getContent() {
        return (Content) theContent;
    }

    public Style getStyle() {
        return (Style) theContent;
    }

    public boolean isHasNamespace() {
        return theContent instanceof Namespace;
    }

    public boolean isHasContent() {
        return theContent instanceof Content;
    }

    public boolean isHasStyle() {
        return theContent instanceof Style;
    }

    public boolean isContentHasStyles() {
        return !getContent().getStyles().isEmpty();
    }

    public String getType() {
        return theContent.getClass().getSimpleName().toLowerCase();
    }

    public void setTheContent(BaseContent theContent) {
        this.theContent = theContent;
    }

    public List<BaseContent> getNamespaceContents() {
        return namespaceContents;
    }

    public void setNamespaceContents(List<BaseContent> namespaceContents) {
        this.namespaceContents = namespaceContents;
    }

    public BaseContent getContentToRemove() {
        return contentToRemove;
    }

    public void setContentToRemove(BaseContent contentToRemove) {
        this.contentToRemove = contentToRemove;
    }

    public Namespace getNamespaceToAdd() {
        return namespaceToAdd;
    }

    public Void setNamespaceToAdd(Namespace namespaceToAdd) {
        this.namespaceToAdd = namespaceToAdd;
        this.baseContentToAdd = namespaceToAdd;
        return null;
    }

    public Content getContentToAdd() {
        return contentToAdd;
    }

    public void setContentToAdd(Content contentToAdd) {
        this.contentToAdd = contentToAdd;
        this.baseContentToAdd = contentToAdd;
    }

    public Style getStyleToAdd() {
        return styleToAdd;
    }

    public void setStyleToAdd(Style styleToAdd) {
        this.styleToAdd = styleToAdd;
        this.baseContentToAdd = styleToAdd;
    }

    public boolean isAddingContent() {
        return addingContent;
    }

    public void setAddingContent(boolean addingContent) {
        this.addingContent = addingContent;
    }

    public boolean isAddingStyle() {
        return addingStyle;
    }

    public void setAddingStyle(boolean addingStyle) {
        this.addingStyle = addingStyle;
    }

    public boolean isAddingNamespace() {
        return addingNamespace;
    }

    public Void setAddingNamespace(boolean addingNamespace) {
        this.addingNamespace = addingNamespace;
        return null;
    }

    public boolean isAddingTopLevelNamespace() {
        return addingTopLevelNamespace;
    }

    public void setAddingTopLevelNamespace(boolean addingTopLevelNamespace) {
        this.addingTopLevelNamespace = addingTopLevelNamespace;
    }

    public BaseContent getBaseContentToAdd() {
        return baseContentToAdd;
    }

    void addGroupToNewContent(GroupPermissions group) {
        if(isAddingContent() || isAddingNamespace() || isAddingTopLevelNamespace() || isAddingStyle())
            baseContentToAdd.getGroupPermissionsList().add(group);
    }
}
