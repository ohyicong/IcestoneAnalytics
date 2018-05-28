<%-- 
    Document   : leveltwoinformation
    Created on : Apr 24, 2018, 9:35:58 AM
    Author     : Owner
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%-- Start of AngularJS Scripts --%>
        <script src="./ANGULARJS/angular.min.js"></script>
        <script src="./ANGULARJS-CONTROLLER/NewLevelTwoInformationControllerNg.js"></script>
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
        <style>
            @-webkit-keyframes yellowWhite {  
                0% { background-color: lightgoldenrodyellow;}
                50% { background-color: lightgoldenrodyellow;}
                100% { background-color: lightgoldenrodyellow;}
            }
            .norm{
                background-color: white;
            }
            .blink{
              
                background-color: white;
                -webkit-animation-name: yellowWhite;
                /* -webkit-animation-name: blackWhiteFade; */
                -webkit-animation-iteration-count: 1;  
                -webkit-animation-duration: 2s; 
            }
            th{
                
                padding:2px;
                white-space: nowrap;
                text-align: center;
                text-overflow: "..";
                overflow:hidden;
            }
            tr{
         
            }
            td{
                
                white-space: nowrap;
                text-align: center;
                padding:2px;
                text-overflow: "..";
                overflow:hidden;
            }
            red{
                color:red;
            }
            green{
                color:green;
            }
        </style>    
        <title>JSP Page</title>
    </head>
    <body ng-app="leveltwoinfoApp" ng-controller="leveltwoinfoController" ng-init="getLevelTwoInfo()">
        <div class="w3-container w3-light-grey w3-padding-0">
                <table ng-repeat="collection in collections[0]" style="width:32%;height:10%;table-layout: fixed;margin:5px;display:inline-table;background-color:white;border:1px solid black;" my-draggable>
                    <tr><th colspan="6" class="w3-teal">{{collection.stocksname}}</th></tr>
                    <tr>
                        <th style="width:5%">Bid Q</th>
                        <th style="width:35%">Bid Qty</th>
                        <th style="width:10%">Bid</th>
                        <th style="width:10%">Ask</th>
                        <th style="width:35%">Ask Qty</th>
                        <th style="width:5%">Ask Q</th>
                    </tr>
                    <tr ng-repeat="element in collection.data" >
                        <td class={{element.boxcolors[0]}} ng-style="{ 'color': element.numbercolors[0] }">{{element.bidqueue}}</td>
                        <td class={{element.boxcolors[1]}} ng-style="{ 'color': element.numbercolors[1] }">{{element.bidqty}}</td>
                        <td class={{element.boxcolors[2]}} ng-style="{ 'color': element.numbercolors[2] }">{{element.bid}}</td>
                        <td class={{element.boxcolors[3]}} ng-style="{ 'color': element.numbercolors[3] }">{{element.ask}}</td>
                        <td class={{element.boxcolors[4]}} ng-style="{ 'color': element.numbercolors[4] }">{{element.askqty}}</td>
                        <td class={{element.boxcolors[5]}} ng-style="{ 'color': element.numbercolors[5] }">{{element.askqueue}}</td>
                    </tr>
                </table>
                
                    
       <div>
       
            
    </body>
</html>
