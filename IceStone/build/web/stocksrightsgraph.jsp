<%-- 
    Document   : StockTimeLineGraph
    Created on : Mar 23, 2018, 2:34:19 PM
    Author     : Owner
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <title>IceStone Analytics</title>
    
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <%-- Start of AngularJS Scripts --%>
        <script src="./ANGULARJS/angular.min.js"></script>
        <script src="./ANGULARJS-CONTROLLER/StocksRightsGraphControllerNg.js"></script>
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
        
        <%-- Start of Google-chart Scripts --%>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/angular-google-chart/1.0.0-beta.1/ng-google-chart.min.js" type="text/javascript"></script>
        <%-- End of Google-chart Scripts --%>
        
        <%-- Start of Ng-Notification Scripts --%>
        <script src="//cdn.jsdelivr.net/angular.ng-notify/0.6.0/ng-notify.min.js"></script>
        <link  rel="stylesheet" href="//cdn.jsdelivr.net/angular.ng-notify/0.6.0/ng-notify.min.css">
        <%-- End of Ng-Notification Scripts --%>
        
        
    </head>
    
    <body class="w3-light-grey" ng-app="stocksrightsgraphApp" ng-controller="stocksrightsgraphController" ng-init="getStocksRightsGraph()" style="overflow-y:auto;overflow-x: auto" >
        <div class="w3-container w3-top w3-black w3-large w3-padding" style="z-index:4">
          <span class="w3-right">Icestone Analytics</span>
        </div>
        
        <div class="w3-container w3-padding-0" style="margin-top:43px;" >
            <div class="w3-half w3-left w3-padding-0" style="background-color: red;border:solid;border-color: lightgrey">
                <div class="w3-half w3-left">
                    <p>Stock name: {{stocksRights[0].stockname}} </p>
                    <p>Best Bid Volume: {{stocksRights[0].stockinfo.bestbidqty|number:3}}</p>
                    <p >Best Bid:{{stocksRights[0].stockinfo.bestbid|number:3}}</p>
                    <p>Transaction Fee:{{stocksRights[0].stockinfo.bestbid*calculatedVar[0].volumeused*calculatedVar[0].transactionpercent/100|number:3}}</p>
                    <p>Sales:{{stocksRights[0].stockinfo.bestbid*calculatedVar[0].volumeused-(stocksRights[0].stockinfo.bestbid*calculatedVar[0].volumeused*calculatedVar[0].transactionpercent/100)|number:3}}</p>
                </div>
                <div class="w3-half w3-left">
                    <p>Rights name: {{stocksRights[0].rightsname}} </p>
                    <p>Best Ask Volume:{{stocksRights[0].rightsinfo.bestaskqty|number:3}}</p>
                    <p>Best Ask:{{stocksRights[0].rightsinfo.bestask|number:3}}</p>                 
                    <p>Transaction Fee:{{stocksRights[0].rightsinfo.bestask*calculatedVar[0].volumeused*calculatedVar[0].transactionpercent/100|number:3}}</p>
                    <p>Subscription Price:{{stocksRights[0].subscriptionprice*calculatedVar[0].volumeused|number:3}}</p>
                    <p>Expenses:{{(stocksRights[0].rightsinfo.bestask*calculatedVar[0].volumeused*(calculatedVar[0].transactionpercent+100)/100)+(stocksRights[0].subscriptionprice*calculatedVar[0].volumeused)|number:3}}</p>
                </div>
                <hr>
                <div class="w3-container w3-padding-0">
                    <p>Current Spread:{{calculatedVar[0].spread|number:3}}</p>
                    <p>Gross Profit:{{calculatedVar[0].spread*calculatedVar[0].volumeused|number:3}}</p>
                    <p>Borrowing Fee:{{calculatedVar[0].borrowingcost|number:3}}</p>
                    <p>Funding Fee:{{calculatedVar[0].fundingcost|number:3}}</p>
                    <p>Net profit:{{calculatedVar[0].netprofit}}</p>
                </div>
                <h3>{{stocksRights}}</h3>                
                <p>{{calculatedVar}}</p>
                {{calculatedVar[0].transactionpercent}}
                {{history}}
            </div>    
            </div>
        </div>    
    </body>
</html>
