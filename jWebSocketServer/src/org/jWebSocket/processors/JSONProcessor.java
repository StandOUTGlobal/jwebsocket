//	---------------------------------------------------------------------------
//	jWebSocket - WebSocket JSON Token Processor
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
package org.jWebSocket.processors;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.text.StrTokenizer;
import org.jWebSocket.api.IDataPacket;
import org.jWebSocket.kit.DataPacket;
import org.jWebSocket.kit.Token;

/**
 *
 * @author aschulze
 */
public class JSONProcessor extends BasePacketProcessor {

	public Token packetToToken(IDataPacket aDataPacket) {
		Token lToken = new Token();
		try {
			String lStr = aDataPacket.getString("UTF-8");
			// cut starting and trailing curly braces
			lStr = lStr.trim().substring(1, lStr.length() - 1);
			// String[] lItems = aData.split(",");
			StrTokenizer lTokens = new StrTokenizer(lStr, ',', '\"');
			String[] lItems = lTokens.getTokenArray();
			for (int i = 0; i < lItems.length; i++) {
				// String[] lKeyVal = lItems[i].split(":", 2);
				lTokens = new StrTokenizer(lItems[i], ':', '\"');
				String[] lKeyVal = lTokens.getTokenArray();
				if (lKeyVal.length == 2) {
					String lVal = lKeyVal[1];
					if (lVal.startsWith("\"") && lVal.endsWith("\"")) {
						lToken.put(lKeyVal[0], lVal.substring(1, lVal.length() - 1));
					} else {
						lToken.put(lKeyVal[0], lVal);
					}
				}
			}
		} catch (UnsupportedEncodingException ex) {
		}
		return lToken;
	}

	private String stringToJSON(String aString) {
		return ("\"" + aString + "\"");
	}

	private String listToJSON(List aList) {
		String lRes = "";
		for (Object lItem : aList) {
			String llRes = objectToJSON(lItem);
			lRes += llRes + ",";
		}
		if (lRes.length() > 1) {
			lRes = lRes.substring(0, lRes.length() - 1);
		}
		return ("[" + lRes + "]");
	}

	private String objectToJSON(Object aObj) {
		String lRes;
		if (aObj instanceof TokenConnector) {
			lRes = "\"" + ((TokenConnector) aObj).getUsername() + "@" + ((TokenConnector) aObj).getSessionId() + "\"";
		} else if (aObj instanceof String) {
			lRes = stringToJSON((String) aObj);
		} else if (aObj instanceof List) {
			lRes = listToJSON((List) aObj);
		} else {
			lRes = aObj.toString();
		}
		return lRes;
	}

	public IDataPacket tokenToPacket(Token aToken) {
		String lData = "{";
		Iterator lIterator = aToken.keySet().iterator();
		while (lIterator.hasNext()) {
			String lKey = (String) lIterator.next();
			Object lVal = aToken.get(lKey);
			lData +=
				lKey + ":" + objectToJSON(lVal)
				+ (lIterator.hasNext() ? "," : "");
		}
		lData += "}";
		IDataPacket lPacket = null;
		try {
			lPacket = new DataPacket(lData, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
		}
		return lPacket;
	}
}