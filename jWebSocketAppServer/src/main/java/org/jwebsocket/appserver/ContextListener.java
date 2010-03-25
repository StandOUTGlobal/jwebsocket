//	---------------------------------------------------------------------------
//	jWebSocket - Context Listener for Web Applications
//	Copyright (c) 2010 jWebSocket.org, Alexander Schulze, Innotrade GmbH
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
package org.jwebsocket.appserver;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.jwebsocket.api.WebSocketEngine;
import org.jwebsocket.config.JWebSocketConstants;
import org.jwebsocket.kit.WebSocketException;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.netty.engines.NettyEngine;
import org.jwebsocket.plugins.TokenPlugInChain;
import org.jwebsocket.plugins.rpc.RPCPlugIn;
import org.jwebsocket.plugins.streaming.StreamingPlugIn;
import org.jwebsocket.plugins.system.SystemPlugIn;
import org.jwebsocket.server.CustomServer;
import org.jwebsocket.server.TokenServer;

/**
 * Web application lifecycle listener.
 * @author aschulze
 */
public class ContextListener implements ServletContextListener {

	TokenServer tokenServer = null;
	CustomServer customServer = null;
	private static Logger log = null;

	/**
	 *
	 * @param sce
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {

		Logging.initLogs("debug");
		log = Logging.getLogger(ContextListener.class);
		if (log.isDebugEnabled()) {
			log.debug("Initialising Context...");
		}

		// create the low-level engine
		WebSocketEngine engine = null;
		try {
			// TODO: find solutions for hardcoded engine id, refer to RPCPlugIn!
			//engine = new TCPEngine("tcp0", Config.DEFAULT_PORT, Config.DEFAULT_TIMEOUT);
			engine = new NettyEngine("tcp0", JWebSocketConstants.DEFAULT_PORT, JWebSocketConstants.DEFAULT_TIMEOUT);
			engine.startEngine();
		} catch (Exception ex) {
			log.error("Error instantating engine: " + ex.getMessage());
			return;
		}

		// create the token server (based on the TCP engine)
		tokenServer = null;
		try {
			// instantiate the Token server and bind engine to it
			tokenServer = new TokenServer("ts0");
			// the token server already instantiates a plug-in chain
			TokenPlugInChain plugInChain = tokenServer.getPlugInChain();
			// let the server support the engine
			tokenServer.addEngine(engine);
			// add the SystemPlugIn listener (for the jWebSocket default functionality)
			plugInChain.addPlugIn(new SystemPlugIn());
			// add the RPCPlugIn plug-in
			plugInChain.addPlugIn(new RPCPlugIn());
			// add the streaming plug-in (e.g. for the time stream demo)
			plugInChain.addPlugIn(new StreamingPlugIn());
			
			if (log.isDebugEnabled()) {
				log.debug("Starting token server...");
			}
			tokenServer.startServer();
		} catch (Exception ex) {
			log.error("Error instantiating TokenServer: " + ex.getMessage());
		}

		// create the custom server (based on the TCP engine as well)
		customServer = null;
		try {
			// instantiate the custom server and bind engine to it
			customServer = new CustomServer("cs0");
			// the custom server already instantiates a plug-in chain
			// BasePlugInChain plugInChain = customServer.getPlugInChain();
			// let the server support the engine
			customServer.addEngine(engine);
			// add the SystemPlugIn listener (for the jWebSocket default functionality)
			// customServer.addPlugIn(new SystemPlugIn());
			if (log.isDebugEnabled()) {
				log.debug("Starting custom server...");
			}
			customServer.startServer();
		} catch (Exception ex) {
			log.error("Error instantating CustomServer: " + ex.getMessage());
		}
	}

	/**
	 *
	 * @param sce
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		try {
			tokenServer.stopServer();
		} catch (WebSocketException ex) {
			log.error("Stopping TokenServer: " + ex.getMessage());
		}
		try {
			customServer.stopServer();
		} catch (WebSocketException ex) {
			log.error("Stopping CustomServer: " + ex.getMessage());
		}
	}
}
