/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myfeedos;

import com.feedos.api.core.FeedOSException;
import com.feedos.api.requests.VariableIncrementPriceBand;
import com.feedos.api.requests.VariableIncrementPriceBandTable;
import com.mycore.MyConstants;
import com.mycore.MyUser;
import java.text.DecimalFormat;

/**
 *
 * @author Owner
 */
public class FeedosTickTable {
    public static void start() throws FeedOSException{
        int count=0;
        VariableIncrementPriceBandTable[] tickResults = MyUser.syncRequester.syncRefGetVariableIncrementBandTable();
        MyUser.allTickTable = new VariableIncrementPriceBandTable[tickResults.length];
        for(VariableIncrementPriceBandTable tickResult : tickResults ){
            MyUser.allTickTable[count]=tickResult;//saving all tickTables into an array
            if(tickResult.getTableId()==MyConstants.defaultTickTableId){
                MyUser.currentTickTable=tickResult;//saving only default tick table
                assignTableVariables(tickResult);
            }
            count++;
        }
    }
    public static void assignTableVariables(VariableIncrementPriceBandTable tickTable ){
        int count=0;
        VariableIncrementPriceBand[] priceBands = tickTable.getPriceBands();
        //Update global tick variables 
        MyUser.currentTickLowerBoundary = new double[priceBands.length];
        MyUser.currentTickPriceIncrement = new double[priceBands.length];
        for(VariableIncrementPriceBand priceBand:priceBands){
            MyUser.currentTickLowerBoundary[count]=priceBand.getLowerBoundary();
            MyUser.currentTickPriceIncrement[count]=priceBand.getPriceIncrement();
            count++;
        }
        System.out.println("Currently using this tick table");
        for(int i=0;i<MyUser.currentTickLowerBoundary.length;i++ ){
            System.out.println("Tick:"+MyUser.currentTickLowerBoundary[i]+" "+MyUser.currentTickPriceIncrement[i]);
        }
        
    }
    public static int getTickToUse (double currentPrice,double oldPrice){
        double currentTickIncre=0, oldTickIncre=0 , currentLowerBoundary=0, oldLowerBoundary=0;
        boolean isCurrentTickRight = false,isOldTickRight=false;
        int count1 = 0,count2=0;
        while(!isCurrentTickRight&&count1<MyUser.currentTickLowerBoundary.length){
            if(currentPrice>MyUser.currentTickLowerBoundary[count1]){
                isCurrentTickRight=true;
                currentTickIncre =MyUser.currentTickPriceIncrement[count1]; 
                currentLowerBoundary =MyUser.currentTickLowerBoundary[count1];
            }
            count1++;
        }
        while(!isOldTickRight&&count2<MyUser.currentTickLowerBoundary.length){
            if(oldPrice>MyUser.currentTickLowerBoundary[count2]){
                isOldTickRight=true;
                oldTickIncre =MyUser.currentTickPriceIncrement[count2];
                oldLowerBoundary=MyUser.currentTickLowerBoundary[count2];
            }
            count2++;
        }
        if(currentTickIncre==oldTickIncre){
            double change=(currentPrice-oldPrice)/currentTickIncre;
            return roundTickIntoInt(change);
        } else{
            int oldTick = (int) Math.abs(roundTickIntoInt(oldPrice-oldLowerBoundary)/oldTickIncre);
            int currentTick=(int) Math.abs(roundTickIntoInt(currentPrice-currentLowerBoundary)/currentTickIncre);
            if((currentPrice-oldPrice)>=0){                
               return Math.abs(oldTick+currentTick);
            }else{
               return -Math.abs(oldTick+currentTick);
            }           
        }
    }
    public static int roundTickIntoInt (double change){
        if(Math.abs(change-Math.ceil(change))<0.0005){
            return (int)Math.ceil(change);
        }
        else{
            return (int)Math.floor(change);
            
        }
    }
}
