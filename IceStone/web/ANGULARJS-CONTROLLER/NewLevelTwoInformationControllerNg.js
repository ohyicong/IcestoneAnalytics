var app = angular.module('leveltwoinfoApp',['ngWebSocket']);
app.directive('myDraggable', ['$document', function($document) {
    return {
      link: function(scope, element, attr) {
        var startX = 0, startY = 0, x = 0, y = 0;

        element.css({
         position: 'relative',
         cursor: 'pointer'
        });

        element.on('mousedown', function(event) {
          // Prevent default dragging of selected content
          event.preventDefault();
          startX = event.pageX - x;
          startY = event.pageY - y;
          $document.on('mousemove', mousemove);
          $document.on('mouseup', mouseup);
        });

        function mousemove(event) {
          y = event.pageY - startY;
          x = event.pageX - startX;
          console.log("x: "+x+" y: "+y);
          element.css({
            top: y + 'px',
            left:  x + 'px'
          });
        }

        function mouseup() {
          $document.off('mousemove', mousemove);
          $document.off('mouseup', mouseup);
        }
      }
    };
}]);
app.factory('leveltwoinfoFactory',function($http,$websocket){
    var ws = $websocket('ws://'+location.host+'/IceStone/stocktimelinegraphsocket');
    var isReady=[false];
    var collections=[];
    var changeBoxColor = function(old,current){
        if(old!==current){
            console.log(old+" "+current);
            return "blink";
        }else 
            return "norm";
    };
    var changeNumberColor = function(old,current,color){
        if(current!==old){
            if(current>old){
                console.log("More");
                return "#00e673";
            }else{
                console.log("less");
                return "red";
            }
        }else{
            console.log("same");
            return color;
        }
            
    };

    var getLevelTwoInfo = function(){
        console.log("Getting leveltwoinfo...");
        $http({
            method: 'POST',
            url: 'GetLevelTwoInformationServlet'
                
        }).then(function (success){               
           console.log(success.data);
           collections.push(success.data);
           isReady.splice(0,1);
           isReady.push(true);
           console.log(collections);
        },function (error){    
            console.log(error.data);
        });
    };
    
    ws.onMessage(function(event) {
        try {
            var response = JSON.parse(event.data);
            //console.log(response);
            if(isReady[0]===false){
                console.log("Not ready");
                return;
            }
            if(response.infotype==="two"){
                angular.forEach(collections[0],function(obj,objKey){
                    if(obj.internalcode===response.internalcode){
                        console.log("Show info of " + obj.stocksname);
                        console.log(response.data);
                        console.log(obj.data);
                        
                        angular.forEach(response.data,function(eachLevel,eachLevelKey){
                            //console.log(eachLevelKey);
                            //console.log(eachLevel);
                            //console.log(eachLevel.bid);
                            obj.data[eachLevelKey].boxcolors[0]= changeBoxColor(obj.data[eachLevelKey].bidqueue,eachLevel.bidqueue);
                            obj.data[eachLevelKey].boxcolors[1]= changeBoxColor(obj.data[eachLevelKey].bidqty,eachLevel.bidqty);
                            obj.data[eachLevelKey].boxcolors[2]= changeBoxColor(obj.data[eachLevelKey].bid,eachLevel.bid);
                            obj.data[eachLevelKey].boxcolors[3]= changeBoxColor(obj.data[eachLevelKey].askqueue,eachLevel.askqueue);
                            obj.data[eachLevelKey].boxcolors[4]= changeBoxColor(obj.data[eachLevelKey].askqty,eachLevel.askqty);
                            obj.data[eachLevelKey].boxcolors[5]= changeBoxColor(obj.data[eachLevelKey].ask,eachLevel.ask);
                            
                            obj.data[eachLevelKey].numbercolors[0]= changeNumberColor(obj.data[eachLevelKey].bidqueue,eachLevel.bidqueue,obj.data[eachLevelKey].numbercolors[0]);
                            obj.data[eachLevelKey].numbercolors[1]= changeNumberColor(obj.data[eachLevelKey].bidqty,eachLevel.bidqty,obj.data[eachLevelKey].numbercolors[1]);
                            obj.data[eachLevelKey].numbercolors[2]= changeNumberColor(obj.data[eachLevelKey].bid,eachLevel.bid,obj.data[eachLevelKey].numbercolors[2]);
                            obj.data[eachLevelKey].numbercolors[3]= changeNumberColor(obj.data[eachLevelKey].askqueue,eachLevel.askqueue,obj.data[eachLevelKey].numbercolors[3]);
                            obj.data[eachLevelKey].numbercolors[4]= changeNumberColor(obj.data[eachLevelKey].askqty,eachLevel.askqty,obj.data[eachLevelKey].numbercolors[4]);
                            obj.data[eachLevelKey].numbercolors[5]= changeNumberColor(obj.data[eachLevelKey].ask,eachLevel.ask,obj.data[eachLevelKey].numbercolors[5]);
                            
                            obj.data[eachLevelKey].bidqueue=eachLevel.bidqueue;
                            obj.data[eachLevelKey].bidqty=eachLevel.bidqty;
                            obj.data[eachLevelKey].bid=eachLevel.bid;
                            obj.data[eachLevelKey].askqueue=eachLevel.askqueue;
                            obj.data[eachLevelKey].askqty=eachLevel.askqty;
                            obj.data[eachLevelKey].ask=eachLevel.ask;

                            
                            
                        });
                    }
                });
            }
            

        } catch(e) {
          console.log("Error: "+e);
        }
        
    });
    ws.onError(function(event) {
        console.log('connection Error', event);
    });

    ws.onClose(function(event) {
        console.log('connection closed', event);
    });

    ws.onOpen(function() {
        console.log('connection open');
        ws.send("100");
    });
    return{
        collections: collections,
        getLevelTwoInfo:function(){
            getLevelTwoInfo();
        },
        status: function() {
          return ws.readyState;
        },
        send: function(message) {
          if (angular.isString(message)) {
            ws.send(message);
          }
          else if (angular.isObject(message)) {
            ws.send(JSON.stringify(message));
          }
        }        
    };
});
app.controller('leveltwoinfoController',function($scope,$http,$websocket,leveltwoinfoFactory){
    $scope.getLevelTwoInfo=leveltwoinfoFactory.getLevelTwoInfo();
    $scope.collections = leveltwoinfoFactory.collections;
});