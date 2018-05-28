/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myservlet;

import com.google.gson.Gson;
import com.mysql.SqlInsert;
import com.template.StockInternalCodePair;
import com.template.StocksRights;
import java.io.IOException;
import java.io.PrintWriter;
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
@WebServlet(name = "AddStocksRightsServlet", urlPatterns = {"/AddStocksRightsServlet"})
public class AddStocksRightsServlet extends HttpServlet {

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
        String input = request.getReader().readLine();
        StocksRights stocksRights = new Gson().fromJson(input,StocksRights.class);
        System.out.println(stocksRights.getInternalCodesOne());
        response.setContentType("application/json");
        String stmtFormat = "insert into stocksrightstrading (internalcodesone,internalcodestwo,fundingfee,brokercomm,accessfee,gst,lastprice,subscriptionprice,fundingborrowed,sharesborrowed,borrowingfee,period,clearingfee) values (%d,%d,%f,%f,%f,%f,%f,%f,%f,%f,%f,%d)";
        String stmtSend = String.format(stmtFormat,stocksRights.getInternalCodesOne(),stocksRights.getInternalCodesTwo(),stocksRights.getFundingFee(),stocksRights.getBrokerComm(),stocksRights.getAccessFee(),stocksRights.getGst(),stocksRights.getLastPrice(),stocksRights.getSubscriptionPrice(),stocksRights.getFundingBorrowed(),stocksRights.getSharesBorrowed(),stocksRights.getBorrowingFee(),stocksRights.getPeriod(),stocksRights.getClearingFee());
        System.out.println(stmtSend);
        try {
            SqlInsert.start(stmtSend);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(AddStocksRightsServlet.class.getName()).log(Level.SEVERE, null, ex);
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
