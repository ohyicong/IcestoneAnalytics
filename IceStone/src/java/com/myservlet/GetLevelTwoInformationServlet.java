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
import com.mycore.StockLayerInformation;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
@WebServlet(name = "GetLevelTwoInformationServlet", urlPatterns = {"/GetLevelTwoInformationServlet"})
public class GetLevelTwoInformationServlet extends HttpServlet {

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
        JsonArray jsonSend = new JsonArray();
        MyUser.stockGeneralInfoHashMap.values().stream().forEach((stockGeneralInfo) -> {
            try {
                JsonObject jsonObject = new JsonObject();
                JsonArray jsonArray = processMBL(stockGeneralInfo.getInternalCode(),MyUser.stockLevelTwoInfoHashMap.get(stockGeneralInfo.getInternalCode()));
                jsonObject.addProperty("stocksname",stockGeneralInfo.getStockName());
                jsonObject.addProperty("internalcode", stockGeneralInfo.getInternalCode());
                jsonObject.add("data",jsonArray);
                jsonSend.add(jsonObject);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(GetLevelTwoInformationServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        try (PrintWriter out = response.getWriter()) {
            out.println(jsonSend);
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
    
    public static JsonArray processMBL(int internalCode,ArrayList mblLayer) throws ClassNotFoundException{
        JsonArray jsonArray = new JsonArray();
        StockLayerInformation stockLayerInformation;
        JsonArray boxColors = new JsonArray();
        JsonArray numberColors = new JsonArray();
        for(int i=0;i<6;i++){
            boxColors.add("norm");
        }
        for(int i=0;i<6;i++){
            numberColors.add("black");
        }
        if(mblLayer==null){
            System.out.println("MblLayer null");
            return jsonArray;
        }
        for(int i=0;i<mblLayer.size();i++){
                JsonObject jsonObject = new JsonObject();
                stockLayerInformation = (StockLayerInformation) mblLayer.get(i);
                jsonObject.addProperty("level", stockLayerInformation.getLevel());
                jsonObject.addProperty("bid", stockLayerInformation.getBid());
                jsonObject.addProperty("bidqty", stockLayerInformation.getBidQty());
                jsonObject.addProperty("ask", stockLayerInformation.getAsk());
                jsonObject.addProperty("askqty", stockLayerInformation.getAskQty());
                jsonObject.addProperty("bidqueue", stockLayerInformation.getBidQueue());
                jsonObject.addProperty("askqueue", stockLayerInformation.getAskQueue());
                jsonObject.add("boxcolors",boxColors);
                jsonObject.add("numbercolors",numberColors);
                jsonArray.add(jsonObject);         
            }
        
        //
        return jsonArray;
    }

}
