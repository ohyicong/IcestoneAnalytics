var app = angular.module("stocksrightsApp",['ngWebSocket']);
app.factory('stocksrightsFactory',function($http,$websocket){
    var response;
    var stocksRightsInfo=[];
    var getStocksRights = function(){
        console.log("StocksRightsStocks:");
        $http({
                method: 'POST',
                url: 'GetStocksRightsServlet'
            }).then(function (success){               
                console.log(success.data);
                stocksRightsInfo.splice(0,stocksRightsInfo.length);
                angular.forEach(success.data,function(obj,key){
                   stocksRightsInfo.push(obj);
                });
                console.log("100 sent");
                ws.send("100");
            },function (error){    
                console.log(error.data);
            }); 
            
        };
    var calculateSpread = function(inputs,updates){
        if(updates.internalcode===inputs.internalcodesone){
            var amtFromSellingShares = updates.bestbid-((updates.bestbid *(inputs.brokercomm+inputs.accessfee+inputs.clearingfee)/100)); 
            var amtFromBuyingRights = inputs.stockdatatwo.levelone.bestask+((inputs.stockdatatwo.levelone.bestask *(inputs.brokercomm+inputs.accessfee+inputs.clearingfee)/100))+inputs.subscriptionprice;
            inputs.spread=amtFromSellingShares-amtFromBuyingRights;
            console.log("AmtfromSellingShares: "+amtFromSellingShares+" AmtFromBuyingRights:"+amtFromBuyingRights);
            return (inputs.spread) ;
        }else{
            var amtFromSellingShares = inputs.stockdataone.levelone.bestbid-((inputs.stockdataone.levelone.bestbid*(inputs.brokercomm+inputs.accessfee+inputs.clearingfee)/100));
            var amtFromBuyingRights = updates.bestask-((updates.bestask *(inputs.brokercomm+inputs.accessfee+inputs.clearingfee)/100))+inputs.subscriptionprice;
            console.log("AmtfromSellingShares: "+amtFromSellingShares+" AmtFromBuyingRights:"+amtFromBuyingRights);
            inputs.spread=amtFromSellingShares-amtFromBuyingRights;
            return (inputs.spread) ; 
        }
    };    
    
    
    var ws = $websocket('ws://'+location.host+'/IceStone/overviewsocket');
    ws.onMessage(function(event) {
        try {
            response = JSON.parse(event.data);      
            if(response.status==="update"){
                console.log("update");
               // console.log(stocksRightsLevelOne[response.levelone.internalcode]);
                angular.forEach(stocksRightsInfo,function(obj,key){
                    if(response.levelone.internalcode===obj.internalcodesone){
                        obj.stockdataone = response;   
                        calculateSpread(obj,response.levelone);
                    } else if(response.levelone.internalcode===obj.internalcodetwo){
                        obj.stockdatatwo = response.levelone;   
                        calculateSpread(obj,response);
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
        getStocksRights();

    });
    // setTimeout(function() {
    //   ws.close();
    // }, 500)

    return {
        stocksRightsInfos:stocksRightsInfo,
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
}).controller("stocksrightsController",function($scope,$http,$interval,stocksrightsFactory){
    $scope.substockResults=[];
    $scope.stockSelected=[];
    $scope.rightSelected=[];
    $scope.totalcost=0;
    $scope.period=0;
    $scope.pageShow=[true,false,false];
    $scope.stocksRightsInfos=stocksrightsFactory.stocksRightsInfos;
    $scope.inputs={brokercomm:'',accessfee:'',clearingfee:'',gst:7,fundingborrowed:'',fundingfee:'',sharesborrowed:'',borrowingfee:'',subscriptionprice:'',startdate:'',enddate:''};
    $scope.addClicked = function(){
        $scope.pageShow=[false,true,false];
    };
    $scope.submitClicked = function(){
        $scope.period = Math.ceil(Math.abs(new Date($scope.inputs.startdate)- new Date($scope.inputs.enddate))/86400000);
        $scope.totalcost=($scope.inputs.fundingborrowed*($scope.inputs.fundingfee/100.0))*($scope.period/365)+ ($scope.inputs.sharesborrowed*$scope.stockSelected.lastprice*($scope.inputs.borrowingfee/100.0))*($scope.period/365);
        var isIncomplete=false;
        angular.forEach($scope.inputs,function(obj,key){
           if(obj===''){
               isIncomplete=true;
           } 
           
        });
        if($scope.stockSelected===''||$scope.rightSelected===''){
             isIncomplete=true;
        }
        if(isIncomplete){
            window.alert("Incomplete form");
            return;
        }
        $scope.pageShow=[false,false,true];
    };
    $scope.confirmClicked = function(){
        $scope.pageShow=[true,false,false];
        $http({
                method: 'POST',
                url: 'AddStocksRightsServlet',
                data:{
                    internalCodesOne: $scope.stockSelected.internalcode,
                    internalCodesTwo: $scope.stockSelected.internalcode,
                    brokerComm: $scope.inputs.brokercomm,
                    accessFee:$scope.inputs.accessfee,
                    clearingFee:$scope.inputs.clearingfee,
                    gst:$scope.inputs.gst,
                    fundingBorrowed:$scope.inputs.fundingborrowed,
                    fundingFee:$scope.inputs.fundingfee,
                    sharesBorrowed:$scope.inputs.sharesborrowed,
                    borrowingFee:$scope.inputs.borrowingfee,
                    lastPrice:$scope.inputs.lastprice,
                    period:$scope.period,
                    subscriptionprice: $scope.inputs.subscriptionprice
                }
            }).then(function (success){               
                console.log(success);
            },function (error){    
                console.log(error.data);
            });  
        $scope.stockSelected=[];
        $scope.rightSelected=[];
        $scope.totalcost=0;
        $scope.period=0;
        $scope.inputs={brokercomm:'',accessfee:'',clearingfee:'',gst:7,fundingborrowed:'',fundingfee:'',sharesborrowed:'',borrowingfee:'',subscriptionprice:'',startdate:'',enddate:''};
        $scope.getStocksRights();
    };
    
    $scope.cancelClicked  = function(){
        $scope.pageShow=[true,false,false];
        $scope.stockSelected=[];
        $scope.rightSelected=[];
        $scope.inputs={brokercomm:'',accessfee:'',clearingfee:'',gst:7,fundingborrowed:'',fundingfee:'',sharesborrowed:'',borrowingfee:'',subscriptionprice:'',startdate:'',enddate:''};
    };
    $scope.openGraph = function (internalCodesOne,internalCodesTwo){
        console.log("Send over");
        console.log(internalCodesOne+" "+internalCodesTwo);
        var popup = window.open("stocksrightsgraph.jsp");
        popup.internalCodesOne=internalCodesOne;
        popup.internalCodesTwo=internalCodesTwo;
    };
});