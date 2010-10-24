//	---------------------------------------------------------------------------
//	jWebSocket Twitter PlugIn (uses jWebSocket Client and Server)
//	(C) 2010 jWebSocket.org, Alexander Schulze, Innotrade GmbH, Herzogenrath
//	---------------------------------------------------------------------------
//	This program is free software; you can redistribute it and/or modify it
//	under the terms of the GNU Lesser General Public License as published by the
//	Free Software Foundation; either version 3 of the License, or (at your
//	option) any later version.
//	This program is distributed in the hope that it will be useful, but WITHOUT
//	ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//	FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//	more details.
//	You should have received a copy of the GNU Lesser General Public License along
//	with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//	---------------------------------------------------------------------------


//	---------------------------------------------------------------------------
//  jWebSocket Twitter Client Plug-In
//	---------------------------------------------------------------------------

jws.TwitterPlugIn = {

	// namespace for twitter plugin
	// if namespace is changed update server plug-in accordingly!
	NS: jws.NS_BASE + ".plugins.twitter",

	processToken: function( aToken ) {
		// check if namespace matches
		if( aToken.ns == jws.TwitterPlugIn.NS ) {
			// here you can handle incoming tokens from the server
			// directy in the plug-in if desired.
			if( "getTimeline" == aToken.reqType ) {
				if( this.OnGotTwitterTimeline ) {
					this.OnGotTwitterTimeline( aToken );
				}
			} else if( "login" == aToken.reqType ) {
				if( this.onTwitterRequestToken ) {
					this.onTwitterRequestToken( aToken );
				}
			}
		}
	},

	tweet: function( aMessage, aOptions ) {
		var lRes = this.checkConnected();
		if( 0 == lRes.code ) {
			var lToken = {
				ns: jws.TwitterPlugIn.NS,
				type: "tweet",
				message: aMessage
			};
			this.sendToken( lToken,	aOptions );
		}
		return lRes;
	},

	twitterLogin: function( aUsername, aPassword, aOptions ) {
		var lRes = this.checkConnected();
		if( 0 == lRes.code ) {
			var lToken = {
				ns: jws.TwitterPlugIn.NS,
				type: "login",
				username: aUsername,
				password: aPassword
			};
			this.sendToken( lToken,	aOptions );
		}
		return lRes;
	},

	twitterLogout: function( aUsername, aPassword, aOptions ) {
		var lRes = this.checkConnected();
		if( 0 == lRes.code ) {
			var lToken = {
				ns: jws.TwitterPlugIn.NS,
				type: "logout"
			};
			this.sendToken( lToken,	aOptions );
		}
		return lRes;
	},

	twitterTimeline: function( aUsername, aOptions ) {
		var lRes = this.checkConnected();
		if( 0 == lRes.code ) {
			var lToken = {
				ns: jws.TwitterPlugIn.NS,
				type: "getTimeline",
				username: aUsername
			};
			this.sendToken( lToken,	aOptions );
		}
		return lRes;
	},

	twitterUserData: function( aUsername, aOptions ) {
		var lRes = this.checkConnected();
		if( 0 == lRes.code ) {
			var lToken = {
				ns: jws.TwitterPlugIn.NS,
				type: "getUserData",
				username: aUsername
			};
			this.sendToken( lToken,	aOptions );
		}
		return lRes;
	},

	setTwitterCallbacks: function( aListeners ) {
		if( !aListeners ) {
			aListeners = {};
		}
		if( aListeners.OnGotTwitterTimeline !== undefined ) {
			this.OnGotTwitterTimeline = aListeners.OnGotTwitterTimeline;
		}
		if( aListeners.onTwitterRequestToken !== undefined ) {
			this.onTwitterRequestToken = aListeners.onTwitterRequestToken;
		}
	}

}

// add the JWebSocket Twitter PlugIn into the TokenClient class
jws.oop.addPlugIn( jws.jWebSocketTokenClient, jws.TwitterPlugIn );