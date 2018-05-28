/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var app= angular.module('homeApp',['ngRoute','ngWebSocket','googlechart','ngNotify','ngMaterial','ui.bootstrap','ngDialog']);
// configure our routes
app.config(function($routeProvider) {
    $routeProvider
        // route for the home page
	.when('/', {
            templateUrl : '/IceStone/overview.jsp',
            controller  : 'overviewController'
        })
        .when('/stocksubscribe', {
            templateUrl : '/IceStone/stocksubscribe.jsp',
            controller  : 'stocksubscribeController'
        })
        .when('/stockgeneralinfo', {
            templateUrl : '/IceStone/stockgeneralinfo.jsp',
            controller  : 'stockgeneralinfoController'
        })
        .when('/stocktransaction', {
            templateUrl : '/IceStone/stocktransaction.jsp',
            controller  : 'stocktransactionController'
        })
        .when('/stockstrategies', {
            templateUrl : '/IceStone/stockstrategies.jsp',
            controller  : 'stockstrategiesController'
        })
        .when('/newsfeed', {
            templateUrl : '/IceStone/newsfeed.jsp',
            controller  : 'newsfeedController'
        })
        .when('/stocksleveltwoinfo', {
            templateUrl : '/IceStone/stockleveltwoinformation.jsp'
        })
        .otherwise({
            redirect:'/'
        });
	
});

app.controller('homeController',function($scope,$http,$interval){
    $scope.testing="homeController";
    //Function to connect to QuantHouse
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

});


