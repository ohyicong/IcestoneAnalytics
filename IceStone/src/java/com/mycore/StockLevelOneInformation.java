/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycore;

/**
 *
 * @author Owner
 */
public class StockLevelOneInformation {
    private double lastPrice,closingPrice,bestBid,bestBidQty,bestAsk,bestAskQty,volumeTraded,assetsTraded;
    private int internalCode;
    private String tradingStatus,timeQH,timeSGD,type;
    public StockLevelOneInformation(int internalCode, double bestBid,double bestBidQty, double bestAsk, double bestAskQty,double lastPrice,double closingPrice,double volumeTraded,double assetsTraded,String tradingStatus,String timeQH,String timeSGD,String type){
        this.internalCode=internalCode;
        this.bestAsk=bestAsk;
        this.bestAskQty=bestAskQty;
        this.bestBid=bestBid;
        this.bestBidQty=bestBidQty;
        this.tradingStatus=tradingStatus;
        this.closingPrice=closingPrice;
        this.lastPrice=lastPrice;
        this.timeQH = timeQH;
        this.timeSGD= timeSGD;
        this.type=type;
        this.volumeTraded=volumeTraded;
        this.assetsTraded=assetsTraded;
    }
    public double getVolumeTraded(){
        return this.volumeTraded;
    }
    public double getAssetsTraded(){
        return this.assetsTraded;
    }
    public String getTimeQH(){
        return this.timeQH;
    }
    public String getTimeSGD(){
        return this.timeSGD;
    }
    public String getType(){
        return this.type;
    }
    public double getLastPrice(){
        return this.lastPrice;
    }
    public double getClosingPrice(){
        return this.closingPrice;
    }
    public double getBestBid(){
        return this.bestBid;
    }
    public double getBestBidQty(){
        return this.bestBidQty;
    }
    public double getBestAsk(){
        return this.bestAsk;
    }
    public double getBestAskQty(){
        return this.bestAskQty;
    }
    public int getInternalCode(){
        return this.internalCode;
    }
    public String getTradingStatus(){
        return this.tradingStatus;
    }
    
    
}
