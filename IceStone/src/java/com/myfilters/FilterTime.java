/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myfilters;

import java.util.Calendar;

/**
 *
 * @author Owner
 */
public class FilterTime {
    
    public static String formatTimeForSQL (String time){
        String newTime="";
        int countColon=0;
        for(int i=0;i<time.length();i++){        
            if(time.charAt(i)!='.'){
                if(time.charAt(i)==':'){
                     countColon++;
                }
                if(countColon==3){
                    newTime+=".";
                    countColon++;
                }else{
                    newTime+=time.charAt(i);
                }
            }
            
        }
        return newTime;
    }
    public static String sgd (String time){
        //time must be filtered already
        String rawMinSec="";
        String rawHour="";
        String rawDate="";
        String Hour="";
        String newTime="";
        int hoursInt;
        for(int i=0;i<=9;i++){
            rawDate+=time.charAt(i);
        }
        for(int i=13;i<time.length();i++){
            if(time.charAt(i)!='}') {
                rawMinSec += time.charAt(i);
            }
        }
        rawHour+= time.charAt(11);
        rawHour+= time.charAt(12);
        hoursInt= Integer.parseInt(rawHour)+8;
        if(hoursInt<10){
            newTime=rawDate+" 0"+hoursInt+rawMinSec;

        }else{
            newTime=rawDate+" "+hoursInt+rawMinSec;
        }
        return newTime;
    }
    public static String getTime (String time){
        String newTime ="";
        boolean isStart=false;
        for(int i=0;i<time.length();i++){
            if(time.charAt(i)==' '){
                isStart=true;
            }else if(isStart){
                newTime+=time.charAt(i);
            }
        }
        return newTime;
    }
    public static String getDate(String time){
        String newTime="";
        for(int i=0;i<time.length();i++){
            if(time.charAt(i)==' '){
                return newTime.trim();
            }
            newTime+=time.charAt(i);
            
        }
        return newTime.trim();
    }
    
    public static Calendar goBackInTime (Calendar now, int days){
        int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
        if(days==0){
            return now;
        }
        if(dayOfWeek==Calendar.SATURDAY||dayOfWeek==Calendar.SUNDAY){
            now.add(Calendar.DAY_OF_YEAR,-1);
            return goBackInTime(now,days);
        }else{
            //System.out.println("co");
            now.add(Calendar.DAY_OF_YEAR,-1);
            return goBackInTime(now,days-1);
        }


    }
    
}