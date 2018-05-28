/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myfeedos;

import com.feedos.api.core.FeedOSException;
import com.feedos.api.requests.MBLLayer;
import com.feedos.api.requests.QuotationTradeEvent;
import com.feedos.api.requests.QuotationTradeEventExt;
import com.mycore.MyUser;
import com.mycore.StockLayerInformation;
import com.mycore.StockLevelOneInformation;
import com.mycore.StockLevelTwoInformation;
import com.mycore.StockTransactionInformation;
import com.mysql.SqlDelete;
import com.mysql.SqlInsert;
import com.mysql.SqlQuery;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author Owner
 */
public class RecordIntoJava {
    public static void levelOneInfoStart (int internalCode){
        ArrayList AL = MyUser.allStockLevelOneInfoHashMap.get(internalCode);
        if(AL == null){
            System.out.println("Creating new LevelOne ram object");
            AL = new ArrayList();
        }         
        AL.add(MyUser.stockLevelOneInfoHashMap.get(internalCode));
        MyUser.allStockLevelOneInfoHashMap.put(internalCode, AL); 
        System.out.println("Internal code:"+internalCode+" has "+AL.size()+" for ONE");
        
        
    }
    public static void transactionInfoStart (int internalCode,QuotationTradeEventExt tradeEvent,String transtype,String timeQH,String timeSGD){
        ArrayList ALTrans = MyUser.allStockTransactionInfoHashMap.get(internalCode);
        if(ALTrans==null){
            System.out.println("Creating new Transaction ram object");
            ALTrans=new ArrayList();
        }
        StockTransactionInformation stockTransactionInfo = new StockTransactionInformation(internalCode, tradeEvent.price , tradeEvent.last_trade_qty, transtype, timeQH, timeSGD);
        ALTrans.add(stockTransactionInfo);
        SocketBroadcast.updateStockTransaction(stockTransactionInfo);
        MyUser.allStockTransactionInfoHashMap.put(internalCode, ALTrans); 
        System.out.println("Trade transaction length:"+ALTrans.size());
    }
    
    public static void levelTwoInfoStart (int internalCode,ArrayList stockLayerInfo,String timeQH,String timeSGD){
        ArrayList alMBL = MyUser.allStockLevelTwoInfoHashMap.get(internalCode);
        if(alMBL == null){
            System.out.println("Creating new MBL ram object");
            alMBL = new ArrayList();
        }         
        alMBL.add(new StockLevelTwoInformation(internalCode,timeQH,timeSGD,stockLayerInfo));
        MyUser.allStockLevelTwoInfoHashMap.put(internalCode, alMBL); 
        System.out.println("Internal code:"+internalCode+" has "+alMBL.size()+" for MBL");
        
    }
    
    public static void storeIntoSql(int internalCode) throws ClassNotFoundException, FeedOSException, SQLException{
        //This function is the start of storing into MYSQL
        //Get current date for insertion
        Calendar now = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = simpleDateFormat.format(now.getTime());
        System.out.println("Storing "+ MyUser.stockGeneralInfoHashMap.get(internalCode).getStockName()+" into mySQL");
        //Retrieve all information collected today
        ArrayList ALlevelOne = MyUser.allStockLevelOneInfoHashMap.get(internalCode);
        ArrayList ALlevelTwo = MyUser.allStockLevelTwoInfoHashMap.get(internalCode);
        ArrayList ALtransaction = MyUser.allStockTransactionInfoHashMap.get(internalCode);
        System.out.println("Level One Storing Started");
        //Sending levelone data as a block
        RecordIntoSQL.levelOneInfo(internalCode);
        SqlDelete.start("delete from intradaystockslevelone where internalcodes = "+internalCode);
        String intradayLevelOneStmt = "insert into intradaystockslevelone select * from stockslevelone where timesgd >='"+dateStr+"'"+"and internalcodes="+internalCode ;
        SqlInsert.start(intradayLevelOneStmt);
        System.out.println(intradayLevelOneStmt);
        System.out.println("Level One Storing Completed");
        System.out.println("Level Two Storing Started");       
        //Sending leveltwo data as a block
        SqlDelete.start("delete from intradaystocksleveltwo where internalcodes = "+internalCode);
        RecordIntoSQL.levelTwoInfo(internalCode);
        System.out.println("Level Two Storing Completed");
        System.out.println("Transaction Storing Started");
        SqlDelete.start("delete from intradaystockstransaction where internalcodes = "+internalCode);
        RecordIntoSQL.transactionInfo(internalCode);
        System.out.println("Transaction Storing Completed");
        System.out.println("Daily Information Storing Started");
        RecordIntoSQL.dailyInformation(internalCode);
        System.out.println("Daily Information Storing Completed");
        //Assign current day information to previous day
         System.out.println("Clearing previous day data");
        if(MyUser.prevDayAllStockLevelOneInfoHashMap.get(internalCode)!=null){
            MyUser.prevDayAllStockLevelOneInfoHashMap.get(internalCode).clear();

        }
        if(MyUser.prevDayAllStockLevelTwoInfoHashMap.get(internalCode)!=null){
            MyUser.prevDayAllStockLevelTwoInfoHashMap.get(internalCode).clear();
        }
        if(MyUser.prevDayAllStockTransactionInfoHashMap.get(internalCode)!=null){
            MyUser.prevDayAllStockTransactionInfoHashMap.get(internalCode).clear();
        }
        System.out.println("Adding previous day data");
        MyUser.prevDayAllStockLevelOneInfoHashMap.put(internalCode, ALlevelOne);
        MyUser.prevDayAllStockLevelTwoInfoHashMap.put(internalCode, ALlevelTwo);
        MyUser.prevDayAllStockTransactionInfoHashMap.put(internalCode, ALtransaction);
        System.out.println("Current day data wiped");
        MyUser.allStockLevelOneInfoHashMap.get(internalCode).clear();
        MyUser.allStockLevelTwoInfoHashMap.get(internalCode).clear();
        MyUser.allStockTransactionInfoHashMap.get(internalCode).clear();
        System.out.println("Reassign ArrayList");
        MyUser.allStockLevelOneInfoHashMap.put(internalCode,new ArrayList());
        MyUser.allStockLevelTwoInfoHashMap.put(internalCode,new ArrayList());
        MyUser.allStockTransactionInfoHashMap.put(internalCode,new ArrayList());
        System.out.println("InternalCode:"+internalCode+" JAVA data cleared");
    }
}
