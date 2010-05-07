//	---------------------------------------------------------------------------
//	jWebSocket - WebSocket TCP Engine
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
package org.jwebsocket.tcp.engines;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.api.WebSocketEngine;
import org.jwebsocket.config.JWebSocketConstants;
import org.jwebsocket.engines.BaseEngine;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.tcp.connectors.TCPConnector;
import org.jwebsocket.kit.CloseReason;
import org.jwebsocket.kit.RequestHeader;
import org.jwebsocket.kit.WebSocketException;

/**
 * Implementation of the jWebSocket TCP engine. The TCP engine provide a Java
 * Socket implementation of the WebSocket protocol. It contains the handshake
 * @author aschulze
 */
public class TCPEngine extends BaseEngine {

	private static Logger log = Logging.getLogger(TCPEngine.class);
	private ServerSocket serverSocket = null;
	private int listenerPort = 8787;
	private int sessionTimeout = 120000;
	private boolean isRunning = false;
	Thread engineThread = null;

	/**
	 * Constructor of the TCP engine. The port and the default session timeout
	 * have to be passed. The session timout passed here is used only when no
	 * explicit timeout per connection is specified.
	 * @param aId
	 * @param aPort TCP port the engine listens on.
	 * @param aSessionTimeout The default server side session time out.
	 * @throws WebSocketException
	 */
	public TCPEngine(String aId, Integer aPort, Integer aSessionTimeout)
		throws WebSocketException {
		super(aId);
		listenerPort = aPort;
		sessionTimeout = aSessionTimeout;
	}

	@Override
	public void startEngine()
		throws WebSocketException {
		if (log.isDebugEnabled()) {
			log.debug("Starting TCP engine (" + getId() + ")...");
		}
		try {
			serverSocket = new ServerSocket(listenerPort);
			/*
			serverSocket = new ServerSocket(listenerPort); // listenerPort
			serverSocket.setReuseAddress(true);
			InetSocketAddress lISA = new InetSocketAddress(listenerPort);
			serverSocket.bind(lISA);
			 */
			setSessionTimeout(sessionTimeout);

			EngineListener listener = new EngineListener(this);
			engineThread = new Thread(listener);
			engineThread.start();

		} catch (IOException ex) {
			throw new WebSocketException(ex.getMessage());
		}

		super.startEngine();
		if (log.isInfoEnabled()) {
			log.info("TCP engine (" + getId() + ") started.");
		}
	}

	@Override
	public void stopEngine(CloseReason aCloseReason)
		throws WebSocketException {
		if (log.isDebugEnabled()) {
			log.debug("Stopping TCP engine (" + getId() + ")...");
		}
		// inherited method stops all connectors
		super.stopEngine(aCloseReason);
		// resetting "isRunning" causes engine listener to terminate
		isRunning = false;
		try {
			// when done, close server socket
			// closing the server socket should lead to an exeption
			// at accept in the listener thread which terminates the listener
			serverSocket.close();
			if (log.isInfoEnabled()) {
				log.info("TCP engine (" + getId() + ") stopped (closed=" + serverSocket.isClosed() + ").");
			}
			serverSocket = null;
		} catch (Exception ex) {
			log.error("Stopping TCP (" + getId() + ") engine:" + ex.getMessage());
		}
	}

	@Override
	public void connectorStarted(WebSocketConnector aConnector) {
		if (log.isDebugEnabled()) {
			log.debug("Detected new connector at port " + aConnector.getRemotePort() + ".");
		}
		super.connectorStarted(aConnector);
	}

	@Override
	public void connectorStopped(WebSocketConnector aConnector, CloseReason aCloseReason) {
		if (log.isDebugEnabled()) {
			log.debug("Detected stopped connector at port " + aConnector.getRemotePort() + ".");
		}
		super.connectorStopped(aConnector, aCloseReason);
	}

	private RequestHeader processHandshake(Socket aClientSocket)
		throws UnsupportedEncodingException, IOException {

		RequestHeader header = new RequestHeader();

		String host = null;
		String origin = null;
		String location = null;
		Map<String, String> args = new HashMap<String, String>();
		String path = "";
		String searchString = "";

		BufferedReader br = null;
		PrintStream os = null;

		br = new BufferedReader(new InputStreamReader(aClientSocket.getInputStream(), "UTF-8"));
		os = new PrintStream(aClientSocket.getOutputStream(), true, "UTF-8");

		// read complete header first...
		String line = br.readLine();
		String req = "";
		while (line != null && line.length() > 0) {
			req += line + "\n";
			line = br.readLine();
		}
		if (log.isDebugEnabled()) {
			log.debug("Received Header (" + req.replace("\n", "\\n") + ")");
		}

		// special handling for flash bridge if trying to access ws:// port
		if (req.indexOf("<policy-file-request/>") >= 0) {
			log.debug("Answering flash bridge policy-file-request...");
			os.print(
				"<cross-domain-policy>"
				+ "<allow-access-from domain=\"*\" to-ports=\"*\" />"
				+ "</cross-domain-policy>\n");
			req = "";
			while (line != null && line.length() > 0) {
				req += line + "\n";
				line = br.readLine();
			}
			if (log.isDebugEnabled()) {
				log.debug("Received Header again (" + req.replace("\n", "\\n") + ")");
			}
			// returning no header means immediately disconnect client again.
			return null;
		}

		// now parse header for correct handshake....
		// get host....
		int pos = req.indexOf("Host:");
		pos += 6;
		host = req.substring(pos);
		pos = host.indexOf("\n");
		host = host.substring(0, pos);
		// get origin....
		pos = req.indexOf("Origin:");
		pos += 8;
		origin = req.substring(pos);
		pos = origin.indexOf("\n");
		origin = origin.substring(0, pos);
		// get path....
		pos = req.indexOf("GET");
		pos += 4;
		path = req.substring(pos);
		pos = path.indexOf("HTTP");
		path = path.substring(0, pos - 1);

		// isolate search string
		pos = path.indexOf(JWebSocketConstants.PATHARG_SEPARATOR);
		if (pos >= 0) {
			searchString = path.substring(pos + 1);
			if (searchString.length() > 0) {
				String[] lArgs = searchString.split(JWebSocketConstants.ARGARG_SEPARATOR);
				for (int i = 0; i < lArgs.length; i++) {
					String[] lKeyValuePair = lArgs[i].split(JWebSocketConstants.KEYVAL_SEPARATOR, 2);
					if (lKeyValuePair.length == 2) {
						args.put(lKeyValuePair[0], lKeyValuePair[1]);
						if (log.isDebugEnabled()) {
							log.debug("arg" + i + ": " + lKeyValuePair[0] + "=" + lKeyValuePair[1]);
						}
					}
				}
			}
		}

		// create location based on ws:// + host + path
		location = "ws://" + host + path;
		if (log.isDebugEnabled()) {
			log.debug("Parsed header ("
				+ "host: " + host + ", "
				+ "origin: " + origin + ", "
				+ "location: " + location + ", "
				+ "path: " + path + ", "
				+ "searchString: " + searchString
				+ ")");
		}
		// now that we have parsed the header send handshake...
		String res =
			"HTTP/1.1 101 Web Socket Protocol Handshake\r\n"
			+ "Upgrade: WebSocket\r\n"
			+ "Connection: Upgrade\r\n"
			+ "WebSocket-Origin: " + origin + "\r\n"
			+ "WebSocket-Location: " + location + "\r\n"
			+ "\r\n";
		if (log.isDebugEnabled()) {
			log.debug("Sent handshake (" + res.replace("\n", "\\n") + ")");
		}

		byte[] ba = res.getBytes("UTF-8");
		os.write(ba);
		os.flush();
		if (log.isDebugEnabled()) {
			log.debug("Handshake flushed.");
		}

		// set default sub protocol if none passed
		if (args.get("prot") == null) {
			args.put("prot", JWebSocketConstants.SUB_PROT_DEFAULT);
		}
		header.put("args", args);
		header.put("origin", origin);
		header.put("location", location);
		header.put("path", path);
		header.put("searchString", searchString);
		header.put("host", host);

		return header;
	}

	@Override
	/*
	 * Returns {@code true} if the TCP engine is running or {@code false} 
	 * otherwise. The alive status represents the state of the TCP engine
	 * listener thread.
	 */
	public boolean isAlive() {
		// TODO: Check isAlive state of TCPEngine
		return true;
	}

	private class EngineListener implements Runnable {

		private WebSocketEngine engine = null;

		/**
		 * Creates the server socket listener for new
		 * incoming socket connections.
		 * @param aEngine
		 */
		public EngineListener(WebSocketEngine aEngine) {
			engine = aEngine;
		}

		@Override
		public void run() {

			// notify server that engine has started
			engineStarted();

			isRunning = true;
			while (isRunning) {
				try {
					// accept is blocking so here is no need
					// to put any sleeps into this loop
					// if (log.isDebugEnabled()) {
					//	log.debug("Waiting for client...");
					// }
					Socket clientSocket = serverSocket.accept();
					// if (log.isDebugEnabled()) {
					//	log.debug("Client accepted...");
					// }
					try {
						// process handshake to parse header data
						RequestHeader header = processHandshake(clientSocket);

						if (header != null) {
							// set socket timeout to given amount of milliseconds
							// check min and max timeout ranges
							int lSessionTimeout = header.getTimeout(JWebSocketConstants.DEFAULT_TIMEOUT);
							if (lSessionTimeout > JWebSocketConstants.MAX_TIMEOUT) {
								lSessionTimeout = JWebSocketConstants.MAX_TIMEOUT;
							} else if (lSessionTimeout < JWebSocketConstants.MIN_TIMEOUT) {
								lSessionTimeout = JWebSocketConstants.MIN_TIMEOUT;
							}
							clientSocket.setSoTimeout(lSessionTimeout);

							// create connector and pass header
							// log.debug("Instantiating connector...");
							WebSocketConnector connector = new TCPConnector(engine, clientSocket);
							// log.debug("Setting header to engine...");
							connector.setHeader(header);
							// log.debug("Adding connector to engine...");
							getConnectors().put(connector.getId(), connector);
							if (log.isDebugEnabled()) {
								log.debug("Starting connector...");
							}
							connector.startConnector();
						} else {
							// if header could not be parsed properly
							// immediately disconnect the client.
							clientSocket.close();
						}
					} catch (UnsupportedEncodingException ex) {
						log.error("(encoding) " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
					} catch (IOException ex) {
						log.error("(io) " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
					} catch (Exception ex) {
						log.error("(other) " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
					}
				} catch (Exception ex) {
					isRunning = false;
					log.error("(accept) " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
				}
			}

			// notify server that engine has stopped
			engineStopped();
		}
	}
}
