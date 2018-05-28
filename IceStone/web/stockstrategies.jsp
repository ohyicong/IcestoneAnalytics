<%-- 
    Document   : stocksubscribe
    Created on : Mar 6, 2018, 5:15:52 PM
    Author     : Owner
--%>

<div class="w3-main" style="margin-left:300px;margin-top:43px;" ng-init="getSubStock()">

    <!-- Header -->
    <header class="w3-container" style="padding-top:22px;margin-left: 10px">
        <h5><b><i class="glyphicon glyphicon-knight"></i> Strategies</b></h5>
    </header>
    <div class="w3-container">
        <div class="w3-row-padding">
            <div class="w3-margin-bottom">
            <input type="text" placeholder="Search for strategies..." style="width:100%" ng-model="searchStrategy" ng-model-options="{ debounce: 5}" >
            </div>
            <div class="w3-container w3-grey w3-padding-15">
                <div class="w3-left">
                    <h3>Strategies available</h3>
                </div>
            </div>
            <table class="w3-table w3-striped w3-white">
                <tr ng-repeat="(key,strategy) in strategies |filter:searchStrategy"><td ng-click="openLink(key)" ng-mouseenter="changeToSolid(key)" ng-mouseleave="changeToWhite(key)" ng-style="{ 'border-style': strategy.color }">{{strategy.name}}</td><tr>
            </table>
            
        </div>
    </div>
   


  <!-- End page content -->
</div>