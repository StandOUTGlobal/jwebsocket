jws.CanvasPlugIn={NS:jws.NS_BASE+".plugins.canvas",processToken:function(aR){if(aR.reqNS==jws.CanvasPlugIn.NS){if("clear"==aR.reqType){this.doClear(aR.id);}else if("beginPath"==aR.reqType){this.doBeginPath(aR.id);}else if("moveTo"==aR.reqType){this.doMoveTo(aR.id,aR.x,aR.y);}else if("lineTo"==aR.reqType){this.doLineTo(aR.id,aR.x,aR.y);}else if("closePath"==aR.reqType){this.doClosePath(aR.id);}}},bm:{},canvasOpen:function(aT,bL){var bG=jws.$(bL);this.bm[aT]={fDOMElem:bG,ctx:bG.getContext("2d")};},canvasClose:function(aT){this.bm[aT]=null;delete this.bm[aT];},doClear:function(aT){var bh=this.bm[aT];if(bh!=null){var lW=bh.fDOMElem.getAttribute("width");var lH=bh.fDOMElem.getAttribute("height");bh.ctx.clearRect(0,0,lW,lH);return true;}return false;},canvasClear:function(aT){if(this.doClear(aT)){var J={reqNS:jws.CanvasPlugIn.NS,reqType:"clear",id:aT};this.broadcastToken(J);}},doBeginPath:function(aT){var bh=this.bm[aT];if(bh!=null){bh.ctx.beginPath();return true;}return false;},canvasBeginPath:function(aT){if(this.doBeginPath(aT)){var J={reqNS:jws.CanvasPlugIn.NS,reqType:"beginPath",id:aT};this.broadcastToken(J);}},doMoveTo:function(aT,aX,aY){var bh=this.bm[aT];if(bh!=null){bh.ctx.moveTo(aX,aY);return true;}return false;},canvasMoveTo:function(aT,aX,aY){if(this.doMoveTo(aT,aX,aY)){var J={reqNS:jws.CanvasPlugIn.NS,reqType:"moveTo",id:aT,x:aX,y:aY};this.broadcastToken(J);}},doLineTo:function(aT,aX,aY){var bh=this.bm[aT];if(bh!=null){bh.ctx.lineTo(aX,aY);bh.ctx.stroke();return true;}return false;},canvasLineTo:function(aT,aX,aY){if(this.doLineTo(aT,aX,aY)){var J={reqNS:jws.CanvasPlugIn.NS,reqType:"lineTo",id:aT,x:aX,y:aY};this.broadcastToken(J);}},doClosePath:function(aT){var bh=this.bm[aT];if(bh!=null){bh.ctx.closePath();return true;}return false;},canvasClosePath:function(aT){if(this.doClosePath(aT)){var J={reqNS:jws.CanvasPlugIn.NS,reqType:"closePath",id:aT};this.broadcastToken(J);}}};jws.oop.addPlugIn(jws.jWebSocketTokenClient,jws.CanvasPlugIn);