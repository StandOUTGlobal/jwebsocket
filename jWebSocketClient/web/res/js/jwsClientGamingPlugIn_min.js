jws.ClientGamingPlugIn={NS:jws.NS_BASE+".plugins.clientGaming",addPlayer:function(aT,an,bE){aT="player"+aT;var be=document.getElementById(aT);if(!be){be=document.createElement("div");}be.id=aT;be.style.position="absolute";be.style.overflow="hidden";be.style.opacity=0.85;be.style.left="100px";be.style.top="100px";be.style.width="75px";be.style.height="75px";be.style.border="1px solid black";be.style.background="url(img/player_"+bE+".png) 15px 18px no-repeat";be.style.backgroundColor=bE;be.style.color="white";be.innerHTML="<font style=\"font-size:8pt\">Player "+an+"</font>";document.body.appendChild(be);if(!this.players){this.players={};}this.players[aT]=be;},removeAllPlayers:function(){if(this.players){for(var lId in this.players){document.body.removeChild(this.players[lId]);}}delete this.players;},removePlayer:function(aT){aT="player"+aT;var be=document.getElementById(aT);if(be){document.body.removeChild(be);if(this.players){delete this.players[aT];}}},movePlayer:function(aT,aX,aY){aT="player"+aT;var be=document.getElementById(aT);if(be){be.style.left=aX+"px";be.style.top=aY+"px";}},processOpened:function(aR){this.addPlayer(aR.sourceId,aR.sourceId,"green");aR.ns=jws.SystemClientPlugIn.NS;aR.type="broadcast";aR.request="identify";this.sendToken(aR);},processClosed:function(aR){this.removeAllPlayers();},processConnected:function(aR){this.addPlayer(aR.sourceId,aR.sourceId,"red");},processDisconnected:function(aR){this.removePlayer(aR.sourceId);},processToken:function(aR){if(aR.ns==jws.SystemClientPlugIn.NS){var lX,lY;if(aR.event=="move"){lX=aR.x;lY=aR.y;this.movePlayer(aR.sourceId,lX,lY);}else if(aR.event=="identification"){this.addPlayer(aR.sourceId,aR.sourceId,"red");lX=aR.x;lY=aR.y;this.movePlayer(aR.sourceId,lX,lY);}else if(aR.request=="identify"){var be=document.getElementById("player"+this.getId());lX=100;lY=100;if(be){lX=parseInt(be.style.left);lY=parseInt(be.style.top);}var J={ns:jws.SystemClientPlugIn.NS,type:"broadcast",event:"identification",x:lX,y:lY,username:this.getUsername()};this.sendToken(J);}}},broadcastGamingEvent:function(aR,d){var bj=this.checkConnected();if(bj.code==0){aR.ns=jws.SystemClientPlugIn.NS;aR.type="broadcast";aR.event="move";aR.senderIncluded=true;aR.responseRequested=false;aR.username=this.getUsername();this.sendToken(aR,d);}return bj;}};jws.oop.addPlugIn(jws.jWebSocketTokenClient,jws.ClientGamingPlugIn);