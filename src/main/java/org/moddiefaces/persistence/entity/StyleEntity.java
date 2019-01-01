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

package org.moddiefaces.persistence.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * User: billreh
 * Date: 1/30/11
 * Time: 8:09 AM
 */
@Entity(name = "style")
public class StyleEntity implements Serializable {
    private long id;
    private long namespaceId;
    private NamespaceEntity namespace;
    private String name;
    private String style;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "styleGen")
    @TableGenerator(name = "styleGen",
            table = "id_gen",
            pkColumnName = "gen_name",
            valueColumnName = "gen_val",
            pkColumnValue = "style_id",
            initialValue = 0,
            allocationSize = 50
    )
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(insertable = false, updatable = false, name = "namespace_id")
    public long getNamespace_id() {
        return namespaceId;
    }

    public void setNamespace_id(long namespaceId) {
        this.namespaceId = namespaceId;
    }

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "namespace_id", referencedColumnName = "ID", insertable = true, updatable = true)
    public NamespaceEntity getNamespace() {
        return namespace;
    }

    public void setNamespace(NamespaceEntity namespace) {
        this.namespace = namespace;
    }

    @Column
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column
    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    private Set<GroupPermissionsEntity> groupPermissions;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "group_permissions_to_style",
            joinColumns = { @JoinColumn(name = "style_id") },
            inverseJoinColumns = { @JoinColumn(name = "group_permissions_id") })
    public Set<GroupPermissionsEntity> getGroupPermissions() {
        return groupPermissions;
    }

    public void setGroupPermissions(Set<GroupPermissionsEntity> groupPermissions) {
        this.groupPermissions = groupPermissions;
    }
}
