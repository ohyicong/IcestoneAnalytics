/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myfeedos;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mycore.MyUser;
import com.mycore.StockGeneralInformation;
import com.mycore.StockLayerInformation;
import com.mycore.StockLevelOneInformation;
import com.mycore.StockTransactionInformation;
import com.mysocket.OverviewSocket;
import com.mysocket.StockTimeLineGraphSocket;
import java.util.ArrayList;

/**
 *
 * @author Owner
 */
public class SocketBroadcast {
    public static JsonObject initOverview (){
        JsonArray jsonArray = new JsonArray();
        JsonObject sendJson = new JsonObject();
        JsonArray jsonArrayTable = new JsonArray();
        MyUser.stockLevelOneInfoHashMap.values().stream().map((temp) -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("internalcode",temp.getInternalCode());
            jsonObject.addProperty("stockname",MyUser.stockGeneralInfoHashMap.get(temp.getInternalCode()).getStockName());
            jsonObject.addProperty("exchangesymbol",MyUser.stockGeneralInfoHashMap.get(temp.getInternalCode()).getExchangeSymbol());            
            jsonObject.addProperty("lastprice",temp.getLastPrice());
            jsonObject.addProperty("closingprice",temp.getClosingPrice());
            jsonObject.addProperty("bestbid",temp.getBestBid());
            jsonObject.addProperty("bestbidqty",temp.getBestBidQty());
            jsonObject.addProperty("bestask",temp.getBestAsk());
            jsonObject.addProperty("bestaskqty",temp.getBestAskQty());
            jsonObject.addProperty("tradingstatus", temp.getTradingStatus());
            if(temp.getTradingStatus().trim().equals("PRE-OPEN1")){
                double bestBidChange = FeedosTickTable.getTickToUse(temp.getBestBid(),temp.getClosingPrice());
                double bestAskChange = FeedosTickTable.getTickToUse(temp.getBestAsk(),temp.getClosingPrice());
                jsonObject.addProperty("bestbidchange",bestBidChange);
                jsonObject.addProperty("bestaskchange",bestAskChange);
            }else{
                double bestBidChange =FeedosTickTable.getTickToUse(temp.getBestBid(),temp.getLastPrice());
                double bestAskChange =FeedosTickTable.getTickToUse(temp.getBestAsk(),temp.getLastPrice());
                jsonObject.addProperty("bestbidchange",bestBidChange);
                jsonObject.addProperty("bestaskchange",bestAskChange);
            }
            return jsonObject;
        }).forEach((jsonObject) -> {
            jsonArray.add(jsonObject);
        });
        for(int i=0;i<MyUser.currentTickLowerBoundary.length;i++){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("lowerbound", MyUser.currentTickLowerBoundary[i]);
            jsonObject.addProperty("priceincrement", MyUser.currentTickPriceIncrement[i]);
            jsonArrayTable.add(jsonObject);
        }
        sendJson.add("levelone", jsonArray);
        sendJson.addProperty("status","init");
        sendJson.addProperty("sqlstatus", MyUser.isSqlRecordingOn);
        sendJson.addProperty("timesgd",MyUser.timesgd);
        sendJson.add("ticktable",jsonArrayTable);
        OverviewSocket.sendMessage(sendJson.toString());

        return sendJson;
    }
    public static JsonObject updateOverview(StockLevelOneInformation stockLevelOneInfo){
        JsonObject jsonObject = new JsonObject();
        JsonObject sendJson = new JsonObject();
        jsonObject.addProperty("internalcode",stockLevelOneInfo.getInternalCode());  
        jsonObject.addProperty("lastprice",stockLevelOneInfo.getLastPrice());
        jsonObject.addProperty("closingprice",stockLevelOneInfo.getClosingPrice());
        jsonObject.addProperty("bestbid",stockLevelOneInfo.getBestBid());
        jsonObject.addProperty("bestbidqty",stockLevelOneInfo.getBestBidQty());
        jsonObject.addProperty("bestask",stockLevelOneInfo.getBestAsk());
        jsonObject.addProperty("bestaskqty",stockLevelOneInfo.getBestAskQty());
        jsonObject.addProperty("tradingstatus", stockLevelOneInfo.getTradingStatus());
        sendJson.add("levelone", jsonObject);
        sendJson.addProperty("status","update");
        sendJson.addProperty("sqlstatus", MyUser.isSqlRecordingOn);
        sendJson.addProperty("timesgd",MyUser.timesgd);
        OverviewSocket.sendMessage(sendJson.toString());
        return sendJson;
    }
     public static JsonObject deleteOverview(StockGeneralInformation stockGenInfo){
        StockLevelOneInformation stockLevelOneInfo = MyUser.stockLevelOneInfoHashMap.get(stockGenInfo.getInternalCode());
        JsonObject jsonObject = new JsonObject();
        JsonObject sendJson = new JsonObject();
        jsonObject.addProperty("internalcode",stockLevelOneInfo.getInternalCode());
        jsonObject.addProperty("stockname",MyUser.stockGeneralInfoHashMap.get(stockLevelOneInfo.getInternalCode()).getStockName());
        jsonObject.addProperty("exchangesymbol",MyUser.stockGeneralInfoHashMap.get(stockLevelOneInfo.getInternalCode()).getExchangeSymbol());   
        jsonObject.addProperty("lastprice",stockLevelOneInfo.getLastPrice());
        jsonObject.addProperty("closingprice",stockLevelOneInfo.getClosingPrice());
        jsonObject.addProperty("bestbid",stockLevelOneInfo.getBestBid());
        jsonObject.addProperty("bestbidqty",stockLevelOneInfo.getBestBidQty());
        jsonObject.addProperty("bestask",stockLevelOneInfo.getBestAsk());
        jsonObject.addProperty("bestaskqty",stockLevelOneInfo.getBestAskQty());
        jsonObject.addProperty("tradingstatus", stockLevelOneInfo.getTradingStatus());
        if(stockLevelOneInfo.getTradingStatus().trim().equals("PRE-OPEN1")&&stockLevelOneInfo.getLastPrice()==0){
            System.out.println("Before market opens");
            double bestBidChange =(stockLevelOneInfo.getBestBid()-stockLevelOneInfo.getClosingPrice())/stockLevelOneInfo.getBestBid();
            double bestAskChange = (stockLevelOneInfo.getBestAsk()-stockLevelOneInfo.getClosingPrice())/stockLevelOneInfo.getBestAsk();
            jsonObject.addProperty("bestbidchange",bestBidChange);
            jsonObject.addProperty("bestaskchange",bestAskChange);
        }else{
            System.out.println("After market opens");
            double bestBidChange =(stockLevelOneInfo.getBestBid()-stockLevelOneInfo.getLastPrice())/stockLevelOneInfo.getBestBid();
            double bestAskChange = (stockLevelOneInfo.getBestAsk()-stockLevelOneInfo.getLastPrice())/stockLevelOneInfo.getBestAsk();
            jsonObject.addProperty("bestbidchange",bestBidChange);
            jsonObject.addProperty("bestaskchange",bestAskChange);
        } 
        sendJson.add("levelone", jsonObject);
        sendJson.addProperty("status","delete");
        sendJson.addProperty("sqlstatus", MyUser.isSqlRecordingOn);
        OverviewSocket.sendMessage(sendJson.toString());
        return sendJson;
     }
     public static void updateStockTimeLineLevelOne (StockLevelOneInformation stockLevelOneInfo){
         int internalCode=0;
         double lastPrice=0,volumeTraded=0;
         String timesgd="nil";
         JsonObject jsonObject = new JsonObject();
         JsonObject jsonSend = new JsonObject();
         internalCode = stockLevelOneInfo.getInternalCode();
         lastPrice=stockLevelOneInfo.getLastPrice();
         timesgd =stockLevelOneInfo.getTimeSGD();
         volumeTraded = stockLevelOneInfo.getVolumeTraded();
         jsonObject.addProperty("lastprice", lastPrice);
         jsonObject.addProperty("volumetraded", volumeTraded);
         jsonObject.addProperty("timesgd", timesgd);
         jsonSend.addProperty("infotype", "one");
         jsonSend.addProperty("internalcode",internalCode);
         jsonSend.add("data",jsonObject);
         StockTimeLineGraphSocket.sendMessage(jsonSend.toString());
     }
     public static void updateStockTimeLineLevelTwo(ArrayList stockLayerInfos,String timeSGD){
         
         JsonObject jsonSend = new JsonObject();
         JsonArray jsonArray = new JsonArray();
         int internalCode=0;
         for(int i=0;i<stockLayerInfos.size();i++){
            StockLayerInformation stockLevelTwoInfo = (StockLayerInformation)stockLayerInfos.get(i);
            JsonObject jsonObject = new JsonObject();
            internalCode=stockLevelTwoInfo.getInternalCodes();
            int level = stockLevelTwoInfo.getLevel();
            double bid = stockLevelTwoInfo.getBid();
            double bidQty = stockLevelTwoInfo.getBidQty();
            double ask = stockLevelTwoInfo.getAsk();
            double askQty =stockLevelTwoInfo.getAskQty();
            double bidQueue = stockLevelTwoInfo.getBidQueue();
            double askQueue = stockLevelTwoInfo.getAskQueue();
            jsonObject.addProperty("level", level);
            jsonObject.addProperty("bid", bid);
            jsonObject.addProperty("bidqty", bidQty);
            jsonObject.addProperty("ask", ask);
            jsonObject.addProperty("askqty", askQty);
            jsonObject.addProperty("bidqueue", bidQueue);
            jsonObject.addProperty("askqueue", askQueue);
            jsonObject.addProperty("timesgd", timeSGD);
            jsonArray.add(jsonObject);
         }
         jsonSend.addProperty("infotype","two");
         jsonSend.addProperty("internalcode",internalCode);
         jsonSend.add("data",jsonArray);
         StockTimeLineGraphSocket.sendMessage(jsonSend.toString());
     }
     
     public static void updateStockTransaction (StockTransactionInformation stockTransactionInfo){
         JsonObject jsonObject = new JsonObject();
         JsonObject jsonSend = new JsonObject();
         jsonSend.addProperty("internalcode",stockTransactionInfo.getInternalCode());
         jsonSend.addProperty("infotype","trans");
         jsonObject.addProperty("quantity",stockTransactionInfo.getQuantity());
         jsonObject.addProperty("price",stockTransactionInfo.getTradePrice());
         jsonObject.addProperty("transtype",stockTransactionInfo.getTransType());
         jsonObject.addProperty("timesgd",stockTransactionInfo.getTimeSGD());
         jsonSend.add("data",jsonObject);
         //StockTimeLineGraphSocket.sendMessage(jsonSend.toString());
     }
     
}
