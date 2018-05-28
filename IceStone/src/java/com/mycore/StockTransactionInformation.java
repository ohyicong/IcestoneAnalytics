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
public class StockTransactionInformation {
    int internalCode;
    double tradePrice,quantity;
    String transType, timeQH, timeSGD;
    public StockTransactionInformation  (int internalCode, double tradePrice, double quantity, String transType , String timeQH ,String timeSGD){
        this.internalCode=internalCode;
        this.tradePrice=tradePrice;
        this.quantity=quantity;
        this.transType=transType;
        this.timeQH = timeQH;
        this.timeSGD=timeSGD;
    }
    public int getInternalCode (){
        return this.internalCode;
    }
    public double getTradePrice(){
        return this.tradePrice;
    }
    public double getQuantity(){
        return this.quantity;
    }
    public String getTransType(){
        return this.transType;
    }
    public String getTimeQH(){
        return this.timeQH;
    }
    public String getTimeSGD(){
        return this.timeSGD;
    }
}
