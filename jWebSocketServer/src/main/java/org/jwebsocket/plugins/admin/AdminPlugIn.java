//	---------------------------------------------------------------------------
//	jWebSocket - jWebSocket Administration Plug-In
//	Copyright (c) 2010 Alexander Schulze, Innotrade GmbH
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
package org.jwebsocket.plugins.admin;

import org.apache.log4j.Logger;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.api.WebSocketEngine;
import org.jwebsocket.config.JWebSocketServerConstants;
import org.jwebsocket.kit.CloseReason;
import org.jwebsocket.kit.PlugInResponse;
import org.jwebsocket.kit.WebSocketException;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.plugins.TokenPlugIn;
import org.jwebsocket.security.SecurityFactory;
import org.jwebsocket.server.TokenServer;
import org.jwebsocket.token.Token;

/**
 *
 * @author aschulze
 */
public class AdminPlugIn extends TokenPlugIn {

	private static Logger log = Logging.getLogger(AdminPlugIn.class);
	// if namespace changed update client plug-in accordingly!
	private String NS_ADMIN = JWebSocketServerConstants.NS_BASE + ".plugins.admin";

	/**
	 *
	 */
	public AdminPlugIn() {
		if (log.isDebugEnabled()) {
			log.debug("Instantiating admin plug-in...");
		}
		// specify default name space for admin plugin
		this.setNamespace(NS_ADMIN);
	}

	@Override
	public void processToken(PlugInResponse aResponse, WebSocketConnector aConnector, Token aToken) {
		String lType = aToken.getType();
		String lNS = aToken.getNS();

		if (lType != null && (lNS == null || lNS.equals(getNamespace()))) {
			// remote shut down server
			if (lType.equals("shutdown")) {
				shutdown(aConnector, aToken);
			}
		}
	}

	/**
	 * shutdown server
	 * @param aConnector
	 * @param aToken
	 */
	public void shutdown(WebSocketConnector aConnector, Token aToken) {
		TokenServer lServer = getServer();

		if (log.isDebugEnabled()) {
			log.debug("Processing 'shutdown'...");
		}

		// check if user is allowed to run 'shutdown' command
		if (!SecurityFactory.checkRight(lServer.getUsername(aConnector), NS_ADMIN + ".shutdown")) {
			lServer.sendToken(aConnector, lServer.createAccessDenied(aToken));
			return;
		}

		Token lResponseToken = lServer.createResponse(aToken);
		lResponseToken.put("msg", "Shutdown in progress...");
		lServer.sendToken(aConnector, lResponseToken);

		for (WebSocketEngine lEngine : lServer.getEngines().values()) {
			try {
				lEngine.stopEngine(CloseReason.SHUTDOWN);
			} catch (WebSocketException ex) {
				log.error(ex.getClass().getSimpleName()
						+ " on shutdown: " + ex.getMessage());
			}
		}

	}
}