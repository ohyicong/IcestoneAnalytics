/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myalgo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mycore.MyUser;
import com.mycore.StockTransactionInformation;
import java.util.ArrayList;

/**
 *
 * @author Owner
 */
public class DetectAlgoTrading {
    public static JsonArray start(int internalCode,int percent){
        return getAlgoResults(internalCode,percent, "s");
        //getAlgoResults(internalCode,percent, "b");
    }
    public static JsonArray getAlgoResults(int internalCode,int percent, String type){
        ArrayList AL = MyUser.allStockTransactionInfoHashMap.get(internalCode);
        int count [] = new int [100000000]; //100,000,000
        int totalCount=0;
        int totalVolume=0;
        int highestPercent=0;
        for(int i=0;i<count.length;i++){
            count[i]=0;
        }
        for(int i=0;i<AL.size();i++){
            StockTransactionInformation stockTransactionInfo = (StockTransactionInformation) AL.get(i);
            if(stockTransactionInfo.getTransType().equals(type)){
                count[(int)stockTransactionInfo.getQuantity()]++;
            }
        }
        JsonArray originalArray = new JsonArray();
        for(int i=0;i<count.length;i++){
            if(count[i]!=0){
                System.out.println("Sell Vol amount @"+i+"Count@"+count[i]);
                totalCount+=count[i];
                totalVolume+=(i*count[i]);
            }
        }
        for(int i=0;i<count.length;i++){
            if(count[i]!=0){
                int tempPercent = (int) (((double)count[i]/totalCount )*100);
                if(tempPercent>highestPercent){
                    highestPercent=tempPercent;
                }
            }
        }
        ArrayList result = new ArrayList();
        System.out.println(type+"<-Results for "+percent+"% ->");
        for(int i=0;i<count.length;i++){
            if(count[i]!=0){
                double tempPercent = ((double)count[i]/totalCount )*100;
                if(tempPercent>=((percent*highestPercent)/100)&&tempPercent<(((percent+10)*highestPercent)/100)){
                   System.out.println(type+" Vol amount @"+i+"Count@"+count[i]+" "+tempPercent);
                   result.add(i);
                }
            }
        }
        System.out.println("<-Results for "+percent+"% ->"+highestPercent);
        JsonArray jsonArray = new JsonArray();
        for(int i=0;i<AL.size();i++){
            StockTransactionInformation stockTransactionInfo = (StockTransactionInformation) AL.get(i);
            for(int x=0;x<result.size();x++){
                if(stockTransactionInfo.getTransType().equals("s")&&stockTransactionInfo.getQuantity()==(int)result.get(x)){
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("volume", stockTransactionInfo.getQuantity());
                    jsonObject.addProperty("date",stockTransactionInfo.getTimeSGD());
                    jsonArray.add(jsonObject);
                }
            }
        }
        return jsonArray;   
    }
}
