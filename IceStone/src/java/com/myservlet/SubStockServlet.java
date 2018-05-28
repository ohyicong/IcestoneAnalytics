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
@WebServlet(name = "SubStockServlet", urlPatterns = {"/SubStockServlet"})
public class SubStockServlet extends HttpServlet {

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
        try {
            processRequest(request, response);
            response.setContentType("text/html;charset=UTF-8");
            String inputData = request.getReader().readLine();
            StockGeneralInformation stockGenInfo = new Gson().fromJson(inputData,StockGeneralInformation.class);
            
            //Update and add new subscribedstocks
            System.out.println("Subscribing stocks...");
            System.out.println("Current GeneralStocksSize: "+MyUser.stockGeneralInfoHashMap.size());
            System.out.println("Current LevelOneStocksSize: "+MyUser.stockLevelOneInfoHashMap.size());
            System.out.println("Current LevelTwoStocksSize: "+MyUser.stockLevelTwoInfoHashMap.size());
            System.out.println("Current AllLevelOneStocksSize: "+MyUser.allStockLevelOneInfoHashMap.size());
            System.out.println("Current AllLevelOneStocksSize: "+MyUser.allStockLevelTwoInfoHashMap.size());
            FeedosUpdateInitVariables.updateStockGeneralInfo(stockGenInfo);
            FeedosASyncQuotSubInstrumentsL1.add(stockGenInfo);
            FeedosASyncQuotSubInstrumentsMBL.add(stockGenInfo);
            System.out.println("After GeneralStocksSize: "+MyUser.stockGeneralInfoHashMap.size());
            System.out.println("After LevelOneStocksSize: "+MyUser.stockLevelOneInfoHashMap.size());
            System.out.println("After LevelTwoStocksSize: "+MyUser.stockLevelTwoInfoHashMap.size());
            System.out.println("After AllLevelOneStocksSize: "+MyUser.allStockLevelOneInfoHashMap.size());
            System.out.println("After AllLevelOneStocksSize: "+MyUser.allStockLevelTwoInfoHashMap.size());
      
        } catch (ClassNotFoundException | FeedOSException | SQLException ex) {
            Logger.getLogger(SubStockServlet.class.getName()).log(Level.SEVERE, null, ex);
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
