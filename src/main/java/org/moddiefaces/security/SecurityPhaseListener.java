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
package org.moddiefaces.security;

import org.moddiefaces.config.ModdieContext;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import java.io.IOException;

public class SecurityPhaseListener implements PhaseListener {
    public static String loginUrl = "./login.jsf";
    private ModdieContext cmfContext = ModdieContext.getInstance();

    @Override
    public void afterPhase(PhaseEvent event) {
        // ignore
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        if(cmfContext.getCurrentUser() == null) {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            switch (ModdieContext.getInstance().getSecurityType()) {
                case LOCAL:
                    try {
                        externalContext.redirect(loginUrl);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;

                case PRINCIPAL:
                    // the container should prompt for authentication however the user has it set up
                    break;

                case CUSTOM:
                    try {
                        externalContext.redirect(cmfContext.getCustomLoginUrl());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
            }
        }
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }
}
