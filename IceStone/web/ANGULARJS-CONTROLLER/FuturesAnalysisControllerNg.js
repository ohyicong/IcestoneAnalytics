var app = angular.module("futuresanalysisApp",['ngWebSocket','ngNotify']);
app.factory('futuresanalysisFactory',function($http,$websocket){
    var ws = $websocket('ws://'+location.host+'/IceStone/stocktimelinegraphsocket');
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
    // setTimeout(function() {
    //   ws.close();
    // }, 500)

    return {
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
}).controller("futuresanalysisController",function($scope,$http,$interval,futuresanalysisFactory,ngNotify){
    
    ngNotify.config({
        theme: 'pure',
        position: 'bottom',
        duration: 3000,
        type: 'error',
        sticky: true,
        button: true,
        html: false
    });
    $scope.getSubStock=function(){
        ngNotify.set('Your notification message goes here!'); 
        $http({
                method: 'POST',
                url: 'GetSubStockServlet'

            }).then(function (success){               
                console.log("GetSubStock: "+success.data);
                $scope.substockResults =success.data;
            },function (error){    
                console.log(error.data);
            });
            
    };
});