<!DOCTYPE html>
<html ng-app="homeApp">
<title>IceStone Analytics</title>
<meta name="viewport" content="width=device-width, initial-scale=1">

<%-- Start of AngularJS Scripts --%>
<script src="./ANGULARJS/angular.min.js"></script>
<script src="./ANGULARJS/angular-websocket.min.js"></script>
<script src="./ANGULARJS/angular-route.min.js"></script>
<script src="./ANGULARJS/angular-animate.min.js"></script>
<script src="./ANGULARJS/angular-aria.min.js"></script>
<script src="./ANGULARJS/angular-messages.min.js"></script>
<script src="./ANGULARJS/angular-material.min.js"></script>
<script src="./ANGULARJS-CONTROLLER/NewHomeControllerNg.js"></script>
<script src="./ANGULARJS-CONTROLLER/OverviewControllerNg.js"></script>
<script src="./ANGULARJS-CONTROLLER/StockSubscribeControllerNg.js"></script>
<script src="./ANGULARJS-CONTROLLER/StockGeneralInfoControllerNg.js"></script>
<script src="./ANGULARJS-CONTROLLER/NewStockTransactionControllerNg.js"></script>
<script src="./ANGULARJS-CONTROLLER/NewStockStrategiesControllerNg.js"></script>
<script src="./ANGULARJS-CONTROLLER/NewsFeedControllerNg.js"></script>

<%-- End of AngularJS Scripts --%>
        
<%-- Start of BootStrap Scripts --%>
<script src="./RANDOMJS/ui-bootstrap-tpls-2.5.0.js"></script>
<%-- End of BootStrap Scripts --%>

<%-- Start of Style Sheets --%>
<link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="./CSS-STYLE/bootstrap.min.css">
<link rel="stylesheet" href="./CSS-STYLE/w3.css">
<link rel="stylesheet" href="./CSS-STYLE/raleway.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<%-- Start of Ng-Notification Scripts --%>
<script src="./RANDOMJS/ng-notify.min.js"></script>
<link  rel="stylesheet" href="./CSS-STYLE/ng-notify.min.css">
<%-- End of Ng-Notification Scripts --%>

<script src="./RANDOMJS/ng-google-chart.min.js" type="text/javascript"></script>

<%-- Start of NgDialog Scripts --%>
<link rel="stylesheet" href="./ng-dialog/css/ngDialog.min.css">
<link rel="stylesheet" href="./ng-dialog/css/ngDialog-theme-default.min.css">
<script src="./ng-dialog/js/ngDialog.min.js"></script>
<%-- End of NgDialog Scripts --%>
<style>
    html,body,h1,h2,h3,h4,h5 {font-family: "Raleway", sans-serif}
</style>

<body class="w3-light-grey" ng-controller="homeController">

<!-- Top container -->
<div class="w3-container w3-top w3-black w3-large w3-padding" style="z-index:4">
    <button class="w3-btn w3-hide-large w3-padding-0 w3-hover-text-grey" onclick="w3_open()"><i class="fa fa-bars"></i>  Menu</button>  
    <span class="w3-right">Icestone Analytics</span>
</div>
<!-- Sidenav/menu -->
<nav class="w3-sidenav w3-collapse w3-white w3-animate-left" style="z-index:3;width:300px;"><br>
  <div class="w3-container w3-row">
    <div class="w3-col s8">
      <span>Welcome, <strong>Admin</strong></span>&nbsp
      <a ng-click="endFeedosConnection()" class="w3-hover-none w3-hover-text-blue w3-show-inline-block"><i class="fa fa-remove"></i></a>
    </div>
  </div>
  <hr>
  <div class="w3-container">
    <h5>Dashboard</h5>
  </div>
 <div class="w3-bar-block">
    <a href="#" class="w3-bar-item w3-button w3-padding-16 w3-hide-large w3-dark-grey w3-hover-black" onclick="w3_close()" title="close menu"><i class="fa fa-remove fa-fw"></i>  Close Menu</a>
    <a href="#" class="w3-bar-item w3-button w3-padding w3-blue"><i class="fa fa-users fa-fw"></i>  Overview</a>
    <a href="#!stocksubscribe" class="w3-bar-item w3-button w3-padding"><i class="glyphicon glyphicon-pencil"></i>  Subscribe Stocks</a> 
    <a href="#!stocksleveltwoinfo" class="w3-bar-item w3-button w3-padding"><i class="glyphicon glyphicon-sort-by-attributes-alt"></i> Level Two Information</a>
    <a href="#!stockgeneralinfo" class="w3-bar-item w3-button w3-padding"><i class="fa fa-bar-chart"></i>  Stock General Information</a>
    <a href="#!stocktransaction" class="w3-bar-item w3-button w3-padding"><i class="glyphicon glyphicon-transfer"></i>  Stock Transaction Information</a>
    <a href="#!stockstrategies" class="w3-bar-item w3-button w3-padding"><i class="glyphicon glyphicon-knight"></i>  Strategies</a>
    <a href="#!newsfeed" class="w3-bar-item w3-button w3-padding"><i class="glyphicon glyphicon-bell"></i> News Feed </a>
  </div>
</nav>

<div ng-view></div>

<hr>



<script>
// Get the Sidebar
var mySidebar = document.getElementById("mySidebar");

// Get the DIV with overlay effect
var overlayBg = document.getElementById("myOverlay");

// Toggle between showing and hiding the sidebar, and add overlay effect
function w3_open() {
    document.getElementsByClassName("w3-sidenav")[0].style.display = "block";
    document.getElementsByClassName("w3-overlay")[0].style.display = "block";
};

// Close the sidebar with the close button
function w3_close() {
    document.getElementsByClassName("w3-sidenav")[0].style.display = "none";
    document.getElementsByClassName("w3-overlay")[0].style.display = "none";
};
</script>

</body>
</html>