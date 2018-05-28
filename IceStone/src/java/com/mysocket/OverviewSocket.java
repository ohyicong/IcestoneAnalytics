/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mysocket;

import com.mycore.MyUser;
import com.myfeedos.SocketBroadcast;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author Owner
 */
@ServerEndpoint (value="/overviewsocket")
public class OverviewSocket {
    public static Set <Session> users = Collections.synchronizedSet(new HashSet<Session>());
    public static HashMap <Session,String> usernames = new HashMap <Session,String>(); 
    
    @OnOpen
    public void handleOpen(Session userSession){
        System.out.println("Connection opened for overview:"+userSession.getId());
        users.add(userSession);
      
    }
    
    @OnClose
    public void handleClose(Session userSession){
        users.remove(userSession);
        System.out.println("Connection closed for overview :"+userSession.getId());
    }
    
    @OnMessage
    public void handleMessage(String message,Session userSession) throws IOException{
        if(message.trim().equals("100")){
            //Request to reset overview;
            System.out.println("Initialise Overview Stocks");
            SocketBroadcast.initOverview();
        }else if(message.trim().equals("201")){
            //Request to turn on recording function
            System.out.println("SQL recording true");
            MyUser.isSqlRecordingOn=true;
        }else if(message.trim().equals("200")){
            //Request to turn off recording function
            System.out.println("SQL recording false");
            MyUser.isSqlRecordingOn=false;
        }
    }
    public static void sendMessage(String message) {
        try{
            if(users!=null){
                users.stream().forEach((session) -> {
                    session.getAsyncRemote().sendText(message);
                });
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }

   
}