<div class="w3-main" style="margin-left:300px;margin-top:43px;" ng-init="getNewsFeed();getShareBuyBack();">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Ice Stone</title>
    </head>
    <header class="w3-container" style="padding-top:22px">
        <h5><b><i class="glyphicon glyphicon-bell"></i> News Feed</b></h5>
    </header>
    <div class="w3-row-padding">
        <nav class="w3-container w3-panel w3-teal w3-padding-top w3-padding-bottom" style="padding-left: 2px">
            <input ng-click="switchMenu('Disclosure Of Interest');getNewsFeed();" type="button" value="Disclosure Of Interest" style="background-color:transparent; border:1px;border-right: 1px solid white;"> 
            <input ng-click="switchMenu('Shares Buy Back');getSharesBuyBack();" type="button" value="Shares Buy Back" style="background-color:transparent; border:none;border-right: 1px solid white;"> 
            <input ng-click="switchMenu('Bloomberg')" type="button" value="CNBC" style="background-color:transparent; border:none;border-right: 1px solid white;"> 
            <input ng-click="switchMenu('BBC')" type="button" value="BBC" style="background-color:transparent; border:1px;border-right: 1px solid white;"> 
            <input ng-click="switchMenu('CNA')" type="button" value="CNA" style="background-color:transparent; border:1px;border-right: 1px solid white;"> 
            <div class="w3-right">
                <input type="button" value="Refresh" style="background-color:transparent; border-style: solid; border-color: white " ng-click="getNewsFeed();getSharesBuyBack();"> 
            </div>
        </nav>
        <div class="w3-container w3-white" style="width:auto;height: 750px; padding:0; overflow-y:auto; ">
            <div ng-show=isShown[1] height="100%" width="100%" style="padding:1px; margin:0px;">  
                <div ng-repeat="result in sharesBuyBackFinalResults">
                    <div class="w3-container w3-light-grey w3-padding" >
                        <p class="w3-center">{{result.issuer}}</p>
                    </div>
                    <div class="w3-container w3-white w3-padding">
                        <p ng-repeat="elementTextOne in result.textone">{{elementTextOne}}</p>
                        <p ng-repeat="elementTextTwo in result.texttwo">{{elementTextTwo}}</p>
                    </div>
                </div>
            </div>     
            <div ng-show=isShown[0] height="100%" width="100%" style="padding:1px; margin:0px;">   
                <div ng-repeat="(key,newsFeedIndividual) in newsFeedResults" >
                    <div class="w3-container" style="padding:0px">
                        <div class="w3-container w3-padding w3-white" style="color:black">
                            <table>
                                <tr padding="10px">
                                    <th width="14%" margin="5px">Issuer</th>
                                    <th width="14%" margin="5px">Shareholders</th>
                                    <th width="14%" margin="5px">Before Amount</th>
                                    <th width="14%" margin="5px">After Amount</th>
                                    <th width="14%" margin="5px">Transacted Amount</th>
                                    <th width="14%" margin="5px">Type</th>
                                    <th width="14%" margin="5px">Date</th>
                                </tr>
                                <tr padding="10px">
                                    <td valign="top" width="14%" margin="5px">{{newsFeedIndividual[0].issuer}}</td>
                                    <td valign="top" width="14%" margin="5px">                                
                                        <p ng-repeat="(key,element) in newsFeedIndividual"> {{(key+1)+". "+element.shareholders+" "}} </p>
                                    </td>
                                    <td valign="top" width="14%" margin="5px">
                                        <p ng-repeat="(key,element) in newsFeedIndividual"> {{(key+1)+". "+element.beforeamount+" "}} </p>
                                    </td>
                                    <td valign="top" width="14%" margin="5px">
                                        <p ng-repeat="(key,element) in newsFeedIndividual"> {{(key+1)+". "+element.afteramount+" "}} </p>
                                    </td>
                                    <td valign="top" width="14%" margin="5px">
                                        <p ng-repeat="(key,element) in newsFeedIndividual"> {{(key+1)+". "+element.transactionamount+" "}} </p>
                                    </td>
                                    <td valign="top" width="14%" margin="5px">
                                        <p ng-repeat="(key,element) in newsFeedIndividual"> {{(key+1)+". "+element.transtype+" "}} </p>
                                    </td>
                                    <td valign="top" width="14%" margin="5px">{{newsFeedIndividual[0].date}}</td>     
                                </tr>    
                            </table>
                        </div>
                        <div class="w3-container w3-light-grey w3-padding">
                            <center>
                                <input type="button" class="btn-primary" ng-click="openLink(newsFeedIndividual[0].pdflink)" value="Download PDF">
                            </center>
                        </div>
                    </div>    
                </div>    
            </div>    
            <iframe ng-show=isShown[2] height="100%" width="100%" sandbox="allow-forms allow-same-origin allow-scripts" src="https://www.cnbc.com/world/?region=worldhttps://www.bloomberg.com/asia" ></iframe>
            <iframe ng-show=isShown[3] height="100%" width="100%" sandbox="allow-forms allow-same-origin allow-scripts" src="http://www.bbc.com/news/world" ></iframe>
            <iframe ng-show=isShown[4] height="100%" width="100%" sandbox="allow-forms allow-same-origin allow-scripts" src="https://www.channelnewsasia.com" ></iframe>
        </div>
        <div class="w3-right">
            <p ng-show=!isSyncing style="font-size:10px;margin-right:2px " ng-click ="SyncNewsFeed();">
                Click to sync
                <i class="glyphicon glyphicon-refresh" ></i>
            </p>
            <p ng-show=isSyncing style="font-size:10px;margin-right:2px ">
                Syncing page {{currentPdfPage}}... 
                <md-progress-circular md-mode="indeterminate" md-diameter="10"></md-progress-circular>
            </p>
            
        </div>
    </div>
    
    
    <body>
        
    </body>
</div>

<script>
    var iframe = document.getElementsByTagName('iframe')[0];
    var url = iframe.src;
    var getData = function (data) {
        if (data && data.query && data.query.results && data.query.results.resources && data.query.results.resources.content && data.query.results.resources.status == 200) loadHTML(data.query.results.resources.content);
        else if (data && data.error && data.error.description) loadHTML(data.error.description);
        else loadHTML('Error: Cannot load ' + url);
    };
    var loadURL = function (src) {
        url = src;
        var script = document.createElement('script');
        script.src = 'http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20data.headers%20where%20url%3D%22' + encodeURIComponent(url) + '%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=getData';
        document.body.appendChild(script);
    };
    var loadHTML = function (html) {
        iframe.src = 'about:blank';
        iframe.contentWindow.document.open();
        iframe.contentWindow.document.write(html.replace(/<head>/i, '<head><base href="' + url + '"><scr' + 'ipt>document.addEventListener("click", function(e) { if(e.target && e.target.nodeName == "A") { e.preventDefault(); parent.loadURL(e.target.href); } });</scr' + 'ipt>'));
        iframe.contentWindow.document.close();
    };

    loadURL(iframe.src);
</script>
<style>
    .dialogdemoBasicUsage #popupContainer {
        position: relative;
        font-size: 100px;
    }
</style>