/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Owner
 */
public class SqlDelete {
    public static void start (String data) throws ClassNotFoundException, SQLException{
        Connection conn = null;
        PreparedStatement stmt = null;
        DBconnection dbConnection = new DBconnection();
        conn=dbConnection.getConnection();
        try {
            //System.out.println("SqlInsert: Executing this statement "+data);
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(data);
            stmt.executeUpdate();

        } catch (SQLException var) {
            System.out.println("Yicong: DB connection failed to deploy error of "+ var);
        } finally {
           if(conn!=null){
               conn.close();
           }
           if(stmt!=null){
               stmt.close();
           }

        }
    }
}
