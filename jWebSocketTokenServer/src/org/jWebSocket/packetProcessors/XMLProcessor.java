//	---------------------------------------------------------------------------
//	jWebSocket - XML Token Processor
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
package org.jWebSocket.packetProcessors;

import java.util.List;
import org.apache.log4j.Logger;
import org.jWebSocket.api.WebSocketPaket;
import org.jWebSocket.token.Token;

/**
 *
 * @author aschulze
 */
public class XMLProcessor  {

	private static Logger log = Logger.getLogger(XMLProcessor.class);

	public static Token packetToToken(WebSocketPaket aDataPacket) {
		// todo: implement!
		Token lArgs = new Token();
		return lArgs;
	}

	private static String stringToXML(String aString) {
		// todo: implement!
		String lRes = null;
		return lRes;
	}

	private static String listToXML(List aList) {
		// todo: implement!
		String lRes = null;
		return lRes;
	}

	private static String objectToXML(Object aObj) {
		// todo: implement!
		String lRes = null;
		return lRes;
	}

	public static WebSocketPaket tokenToPacket(Token aToken) {
		// todo: implement!
		return null;
	}
}