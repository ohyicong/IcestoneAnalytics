/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myfeedos;

import com.feedos.api.core.FeedOSException;
import com.feedos.api.requests.Constants;
import com.feedos.api.requests.InstrumentCharacteristics;
import com.feedos.api.requests.ListOfTagValue;
import com.feedos.api.requests.SyncRequestSender;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mycore.MyUser;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Owner
 */

public class FeedosSyncLookUp {
    private String lookUpPattern;

    public JsonArray start(String loopUpPattern){
     
        ListOfTagValue filter = null;
        filter = new ListOfTagValue();
        JsonArray jsonArray = new JsonArray();
        try {
            this.lookUpPattern = loopUpPattern;
            InstrumentCharacteristics[] result = MyUser.syncRequester.syncRefLookup(lookUpPattern, 10, false, filter,(int[]) null);
            
            if(result!=null){
                //The following code is to extract the following information: internalcode, stock name and exchange symbol from result array
                for(InstrumentCharacteristics instrument:result){
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("internalcode",instrument.getInternal_instrument_code());
                    //Iterate through the referential information 
                    for(int i=0; i<instrument.getRef_values().size();i++){
                        if(instrument.getRef_values().getTagNums()[i]==Constants.TAG_Description){
                                jsonObject.addProperty("stocksname",instrument.getRef_values().getTagValues()[i].get_string_nocheck()+"");
                        }else if(instrument.getRef_values().getTagNums()[i]==Constants.TAG_ExchangeSymbol){
                                jsonObject.addProperty("exchangesymbol",instrument.getRef_values().getTagValues()[i].get_string_nocheck()+"");
                        }
                    }
                    jsonArray.add(jsonObject);
                }
                System.out.println("FeedosSyncLookUp: information sent"+jsonArray.toString());
                return jsonArray;
            }
        } catch (FeedOSException ex) {
            Logger.getLogger(FeedosSyncLookUp.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("FeedosSyncLookUp: SyncLookUp failed to deploy");
        return null;
    }

}