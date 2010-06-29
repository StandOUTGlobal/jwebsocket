/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jwebsocket.appserver;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.factory.JWebSocketFactory;
import org.jwebsocket.kit.WebSocketEvent;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.server.TokenServer;
import org.jwebsocket.token.Token;

/**
 *
 * @author aschulze
 */
public class WebSocketDemo extends HttpServlet implements WebSocketTokenListener {

	private static Logger log = Logging.getLogger(WebSocketDemo.class);

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/plain;charset=UTF-8");
		PrintWriter out = response.getWriter();

		try {
			out.println("This session: " + request.getSession().getId());
			out.println("Http sessions: " + WebSocketHttpSessionMerger.getHttpSessionsCSV());
			out.println("WebSocket sessions: " + WebSocketHttpSessionMerger.getWebSocketSessionsCSV());
		} finally {
			out.close();
		}
	}

	@Override
	public void init() {
		log.info("Adding servlet '" + getClass().getSimpleName() + "' to WebSocket listeners...");
		TokenServer lServer = (TokenServer) JWebSocketFactory.getServer("ts0");
		lServer.addListener(this);
	}

	@Override
	public void processOpened(WebSocketEvent aEvent) {
		log.info("Opened WebSocket session: " + aEvent.getSession().getSessionId());
		// if a new web socket connection has been started,
		// update the session tables accordingly
		WebSocketHttpSessionMerger.addWebSocketSession(aEvent.getSession());
	}

	@Override
	public void processPacket(WebSocketEvent aEvent, WebSocketPacket aPacket) {
		log.info("Received WebSocket packet: " + aPacket.getASCII());
	}

	@Override
	public void processWebSocketToken(WebSocketEvent aEvent, Token aToken) {
	}

	@Override
	public void processClosed(WebSocketEvent aEvent) {
		log.info("Closed WebSocket session: " + aEvent.getSession().getSessionId());
		// if a web socket connection has been terminated,
		// update the session tables accordingly
		WebSocketHttpSessionMerger.removeWebSocketSession(aEvent.getSession());
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>
}
