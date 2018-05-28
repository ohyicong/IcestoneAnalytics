/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.template;

/**
 *
 * @author Owner
 */
public class StocksRights {
    private double brokerComm,accessFee,clearingFee,gst,lastPrice,fundingFee,borrowingFee,fundingBorrowed,sharesBorrowed,subscriptionPrice;
    private int internalCodesOne, internalCodesTwo,period;
    public StocksRights(int internalCodesOne,int internalCodesTwo,double brokerComm,double accessFee,double clearingFee,double gst,double lastPrice,int period,double fundingFee,double borrowingFee,double fundingBorrowed,double sharesBorrowed,double subscriptionPrice){
        this.subscriptionPrice=subscriptionPrice;
        this.brokerComm = brokerComm;
        this.accessFee=accessFee;
        this.clearingFee= clearingFee;
        this.gst = gst;
        this.lastPrice=lastPrice;
        this.period = period;
        this.fundingFee= fundingFee;
        this.borrowingFee = borrowingFee;
        this.fundingBorrowed = fundingBorrowed;
        this.sharesBorrowed = sharesBorrowed;
        this.internalCodesOne = internalCodesOne;
        this.internalCodesTwo = internalCodesTwo;
    }

    public double getSubscriptionPrice() {
        return subscriptionPrice;
    }

    public double getBrokerComm() {
        return brokerComm;
    }

    public double getAccessFee() {
        return accessFee;
    }

    public double getClearingFee() {
        return clearingFee;
    }

    public double getGst() {
        return gst;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public int getPeriod() {
        return period;
    }

    public double getFundingFee() {
        return fundingFee;
    }

    public double getBorrowingFee() {
        return borrowingFee;
    }

    public double getFundingBorrowed() {
        return fundingBorrowed;
    }

    public double getSharesBorrowed() {
        return sharesBorrowed;
    }

    public int getInternalCodesOne() {
        return internalCodesOne;
    }

    public int getInternalCodesTwo() {
        return internalCodesTwo;
    }
    
}
