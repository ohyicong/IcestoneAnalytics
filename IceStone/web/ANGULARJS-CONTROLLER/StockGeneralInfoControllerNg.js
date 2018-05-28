/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var app = angular.module('homeApp');
app.service('popupService',function(){
    this.popupFunction = function (internalCode,stocksName,isLive){
        console.log("help "+isLive);
        if(isLive){
            console.log("Live Services"+internalCode);
            var popUp = window.open('stocktimelinegraph.jsp');
            popUp.sharedInternalCode = internalCode;
            popUp.sharedStocksName=stocksName;
        }else{
            console.log("Offline Services"+internalCode);
            var popUp = window.open('offlinestocktimelinegraph.jsp');
            popUp.sharedInternalCode = internalCode;
            popUp.sharedStocksName=stocksName;
        }
    };
})
.controller('stockgeneralinfoController',function($scope,$interval,$http,popupService,$window){
    $scope.searchName="";
    $scope.substockResults=[];
    $scope.substockGeneralInfo=[];
    $scope.internalCode;
    $scope.stocksName;
    $scope.updatedTime=new Date().toLocaleTimeString();;
    var isChanged=false;
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
    $scope.getStockGeneralInfo = function(internalCode,stocksName,isLive){
        $scope.internalCode=internalCode;
        $scope.stocksName=stocksName;
        $http({
                method: 'POST',
                url: 'GetStockGeneralInfoServlet',
                data:{
                    internalCode:$scope.internalCode
                }
            }).then(function (success){               
                console.log("GetGeneralInfo: "+success.data);
                angular.forEach(success.data,function(val,key){
                    console.log(val);
                });
                $scope.substockGeneralInfo =success.data;
                $scope.updatedTime= new Date().toLocaleTimeString();
                popupService.popupFunction($scope.internalCode,$scope.stocksName,isLive);
                
            },function (error){    
                console.log(error.data);
            }); 
        
    };
    $scope.setInternalCode=function(internalCode,stocksName){
        $scope.internalCode = internalCode;
        $scope.stocksName=stocksName;
        isChanged=true;
    };

});