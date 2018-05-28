/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myfeedos;

import com.feedos.api.core.*;
import com.feedos.api.requests.*;
import com.mycore.MyConstants;
import com.mycore.MyUser;
import static com.mycore.MyUser.session;
import java.sql.SQLException;

/**
 *
 * @author Owner
 */
public class FeedosConnection {
    

    public static boolean start() throws InterruptedException, ClassNotFoundException, SQLException, FeedOSException {
        MyUser.session = new Session();
        MyUser.sessionObserver= new MyObserverSession();
        
        //This is importasnt information to log into quanthouse system
        //Any changes please update the new credentials here 

        if (0 != Session.init_api("TheSessionForEverything")) {
            System.err.println("cannot initialise FeedOS API ");
            return false;
        }

        System.err.println("connecting...");

        ProxyFeedosTCP my_socket_proxy = new ProxyFeedosTCP(MyConstants.server, MyConstants.port, new Credentials(MyConstants.name, MyConstants.password));
        int rc = MyUser.session.open(MyUser.sessionObserver, my_socket_proxy, 0);

        if (rc != Constants.RC_OK) {
            MyUser.isConnected=false;
            System.err.println("Cannot connect: rc=" + Constants.getErrorCodeName(rc));
            return false;
        }
        MyUser.isConnected=true;
        System.err.println("connection OK");
        System.err.flush();
        FeedosInitVariables.start();
        return true;
    }
    public static void shutdown(){
        FeedosASyncQuotSubInstrumentsL1.stop();
        FeedosASyncQuotSubInstrumentsMBL.stop();
        MyUser.session.close();
        Session.shutdown_api();
        System.out.println("MyUser: All QuantHouse services successfully shutdown");
        MyUser.isConnected=false;
        MyUser.isSqlRecordingOn=false;
    }
}
