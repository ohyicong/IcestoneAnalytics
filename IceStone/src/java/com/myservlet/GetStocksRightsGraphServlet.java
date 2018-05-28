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
import com.mycore.StockLevelOneInformation;
import com.mysql.SqlQuery;
import com.template.StockInternalCodePair;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
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
@WebServlet(name = "GetStocksRightsGraphServlet", urlPatterns = {"/GetStocksRightsGraphServlet"})
public class GetStocksRightsGraphServlet extends HttpServlet {

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
        String stockName,rightsName;
        int internalCodesOne, internalCodesTwo;
        double fundingFee,fundingBorrowed,brokerComm,accessFee,gst,lastPrice,subscriptionPrice,clearingFee,sharesBorrowed,borrowingFee,period;
        response.setContentType("application/json");
        String inputData = request.getReader().readLine();
        System.out.println(inputData);
        StockInternalCodePair stockInternalCodePair = new Gson().fromJson(inputData,StockInternalCodePair.class);
        System.out.println(stockInternalCodePair.getInternalCodeOne());
        System.out.println(stockInternalCodePair.getInternalCodeTwo());
        String stmt= "select * from stocksrightstrading where internalcodesone =%d and internalcodestwo=%d";     
        JsonObject jsonSend = new JsonObject();
        int count=0;
        SqlQuery sqlQuery = new SqlQuery();
        try {
            
            ResultSet rs = sqlQuery.start(String.format(stmt,stockInternalCodePair.getInternalCodeOne(),stockInternalCodePair.getInternalCodeTwo()));
            while(rs.next()){
                System.out.println(count);
                internalCodesOne = rs.getInt("internalcodesone");
                internalCodesTwo = rs.getInt("internalcodestwo");
                fundingFee = rs.getDouble("fundingfee");
                fundingBorrowed = rs.getDouble("fundingborrowed");
                brokerComm = rs.getDouble("brokercomm");
                accessFee = rs.getDouble("accessfee");
                gst = rs.getDouble("gst");
                lastPrice = rs.getDouble("lastprice");
                subscriptionPrice = rs.getDouble("subscriptionprice");
                clearingFee=rs.getDouble("clearingfee");
                sharesBorrowed = rs.getDouble("sharesborrowed");
                borrowingFee = rs.getDouble("borrowingFee");
                period = rs.getInt("period");
                jsonSend.add("stockinfo", packageLevelOneResult (internalCodesOne));
                jsonSend.add("rightsinfo",packageLevelOneResult (internalCodesOne));
                jsonSend.addProperty("internalcodesone",internalCodesOne);
                jsonSend.addProperty("internalcodestwo",internalCodesTwo);
                jsonSend.addProperty("stockname",MyUser.stockGeneralInfoHashMap.get(internalCodesOne).getStockName());
                jsonSend.addProperty("rightsname",MyUser.stockGeneralInfoHashMap.get(internalCodesTwo).getStockName());
                jsonSend.addProperty("fundingfee",fundingFee);
                jsonSend.addProperty("fundingborrowed",fundingBorrowed);
                jsonSend.addProperty("brokercomm",brokerComm);
                jsonSend.addProperty("accessFee",accessFee);
                jsonSend.addProperty("gst",gst);
                jsonSend.addProperty("lastprice",lastPrice);
                jsonSend.addProperty("subscriptionprice",subscriptionPrice);
                jsonSend.addProperty("clearingfee",clearingFee);
                jsonSend.addProperty("sharesborrowed",sharesBorrowed);
                jsonSend.addProperty("borrowingfee",borrowingFee);
                jsonSend.addProperty("period",period);
                jsonSend.addProperty("accessfee",accessFee);
            }
            rs.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(GetStocksRightsGraphServlet.class.getName()).log(Level.SEVERE, null, ex);            
        }
        try (PrintWriter out = response.getWriter()) {
            out.println(jsonSend.toString());
            System.out.println("GetStocksRights :"+jsonSend.toString());
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
    //Below is to get all levelone information from java 
    public static JsonObject packageLevelOneResult (int internalCode) throws SQLException{
        JsonObject jsonObject = new JsonObject();
        if(MyUser.allStockLevelOneInfoHashMap.get(internalCode)!=null){
            StockLevelOneInformation stockLevelOneInfo = MyUser.stockLevelOneInfoHashMap.get(internalCode);
            stockLevelOneInfo.getLastPrice();
            jsonObject.addProperty("bestbid",stockLevelOneInfo.getBestBid());
            jsonObject.addProperty("bestask",stockLevelOneInfo.getBestAsk());
            jsonObject.addProperty("bestbidqty",stockLevelOneInfo.getBestBidQty());
            jsonObject.addProperty("bestaskqty",stockLevelOneInfo.getBestAskQty());
            
        }
        return jsonObject;
    }
}
