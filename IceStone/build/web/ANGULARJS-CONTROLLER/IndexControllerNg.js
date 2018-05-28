/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var app= angular.module('indexApp',[]);

app.controller('indexController',['$scope','$http','$interval','$window',function($scope,$http,$interval){
    var isConnected=false;
    $scope.testing="indexController";
    $scope.isConnecting = false;
    $scope.loaderStyle = 'none';
    $scope.response="";
    $scope.changeLoading = function(){
        $scope.isConnecting = !$scope.isConnecting;
        if ($scope.isConnecting) {     
          $scope.loaderStyle = 'block';
          $scope.startFeedosConnection();
        }else{
          $scope.loaderStyle = 'none';
        }
    };
    
    //Function to connect to QuantHouse
    $scope.startFeedosConnection=function(){
        
        $http({
            method: 'GET',
            url: 'FeedosInitConnectionServlet'
        }).then(function (success){
            isConnected = success.data.result;
            if(!isConnected){
                window.alert("Error connecting to QuantHouse");
                $scope.isConnecting = false;
                $scope.loaderStyle = 'none';
            }else{
                window.location.href= "http://localhost:8084/IceStone/home.jsp";
            }
            
        },function (error){
            console.log(error.data);
            $scope.isConnecting = false;
            $scope.loaderStyle = 'none';
            window.alert("Error connecting to QuantHouse");
        });
    };

}]);





