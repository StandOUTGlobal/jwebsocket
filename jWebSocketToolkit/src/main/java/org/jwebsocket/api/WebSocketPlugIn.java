//	---------------------------------------------------------------------------
//	jWebSocket - Basic PlugIn Class
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
package org.jwebsocket.api;

import org.jwebsocket.kit.CloseReason;
import org.jwebsocket.kit.PlugInResponse;

/**
 *
 * @author aschulze
 */
public interface WebSocketPlugIn {

	/**
	 * is called by the server when the engine has been started.
	 * @param aEngine
	 */
	void engineStarted(WebSocketEngine aEngine);

	/**
	 * is called by the server when the engine has been stopped.
	 * @param aEngine
	 */
	void engineStopped(WebSocketEngine aEngine);

	/**
	 *
	 * @param aConnector
	 */
	public void connectorStarted(WebSocketConnector aConnector);

	/**
	 *
	 * @param aResponse
	 * @param aConnector
	 * @param aDataPacket
	 */
	public void processPacket(PlugInResponse aResponse, WebSocketConnector aConnector, WebSocketPaket aDataPacket);

	/**
	 *
	 * @param aConnector
	 * @param aCloseReason
	 */
	public void connectorStopped(WebSocketConnector aConnector, CloseReason aCloseReason);

	/**
	 *
	 * @param aPlugInChain
	 */
	public void setPlugInChain(WebSocketPlugInChain aPlugInChain);

	/**
	 * @return the plugInChain
	 */
	public WebSocketPlugInChain getPlugInChain();

}
