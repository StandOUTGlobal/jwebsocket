//	---------------------------------------------------------------------------
//	jWebSocket - Copyright (c) 2010 jwebsocket.org
//	---------------------------------------------------------------------------
//	This program is free software; you can redistribute it and/or modify it
//	under the terms of the GNU General Public License as published by the
//	Free Software Foundation; either version 3 of the License, or (at your
//	option) any later version.
//	This program is distributed in the hope that it will be useful, but WITHOUT
//	ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//	FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
//	more details.
//	You should have received a copy of the GNU General Public License along
//	with this program; if not, see <http://www.gnu.org/licenses/>.
//	---------------------------------------------------------------------------
package org.jwebsocket.server.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jwebsocket.api.WebSocketEngine;
import org.jwebsocket.api.WebSocketFilter;
import org.jwebsocket.api.WebSocketInitializer;
import org.jwebsocket.api.WebSocketPlugIn;
import org.jwebsocket.api.WebSocketServer;
import org.jwebsocket.config.JWebSocketConstants;
import org.jwebsocket.filters.custom.CustomTokenFilter;
import org.jwebsocket.filters.system.SystemFilter;
import org.jwebsocket.plugins.flashbridge.FlashBridgePlugIn;
import org.jwebsocket.plugins.rpc.RPCPlugIn;
import org.jwebsocket.plugins.streaming.StreamingPlugIn;
import org.jwebsocket.plugins.system.SystemPlugIn;
import org.jwebsocket.server.TokenServer;
import org.jwebsocket.tcp.engines.TCPEngine;

/**
 * Abstract initializer class
 * @author puran
 * @version $Id: AbstractJWebSocketInitializer.java 437 2010-05-03 22:10:20Z mailtopuran $
 */
public abstract class AbstractJWebSocketInitializer implements WebSocketInitializer {
	/**
	 * Initialize the engine to be started based on configuration.
	 * 
	 * @return the initialized engine ready to start
	 */
	@Override
	public WebSocketEngine intializeEngine() {
		WebSocketEngine newEngine = null;
		try {
			newEngine = new TCPEngine("tcp0", JWebSocketConstants.DEFAULT_PORT, JWebSocketConstants.DEFAULT_TIMEOUT);
		} catch (Exception e) {
			System.out.println("Error instantating engine: " + e.getMessage());
			System.exit(0);
		}
		return newEngine;
	}

	/**
	 * Initializes all the servers configured via jWebSocket configuration
	 * @return the list of initialized servers
	 */
	@Override
	public List<WebSocketServer> initializeServers() {
		List<WebSocketServer> servers = new ArrayList<WebSocketServer>();
		// instantiate the Token server by default
		TokenServer tokenServer = new TokenServer("ts0");
		servers.add(tokenServer);
		
		List<WebSocketServer> customServers = initializeCustomServers();
		if (customServers != null) {
			servers.addAll(customServers);
		}
		return servers;
	}
	
	 /**
	  * intialize the plugins as per the serverss
	  * @return the map of server id to list of plugins
	  */
	@Override
	public Map<String, List<WebSocketPlugIn>> initializePlugins() {
		
		Map<String, List<WebSocketPlugIn>> pluginMap = new HashMap<String, List<WebSocketPlugIn>>();
		
		List<WebSocketPlugIn> defaultPlugins = new ArrayList<WebSocketPlugIn>();
		defaultPlugins.add(new SystemPlugIn());
		defaultPlugins.add(new RPCPlugIn());
		defaultPlugins.add(new StreamingPlugIn());
		defaultPlugins.add(new FlashBridgePlugIn());
		
		pluginMap.put("ts0", defaultPlugins);
		
		Map<String, List<WebSocketPlugIn>> customPluginMap = initializeCustomPlugins();
		
		for (Map.Entry<String, List<WebSocketPlugIn>> entry : customPluginMap.entrySet()) {
			String id = entry.getKey();
			pluginMap.get(id).addAll(entry.getValue());
		}
		return pluginMap;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, List<WebSocketFilter>> initializeFilters() {
		Map<String, List<WebSocketFilter>> filterMap = new HashMap<String, List<WebSocketFilter>>();
		
		List<WebSocketFilter> defaultFilters = new ArrayList<WebSocketFilter>();
		defaultFilters.add(new SystemFilter("systemFilter"));
		defaultFilters.add(new CustomTokenFilter("userFilter"));
		
		filterMap.put("ts0", defaultFilters);
		
		Map<String, List<WebSocketFilter>> customFilterMap = initializeCustomFilters();
		
		for (Map.Entry<String, List<WebSocketFilter>> entry : customFilterMap.entrySet()) {
			String id = entry.getKey();
			filterMap.get(id).addAll(entry.getValue());
		}
		return filterMap;
	}
	
    /**
     * Allow subclass of this class to initialize custom plugins.
     * @return the map of custom plugins to server id.
     */
	public abstract Map<String, List<WebSocketPlugIn>> initializeCustomPlugins();

	/**
	 * Allow the subclass of this class to initialize custom servers
	 * @return the list of custom servers
	 */
	public abstract List<WebSocketServer> initializeCustomServers();
	
	/**
	 * Allow the subclass of this class to initialize custom filters
	 * @return the list of custom filters to server id
	 */
	public abstract Map<String, List<WebSocketFilter>> initializeCustomFilters();

}