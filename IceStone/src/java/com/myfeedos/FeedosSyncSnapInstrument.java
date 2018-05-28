/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myfeedos;

import com.feedos.api.core.Any;
import com.feedos.api.core.FeedOSException;
import com.feedos.api.core.PolymorphicInstrumentCode;
import com.feedos.api.requests.Constants;
import com.feedos.api.requests.InstrumentQuotationData;
import com.feedos.api.requests.OrderBook;
import com.feedos.api.requests.OrderBookSide;
import com.google.gson.JsonObject;
import com.mycore.MyUser;
import com.mycore.StockLevelOneInformation;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Owner
 */
public class FeedosSyncSnapInstrument {
    public static InstrumentQuotationData[] start(int internalCode) throws FeedOSException{
        PolymorphicInstrumentCode[] instr_list = new PolymorphicInstrumentCode[1];
	instr_list[0] = new PolymorphicInstrumentCode(internalCode);
        InstrumentQuotationData[] result;
        result = MyUser.syncRequester.syncQuotSnapshotInstrumentsL1(
            instr_list,
            null);
        return result;
    }

    public static JsonObject processSyncSnapInstrument (int internalCode) throws FeedOSException{
        InstrumentQuotationData [] instruments = FeedosSyncSnapInstrument.start(internalCode);
        int count=0;
        double bestAsk=0,bestBid=0,bestAskQty=0,bestBidQty=0,lastPrice=0,lastTradeQty=0,closingPrice=0,volumeTraded=0,assetsTraded=0,prevVolumeTraded=0,prevAssetsTraded=0; 
        JsonObject jsonObject = new JsonObject();
        for (InstrumentQuotationData v : instruments) {
            //Retrieve internal code 
            internalCode= v.getInstrumentCode().get_internal_code();
            StockLevelOneInformation tempInstrument =MyUser.stockLevelOneInfoHashMap.get(internalCode);
            bestAsk=tempInstrument.getBestAsk();
            bestAskQty=tempInstrument.getBestAskQty();
            bestBid=tempInstrument.getBestBid();
            bestBidQty=tempInstrument.getBestBidQty();
            lastPrice=tempInstrument.getLastPrice();
            volumeTraded=tempInstrument.getVolumeTraded();
            assetsTraded =tempInstrument.getAssetsTraded();
            int[] tagNums;
            Any[] tagValues;
            synchronized (v) {
		tagNums = v.getAllTagNumbers_nolock();
		tagValues = v.getAllTagValues_nolock();
            }

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
                    case Constants.TAG_PreviousDailyTotalVolumeTraded:
                        prevVolumeTraded = v.getTagByNumber(Constants.TAG_PreviousDailyTotalVolumeTraded).get_float64();
                        break;
                    case Constants.TAG_PreviousDailyTotalAssetTraded:
                        prevAssetsTraded = v.getTagByNumber(Constants.TAG_PreviousDailyTotalAssetTraded).get_float64();
                        break;
                    case Constants.TAG_DailyTotalVolumeTraded:
                        volumeTraded = v.getTagByNumber(Constants.TAG_DailyTotalVolumeTraded).get_float64();
                        break;
                    case Constants.TAG_DailyTotalAssetTraded:
                        assetsTraded = v.getTagByNumber(Constants.TAG_DailyTotalAssetTraded).get_float64();
                        break;
                    default:
                        break;
                }
            }
        }
        jsonObject.addProperty("stocksname",MyUser.stockGeneralInfoHashMap.get(internalCode).getStockName());
        jsonObject.addProperty("bid",bestBid);
        jsonObject.addProperty("bidvolume",bestBidQty);
        jsonObject.addProperty("ask",bestAsk);
        jsonObject.addProperty("askvolume",bestAskQty);
        jsonObject.addProperty("lastprice",lastPrice);
        jsonObject.addProperty("closingprice",closingPrice);
        jsonObject.addProperty("volumetraded",volumeTraded);
        jsonObject.addProperty("assetstraded",assetsTraded);
        jsonObject.addProperty("prevvolumetraded",prevVolumeTraded);
        jsonObject.addProperty("prevassetstraded",prevAssetsTraded);
        return jsonObject;
    }
}
