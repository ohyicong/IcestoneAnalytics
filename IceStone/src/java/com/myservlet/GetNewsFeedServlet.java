/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myservlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mycore.MyUser;
import com.mysql.SqlQuery;
import com.template.NewsFeedMenuSelect;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Owner
 */
@WebServlet(name = "GetNewsFeedServlet", urlPatterns = {"/GetNewsFeedServlet"})
public class GetNewsFeedServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code."
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
        response.setContentType("application/json");
        try {
            SqlQuery sqlQuery = new SqlQuery();
            ResultSet rs = sqlQuery.start("select * from doitransactions");
            JsonArray jsonArrayAll = new JsonArray();   
            JsonArray jsonArrayTransaction = new JsonArray();
            boolean isFirst=true;
            int tempId=0,count=0;
            while(rs.next()){
                if(isFirst){
                    isFirst=false;
                    tempId=rs.getInt("transactionid");
                }else if(tempId!=rs.getInt("transactionid")){
                    tempId=rs.getInt("transactionid");
                    jsonArrayAll.add(jsonArrayTransaction);
                    jsonArrayTransaction= new JsonArray();
                }
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("transnumber",rs.getInt("transactionid"));
                jsonObject.addProperty("shareholders", rs.getString("shareholders"));
                jsonObject.addProperty("beforeamount",rs.getInt("beforeamount"));
                jsonObject.addProperty("afteramount",rs.getInt("afteramount"));
                jsonObject.addProperty("transactionamount",rs.getInt("transactionamount"));
                jsonObject.addProperty("transtype", rs.getString("transtype"));
                jsonObject.addProperty("pdflink", rs.getString("pdflink"));
                jsonObject.addProperty("date", rs.getString("date"));
                jsonObject.addProperty("issuer", rs.getString("issuer"));
                jsonObject.addProperty("shareamount", rs.getString("shareamount"));
                jsonObject.addProperty("paidamount", rs.getString("paidamount"));
                jsonArrayTransaction.add(jsonObject);
                count++;
            }
            if(count!=0){
                jsonArrayAll.add(jsonArrayTransaction);
            }
            try (PrintWriter out = response.getWriter()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("isSyncing", MyUser.isNewsFeedSyncing);
                jsonObject.add("data",jsonArrayAll);
                jsonObject.addProperty("currentPdfPage",MyUser.currentPdfPage);
                out.println(jsonObject.toString());
                System.out.println(jsonObject.toString());
            }
            sqlQuery.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(GetNewsFeedServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
