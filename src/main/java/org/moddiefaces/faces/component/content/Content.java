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

package org.moddiefaces.faces.component.content;

import org.moddiefaces.core.ContentManager;
import org.moddiefaces.core.Namespace;
import org.moddiefaces.core.Script;
import org.moddiefaces.core.Style;

import javax.faces.application.Application;
import javax.faces.component.FacesComponent;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ListenerFor;
import javax.faces.event.PostAddToViewEvent;
import org.omnifaces.config.BeanManager;

/**
 * @since 0.1
 * @author Dmytro Maidaniuk
 */
@FacesComponent(Content.COMPONENT_TYPE)
@ListenerFor(systemEventClass = PostAddToViewEvent.class)
public class Content extends HtmlOutputText {
    
    /** The standard component type. */
    public static final String COMPONENT_TYPE = "org.moddiefaces.component.Content";
    
    protected ContentManager contentManager;

    protected enum PropertyKeys {
        namespace,
        name
    }

    @Override
    public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
        FacesContext context = FacesContext.getCurrentInstance();

        contentManager = BeanManager.INSTANCE.getReference(ContentManager.class);
        org.moddiefaces.core.Content content = contentManager.loadContent(Namespace.createFromString(getNamespace()), getName());

        if(content != null) {
            for(Script resource : content.getScripts()) {
                addResource(context, resource);
            }

            for(Style resource : content.getStyles()) {
                addResource(context, resource);
            }
        }
    }

    public void addResource(FacesContext context, Style style) {
        addResource(context, style.getName(), style.getNamespace().getFullName(), style.getStyle(), "style");
    }

    public void addResource(FacesContext context, Script script) {
        addResource(context, script.getName(), script.getNamespace().getFullName(), script.getScript(), "script");
    }

    private void addResource(FacesContext context, String name, String namespace, String content, String type) {
        if(content != null && ! content.isEmpty()) {
            Application application = context.getApplication();
            ContentResource contentResource = (ContentResource)
                    application.createComponent(context, "ContentResource", "ContentResourceRenderer");
            contentResource.setContent(content);
            contentResource.setType(type);
            contentResource.setName(name);
            contentResource.setNamespace(namespace);
            context.getViewRoot().addComponentResource(context, contentResource, "head");
        }
    }

    public String getName() {
        return (String) getStateHelper().eval(PropertyKeys.name);
    }

    public void setName(String name) {
        getStateHelper().put(PropertyKeys.name, name);
    }

    public String getNamespace() {
        return (String) getStateHelper().eval(PropertyKeys.namespace);
    }

    public void setNamespace(String namespace) {
        getStateHelper().put(PropertyKeys.namespace, namespace);
    }
}
