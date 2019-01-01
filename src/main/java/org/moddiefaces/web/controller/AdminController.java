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

package org.moddiefaces.web.controller;

import org.moddiefaces.core.GroupPermissions;
import org.moddiefaces.core.ContentManager;
import org.moddiefaces.core.Content;
import org.moddiefaces.core.Style;
import org.moddiefaces.core.BaseContent;
import org.moddiefaces.core.Namespace;
import org.moddiefaces.config.ModdieContext;
import org.moddiefaces.persistence.JpaEntityManagerProducer;
import org.primefaces.component.datatable.DataTable;
//import org.primefaces.component.editor.Editor;
import org.primefaces.context.RequestContext;
import org.primefaces.event.DragDropEvent;
import org.primefaces.model.TreeNode;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.Serializable;
import java.util.*;
import javax.annotation.PostConstruct;
import lombok.Getter;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;

/**
 * @since 0.1
 * @author Dmytro Maidaniuk
 */
@Named
@ViewScoped
public class AdminController implements Serializable {
    
    @Inject
    private TheTree theTree;
    
    @Inject
    private ContentHolder contentHolder;
    
    @Inject
    private ContentManager contentManager;
    
    @Getter
    PageContent pageContent;

    /** true if we need to configure the embedded database */
    private boolean embeddedDbNeedsConfig = ModdieContext.getInstance().isEmbeddedDbNeedsConfig();
    /** the path to the embedded db directory */
    private String derbyPath;
    @Deprecated
    private String contentCss;
    private String incomingNamespace;
    private String incomingContentName;
    private String selectedGroup;
    private String newNamespace;
    private String newContentName;
    private String newStyleName;
    private DataTable groupsDataTable;
    private DataTable dialogGroupsDataTable;
    private DataTable contentDialogGroupsDataTable;
    private DataTable styleDialogGroupsDataTable;
//    private Editor editor;
    private UIComponent stylePanel;
    private UIComponent theTreeComponent;
    private UIComponent dropper;
    
    @PostConstruct
    private void init() {
        pageContent = new PageContent();
    }

    public void loadNamespace() {
        if(isAjaxRequest())
            return;
        loadPage("namespace");
        fetchNamespaceContents();
    }

    public void loadContent() {
        if(isAjaxRequest())
            return;
        loadPage("content");
        makeContentCss();
    }

    public void loadStyle() {
        if(isAjaxRequest())
            return;
        loadPage("style");
    }

    public void addNewTopLevelNamespace() {
        pageContent.setNamespaceToAdd(new Namespace());
        pageContent.getNamespaceToAdd().setGroupPermissionsList(newDefaultGroupPermissions());
        pageContent.setAddingNamespace(true);
        pageContent.setAddingTopLevelNamespace(true);
    }

    public void addNewNamespace() {
        Namespace parent = pageContent.getNamespace();
        pageContent.setNamespaceToAdd(new Namespace(parent, ""));
        pageContent.getNamespaceToAdd().setGroupPermissionsList(newDefaultGroupPermissions());
        pageContent.setAddingNamespace(true);
    }

    public void addNewContent() {
        pageContent.setAddingContent(true);
        pageContent.setContentToAdd(new Content());
        pageContent.getContentToAdd().setGroupPermissionsList(newDefaultGroupPermissions());
    }

    public void addNewStyle() {
        pageContent.setAddingStyle(true);
        pageContent.setStyleToAdd(new Style());
        pageContent.getStyleToAdd().setGroupPermissionsList(newDefaultGroupPermissions());
    }

    public void addGroup() {
        BaseContent content = pageContent.getBaseContent();
        content.getGroupPermissionsList().add(new GroupPermissions(selectedGroup, true, false, false, false));
        saveBaseContent();
    }

    public void addGroupToNewContent() {
        pageContent.addGroupToNewContent(new GroupPermissions(selectedGroup, true, false, false, false));
    }

    public void addContent() {
        Content content = pageContent.getContentToAdd();
        content.setName(newContentName);
        content.setNamespace(pageContent.getNamespace());
        content.setDateCreated(new Date());
        content.setDateModified(content.getDateCreated());
        contentHolder.add(content);
        contentManager.saveContent(content);
        pageContent.setAddingContent(false);
        pageContent.getNamespaceContents().add(content);
        theTree.createTreeModel();
        Messages.add(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Content " + content.getFullName() + " added.", ""));
    }

    public void addStyle() {
        Style style = pageContent.getStyleToAdd();
        style.setName(newStyleName);
        style.setNamespace(pageContent.getNamespace());
        contentHolder.add(style);
        contentManager.saveStyle(style);
        pageContent.setAddingStyle(false);
        pageContent.getNamespaceContents().add(style);
        theTree.createTreeModel();
        Messages.add(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Style " + style.getFullName() + " added.", ""));
    }

    public void removeGroup(ActionEvent e) {
        String groupName = (String) e.getComponent().getAttributes().get("group");
        BaseContent content = pageContent.getBaseContent();

        removeFromGroupPermissions(groupName, content);
        saveBaseContent();

        addGroupUpdateTarget(groupsDataTable.getClientId(Faces.getContext()));
    }

    public void removeGroupForNewContent(ActionEvent e) {
        String groupName = (String) e.getComponent().getAttributes().get("group");
        BaseContent content = pageContent.getBaseContentToAdd();

        removeFromGroupPermissions(groupName, content);
        saveBaseContent();

        addGroupUpdateTarget(dialogGroupsDataTable.getClientId(Faces.getContext()));
    }

    public void permissionChanged() {
        saveBaseContent();
    }

    public void saveNewNamespace() {
        pageContent.getNamespaceToAdd().setNodeName(newNamespace);
        contentManager.saveNamespace(pageContent.getNamespaceToAdd());
        pageContent.getNamespaceContents().add(pageContent.getNamespaceToAdd());
        pageContent.setAddingNamespace(false);
        pageContent.setAddingTopLevelNamespace(false);
        newNamespace = null;
        theTree.createTreeModel();
    }

    public void saveContent() {
        Content content = pageContent.getContent();
        contentManager.saveContent(content);
        Messages.add(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Content " + content.getName() + " saved successfully.", ""));
    }

    public void saveStyle() {
        Style style = pageContent.getStyle();
        contentManager.saveStyle(style);
        Messages.add(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Style " + style.getName() + " saved successfully.", ""));
    }

    public void styleDrop(DragDropEvent event) {
        Map<String,String> paramMap = Faces.getRequestParameterMap();
        FacesContext facesContext = Faces.getContext();
        String namespace = paramMap.get("styleNamespace");
        String styleName = paramMap.get("styleName");
        Style style = contentManager.loadStyle(new Namespace(namespace), styleName);
        Content content = pageContent.getContent();

        if (!content.getStyles().contains(style)) {
            content.getStyles().add(style);
            contentManager.saveContent(content);

            List<String> componentIds = new ArrayList<>();
            componentIds.add(event.getDropId());
            componentIds.add(stylePanel.getClientId(facesContext));
//            componentIds.add(editor.getClientId(facesContext));
            componentIds.add(dropper.getClientId(facesContext));
            componentIds.add(theTreeComponent.getClientId(facesContext));

            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.update(componentIds);
        }
        makeContentCss();
    }

    public void removeStyle(ActionEvent event) {
        UIComponent component = (UIComponent) event.getSource();
        String namespace = (String) component.getAttributes().get("namespace");
        String name = (String) component.getAttributes().get("styleName");
        Style style = (Style) contentHolder.find(new ContentKey(name, namespace, "style")).getData();
        Content content = pageContent.getContent();

        if (content.getStyles().contains(style)) {
            content.getStyles().remove(style);
            contentManager.saveContent(content);
        }

        List<String> componentIds = new ArrayList<>();
        componentIds.add(stylePanel.getClientId(Faces.getContext()));
//        componentIds.add(editor.getClientId(Faces.getContext()));
        componentIds.add(dropper.getClientId(Faces.getContext()));
        componentIds.add(theTreeComponent.getClientId(Faces.getContext()));

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.update(componentIds);

        makeContentCss();
    }

    public void removeBaseContent() {
        BaseContent content = pageContent.getContentToRemove();

        if (content instanceof Namespace) {
            removeNamespace((Namespace) content);
        }
        else if (content instanceof Content) {
            contentManager.deleteContent((Content) content);
        }
        else if (content instanceof Style) {
            contentManager.deleteStyle((Style) content);
        }
        pageContent.getNamespaceContents().remove(content);
        theTree.createTreeModel();
    }

    /**
     * Action listener that creates the embedded database from the initial screen.
     */
    public void createEmbeddedDb() {
        File file = new File(derbyPath);
        String jdbc = "jdbc:derby:";
        String props = ";create=true";
        Properties properties = new Properties();

        properties.put("javax.persistence.jdbc.driver", "org.apache.derby.jdbc.EmbeddedDriver");
        properties.put("javax.persistence.jdbc.url", jdbc + derbyPath + props);

        if (!file.getParentFile().exists()) {
            Messages.add(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                "Invalid path: " + derbyPath + ".  Directory " + file.getParentFile()
                                                .getAbsolutePath()
                                                + " does not exist.", null));
            derbyPath = null;
            throw new FacesException();
        }

        ((JpaEntityManagerProducer)ModdieContext.getInstance().getEntityManagerProducer()).createEmbeddedDb(properties);

        embeddedDbNeedsConfig = false;

        theTree.createTreeModel();
    }

    public String getIncomingNamespace() {
        return incomingNamespace;
    }

    public void setIncomingNamespace(String incomingNamespace) {
        this.incomingNamespace = incomingNamespace;
    }

    public String getIncomingContentName() {
        return incomingContentName;
    }

    public void setIncomingContentName(String incomingContentName) {
        this.incomingContentName = incomingContentName;
    }

    public String getContentCss() {
        return contentCss;
    }

    public void setContentCss(String contentCss) {
        this.contentCss = contentCss;
    }

    public String getSelectedGroup() {
        return selectedGroup;
    }

    public void setSelectedGroup(String selectedGroup) {
        this.selectedGroup = selectedGroup;
    }

    public DataTable getGroupsDataTable() {
        return groupsDataTable;
    }

    public void setGroupsDataTable(DataTable groupsDataTable) {
        this.groupsDataTable = groupsDataTable;
    }

    public String getNewNamespace() {
        return newNamespace;
    }

    public void setNewNamespace(String newNamespace) {
        this.newNamespace = newNamespace;
    }

    public DataTable getDialogGroupsDataTable() {
        return dialogGroupsDataTable;
    }

    public void setDialogGroupsDataTable(DataTable dialogGroupsDataTable) {
        this.dialogGroupsDataTable = dialogGroupsDataTable;
    }

    public String getNewContentName() {
        return newContentName;
    }

    public void setNewContentName(String newContentName) {
        this.newContentName = newContentName;
    }

    public DataTable getContentDialogGroupsDataTable() {
        return contentDialogGroupsDataTable;
    }

    public void setContentDialogGroupsDataTable(DataTable contentDialogGroupsDataTable) {
        this.contentDialogGroupsDataTable = contentDialogGroupsDataTable;
    }

    public String getNewStyleName() {
        return newStyleName;
    }

    public void setNewStyleName(String newStyleName) {
        this.newStyleName = newStyleName;
    }

//    public Editor getEditor() {
//        return editor;
//    }
//
//    public void setEditor(Editor editor) {
//        this.editor = editor;
//    }

    public UIComponent getStylePanel() {
        return stylePanel;
    }

    public void setStylePanel(UIComponent stylePanel) {
        this.stylePanel = stylePanel;
    }

    public UIComponent getTheTreeComponent() {
        return theTreeComponent;
    }

    public void setTheTreeComponent(UIComponent theTreeComponent) {
        this.theTreeComponent = theTreeComponent;
    }

    public UIComponent getDropper() {
        return dropper;
    }

    public void setDropper(UIComponent dropper) {
        this.dropper = dropper;
    }

    public DataTable getStyleDialogGroupsDataTable() {
        return styleDialogGroupsDataTable;
    }

    public void setStyleDialogGroupsDataTable(DataTable styleDialogGroupsDataTable) {
        this.styleDialogGroupsDataTable = styleDialogGroupsDataTable;
    }

    public boolean isEmbeddedDbNeedsConfig() {
        return embeddedDbNeedsConfig;
    }

    public void setEmbeddedDbNeedsConfig(boolean embeddedDbNeedsConfig) {
        this.embeddedDbNeedsConfig = embeddedDbNeedsConfig;
    }

    public String getDerbyPath() {
        return derbyPath;
    }

    public void setDerbyPath(String derbyPath) {
        this.derbyPath = derbyPath;
    }

    private boolean isAjaxRequest() {
        return Faces.isAjaxRequest();
    }

    private void saveBaseContent() {
        BaseContent content = pageContent.getBaseContent();

        saveBaseContent(content);
    }

    private void saveBaseContent(BaseContent content) {
        if(content instanceof Content)
            contentManager.saveContent((Content) content);
        else if(content instanceof Style)
            contentManager.saveStyle((Style) content);
        else if(content instanceof Namespace)
            contentManager.saveNamespace((Namespace) content);
    }

    private void fetchNamespaceContents() {
        Namespace namespace = pageContent.getNamespace();
        pageContent.getNamespaceContents().clear();

        addNamespaceContents(namespace);

        if(isEmptyRootNamespace(namespace))
            pageContent.getNamespaceContents().add(namespace);
    }

    private void addNamespaceContents(Namespace namespace) {
        List<BaseContent> namespaceContents = pageContent.getNamespaceContents();
        List<Content> contentList = contentManager.loadContent(namespace);
        namespaceContents.addAll(contentList);
        namespaceContents.addAll(contentManager.loadStyle(namespace));
        namespaceContents.addAll(contentManager.loadChildNamespaces(namespace));
    }

    private boolean isEmptyRootNamespace(Namespace namespace) {
        return contentManager.loadChildNamespaces(namespace).isEmpty() && namespace.getParent() == null;
    }

    private void makeContentCss() {
        contentCss = "";

        if(contentHasStyles()) {
            for(Style style : pageContent.getContent().getStyles())
                contentCss =  contentCss + style.getStyle();
        }

        contentCss = contentCss.replace('\r', ' ');
        contentCss = contentCss.replace('\n', ' ');
    }

    private boolean contentHasStyles() {
        return pageContent.getContent() != null && pageContent.getContent().getStyles() != null;
    }

    static List<GroupPermissions> newDefaultGroupPermissions() {
        List<GroupPermissions> defaultGroupPermissionsList = new ArrayList<>();
        String group = ModdieContext.getInstance().getCurrentUser();
        GroupPermissions groupPermissions = new GroupPermissions(group, true, true, true, true);
        defaultGroupPermissionsList.add(groupPermissions);
        return defaultGroupPermissionsList;
    }

    private void loadPage(String type) {
        TreeNode selectedNode = theTree.getSelectedNode();
        TreeNode newContent =  "namespace".equals(type) ?
                contentHolder.find(new ContentKey(null, incomingNamespace, "namespace")) :
                contentHolder.find(new ContentKey(incomingContentName, incomingNamespace, type));

        if(newContent == null) {
            showLoadError(type);
            return;
        }

        newContent.setSelected(true);

        if(selectedNode != null)
            selectedNode.setSelected(false);

        theTree.setSelectedNode(newContent);
        pageContent.setTheContent((BaseContent) newContent.getData());
        theTree.createTreeModel();
    }

    private void showLoadError(String type) {
        if(null != type)
            switch (type) {
            case "namespace":
                Messages.add(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Namespace [" + incomingNamespace + "] not found.", null));
                break;
            case "content":
                Messages.add(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Content for namespace [" + incomingNamespace + "] and " + type + " name [" +
                                incomingContentName + "] not found.", null));
                break;
            default:
                Messages.add(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Style for namespace [" + incomingNamespace + "] and " + type + " name [" +
                                incomingContentName + "] not found.", null));
                break;
        }
    }

    private void removeFromGroupPermissions(String groupName, BaseContent content) {
        for(Iterator<GroupPermissions> it = content.getGroupPermissionsList().iterator(); it.hasNext(); ) {
            GroupPermissions groupPermissions = it.next();
            if(groupPermissions.getGroup().equals(groupName)) {
                it.remove();
                break;
            }
        }
    }

    private void addGroupUpdateTarget(String clientId) {
        RequestContext requestContext = RequestContext.getCurrentInstance();
        if(clientId.matches(".*[0-9]+$"))
            clientId = clientId.replaceAll(":[0-9]+$", "");
        requestContext.update(clientId);
    }

    private void removeNamespace(Namespace theNamespace) {
        Namespace namespace = theNamespace.getNamespace();
        if(contentManager.loadChildNamespaces(namespace).isEmpty()
                && contentManager.loadStyle(namespace).isEmpty()
                && contentManager.loadContent(namespace).isEmpty())
        {
            contentManager.deleteNamespace(namespace);
        } else {
            throw new ValidatorException(
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "You can only delete an empty namespace", null));
        }
    }
}
