<%-- 
    Document   : StockTimeLineGraph
    Created on : Mar 23, 2018, 2:34:19 PM
    Author     : Owner
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html ng-app="pairtradingApp">
    <title>IceStone Analytics</title>
    
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%-- Start of AngularJS Scripts --%>
        <script src="./ANGULARJS/angular.min.js"></script>
        <script src="./ANGULARJS-CONTROLLER/PairTradingControllerNg.js"></script>
        <script src="./ANGULARJS/angular-websocket.min.js"></script>
        <%-- End of AngularJS Scripts --%>
        
        <%-- Start of BootStrap Scripts --%>
        <script src="//angular-ui.github.io/bootstrap/ui-bootstrap-tpls-2.5.0.js"></script>
        <%-- End of BootStrap Scripts --%>
        <%-- Start of Ng-Notification Scripts --%>
        <script src="//cdn.jsdelivr.net/angular.ng-notify/0.6.0/ng-notify.min.js"></script>
        <link  rel="stylesheet" href="//cdn.jsdelivr.net/angular.ng-notify/0.6.0/ng-notify.min.css">
        <%-- End of Ng-Notification Scripts --%>
        <%-- Start of Style Sheets --%>
        <link  rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <link rel="stylesheet" href="./CSS-STYLE/w3.css">
        <link rel="stylesheet" href="./CSS-STYLE/raleway.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
        <link rel="stylesheet" href="https://ajax.googleapis.com/ajax/libs/angular_material/1.1.8/angular-material.min.css">
        <%-- End of Style Sheets --%>
        
    </head>
    
    <body class="w3-light-grey" ng-controller="pairtradingController" ng-init="getSubStock();getPairTradingStocksInfo();" >
        <!-- Top container -->
        <div class="w3-container w3-top w3-black w3-large w3-padding" style="z-index:4">
          <span class="w3-right">Icestone Analytics</span>
          <span class="w3-left">Pair Trading</span>
        </div>
        <div class="w3-container" style="margin-top:43px;" >
            <div class="w3-third w3-left " style="padding-top:22px">
                    <div class="w3-row-padding" style="margin:0px">
                        <div class="w3-margin-bottom">
                            <input type="text" placeholder="Search for stocks..." style="width:100%" ng-model="searchName" ng-model-options="{ debounce: 5}" >
                        </div>
                        <div style="width:auto;height:735px;overflow-y:auto;">
                            <div class="w3-container w3-blue w3-padding-15">
                                <div class="w3-left">
                                    <h3>Stocks Subscribed</h3>
                                </div>
                            </div>
                            <table class="w3-table w3-striped w3-white">
                                <tr ng-repeat="subStockResult in substockResults |filter:searchName">
                                    <td ng-hide="true">{{subStockResult.internalcode}}</td><td style="width:40%">{{subStockResult.stocksname}}</td><td style="width:35%">{{subStockResult.exchangesymbol}}</td><td><input type="button" class="btn-primary" value= "Add" ng-click="addPairTradingStock(subStockResult.internalcode,subStockResult.stocksname)"></td>              
                                </tr>
                            </table>
                        </div>
                    </div>
            </div>   
            <div class="w3-twothird w3-right " style="padding-top:22px" >
                <center class="w3-margin-bottom w3-container" style="width: 1350px; margin-left: -50px">
                    <input type="text" placeholder="Stock 1" ng-disabled="true" style="height: 34px; width:28%" value={{stock[0].stocksname}}>
                    <div class="glyphicon glyphicon-plus-sign"></div>
                    <input type="text" placeholder="Stock 2" ng-disabled="true" style="height: 34px; width:28%" value={{stock[1].stocksname}}>
                    <input type="number" placeholder="Alert Margin(%)" ng-disabled="locked" style="height: 34px; width:10%" ng-model="percentage" >
                    <input type="button" class="btn-primary" value="Execute" style=" margin-left:9px; width: 10%;height: 34px;" ng-click="addPairTrading()">
                    <input type="button" class="btn-danger" value="Cancel" style=" margin-left:9px; width: 10%;height: 34px;" ng-click="cancel()">    
                </center>
                <div class="w3-container " style="margin-top: -7px;">
                    <table class="w3-table">
                        <tr class="w3-grey" style="height:45px"><th>Stock 1</th><th>Stock 2</th><th>Average Price Diff.</th><th>Current Price Diff.</th><th>Alert Margin</th><th>Cancel</th></tr>
                        <tr ng-repeat="pairTradingStock in pairTradingStocks ">
                            <td ng-show="false">{{pairTradingStock.internalcodesone}}</td>
                            <td ng-show="false">{{pairTradingStock.internalcodestwo}}</td>
                            <td>{{pairTradingStock.stocksnameone}}</td>
                            <td>{{pairTradingStock.stocksnametwo}}</td>
                            <td>{{pairTradingStock.olddifference|number:3}}</td>
                            <td ng-style="{'background': pairTradingStock.color}">{{pairTradingStock.currentdifference|number:3}}</td>
                            <td>{{pairTradingStock.percentage}}%</td>
                            <td><input type ="button" ng-click="deletePairTrading(pairTradingStock.internalcodesone,pairTradingStock.internalcodestwo,pairTradingStock.percentage)" class="btn-danger" value="cancel"></td>
                            
                       </tr>
                    </table>
                </div>
            </div> 
            
        </div>    
    </body>
</html>
