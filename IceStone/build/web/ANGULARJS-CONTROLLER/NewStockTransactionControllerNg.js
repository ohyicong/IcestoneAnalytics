/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var app = angular.module('homeApp');

app.controller('stocktransactionController',function($scope,$interval,$http,popupService,$window){
    $scope.searchName="";
    $scope.substockResults=[];
    $scope.substockTransactionInfos=[];
    $scope.internalCode=0;
    $scope.stocksName="";
    $scope.updatedTime=new Date().toLocaleTimeString();
    $scope.sellQty=0;
    $scope.buyQty=0;
    $scope.sellQtyPercent="0%";
    $scope.buyQtyPercent="100%";
    $scope.volumeCountSell={};
    $scope.volumeCountBuy={};
    $scope.volumeCountSellGraph=[];
    $scope.volumeCountBuyGraph=[];
    $scope.page=[true,false,false,false];
    var isInit=false;
    var isChanged=false;
    $scope.selectedTrans=[];
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
    $scope.getTransaction=function(internalCode,stocksName){
        isInit=true;
        $scope.internalCode = internalCode;
        $scope.stocksName=stocksName;
        $scope.sellQty=0;
        $scope.buyQty=0;
        $scope.sellQtyPercent="0%";
        $scope.buyQtyPercent="100%";
        $scope.volumeCountSell={};
        $scope.volumeCountBuy={};
        $http({
                method: 'POST',
                url: 'GetStockTransactionServlet',
                data:{
                    internalCode:internalCode
                }

            }).then(function (success){               
                $scope.substockTransactionInfos =success.data;
                $scope.updatedTime=new Date().toLocaleTimeString();
                angular.forEach($scope.substockTransactionInfos,function(obj,key){
                   if(obj.transtype==="b"){
                       $scope.buyQty+=obj.quantity;
                       if(typeof $scope.volumeCountBuy[obj.quantity]==='undefined'){
                           $scope.volumeCountBuy[obj.quantity]={
                               count:1,
                               trans:[obj]
                           };
                       }else{
                           $scope.volumeCountBuy[obj.quantity].count++;
                           $scope.volumeCountBuy[obj.quantity].trans.push(obj);
                       }
                   } else if(obj.transtype==="s"){
                       $scope.sellQty+=obj.quantity;
                       if(typeof $scope.volumeCountSell[obj.quantity]==='undefined'){
                          $scope.volumeCountSell[obj.quantity]={
                               count:1,
                               trans:[obj]
                           };
                       }else{
                           $scope.volumeCountSell[obj.quantity].count++;
                           $scope.volumeCountSell[obj.quantity].trans.push(obj);
                       }
                   }
                });
                $scope.sellQtyPercent=($scope.sellQty/($scope.sellQty+$scope.buyQty))*100+"%";
                $scope.buyQtyPercent=($scope.buyQty/($scope.sellQty+$scope.buyQty))*100+"%";
            },function (error){    
                console.log(error.data);
        });
    };
    
    $scope.onTrans=function(){
        $scope.page=[true,false,false,false];
    };
    $scope.onBuy=function(){
        if(isInit){
            $scope.page=[false,true,false,false];
            
        }else{
            window.alert("Load information first");
        }
    };
    $scope.onSell=function(){
        if(isInit){
            $scope.page=[false,false,true,false];
            
        }else{
            window.alert("Load information first");
        }
    };
    $scope.onSelect=function(obj,key){
        $scope.selectedTrans=obj[key].trans;
        $scope.page=[false,false,false,true];
    };
});