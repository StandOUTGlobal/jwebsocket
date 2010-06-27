//	---------------------------------------------------------------------------
//	jWebSocket - Copyright (c) 2010 jwebsocket.org
//	---------------------------------------------------------------------------
//	This program is free software; you can redistribute it and/or modify it
//	under the terms of the GNU Lesser General Public License as published by the
//	Free Software Foundation; either version 3 of the License, or (at your
//	option) any later version.
//	This program is distributed in the hope that it will be useful, but WITHOUT
//	ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//	FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//	more details.
//	You should have received a copy of the GNU Lesser General Public License along
//	with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//	---------------------------------------------------------------------------
package org.jwebsocket.factory;

import static org.jwebsocket.config.JWebSocketConstants.JWEBSOCKET_HOME;
import static org.jwebsocket.config.JWebSocketConstants.CATALINA_HOME;
import static org.jwebsocket.config.JWebSocketConstants.JWEBSOCKET_XML;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;
import org.jwebsocket.api.WebSocketInitializer;
import org.jwebsocket.config.JWebSocketConfig;
import org.jwebsocket.config.xml.JWebSocketConfigHandler;
import org.jwebsocket.kit.WebSocketException;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.security.SecurityFactory;

/**
 * An object that does the process of loading configuration, intialization of
 * the jWebSocket server system.
 * 
 * @author puran
 * @version $Id: JWebSocketLoader.java 345 2010-04-10 20:03:48Z fivefeetfurther$
 */
public final class JWebSocketLoader {

    private static Logger log = Logging.getLogger(JWebSocketLoader.class);
    private JWebSocketConfigHandler configHandler = new JWebSocketConfigHandler();

    /**
     * Initialize the jWebSocket Server system
     * 
     * @return the initializer object
     * @throws WebSocketException
     *             if there's an exception while initialization
     */
    public final WebSocketInitializer initialize() throws WebSocketException {
        String configPath = getConfigurationPath();
        if (configPath == null) {
            throw new WebSocketException("Could not load the configuration file jWebSocket.xml. Make sure that " + "JWEBSOCKET_HOME variable is set " +
            		"and jWebSocket.xml file exists at JWEBSOCKET_HOME/conf");
        }
        // load the entire settings from the configuration xml file
        JWebSocketConfig config = loadConfiguration(configPath);

        // initialize security by using config settings
        SecurityFactory.initFromConfig(config);

        WebSocketInitializer initializer = getInitializer(config);
        if (initializer == null) {
            initializer = JWebSocketXmlConfigInitializer.getInitializer(config);
        }
        return initializer;
    }

    /**
     * Returns the appropriate {@code WebSocketInitializer} implementation
     * 
     * @param config
     *            the config object
     * @return the {@code WebSocketInitializer} object
     */
    private WebSocketInitializer getInitializer(JWebSocketConfig config) {
        WebSocketInitializer initializer = null;
        // if we are in development mode load the initializer class
        if ("dev".equals(config.getInstallation())) {
            initializer = instantiateInitializer(config.getInitializer());
            // if we are in production mode use the
            // JWebSocketXmlConfigInitializer class
        } else if ("prod".equals(config.getInstallation())) {
            initializer = JWebSocketXmlConfigInitializer.getInitializer(config);
        } else {
            // ignore
        }
        return initializer;
    }

    /**
     * Instantiate the initializer custom initializer class if there's any
     * configured via xml configuration, otherwise return the default {@code
     * JWebSocketInitializer} class that initialize all the default engine,
     * plugins and servers.
     * 
     * @param initializerClass the class name to instantiate
     * @return the instantiated initializer object
     */
    @SuppressWarnings("unchecked")
    private WebSocketInitializer instantiateInitializer(String initializerClass) {
        WebSocketInitializer initializer = null;
        try {
            Class<WebSocketInitializer> lClass = (Class<WebSocketInitializer>) Class.forName(initializerClass);
            initializer = lClass.newInstance();
        } catch (ClassNotFoundException ex) {
            if (log.isInfoEnabled()) {
                log.info(ex.getClass().getSimpleName() + " instantiating initializer", ex);
            }
        } catch (InstantiationException ex) {
            if (log.isInfoEnabled()) {
                log.info(ex.getClass().getSimpleName() + " instantiating initializer", ex);
            }
        } catch (IllegalAccessException ex) {
            if (log.isInfoEnabled()) {
                log.info(ex.getClass().getSimpleName() + " instantiating initializer", ex);
            }
        }
        if (log.isInfoEnabled()) {
            log.info("Initializer found: " + initializer.getClass().getName());
        }

        return initializer;
    }

    /**
     * Load all the configurations based on jWebSocket.xml file at the given
     * <tt>configFilePath</tt> location.
     * 
     * @param configFilePath
     *            the path to jWebSocket.xml file
     * @return the web socket config object with all the configuration
     * @throws WebSocketException
     *             if there's any while loading configuration
     */
    private JWebSocketConfig loadConfiguration(final String configFilePath) throws WebSocketException {
        JWebSocketConfig config = null;
        File lFile = new File(configFilePath);
        String lMsg;
        try {
            FileInputStream fis = new FileInputStream(lFile);
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader streamReader = null;
            streamReader = factory.createXMLStreamReader(fis);
            config = configHandler.processConfig(streamReader);
        } catch (XMLStreamException ex) {
            lMsg = ex.getClass().getSimpleName() + " occurred while creating XML stream (" + configFilePath + ").";
            throw new WebSocketException(lMsg);
        } catch (FileNotFoundException ex) {
            lMsg = "jWebSocket config file not found while creating XML stream (" + configFilePath + ").";
            throw new WebSocketException(lMsg);
        }
        return config;
    }

    /**
     * private method that checks the path of the jWebSocket.xml file
     * 
     * @return the path to jWebSocket.xml
     */
    private String getConfigurationPath() {
        String lWebSocketXML = null;
        String lWebSocketHome = null;
        String lFileSep = System.getProperty("file.separator");
        File lFile;
        // try to obtain JWEBSOCKET_HOME environment variable
        lWebSocketHome = System.getenv(JWEBSOCKET_HOME);
        if (lWebSocketHome != null) {
            // append trailing slash if needed
            if (!lWebSocketHome.endsWith(lFileSep)) {
                lWebSocketHome += lFileSep;
            }
            // jWebSocket.xml can be located in %JWEBSOCKET_HOME%/conf
            lWebSocketXML = lWebSocketHome + "conf" + lFileSep + JWEBSOCKET_XML;
            lFile = new File(lWebSocketXML);
            if (lFile.exists()) {
                return lWebSocketXML;
            }
        }

        // try to obtain CATALINA_HOME environment variable
        lWebSocketHome = System.getenv(CATALINA_HOME);
        if (lWebSocketHome != null) {
            // append trailing slash if needed
            if (!lWebSocketHome.endsWith(lFileSep)) {
                lWebSocketHome += lFileSep;
            }
            // jWebSocket.xml can be located in %CATALINA_HOME%/conf
            lWebSocketXML = lWebSocketHome + "conf" + lFileSep + JWEBSOCKET_XML;
            lFile = new File(lWebSocketXML);
            if (lFile.exists()) {
                return lWebSocketXML;
            }
        }
        return null;
    }
}
