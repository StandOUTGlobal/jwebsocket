//  ---------------------------------------------------------------------------
//  jWebSocket - ChannelListener
//  Copyright (c) 2010 Innotrade GmbH, jWebSocket.org
//  ---------------------------------------------------------------------------
//  This program is free software; you can redistribute it and/or modify it
//  under the terms of the GNU Lesser General Public License as published by the
//  Free Software Foundation; either version 3 of the License, or (at your
//  option) any later version.
//  This program is distributed in the hope that it will be useful, but WITHOUT
//  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//  more details.
//  You should have received a copy of the GNU Lesser General Public License along
//  with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//  ---------------------------------------------------------------------------
package org.jwebsocket.plugins.channels;

/**
 * Listener interface for the <tt>Channel</tt>. 
 * @author puran
 * @version $Id$
 */
public interface ChannelListener {

	/**
	 * Called when the channel is started.
	 * @param channel the started channel
	 */
	void channelStarted(Channel channel);

	/**
	 * called when the channel is stopped for receiving/sending any data
	 * @param channel the stopped channel object
	 */
	void channelStopped(Channel channel);

	/**
	 * called when the channel is suspended that means the channel suspended
	 * can no longer be used to send and receive data unless it is started again
	 * @param channel the suspended channel
	 */
	void channelSuspended(Channel channel);

	/**
	 * called when a new subscriber subscribes the channel
	 * @param channel the channel
	 * @param subscriber the subscriber
	 */
	void subscribed(Channel channel, Subscriber subscriber);

	/**
	 * called when someone unsuscribes the channel
	 * @param channel the channel
	 * @param subscriber the subscriber who unsuscribed
	 */
	void unsubscribed(Channel channel, Subscriber subscriber);
}