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
public class StockInternalCodePercent {
    private int percent , internalCode;
    public StockInternalCodePercent(int internalCode,int percent){
        this.percent=percent;
        this.internalCode=internalCode;
    }
    public int getInternalCode(){
        return this.internalCode;
    }
    public int getPercent(){
        return this.percent;
    }
}
