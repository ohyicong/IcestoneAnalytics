/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycore;

import com.feedos.api.core.Session;
import com.feedos.api.requests.MBLLayer;
import com.feedos.api.requests.QuotationContentMask;
import com.feedos.api.requests.RequestSender;
import com.feedos.api.requests.SyncRequestSender;
import com.feedos.api.requests.VariableIncrementPriceBandTable;
import com.myfeedos.FeedosMySubscribeInstrumentsReceiverL1;
import com.myfeedos.MyObserverSession;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Owner
 */
public class MyUser {
    public static int worldcount=0;
    public static boolean isConnected=false;
    public static boolean isNewDay;
    public static HashMap <Integer,StockLevelOneInformation> stockLevelOneInfoHashMap; 
    public static HashMap <Integer,StockGeneralInformation> stockGeneralInfoHashMap; 
    public static HashMap <Integer,Double> stockPastAverageVolume;
    public static HashMap <Integer,ArrayList> stockLevelTwoInfoHashMap;
    public static Session session;
    public static MyObserverSession sessionObserver;
    public static SyncRequestSender syncRequester;
    public static QuotationContentMask requestedContent;
    public static FeedosMySubscribeInstrumentsReceiverL1 receiverSubL1;
    public static RequestSender asyncRequester;
    public static int subL1Number;
    public static int subMBLNumber;
    public static String lastPdfLink;
    public static int currentPdfPage;
    public static boolean isStoring;
    public static String timesgd;
    //Ticks variables
    public static VariableIncrementPriceBandTable currentTickTable;
    public static VariableIncrementPriceBandTable [] allTickTable;
    public static double [] currentTickLowerBoundary,currentTickPriceIncrement;
    
    //Options
    public static boolean isSqlRecordingOn;
    public static boolean isNewsFeedSyncing;
    
    //Storing of data into
    public static HashMap <Integer,ArrayList> allStockTransactionInfoHashMap;
    public static HashMap <Integer,ArrayList> allStockLevelOneInfoHashMap;
    public static HashMap <Integer,ArrayList> allStockLevelTwoInfoHashMap;
    
    public static HashMap <Integer,ArrayList> prevDayAllStockTransactionInfoHashMap;
    public static HashMap <Integer,ArrayList> prevDayAllStockLevelOneInfoHashMap;
    public static HashMap <Integer,ArrayList> prevDayAllStockLevelTwoInfoHashMap;
    
    
    
    
    
}
