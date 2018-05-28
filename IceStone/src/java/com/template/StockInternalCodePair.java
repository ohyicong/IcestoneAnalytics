/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.template;

/**
 *
 * @author Owner
 */
public class StockInternalCodePair {
    private int internalCodeOne,internalCodeTwo;
    private int  percentage;
    public StockInternalCodePair(int internalCodeOne, int internalCodeTwo,int percentage){
        this.internalCodeOne=internalCodeOne;
        this.internalCodeTwo=internalCodeTwo;
        this.percentage = percentage;
    }
    public int getInternalCodeOne(){
        return this.internalCodeOne;
    }
    public int getInternalCodeTwo(){
        return this.internalCodeTwo;
    }
    public int getPercentage(){
        return this.percentage;
    }
}
