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
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%-- Start of AngularJS Scripts --%>
        <script src="./ANGULARJS/angular.min.js"></script>
        <script src="./ANGULARJS-CONTROLLER/NewStocksRightsTradingControllerNg.js"></script>
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
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Raleway">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
        <link rel="stylesheet" href="https://ajax.googleapis.com/ajax/libs/angular_material/1.1.8/angular-material.min.css">
        <%-- End of Style Sheets --%>
        
    </head>
    
    <body class="w3-light-grey" ng-app="stocksrightsApp" ng-controller="stocksrightsController" ng-init="" >
        <!-- Top container -->
        <div class="w3-container w3-top w3-black w3-large w3-padding" style="z-index:4">
          <span class="w3-right">Icestone Analytics</span>
          <span class="w3-left">Stocks Rights Trading</span>
        </div>
        <div class="w3-container w3-padding-0 w3-white" style="height:800px;width:auto;border-bottom:solid;border-bottom-color:grey;border-width:thin;margin-top:40px">
            <%-- Page 1 --%>
            <div ng-show=pageShow[0]>
                <table class="w3-table" ng-repeat="stocksRightsInfo in stocksRightsInfos" ng-click="openGraph(stocksRightsInfo.internalcodesone,stocksRightsInfo.internalcodestwo)">
                    <tr class="w3-teal w3-margin-0"><td colspan="7" style="text-align: center">{{stocksRightsInfo.stocknameone+" : "+stocksRightsInfo.stocknametwo}}</td></tr>
                    <tr class="w3-teal w3-margin-0"><td>Name</td><td>Symbol</td><td>BestBid Qty</td><td>BestBid</td><td>BestAsk</td><td>BestAsk Qty</td><td>Spread</td></tr>
                    <tr>
                        <td>{{stocksRightsInfo.stocknameone}}</td>
                        <td>{{stocksRightsInfo.stocksymbolone}}</td>
                        <td>{{stocksRightsInfo.stockdataone.levelone.bestbidqty}}</td>
                        <td>{{stocksRightsInfo.stockdataone.levelone.bestbid}}</td>
                        <td>{{stocksRightsInfo.stockdataone.levelone.bestask}}</td>
                        <td>{{stocksRightsInfo.stockdataone.levelone.bestaskqty}}</td>
                        <td>{{stocksRightsInfo.spread}}</td>
                    </tr>
                    <tr>
                        <td>{{stocksRightsInfo.stocknametwo}}</td>
                        <td>{{stocksRightsInfo.stocksymboltwo}}</td>
                        <td>{{stocksRightsInfo.stockdatatwo.levelone.bestbidqty}}</td>
                        <td>{{stocksRightsInfo.stockdatatwo.levelone.bestbid}}</td>
                        <td>{{stocksRightsInfo.stockdatatwo.levelone.bestask}}</td>
                        <td>{{stocksRightsInfo.stockdatatwo.levelone.bestaskqty}}</td>
                        <td>{{stocksRightsInfo.spread}}</td>
                    </tr>
                </table>
            </div>

            <%-- Page 2--%>
            <div ng-show=pageShow[1] class="w3-container w3-half w3-border w3-padding-0 w3-light-grey" style="margin-left:25%;margin-top: 55px" style="z-index:4">
                <table class="w3-table">
                    <tr class="w3-teal"><th colspan="2">Add Stocks Rights</th></tr>
                    <tr>
                        <td>Stock Name</td>
                        <td>
                            <select ng-model="stockSelected" ng-options="(collection.stockname+'('+ collection.exchangesymbol+')') for collection in collections" style="color:black"></select>
                        </td>
                    </tr>
                    <tr style='border-bottom:solid; border-width:thin; border-bottom-color: grey'>
                        <td>Rights Name</td>
                        <td>
                            <select ng-model="rightSelected" ng-options="(collection.stockname+'('+ collection.exchangesymbol+')') for collection in collections" style="color:black"></select>
                        </td>
                    </tr>
                    <tr>
                        <td>Broker Commission %</td>
                        <td><input type='number' ng-model="inputs.brokercomm"></td>
                    </tr>
                    <tr>
                        <td>SGX Access Fee %</td>
                        <td><input type='number' ng-model="inputs.accessfee"></td>
                    </tr>
                    <tr>
                        <td>SGX Clearing Fee %</td>
                        <td><input type='number' ng-model="inputs.clearingfee"></td>
                    </tr>
                    <tr>
                        <td>GST %</td>
                        <td><input type='number' ng-model="inputs.gst"></td>
                    </tr>
                    <tr>
                        <td>Margin Requirement %</td>
                        <td><input type='number' value='' ng-disabled="true"></td>
                    </tr>
                    <tr>
                        <td>Funding Borrowed $</td>
                        <td><input type='number' ng-model="inputs.fundingborrowed"></td>
                    </tr>
                    <tr>
                        <td>Funding Fee %</td>
                        <td><input type='number' ng-model="inputs.fundingfee"></td>
                    </tr>
                    <tr>
                        <td>Shares Borrowed (Units)</td>
                        <td><input type='number' ng-model="inputs.sharesborrowed"></td>
                    </tr>
                    <tr>
                        <td>Borrowing Fee %</td>
                        <td><input type='number' ng-model="inputs.borrowingfee"></td>
                    </tr>                         
                    <tr>
                        <td>Last Price $</td>
                        <td><input type='number' ng-model="stockSelected.lastprice"></td>
                    </tr>
                    <tr>
                        <td>Subscription Price $</td>
                        <td><input type='number' ng-model="inputs.subscriptionprice"></td>
                    </tr>
                    <tr>
                        <td>Start date</td>
                        <td><input type='date' ng-model="inputs.startdate"></td>
                    </tr>
                    <tr>
                        <td>End date</td>
                        <td><input type='date' ng-model="inputs.enddate"></td>
                    </tr>
                </table>
                               
                <center>
                    <input type='button' value='Cancel' class='btn-danger w3-margin' ng-click="cancelClicked()" >
                    <input type='button' value='Submit' class='btn-primary w3-margin' ng-click="submitClicked()" >
                </center>
            </div>
            <%-- Page 3--%>
            <div ng-show=pageShow[2]  class="w3-container w3-half w3-border w3-padding-0 w3-light-grey" style="margin-top:55px ;margin-left:25%;" style="z-index:4">
                <table class="w3-table">
                    <tr class="w3-teal"><th colspan="2">Comfirmation page</th></tr>
                    <tr>
                        <td>Stock Name</td>
                        <td>
                            <select ng-model="stockSelected" ng-options="(collection.stockname+'('+ collection.exchangesymbol+')') for collection in collections" style="color:black"></select>
                        </td>
                    </tr>
                    <tr style='border-bottom:solid; border-width:thin; border-bottom-color: grey'>
                        <td>Rights Name</td>
                        <td>
                            <select ng-model="rightSelected" ng-options="(collection.stockname+'('+ collection.exchangesymbol+')') for collection in collections" style="color:black"></select>
                        </td>
                    </tr>
                    <tr>
                        <td>Broker Commission %</td>
                        <td><input type='number' ng-model="inputs.brokercomm" disabled="true"></td>
                    </tr>
                    <tr>
                        <td>SGX Access Fee %</td>
                        <td><input type='number' ng-model="inputs.accessfee" disabled="true"></td>
                    </tr>
                    <tr>
                        <td>SGX Clearing Fee %</td>
                        <td><input type='number' ng-model="inputs.clearingfee" disabled="true"></td>
                    </tr>
                    <tr>
                        <td>GST %</td>
                        <td><input type='number' ng-model="inputs.gst" disabled="true"></td>
                    </tr>
                    <tr>
                        <td>Margin Requirement %</td>
                        <td><input type='number' value='0' ng-disabled="true" disabled="true"></td>
                    </tr>
                    <tr>
                        <td>Funding Borrowed $</td>
                        <td><input type='number' ng-model="inputs.fundingborrowed" disabled="true"></td>
                    </tr>
                    <tr>
                        <td>Funding Fee %</td>
                        <td><input type='number' ng-model="inputs.fundingfee" disabled="true"></td>
                    </tr>
                    <tr>
                        <td>Shares Borrowed (Units)</td>
                        <td><input type='number' ng-model="inputs.sharesborrowed" disabled="true"></td>
                    </tr>
                    <tr>
                        <td>Borrowing Fee %</td>
                        <td><input type='number' ng-model="inputs.borrowingfee" disabled="true"></td>
                    </tr>                         
                    <tr>
                        <td>Last Price $</td>
                        <td><input type='number' ng-model="stockSelected.lastprice" disabled="true"></td>
                    </tr>
                    <tr>
                        <td>Subscription Price $</td>
                        <td><input type='number' ng-model="inputs.subscriptionprice" disabled="true"></td>
                    </tr>
                    <tr>
                        <td>Period</td>
                        <td><input type='number' ng-model="period" disabled="true"></td>
                    </tr>
                    <tr>
                        <td>Total Cost (Funding & Borrowing)</td>
                        <td><input type='number' value={{totalcost|number:2}} disabled="true"></td>
                    </tr>
                </table>
                               
                <center>
                    <input type='button' value='Cancel' class='btn-danger w3-margin' ng-click="cancelClicked()" >
                    <input type='button' value='Confirm' class='btn-primary w3-margin' ng-click="confirmClicked()" >
                </center>
            </div>
        </div>
        <center ng-show=pageShow[0]>
            <input type='button' class='btn-primary w3-margin-top' value='Add Pair' ng-click='addClicked();'>
        </center>
           
                        {{stocksRightsInfos}}
    </body>
</html>
