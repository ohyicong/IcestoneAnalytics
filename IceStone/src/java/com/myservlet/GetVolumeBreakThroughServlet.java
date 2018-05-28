/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myservlet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mycore.MyUser;
import com.mycore.StockGeneralInformation;
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
@WebServlet(name = "GetVolumeBreakThroughServlet", urlPatterns = {"/GetVolumeBreakThroughServlet"})
public class GetVolumeBreakThroughServlet extends HttpServlet {

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
        JsonArray jsonArray = new JsonArray();
        MyUser.stockGeneralInfoHashMap.values().stream().forEach((stockGeneralInfo) -> {
            double avgVolume = MyUser.stockPastAverageVolume.get(stockGeneralInfo.getInternalCode());
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("internalcode", stockGeneralInfo.getInternalCode());
            jsonObject.addProperty("stockname", stockGeneralInfo.getStockName());            
            jsonObject.addProperty("symbol",stockGeneralInfo.getExchangeSymbol() );
            jsonObject.addProperty("avgvolume", avgVolume);
            jsonObject.addProperty("volumetraded",MyUser.stockLevelOneInfoHashMap.get(stockGeneralInfo.getInternalCode()).getVolumeTraded());
            jsonObject.addProperty("color","transparent");
            jsonObject.addProperty("difference",Math.abs(avgVolume-MyUser.stockLevelOneInfoHashMap.get(stockGeneralInfo.getInternalCode()).getVolumeTraded()));
            jsonArray.add(jsonObject);
        });
        try (PrintWriter out = response.getWriter()) {
           out.println(jsonArray);
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
