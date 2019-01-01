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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.faces.context.FacesContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

/**
 * @since 0.1
 * @author Dmytro Maidaniuk
 */
@Slf4j
public class ConfigFile {
    
    private static final String CONFIG_PATH = "/WEB-INF/moddie-config.xml";
    
    private String contentManager;
    
    private String entityManagerProvider;
    
    private Map<String,String> persistenceProperties = new HashMap<>();
    
    private String customLoginUrl;

    public ConfigFile() {
        this(true);
    }

    public ConfigFile(boolean parse) {
        if (parse) {
            parseXml();
        }
    }

    public String getContentManager() {
        return contentManager;
    }

    public void setContentManager(String contentManager) {
        this.contentManager = contentManager;
    }

    public String getEntityManagerProvider() {
        return entityManagerProvider;
    }

    public void setEntityManagerProvider(String entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    public Map<String, String> getPersistenceProperties() {
        return persistenceProperties;
    }

    public void setPersistenceProperties(Map<String, String> persistenceProperties) {
        this.persistenceProperties = persistenceProperties;
    }

    public final void parseXml() {
        InputStream stream = null;
        try {
            stream = FacesContext.getCurrentInstance().getExternalContext().
                    getResourceAsStream(CONFIG_PATH);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(stream);
            Element root = document.getDocumentElement();

            NodeList nodes = root.getElementsByTagName("content-manager");
            for (int i = 0; i < nodes.getLength(); i++) {
                Node n = nodes.item(i);
                if (n.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                contentManager = nodes.item(i).getTextContent();
            }

            nodes = root.getElementsByTagName("entity-manager-provider");
            for (int i = 0; i < nodes.getLength(); i++) {
                Node n = nodes.item(i);
                if (n.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                entityManagerProvider = nodes.item(i).getTextContent();
            }

            nodes = root.getElementsByTagName("persistence-properties");
            for (int i = 0; i < nodes.getLength(); i++) {
                Node n = nodes.item(i);
                if(n.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                parsePersistenceProperties(nodes.item(i));
            }
        } 
        catch (ParserConfigurationException | SAXException | IOException | DOMException e){
            throw new RuntimeException(e);
        } 
        finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } 
            catch(IOException ioe) {
                log.error("Can't close stream", ioe);
            }
        }
    }

    private void parsePersistenceProperties(Node persistencePropertiesNode) {
        NodeList nodes = persistencePropertiesNode.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (!node.getNodeName().equalsIgnoreCase("persistence-property")) {
                continue;
            }
            Map.Entry<String,String> entry = parsePersistenceProperty(node);
            persistenceProperties.put(entry.getKey(), entry.getValue());
        }
    }

    private Map.Entry<String,String> parsePersistenceProperty(Node persistencePropertyNode) {
        Node n = persistencePropertyNode.getFirstChild().getNextSibling();
        String s = n.getFirstChild().getTextContent();
        String v = n.getNextSibling().getNextSibling().getFirstChild().getTextContent();
        return new AbstractMap.SimpleEntry<>(s, v);
    }

    public String getCustomLoginUrl() {
        return customLoginUrl;
    }
}
