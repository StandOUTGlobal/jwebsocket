//	---------------------------------------------------------------------------
//	jWebSocket - In- and Outbound Stream
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
package org.jWebSocket.streaming;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javolution.util.FastList;
import org.apache.log4j.Logger;
import org.jWebSocket.connectors.BaseConnector;
import org.jWebSocket.server.BaseServer;

/**
 *
 * @author aschulze
 */
public class BaseStream extends Thread {

	private static Logger log = Logger.getLogger(BaseStream.class);
	private FastList<BaseConnector> clients = new FastList<BaseConnector>();
	private boolean isRunning = false;
	private String streamID = null;
	public final FastList<Object> queue = new FastList<Object>();

	/**
	 *
	 */
	public BaseStream(String aStreamID) {
		this.streamID = aStreamID;
	}

	/**
	 *
	 */
	public void registerClient(BaseConnector aClient) {
		clients.add(aClient);
	}

	/**
	 *
	 */
	public boolean isClientRegistered(BaseConnector aClient) {
		return clients.indexOf(aClient) >= 0;
	}

	/**
	 *
	 */
	public void unregisterClient(BaseConnector aClient) {
		clients.remove(aClient);
	}

	/**
	 *
	 */
	public void registerAllClients(BaseServer aServer) {
		// clients.add(aServer);
	}

	/**
	 *
	 */
	public void unregisterAllClients(BaseServer aServer) {
		// clients.remove(aServer);
	}

	/**
	 *
	 */
	public void put(Object aObject) {
		synchronized (queue) {
			// add the queue item into the queue
			queue.add(aObject);
			// trigger sender thread
			queue.notify();
		}
	}

	protected void processClient(BaseConnector aClient, Object aObject) {
		try {
			aClient.send(aObject);
		} catch (UnsupportedEncodingException ex) {
			log.error("Exception: " + ex.getMessage());
		} catch (IOException ex) {
			//
		}

	}

	protected void processItem(Object aObject) {
		for (int i = 0; i < clients.size(); i++) {
			processClient(clients.get(i), aObject);
		}
	}

	@Override
	public void run() {
		isRunning = true;
		while (isRunning) {
			synchronized (queue) {
				if (queue.size() > 0) {
					Object lObj = queue.remove(0);
					processItem(lObj);
				} else {
					try {
						queue.wait();
					} catch (InterruptedException ex) {
						log.error("Exception:" + ex.getMessage());
					}
				}
			}
		}
	}

	/**
	 * @return the streamID
	 */
	public String getStreamID() {
		return streamID;
	}
}