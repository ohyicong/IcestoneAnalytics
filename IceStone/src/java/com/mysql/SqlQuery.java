/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Owner
 */
public class SqlQuery {
    private Statement stmt;
    private Connection conn;
    public SqlQuery(){
        this.stmt = null;
        this.conn = null;
    }
    //SQL query returns ResultSet upon successful query 
    public ResultSet start (String data) throws ClassNotFoundException, SQLException{
        DBconnection dbConnection = new DBconnection();
        conn=dbConnection.getConnection();          
        try {
            System.out.println(data);
            stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery(data);
            return rset;
        } catch (SQLException ex) {
            Logger.getLogger(SqlQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public void close () throws SQLException{
        if(this.stmt!=null){
            this.stmt.close();
        }
        if(this.conn!=null){
            this.conn.close();
        }
    }
}