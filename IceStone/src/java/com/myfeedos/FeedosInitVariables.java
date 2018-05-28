/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myfeedos;

import com.feedos.api.core.Any;
import com.feedos.api.core.FeedOSException;
import com.feedos.api.core.PDU;
import com.feedos.api.requests.Constants;
import com.feedos.api.requests.InstrumentQuotationData;
import com.feedos.api.requests.MBLLayer;
import com.feedos.api.requests.OrderBook;
import com.feedos.api.requests.OrderBookSide;
import com.feedos.api.requests.QuotationContentMask;
import com.feedos.api.requests.RequestSender;
import com.feedos.api.requests.SyncRequestSender;
import com.mycore.MyUser;
import com.mycore.StockGeneralInformation;
import com.mycore.StockLayerInformation;
import com.mycore.StockLevelOneInformation;
import com.mycore.StockLevelTwoInformation;
import com.mycore.StockTransactionInformation;
import com.myfilters.FilterTime;
import com.mysql.SqlQuery;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 *
 * @author Owner
 */
//Start up of application
public class FeedosInitVariables {
    public static void start() throws ClassNotFoundException, SQLException, FeedOSException{
        MyUser.timesgd="Loading...";
        MyUser.isStoring=false;
        MyUser.isNewsFeedSyncing=false;
        MyUser.isSqlRecordingOn=true;
        MyUser.isNewDay=true;
        MyUser.syncRequester= new SyncRequestSender (MyUser.session, 0);
        
        
        if (MyUser.stockGeneralInfoHashMap!=null)
            MyUser.stockGeneralInfoHashMap.clear();
        if (MyUser.stockLevelOneInfoHashMap!=null)
            MyUser.stockLevelOneInfoHashMap.clear();
        if (MyUser.stockLevelTwoInfoHashMap!=null)
            MyUser.stockLevelTwoInfoHashMap.clear();
        if (MyUser.stockPastAverageVolume!=null)
            MyUser.stockPastAverageVolume.clear();
        if (MyUser.allStockTransactionInfoHashMap!=null)
            MyUser.allStockTransactionInfoHashMap.clear();
        if (MyUser.allStockLevelOneInfoHashMap!=null)
            MyUser.allStockLevelOneInfoHashMap.clear();
        if (MyUser.allStockLevelTwoInfoHashMap!=null)
            MyUser.allStockLevelTwoInfoHashMap.clear();
        if (MyUser.prevDayAllStockTransactionInfoHashMap!=null)
            MyUser.prevDayAllStockTransactionInfoHashMap.clear();
        if (MyUser.prevDayAllStockLevelOneInfoHashMap!=null)
            MyUser.prevDayAllStockLevelOneInfoHashMap.clear();
        if (MyUser.stockLevelTwoInfoHashMap!=null)
            MyUser.prevDayAllStockLevelTwoInfoHashMap.clear();
        
        MyUser.stockPastAverageVolume = new HashMap <>();
        MyUser.stockGeneralInfoHashMap = new HashMap <>();
        MyUser.stockLevelOneInfoHashMap = new HashMap <>();
        MyUser.stockLevelTwoInfoHashMap = new HashMap <>();
        MyUser.allStockTransactionInfoHashMap = new HashMap<>();
        MyUser.allStockLevelOneInfoHashMap = new HashMap<>();
        MyUser.allStockLevelTwoInfoHashMap = new HashMap<>();
        MyUser.prevDayAllStockTransactionInfoHashMap=new HashMap <>();
        MyUser.prevDayAllStockLevelOneInfoHashMap=new HashMap <>();
        MyUser.prevDayAllStockLevelTwoInfoHashMap=new HashMap <>();
        MyUser.currentPdfPage=0;
        FeedosInitVariables.InitStocksGeneralInfo();
        //FeedosInitVariables.initPrevInfo(); // takes too long liao
        recalibration();
        FeedosTickTable.start();//Must be initialised before starting subscription
        FeedosASyncQuotSubInstrumentsL1.start();
        FeedosASyncQuotSubInstrumentsMBL.start();
    }
    //Restart init variables for new day
    public static void restart() throws ClassNotFoundException, SQLException, FeedOSException{
        System.out.println("Restart init variables");
        MyUser.isNewDay=true;
        if (MyUser.stockPastAverageVolume!=null)
            MyUser.stockPastAverageVolume.clear();
        MyUser.stockPastAverageVolume = new HashMap <>();
        recalibration();
    }
    
    public static void recalibration() throws FeedOSException, ClassNotFoundException, SQLException{
         //FeedosTickTable.start();//Must be initialised before starting subscription
          for(StockGeneralInformation stockGeneralInfo : MyUser.stockGeneralInfoHashMap.values()){
              initHistoricalAverageVolume(stockGeneralInfo.getInternalCode());
          }
         
    }
    public static void InitStocksGeneralInfo() throws ClassNotFoundException, SQLException{
        String stmt="select * from subscribedstocks";
        SqlQuery sqlQuery = new SqlQuery();
        ResultSet resultSet = sqlQuery.start(stmt);
        while(resultSet.next()){
            int internalCode =resultSet.getInt("internalcodes");
            String stockName=resultSet.getString("stocksname");
            String exchangeSymbol =resultSet.getString("exchangesymbol");
            MyUser.stockGeneralInfoHashMap.put(internalCode,new StockGeneralInformation(internalCode,stockName,exchangeSymbol));
        }
        sqlQuery.close();
        
    }
    public static void InitStocksLevelOneInfo(InstrumentQuotationData[] instruments) throws ClassNotFoundException, SQLException{
        System.out.println("Init number of instruments: "+instruments.length);
        int internalCode=0;
        double bestAsk=0,bestBid=0,bestAskQty=0,bestBidQty=0,lastPrice=0,lastTradeQty=0,closingPrice=0,volumeTraded=0,assetsTraded=0; 
        String tradingStatus="",timeQH="",timeSGD="";
        long rawTime=0;
        for (InstrumentQuotationData v : instruments) {
            int[] tagNums;
            Any[] tagValues;
            synchronized (v) {
		tagNums = v.getAllTagNumbers_nolock();
		tagValues = v.getAllTagValues_nolock();
            }
            //Retrieve internal code 
            internalCode= v.getInstrumentCode().get_internal_code();
            //Retrieve OrderBook 
            OrderBook orderBook = v.getOrderBook();
            OrderBookSide bidSide = orderBook.getBidSide();
            OrderBookSide askSide = orderBook.getAskSide();
            
            //Retrieve best ask/bid value and qty for each instruments
            for (int index=0; index<bidSide.getDepth(); ++index) {
                bestBid=bidSide.getPrice(index);
                bestBidQty=bidSide.getQty(index);

            }
            for (int index=0; index<askSide.getDepth(); ++index) {
                bestAsk=askSide.getPrice(index);
                bestAskQty=askSide.getQty(index);
            }
            
            for (int index=0; index<tagNums.length; ++index) {
                switch (tagNums[index]) {
                    case Constants.TAG_LastPrice:
                        lastPrice= v.getTagByNumber(Constants.TAG_LastPrice).get_float64();
                        break;
                    case Constants.TAG_PreviousClosingPrice:
                        closingPrice=v.getTagByNumber(Constants.TAG_PreviousClosingPrice).get_float64();
                        break;
                    case Constants.TAG_MARKET_TradingStatus:
                        final Constants.DictionaryEntry entry = Constants.getDictionaryEntry(new Constants.DictionaryEntryKey(Constants.TAG_MARKET_TradingStatus, v.getTagByNumber(Constants.TAG_MARKET_TradingStatus).get_uint32()));
                        tradingStatus=entry.getShortName();
                        break;
                    case Constants.TAG_DailyTotalVolumeTraded:
                        volumeTraded=v.getTagByNumber(Constants.TAG_DailyTotalVolumeTraded).get_float64();
                        break;
                    case Constants.TAG_DailyTotalAssetTraded:
                        assetsTraded=v.getTagByNumber(Constants.TAG_DailyTotalAssetTraded).get_float64();
                        break;
                    case Constants.TAG_InternalPriceActivityTimestamp:
                        rawTime=v.getTagByNumber(Constants.TAG_InternalPriceActivityTimestamp).get_date_nocheck();
                        break;
                    default:
                        break;
                }
            }
            timeQH = PDU.date2ISOstring(rawTime);
            timeSGD= FilterTime.sgd(timeQH);
            //Initialise stocks informations in java class
            
            MyUser.stockLevelOneInfoHashMap.put(internalCode, new StockLevelOneInformation(internalCode,bestBid,bestBidQty,bestAsk,bestAskQty,lastPrice,closingPrice,volumeTraded,assetsTraded,tradingStatus,timeQH,timeSGD,"NIL"));
            //FeedosInitVariables.initHistoricalAverageVolume(internalCode);
            System.out.println("Updated size stockinfohashmap:" +MyUser.stockLevelOneInfoHashMap.size());
            System.out.println("InternalCode:"+internalCode+" BestBid:"+bestBid+" BestBidQty:"+ bestBidQty+ " BestAsk:"+bestAsk+" BestAskQty:"+bestAskQty+" Last Price:"+lastPrice+" closingPrice:"+closingPrice+" tradingStatus:"+tradingStatus);
        
        }
    }
    public static void initPrevInfo () throws ClassNotFoundException, SQLException{
        String stmtLevelOne ="select * from intradaystockslevelone where internalcodes = %d";
        String stmtLevelTwo ="select * from intradaystocksleveltwo where internalcodes = %d";
        String stmtTransaction="select * from intradaystockstransaction where internalcodes = %d";
        String stmtSend="";
        System.out.println("Reinitialising all previous day data");
        ResultSet rs;
        String timeQH="",timeSGD="";
        for(StockGeneralInformation stockGeneralInfo : MyUser.stockGeneralInfoHashMap.values()){
            //level One
            stmtSend = String.format(stmtLevelOne,stockGeneralInfo.getInternalCode());
            SqlQuery sqlQuery = new SqlQuery();
            rs = sqlQuery.start(stmtSend);
            ArrayList alLevelOne = new ArrayList();
            while(rs.next()){
                double lastPrice = rs.getDouble("lastprice");
                double bestBid=rs.getDouble("bestbid");
                double bestBidQty=rs.getDouble("bestbidqty");
                double bestAsk=rs.getDouble("bestask");
                double bestAskQty=rs.getDouble("bestaskqty");
                String type = rs.getString("type");
                String marketStatus=rs.getString("marketstatus");
                timeQH = rs.getTimestamp("timeqh").toString();
                timeSGD = rs.getTimestamp("timesgd").toString();
                alLevelOne.add(new StockLevelOneInformation(stockGeneralInfo.getInternalCode(),bestBid,bestBidQty,bestAsk,bestAskQty,lastPrice,0,0,0,marketStatus,timeQH,timeSGD,type));
                
            }
            if(!alLevelOne.isEmpty()){
                MyUser.prevDayAllStockLevelOneInfoHashMap.put(stockGeneralInfo.getInternalCode(), alLevelOne);
            }
            rs.close();
            //level Two init
            stmtSend = String.format(stmtLevelTwo,stockGeneralInfo.getInternalCode());
            rs = sqlQuery.start(stmtSend);
            boolean isFirst=true;
            ArrayList alLevelTwo = new ArrayList();
            ArrayList MBLLayer= new ArrayList();
            int count=0;
            while(rs.next()){
                int level = rs.getInt("level");
                if(level==0&&!isFirst){
                   alLevelTwo.add(new StockLevelTwoInformation(stockGeneralInfo.getInternalCode(),timeQH,timeSGD,MBLLayer));
                   MBLLayer = new ArrayList();
                }else{
                    isFirst=false;
                }
                double bid = rs.getDouble("bid");
                double bidQty = rs.getDouble("bidQty");
                int bidQueue = rs.getInt("bidQueue");
                double ask = rs.getDouble("ask");
                double askQty = rs.getDouble("askqty");
                int askQueue = rs.getInt("askQueue");
                timeQH = rs.getString("timeqh");
                timeSGD = rs.getString("timeSGD");
                MBLLayer.add( new StockLayerInformation(stockGeneralInfo.getInternalCode(),level,bid,bidQty,ask,askQty,bidQueue,askQueue)); 
            }
            if(!alLevelTwo.isEmpty()){
                MyUser.prevDayAllStockLevelTwoInfoHashMap.put(stockGeneralInfo.getInternalCode(), alLevelTwo);
            }
            sqlQuery.close();
            //transaction
            stmtSend = String.format(stmtTransaction,stockGeneralInfo.getInternalCode());
            rs = sqlQuery.start(stmtSend);
            ArrayList alTransaction= new ArrayList();
            while(rs.next()){
                double tradePrice = rs.getDouble("tradeprice");
                double quantity = rs.getDouble("quantity");
                String transtype = rs.getString("transtype");
                timeQH = rs.getTimestamp("timeqh").toString();
                timeSGD = rs.getTimestamp("timesgd").toString();
                alTransaction.add(new StockTransactionInformation(stockGeneralInfo.getInternalCode(),tradePrice,quantity,transtype,timeQH,timeSGD));
            }
            sqlQuery.close();
           //past average volume
            initHistoricalAverageVolume(stockGeneralInfo.getInternalCode());
            
        }
    }
    public static void initHistoricalAverageVolume(int internalCode) throws ClassNotFoundException, SQLException{
        System.out.println("Geting average volume "+internalCode);
        Calendar now =  Calendar.getInstance();
        //GO back 3 days
        Calendar past = FilterTime.goBackInTime(now, 3);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String stmtFormat = "select * from dailyinformation where internalcodes = %d and date >= '"+simpleDateFormat.format(past.getTime())+"'";
        String stmtSend ="";
            stmtSend = String.format(stmtFormat, internalCode);
            SqlQuery sqlQuery = new SqlQuery();
            ResultSet rs = sqlQuery.start(stmtSend);
            double totalVolume=0;
            int count=0;
            while(rs.next()){
                totalVolume+=rs.getDouble("totalvolume");
                count++;
            }
            if(count!=0){
                MyUser.stockPastAverageVolume.put(internalCode, totalVolume/count);
                System.out.println("Average volume is: "+totalVolume/count);
            }else{
                MyUser.stockPastAverageVolume.put(internalCode, 0.0);
                System.out.println("Average volume is: 0");
            }
            sqlQuery.close();
        }
}
