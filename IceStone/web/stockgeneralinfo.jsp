<%-- 
    Document   : stocksubscribe
    Created on : Mar 6, 2018, 5:15:52 PM
    Author     : Owner
--%>

<div class="w3-main" style="margin-left:300px;margin-top:43px;" ng-init="getSubStock()">

    <!-- Header -->
    <header class="w3-container" style="padding-top:22px">
        <h5><b><i class="fa fa-bar-chart"></i> Stocks General Information</b></h5>
    </header>
    <div class="w3-third">
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
                        <td ng-hide="true">{{subStockResult.internalcode}}</td>
                        <td style="width:40%">{{subStockResult.stocksname}}</td>
                        <td style="width:35%">{{subStockResult.exchangesymbol}}</td>
                        <td><input type="button" class="btn-primary" value= "Live" ng-click="getStockGeneralInfo(subStockResult.internalcode,subStockResult.stocksname,true)"></td>
                        <td><input type="button" class="btn-primary" value= "Non-Live" ng-click="getStockGeneralInfo(subStockResult.internalcode,subStockResult.stocksname,false)"></td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
    <div class="w3-twothird">
        <div class="w3-row-padding" style="margin:-15px">
            <div class="w3-container" >
                <div class="w3-container w3-dark-grey w3-padding-15" style="margin-bottom:0px">
                    <h3>Stock Information</h3>
                </div>  
                <table class="w3-table w3-striped w3-white w3-margin-bottom" >
                    <tr><th>Description</th><th>Details</th></tr>
                    <tr><td>Name</td><td>{{substockGeneralInfo.stocksname}}</td></tr>
                    <tr><td>Best Bid</td><td>{{substockGeneralInfo.bid}}</td></tr>
                    <tr><td>Best Ask</td><td>{{substockGeneralInfo.ask}}</td></tr>
                    <tr><td>Last Price</td><td>{{substockGeneralInfo.lastprice}}</td></tr>
                    <tr><td>Daily Volume Traded</td><td>{{substockGeneralInfo.volumetraded}}</td></tr>
                    <tr><td>Daily Assets Traded</td><td>{{substockGeneralInfo.assetstraded}}</td></tr>
                    <tr><td>Previous Closing Price</td><td>{{substockGeneralInfo.closingprice}}</td></tr>
                    <tr><td>Previous Volume traded</td><td>{{substockGeneralInfo.prevvolumetraded}}</td></tr>
                    <tr><td>Previous Assets traded</td><td>{{substockGeneralInfo.prevassetstraded}}</td></tr>
                </table>
                <div class="w3-right">
                    <p style="font-size:10px;margin-right:2px ">
                        <i class="glyphicon glyphicon-refresh"></i>
                        Updated as of: {{updatedTime}}
                    </p>
                </div>
            </div>
        </div>
    </div>
   


  <!-- End page content -->
</div>