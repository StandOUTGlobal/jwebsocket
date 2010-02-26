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
package org.jwebsocket.server.api;

/**
 * Context interface for the connector clients
 * @author Puran Singh
 * @version $Id$
 * 
 * TODO: add more methods to hide the core engine implementation specific details 
 */
public interface ConnectorContext {
	
	/**
	 * sends the string data to the output of WebSocket 
	 * @param stringData the string data 
	 */
	void sendString(String stringData);
	/**
	 * Returns the session id 
	 * @return the session id
	 */
	String getSessionId();
	
	/**
	 * Returns the local port.
	 * @return the port number
	 */
	int getLocalPort();
	
	/**
	 * Returns the server instance
	 * @return the server 
	 */
	JWebSocketServer getJWebSocketServer();
}