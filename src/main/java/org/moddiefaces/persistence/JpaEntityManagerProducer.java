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

package org.moddiefaces.persistence;

import org.moddiefaces.core.JpaContentManager;
import org.moddiefaces.config.ModdieContext;
import org.moddiefaces.config.ConfigFile;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import lombok.extern.slf4j.Slf4j;

/**
 * @since 0.1
 * @author Dmytro Maidaniuk
 */
@Slf4j
public class JpaEntityManagerProducer implements EntityManagerProducer {
    
    private EntityManager em;
    
    private EntityManagerFactory emFactory;

    @Override
    public void shutdown() {
        if (em != null) {
            em.close();
        }
        if (emFactory != null) {
            emFactory.close();
        }
    }

    @Override
    @Produces
    @RequestScoped
    public EntityManager getEntityManager() {
        if (em == null) {
            ConfigFile configFile = ModdieContext.getInstance().getConfigFile();

            Map<String,String> properties = new HashMap<>();

            for (Map.Entry<String,String> entry : configFile.getPersistenceProperties().entrySet()) {
                properties.put(entry.getKey(), entry.getValue());
            }
            properties.put("hibernate.archive.autodetection", "class");

            emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
            em = emFactory.createEntityManager();
            boolean embedded =
                    properties.get("javax.persistence.jdbc.driver").equals("org.apache.derby.jdbc.EmbeddedDriver");
            boolean mem = properties.get("javax.persistence.jdbc.url").matches("^jdbc:derby:memory:.*");
            if (embedded && mem) {
                ModdieContext.getInstance().setEmbeddedDbNeedsConfig(true);
                // drop and recreate the tables
                try {
                    dropEmbeddedTables();
                } 
                catch(Exception ignore) {
                    log.warn("Error during dropping tables: " + ignore.getMessage());
                }
                createEmbeddedTables();
                ModdieContext.getInstance().setInMemory(true);
            }
        }

        log.info("Call to entity manager " + em);
        return em;
    }
    
    protected void closeEntityManager(@Disposes EntityManager entityManager) {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }

    private void createEmbeddedTables() {
        em.getTransaction().begin();
        try {
            String[] tableSql = getDerbyCreate();
            for(String query : tableSql) {
                if(query.matches("^\\s*$"))
                    continue;
                em.createNativeQuery(query).executeUpdate();
            }
            em.getTransaction().commit();
        } 
        catch(Exception e) {
            // most likely the tables are already there, probably the cmf-config.xml was overwritten
            em.getTransaction().rollback();
        }
    }

    private void dropEmbeddedTables() {
        try {
            em.getTransaction().begin();
            for (String query : dropDerbyTables) {
                em.createNativeQuery(query).executeUpdate();
            }
            em.getTransaction().commit();
        }
        catch (Exception ignore) {
            // most likely the tables are already there, probably the moddie-config.xml was overwritten
            em.getTransaction().rollback();
        }
    }

    public void createEmbeddedDb(Properties properties) {
        shutdown();

        emFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
        em = emFactory.createEntityManager();

        createEmbeddedTables();

        ((JpaContentManager)ModdieContext.getInstance().getContentManager()).setEm(em);
        try {
            editWebXml(properties);
        } catch (Exception e) {
            log.error(PERSISTENCE_UNIT_NAME, e);
        }
        ModdieContext.getInstance().setInMemory(false);
    }

    private void editWebXml(Properties properties) throws Exception {
        String url = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/moddie-config.xml");
        File file = new File(url);
        String contents = readFileAsString(file);
        String newUrl = properties.getProperty("javax.persistence.jdbc.url");
        contents = contents.replaceAll("jdbc:derby:memory:cmf;create=true", newUrl);
        writeStringToFile(file, contents);
    }

    private String[] getDerbyCreate() throws Exception {
        String url = "/Users/billreh/IdeaProjects/content-management-faces/scripts/cmf-derby.sql";
        File file = new File(url);
        return readFileAsString(file).split(";");
    }

    static void writeStringToFile(File file, String string) throws java.io.IOException {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(string.getBytes());
        }
        finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
            catch (Exception ignore) {
            }
        }

    }

    static String readFileAsString(File file) throws java.io.IOException{
        byte[] buffer = new byte[(int) file.length()];
        FileInputStream f = null;
        try {
            f = new FileInputStream(file);
            //noinspection ResultOfMethodCallIgnored
            f.read(buffer);
        }
        finally {
            if (f != null) {
                try {
                    f.close();
                }
                catch (IOException ignore) {
                }
            }
        }
        return new String(buffer);
    }

    String dropDerbyTables[] = {
            "drop table group_permissions_to_content\n",
            "drop table group_permissions_to_namespace\n",
            "drop table group_permissions_to_style\n",
            "drop table group_permissions\n",
            "drop table user_to_group\n",
            "drop table groups\n",
            "drop table users\n",
            "drop table style_to_content\n",
            "drop table style\n",
            "drop table content\n",
            "drop table namespace\n",
            "drop table id_gen\n"
    };

    String createDerbyTables[] = {
            "CREATE TABLE namespace (\n" +
                    "    id bigint NOT NULL,\n" +
                    "    name varchar(32000) NOT NULL,\n" +
                    "    parent_id bigint,\n" +
                    "\t primary key(id)\n" +
                    ")\n",
            "CREATE TABLE content (\n" +
                    "    id bigint NOT NULL,\n" +
                    "    namespace_id bigint NOT NULL,\n" +
                    "    name character varying(256) NOT NULL,\n" +
                    "    content varchar(32000),\n" +
                    "    date_created timestamp,\n" +
                    "    date_modified timestamp,\n" +
                    "\t primary key(id),\n" +
                    "\t foreign key (namespace_id) references namespace(id)\n" +
                    ")\n",
            "CREATE TABLE style (\n" +
                    "    id bigint NOT NULL,\n" +
                    "    namespace_id bigint NOT NULL,\n" +
                    "    name character varying(256) NOT NULL,\n" +
                    "    style varchar(32000),\n" +
                    "\t primary key(id),\n" +
                    "\t foreign key (namespace_id) references namespace(id)\n" +
                    ")\n",
            "CREATE TABLE style_to_content (\n" +
                    "    id bigint NOT NULL,\n" +
                    "    content_id bigint NOT NULL,\n" +
                    "    style_id bigint NOT NULL,\n" +
                    "\t primary key(id),\n" +
                    "\t foreign key (content_id) references content(id),\n" +
                    "\t foreign key (style_id) references style(id)\n" +
                    ")\n",
            "CREATE TABLE id_gen (\n" +
                    "    gen_name character varying(80) NOT NULL,\n" +
                    "    gen_val integer\n" +
                    ")\n",
            "INSERT INTO id_gen VALUES('style_id', 100)\n",
            "INSERT INTO id_gen VALUES('content_id', 100)\n",
            "INSERT INTO id_gen VALUES('namespace_id', 100)\n",
            "INSERT INTO id_gen VALUES('style_to_content_id', 100)"
    };
}
