<%-- 
    Document   : stocksubscribe
    Created on : Mar 6, 2018, 5:15:52 PM
    Author     : Owner
--%>

<style>
    div.buyQty{
        background-color:green;
        height:10px;
        width:100%;
        float:left;
        margin:0;
        padding:0;
    }
    div.sellQty{
        background-color:red;
        height:10px;
        width:0%;
        float:right;
        padding:0; 
    }
    .myrow.hover{
        border:1px solid black;
    }
</style>


<div class="w3-main" style="margin-left:300px;margin-top:43px;" ng-init="getSubStock()">

    <!-- Header -->
    <header class="w3-container" style="padding-top:22px">
        <h5><b><i class="glyphicon glyphicon-transfer"></i> Stocks Transaction Information</b></h5>
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
                        <td ng-hide="true">{{subStockResult.internalcode}}</td><td style="width:40%">{{subStockResult.stocksname}}</td><td style="width:35%">{{subStockResult.exchangesymbol}}</td><td><input type="button" class="btn-primary" value= "View" ng-click="getTransaction(subStockResult.internalcode,subStockResult.stocksname)"></td>              
                    </tr>
                </table>
            </div>
        </div>
    </div>
    <div class="w3-twothird">
        <div class="w3-row-padding" style="margin:-15px">
            <div class="w3-container">
                <div class="w3-container w3-dark-grey" style="margin:0px;padding:5px">
                    <h3 style="float:left">Intraday Stock Transaction</h3>
                    <span style="float:right;margin:5px" class="form-group" >
                        <input type="button" value="Trans" class="form-group btn btn-primary" ng-click="onTrans()">
                        <input type="button" value="Buy" class= "form-group btn btn-primary" ng-click="onBuy()">
                        <input type="button" value="Sell" class="form-group btn btn-primary" ng-click="onSell()">
                    </span>
                </div>
                <div style="height : 720px;
                            display:block;
                            overflow-y:auto;
                            border : 1px solid black;" ng-show="page[0]">
                    <table class="w3-table w3-striped w3-white " >
                        <tr><th>S/N</th><th>Trade Price</th><th>Quantity</th><th>Type</th><th>Time</th></tr>
                        <tr ng-repeat="(key,info) in substockTransactionInfos| orderBy:'-timesgd' "><td>{{key}}</td><td>{{info.tradeprice}}</td><td>{{info.quantity}}</td><td>{{info.transtype}}</td><td>{{info.timesgd|limitTo:19}}</td></tr>
                    </table>
                    
                </div>
                <div style="height : 720px;
                            display:block;
                            overflow-y:auto;
                            border : 1px solid black;" ng-show="page[1]">
                    <table class="w3-table w3-striped w3-white " >
                        <tr><th>Buy Volume Quantity</th><th>Count</th></tr>
                        <tr ng-repeat="(key,info) in volumeCountBuy" class="myrow" ng-click="onSelect(volumeCountBuy,key)">
                            <td>{{key}}</td>
                            <td>{{info.count}}</td>
                        </tr>
                    </table>
                    
                </div>
                <div style="height : 720px;
                            display:block;
                            overflow-y:auto;
                            border : 1px solid black;" ng-show="page[2]">
                    <table class="w3-table w3-striped w3-white " >
                        <tr><th>Sell Volume Quantity</th><th>Count</th></tr>
                        <tr ng-repeat="(key,info) in volumeCountSell" class="myrow" ng-click="onSelect(volumeCountSell,key)">
                            <td>{{key}}</td>
                            <td>{{info.count}}</td>
                        </tr>
                    </table>
                    
                </div>
                <div style="height : 720px;
                            display:block;
                            overflow-y:auto;
                            border : 1px solid black;" ng-show="page[3]">
                    <table class="w3-table w3-striped w3-white " >
                        <tr><th>S/N</th><th>Trade Price</th><th>Quantity</th><th>Type</th><th>Time</th></tr>
                        <tr ng-repeat="(key,info) in selectedTrans| orderBy:'-timesgd' "><td>{{key}}</td><td>{{info.tradeprice}}</td><td>{{info.quantity}}</td><td>{{info.transtype}}</td><td>{{info.timesgd|limitTo:19}}</td></tr>
                    </table>
                    
                </div>
                <div class='w3-container' style="margin:0px;padding:0px;">
                    <div class="buyQty" ng-style="{ 'width': buyQtyPercent }"></div>
                    <div class="sellQty" ng-style="{ 'width': sellQtyPercent }"></div>
                </div>
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