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
public class StockLayerInformation {
    private int internalCodes,level,askQueue,bidQueue;
    private double bid,bidQty,ask,askQty;
    
    public StockLayerInformation(int internalCodes,int level, double bid,double bidQty,double ask,double askQty,int bidQueue,int askQueue ){
        this.internalCodes=internalCodes;
        this.level = level;
        this.bid=bid;
        this.bidQty=bidQty;
        this.ask=ask;
        this.askQty=askQty;
        this.askQueue=askQueue;
        this.bidQueue = bidQueue;
        
    }
    public int getInternalCodes(){
        return this.internalCodes;
    }
    public int getLevel(){
        return this.level;
    }
    public double getBid(){
        return this.bid;
    }
    public double getBidQty(){
        return this.bidQty;
    }
    
    public double getAsk(){
        return this.ask;
    }
    public double getAskQty(){
        return this.askQty;
    }
    public int getBidQueue(){
        return this.bidQueue;
    }
    public int getAskQueue(){
        return this.askQueue;
    }
}
