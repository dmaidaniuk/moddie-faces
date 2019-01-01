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

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.html.HtmlInputTextarea;

/**
 * @since 0.1
 * @author Dmytro Maidaniuk
 */
@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="jquery/jquery.js", target = "head"),
	@ResourceDependency(library="moddiefaces-external", name="jquery.markitup.js", target = "head"),
    @ResourceDependency(library="moddiefaces-external", name="markdown.set.js", target = "head"),
    @ResourceDependency(library="moddiefaces-external", name="markdown.css", target = "head"),
    @ResourceDependency(library="moddiefaces-external", name="markitup.css", target = "head")
})

@FacesComponent(HtmlMarkdown.COMPONENT_TYPE)
public class HtmlMarkdown extends HtmlInputTextarea {
    
    public static final String COMPONENT_TYPE = "org.moddiefaces.component.Markdown";
    
    /** The standard component family. **/
	public static final String COMPONENT_FAMILY = "org.moddiefaces.component.family";
    
    public static final String RENDERER_TYPE = "org.moddiefaces.component.MarkdownRenderer";

    public HtmlMarkdown() {
        super();
        this.setRendererType(RENDERER_TYPE);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
    
}
