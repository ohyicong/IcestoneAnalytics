/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

angular.module('homeApp')
.factory('overviewsocket',function($websocket,$window,$interval,$timeout){
    var ws = $websocket('ws://'+location.host+'/IceStone/overviewsocket');
    var stocklevelone = [];
    var response=[];
    var isSqlRecordingOn=[{data:"ON"}];
    var tickTable=[];
    var timesgd=[];
    var checkColor = function(old,updated){
        if(updated>old){
            return "lime";
        }else if (updated<old){
            return "pink";
        }else
            return "white";
    };
    var checkBidAskChangeColor = function(change){
        if(change<0){
            return "pink";
        }else if (change>0){
            return "lime";
        }else 
            return "white";
    };
    
    
    var addStock = function(response){
        stocklevelone.push({
            internalcode:response.internalcode,
            exchangesymbol:response.exchangesymbol,
            stockname:response.stockname,
            lastprice:response.lastprice,
            closingprice:response.closingprice,
            bestbid:response.bestbid,
            bestbidqty:response.bestbidqty,
            bestask:response.bestask,
            bestaskqty:response.bestaskqty,
            bestbidchange:response.bestbidchange,
            bestaskchange:response.bestaskchange,
            tradingstatus:response.tradingstatus,
            color1: checkBidAskChangeColor(response.bestbidchange),
            color2: checkBidAskChangeColor(response.bestaskchange)
        });  
        
    };
    var updateStock = function(response,value){
        if(response.tradingstatus.includes("1")){
            value.bestbidchange=getTicksChange(value.closingprice,response.bestbid);
            value.bestaskchange=getTicksChange(value.closingprice,response.bestask);
        }else{
            value.bestbidchange=getTicksChange(value.lastprice,response.bestbid);
            value.bestaskchange=getTicksChange(value.lastprice,response.bestask);
        }
        value.lastprice=response.lastprice;
        value.closingprice=response.closingprice;
        value.bestbid=response.bestbid;
        value.bestbidqty=response.bestbidqty;
        value.bestask=response.bestask;
        value.bestaskqty=response.bestaskqty;
        value.tradingstatus=response.tradingstatus;
        value.color1=checkBidAskChangeColor(value.bestbidchange);
        value.color2=checkBidAskChangeColor(value.bestaskchange);
        
    };
    var roundOff = function (val){
        if(val>0){
            val = Math.abs(val);
            var difference = val - Math.floor(val);
            if(difference<0.0005){
                return Math.floor(val);
            }else
                return Math.ceil(val);
        }else{
            val = Math.abs(val);
            var difference = val - Math.floor(val); 
            if(difference<0.0005){
                return -Math.floor(val);
            }else
                return -Math.ceil(val);
        }
    };
    
    var getTicksChange = function (old,current){
        var currentKey=0,oldKey=0;
        var isTrue=false;
        angular.forEach(tickTable[0],function(obj,objkey){
            if(current>obj.lowerbound&&!isTrue){
                currentKey=objkey;
                isTrue=true;
            }
        });
        isTrue=false;
        angular.forEach(tickTable[0],function(obj,objkey){
            if(old>obj.lowerbound&&!isTrue){
                oldKey=objkey;
                isTrue=true;
            }
        });
        if(oldKey===currentKey){
            var difference =  current-old;
            var approxTicks = difference/(tickTable[0][currentKey].priceincrement);
            return (roundOff(approxTicks));
        }else{
            var oldDifference = Math.abs(old-tickTable[0][oldKey].lowerbound);
            var oldApproxTicks= oldDifference/(tickTable[0][oldKey].priceincrement);
            var currentDifference = Math.abs(current-tickTable[0][currentKey].lowerbound);
            var currentApproxTicks= currentDifference/(tickTable[0][currentKey].priceincrement);
            return roundOff(oldApproxTicks+currentApproxTicks);
        }
        
    };
    
    
    ws.onMessage(function(event) {
        try {
            response = JSON.parse(event.data);
            if(response.sqlstatus){
                isSqlRecordingOn.splice(0,1);
                isSqlRecordingOn.push({
                    data:"ON"
                });
                //console.log("ON");
            }else{
                isSqlRecordingOn.splice(0,1);
                isSqlRecordingOn.push({
                    data:"OFF"
                });
                //console.log("OFF");
            }
            if(response.status==="delete"){
                angular.forEach(stocklevelone, function (value,key){
                   if(value.internalcode===response.levelone.internalcode){
                        console.log("deleted");
                        stocklevelone.splice(key,1);
                   }
                });
            }else if(response.status==="init"){
                console.log("init stock");
                stocklevelone.splice(0,stocklevelone.length);
                tickTable.splice(0,tickTable.length);
                tickTable.push(response.ticktable);
                angular.forEach(response.levelone,function(value,key){                    
                    addStock(value);
                });              
            }else{
                var isUpdated =false;
                //console.log("Updating stock");
                angular.forEach(stocklevelone, function (value,key){
                   if(value.internalcode===response.levelone.internalcode){
                       updateStock(response.levelone,value);
                       isUpdated=true;
                   }
                });
                if(!isUpdated){
                    addStock(response.levelone);
                }
            }
            timesgd.splice(0,timesgd.length);
            timesgd.push(response.timesgd);

        } catch(e) {
          console.log("Error: "+e);
        }
        
    });
    ws.onError(function(event) {
        console.log('connection Error', event);
        window.alert("Web socket error");
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
      timesgd:timesgd,
      isSqlRecordingOn:isSqlRecordingOn,
      stocklevelone: stocklevelone,
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
})
.controller('overviewController',function($scope,$http,$interval,overviewsocket){
    $scope.timesgd=overviewsocket.timesgd;
    $scope.testing="overviewController";
    $scope.options=[{type:"Name",func:"stockname"},{type:"Bid Change",func:"-bestbidchange"},{type:"Ask Change",func:"-bestaskchange"}];
    $scope.selected=$scope.options[0];
    $scope.stocklevelone=overviewsocket.stocklevelone;
    $scope.isSqlRecordingOn=overviewsocket.isSqlRecordingOn;
    $scope.endFeedosConnection=function(){    
        $http({
            method: 'GET',
            url: 'FeedosDisconnectionServlet'
        }).then(function (success){
            console.log(success.data);
            window.location.href= "http://localhost:8084/IceStone";       
        },function (error){
            console.log(error.data);
            console.log("Error Disconnecting");
        });
    };
    $scope.ToggleSqlRecording = function (isOn){
        if(isOn==="ON"){
            $scope.isSqlRecordingOn.splice(0,1);
            $scope.isSqlRecordingOn.push({
                data:"OFF"
            });
            overviewsocket.send("200");
        }else{
            $scope.isSqlRecordingOn.splice(0,1);
            $scope.isSqlRecordingOn.push({
                data:"ON"
            });
            overviewsocket.send("201");
        }
    };
    
});






