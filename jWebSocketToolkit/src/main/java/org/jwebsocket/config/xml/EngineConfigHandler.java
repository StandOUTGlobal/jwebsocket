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
package org.jwebsocket.config.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.jwebsocket.config.Config;

/**
 * @author puran
 * @version $Id$
 * 
 */
public class EngineConfigHandler implements ConfigHandler {
	private static final String ELEMENT_ENGINE = "engine";
	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String JAR = "jar";
	private static final String PORT = "port";
	private static final String TIMEOUT = "timeout";
	private static final String DOMAINS = "domains";
	private static final String DOMAIN = "domain";

	@Override
	public Config processConfig(XMLStreamReader streamReader)
			throws XMLStreamException {
		String id = "", name = "", jar = "";
		int port = 0,timeout = 0;
		List<String> domains = null;
		while (streamReader.hasNext()) {
			streamReader.next();
			if (streamReader.isStartElement()) {
				String elementName = streamReader.getLocalName();
				if (elementName.equals(ID)) {
					streamReader.next();
					id = streamReader.getText();
				} else if (elementName.equals(NAME)) {
					streamReader.next();
					name = streamReader.getText();
				} else if (elementName.equals(JAR)) {
					streamReader.next();
					jar = streamReader.getText();
				} else if (elementName.equals(PORT)) {
					streamReader.next();
					port = Integer.parseInt(streamReader.getText());
				} else if (elementName.equals(TIMEOUT)) {
					streamReader.next();
					timeout = Integer.parseInt(streamReader.getText());
				} else if (elementName.equals(DOMAINS)) {
					domains = getDomains(streamReader);
				} else {
					//ignore
				}
			}
			if (streamReader.isEndElement()) {
				String elementName = streamReader.getLocalName();
				if (elementName.equals(ELEMENT_ENGINE)) {
					break;
				}
			}
		}
		return new Engine(id, name, jar, port, timeout, domains);
	}

	/**
	 * Read the list of domains
	 * @param streamReader the stream reader object
	 * @return the list of domains for the engine
	 * @throws XMLStreamException in case of stream exception
	 */
	private List<String> getDomains(XMLStreamReader streamReader) throws XMLStreamException {
		List<String> domains = new ArrayList<String>();
		while (streamReader.hasNext()) {
			streamReader.next();
			if (streamReader.isStartElement()) {
				String elementName = streamReader.getLocalName();
				if (elementName.equals(DOMAIN)) {
					streamReader.next();
					domains.add(streamReader.getText());
				}
			}
			if (streamReader.isEndElement()) {
				String elementName = streamReader.getLocalName();
				if (elementName.equals(DOMAINS)) {
					break;
				}
			}
		}
		return domains;
	}

}
