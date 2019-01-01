/*
 * Copyright (c) 2015 Dmytro Maidaniuk.
 *
 * This file is part of ModdieFaces.
 *
 * ModdieFaces is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package org.moddiefaces.faces.component.content;

import org.moddiefaces.faces.component.DynamicResourceLoader;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;
import java.io.IOException;

@FacesRenderer(componentFamily = "javax.faves.Output", rendererType = "ContentResource")
public class ContentResourceRenderer extends Renderer {
    private static final String resourceLibrary = "cmfDynamicResources";


    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ContentResource content = (ContentResource) component;
        String name = content.getNamespace().replaceAll("\\.", "-")+ "-" + content.getName() +
                ("style".equals(content.getType()) ? ".css" : ".js");
        String path = DynamicResourceLoader.encodeDynamicResource(context, resourceLibrary,
                name, content.getContent());

        context.getResponseWriter().write(
                "<link type=\"text/css\" rel=\"stylesheet\" href=\"" + path + "\"/>");
    }
}
