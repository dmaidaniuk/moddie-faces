# Introduction

**Moddie Faces** is an open-source tag library and administration interface for content management with Java Server Faces applications. 

# Features

* a single JAR and configuration file to deploy;
* tag library to put content into your jsf pages;
* built in interface to administer your content automatically deployed under `your-webroot/cmf/admin/index.jsf`;
* embedded Derby database for quick startup;
* connect to JDBC data source of your choice by specifying JPA style configuration options.

# Status

The project is currently in alpha stage. The software is fairly stable, but untested on various application servers. Currently it is working with Glassfish 3.0.1 and Tomcat 7. 

# Details

The following steps should get you up and running. Once you're logged into the admin panel you can continue to use the in memory derby database or specify a place on the file system to persist the data.

* You should use servlet 3.0 container and `web.xml` version 3.0.
* Copy `cmf.jar` to your project's `WEB-INF/lib` directory.
* If you're running `Tomcat 7`, copy `derby-10.5.3.0_1.jar`, `eclipselink.jar`, and `javax.persistence.jar` to your `WEB-INF/lib` directory as well. You'll also need to deploy the JSF implementation libraries (available here: http://javaserverfaces.java.net/download.html).
* Copy `cmf-config.xml` to your project's WEB-INF directory.
* Examples of using PostgreSQL and MySQL datasources are included in the `cmf-config.xml` file. If you wish to use one of these databases, database generation scripts are located in the scripts directory. (You'll need to supply your own jdbc jar files, i.e. `mysql-connector.jar`, in the `WEB-INF/lib` directory).
* If you're using an IDE, make sure it includes `cmf.jar` in its classpath so that it sees the tag library.
* Include the taglib in your html declaration:
```
          <html xmlns="http://www.w3.org/1999/xhtml"
                xmlns:h="http://java.sun.com/jsf/html"
                xmlns:f="http://java.sun.com/jsf/core"
                xmlns:ui="http://java.sun.com/jsf/facelets"
                xmlns:cmf="http://tralfamadore.net/cmf">
```
* You can now use the content tag in your pages like this:
```
          <cmf:content namespace="com.somesite" name="contentName"/>
```
* You can access the administration panel at:
```
          http://yoursite.com/yourwebroot/cmf/admin/index.jsf
```
* The administration panel will prompt you to specify a place on the file system to persist the content data. If you wish to use the in memory database you can ignore this, but your data will not be persisted between deployments or restarts. 

**Note:** Currently, the package has only been verified with `Glassfish v3.0.1` and `Tomcat 7`. There is no `JBoss 6` support as of now.

Inspired by **Bill Reh's** Content Management Faces library (http://content-management-faces.blogspot.com/)
