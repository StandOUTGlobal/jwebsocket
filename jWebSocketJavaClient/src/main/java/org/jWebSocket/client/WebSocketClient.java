/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jWebSocket.client;

import org.jwebsocket.api.WebSocketPaket;

/**
 *
 * @author aschulze
 */
public interface WebSocketClient {

	/**
	 * Starts the WebSocket client.
	 */
	void startClient();

	/**
	 * Stops the WebSocket client.
	 */
	void stopClient();
	
	/**
	 * Notifies the application that the client has started.
	 */
	void clientStarted();
	
	/**
	 * Notifies the application that the client has stopped.
	 */
	void clientStopped();
	
	/**
	 * Allows the application to send a data packet to the server.
	 *
	 * @param aDataPacket
	 */
	void sendPacket(WebSocketPaket aDataPacket);
	
	/**
	 * Notifies the application that a data packet has been received from
	 * the server.
	 *
	 * @param aDataPacket
	 */
	void processPacket(WebSocketPaket aDataPacket);

}
