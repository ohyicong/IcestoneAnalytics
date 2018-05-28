/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myfeedos;

import com.feedos.api.core.PDU;
import com.feedos.api.core.PolymorphicInstrumentCode;
import com.feedos.api.requests.Constants;
import com.feedos.api.requests.InstrumentQuotationData;
import com.feedos.api.requests.OrderBook;
import com.feedos.api.requests.QuotationTradeCancelCorrection;
import com.feedos.api.requests.QuotationTradeEventExt;
import com.feedos.api.requests.Receiver_Quotation_ChgSubscribeInstrumentsL1;
import com.feedos.api.requests.Receiver_Quotation_SubscribeInstrumentsL1;
import com.mycore.MyUser;
import com.myfilters.FilterTime;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Owner
 */
public class FeedosMySubscribeInstrumentsReceiverL1 implements 
		Receiver_Quotation_SubscribeInstrumentsL1,
		Receiver_Quotation_ChgSubscribeInstrumentsL1
//![snippet_subL1]
{
	// let's store "instrument status" values, indexed by internal_code
	Map<Integer, InstrumentQuotationData> m_InstrMap = Collections.synchronizedMap(new HashMap<Integer, InstrumentQuotationData>());

	PolymorphicInstrumentCode[] input_instr_codes;
	boolean m_dump_cache = false;
	
	// pass the list of "polymorphic codes" that this receiver is supposed to handle.
	// This will be used to "enrich" instrument codes received.
	public FeedosMySubscribeInstrumentsReceiverL1 (PolymorphicInstrumentCode[] the_instr_codes, boolean dump_cache)
	{
		input_instr_codes = the_instr_codes;
		m_dump_cache = dump_cache;
	}

	//![snippet_subL1_cb_response]
        @Override
	public void quotSubscribeInstrumentsL1Response(
			int subscription_num,
			Object user_context,
			int rc,						
			InstrumentQuotationData[] result)
	{
		if (rc != Constants.RC_OK)
		{
			DumpFunctions.DUMP ("==== Subscription failed, rc="+
					Constants.getErrorCodeName(rc));			
		}
		else
		{
			DumpFunctions.DUMP ("==== Subscription started");
			DumpFunctions.dump (result);
                    try {
                        //This init the starting variables of all the stocks subscribed into local memory
                        //![snippet_subL1_cb_response]
                        FeedosInitVariables.InitStocksLevelOneInfo(result);
                    } catch (ClassNotFoundException | SQLException ex) {
                        Logger.getLogger(FeedosMySubscribeInstrumentsReceiverL1.class.getName()).log(Level.SEVERE, null, ex);
                    }
                        
			// create new entries in the map, one per instrument received
			for (int i=0; i<result.length; ++i) {
				PolymorphicInstrumentCode instr = result[i].getInstrumentCode();
				
				//int internal_instr_code = instr.instrument_code.get_internal_code();
				int internal_instr_code = instr.get_internal_code();
				
				if (0 == internal_instr_code) {
					// this may happen: 
					// 1) an invalid instr code was provided in request 
					// 2) IgnoreInvalidCodes was set to true (hence the request succeeded despite the invalid input)
				} else {

					// enrich the instrument code because:
					// 1) only the "local code" flavour was set in the request
					// 2) only the "internal" flavour is present in the response data
					instr.merge_local_code(input_instr_codes[i].get_local_code_str());					
					input_instr_codes[i].merge_internal_code(instr.get_internal_code());
					
					// create the entry
					m_InstrMap.put(internal_instr_code, result[i]);
				}
			}
		}
	}

	public void declareEndOfSubscription (
			int subscription_num,
			Object user_context,
			int rc)
	{
		quotSubscribeInstrumentsL1UnsubNotif(subscription_num, user_context, rc);
	}
	
	//![snippet_subL1_cb_unsub]
        @Override
	public void quotSubscribeInstrumentsL1UnsubNotif(
			int subscription_num,
			Object user_context,
			int rc)
	{
		DumpFunctions.DUMP ("==== Subscription ended: " +
				input_instr_codes.length);
		
		for(int i = 0; i < input_instr_codes.length; ++i)
		{
			if (m_InstrMap.get(input_instr_codes[i].get_internal_code())
					!= null)
			{
				DumpFunctions.dump(m_InstrMap.get(input_instr_codes[i]
						.get_internal_code()));
                                InstrumentQuotationData[] v = {m_InstrMap.get(input_instr_codes[i].get_internal_code())};
                                //InsertionFunctions.SubL1Init(v);
			}
		}
                
	}
	//![snippet_subL1_cb_unsub]
		
	//![snippet_subL1_cb_tee]
        @Override
	public void quotNotifTradeEventExt (
			int subscription_num,
			Object user_context,	
			int instrument_code, 
			long server_timestamp, 
			long market_timestamp, 
			QuotationTradeEventExt trade_event)
	{
                String type ="NIL";
                String transtype="NIL";
                InstrumentQuotationData [] iqdArray= new InstrumentQuotationData[1];
		InstrumentQuotationData iqd = m_InstrMap.get(instrument_code);
		if (iqd == null)
		{
			iqd = new InstrumentQuotationData(instrument_code, new OrderBook());
			m_InstrMap.put(instrument_code, iqd);
		}
		iqd.update_with_TradeEventExt(
				  server_timestamp
				, market_timestamp
				, trade_event
				);
		
		String market_timestamp_str = PDU.date2ISOstring(market_timestamp);
		String server_timestamp_str = PDU.date2ISOstring(server_timestamp);
		String timeQH=FilterTime.formatTimeForSQL(server_timestamp_str) ;
                String timeSGD =FilterTime.sgd(timeQH);
		//DumpFunctions.DUMP ("==== TEE servertime: " + timeQH + "\t sgdTime" + timeSGD);
		//++DumpFunctions.indent_count;
		if (trade_event.content_mask.isSetChangeBusinessDay())
		{
			DumpFunctions.DUMP ("/!\\ ChangeBusinessDay /!\\");
		}
		
		//
		// Now parse the event and dump the values.
		//
		//![snippet_subL1_cb_tee]
		
		// check best limits
		if (trade_event.content_mask.isSetBidLimit()) {
			DumpFunctions.DUMP ("BEST BID: "+Constants.print_price(trade_event.best_bid_price)+ " x "+ trade_event.best_bid_qty);
                        type="BID ";
		}
		if (trade_event.content_mask.isSetAskLimit()) {
			DumpFunctions.DUMP ("BEST ASK: "+Constants.print_price(trade_event.best_ask_price)+ " x "+ trade_event.best_ask_qty);			
                        type="ASK ";
                }
		
		StringBuilder flag_builder = new StringBuilder();

		if (trade_event.content_mask.isSetOpen()) 
		{
			if ((trade_event.content_mask.isSetSession())
				&& (trade_event.content_mask.isSetOtherValues())) 
			{
				if (trade_event.Value.tagIsPresent(Constants.TAG_TradingSessionId))
				{
					int trading_session_id = trade_event.Value.getTagByNumber(Constants.TAG_TradingSessionId).get_int8();
					flag_builder.append("<new ");
					if (trading_session_id < 0)
					{
						flag_builder.append("extended ");
					}
					flag_builder.append("session#");
					flag_builder.append(trading_session_id);
					flag_builder.append("> ");
				}
			}
			if (trade_event.content_mask.isSetOCHLdaily()) { flag_builder.append("<daily> "); }
			flag_builder.append("OPEN "); 
		}
		if (trade_event.content_mask.isSetClose()) 
		{
			if (trade_event.content_mask.isSetOCHLdaily()) { flag_builder.append("<daily> "); }
			else { flag_builder.append("<end session> ");}
			flag_builder.append("CLOSE "); 
		}
		if (trade_event.content_mask.isSetHigh()) 
		{
			if (trade_event.content_mask.isSetOCHLdaily()) { flag_builder.append("<daily> "); }
			else { flag_builder.append("<session> ");}
			flag_builder.append("HIGH "); 
		}		
		if (trade_event.content_mask.isSetLow()) 
		{
			if (trade_event.content_mask.isSetOCHLdaily()) { flag_builder.append("<daily> "); }
			else { flag_builder.append("<session> ");}
			flag_builder.append("LOW "); 
		}

		String flags = flag_builder.toString();
		if (!flags.isEmpty()) 
		{
			//DumpFunctions.DUMP ("flags: "+flags);	
		}
		
		// check price/trade update
		if (trade_event.content_mask.isSetLastPrice()) 
		{       
			if (trade_event.content_mask.isSetLastTradeQty()) 
			{      
                                
                                System.out.println("Trade price:"+trade_event.price+" bestask:"+MyUser.stockLevelOneInfoHashMap.get(instrument_code).getBestAsk()+" bestBid"+MyUser.stockLevelOneInfoHashMap.get(instrument_code).getBestBid());
                                if(trade_event.price==MyUser.stockLevelOneInfoHashMap.get(instrument_code).getBestAsk()){
                                    // buy
                                    transtype="b";
                                    System.out.println("buy");
                                }else{
                                    // sell
                                    transtype="s";
                                    System.out.println("Sell");                                    
                                }
				if(trade_event.content_mask.isSetOffBookTrade())
				{
					DumpFunctions.DUMP ("OFFTRADE: "+trade_event.price + " x "+ trade_event.last_trade_qty);
                                        transtype="x";
                                        type="OFFTRADE";
				}
				else
				{
					DumpFunctions.DUMP ("TRADE: "+trade_event.price + " x "+ trade_event.last_trade_qty);
                                        type="TRADE";
				}
			} 
			else 
			{
				DumpFunctions.DUMP ("PRICE: "+trade_event.price);
                                transtype="p";
                                type="PRICE";
			}
                        RecordIntoJava.transactionInfoStart(instrument_code, trade_event, transtype, timeQH, timeSGD);
                        
			/*if (trade_event.content_mask.isSetSession() && trade_event.content_mask.isSetContext())
			{
				if (trade_event.Context.tagIsPresent(Constants.TAG_ActualTradingSessionId))
				{
					//DumpFunctions.DUMP ("IMPACTED SESSION: " + trade_event.Context.getTagByNumber(Constants.TAG_ActualTradingSessionId).get_int8());
                                    //type+="IMPACTED ";
				}
			}*/
		}
		
		if( trade_event.getContext().size() > 0)
		{
			//DumpFunctions.DUMP ("Context: "); DumpFunctions.dump(trade_event.getContext());
                        //type+="CONTEXT ";
		}
		
		if( trade_event.getValue().size() > 0)
		{
			//DumpFunctions.DUMP ("Values: "); DumpFunctions.dump(trade_event.getValue());
                        //type+="VALUES ";
		}
		
		if (m_dump_cache&&MyUser.isNewDay)
		{        
                    //DumpFunctions.DUMP ("Cache Updates: "); 
                    //DumpFunctions.dump(iqd.getQuotationValues());
                    //DumpFunctions.dump(m_InstrMap.get(instrument_code).getOrderBook());
                    iqdArray[0]= iqd;
                    FeedosUpdateInitVariables.updateStockLevelOneInformation(iqdArray,timeQH,timeSGD,type);
                    SocketBroadcast.updateOverview(MyUser.stockLevelOneInfoHashMap.get(instrument_code));
                    SocketBroadcast.updateStockTimeLineLevelOne(MyUser.stockLevelOneInfoHashMap.get(instrument_code));
                    RecordIntoJava.levelOneInfoStart(instrument_code);
                    //recording here
                    
		}
		
		--DumpFunctions.indent_count;	
	}

        @Override
	public void quotNotifTradeCancelCorrection(
			int subscription_num,
			Object user_context,	
			int instrument_code, 
			long server_timestamp, 
			QuotationTradeCancelCorrection data)
	{
		InstrumentQuotationData iqd = m_InstrMap.get(instrument_code);
		if (iqd == null)
		{
			iqd = new InstrumentQuotationData(instrument_code, new OrderBook());
			m_InstrMap.put(instrument_code, iqd);
		}
		iqd.update_with_TradeCancelCorrection(
				  server_timestamp
				, data
				);
		
		String server_timestamp_str = PDU.date2ISOstring(server_timestamp);
		DumpFunctions.DUMP("==== TCC " + server_timestamp_str);
		++DumpFunctions.indent_count;

		DumpFunctions.DUMP("Content: " + data.getContentMask().printContent());
		
		DumpFunctions.DUMP("Original Trade: " + data.getOriginalTrade().printContent());
		
		if (data.getTradingSessionId() != 0)
		{
			DumpFunctions.DUMP("Trading Session ID: " + data.getTradingSessionId());
		}
		
		if (data.getContentMask().isCorrection())
		{
			DumpFunctions.DUMP("Corrected Trade: " + data.getCorrectedTrade().printContent());
		}
		
		if (data.getContentMask().hasCorrectedValues() && (data.getCorrectedValues().size() != 0))
		{
			DumpFunctions.DUMP("Corrected Values: ");
			DumpFunctions.dump(data.getCorrectedValues());
		}

		if (m_dump_cache)
		{
			//DumpFunctions.DUMP("Cache: ");
			//DumpFunctions.dump(m_InstrMap.get(instrument_code).getQuotationValues());
		}
		
		--DumpFunctions.indent_count;
	}
	
	//![snippet_subL1_cb_add]
        @Override
	public void quotChgSubscribeInstrumentsAddInstrumentsL1Response(
			Object userContext, int rc, InstrumentQuotationData[] result) {
            //Add Level 1 snapshot data into java 
            //InsertionFunctions.SubL1Init(result);
            
            System.out.println("I'm added level one");
            try {
                //DumpFunctions.dump(result);
                FeedosInitVariables.InitStocksLevelOneInfo(result);
            } catch (ClassNotFoundException | SQLException ex) {
                Logger.getLogger(FeedosMySubscribeInstrumentsReceiverL1.class.getName()).log(Level.SEVERE, null, ex);
            }
            SocketBroadcast.initOverview();
        }
	//![snippet_subL1_cb_add]


	//![snippet_subL1_cb_add_ov]
        @Override
	public void quotChgSubscribeInstrumentsAddOtherValuesToLookForL1Response(
			Object userContext, int rc, InstrumentQuotationData[] result) 
	{
	}
	//![snippet_subL1_cb_add_ov]


	//![snippet_subL1_cb_mask]
        @Override
	public void quotChgSubscribeInstrumentsNewContentMaskL1Response(
			Object userContext, int rc, InstrumentQuotationData[] result)
	{
	}
	//![snippet_subL1_cb_mask]


	//![snippet_subL1_cb_remove]
        @Override
	public void quotChgSubscribeInstrumentsRemoveInstrumentsL1Response(
			Object userContext, int rc) 
	{
            System.out.println("I'm removed "+rc);
         
	}
	//![snippet_subL1_cb_remove]
	
}
