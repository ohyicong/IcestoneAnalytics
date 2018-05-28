/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myservlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mycore.MyUser;
import com.mysql.SqlInsert;
import com.mysql.SqlQuery;
import com.template.StockInternalCode;
import com.template.StockInternalCodePair;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
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
@WebServlet(name = "AddPairTradingServlet", urlPatterns = {"/AddPairTradingServlet"})
public class AddPairTradingServlet extends HttpServlet {

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

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
        String inputData = request.getReader().readLine();
        String stmt="";
        System.out.println(inputData);
        StockInternalCodePair stockInternalCodePair = new Gson().fromJson(inputData,StockInternalCodePair.class);
        int internalCodeOne = stockInternalCodePair.getInternalCodeOne();
        int internalCodeTwo = stockInternalCodePair.getInternalCodeTwo();
        int percentage = stockInternalCodePair.getPercentage();
        String insertFormatter = "insert into pairtrading (internalcodesone,internalcodestwo,percentage) values (%d,%d,%d) ";
        stmt = String.format(insertFormatter, internalCodeOne , internalCodeTwo , percentage);
        try {
            SqlInsert.start(stmt);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(AddPairTradingServlet.class.getName()).log(Level.SEVERE, null, ex);
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
