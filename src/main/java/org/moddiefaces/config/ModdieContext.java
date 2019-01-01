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

package org.moddiefaces.config;

import lombok.extern.slf4j.Slf4j;
import org.moddiefaces.core.ContentManager;
import org.moddiefaces.security.SecurityType;
import org.moddiefaces.persistence.EntityManagerProducer;

/**
 * @since 0.1
 * @author Dmytro Maidaniuk
 */
@Slf4j
public class ModdieContext {
    private ConfigFile configFile;
    private boolean initialized = false;
    private ContentManager contentManager;
    private EntityManagerProducer entityManagerProvider;
    private SecurityType securityType = SecurityType.NONE;
    private boolean embeddedDbNeedsConfig = false;
    private boolean inMemory;

    private ModdieContext() {
        
    }
    
    /**
     * Called to get a reference to the singleton INSTANCE.
     *
     * @return the INSTANCE.
     */
    public static ModdieContext getInstance() {
        return CmfContextHolder.INSTANCE;
    }
    
    public String getCustomLoginUrl() {
        return configFile.getCustomLoginUrl();
    }

    public String getCurrentUser() {
        return securityType.getCurrentUserInfo().getCurrentUser();
    }

    public boolean isInMemory() {
        return inMemory;
    }

    public void setInMemory(boolean inMemory) {
        this.inMemory = inMemory;
    }

    public ConfigFile getConfigFile() {
        return configFile;
    }

    public void setConfigFile(ConfigFile configFile) {
        this.configFile = configFile;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public boolean isEmbeddedDbNeedsConfig() {
        return embeddedDbNeedsConfig;
    }

    public void setEmbeddedDbNeedsConfig(boolean embeddedDbNeedsConfig) {
        this.embeddedDbNeedsConfig = embeddedDbNeedsConfig;
    }

    public SecurityType getSecurityType() {
        return securityType;
    }

    public void setSecurityType(SecurityType securityType) {
        this.securityType = securityType;
    }

    public ContentManager getContentManager() {
        if (contentManager == null) {
            String contentManagerName = configFile.getContentManager();

            try {
                contentManager = (ContentManager) getClass().getClassLoader().loadClass(contentManagerName).newInstance();
            }
            catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                log.error("Context not started", e);
            }
        }

        return contentManager;
    }

    public EntityManagerProducer getEntityManagerProducer() {
        if(entityManagerProvider == null) {
            String entityManagerProviderName = configFile.getEntityManagerProvider();
            try {
                entityManagerProvider = (EntityManagerProducer)
                        getClass().getClassLoader().loadClass(entityManagerProviderName).newInstance();
            } catch(ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return entityManagerProvider;
    }

    /**
     * A private class to hold our single INSTANCE.
     */
    private static class CmfContextHolder {
        public static final ModdieContext INSTANCE = new ModdieContext();
    }
    
}