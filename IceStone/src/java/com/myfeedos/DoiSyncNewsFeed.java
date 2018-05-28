/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myfeedos;

import com.mycore.MyUser;
import com.mysql.SqlDelete;
import com.mysql.SqlInsert;
import com.mysql.SqlQuery;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.Test;



/**
 *
 * @author Owner
 */
public class DoiSyncNewsFeed {
    private static int count=0;
    private static int preCount=0;
    
    
    public static void init() throws ClassNotFoundException, SQLException{
        System.out.println("Deleting past records");
        String stmt ="delete from doitransactions";
        SqlDelete.start(stmt);
        stmt ="delete from doitransactionsummary";
        SqlDelete.start(stmt);  
        stmt ="delete from sharesbuyback";
        SqlDelete.start(stmt);
    }
    
    @Test
    public static void start() throws IOException, ClassNotFoundException, SQLException{
        //
        //Wait for it to stop
        //MyUser.isNewsFeedSyncing = !MyUser.isNewsFeedSyncing; 
        MyUser.isNewsFeedSyncing=true;
        if(MyUser.isNewsFeedSyncing){
           System.out.println("Syncing started....");
           DoiSyncNewsFeed.init();
           MyUser.currentPdfPage=0;
           //Setting up PhantomJS Driver
           //File src = new File("C:\\Users\\Owner\\Documents\\NetBeansProjects\\IceStone\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");
           //System.setProperty("phantomjs.binary.path",src.getAbsolutePath());
           File src = new File("C:\\Users\\Owner\\Documents\\NetBeansProjects\\IceStone\\chromedriver.exe");
           System.setProperty("webdriver.chrome.driver", src.getAbsolutePath());
           WebDriver driver = new ChromeDriver();
           //Go to link
           driver.get("http://www.sgx.com/wps/portal/sgxweb/home/company_disclosure/company_announcements");
           driver.findElement(By.className("modal-btn-close")).click();
           //Select history period
           Select dropdown  = new Select(driver.findElement(By.id("period")));
           dropdown.selectByVisibleText("Last 3 Months");

           WebElement [] webElements ;
           JavascriptExecutor jse = (JavascriptExecutor) driver;
           //Execute "GO" to go to last 3 months result
           jse.executeScript("fetchResult();");

           while(true) {
               //Wait for page to load
               while(driver.findElement(By.tagName("body")).getText().contains("Loading")){
                   System.out.println("Still loading");
               }
               System.out.println("Scanning page ");
               MyUser.currentPdfPage++;
               //This is to retrieve all PDF link
               ArrayList secondLinks = DoiSyncNewsFeed.getSecondLinks((ChromeDriver) driver);
               if(secondLinks!=null) {
                   //Download PDF
                   for (int i = preCount; i < preCount+secondLinks.size(); i++) {
                       String secondLink = (String) secondLinks.get(i-preCount);
                       System.out.println("PDF Link " + secondLink);
                       DoiSyncNewsFeed.createPDF(secondLink);

                   }
                   for (int i = preCount; i <preCount+secondLinks.size(); i++) {
                       String secondLink = (String) secondLinks.get(i-preCount);
                       DoiSyncNewsFeed.convertIntoText(i);
                       DoiSyncNewsFeed.readFromText(i,secondLink);
                   }
               }
               preCount=count;
               try{
                    WebElement webElement = driver.findElement(By.id("PageIdnextIdns_Z7_680412S0JO2450AOAFHGTG30G5_"));
                    if(!webElement.getText().contains("Next")){
                        System.out.println("Break0");
                        MyUser.isNewsFeedSyncing=false;
                        driver.close();
                        break;
                    }
                    //check if there is next
                    System.out.println("Does next exist? "+webElement.getText());
                    webElement.click();
               }catch (Exception e){
                   System.out.println("Break1");
                   MyUser.isNewsFeedSyncing=false;
                   driver.close();
                   break;
               }
               if(MyUser.currentPdfPage>5){
                   System.out.println("Break2");
                   MyUser.isNewsFeedSyncing=false;
                   driver.close();
                   break;
               }
               
           }
           
        }
    }
    public static void readFromText (int tempCount,String pdfLink) throws IOException, ClassNotFoundException, SQLException {
        System.out.println("Reading text.... Dcoument "+tempCount);
        Date calendar = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        String listedIssuer="N/A",date="N/A";
        FileReader fileReader = new FileReader("C:\\Users\\Owner\\Documents\\NetBeansProjects\\IceStone\\pdfdownload\\downloaded"+tempCount+".txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line="",tempShareHolder="N/A";
        ArrayList shares= new ArrayList(),shareHolder = new ArrayList();
        String sharesAcqOrDes= "";
        String amountAcqOrDes= "";
        String issuerChecks [] ={"FORM 3/[ Version 2.0 ]/Effective Date [ 21 March 2014 ]","Name of Listed Issuer"};
        String buyerChecks [] ={"Name of Substantial Shareholder/Unitholder","Name of Director/CEO:3","Is Substantial Shareholder/Unitholder"};
        String amountChecks [] ={"acquired or disposed","duties):"}; 
        String dateChecks [] = {"change in, interest)","Date of acquisition of or change in interest:"};
        String befAftChecks [] ={"convertible debentures:","No. of ordinary voting shares","convertible debentures :","No. of rights/options/warrants"};
        while((line=bufferedReader.readLine())!=null){
            if(line.contains(issuerChecks[0])){
                tempShareHolder=bufferedReader.readLine();
                line=tempShareHolder;
            }
                      
            if(line.contains(issuerChecks[1])){
                listedIssuer = bufferedReader.readLine();
            }else if(line.contains(buyerChecks[0])||line.contains(buyerChecks[1])){
                if((line=bufferedReader.readLine()).contains(buyerChecks[2])){
                    String removeCommasShareHolder = "";
                    for(int i=0;i<tempShareHolder.length();i++){
                        if(!(tempShareHolder.charAt(i)>=33&&tempShareHolder.charAt(i)<=47)){
                            removeCommasShareHolder+=tempShareHolder.charAt(i);
                        }
                    }
                    shareHolder.add(removeCommasShareHolder);
                }else{
                    String removeCommasShareHolder = "";
                    for(int i=0;i<line.length();i++){
                        if(!(line.charAt(i)>=33&&line.charAt(i)<=47)){
                            removeCommasShareHolder+=line.charAt(i);
                        }
                    }
                    shareHolder.add(removeCommasShareHolder);
                }

            }else if(line.contains(amountChecks[0])){
                String tempString ="";
                line=bufferedReader.readLine();
                while(!(line.trim().contains("consideration paid or received")||line.trim().contains("Effective Date"))){
                    tempString+=line.trim()+" ";
                    line=bufferedReader.readLine();
                    System.out.println("Im stuck21");
                }
                sharesAcqOrDes=tempString;
                System.out.println("NewsFeed " +" "+tempString);
            
            }else if(line.contains(amountChecks[1])){
                String tempString="";
                line=bufferedReader.readLine();
                while(!((line.contains("Date")||line.contains("Acquisition of:")||line.trim().equals("A")||line.trim().contains("interest or change")))){
                    tempString+=line+" ";
                    line=bufferedReader.readLine();
                    System.out.println("Im stuck31 ");
                }
                amountAcqOrDes=tempString;
                System.out.println("NewsFeed "+tempString+"\n");           
            }else if(line.contains(dateChecks[0])||line.contains(dateChecks[1])) {
                line=bufferedReader.readLine();
                for(int i=0;i<3;i++){
                    if(line.contains("-20")){
                        date = line;                      
                    }  
                    line=bufferedReader.readLine().trim();
                }    
                
            } else if(line.contains(befAftChecks [0])||line.contains(befAftChecks [1])||line.contains(befAftChecks [2])||line.contains(befAftChecks [3])){
                line=bufferedReader.readLine();
                while(line.trim().equals("")){
                    line=bufferedReader.readLine();
                }
                //Removing the commas 
                boolean isFinish = false;
                int initCountSpace=0;
                int countSpace=0;
                String sharesTotal="";
                for(int i=0;i<line.length();i++){
                    if(line.charAt(i)==' ') {
                        if(line.charAt(i+1)>=65&&line.charAt(i+1)<=122){
                            break;
                        }
                        initCountSpace++;
                    }
                }
                for(int i=0;i<line.length();i++){
                    if(line.charAt(i)==' ') {
                        countSpace++;
                    }else if(countSpace ==initCountSpace&&line.charAt(i)!=','){
                        sharesTotal+=line.charAt(i);
                        if(line.charAt(i)==' '){
                            break;
                        }
                    }
                }
                shares.add(sharesTotal);
            }


        }
        System.out.println("Finished reading");
        SqlInsert.start("insert into doitransactionsummary (date,datetime) values ('"+date+"','"+simpleDateFormat.format(calendar)+"')");
        SqlQuery sqlQuery = new SqlQuery();
        ResultSet rs = sqlQuery.start("select * from doitransactionsummary where datetime >= '"+simpleDateFormat2.format(calendar)+"'" );
        System.out.println(simpleDateFormat.format(calendar)+" "+simpleDateFormat2.format(calendar));
        int rsCount=0;
        String tempListedIssuer="";
        for(int i=0;i<listedIssuer.length();i++){
            if(!(listedIssuer.charAt(i)>=33&&listedIssuer.charAt(i)<=47)){
                tempListedIssuer+=listedIssuer.charAt(i);
            }
        }
        listedIssuer = tempListedIssuer;
        while(rs.next()){
            rsCount=rs.getInt("transactionsummaryid");
            
        }
        String stmtTransactionFormatter = "insert into doitransactions (transactionid,shareholders,beforeamount,afteramount,transactionamount,transtype,pdflink,date,issuer,shareamount,paidamount) values (%d,'%s',%s,%s,%s,'%s','%s','%s','%s','%s','%s') ";
        String stmt="";
        String types="NIL";
        long tempDifference = 0,tempBefore=0,tempAfter=0;
        System.out.println("Listed Issuer is "+listedIssuer);
        if(shareHolder.isEmpty()){
            if(shares.size()>=2){
                System.out.println(shares.get(0)+" "+shares.get(1));
                try {
                    tempBefore= Long.parseLong(((String) shares.get(0)).trim());
                    tempAfter = Long.parseLong(((String) shares.get(1)).trim());
                }
                catch (Exception e) {
                    System.out.println(e);
                    tempBefore=0;
                    tempAfter=0;
                }

            }
            tempDifference = tempBefore-tempAfter;
            if(tempDifference==0){
                types="N/A";
            }else if (tempDifference<0){
                types="Buy";
            }else{
                types="Sell";
            }
            try{
                System.out.println("NewsFeed " +" "+rsCount+"N/A"+tempBefore+tempAfter+Math.abs(tempDifference)+types+pdfLink+date+listedIssuer+sharesAcqOrDes+amountAcqOrDes);
                stmt = String.format(stmtTransactionFormatter, rsCount,"N/A",tempBefore ,tempAfter,Math.abs(tempDifference),types,pdfLink,date,listedIssuer,sharesAcqOrDes,amountAcqOrDes);
                System.out.println(stmt);
                SqlInsert.start(stmt);
            }catch (Exception e){
                System.out.println(e);
            }
        }else{
            for(int i=0;i<shareHolder.size();i++) {
                System.out.println("ShareHolder is "+shareHolder.get(i));
                if(shares.size()>=(i*2)&&shares.size()>=((i*2)+1)){
                    try{
                        System.out.println(shares.get(i*2)+" | "+shares.get((i*2)+1));
                        tempBefore= Long.parseLong(((String) shares.get(i*2)).trim());
                        tempAfter = Long.parseLong(((String) shares.get((i*2)+1)).trim());
                        tempDifference = tempBefore-tempAfter;
                    } catch (Exception e) {
                        System.out.println(e);
                        tempBefore=0;
                        tempAfter=0;
                    }
                }
                types="";
                if(tempDifference==0){
                    types="N/A";
                }else if (tempDifference<0){
                    types="buy";
                }else{
                    types="sell";
                }
                try{
                                    System.out.println("NewsFeed " +" "+rsCount+"N/A"+tempBefore+tempAfter+Math.abs(tempDifference)+types+pdfLink+date+listedIssuer+sharesAcqOrDes+amountAcqOrDes);
                    stmt = String.format(stmtTransactionFormatter, rsCount,shareHolder.get(i),tempBefore,tempAfter,Math.abs(tempDifference),types,pdfLink,date,listedIssuer,sharesAcqOrDes,amountAcqOrDes);
                    System.out.println(stmt);
                    SqlInsert.start(stmt);
                }catch (Exception e){
                    System.out.println(e);
                }
            }
        }
        fileReader.close();
        sqlQuery.close();
    }
    public static void convertIntoText (int tempCount) throws IOException {
        System.out.println("Converting into text....");
        PDDocument document = PDDocument.load(new File("C:\\Users\\Owner\\Documents\\NetBeansProjects\\IceStone\\pdfdownload\\downloaded"+tempCount+".pdf"));
        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        try (PrintWriter writer = new PrintWriter("C:\\Users\\Owner\\Documents\\NetBeansProjects\\IceStone\\pdfdownload\\downloaded"+tempCount+".txt", "UTF-8")) {
            String val =pdfTextStripper.getText(document);
            writer.println(val);
            writer.close();
            document.close();
        }

    }
    public static void createPDF(String secondLink) throws IOException {
        System.out.println("Creating PDF from "+secondLink);
        URL url = new URL(secondLink);
       
        InputStream in = url.openConnection().getInputStream();
        FileOutputStream out = new FileOutputStream(new File("C:\\Users\\Owner\\Documents\\NetBeansProjects\\IceStone\\pdfdownload\\downloaded"+count+".pdf"));
        System.out.println("reading from resource and writing to file...");
        int length = -1;
        byte[] buffer = new byte[10240];// buffer for portion of data from connection
        int bytesBuffered=0;
        while ((length = in.read(buffer)) > -1) {
            out.write(buffer, 0, length);
            bytesBuffered+=length;
            if (bytesBuffered > 1024 * 1024) {
                out.flush();
            }
        }
        count++;
        out.close();
        in.close();

    }

    @Test
    public static ArrayList getSecondLinks(ChromeDriver driver) throws ClassNotFoundException, SQLException{
        System.out.println("GetSecondlinks");
        ArrayList secondLinks = new ArrayList();
        WebElement [] webElements = driver.findElements(By.tagName("a")).toArray(new WebElement[25]);
        ArrayList links[] =getFirstLinks(webElements);
        if(links[0]==null){
            return null;
        }
        //The below function is for share buy back
        for(int i=0;i<links[1].size();i++){
            String link = (String) links[1].get(i);
            WebDriver temp = new ChromeDriver();
            temp.get(link);
            shareBuyBackFilter((ChromeDriver) temp);
            temp.close();
        }
        if(links[1]==null){
            return null;
        }
        //The below function is for disclosure of interest
        for(int i=0;i<links[0].size();i++){
            String link = (String) links[0].get(i);
            WebDriver temp = new ChromeDriver();
            temp.get(link);
            WebElement []  pdfElements = temp.findElements(By.tagName("a")).toArray(new WebElement[25]);
            for(int x=0;x<pdfElements.length;x++){
                if(x==1){ 
                    System.out.println("Link text "+pdfElements[1].getText());
                    secondLinks.add(getSecondLink(pdfElements[1]));
                }
            }
            temp.close();
        }
        return secondLinks;
    }

    public static String getSecondLink (WebElement link){
        return link.getAttribute("href");

    }
    public static ArrayList [] getFirstLinks(WebElement[] webElements){
        System.out.println("GetFirstLinks");
        ArrayList [] links = new ArrayList[2];
        links[0] = new ArrayList();
        links[1] = new ArrayList();
        for(WebElement webElement:webElements){
            if(webElement.getText().contains("Disclosure of Interest")) {
                System.out.println(webElement.getText());
                links[0].add(reconstructLink(webElement.getAttribute("href")));
            }else if(webElement.getText().contains("Share Buy Back")){
                System.out.println(webElement.getText());
                links[1].add(reconstructLink(webElement.getAttribute("href")));
            }
        }
        return links;
    }//
    public static String reconstructLink(String hrefLink){
        String initStmt="http://infopub.sgx.com/Apps?A=COW_CorpAnnouncement_Content&B=AnnouncementLast3Months&F=";
        String queryStmt="";
        boolean isFirst=false;
        for(int i=0;i<hrefLink.length();i++){
            if(hrefLink.charAt(i)==','&&!"".equals(queryStmt)){
                break;
            }
            if(hrefLink.charAt(i)=='"'){
                isFirst=!isFirst;
            }else if(isFirst){
                queryStmt+=hrefLink.charAt(i);
            }
        }
        initStmt+=queryStmt;
        System.out.println("ReconstructLink "+initStmt);
        return initStmt;
    }
    //
    public static void shareBuyBackFilter(ChromeDriver driver) throws ClassNotFoundException, SQLException{
        String stmtFormat = "insert into sharesbuyback (issuer,textone,texttwo) values ('%s','%s','%s')";
        String stmtSend="";
        WebElement [] tdElements = driver.findElementsByTagName("td").toArray(new WebElement[25]);
        int shareBuyBackCount=0;
        int type =0;
        String issuer="",textOne="",textTwo="";
        boolean isIssuer =false;
        boolean isDateOfPurchase =false;
        for(WebElement elements : tdElements){
           
           String text = elements.getText();
           
           if(text.contains("Issuer/ Manager")||isIssuer){
               if(isIssuer){
                   issuer=text;
                   System.out.println("Issuer: "+text);
                   isIssuer =false;
               }else{
                   isIssuer=true;
               }
           }else if (text.contains("Total Number of shares purchased ")){
                textOne=text.substring(37);
               
           }else if (text.contains("Highest Price per share SGD ")){
               textTwo=text;
           }else if  (text.contains ("Total Consideration (including stamp duties, clearing changes etc) paid or payable for the shares SGD ")){
               textTwo=text;
           }
        }
        stmtSend = String.format(stmtFormat,issuer,textOne,textTwo);
        SqlInsert.start(stmtSend);
    }
    
}
