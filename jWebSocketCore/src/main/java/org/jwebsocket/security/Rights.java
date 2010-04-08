//	---------------------------------------------------------------------------
//	jWebSocket - Right Class
//	Copyright (c) 2010 jWebSocket.org, Alexander Schulze, Innotrade GmbH
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
package org.jwebsocket.security;

import javolution.util.FastMap;
import org.apache.log4j.Logger;

/**
 * implements a map of rights to act as a role.
 * @author aschulze
 */
public class Rights {

	private static Logger log = Logger.getLogger(Rights.class);
	private FastMap<String, Right> rights = new FastMap<String, Right>();

	/**
	 * adds a new right to the map of rights. If there is already a right with
	 * the given stored in the map it will be overwritten. If null is passed or
	 * if the right has no valid key no operation is performed.
	 * @param aRight
	 */
	public void addRight(Right aRight) {
		if (aRight != null && aRight.getKey() != null) {
			rights.put(aRight.getKey(), aRight);
		}
	}

	/**
	 * returns a right identified by its key or <tt>null</tt> if the right
	 * cannot be found in the map or the key passed is <tt>null</tt>.
	 * @param aKey
	 * @return
	 */
	public Right get(String aKey) {
		if (aKey != null) {
			return rights.get(aKey);
		} else {
			return null;
		}
	}

	/**
	 * removes a certain right indentified by its key from the map of rights.
	 * If the key is <tt>null</tt> or right could not be found in the map no
	 * operation is performed.
	 * @param aKey
	 */
	public void removeRight(String aKey) {
		if (aKey != null) {
			rights.remove(aKey);
		}
	}

	/**
	 * removes a certain right from the map of rights.
	 * If the right could not be found in the map no operation is performed.
	 * @param aRight
	 */
	public void removeRight(Right aRight) {
		if (aRight != null) {
			rights.remove(aRight.getKey());
		}
	}

	/**
	 * checks if the map of rights contains a certain right. The key of the
	 * right passed must not be null.
	 * @param aRight
	 * @return
	 */
	public boolean hasRight(Right aRight) {
		if (aRight != null && aRight.getKey() != null) {
			return rights.containsKey(aRight.getKey());
		} else {
			return false;
		}
	}

	/**
	 * checks if the map of rights contains a certain right identified by its
	 * key. The key must not be null.
	 * @param aKey
	 * @return
	 */
	public boolean hasRight(String aKey) {
		if (aKey != null) {
			return rights.containsKey(aKey);
		} else {
			return false;
		}
	}
}
