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
public class SqlInsert {
    
    public static void start (String data) throws ClassNotFoundException, SQLException{
        Connection conn = null;
        PreparedStatement stmt = null;
        DBconnection dbConnection = new DBconnection();
        conn=dbConnection.getConnection();
        try {
            stmt = conn.prepareStatement(data);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Yicong: DB connection failed to deploy error of "+ ex);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }

        }
    }
    
}
