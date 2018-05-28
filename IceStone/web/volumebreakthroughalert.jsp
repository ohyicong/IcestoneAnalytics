<%-- 
    Document   : VolumeBreakthroughAlert
    Created on : Apr 13, 2018, 1:24:35 PM
    Author     : Owner
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html ng-app="volumebreakthroughApp">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%-- Start of AngularJS Scripts --%>
        <script src="./ANGULARJS/angular.min.js"></script>
        <script src="./ANGULARJS-CONTROLLER/VolumeBreakThroughControllerNg.js"></script>
        <script src="./ANGULARJS/angular-websocket.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.6.9/angular-animate.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.6.9/angular-aria.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.6.9/angular-messages.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/angular_material/1.1.8/angular-material.min.js"></script>
        <%-- End of AngularJS Scripts --%>
        
        <%-- Start of BootStrap Scripts --%>
        <script src="//angular-ui.github.io/bootstrap/ui-bootstrap-tpls-2.5.0.js"></script>
        <%-- End of BootStrap Scripts --%>
        
        <%-- Start of Style Sheets --%>
        <link  rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <link rel="stylesheet" href="./CSS-STYLE/w3.css">
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Raleway">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
        <link rel="stylesheet" href="https://ajax.googleapis.com/ajax/libs/angular_material/1.1.8/angular-material.min.css">
        <%-- End of Style Sheets --%>
        
        <%-- Start of Ng-Notification Scripts --%>
        <script src="//cdn.jsdelivr.net/angular.ng-notify/0.6.0/ng-notify.min.js"></script>
        <link  rel="stylesheet" href="//cdn.jsdelivr.net/angular.ng-notify/0.6.0/ng-notify.min.css">
        <%-- End of Ng-Notification Scripts --%>
        
        <title>IceStone Analytics</title>
        
    </head>
    <body class=" w3-light-grey" ng-controller="volumebreakthroughController" ng-init="getVolume();">
        <div class="w3-container w3-top w3-black w3-large w3-padding" style="z-index:4">
          <span class="w3-right">Icestone Analytics</span>
          <span class="w3-left">Volume Break Through Alert (Last 3 days)</span>
        </div>
        <div class="w3-container" style="margin-top:43px;height:max-content;width:auto;padding:0">
            <div class="w3-container w3-white" style="padding:0" >
                <table class="w3-table">
                    <tr ng-show=!isFiltered class="w3-teal">
                        <th>Name</th>
                        <th>Symbol</th>
                        <th>Average volume</th>
                        <th>Current volume traded </th>
                    </tr>
                    <tr ng-show=!isFiltered ng-repeat="element in collection[0]|filter:filterColor">
                        <td>{{element.stockname}}</td>
                        <td>{{element.symbol}}</td>
                        <td>{{element.avgvolume|number:0}}</td>
                        <td>{{element.volumetraded|number:0}}</td>
                    </tr>
                    <tr ng-show=isFiltered class="w3-teal">
                        <th>Name</th>
                        <th>Symbol</th>
                        <th>Average volume</th>
                        <th>Current volume traded </th>
                        <th>Progress </th>
                    </tr>
                    <tr ng-show=isFiltered ng-repeat="element in collection[0]" ng-style="{ 'background': element.color }">
                        <td>{{element.stockname}}</td>
                        <td>{{element.symbol}}</td>
                        <td>{{element.avgvolume|number:0}}</td>
                        <td>{{element.volumetraded|number:0}}</td>
                        <td>
                            <md-progress-linear md-mode="determinate" value="{{(element.volumetraded/element.avgvolume)*100}}"></md-progress-linear>
                        </td>
                    </tr>
                </table>
                <center class="w3-grey" style="margin:10px;padding:10px"><input class="btn-primary" type="button" value="Toggle view" ng-click="toggleView();"></center>   
            </div>
        </div>
    </body>
</html>
