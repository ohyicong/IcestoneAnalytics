<%-- 
    Document   : stocksubscribe
    Created on : Mar 6, 2018, 5:15:52 PM
    Author     : Owner
--%>

<div class="w3-main" style="margin-left:300px;margin-top:43px;" >

    <!-- Header -->
    <header class="w3-container" style="padding-top:22px;margin-left: 10px">
        <h5><b><i class="glyphicon glyphicon-sort-by-attributes-alt"></i> Stock level two information</b></h5>
    </header>
    <div class="w3-container" style="height:800px;padding:0px">
        <div class="w3-row-padding"></div>                
        <iframe height="100%" width="100%" style="padding:0px" sandbox="allow-forms allow-same-origin allow-scripts" src="http://192.168.1.11:8084/IceStone/leveltwoinformation.jsp" ></iframe>
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