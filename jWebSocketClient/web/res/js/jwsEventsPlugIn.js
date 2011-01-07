jws.oop.declareClass( "jws", "EventsNotifier", null, {
	jwsClient: {}
	,
	filterChain: []
	,
	plugIns: []
	,
	initialize : function(){
		jws.oop.addPlugIn(jws.jWebSocketTokenClient, this);
	}
	,
	notify: function(aEventName, aOptions){
		if (this.jwsClient.isConnected()){
			var lToken = {};
			if (aOptions.args){
				lToken = aOptions.args;
				delete (aOptions.args);
			}
			lToken.type      = aEventName;
			lToken._IS_EM_   = true;
			
			//Generating the unique token identifier
			aOptions._tokenUID = hex_md5(lToken);

			var aOnResponseObject = new jws.OnResponseObject();
			aOnResponseObject.request = aOptions;
			aOnResponseObject.filterChain = this.filterChain;

			if (undefined != aOptions.eventDefinition){
				for (var i = 0; i < this.filterChain.length; i++){
					try
					{
						this.filterChain[i].firstCall(lToken, aOnResponseObject);
					}
					catch(err)
					{
						switch (err)
						{
							case "stop_filter_chain":
								return;
							break;
							default:
								throw err;
							break;
						}
					}
					
				}
			}

			this.jwsClient.sendToken(lToken, aOnResponseObject);
		}
		else
			throw "client:not_connected";
    }
	,
	processToken: function (aToken) {
		if ("s2c.event_notification" == aToken.type){
			var event_name = aToken.event_name;
			var plugin_id = aToken.plugin_id;

			if (undefined != this.plugIns[plugin_id] && undefined != this.plugIns[plugin_id][event_name]){
				this.plugIns[plugin_id][event_name](aToken);
			}
			else {
				throw "s2c_event_support_not_found:" + event_name;
			}
		}
	}
});

jws.oop.declareClass( "jws", "OnResponseObject", null, {
	request: {}
	,
	filterChain: []
	,
	OnResponse: function(aResponseToken){
		if (undefined != this.request.eventDefinition){
			var index = this.filterChain.length - 1;
			while (index > -1){
				try
				{
					this.filterChain[index].secondCall(this.request, aResponseToken);
				}
				catch(err)
				{
					switch (err)
					{
						case "stop_filter_chain":
							return;
						break;
						default:
							throw err;
						break;
					}
				}
				index--;
			}
		}
		
		if (aResponseToken.code == 0){
			if (undefined != this.request.OnResponse)
				this.request.OnResponse(aResponseToken);

			if (undefined != this.request.OnSuccess)
				this.request.OnSuccess(aResponseToken);
		}
		else {
			if (undefined != this.request.OnResponse)
				this.request.OnResponse(aResponseToken);

			if (undefined != this.request.OnFailure)
				this.request.OnFailure(aResponseToken);
		}
    }
});

jws.oop.declareClass( "jws", "EventsPlugInGenerator", null, {
	generate: function(aPlugInId, aNotifier, OnReady){
		var plugIn = new jws.EventsPlugIn();
		plugIn.notifier = aNotifier;

		aNotifier.notify("plugin.getapi", {
			args: {
				plugin_id: aPlugInId
			}
			,
			plugIn: plugIn
			,
			OnReady: OnReady
			,
			OnSuccess: function(aResponseToken){
				this.plugIn.id = aResponseToken.id;
				this.plugIn.plugInAPI = aResponseToken.api;

				//Generate the plugin here
				for (method in aResponseToken.api){
					eval("this.plugIn." + method + "=function(aOptions){if (undefined == aOptions){aOptions = {};};var eventName=this.plugInAPI."+method+".type; aOptions.eventDefinition=this.plugInAPI."+ method + "; this.notifier.notify(eventName, aOptions);}")
				}

				//Registering the plugin in the notifier
				this.plugIn.notifier.plugIns[this.plugIn.id] = this.plugIn;

				//Plugin is ready to use
				this.OnReady(this.plugIn);
			}
			,
			OnFailure: function(aResponseToken){
				throw aResponseToken.msg;
			}	
		});

		return plugIn;
	}
});

jws.oop.declareClass( "jws", "EventsPlugIn", null, {
	id: ""
	,
	notifier: {}
	,
	plugInAPI: {}
	
	//Methods are generated in runtime!
	//Custom methods can be added using the OnReady callback
});

jws.oop.declareClass( "jws", "EventsBaseFilter", null, {
	firstCall: function(aToken, aOnResponseObject){}
	,
	secondCall: function(aRequest, aResponseToken){}
});

jws.oop.declareClass( "jws", "SecurityFilter", jws.EventsBaseFilter, {
	user:[]
	,
	firstCall: function(aToken, aOnResponseObject){
		if (aOnResponseObject.request.eventDefinition.isSecurityEnabled){
			var roles = null;
			//Getting allowed roles to notify the event
			roles = aOnResponseObject.request.eventDefinition.roles;

			//Checking if the user have the allowed roles
			if (roles.length > 0){
				for (var i = 0; i < roles.length; i++){
					for (var j = 0; j < this.user.roles.length; j++)
						if (roles[i] == this.user.roles[j])
							return;
				}
			}

			//Not Authorized
			aOnResponseObject.OnResponse({
				code: -1,
				msg: "Not autorized to notify this event. Allowed roles: " + roles.toString() 
			});
			this.OnNotAuthorized(aToken);
			throw "stop_filter_chain";
		}
	}
	,
	OnNotAuthorized: function(aToken){
		//Define a global 'OnNotAuthorized' behiavor here
		throw "not_authorized";
	}
});

jws.oop.declareClass( "jws", "CacheFilter", jws.EventsBaseFilter, {
	cache:{}
	,
	firstCall: function(lToken, aOnResponseObject){
		if (aOnResponseObject.request.eventDefinition.isCacheEnabled){
			var cachedResponseToken = this.cache.getItem(aOnResponseObject.request._tokenUID);
			if (null != cachedResponseToken){
				aOnResponseObject.OnResponse(cachedResponseToken);
				throw "stop_filter_chain";
			}
		}
	}
	,
	secondCall: function(aRequest, aResponseToken){
		if (aRequest.eventDefinition.isCacheEnabled){
			this.cache.setItem(aRequest._tokenUID, aResponseToken, {
				expirationAbsolute: null,
				expirationSliding: aRequest.eventDefinition.cacheTime,
				priority: jws.cache.CachePriority.High
			});
		}
	}
});

jws.oop.declareClass( "jws", "ValidatorFilter", jws.EventsBaseFilter, {
	typesMap: {}
	,
	firstCall: function(lToken, aOnResponseObject){
		var arguments = aOnResponseObject.request.eventDefinition.incomingArgsValidation;
		
		for (var index = 0; index < arguments.length; index++){
			if (!lToken[arguments[index].name] && !arguments[index].optional){
				aOnResponseObject.OnResponse({
					code: -1,
					msg: "Argument '"+arguments[index].name+"' is required!"
				});
				throw "stop_filter_chain";
			}else if (lToken.hasOwnProperty(arguments[index].name)){
				var requiredType = arguments[index].type;
				if (requiredType != typeof(lToken[arguments[index].name])){
					aOnResponseObject.OnResponse({
						code: -1,
						msg: "Argument '"+arguments[index].name+"' has invalid type. Required: '"+requiredType+"'"
					});
					throw "stop_filter_chain";
				}
			}
		}
	}
});


