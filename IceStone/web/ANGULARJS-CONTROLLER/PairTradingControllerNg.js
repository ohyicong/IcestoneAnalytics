var app = angular.module("pairtradingApp",['ngWebSocket','ngNotify']);
app.factory('pairtradingFactory',function($http,$websocket,ngNotify){
    //ngNotify package
    ngNotify.config({
        theme: 'pure',
        position: 'bottom',
        duration: 3000,
        type: 'error',
        sticky: false,
        button: true,
        html: false
    });
    var message;
    var pairTradingStocks=[];
    var deletePairTrading = function(internalcodeone,internalcodetwo,percentage){
        $http({
                method: 'POST',
                url: 'DeletePairTradingServlet',
                data:{
                    internalCodeOne: internalcodeone,
                    internalCodeTwo: internalcodetwo,
                    percentage:percentage
        
                }
            }).then(function (success){               
                angular.forEach(pairTradingStocks,function(obj,key){
                    if(obj.internalcodesone===internalcodeone&&obj.internalcodestwo===internalcodetwo){
                        pairTradingStocks.splice(key,1);
                        console.log("PairTrading removed key:"+key);
                    }
                });
                console.log(success);
            },function (error){    
                console.log(error.data);
            }); 
    };
    var getPairTradingStocksInfo=function(){
        $http({
                method: 'POST',
                url: 'GetPairTradingServlet'                
            }).then(function (success){               
                message = success.data;
                angular.forEach(message.data,function(value,key){
                    pairTradingStocks.push(value);
                });
            },function (error){    
                console.log(error.data);
            });
    };
    
    var addPairTrading = function(internalcodeone,internalcodetwo,percentage){
        if(internalcodeone===internalcodetwo){
            ngNotify.set("Pair trading on same stocks are not allowed");
            return;
        }
        if(internalcodeone===null ||internalcodetwo===null){
            ngNotify.set("Please complete the form ");
            return;
        }

        $http({
                method: 'POST',
                url: 'AddPairTradingServlet',
                data:{
                    internalCodeOne: internalcodeone,
                    internalCodeTwo: internalcodetwo,
                    percentage:percentage
                }
            }).then(function (success){               
                console.log(success);
                pairTradingStocks.splice(0,pairTradingStocks.length);
                getPairTradingStocksInfo();
            },function (error){    
                console.log(error.data);
            });  
    };
    
    
    var ws = $websocket('ws://'+location.host+'/IceStone/stocktimelinegraphsocket');
    ws.onMessage(function(event) {
        try {
            response = JSON.parse(event.data);
            if(response.infotype==="one"){
                angular.forEach(pairTradingStocks,function(obj,key){
                    //New level one information, preprocessing of information for user
                    if(response.internalcode===obj.internalcodesone){
                            obj.lastpriceone=response.data.lastprice;
                            obj.currentdifference= Math.abs(obj.lastpriceone-obj.lastpricetwo);                            
                    }else if(response.internalcode===obj.internalcodestwo){
                            obj.lastpricetwo=response.data.lastprice;
                            obj.currentdifference= Math.abs(obj.lastpriceone-obj.lastpricetwo);
                          
                    }
                    var cal =(Math.abs(obj.currentdifference-obj.olddifference)/obj.olddifference)*100;
                    if( cal >= obj.percentage && obj.color !=="pink" ){
                        obj.color ="pink";
                        ngNotify.set("Please check trading pair "+ obj.stocksnameone+" & "+obj.stocksnametwo);
                    }else if (cal >= obj.percentage){
                        obj.color ="pink";
                    }else{
                        obj.color ="none";
                    }
                }
                );
            };
            
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
    // setTimeout(function() {
    //   ws.close();
    // }, 500)

    return {
        deletePairTrading:function(internalcodeone,internalcodetwo,percentage){
            deletePairTrading(internalcodeone,internalcodetwo,percentage);  
        },
        pairTradingStocks:pairTradingStocks,
        getPairTradingStocksInfo:function(){
            getPairTradingStocksInfo();
        },
        addPairTrading:function(internalcodeone,internalcodetwo,percentage){
            addPairTrading(internalcodeone,internalcodetwo,percentage);  
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
}).controller("pairtradingController",function($scope,$http,$interval,pairtradingFactory,ngNotify){
    ngNotify.config({
        theme: 'pure',
        position: 'bottom',
        duration: 3000,
        type: 'error',
        sticky: false,
        button: true,
        html: false
    });
    $scope.percentage=null;
    $scope.pairTradingStocks = pairtradingFactory.pairTradingStocks;

    $scope.getSubStock=function(){
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
    var count=0;
    $scope.stock=[{
        stocksname:"",
        internalcode:"",
        color:""
    },{
        stocksname:"",
        internalcode:"",
        color:""
    }];
    $scope.cancel = function(){
        count=0;
        $scope.stock=[{
            name:"",
            internalcode:"",
            color:""
        },{
            name:"",
            internalcode:"",
            color:""
        }];  
        $scope.percentage=null;
    };
    $scope.addPairTradingStock = function(internalcode,stocksname){
        if(count===0){
            $scope.stock[0]={
                stocksname:stocksname,
                internalcode:internalcode,
                color:"solid"
            };
            count++;
        }else if (count===1){
            $scope.stock[1]={
                stocksname:stocksname,
                internalcode:internalcode,
                color:"solid"
            };
        }
    };
    $scope.addPairTrading = function(){
        //Inform user on uncompleted information
        if($scope.percentage===null||$scope.percentage===0){
            ngNotify.set("Alert Margin cannot be 0");
            return;
        }
        //Add new trading pair
        pairtradingFactory.addPairTrading($scope.stock[0].internalcode,$scope.stock[1].internalcode,$scope.percentage);
        $scope.cancel();
    };
    $scope.getPairTradingStocksInfo = function(){
        pairtradingFactory.getPairTradingStocksInfo();
    };
    $scope.deletePairTrading=function(internalcodeone,internalcodetwo,percentage){
        pairtradingFactory.deletePairTrading(internalcodeone,internalcodetwo,percentage);
        console.log(percentage);
    };
});