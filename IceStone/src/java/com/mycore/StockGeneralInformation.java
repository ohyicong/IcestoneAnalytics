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
public class StockGeneralInformation {
    private String stockName, exchangeSymbol;
    private int internalCode;
    public StockGeneralInformation(int internalCode,String stockName, String exchangeSymbol){
        this.internalCode=internalCode;
        this.stockName=stockName;
        this.exchangeSymbol=exchangeSymbol;
    }

    public int getInternalCode() {
        return this.internalCode;
    }
    public String getStockName(){
        return this.stockName;
    }
    public String getExchangeSymbol(){
        return this.exchangeSymbol;
    }
    public void setInternalCode(int internalCode){
        this.internalCode=internalCode;
    }
    public void setStockName(String stockName){
        this.stockName=stockName;
    }
    public void setExchangeSymbol(String exchangeSymbol){
        this.exchangeSymbol=exchangeSymbol;
    }
}
