// ---------------------------------------------------------------------------
// jWebSocket - Copyright (c) 2010 Innotrade GmbH
// ---------------------------------------------------------------------------
// This program is free software; you can redistribute it and/or modify it
// under the terms of the GNU Lesser General Public License as published by the
// Free Software Foundation; either version 3 of the License, or (at your
// option) any later version.
// This program is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
// for more details.
// You should have received a copy of the GNU Lesser General Public License
// along with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
// ---------------------------------------------------------------------------
package org.jwebsocket.android.canvasdemo;

import org.jwebsocket.client.token.BaseTokenClient;
import org.jwebsocket.kit.WebSocketException;

/**
 *
 * @author aschulze
 */
public class JWC {

    private static String URL = "ws://192.168.2.232:8787";
    private static BaseTokenClient jwc = new BaseTokenClient();


    public static void open() throws WebSocketException {
        jwc.open(URL);
    }

    public static void close() throws WebSocketException {
        jwc.close();
    }
}
