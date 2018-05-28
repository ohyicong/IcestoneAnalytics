/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mysql;

import com.mycore.MyConstants;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {
     private Connection conn = null;

        public DBconnection() throws ClassNotFoundException {
            
            try {
                Class.forName("com.mysql.jdbc.Driver"); 
                this.conn = (com.mysql.jdbc.Connection)DriverManager.getConnection(MyConstants.sqlAddress,MyConstants.sqlName, MyConstants.sqlPassword);
                if (this.conn != null) {
                } else {
                    System.out.println("Yicong: SQL failed to be deployed");
                }
            } catch (SQLException var3) {
                System.out.println("Yicong: SQL failed to be deployed error of "+var3);
            }

        }
         public Connection getConnection() {
            return this.conn;
        }
}
