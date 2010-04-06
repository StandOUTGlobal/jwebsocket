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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.jwebsocket.config.Config;

/**
 * Handler class that reads the <tt>right</tt> configuration
 * @author puran
 * @version $Id:$
 * 
 */
public class RightConfigHandler implements ConfigHandler {

	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String DESCRIPTION = "description";
	private static final String ELEMENT_RIGHT = "right";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Config processConfig(XMLStreamReader streamReader) throws XMLStreamException {
		String id = "", namespace = "", description = "";
		while (streamReader.hasNext()) {
			streamReader.next();
			if (streamReader.isStartElement()) {
				String elementName = streamReader.getLocalName();
				if (elementName.equals(ID)) {
					streamReader.next();
					id = streamReader.getText();
				} else if (elementName.equals(NAME)) {
					streamReader.next();
					namespace = streamReader.getText();
				} else if (elementName.equals(DESCRIPTION)) {
					streamReader.next();
					description = streamReader.getText();
				} else {
					//ignore
				}
			}
			if (streamReader.isEndElement()) {
				String elementName = streamReader.getLocalName();
				if (elementName.equals(ELEMENT_RIGHT)) {
					break;
				}
			}
		}
		return new RightConfig(id, namespace, description);
	}

}