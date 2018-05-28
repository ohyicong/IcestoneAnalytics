angular.module('volumebreakthroughApp',['ngWebSocket','ngNotify','ngMaterial'])
.factory('volumebreakthroughFactory',function($http,$websocket,ngNotify){
    var ws = $websocket('ws://'+location.host+'/IceStone/stocktimelinegraphsocket');
    var isReady=[false];
    var collection=[];
    var changeColor = function(name,old,current){
        if(current>=old){
            return "pink";
        }else{
            return "transparent";
        }
    };
    var getVolume = function(){
        console.log("Getting volume...");
        $http({
            method: 'POST',
            url: 'GetVolumeBreakThroughServlet'
                
        }).then(function (success){               
           console.log(success.data);
           collection.push(success.data);
           isReady.splice(0,1);
           isReady.push(true);
           console.log(collection);
        },function (error){    
            console.log(error.data);
        });
    };
    
    ws.onMessage(function(event) {
        try {
            response = JSON.parse(event.data);
            if(isReady[0]===false){
                console.log("Not ready");
                return;
            }
            if(response.infotype==="one"){
                angular.forEach(collection[0],function(obj,objKey){
                    if(obj.internalcode===response.internalcode){
                        obj.volumetraded = response.data.volumetraded;
                        if(obj.color!=='pink'){
                            obj.color = changeColor(obj.stockname,obj.avgvolume,response.data.volumetraded);
                        }
                        obj.difference= Math.abs(obj.avgvolume-response.data.volumetraded);
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
        collection: collection,
        getVolume:function(){
            getVolume();
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
}).controller('volumebreakthroughController',function($scope,volumebreakthroughFactory){
    $scope.collection=volumebreakthroughFactory.collection;
    $scope.getVolume = volumebreakthroughFactory.getVolume();
    $scope.isFiltered=false;
    $scope.filterColor = function(element){
        if(element.avgvolume<=element.volumetraded){
            return true;
        }else
            return false;
    };
    $scope.toggleView=function(){
        $scope.isFiltered=!$scope.isFiltered;
        console.log("Filtered view:"+$scope.isFiltered);
    };
    $scope.getProgress = function(avgVol,currentVol){
        if(currentVol<avgVol){
            return (currentVol/avgVol)*100;
        }else{
            return 100;
        }
    };
});