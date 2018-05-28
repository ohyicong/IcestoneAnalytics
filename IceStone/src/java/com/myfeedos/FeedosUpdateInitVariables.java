/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myfeedos;

import com.feedos.api.core.Any;
import com.feedos.api.requests.Constants;
import com.feedos.api.requests.InstrumentQuotationData;
import com.feedos.api.requests.OrderBook;
import com.feedos.api.requests.OrderBookSide;
import com.mycore.MyUser;
import com.mycore.StockGeneralInformation;
import com.mycore.StockLevelOneInformation;
import com.mysql.SqlInsert;
import java.sql.SQLException;

/**
 *
 * @author Owner
 */
public class FeedosUpdateInitVariables {
    
    
    public static void updateStockGeneralInfo(StockGeneralInformation stockGenInfo) throws ClassNotFoundException, SQLException{
        String stmtFormatter= "insert into subscribedstocks (internalcodes,stocksname,exchangesymbol) values (%d,'%s','%s')";
        String stmt = String.format(stmtFormatter, stockGenInfo.getInternalCode(),stockGenInfo.getStockName(),stockGenInfo.getExchangeSymbol());
        SqlInsert.start(stmt);
        MyUser.stockGeneralInfoHashMap.put(stockGenInfo.getInternalCode(), stockGenInfo);
    }
    public static void updateStockLevelOneInformation (InstrumentQuotationData [] instruments,String timeQH,String timeSGD, String type){
        int count=0;
        int internalCode=0;
        double bestAsk=0,bestBid=0,bestAskQty=0,bestBidQty=0,lastPrice=0,lastTradeQty=0,closingPrice=0,volumeTraded=0,assetsTraded=0; 
        String tradingStatus="NIL";
        for (InstrumentQuotationData v : instruments) {
            //Retrieve internal code 
            internalCode= v.getInstrumentCode().get_internal_code();
            StockLevelOneInformation tempInstrument =MyUser.stockLevelOneInfoHashMap.get(internalCode);
            bestAsk=tempInstrument.getBestAsk();
            bestAskQty=tempInstrument.getBestAskQty();
            bestBid=tempInstrument.getBestBid();
            bestBidQty=tempInstrument.getBestBidQty();
            lastPrice=tempInstrument.getLastPrice();
            tradingStatus=tempInstrument.getTradingStatus();
            closingPrice=tempInstrument.getClosingPrice();
            volumeTraded=tempInstrument.getVolumeTraded();
            assetsTraded=tempInstrument.getAssetsTraded();
                    
            int[] tagNums;
            //Any[] tagValues;
            synchronized (v) {
		tagNums = v.getAllTagNumbers_nolock();
		//tagValues = v.getAllTagValues_nolock();
            }

            //Retrieve OrderBook 
            OrderBook orderBook = v.getOrderBook();
            OrderBookSide bidSide = orderBook.getBidSide();
            OrderBookSide askSide = orderBook.getAskSide();
            
            //Retrieve best ask/bid value and qty for each instruments
            for (int index=0; index<bidSide.getDepth(); ++index) {
                bestBid=bidSide.getPrice(index);
                bestBidQty=bidSide.getQty(index);
                if(bestBid>=999999){
                    bestBid=tempInstrument.getBestBid();
                }
            }
            for (int index=0; index<askSide.getDepth(); ++index) {
                bestAsk=askSide.getPrice(index);
                bestAskQty=askSide.getQty(index);
                if(bestAsk>=999999){
                    bestAsk=tempInstrument.getBestAsk();
                }
            }
            
            for (int index=0; index<tagNums.length; ++index) {
                switch (tagNums[index]) {
                    case Constants.TAG_LastPrice:  
                        if(!(v.getTagByNumber(Constants.TAG_LastPrice).get_float64() <=0.000001)){
                            lastPrice= v.getTagByNumber(Constants.TAG_LastPrice).get_float64();
                        }
                        break;
                    case Constants.TAG_PreviousClosingPrice:
                        closingPrice=v.getTagByNumber(Constants.TAG_PreviousClosingPrice).get_float64();
                        break;
                    case Constants.TAG_DailyTotalVolumeTraded:
                        volumeTraded=v.getTagByNumber(Constants.TAG_DailyTotalVolumeTraded).get_float64();
                        break;
                    case Constants.TAG_DailyTotalAssetTraded:
                        assetsTraded=v.getTagByNumber(Constants.TAG_DailyTotalAssetTraded).get_float64();
                        break;
                    case Constants.TAG_MARKET_TradingStatus:
                        final Constants.DictionaryEntry entry = Constants.getDictionaryEntry(new Constants.DictionaryEntryKey(Constants.TAG_MARKET_TradingStatus, v.getTagByNumber(Constants.TAG_MARKET_TradingStatus).get_uint32()));
                        tradingStatus=entry.getShortName();
                        break;
                    default:
                        break;
                }
            }
            
            
            //Initialise stocks informations in java class
            MyUser.stockLevelOneInfoHashMap.put(internalCode, new StockLevelOneInformation(internalCode,bestBid,bestBidQty,bestAsk,bestAskQty,lastPrice,closingPrice,volumeTraded,assetsTraded,tradingStatus,timeQH,timeSGD,type));
            System.out.println("Stockinfohashmap:" +MyUser.stockLevelOneInfoHashMap.size());
            System.out.println("Updates on InternalCode:"+internalCode+" BestBid:"+bestBid+" BestBidQty:"+ bestBidQty+ " BestAsk:"+bestAsk+" BestAskQty:"+bestAskQty+" Last Price:"+lastPrice+" closingPrice:"+closingPrice+" tradingStatus:"+tradingStatus+" volume:"+volumeTraded+" assets:"+assetsTraded);
        }
    }
    public static void removeStockGeneralInfo(StockGeneralInformation stockGenInfo){
        System.out.println("General Info Removing:"+stockGenInfo.getStockName());
        MyUser.stockGeneralInfoHashMap.remove(stockGenInfo.getInternalCode());
        //reduce number of substocks once only
      
    }
    public static void removeStockLevelOneInfo(StockGeneralInformation stockGenInfo){
        System.out.println("Level One Info Removing:"+stockGenInfo.getStockName());
        MyUser.stockLevelOneInfoHashMap.remove(stockGenInfo.getInternalCode());
        MyUser.allStockLevelOneInfoHashMap.remove(stockGenInfo.getInternalCode());
        MyUser.prevDayAllStockLevelOneInfoHashMap.remove(stockGenInfo.getInternalCode());
    }
    public static void removeStockLevelTwoInfo(StockGeneralInformation stockGenInfo){
        System.out.println("Level Two Info Removing:"+stockGenInfo.getStockName());
        MyUser.stockLevelTwoInfoHashMap.remove(stockGenInfo.getInternalCode());
        MyUser.allStockLevelTwoInfoHashMap.remove(stockGenInfo.getInternalCode());
        MyUser.prevDayAllStockLevelTwoInfoHashMap.remove(stockGenInfo.getInternalCode());
    }
    public static void removeStockTransactionInfo(StockGeneralInformation stockGenInfo){
        System.out.println("Transaction Info Removing:"+stockGenInfo.getStockName());
        MyUser.allStockTransactionInfoHashMap.remove(stockGenInfo.getInternalCode()); 
        MyUser.prevDayAllStockTransactionInfoHashMap.remove(stockGenInfo.getInternalCode());
    }
}
