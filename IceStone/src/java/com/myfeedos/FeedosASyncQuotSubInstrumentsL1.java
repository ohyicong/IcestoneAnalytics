/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myfeedos;

import com.feedos.api.core.FeedOSException;
import com.feedos.api.core.PolymorphicInstrumentCode;
import com.feedos.api.requests.Constants;
import com.feedos.api.requests.QuotationContentMask;
import com.feedos.api.requests.RequestSender;
import com.mycore.MyUser;
import com.mycore.StockGeneralInformation;
import java.util.ArrayList;

/**
 *
 * @author Owner
 */
public class FeedosASyncQuotSubInstrumentsL1  {

        //Passing in a list of code_argument
	public static void start() {
            MyUser.requestedContent = new QuotationContentMask (true);// request all events
            MyUser.asyncRequester = new RequestSender (MyUser.session, 0);
            String code_argument="";
            boolean isFirst=true;
		for(int internalCode:MyUser.stockGeneralInfoHashMap.keySet()){
                    if(isFirst){
                        code_argument+=internalCode+"";
                        isFirst=false;
                    }else
                        code_argument+=","+internalCode;
                }
                System.out.println("Init Subscribing to:"+code_argument);
            
		boolean dump_cache = true;
		//![snippet_sub_l1_custom]
		// Process instruments from command line
		ArrayList<PolymorphicInstrumentCode> instruments = FeedosInstrumentCodesParserHelper.parse(code_argument);
		if (null == instruments){
			System.out.println("FeedosASyncQuotSubInstrumentsL1: No instrument code");
			return ;
		}
		PolymorphicInstrumentCode[] instr_codes = instruments.toArray(new PolymorphicInstrumentCode[instruments.size()]);

		System.err.println("starting subscription, content_mask=EVERYTHING");
		System.err.flush();

		//![snippet_l1_req]
		MyUser.receiverSubL1 = new FeedosMySubscribeInstrumentsReceiverL1(instr_codes, dump_cache);
                
		// Store the returned value, because you will need it to stop the
		// subscription.
                
                //
                int [] quot_format ={
                    Constants.TAG_PreviousDailyClosingPrice,
                    Constants.TAG_InternalPriceActivityTimestamp,
                    Constants.TAG_LastPrice,
                    Constants.TAG_LastTradeQty,
                    Constants.TAG_MARKET_TradingStatus,
                    Constants.TAG_DailyTotalAssetTraded,
                    Constants.TAG_DailyTotalVolumeTraded,
                    Constants.TAG_InternalPriceActivityTimestamp
                };
		MyUser.subL1Number = MyUser.asyncRequester.asyncQuotSubscribeInstrumentsL1_start(
				MyUser.receiverSubL1,
				"my_subscription_context",
				instr_codes,// list of instr code
				quot_format,		// Other variables to look for (none in this case).
				MyUser.requestedContent);
	}
        //This function add on to subscriptions
        public static void add (StockGeneralInformation stockGeneralInformation) throws FeedOSException{
            //MyUser.stockGeneralInfoHashMap.put(stockGeneralInformation.getInternalCode(),stockGeneralInformation);
            ArrayList<PolymorphicInstrumentCode> instruments = FeedosInstrumentCodesParserHelper.parse(stockGeneralInformation.getInternalCode()+"");
            //Structuring ends here
            if(instruments==null){
                System.err.println("FeedosASyncQuotSubInstrumentsL1: No instrument code");
            }else{
                PolymorphicInstrumentCode[] instrCodes = (PolymorphicInstrumentCode[])instruments.toArray(new PolymorphicInstrumentCode[instruments.size()]);
                MyUser.asyncRequester.asyncQuotChgSubscribeInstrumentsL1_addInstruments(MyUser.subL1Number, MyUser.receiverSubL1, "my_subscription_context",instrCodes);
                System.out.println("FeedosASyncQuotSubInstrumentsL1: internalcode added "+stockGeneralInformation.getInternalCode());
            }
        }
        
        //This is to drop existing subscription *not tested*
        public static void drop (StockGeneralInformation stockGeneralInformation) throws FeedOSException, InterruptedException{
          
            ArrayList<PolymorphicInstrumentCode> instruments =
				FeedosInstrumentCodesParserHelper.parse(stockGeneralInformation.getInternalCode()+"");
		if (null == instruments)
		{
                    System.out.println("FeedosASyncQuotSubInstrumentsL1: No instrument code");
                    return ;
		}
		PolymorphicInstrumentCode[] instrCodes = instruments.toArray(
				new PolymorphicInstrumentCode[instruments.size()]);
                
                MyUser.asyncRequester.asyncQuotChgSubscribeInstrumentsL1_removeInstruments(MyUser.subL1Number, MyUser.receiverSubL1, "my_subscription_context",instrCodes);
                System.out.println("FeedosASyncQuotSubInstrumentsL1: internalcode removed "+stockGeneralInformation.getInternalCode());
        }
        public static void stop (){
            MyUser.asyncRequester.asyncQuotSubscribeInstrumentsL1_stop(MyUser.subL1Number);
            MyUser.receiverSubL1.declareEndOfSubscription(MyUser.subL1Number, "my_subscription_context", Constants.RC_OK);
            MyUser.asyncRequester.terminate();
            System.out.println("L1 Subscription services stopped");
        }

 
}