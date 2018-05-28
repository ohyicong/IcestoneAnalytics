/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myservlet;

import com.feedos.api.core.FeedOSException;
import com.google.gson.Gson;
import com.mycore.MyUser;
import com.mycore.StockGeneralInformation;
import com.myfeedos.FeedosASyncQuotSubInstrumentsL1;
import com.myfeedos.FeedosASyncQuotSubInstrumentsMBL;
import com.myfeedos.FeedosUpdateInitVariables;
import com.myfeedos.SocketBroadcast;
import com.mysql.SqlDelete;
import com.template.StockName;
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
@WebServlet(name = "DeleteStockServlet", urlPatterns = {"/DeleteStockServlet"})
public class DeleteStockServlet extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        System.out.println("Deleting stock");
        String stmtFormatter= "delete from subscribedstocks where internalcodes=%d and stocksname='%s' and exchangesymbol='%s' ";
        String inputData = request.getReader().readLine();
        StockGeneralInformation stockGenInfo = new Gson().fromJson(inputData,StockGeneralInformation.class);
        try {
            SocketBroadcast.deleteOverview(stockGenInfo);
            FeedosASyncQuotSubInstrumentsL1.drop(stockGenInfo);
            FeedosASyncQuotSubInstrumentsMBL.drop(stockGenInfo);
            FeedosUpdateInitVariables.removeStockGeneralInfo(stockGenInfo);
            FeedosUpdateInitVariables.removeStockLevelOneInfo(stockGenInfo);
            FeedosUpdateInitVariables.removeStockLevelTwoInfo(stockGenInfo);
            String stmt = String.format(stmtFormatter,stockGenInfo.getInternalCode(),stockGenInfo.getStockName(),stockGenInfo.getExchangeSymbol());
            SqlDelete.start(stmt);
            
            

        } catch (FeedOSException | InterruptedException | ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DeleteStockServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /*
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
