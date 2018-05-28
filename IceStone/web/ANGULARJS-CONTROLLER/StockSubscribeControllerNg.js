
/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var app= angular.module('homeApp');
app.controller('stocksubscribeController',function($scope,$http,$interval,ngNotify){
    ngNotify.config({
        theme: 'pure',
        position: 'bottom',
        duration: 3000,
        type: 'error',
        sticky: false,
        button: true,
        html: false
    });
    var promise;
    $scope.testing="stocksubscribeController";
    $scope.substockResults=[];
    $scope.searchName=" ";
    $scope.searchResults=[];
    $scope.isQueried=false;
    
    $scope.subData=[];
    $scope.subData.internalCode="";
    $scope.subData.stockName="";
    $scope.subData.exchangeSymbol="";
    
    $scope.searchForSubStock=function(){
        console.log($scope.searchName);
        $http({
            method: 'POST',
            url: 'SearchForSubStockServlet',
            data:{
                stockName:$scope.searchName
            }
           
        }).then(function (success){
            //JSON format
            $scope.searchResults= success.data;
            $scope.isQueried=true;
        },function (error){    
            console.log(error.data);
        });
    };
    $scope.callFillName=function (name,code,symbol){
        $scope.subData.stockName=name;
        $scope.subData.internalCode=code;
        $scope.subData.exchangeSymbol=symbol;
        $scope.searchName=name+" "+symbol;
        $scope.isQueried=false;
    };
    $scope.isSearchLegit =function(){
        var isLegit=true;
        if($scope.searchName===$scope.subData.stockName+" "+$scope.subData.exchangeSymbol){
            angular.forEach($scope.substockResults,function(substockValue,substockKey){
                console.log(substockValue);
                if($scope.searchName===(substockValue.stocksname+" "+substockValue.exchangesymbol)){
                    ngNotify.set(substockValue.stocksname+" "+substockValue.exchangesymbol+" subscibed already");
                    isLegit= false;              
                }
            });
            return isLegit;
        }else{
            ngNotify.set("Stock name incorrect");
            return false;
        }
    };
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
    $scope.subscribeStock=function(){
        console.log($scope.subData.internalCode+" "+$scope.subData.exchangeSymbol+" "+$scope.subData.stockName);
        if($scope.isSearchLegit()===true){           
            $http({
                method: 'POST',
                url: 'SubStockServlet',
                data:{
                    internalCode:$scope.subData.internalCode,
                    stockName:$scope.subData.stockName,
                    exchangeSymbol:$scope.subData.exchangeSymbol
                }

            }).then(function (success){
                //JSON format
                $scope.searchResults= success.data;
                $scope.isQueried=true;
                $scope.getSubStock();
                console.log(success.data);
            },function (error){    
                console.log(error.data);
            });
        }
        $scope.resetSubVar();
        
    };
    
    $scope.deleteStock=function(code,name,symbol){
        console.log(code+" "+name+" "+symbol);          
        $http({
            method: 'POST',
            url: 'DeleteStockServlet',
            data:{
                internalCode:code,
                stockName:name,
                exchangeSymbol:symbol
            }

        }).then(function (success){
            console.log(success.data);
            $scope.getSubStock();
        },function (error){    
            console.log(error.data);
        });
        
    };
    
    $scope.resetSubVar = function(){
        $scope.subData.internalCode="";
        $scope.subData.exchangeSymbol="";
        $scope.subData.stockName="";
        $scope.searchName=" ";
        $scope.searchResults=[];
        $scope.isQueried=false;
    };
    $scope.$watch('$viewcontentloaded',function(){
        console.log("SubStock content loaded");
        promise = $interval($scope.getSubStock,5000);   
    });
    $scope.$on('$destroy',function(){
        console.log("SubStock content destroyed");
        $interval.cancel(promise);
    });  
});
