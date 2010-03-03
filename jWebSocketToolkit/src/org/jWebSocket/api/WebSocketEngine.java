//	---------------------------------------------------------------------------
//	jWebSocket - Engine API
//	Copyright (c) 2010 Alexander Schulze, Innotrade GmbH
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
package org.jWebSocket.api;

import java.util.List;
import org.jWebSocket.kit.WebSocketException;

/**
 * Specifies the API for jWebSocket engines. An engine maintains multiple 
 * connectors. The engine does neither parse nor process but data packets but
 * only passes them from either an underlying connector to the above server(s)
 * or from one of the higher level servers to one or more connectors of a
 * particular engine.
 * @author Alexander Schulze
 * @author Puran Singh
 * @version $Id: WebSocketEngine.java 2010-03-03
 *
 */
public interface WebSocketEngine {

	/**
	 * Starts the engine. Usually an engine is implemented as a thread which
	 * waits for new clients to be connected via a WebSocketConnector. So here
	 * usually the listener threads for incoming connections are instantiated.
	 *
	 * @throws WebSocketException
	 */
	void startEngine() throws WebSocketException;

	/**
	 * Stops the engine. Here usually first all connected clients are stopped
	 * and afterwards the listener threads for incoming new clients is stopped
	 * as well.
	 * @throws WebSocketException
	 */
	void stopEngine() throws WebSocketException;

	/**
	 * Is called after the web socket engine has been started sucessfully.
	 * Here usually the engine notifies the server(s) above that the engine
	 * is started.
	 */
	void engineStarted();

	/**
	 * Is called after the web socket engine has (been) stopped sucessfully.
	 * Here usually the engine notifies the server(s) above that the engine
	 * has stopped.
	 */
	public void engineStopped();

	/**
	 * Is called after a new client has connected. Here usually the engine
	 * notifies the server(s) above that a new connection has been established.
	 * @param aConnector
	 */
	void connectorStarted(WebSocketConnector aConnector);

	/**
	 * Is called after a new client has disconnected. Here usually the engine
	 * notifies the server(s) above that a connection has been closed.
	 * @param aConnector
	 */
	void connectorStopped(WebSocketConnector aConnector);

	/**
	 * Returns the list of clients connected to this engine. Please consider
	 * that a server can support multiple engines. This method only returns
	 * the clients of this engine.
	 * @return the connector clients
	 */
	List<WebSocketConnector> getConnectors();

	/**
	 * Returns {@code true} if the engine is running or {@code false} otherwise.
	 * The alive status usually represents the state of the main engine listener
	 * thread.
	 * @return true or false based on the server status
	 */
	boolean isAlive();

	/**
	 * Processes an incoming data packet from a certain connector. The
	 * implementation of the engine usually simply passes the packets to the
	 * server(s) of the overlying communication tier.
	 * @param aConnector
	 * @param aDataPacket
	 */
	void processPacket(WebSocketConnector aConnector, WebSocketPaket aDataPacket);

	/**
	 * Sends a data packet to a certain connector.
	 * @param aConnector
	 * @param aDataPacket
	 */
	void sendPacket(WebSocketConnector aConnector, WebSocketPaket aDataPacket);

	/**
	 * Broadcasts a data packet to all connectors. Usually the implementation
	 * simply iterates through the list of connectors and calls their sendPacket
	 * method.
	 * @param aDataPacket
	 */
	void broadcastPacket(WebSocketPaket aDataPacket);

	/**
	 * Removes a certain connector from the engine. This usually has not to be
	 * done by the application but by the engine implementations only.
	 * @param aConnector
	 */
	void removeConnector(WebSocketConnector aConnector);

	/**
	 * Returns a list of all servers that are currenly bound to this engine.
	 * This list of servers is maintained by the engine and should not be
	 * manipulated by the application.
	 * @return
	 */
	public List<WebSocketServer> getServers();

	/**
	 * Registers a server at the engine so that the engine is able to notify
	 * the server in case of new connections and incoming data packets from
	 * a connector. This method is not supposed to be called directly from the
	 * application.
	 * @param aServer
	 */
	public void addServer(WebSocketServer aServer);

	/**
	 * Unregisters a server from the engine so that the engine won't notify
	 * the server in case of new connections or incoming data packets from
	 * a connector. This method is not supposed to be called directly from the
	 * application.
	 * @param aServer
	 */
	public void removeServer(WebSocketServer aServer);


}


