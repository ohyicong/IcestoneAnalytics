/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myservlet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mycore.MyUser;
import com.mycore.StockLevelOneInformation;
import com.mysocket.OverviewSocket;
import com.mysql.SqlQuery;
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
@WebServlet(name = "GetStocksRightsServlet", urlPatterns = {"/GetStocksRightsServlet"})
public class GetStocksRightsServlet extends HttpServlet {

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
        String stmt = "select * from stocksrightstrading ";
        SqlQuery sqlQuery = new SqlQuery();
        JsonArray jsonArray = new JsonArray();
    
        try {
            ResultSet rs = sqlQuery.start(stmt);
            while (rs.next()){
                double subscriptionPrice= rs.getDouble("subscriptionprice");
                double brokerComm = rs.getDouble("brokercomm");
                double accessFee=rs.getDouble("accessfee");
                double clearingFee= rs.getDouble("clearingfee");
                double gst = rs.getDouble("gst");
                double lastPrice=rs.getDouble("lastprice");
                int period = rs.getInt("period");
                double fundingFee= rs.getDouble("fundingfee");
                double borrowingFee = rs.getDouble("borrowingfee");
                double fundingBorrowed = rs.getDouble("fundingborrowed");
                double sharesBorrowed = rs.getDouble("sharesborrowed");
                int internalCodesOne = rs.getInt("internalcodesone");
                int internalCodesTwo = rs.getInt("internalcodestwo");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("subscriptionprice",subscriptionPrice);
                jsonObject.addProperty("brokercomm",brokerComm);
                jsonObject.addProperty("accessfee",accessFee);
                jsonObject.addProperty("clearingfee",clearingFee);
                jsonObject.addProperty("gst",gst);
                jsonObject.addProperty("lastprice",lastPrice );
                jsonObject.addProperty("period",period);
                jsonObject.addProperty("fundingfee",fundingFee);
                jsonObject.addProperty("borrowingfee",borrowingFee);
                jsonObject.addProperty("fundingborrowed",fundingBorrowed);
                jsonObject.addProperty("sharesborrowed",sharesBorrowed);
                jsonObject.addProperty("internalcodesone",internalCodesOne);
                jsonObject.addProperty("internalcodestwo",internalCodesTwo);
                jsonObject.addProperty("stocknameone",MyUser.stockGeneralInfoHashMap.get(internalCodesOne).getStockName());
                jsonObject.addProperty("stocknametwo",MyUser.stockGeneralInfoHashMap.get(internalCodesTwo).getStockName());
                jsonObject.addProperty("stocksymbolone",MyUser.stockGeneralInfoHashMap.get(internalCodesOne).getExchangeSymbol());
                jsonObject.addProperty("stocksymboltwo",MyUser.stockGeneralInfoHashMap.get(internalCodesTwo).getExchangeSymbol());
                jsonObject.addProperty("spread","N/A");
                jsonObject.add("stockdataone",getOverviewInfo(MyUser.stockLevelOneInfoHashMap.get(internalCodesOne)));
                jsonObject.add("stockdatatwo",getOverviewInfo(MyUser.stockLevelOneInfoHashMap.get(internalCodesTwo)));
                jsonArray.add(jsonObject);
            }
            sqlQuery.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(GetStocksRightsServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        try (PrintWriter out = response.getWriter()) {
            out.println(jsonArray.toString());
            System.out.println("GetStocksRights :"+jsonArray.toString());
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
    
    public static JsonObject getOverviewInfo (StockLevelOneInformation stockLevelOneInfo){
        JsonObject jsonObject = new JsonObject();
        JsonObject sendJson = new JsonObject();
        jsonObject.addProperty("internalcode",stockLevelOneInfo.getInternalCode());  
        jsonObject.addProperty("lastprice",stockLevelOneInfo.getLastPrice());
        jsonObject.addProperty("closingprice",stockLevelOneInfo.getClosingPrice());
        jsonObject.addProperty("bestbid",stockLevelOneInfo.getBestBid());
        jsonObject.addProperty("bestbidqty",stockLevelOneInfo.getBestBidQty());
        jsonObject.addProperty("bestask",stockLevelOneInfo.getBestAsk());
        jsonObject.addProperty("bestaskqty",stockLevelOneInfo.getBestAskQty());
        jsonObject.addProperty("tradingstatus", stockLevelOneInfo.getTradingStatus());
        sendJson.add("levelone", jsonObject);
        sendJson.addProperty("status","update");
        sendJson.addProperty("sqlstatus", MyUser.isSqlRecordingOn);
        sendJson.addProperty("timesgd",MyUser.timesgd);
        OverviewSocket.sendMessage(sendJson.toString());
        return sendJson;
    }
}
