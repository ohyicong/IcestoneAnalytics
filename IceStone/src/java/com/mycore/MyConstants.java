/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycore;

/**
 *
 * @author Owner
 */
public class MyConstants {
    /*IMPORTANT FOR INITIAL CONNECTION TO QUANTHOUSE*/
    /*Any changes to login details please change here*/
    public static final String name ="northpoint_primary_prod01_ND";
    public static final String password="Ahghaph8ui" ;
    public static final String server="172.18.108.68" ;
    public static final int port=6040;
    
    /*IMPORTANT FOR INITIAL CONNECTION TO MySQL Database*/
    /*Any changes to login details please change here*/
    public static final String sqlAddress = "jdbc:mysql://localhost:3306/rockstone?useSSL=false";
    public static final String sqlName ="root";
    public static final String sqlPassword ="root";
    
    //My Network constants
    public static int broadcastLevelOneData = 1000;
    public static int broadcastLevelTwoData = 1001;
    public static int broadcastStockGraphData = 1002;
    
    //Default tick table ID *May change overtime*
    public static int defaultTickTableId=14483559;
}
