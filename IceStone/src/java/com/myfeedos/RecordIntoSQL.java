/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myfeedos;

import com.mycore.MyUser;
import com.mycore.StockLayerInformation;
import com.mycore.StockLevelOneInformation;
import com.mycore.StockLevelTwoInformation;
import com.mycore.StockTransactionInformation;
import com.myfilters.FilterTime;
import com.mysql.SqlInsert;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author Owner
 */
public class RecordIntoSQL {
    public static void levelOneInfo (int internalCode) throws ClassNotFoundException, SQLException{
        ArrayList ALlevelOne = MyUser.allStockLevelOneInfoHashMap.get(internalCode);
        String initFormat = "insert into stockslevelone (internalcodes,lastprice,bestbid,bestbidqty,bestask,bestaskqty,type,marketstatus,timeqh,timesgd) values (%d,%f,%f,%f,%f,%f,'%s','%s','%s','%s')";
        String basicFormat = ",(%d,%f,%f,%f,%f,%f,'%s','%s','%s','%s')";
        String stmtSend="";
        for(int i=0;i<ALlevelOne.size();i++){
            StockLevelOneInformation stockLevelOneInfo = (StockLevelOneInformation) ALlevelOne.get(i);
            if(i==0){
                stmtSend = String.format(initFormat,stockLevelOneInfo.getInternalCode(),stockLevelOneInfo.getLastPrice(),stockLevelOneInfo.getBestBid(),stockLevelOneInfo.getBestBidQty(),stockLevelOneInfo.getBestAsk(),stockLevelOneInfo.getBestAskQty(),stockLevelOneInfo.getType(),stockLevelOneInfo.getTradingStatus(),stockLevelOneInfo.getTimeQH(),stockLevelOneInfo.getTimeSGD());
            }else{
                stmtSend+=String.format(basicFormat,stockLevelOneInfo.getInternalCode(),stockLevelOneInfo.getLastPrice(),stockLevelOneInfo.getBestBid(),stockLevelOneInfo.getBestBidQty(),stockLevelOneInfo.getBestAsk(),stockLevelOneInfo.getBestAskQty(),stockLevelOneInfo.getType(),stockLevelOneInfo.getTradingStatus(),stockLevelOneInfo.getTimeQH(),stockLevelOneInfo.getTimeSGD());
            }
        }
        SqlInsert.start(stmtSend);
    }
    public static void transactionInfo (int internalCode) throws ClassNotFoundException, SQLException{
        ArrayList ALtransaction = MyUser.allStockTransactionInfoHashMap.get(internalCode);
        String initFormat= "insert into stockstransaction (internalcodes,tradeprice,quantity,transtype,timeqh,timesgd) values (%d,%f,%f,'%s','%s','%s')";
        String initFormatIntraday="insert into intradaystockstransaction (internalcodes,tradeprice,quantity,transtype,timeqh,timesgd) values (%d,%f,%f,'%s','%s','%s')";
        String basicFormat= ",(%d,%f,%f,'%s','%s','%s')";
        String stmtSend="",stmtSendIntraday="";
        for(int i=0;i<ALtransaction.size();i++){
            StockTransactionInformation stockTransactionInfo = (StockTransactionInformation) ALtransaction.get(i);
            if(i==0){
                stmtSend+=String.format(initFormat,internalCode,stockTransactionInfo.getTradePrice(),stockTransactionInfo.getQuantity(),stockTransactionInfo.getTransType(),stockTransactionInfo.getTimeQH(),stockTransactionInfo.getTimeSGD());
                stmtSendIntraday+=String.format(initFormatIntraday,internalCode,stockTransactionInfo.getTradePrice(),stockTransactionInfo.getQuantity(),stockTransactionInfo.getTransType(),stockTransactionInfo.getTimeQH(),stockTransactionInfo.getTimeSGD());
            }else{
                stmtSend+=String.format(basicFormat,internalCode,stockTransactionInfo.getTradePrice(),stockTransactionInfo.getQuantity(),stockTransactionInfo.getTransType(),stockTransactionInfo.getTimeQH(),stockTransactionInfo.getTimeSGD());
                stmtSendIntraday+=String.format(basicFormat,internalCode,stockTransactionInfo.getTradePrice(),stockTransactionInfo.getQuantity(),stockTransactionInfo.getTransType(),stockTransactionInfo.getTimeQH(),stockTransactionInfo.getTimeSGD());
            }
            
        }
        SqlInsert.start(stmtSend);
        SqlInsert.start(stmtSendIntraday);
        
    }
    public static void levelTwoInfo (int internalCode) throws ClassNotFoundException, SQLException{
        ArrayList ALLevelTwo=MyUser.allStockLevelTwoInfoHashMap.get(internalCode);
        String sendStmt ="insert into stocksleveltwo (internalcodes,level,bid,bidqty,bidqueue,ask,askqty,askqueue,timeqh,timesgd) values ";
        String sendStmtIntraday="insert into intradaystocksleveltwo (internalcodes,level,bid,bidqty,bidqueue,ask,askqty,askqueue,timeqh,timesgd) values ";
        //Batching function
        int batchSize = (int) Math.floor((double)ALLevelTwo.size() / 5000.0);
        int remainderSize = ALLevelTwo.size() % 5000;
        //Normal batch routing
        for(int x=0;x<batchSize;x++){
            for(int i=x*5000;i<((x*5000)+5000);i++){
                StockLevelTwoInformation stockLevelTwoInfo = (StockLevelTwoInformation) ALLevelTwo.get(i);
                ArrayList mblLayer = stockLevelTwoInfo.getMBLLayer();
                String timeQH = stockLevelTwoInfo.getTimeQH();
                String timeSGD = stockLevelTwoInfo.getTimeSGD();
                if(i==x*5000){
                    String temp =processMBL(true,internalCode,mblLayer,timeQH,timeSGD);
                    sendStmt +=temp;
                    sendStmtIntraday+=temp;
                }else{
                    String temp =processMBL(false,internalCode,mblLayer,timeQH,timeSGD);
                    sendStmt += temp;
                    sendStmtIntraday+=temp;
                }
            }
            SqlInsert.start(sendStmt);
            SqlInsert.start(sendStmtIntraday);
            sendStmt ="insert into stocksleveltwo (internalcodes,level,bid,bidqty,bidqueue,ask,askqty,askqueue,timeqh,timesgd) values ";
            sendStmtIntraday="insert into intradaystocksleveltwo (internalcodes,level,bid,bidqty,bidqueue,ask,askqty,askqueue,timeqh,timesgd) values ";
        }
        //Reminder routine
        for(int i=(batchSize*5000);i<((batchSize*5000)+remainderSize);i++){
            StockLevelTwoInformation stockLevelTwoInfo = (StockLevelTwoInformation) ALLevelTwo.get(i);
            ArrayList mblLayer = stockLevelTwoInfo.getMBLLayer();
            String timeQH = stockLevelTwoInfo.getTimeQH();
            String timeSGD = stockLevelTwoInfo.getTimeSGD();
            if(i==(batchSize*5000)){
                    String temp =processMBL(true,internalCode,mblLayer,timeQH,timeSGD);
                    sendStmt +=temp;
                    sendStmtIntraday+=temp;
            }else{
                    String temp =processMBL(false,internalCode,mblLayer,timeQH,timeSGD);
                    sendStmt += temp;
                    sendStmtIntraday+=temp;
            }
        }
        SqlInsert.start(sendStmt);
        SqlInsert.start(sendStmtIntraday);
        
    }

    public static String processMBL(boolean isFirst, int internalCode,ArrayList mblLayer,String timeQH,String timeSGD) throws ClassNotFoundException{
        String basicFormatter =",(%d,%d,%f,%f,%d,%f,%f,%d,'%s','%s')",sendStmt="",initFormatter = "(%d,%d,%f,%f,%d,%f,%f,%d,'%s','%s')";
        StockLayerInformation stockLayerInfo;
        if(isFirst){
            for(int i=0;i<mblLayer.size();i++){
                stockLayerInfo = (StockLayerInformation) mblLayer.get(i);
                if(i==0){
                    sendStmt+=String.format(initFormatter,internalCode,i,stockLayerInfo.getBid(),stockLayerInfo.getBidQty(),stockLayerInfo.getBidQueue(),stockLayerInfo.getAsk(),stockLayerInfo.getAskQty(),stockLayerInfo.getAskQueue(),timeQH,timeSGD);
                    
                }else{
                    sendStmt+=String.format(basicFormatter,internalCode,i,stockLayerInfo.getBid(),stockLayerInfo.getBidQty(),stockLayerInfo.getBidQueue(),stockLayerInfo.getAsk(),stockLayerInfo.getAskQty(),stockLayerInfo.getAskQueue(),timeQH,timeSGD);
                }
            }
        }else{
            for(int i=0;i<mblLayer.size();i++){
                stockLayerInfo = (StockLayerInformation) mblLayer.get(i);
                sendStmt+=String.format(basicFormatter,internalCode,i,stockLayerInfo.getBid(),stockLayerInfo.getBidQty(),stockLayerInfo.getBidQueue(),stockLayerInfo.getAsk(),stockLayerInfo.getAskQty(),stockLayerInfo.getAskQueue(),timeQH,timeSGD);
            }
        }
        return sendStmt;
    }
    
    public static void dailyInformation (int internalCode) throws ClassNotFoundException, SQLException{
        String stmtFormat = "insert into dailyinformation (internalcodes,closingprice,bestbid,bestbidqty,bestask,bestaskqty,totalasset,totalvolume,date) values (%d,%f,%f,%f,%f,%f,%f,%f,'%s')";
        String stmtSend ="";
        double closingPrice=0,bestBid=0,bestBidQty=0,bestAsk=0,bestAskQty=0,totalAssets=0,totalVolume=0;
        String date="";
        StockLevelOneInformation stockLevelOneInfo = MyUser.stockLevelOneInfoHashMap.get(internalCode);
        //End of day last price is the new closing price
        closingPrice=stockLevelOneInfo.getLastPrice();
        bestBid=stockLevelOneInfo.getBestBid();
        bestBidQty=stockLevelOneInfo.getBestBidQty();
        bestAsk=stockLevelOneInfo.getBestAsk();
        bestAskQty=stockLevelOneInfo.getBestAskQty();
        totalAssets=stockLevelOneInfo.getAssetsTraded();
        totalVolume=stockLevelOneInfo.getVolumeTraded();
        date=FilterTime.getDate(stockLevelOneInfo.getTimeSGD());
        stmtSend = String.format(stmtFormat, internalCode,closingPrice,bestBid,bestBidQty,bestAsk,bestAskQty,totalAssets,totalVolume,date);
        SqlInsert.start(stmtSend);
    }
}
