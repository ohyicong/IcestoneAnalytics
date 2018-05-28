/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycore;

import com.feedos.api.requests.MBLLayer;
import java.util.ArrayList;

/**
 *
 * @author Owner
 */
public class StockLevelTwoInformation {
    private int internalCode;
    private String timeQH,timeSGD;
    private ArrayList MBLLayer;
    
    public StockLevelTwoInformation (int internalCode,String timeQH,String timeSGD, ArrayList MBLLayer){
        this.internalCode = internalCode;
        this.timeQH=timeQH;
        this.timeSGD=timeSGD;
        this.MBLLayer=MBLLayer;
    }
    public int getInternalCode(){
        return this.internalCode;
    }
    public String getTimeQH(){
        return this.timeQH;
    }
    public String getTimeSGD(){
        return this.timeSGD;
    }
    public ArrayList getMBLLayer(){
        return this.MBLLayer;
    }
    
    
    
}
