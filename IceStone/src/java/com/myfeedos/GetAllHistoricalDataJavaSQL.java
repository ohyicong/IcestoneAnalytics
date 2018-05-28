/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myfeedos;

import com.feedos.api.requests.MBLLayer;
import com.feedos.api.requests.OrderBookSide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mycore.MyUser;
import com.mycore.StockLayerInformation;
import com.mycore.StockLevelOneInformation;
import com.mycore.StockLevelTwoInformation;
import com.mycore.StockTransactionInformation;
import com.mysql.SqlQuery;
import com.template.StockInternalCode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 *
 * @author Owner
 */
public class GetAllHistoricalDataJavaSQL {
    public static JsonObject start(int internalCode) throws ClassNotFoundException, SQLException{
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("internalcode", internalCode);
        jsonObject.add("leveloneinfo", packageLevelOneResult(internalCode));
        jsonObject.add("leveltwoinfo",packageLevelTwoResult(internalCode));
        return jsonObject;
    }
    //Below is to get all levelone information from java 
    public static JsonArray packageLevelOneResult (int internalCode) throws SQLException{
        System.out.println("HistoricalData: Packaging level one");
        JsonArray jsonArray = new JsonArray();
        double lastPrice=0;
        double bestBidQty=0;
        double bestAskQty=0;
        String timeSGD="NIL";
        if(MyUser.prevDayAllStockLevelOneInfoHashMap.get(internalCode)!=null){
            ArrayList prevAlLevelOne = MyUser.prevDayAllStockLevelOneInfoHashMap.get(internalCode);
            if(!prevAlLevelOne.isEmpty()){
                for(int i=0;i<prevAlLevelOne.size();i++){
                    JsonObject jsonObject = new JsonObject();
                    StockLevelOneInformation stockLevelOneInfo = (StockLevelOneInformation) prevAlLevelOne.get(i);
                    lastPrice = stockLevelOneInfo.getLastPrice();
                    bestBidQty= stockLevelOneInfo.getBestBid();
                    bestAskQty=stockLevelOneInfo.getBestAskQty();
                    timeSGD = stockLevelOneInfo.getTimeSGD();
                    jsonObject.addProperty("lastprice", lastPrice);
                    jsonObject.addProperty("bestbidqty", bestBidQty);
                    jsonObject.addProperty("bestaskqty", bestAskQty);
                    jsonObject.addProperty("timesgd", timeSGD);
                    jsonArray.add(jsonObject);            
                }
            }
        }
        if(MyUser.allStockLevelOneInfoHashMap.get(internalCode)!=null){
            ArrayList alLevelOne = MyUser.allStockLevelOneInfoHashMap.get(internalCode);
            for(int i=0;i<alLevelOne.size();i++){
                JsonObject jsonObject = new JsonObject();
                StockLevelOneInformation stockLevelOneInfo = (StockLevelOneInformation) alLevelOne.get(i);
                lastPrice = stockLevelOneInfo.getLastPrice();
                bestBidQty= stockLevelOneInfo.getBestBid();
                bestAskQty=stockLevelOneInfo.getBestAskQty();
                timeSGD = stockLevelOneInfo.getTimeSGD();
                jsonObject.addProperty("lastprice", lastPrice);
                jsonObject.addProperty("bestbidqty", bestBidQty);
                jsonObject.addProperty("bestaskqty", bestAskQty);
                jsonObject.addProperty("timesgd", timeSGD);
                jsonArray.add(jsonObject);            
            }
        }
        return jsonArray;
    }
    //Below is to get all leveltwo information from java 
    public static JsonArray packageLevelTwoResult(int internalCode) throws SQLException, ClassNotFoundException{
        System.out.println("HistoricalData: Packaging level two");
        JsonArray jsonSend = new JsonArray();
        JsonArray jsonArray = new JsonArray();
        boolean isFirst = true;
        if(MyUser.prevDayAllStockLevelTwoInfoHashMap.get(internalCode)!=null){
            System.out.println("Processing previous day information");
            ArrayList prevAlLevelTwo = MyUser.prevDayAllStockLevelTwoInfoHashMap.get(internalCode);
            for(int i=0;i<prevAlLevelTwo.size();i++){
                //JsonObject jsonObject = new JsonObject();
                StockLevelTwoInformation stockLevelTwoInfo = (StockLevelTwoInformation) prevAlLevelTwo.get(i);
                ArrayList mblLayer= stockLevelTwoInfo.getMBLLayer();
                String timeQH = stockLevelTwoInfo.getTimeQH();
                String timeSGD = stockLevelTwoInfo.getTimeSGD();
                jsonSend.add(processMBL(internalCode,mblLayer,timeQH,timeSGD));
            }   
        }
        if(MyUser.allStockLevelTwoInfoHashMap.get(internalCode)!=null){
            System.out.println("Processing today information");
            ArrayList alLevelTwo = MyUser.allStockLevelTwoInfoHashMap.get(internalCode);
            for(int i=0;i<alLevelTwo.size();i++){
                StockLevelTwoInformation stockLevelTwoInfo = (StockLevelTwoInformation) alLevelTwo.get(i);
                ArrayList mblLayer= stockLevelTwoInfo.getMBLLayer();
                String timeQH = stockLevelTwoInfo.getTimeQH();
                String timeSGD = stockLevelTwoInfo.getTimeSGD();
                jsonSend.add(processMBL(internalCode,mblLayer,timeQH,timeSGD));

            }
        }
        return jsonSend; 
    }
    public static JsonArray packageTransactionResult(ResultSet transactionResult) throws SQLException{
        JsonArray jsonArray = new JsonArray();
        while(transactionResult.next()){
            JsonObject jsonObject = new JsonObject();
            double tradePrice = transactionResult.getDouble("tradeprice");
            double quantity = transactionResult.getDouble("quantity");
            String type = transactionResult.getString("type");
            String timeSGD = transactionResult.getTimestamp("timesgd").toString();
            jsonObject.addProperty("tradeprice", tradePrice);
            jsonObject.addProperty("quantity", quantity);
            jsonObject.addProperty("type", type);
            jsonObject.addProperty("timesgd", timeSGD);
            jsonArray.add(jsonObject);
        }
        
        return jsonArray;
    }
    
    public static JsonArray processMBL(int internalCode,ArrayList mblLayer,String timeQH,String timeSGD) throws ClassNotFoundException{
        JsonArray jsonArray = new JsonArray();
        StockLayerInformation stockLayerInformation;
        for(int i=0;i<mblLayer.size();i++){
            JsonObject jsonObject = new JsonObject();
            stockLayerInformation = (StockLayerInformation) mblLayer.get(i);
            jsonObject.addProperty("level", stockLayerInformation.getLevel());
            jsonObject.addProperty("bid", stockLayerInformation.getBid());
            jsonObject.addProperty("bidqty", stockLayerInformation.getBidQty());
            jsonObject.addProperty("ask", stockLayerInformation.getAsk());
            jsonObject.addProperty("askqty", stockLayerInformation.getAskQty());
            jsonObject.addProperty("bidqueue", stockLayerInformation.getBidQueue());
            jsonObject.addProperty("askqueue", stockLayerInformation.getAskQueue());
            jsonObject.addProperty("timesgd", timeSGD);
            jsonArray.add(jsonObject);
           
        }
        return jsonArray;
    }
    public static JsonArray startTransactionInfo(int stockInternalCode){
        ArrayList alTransaction = MyUser.allStockTransactionInfoHashMap.get(stockInternalCode);
        JsonArray jsonArray = new JsonArray();
        for(int i=0;i<alTransaction.size();i++){
            StockTransactionInformation stockTransactionInfo = (StockTransactionInformation) alTransaction.get(i);
            double quantity = stockTransactionInfo.getQuantity();
            double tradePrice = stockTransactionInfo.getTradePrice();
            String transType = stockTransactionInfo.getTransType();
            String timeSGD = stockTransactionInfo.getTimeSGD();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("tradeprice",tradePrice);
            jsonObject.addProperty("quantity", quantity);
            jsonObject.addProperty("transtype",transType);
            jsonObject.addProperty("timesgd", timeSGD);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
 
}