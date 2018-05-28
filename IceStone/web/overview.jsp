<%-- 
    Document   : dashboard
    Created on : Mar 6, 2018, 4:22:07 PM
    Author     : Owner
--%>

<!-- !PAGE CONTENT! -->
<div class="w3-main" style="margin-left:300px;margin-top:43px;">

  <!-- Header -->
  <header class="w3-container" style="padding-top:22px">
    <h5><b><i class="fa fa-dashboard"></i> Overview</b></h5>
  </header>

  <div class="w3-row-padding w3-margin-bottom">
    <div class="w3-quarter">
      <div class="w3-container w3-blue w3-padding-16">
        <div class="w3-left"><i class="glyphicon glyphicon-eye-open w3-xxlarge"></i></div>
        <div class="w3-right">
            <h3>{{stocklevelone.length}}</h3>
        </div>
        <div class="w3-clear"></div>
        <h4>Watchlist</h4>
      </div>
    </div>  
    <div class="w3-quarter">
        <div class="w3-container w3-teal w3-padding-16" ng-click="ToggleSqlRecording(isSqlRecordingOn[0].data)">
            <div class="w3-left">
                <i class="glyphicon glyphicon-pencil w3-xxlarge "></i>
            </div>
            <div class="w3-right">
              <h3>{{isSqlRecordingOn[0].data}}</h3>
            </div>
            <div class="w3-clear"></div>
            <h4>SQL Record</h4>
      </div>
    </div>
    <div class="w3-half">
        <div class="w3-container w3-orange w3-padding-16" style="color: white" >
            <div class="w3-left">
                <i class="glyphicon glyphicon-time w3-xxlarge "></i>
            </div>
            <div class="w3-center">
              <h3 style="font-family: 'Arial', Helvetica, sans-serif;">{{timesgd[0]}}</h3>
            </div>
            <div class="w3-clear"></div>
            <h4>Time</h4>
      </div>
    </div>
  </div>  
  <hr>
   <div class="w3-margin">
            <div class="w3-container w3-teal w3-padding-4 w3-margin-bottom">
                <h5 style="display:inline-block">Stock Feeds &nbsp &nbsp</h5> 
                <div class="w3-margin-top w3-margin-bottom" style="display:inline-block">
                <select ng-model="selected" ng-options="option.type for option in options" style="color:black"></select>
            </div>
            
            </div>
            <div style="height : 750px;
                                display:block;
                                overflow-y:auto;
                                border : 1px solid black;">
            <table class="w3-table w3-teal w3-bordered w3-border" >
                <tr><th>Name</th><th>Symbol</th><th>BestBid Qty</th><th>BestBid</th><th>BestAsk</th><th>BestAsk Qty</th><th>Last Price</th><th>Closing Price</th><th>BidChange Ticks</th><th>AskChange Ticks</th></tr>
                <tr class="w3-white" ng-repeat="stock in stocklevelone |orderBy:selected.func">
                    <td>{{stock.stockname}}</td>
                    <td>{{stock.exchangesymbol}}</td>
                    <td>{{stock.bestbidqty}}</td>
                    <td>{{stock.bestbid|number:3}}</td>
                    <td>{{stock.bestask|number:3}}</td>
                    <td>{{stock.bestaskqty}}</td>
                    <td>{{stock.lastprice|number:3}}</td>
                    <td>{{stock.closingprice|number:3}}</td>
                    <td ng-style="{ 'background': stock.color1 }">{{(stock.bestbidchange|number:0)}}</td>
                    <td ng-style="{ 'background': stock.color2 }">{{(stock.bestaskchange|number:0)}}</td>
                </tr>
            </table>
            </div>
       </div>
  <hr>
    <div class="w3-container w3-margin-top" style="height:400px">
        <iframe height="100%" width="100%" sandbox="allow-forms allow-same-origin allow-scripts" src="http://192.168.1.11:8084/IceStone/volumebreakthroughalert.jsp" ></iframe>
    </div>
 </div>
 <hr>
  <!-- Footer -->
  <div class="w3-container w3-padding w3-right">
    <p>Powered by NorthPoint Global</p>
  </div>

  <!-- End page content -->
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