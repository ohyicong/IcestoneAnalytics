<%-- 
    Document   : stocksubscribe
    Created on : Mar 6, 2018, 5:15:52 PM
    Author     : Owner
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<div class="w3-main" style="margin-left:300px;margin-top:43px;" ng-init="getSubStock()">

    <!-- Header -->
    <header class="w3-container" style="padding-top:22px">
        <h5><b><i class="glyphicon glyphicon-pencil"></i> Subscribe stocks</b></h5>
    </header>
    <div class="w3-third">
        <div class="w3-row-padding" >
            <input type="text" placeholder="Search for stocks..." style="width:65%" ng-model="searchName" ng-keyup="searchForSubStock()" ng-model-options="{ debounce: 5}" >
            <button style="width:32%" ng-click="subscribeStock()" >Subscribe</button>
        </div>
        <div class="w3-row-padding ">
            <table ng-hide="!isQueried" class="w3-white" border="1px black" style="width:65%" >
                <tr ng-repeat="(key,response) in searchResults" ng-click="callFillName(response.stocksname,response.internalcode,response.exchangesymbol)">
                    <td ng-hide="true">{{response.internalcode}}</td><td>{{response.stocksname+" ("+response.exchangesymbol+" )"}}</td>
                </tr>
            </table>
        </div>
    </div>
    <div class="w3-twothird">
        <div class="w3-row-padding w3-container" style="margin:-15px">
            <div class="w3-container w3-blue w3-padding-15" style="margin-bottom:0px">
                <h3>Stocks Subscribed</h3>
            </div>
            <div style="height : 750px;
                                display:block;
                                overflow-y:auto;
                                border : 1px solid black;">
                <table class="w3-table w3-striped w3-white">
                    <tr ng-repeat="subStockResult in substockResults ">
                        <td ng-hide="true">{{subStockResult.internalcode}}</td><td style="width:18%">{{subStockResult.stocksname}}</td><td style="width:35%">{{subStockResult.exchangesymbol}}</td><td style="width:10%"><button class="btn-danger" ng-click="deleteStock(subStockResult.internalcode,subStockResult.stocksname,subStockResult.exchangesymbol);" >Delete </button></td></td>              
                    </tr>
                </table>
            </div>
       </div>     
    </div>
   


  <!-- End page content -->
</div>