var app = angular.module("stocksrightsgraphApp",['ngWebSocket','ngNotify']);

app.factory('stocksrightsgraphFactory',function($http,$websocket,ngNotify){
    var stocksRights=[];
    var calculatedVar=[];
    var history=[];
    var isTriggered=[false];
    var getStocksRightsGraph = function(){
        console.log("StocksRightsStocksGraph: ");
        $http({
                method: 'POST',
                url: 'GetStocksRightsGraphServlet',
                data:{
                    internalCodeOne:window.internalCodesOne,
                    internalCodeTwo:window.internalCodesTwo
                }
            }).then(function (success){               
                console.log("What happened");
                var stocksRightsInfo=success.data;
                console.log(stocksRightsInfo);
                var transactionPercent = stocksRightsInfo.brokercomm + stocksRightsInfo.accessfee + stocksRightsInfo.gst + stocksRightsInfo.clearingfee;
                var fundingCost = stocksRightsInfo.fundingborrowed * stocksRightsInfo.fundingfee/100.0;
                var borrowingCost = stocksRightsInfo.sharesborrowed* stocksRightsInfo.lastprice * stocksRightsInfo.borrowingfee/100.0;               
                if(stocksRightsInfo.stockinfo.bestbidqty>stocksRightsInfo.rightsinfo.bestaskqty){
                    var volumeUsed = stocksRightsInfo.rightsinfo.bestaskqty;
                }else{
                    var volumeUsed =stocksRightsInfo.stockinfo.bestbidqty;
                }
                var spread = (stocksRightsInfo.stockinfo.bestbid-(stocksRightsInfo.stockinfo.bestbid * transactionPercent/100))-(stocksRightsInfo.rightsinfo.bestask*transactionPercent/100)-stocksRightsInfo.subscriptionprice-stocksRightsInfo.rightsinfo.bestask;
                var netProfit = (spread * volumeUsed) - borrowingCost - fundingCost;
                calculatedVar.push({
                    transactionpercent:transactionPercent,
                    fundingcost:fundingCost,
                    borrowingcost:borrowingCost,
                    spread: spread,
                    volumeused: volumeUsed,
                    netprofit:netProfit
                });
                stocksRights.push(success.data);
                ws.send("100");
            },function (error){    
                console.log(error.data);
            }); 
            
        };
    var ws = $websocket('ws://'+location.host+'/IceStone/overviewsocket');
    ws.onMessage(function(event) {
        try {
            response = JSON.parse(event.data);
            
            if(response.status==="update"){
                
                if(response.levelone.internalcode===stocksRights[0].internalcodesone){
                    //Stock 
                    console.log("updating stock");
                    stocksRights[0].stockinfo = response.levelone;
                    if(stocksRights[0].stockinfo.bestbidqty>stocksRights[0].rightsinfo.bestaskqty){
                        var volumeUsed = stocksRights[0].rightsinfo.bestaskqty;
                    }else{
                        var volumeUsed =stocksRights[0].stockinfo.bestbidqty;
                    }
                    var spread =(stocksRights[0].stockinfo.bestbid * (100-calculatedVar[0].transactionpercent)/100)-(stocksRights[0].rightsinfo.bestask*(100+calculatedVar[0].transactionpercent)/100)-stocksRights[0].subscriptionprice;
                    console.log(spread);
                    calculatedVar[0].volumeused=volumeUsed;
                    calculatedVar[0].spread=spread;
                    var netProfit = (spread * volumeUsed) - calculatedVar[0].borrowingcost - calculatedVar[0].fundingcost;
                    calculatedVar[0].netprofit=netProfit;
                    console.log("NetProfit");
                    console.log(netProfit);
                    if(netProfit<0){
                        if(!isTriggered[0]){
                            history.push({
                                stocksRights:stocksRights[0],
                                calculatedVar:calculatedVar[0]
                            });
                        }
                    }
                }else if(response.levelone.internalcode===stocksRights[0].internalcodestwo){
                    console.log("updating rights");
                    stocksRights[0].rightsinfo = response.levelone;
                    if(stocksRights[0].stockinfo.bestbidqty>stocksRights[0].rightsinfo.bestaskqty){
                        var volumeUsed = stocksRights[0].rightsinfo.bestaskqty;
                    }else{
                        var volumeUsed =stocksRights[0].stockinfo.bestbidqty;
                    }
                    var spread = (stocksRights[0].stockinfo.bestbid * (100-calculatedVar[0].transactionpercent)/100)-(stocksRights[0].rightsinfo.bestask*(100+calculatedVar[0].transactionpercent)/100)-stocksRights[0].subscriptionprice;
                    calculatedVar[0].spread=spread;
                    calculatedVar[0].volumeused=volumeUsed;
                    var netProfit = (spread * volumeUsed) - calculatedVar[0].borrowingcost - calculatedVar[0].fundingcost;
                    calculatedVar[0].netprofit=netProfit;
                    console.log("NetProfit");
                    console.log(netProfit);
                    if(netProfit<0){
                        console.log("recording history");
                        console.log(history);
                        if(isTriggered[0]===false){
                            history.push({
                                stocksRights:stocksRights[0],
                                calculatedVar:calculatedVar[0]
                            });
                            isTriggered[0]=true;
                        }
                        
                    }else{
                        isTriggered[0]=false;
     
                    }
                }
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
        getStocksRightsGraph();

    });
    return {
        history:history,
        stocksRights:stocksRights,
        calculatedVar:calculatedVar,
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
}).controller("stocksrightsgraphController",function($scope,$http,$interval,stocksrightsgraphFactory,ngNotify){
    $scope.stocksRights=stocksrightsgraphFactory.stocksRights;
    $scope.calculatedVar =stocksrightsgraphFactory.calculatedVar; 
    $scope.transactionfee=0;
    $scope.history=stocksrightsgraphFactory.history;
});