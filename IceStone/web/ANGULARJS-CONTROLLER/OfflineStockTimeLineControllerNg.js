angular.module('offlinegraphApp',['ngWebSocket','googlechart','ngMaterial'])
.config(function($mdIconProvider) {
    $mdIconProvider.iconSet('device', 'img/icons/sets/device-icons.svg', 24);
})
.factory('offlinestocktimelineFactory',function($http){
    var isReady=[false];
    var internalCode = window.sharedInternalCode;
    console.log(internalCode);
    var dataGraph=[];
    var dataLevelTwo=[{}];
    var getGraph=function(){
        console.log("really requesting");
        dataGraph.splice(0,dataGraph.length);
        $http({
                method: 'POST',
                url: 'GetAllHistoricalInfoServlet',
                data:{
                    internalCode:internalCode
                }
                
            }).then(function (success){               
                result=success.data;
                console.log(result);
                dataGraph.splice(0,dataGraph.length);
                dataLevelTwo.splice(0,dataLevelTwo.length);
                angular.forEach(result.leveloneinfo,function(obj,objkey){
                    dataGraph.push({
                        c:[{v:new Date(obj.timesgd)},
                           {v:obj.lastprice}]
                                                             
                    });

                });
                
                dataLevelTwo.push(result.leveltwoinfo);
                isReady.splice(0,1);
                isReady.push(true);
                console.log("Ready: "+isReady);
                
            },function (error){    
                console.log(error.data);
            });
    };
    
    return {
        isReady:isReady,
        dataLevelTwo:dataLevelTwo,  
        dataGraph:dataGraph,  
        getGraph:function(){
            console.log("posting request");
            getGraph();
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
}).controller('offlinestocktimelineController',function($scope,$interval,offlinestocktimelineFactory){   
    $scope.isReady=offlinestocktimelineFactory.isReady;
    $scope.stocksName = window.sharedStocksName;
    $scope.isPlaying=true;
    $scope.isForward=true;
    $scope.allDataLevelTwo=offlinestocktimelineFactory.dataLevelTwo;
    $scope.dataLevelTwo;
    $scope.maxValue=10000;
    $scope.currentValue=1; 
    $scope.currentTime;
    $scope.colors=[];
    $scope.myChartObject = {};
    $scope.myChartObject.type = "AnnotationChart";
    $scope.myChartObject.data = {"cols": [
            {label: "Date", type: "date"},
            {label: "Last Price", type: "number"}
        ], "rows": offlinestocktimelineFactory.dataGraph
    };
    $scope.myChartObject.options = {
        displayAnnotations: false
    };
    
    var assignColor = function(old,updated){
        if(old===null){
            return 'white';
        }else if(updated>old){
            return 'lime';
        }else if(updated<old){
            return 'pink';
        }
        return 'white';
        
    };
    $scope.show=function(){
        $scope.colors=[];
        angular.forEach( $scope.allDataLevelTwo[0],function(val,key){
            if(key===$scope.currentValue){
                angular.forEach($scope.dataLevelTwo,function(level,levelKey){
                   $scope.colors.push({
                       color1:assignColor(level.bidqueue,val[levelKey].bidqueue),
                       color2:assignColor(level.bidqty,val[levelKey].bidqty),
                       color3:assignColor(level.bid,val[levelKey].bid),
                       color4:assignColor(level.ask,val[levelKey].ask),
                       color5:assignColor(level.askqty,val[levelKey].askqty),
                       color6:assignColor(level.askqueue,val[levelKey].askqueue)
                   });
                });
                $scope.dataLevelTwo=val;
                $scope.currentTime=val[0].timesgd;
            }
            $scope.maxValue=key;
        });
    };
    $scope.$watch('$viewcontentloaded',function(){
        console.log("stockTimeLine content loaded");
        offlinestocktimelineFactory.getGraph(); 
        $scope.show();
    });
    $scope.togglePlaying = function(){
        $scope.isPlaying=!$scope.isPlaying;
    };
    $scope.playForward =function(){
        $scope.isForward=true;
    };
    $scope.playBackward =function(){
        $scope.isForward=false;
    };
    var looping=function(){
        angular.forEach( $scope.allDataLevelTwo[0],function(val,key){
            $scope.maxValue=key;
        });
        if($scope.isPlaying&&$scope.currentValue>=0&&$scope.currentValue<$scope.maxValue){
            if($scope.isForward){
                $scope.currentValue++;
            }else{
                $scope.currentValue--;
            }
            $scope.show();
        }
    };
    $interval(looping,1000);

});

