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

import java.util.Date;

/**
 * User: billreh
 * Date: 1/18/11
 * Time: 11:41 PM
 */
public class Content extends Template {
    private Date dateCreated;
    private Date dateModified;


    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Content)) return false;
        if (!super.equals(o)) return false;

        Content content = (Content) o;

        if (dateCreated != null ? !dateCreated.equals(content.dateCreated) : content.dateCreated != null) return false;
        return !(dateModified != null ? !dateModified.equals(content.dateModified) : content.dateModified != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (dateCreated != null ? dateCreated.hashCode() : 0);
        result = 31 * result + (dateModified != null ? dateModified.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Content{" +
                "dateCreated=" + dateCreated +
                ", dateModified=" + dateModified +
                "} " + super.toString();
    }
}
