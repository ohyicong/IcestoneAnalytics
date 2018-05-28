/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myservlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.myfeedos.FeedosSyncLookUp;
import com.template.StockName;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Owner
 */
@WebServlet(name = "SearchForSubStockServlet", urlPatterns = {"/SearchForSubStockServlet"})
public class SearchForSubStockServlet extends HttpServlet {

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
        System.out.println("Synclookup");
        String inputData = request.getReader().readLine();
        try (PrintWriter out = response.getWriter()) {
            StockName stockName = new Gson().fromJson(inputData,StockName.class);
            System.out.println("Yicong: StockName look up "+ stockName.getStockName());
            JsonArray jsonSend = new JsonArray();
            if(!stockName.getStockName().trim().equals("")){
                FeedosSyncLookUp feedosSyncLookUp = new FeedosSyncLookUp();
                jsonSend= feedosSyncLookUp.start(stockName.getStockName());
                out.println(jsonSend.toString());
                System.out.println(jsonSend.toString());
            }
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
