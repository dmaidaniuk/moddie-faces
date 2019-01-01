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
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package org.moddiefaces.core;

import org.primefaces.model.TreeNode;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

/**
 * User: billreh
 * Date: 1/18/11
 * Time: 11:42 PM
 */
public abstract class BaseContent implements Serializable, TreeContent {
    private String name;
    private Namespace namespace;
    private List<GroupPermissions> groupPermissionsList = new Vector<GroupPermissions>();
    private TreeNode treeNode;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
    }

    public String getFullName() {
        return namespace.getFullName() + "." + name;
    }

    public List<GroupPermissions> getGroupPermissionsList() {
        return groupPermissionsList;
    }

    public void setGroupPermissionsList(List<GroupPermissions> groupPermissionsList) {
        this.groupPermissionsList = groupPermissionsList;
    }

    @Override
    public TreeNode getTreeNode() {
        return treeNode;
    }

    @Override
    public void setTreeNode(TreeNode treeNode) {
        this.treeNode = treeNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseContent)) return false;

        BaseContent that = (BaseContent) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        //noinspection RedundantIfStatement
        if (namespace != null ? !namespace.equals(that.namespace) : that.namespace != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BaseContent{" +
                "name='" + name + '\'' +
                ", namespace=" + namespace +
                '}';
    }
}
