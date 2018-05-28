

var app = angular.module('homeApp');
app.controller('stockstrategiesController',function($scope,$interval,$http,popupService,$window){
    $scope.strategies =[{
            name:"Volume breakthrough alert",
            color:"white",
            link:"volumebreakthroughalert.jsp"
        },{
            name:"Pair trading",
            color:"white",
            link:"pairtrading.jsp"
        },{
            name:"Volume price analysis",
            color:"white",
            link:"volumeprice.jsp"
        },
        {
            name:"Futures analysis",
            color:"white",
            link:"futuresanalysis.jsp"
        }];
    $scope.searchStrategy ="";
    $scope.changeToSolid = function(key){
        $scope.strategies[key].color='solid';
    };
    $scope.changeToWhite = function(key){
        $scope.strategies[key].color='none';
    };
    $scope.openLink=function(key){
        window.open($scope.strategies[key].link);
    };
});