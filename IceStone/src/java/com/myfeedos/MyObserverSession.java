package com.myfeedos;

import com.feedos.api.core.*;
import com.mycore.MyUser;
import com.mycore.StockGeneralInformation;
import com.myfilters.FilterTime;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/*
 * (c) Copyright 2004 FeedOS
 * All Rights Reserved.
 * 
 * @author fenouil
 */


/** 
 * sample "Observer" for reacting to session events
 */

public class MyObserverSession implements SessionObserver {
        public MyObserverSession(){
            
        }
    
	public static void DUMP (String s) {
		System.out.println(s);
		System.out.flush();
	}

	//---------------------------------------------------------------------------                                                                                
	//							ProxyObserver
	//---------------------------------------------------------------------------

        @Override
	public void sessionOpened (Session proxy, int heartbeat_period_sec, int uid, int gid)
	{
		DUMP("MySessionObserver: session opened, heartbeat_period_sec = " + heartbeat_period_sec);
		DUMP("User ID : " + uid + ", Group ID : " +gid);
	}

	/// heartbeat signal from server

    /**
     *
     * @param proxy
     * @param heartbeat_period_sec
     */
        @Override
	public void heartbeat (Session proxy, long heartbeat_period_sec) {
                String currentDateTime=PDU.date2ISOstring(heartbeat_period_sec);
		DUMP("MySessionObserver: session heartbeat, server_date = " + currentDateTime );
                Calendar now = Calendar.getInstance();
                System.out.println("Singapore:"+ now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE));
                if(now.get(Calendar.HOUR_OF_DAY)>=18 && now.get(Calendar.MINUTE) >=0 &&MyUser.isSqlRecordingOn&&MyUser.isNewDay){
                    MyUser.isNewDay=false;
                    Thread thread = new Thread(){
                         @Override
                         public void run() {
                            MyUser.isStoring=true;//
                            System.out.println("Start recording intraday information");
                            MyUser.stockGeneralInfoHashMap.entrySet().stream().forEach((Map.Entry<Integer, StockGeneralInformation> entry) -> {
                                StockGeneralInformation currentStock = entry.getValue();
                                try {
                                    RecordIntoJava.storeIntoSql(currentStock.getInternalCode());
                                } catch (ClassNotFoundException | FeedOSException | SQLException ex) {
                                    Logger.getLogger(MyObserverSession.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            });
                            MyUser.isStoring=false;
                        }
                    };
                    thread.start();
                    
                }
                if(now.get(Calendar.HOUR_OF_DAY)==8 && now.get(Calendar.MINUTE) >=10&&!MyUser.isNewDay){
                    System.out.println("New DAY!!!");
                    try {
                        FeedosInitVariables.restart();
                    } catch (ClassNotFoundException | SQLException | FeedOSException ex) {
                        Logger.getLogger(MyObserverSession.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
	}

        @Override
	public void adminMessage (Session proxy, boolean isUrgent, String origin, String headline, String content) {

		String adminMessage = "MySessionObserver: admin message: ";
		if(isUrgent) adminMessage+=("[URGENT]");
		adminMessage+= origin + " " + headline + " " + content; 
		DUMP(adminMessage);
	}
	/// connection with server has been lost. pending requests are about to be terminated
        @Override
	public void closeInProgress (Session proxy) {
		DUMP("MySessionObserver: session closeInProgress");
		// TODO: set a flag to disable error-handling of pending requests ?
	}

	/// connection with server has been lost. pending requests have been terminated
        @Override
	public void closeComplete (Session proxy) {
		DUMP("MySessionObserver: session closeComplete");
		// TODO: synchronously reopen session ?
	}

}


