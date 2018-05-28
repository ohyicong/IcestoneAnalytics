var app = angular.module("homeApp");

app.controller("newsfeedController",function($scope,$interval,$http,ngDialog){
    $scope.selections =["Today","Past week","Past Month"];
    $scope.selected="Today";
    $scope.isShown=[true,false,false,false,false,false];
    $scope.newsFeedResults =[];
    $scope.newsFeedSummary =[];
    $scope.sharesBuyBackResults =[];
    $scope.sharesBuyBackFinalResults=[];
    $scope.isSyncing=false;
    $scope.currentPdfPage=0;
    var promise;
    $scope.switchMenu = function(menu){
        switch(menu){
            case "Disclosure Of Interest":$scope.isShown=[true,false,false,false,false,false];break;
            case "Shares Buy Back":$scope.isShown=[false,true,false,false,false,false];break;
            case "Bloomberg":$scope.isShown=[false,false,true,false,false,false];break;
            case "BBC":$scope.isShown=[false,false,false,true,false,false];break;
            case "CNA":$scope.isShown=[false,false,false,false,true,false];break;     
        }
    };
    $scope.openLink=function(link){
         window.open(link);
    };
    $scope.getNewsFeed = function (menu){
        console.log("Getting news Feed");
        $http({
                method: 'POST',
                url: 'GetNewsFeedServlet',
                data:{
                    selected:$scope.selected,
                    menu:menu
                }
            }).then(function (success){               
                console.log(success.data);
                $scope.newsFeedResults =success.data.data;
                $scope.isSyncing= success.data.isSyncing;
                $scope.currentPdfPage = success.data.currentPdfPage;
            },function (error){    
                console.log(error.data);
            });
 
    };
    $scope.getSharesBuyBack = function(){
        console.log("Getting sharebuyback");
        $http({
                method: 'POST',
                url: 'GetSharesBuyBackServlet'
            }).then(function (success){               
                console.log(success.data);
                $scope.sharesBuyBackResults =success.data;
                $scope.sharesBuyBackFinalResults=[];
                angular.forEach($scope.sharesBuyBackResults,function(obj,objKey){
                    var textone=obj.textone;
                    var texttwo=obj.texttwo;
                    $scope.sharesBuyBackFinalResults.push({
                        issuer:obj.issuer,
                        textone:textone.split("\n"),
                        texttwo:texttwo.split("\n")
                    });
                });
            },function (error){    
                console.log(error.data);
            });
    };
    $scope.$watch('$viewcontentloaded',function(){
         promise = $interval($scope.routineChecks,30000);
    });
    $scope.$on('$destroy',function(){
        $interval.cancel(promise);       
    }); 
    $scope.SyncNewsFeed = function(){
        console.log("Syncing");
        $scope.isSyncing=true;
        $http({
                method: 'POST',
                url: 'SyncNewsFeedServlet'
            }).then(function (success){               
                console.log("SyncNewsFeed: "+success.data);
            },function (error){    
                console.log(error.data);
            }); 
    };
    $scope.routineChecks = function(){
        $scope.getNewsFeed();
        $scope.getSharesBuyBack();
    };
   
});


