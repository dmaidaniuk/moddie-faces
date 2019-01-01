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

package org.moddiefaces.faces.component.markdown;

import com.sun.faces.renderkit.Attribute;
import com.sun.faces.renderkit.AttributeManager;
import com.sun.faces.renderkit.RenderKitUtils;
import com.sun.faces.renderkit.html_basic.HtmlBasicInputRenderer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import java.io.IOException;
import org.moddiefaces.web.util.StringUtils;
import org.omnifaces.util.Faces;

/**
 * @since 0.1
 * @author Dmytro Maidaniuk
 */
@FacesRenderer(componentFamily = HtmlMarkdown.COMPONENT_FAMILY, rendererType = HtmlMarkdown.RENDERER_TYPE)
public class MarkdownRenderer extends HtmlBasicInputRenderer {

    private static final Attribute[] ATTRIBUTES = AttributeManager.getAttributes(AttributeManager.Key.INPUTTEXTAREA);

    @Override
    public void encodeBegin(final FacesContext context, final UIComponent component) throws IOException {
        rendererParamsNotNull(context, component);

        if (!shouldEncode(component)) {
            return;
        }

        super.encodeBegin(context, component);
    }

    /**
     * @return additional component style class for outer div.
     */
    protected String getComponentNameStyleClass() {
        return "moddie-component-markdown";
    }

    @Override
    public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
        rendererParamsNotNull(context, component);

        final HtmlMarkdown htmlComponent = (HtmlMarkdown) component;
        final ResponseWriter writer = context.getResponseWriter();

        if (!htmlComponent.isReadonly()) {
            super.encodeEnd(context, component);
        }

        this.renderAdditionalScript(context, htmlComponent);
    }

    /**
     * Method copied from super class to add html features.
     */
    @Override
    protected void getEndTextToRender(final FacesContext context, final UIComponent component, final String currentValue)
            throws IOException {

        final ResponseWriter writer = context.getResponseWriter();
        assert (writer != null);

        writer.startElement("textarea", component);
        this.writeIdAttributeIfNecessary(context, writer, component);
        writer.writeAttribute("name", component.getClientId(context), "clientId");

        String styleClass = (String) component.getAttributes().get("styleClass");
        
        if (StringUtils.isNotEmpty(styleClass)) {
            writer.writeAttribute("class", styleClass, "styleClass");
        }
        
        // style is rendered as a passthru attribute
        RenderKitUtils.renderPassThruAttributes(context, writer, component, ATTRIBUTES);
        RenderKitUtils.renderXHTMLStyleBooleanAttributes(writer, component);

        RenderKitUtils.renderOnchange(context, component, false);

        // render default text specified
        if (currentValue != null) {
            writer.writeText(currentValue, component, "value");
        }

        writer.endElement("textarea");

    }
    
    private void renderAdditionalScript(final FacesContext context, final HtmlMarkdown markdown) throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
        writer.startElement("script", markdown);
        if (!markdown.isReadonly()) {
            writer.writeText(createJQueryMarkdownPluginCall(markdown), null);
        } else {
            // TODO: What should be here?
        }
        writer.endElement("script");
    }
    
    private String createJQueryMarkdownPluginCall(HtmlMarkdown markdown) {
        final StringBuilder jQueryPluginCall = new StringBuilder();
        jQueryPluginCall.append("initMarkdown('");
        jQueryPluginCall.append(markdown.getClientId());
        jQueryPluginCall.append("', '");
        jQueryPluginCall.append(Faces.getRequest().getContextPath());
        jQueryPluginCall.append("');");
        return jQueryPluginCall.toString();
    }

}
