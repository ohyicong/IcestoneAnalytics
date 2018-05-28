<%-- 
    Document   : StockTimeLineGraph
    Created on : Mar 23, 2018, 2:34:19 PM
    Author     : Owner
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html ng-app="offlinegraphApp">
    <title>IceStone Analytics</title>
    
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <%-- Start of AngularJS Scripts --%>
        <script src="./ANGULARJS/angular.min.js"></script>
        <script src="./ANGULARJS/angular-websocket.min.js"></script>
        <script src="./ANGULARJS/angular-route.min.js"></script>
        <script src="./ANGULARJS/angular-animate.min.js"></script>
        <script src="./ANGULARJS/angular-aria.min.js"></script>
        <script src="./ANGULARJS/angular-messages.min.js"></script>
        <script src="./ANGULARJS/angular-material.min.js"></script>
        <script src="./ANGULARJS-CONTROLLER/OfflineStockTimeLineControllerNg.js"></script>
        
        <%-- End of AngularJS Scripts --%>
        
        <%-- Start of BootStrap Scripts --%>
        <script src="//angular-ui.github.io/bootstrap/ui-bootstrap-tpls-2.5.0.js"></script>
        <%-- End of BootStrap Scripts --%>
        
        <%-- Start of Style Sheets --%>
        <link  rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <link rel="stylesheet" href="./CSS-STYLE/w3.css">
        <link rel="stylesheet" href="./CSS-STYLE/raleway.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
        <link rel="stylesheet" href="./CSS-STYLE/angular-material.min.css">
        <%-- End of Style Sheets --%>
        
        <%-- Start of Google-chart Scripts --%>
        <script src="./RANDOMJS/ng-google-chart.min.js" type="text/javascript"></script>
        <%-- End of Google-chart Scripts --%>
        
        
    </head>
    
    <body class="w3-light-grey" ng-controller="offlinestocktimelineController" >
        <div class="w3-container w3-top w3-black w3-large w3-padding" style="z-index:4">
          <span class="w3-right">Icestone Analytics</span>
          <span class="w3-left">{{stocksName}}</span>
        </div>
        <div class="w3-container" style="margin-top:43px;" >
            <center ng-show="!isReady[0]" >
                <div  style="padding-top:250px">
                        <md-progress-circular md-mode="indeterminate" md-diameter="100"></md-progress-circular><br>
                </div>
                <p style="color: darkblue">Loading...</p>
            </center>
            <div ng-show="isReady[0]" class="w3-third w3-left " style="padding-top:22px">
                <table class="w3-table w3-border w3-teal">
                    <tr><td>Bid Queue</td><td>Bid Qty</td><td>Bid</td><td>Ask</td><td>Ask Qty</td><td>Ask Queue</td></tr>
                    <tr ng-repeat="(key,level) in dataLevelTwo" class="w3-white">
                        <td ng-style="{ 'background': colors[key].color1 }">{{level.bidqueue}}</td>
                        <td ng-style="{ 'background': colors[key].color2 }">{{level.bidqty}}</td>
                        <td ng-style="{ 'background': colors[key].color3 }">{{level.bid}}</td>
                        <td ng-style="{ 'background': colors[key].color4 }">{{level.ask}}</td>
                        <td ng-style="{ 'background': colors[key].color5 }">{{level.askqty}}</td>
                        <td ng-style="{ 'background': colors[key].color6 }">{{level.askqueue}}</td>
                    </tr>
                </table><br>
                <div class="w3-container w3-white">
                    <div class="w3-container">
                        <md-slider ng-click="show()" flex min="0" max={{maxValue}} ng-model="currentValue" aria-label="red" id="red-slider">
                        </md-slider>
                    </div>
                    <div class="w3-container w3-grey" style="margin-right: -16px ; margin-left: -16px; padding-top: 10px">
                        <div class="w3-left">
                            <i ng-click="playBackward()" class="glyphicon glyphicon-backward"></i>&nbsp
                            <div ng-show="!isPlaying" ng-click="togglePlaying()" class="glyphicon glyphicon-play"></div>
                            <div ng-show="isPlaying" ng-click="togglePlaying()" class="glyphicon glyphicon-pause"></div>&nbsp
                            <div ng-click="playForward()" class="glyphicon glyphicon-forward"></div>  
                        </div>
                        <center>
                            <p>
                               {{"Time: "+currentTime}}
                            </p>
                        </center>
                    </div>
                </div>
 
            </div>   
            <div ng-show="isReady[0]" class="w3-twothird w3-right" style="padding-top:22px" >
                <div google-chart chart="myChartObject" style="height:600px; width:100%;"></div>
            </div> 
            
        </div>    
    </body>
</html>
