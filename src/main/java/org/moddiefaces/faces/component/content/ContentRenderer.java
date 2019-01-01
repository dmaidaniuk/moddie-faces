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

import org.moddiefaces.core.ContentManager;
import org.moddiefaces.core.Namespace;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;
import java.io.IOException;
import org.omnifaces.config.BeanManager;

@FacesRenderer(rendererType = ContentRenderer.RENDERER_TYPE, componentFamily = UIOutput.COMPONENT_TYPE)
public class ContentRenderer extends Renderer {
    
    /** The standard renderer type. */
    public static final String RENDERER_TYPE = "org.moddiefaces.renderkit.ContentRenderer";
    
    protected ContentManager contentManager;
    
    private String contentValue;

//    public ContentManager getContentManager() {
//        if(contentManager == null) {
//            try {
//                contentManager = ModdieContext.getInstance().getContentManager();
//            } catch(Exception e) {
//                // ignore
//            }
//        }
//        return  contentManager;
//    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        contentValue = getContent((Content)component);
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        if(contentValue == null)
            super.encodeChildren(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();
        if(contentValue != null) {
            responseWriter.startElement("span", component);
            writeAttributes(context, component);
            responseWriter.write(contentValue);
            responseWriter.endElement("span");
            contentValue = null;
        }
        responseWriter.flush();
    }

    private void writeAttributes(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter responseWriter = context.getResponseWriter();
        Content content = (Content) component;
        responseWriter.writeAttribute("id", content.getId(), "clientId");
        if(content.getStyle() != null)
            responseWriter.writeAttribute("style", content.getStyle(), "style");
        if(content.getStyleClass() != null)
            responseWriter.writeAttribute("class", content.getStyleClass(), "styleClass");
        if(content.getDir() != null)
            responseWriter.writeAttribute("dir", content.getDir(), "dir");
        if(content.getLang() != null)
            responseWriter.writeAttribute("lang", content.getLang(), "lang");
        if(content.getTitle() != null)
            responseWriter.writeAttribute("title", content.getTitle(), "title");
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    /**
     * Retrieve the content from wherever we're storing it.
     *
     * @param content The content component
     *
     * @return The found content, or null.
     */
    protected String getContent(Content content) {
        Namespace namespace = Namespace.createFromString(content.getNamespace());
        String name = content.getName();
        contentManager = BeanManager.INSTANCE.getReference(ContentManager.class);
        org.moddiefaces.core.Content c = contentManager.loadContent(namespace, name);
        return c == null ? null : c.getContent();
    }
}
