/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myservlet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mycore.MyUser;
import com.mysql.SqlDelete;
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
@WebServlet(name = "GetPairTradingServlet", urlPatterns = {"/GetPairTradingServlet"})
public class GetPairTradingServlet extends HttpServlet {

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
        String stmt="";
        String selectStmt = "select * from pairtrading";
        String deleteFomatter = "delete from pairtrading where internalcodesone=%d and internalcodestwo=%d";
        String getAveragePriceFormatter ="select * from dailyinformation where internalcodes=%d";
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonSend = new JsonObject();
        int internalCodesOne,internalCodesTwo;
        double percentage;
       
        try {
            SqlQuery sqlQuery = new SqlQuery();
            ResultSet rs = sqlQuery.start(selectStmt);
            while(rs.next()){            
                internalCodesOne = rs.getInt("internalcodesone");
                internalCodesTwo = rs.getInt("internalcodestwo");
                percentage = rs.getDouble("percentage");
                if(MyUser.stockGeneralInfoHashMap.get(internalCodesOne)==null||MyUser.stockGeneralInfoHashMap.get(internalCodesTwo)==null&&MyUser.isConnected){
                    stmt= String.format(deleteFomatter, internalCodesOne,internalCodesTwo);
                    SqlDelete.start(stmt);
                    stmt =String.format(deleteFomatter, internalCodesTwo,internalCodesOne);
                    SqlDelete.start(stmt);
                    return;
                }else{
                    stmt =String.format(getAveragePriceFormatter,internalCodesOne);
                    SqlQuery sqlQuery2 = new SqlQuery();
                    ResultSet rsAverage1 = sqlQuery2.start(stmt);
                    System.out.println(stmt);
                    int count1=0,count2=0;
                    double total1=0,total2=0,average1=0,average2=0;
                    while(rsAverage1.next()){
                        total1+=rsAverage1.getDouble("closingprice");
                        count1++;
                        System.out.println("Average 1 " + total1);
                    }
                    stmt =String.format(getAveragePriceFormatter,internalCodesTwo);
                    sqlQuery2.close();
                    ResultSet rsAverage2 = sqlQuery2.start(stmt);
                    System.out.println(stmt);
                    while(rsAverage2.next()){
                        total2+=rsAverage2.getDouble("closingprice");
                        count2++;
                        System.out.println("Average 2 " + total2);
                    }
                    sqlQuery2.close();
                    if(total1==0){
                        average1 = MyUser.stockLevelOneInfoHashMap.get(internalCodesOne).getClosingPrice();
                        System.out.println("I'm assigned 1");
                    }else{
                        average1=total1/count1;
                    }
                    if(total2==0){
                        average2=MyUser.stockLevelOneInfoHashMap.get(internalCodesTwo).getClosingPrice();
                         System.out.println("I'm assigned 2");
                    }else{
                        average2=total2/count2;
                    }
                    System.out.println("Average 1 is "+average1 +" Average 2 is"+average2);
                    System.out.println("internalcode 1 is "+internalCodesOne +" internalcode 2 is"+internalCodesTwo);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("internalcodesone",internalCodesOne);
                    jsonObject.addProperty("internalcodestwo", internalCodesTwo);
                    jsonObject.addProperty("stocksnameone", MyUser.stockGeneralInfoHashMap.get(internalCodesOne).getStockName());
                    jsonObject.addProperty("stocksnametwo", MyUser.stockGeneralInfoHashMap.get(internalCodesTwo).getStockName());
                    jsonObject.addProperty("averageclosingone",average1);
                    jsonObject.addProperty("averageclosingtwo",average2);
                    jsonObject.addProperty("lastpriceone",MyUser.stockLevelOneInfoHashMap.get(internalCodesOne).getLastPrice());
                    jsonObject.addProperty("lastpricetwo",MyUser.stockLevelOneInfoHashMap.get(internalCodesTwo).getLastPrice());
                    jsonObject.addProperty("olddifference", Math.abs(average1-average2));
                    jsonObject.addProperty("currentdifference", Math.abs(MyUser.stockLevelOneInfoHashMap.get(internalCodesOne).getLastPrice()-MyUser.stockLevelOneInfoHashMap.get(internalCodesTwo).getLastPrice()));
                    jsonObject.addProperty("color","none");
                    jsonObject.addProperty("percentage",percentage);
                    jsonArray.add(jsonObject);
                }               
            }
            sqlQuery.close();
            
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(GetPairTradingServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            jsonSend.addProperty("infotype","init");
            jsonSend.add("data",jsonArray);
            out.println(jsonSend.toString());
            System.out.println(jsonSend.toString());
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
