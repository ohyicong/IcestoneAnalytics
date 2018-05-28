/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myfeedos;

import com.feedos.api.core.Any;
import com.feedos.api.core.FOSUuid;
import com.feedos.api.core.PDU;
import com.feedos.api.core.PolymorphicInstrumentCode;
import com.feedos.api.requests.Constants;
import com.feedos.api.requests.DailyHistoryPointExt;
import com.feedos.api.requests.FeedDescription;
import com.feedos.api.requests.FeedServiceStatus;
import com.feedos.api.requests.FeedStatus;
import com.feedos.api.requests.FeedStatusEvent;
import com.feedos.api.requests.FeedUsability;
import com.feedos.api.requests.InstrumentCharacteristics;
import com.feedos.api.requests.InstrumentData;
import com.feedos.api.requests.InstrumentQuotationData;
import com.feedos.api.requests.IntradayBar;
import com.feedos.api.requests.IntradayHistoryCancel;
import com.feedos.api.requests.IntradayHistoryCorrection;
import com.feedos.api.requests.IntradayHistoryDataExtended;
import com.feedos.api.requests.ListOfTagValue;
import com.feedos.api.requests.MBLLayer;
import com.feedos.api.requests.MarketBranchContent;
import com.feedos.api.requests.MarketBranchId;
import com.feedos.api.requests.MarketCharacteristics;
import com.feedos.api.requests.MarketContent;
import com.feedos.api.requests.MarketSheetEntry;
import com.feedos.api.requests.MarketSheetVector;
import com.feedos.api.requests.NewsData;
import com.feedos.api.requests.OrderBook;
import com.feedos.api.requests.OrderBookSide;
import com.feedos.api.requests.QuotationContentMask;
import com.feedos.api.requests.QuotationTradeEventExt;
import com.feedos.api.requests.TradeImpactIndicator;
import com.feedos.api.requests.UTCTimestamps;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author Owner
 */
public class DumpFunctions
{
	// use this to perform basic indentation
	public static int indent_count=0;
		
	public static void DUMP (String s) {
		for (int i=0; i<indent_count; ++i) {
			System.out.print('\t');
		}
		System.out.println(s);
		System.out.flush();
	}
	
	public static void DUMP_AND_STIMESTAMP (String s) {
		for (int i=0; i<indent_count; ++i) {
			System.out.print('\t');
		}
		SimpleDateFormat format = new SimpleDateFormat("[ HH:mm:ss:SSS ] ");
		System.out.println(format.format(new Date(System.currentTimeMillis())) + s);
		System.out.flush();
	}
	

///////////////////////////////////////// 
///////////////////////////////////////// 
///////////////////////////////////////// DISPLAY FUNCTIONS
///////////////////////////////////////// 
///////////////////////////////////////// 
	
	public final static String internalCodeToStr(int internal_code)
	{
		int marketId = PolymorphicInstrumentCode.get_market_id_from_internal_code(internal_code);
        int localCode = PolymorphicInstrumentCode.get_local_code_from_internal_code(internal_code);
		
        return new String(marketId + "/" + localCode);
	}

	public static void dump (int[] instrument_codes) {
		for (int code_index=0; code_index<instrument_codes.length; ++code_index) {
			DUMP("instrument_code = " + instrument_codes[code_index]);
		}
	}

	
	public static final void dump (		String what,
										int[] tag_nums, 
										Any[] tag_values,
										int length
									)
	{
		DUMP(what);
		++indent_count;
		for (int attr_index=0; attr_index<length; ++attr_index) {
			dump (tag_nums[attr_index], tag_values[attr_index]);
		}
		--indent_count;
	}

	public static final void dump (ListOfTagValue tag_values)
	{
		++indent_count;
		for (int attr_index=0; attr_index<tag_values.size(); ++attr_index) {
			dump(tag_values.getTagNums()[attr_index], tag_values.getTagValues()[attr_index]);
		}
		--indent_count;
	}

	public static final void dump(int tag_num, Any tag_value) {
		if (tag_value == null) {
			DUMP(Constants.getTagName(tag_num) + " = " + tag_value);
		} else if (tag_value.getSyntax() == Any.SYNTAX_UNKNOWN) {
			DUMP(Constants.getTagName(tag_num) + " = ?");
		} else {
			//![snippet_sub_L1_generic_tags]
			if (Constants.isDictionaryTag(tag_num, tag_value)) {
				final Constants.DictionaryEntry entry = Constants.getDictionaryEntry(new Constants.DictionaryEntryKey(tag_num, tag_value.get_uint32()));
				if (entry != null) {
					DUMP(Constants.getTagName(tag_num) + " = " + entry.getShortName() + " (" + entry.getLongName() + ")");
					return;
				}
			}
			//![snippet_sub_L1_generic_tags]
			if (tag_num == Constants.TAG_InternalEntitlementId) {
				final String eidStr = Constants.getEIDString(tag_value.get_int32());
				DUMP(Constants.getTagName(tag_num) + " = " + ((eidStr != null) ? eidStr : tag_value));
			} else if (tag_num == Constants.TAG_TradeImpactIndicator) {
				final TradeImpactIndicator tii = new TradeImpactIndicator(tag_value.get_uint32());
				DUMP(Constants.getTagName(tag_num) + " = " + tii.printContent());
			} else if (tag_num == Constants.TAG_TradingStatus) {
				DUMP(Constants.getTagName(tag_num) + " = " + Constants.getTradingStatusAsString(tag_value.get_enum()));
			} else if (tag_num == Constants.TAG_SecurityStatus) {
				DUMP(Constants.getTagName(tag_num) + " = " + Constants.getSecurityStatusAsString(tag_value.get_uint8()));
			} else if (tag_num == Constants.TAG_OrderEntryStatus) {
				DUMP(Constants.getTagName(tag_num) + " = " + Constants.getOrderEntryStatusAsString(tag_value.get_uint8()));
			} else {
				DUMP(Constants.getTagName(tag_num) + " = " + tag_value);
			}
		}
	}

	//![snippet_sub_L1_generic_tags_entry_id_by_source_id]
	public static final void dump(int tag_id, int source_id, String short_name) {
		DUMP("\trelated_tags=" + Constants.getTagName(tag_id) +
				 "\n\tsource_id=" + source_id);

		if (short_name == null) {
			// Get a list of the entry_id, short_name and long_name of a tag associated to the tag number and the source id provided
			final List<Constants.DictionaryEntryWithId> entries = Constants.getDictionaryEntryWithIdList(tag_id, source_id);

			if (entries != null && !entries.isEmpty()) {
				for (Constants.DictionaryEntryWithId entry : entries) {
					DUMP("\n\t\tvalue=" + entry.getEntryId() +
						 "\n\t\t\tname=" + entry.getDictionaryEntry().getShortName() +
						 "\n\t\t\tdescription=" + entry.getDictionaryEntry().getLongName() +
						 "\n");
				}
			}
		} else {
			// Get the entry_id, short_name and long_name of the tag associated to a short name, tag number and source id provided
			final Constants.DictionaryEntryWithId entry = Constants.getDictionaryEntryWithId(tag_id, source_id, short_name);

			if (entry != null) {
				DUMP("\n\t\tvalue=" + entry.getEntryId() +
					 "\n\t\t\tname=" + entry.getDictionaryEntry().getShortName() +
					 "\n\t\t\tdescription=" + entry.getDictionaryEntry().getLongName());
			};
		}
	}
	//![snippet_sub_L1_generic_tags_entry_id_by_source_id]

	public static final void dump (InstrumentCharacteristics v) {
		DUMP("instrument_code = " + v.getInternal_instrument_code());
		dump ("referential attributes:", v.getRef_values().getTagNums(), v.getRef_values().getTagValues(), v.getRef_values().size());
	}

	public static final void dump (InstrumentData v) {
		DUMP("instrument_code = " + v.getInternal_instrument_code());
		dump ("referential attributes:", v.getRef_values().getTagNums(), v.getRef_values().getTagValues(), v.getRef_values().size());
		dump ("quotation attributes:", v.getQuot_values().getTagNums(), v.getQuot_values().getTagValues(), v.getQuot_values().size());
		dump(v.getOrder_book());
		
	}
	
	public static void dump (PolymorphicInstrumentCode[] instruments) {
		for (int index=0; index<instruments.length; ++index) {
			DUMP (instruments[index].toString());
			System.out.println();
		}
	}

	public static void dump (InstrumentCharacteristics[] instruments) {
		for (int index=0; index<instruments.length; ++index) {
			dump (instruments[index]);
			System.out.println();
		}
	}
	
	public static void dump (InstrumentData[] instruments) {
		for (int index=0; index<instruments.length; ++index) {
			dump (instruments[index]);
			System.out.println();
		}
	}
	
	public static void dump (OrderBookSide side) {
		for (int index=0; index<side.getDepth(); ++index) {
			DUMP (Constants.print_price(side.getPrice(index))+"\tx "+side.getQty(index));
		}
	}

	public static void dumpMbl (OrderBookSide side) {
		if (null != side) {
			for (int index=0; index < side.getDepth(); ++index) {
				DUMP (Constants.print_price(side.getPrice(index))+"\tx "+side.getQty(index) + "\t@ " + Constants.print_nbOrders(side.getNbOrders(index)));
			}
		}
	}
	
	public final static void dumpMblHeader(int internal_code, int layerId, UTCTimestamps timestamps)
	{	
		DumpFunctions.DUMP(internal_code + " L("+layerId+ ") " + PDU.date2ISOstring (timestamps.getMarket())+ " /serverUTCTimestamp: " + PDU.date2ISOstring (timestamps.getServer()));	
	}
	
	
	private final static String s_ALLClearFromLevel_str = new String("ALLClearFromLevel");
	private final static String s_BidClearFromLevel_str = new String("BidClearFromLevel");
	private final static String s_AskClearFromLevel_str = new String("AskClearFromLevel");
	private final static String s_BidInsertAtLevel_str 	= new String("BidInsertAtLevel");
	private final static String s_AskInsertAtLevel_str 	= new String("AskInsertAtLevel");
	private final static String s_BidRemoveLevel_str 	= new String("BidRemoveLevel");
	private final static String s_AskRemoveLevel_str 	= new String("AskRemoveLevel");
	private final static String s_BidChangeQtyAtLevel_str= new String("BidChangeQtyAtLevel");
	private final static String s_AskChangeQtyAtLevel_str= new String("AskChangeQtyAtLevel");	
	private final static String s_BidRemoveLevelAndAppend_str= new String("BidRemoveLevelAndAppend");
	private final static String s_AskRemoveLevelAndAppend_str= new String("AskRemoveLevelAndAppend");	
	
	
	public static void dumpAction(int action)
	{
		switch(action)
		{
		case Constants.OrderBookDeltaAction_ALLClearFromLevel: 	System.out.print(s_ALLClearFromLevel_str); break;
		case Constants.OrderBookDeltaAction_BidClearFromLevel: 	System.out.print(s_BidClearFromLevel_str); break;
		case Constants.OrderBookDeltaAction_AskClearFromLevel: 	System.out.print(s_AskClearFromLevel_str); break;
		case Constants.OrderBookDeltaAction_BidInsertAtLevel:	System.out.print(s_BidInsertAtLevel_str); break;
		case Constants.OrderBookDeltaAction_AskInsertAtLevel:	System.out.print(s_AskInsertAtLevel_str); break;
		case Constants.OrderBookDeltaAction_BidRemoveLevel:		System.out.print(s_BidRemoveLevel_str); break;
		case Constants.OrderBookDeltaAction_AskRemoveLevel:		System.out.print(s_AskRemoveLevel_str);	break;
		case Constants.OrderBookDeltaAction_BidChangeQtyAtLevel:System.out.print(s_BidChangeQtyAtLevel_str); break;
		case Constants.OrderBookDeltaAction_AskChangeQtyAtLevel:System.out.print(s_AskChangeQtyAtLevel_str); break;
		case Constants.OrderBookDeltaAction_BidRemoveLevelAndAppend:System.out.print(s_BidRemoveLevelAndAppend_str); break;
		case Constants.OrderBookDeltaAction_AskRemoveLevelAndAppend:System.out.print(s_AskRemoveLevelAndAppend_str); break;		
		}
	}
	
	private enum BidOrAskSide
	{
		Bid,
		Ask
	}
	
	private final static String s_BID_str = new String("BID ");
	private final static String s_ASK_str = new String("ASK ");
	
	private final static String s_FormatEntry = new String("%9s x %6s @ %6s");
	
	private final static String s_UNCOMPLETE_EMPTY_LINE = new String("                          ");
	private final static String s_COMPLETE_EMPTY_LINE 	= new String("**************************");
	
	public static void dumpMBLentry(BidOrAskSide side, int level, double price, double qty, int nb_orders, boolean complete)
	{
		System.out.print((BidOrAskSide.Bid == side) ? ("\n\t"+level+"\t") : "\t");
		System.out.print((BidOrAskSide.Bid == side) ? s_BID_str: s_ASK_str);
		// means this is a null entry
		if (0 == (int)price && 0 == (int)qty && 0 == nb_orders)
		{
			System.out.print(complete ? s_COMPLETE_EMPTY_LINE : s_UNCOMPLETE_EMPTY_LINE);
		}
		else 
		{
			System.out.format(s_FormatEntry,
					Constants.print_price		(price),
					Constants.print_quantity	(qty),
					Constants.print_nbOrders	(nb_orders));
		}
	}
	
	public static void dumpMBL(int maxVisibleDepth, OrderBookSide bidSide, OrderBookSide askSide)
	{
		int bidSize = (null != bidSide) ? bidSide.getDepth() : 0;
		int askSize = (null != askSide) ? askSide.getDepth() : 0;
		if (null != bidSide || null != askSide)
		{
			double bidPrice = 0.; double bidQty = 0.; int bidNbOrders = 0;
			double askPrice = 0.; double askQty = 0.; int askNbOrders = 0;
			if (maxVisibleDepth < 0)
			{
				maxVisibleDepth = Math.max(bidSize,askSize);
			}	
			for (int i = 0; (i <= maxVisibleDepth) && ((i < bidSize) || (i < askSize)); i++)
			{
				if (i < bidSize)
				{
					bidPrice = bidSide.getPrice(i); bidQty = bidSide.getQty(i); bidNbOrders = bidSide.getNbOrders(i);
				}
				else
				{
					bidPrice = 0.; bidQty = 0.; bidNbOrders = 0;
				}
				dumpMBLentry(BidOrAskSide.Bid,i,bidPrice,bidQty,bidNbOrders,maxVisibleDepth==bidSize);
				if (i < askSize)
				{
					askPrice = askSide.getPrice(i); askQty = askSide.getQty(i); askNbOrders = askSide.getNbOrders(i);
				}
				else
				{
					askPrice = 0.; askQty = 0.; askNbOrders = 0;
				}
				dumpMBLentry(BidOrAskSide.Ask,i,askPrice,askQty,askNbOrders,maxVisibleDepth==askSize);
			}
		}
		System.out.println("");
	}
	
	public static void dump (OrderBook orderbook) 
	{
		DUMP("update_timestamp="+PDU.date2ISOstring (orderbook.getLastUpdateTimestamp()));	
		DUMP("bid side");
			++indent_count;
			dump (orderbook.getBidSide());
			--indent_count;
		DUMP("ask side");
			++indent_count;
			dump (orderbook.getAskSide());
			--indent_count;
	}
	
	public static void dump	(String prefix,MBLLayer layer) 
	{
		if (null == layer) return ;
		System.out.print(prefix + " L("+layer.getLayerId() + ") bid(" + layer.getBidLimits().getDepth() + ") ask(" + layer.getAskLimits().getDepth() + ") TimesUTC(Market=" + PDU.date2ISOstring (layer.getTimestamps().getMarket())+ ",Server=" + PDU.date2ISOstring (layer.getTimestamps().getServer()) + ") MVD=" + layer.getMaxVisibleDepth());	
		dumpMBL(layer.getMaxVisibleDepth(),layer.getBidLimits(),layer.getAskLimits());
		if (null != layer.getOtherValues())
		{
			DUMP("OtherValues: "); dump(layer.getOtherValues());	
		}
	}
	
	public static void dump(MBLLayer layer) 
	{
		dump("",layer);
	}
	
	@Deprecated
	public static void dump (Vector<MarketSheetEntry> entry)
	{
		dump(new ArrayList<MarketSheetEntry>(entry));
	}

	public static void dump(List<MarketSheetEntry> entries)
	{
		for (int index=0; index<entries.size(); ++index) {
			DUMP ( entries.get(index).getId()+"\tx "+
					Constants.print_price(entries.get(index).getPrice())+"\tx "+
					entries.get(index).getQty());
		}
	}
	
	public static void dump (MarketSheetVector marketsheet) 
	{
		DUMP("bid side");
			++indent_count;
			dump(marketsheet.getBuyOrders());
			--indent_count;
		DUMP("ask side");
			++indent_count;
			dump(marketsheet.getSellOrders());
			--indent_count;
	}
	
   	public static final void dump (InstrumentQuotationData v) {
		DUMP("instrument_code = " + v.getInstrumentCode());
		dump (v.getOrderBook());
		
		int[] tag_nums;
		Any[] tag_values;
		synchronized (v) {
			tag_nums = v.getAllTagNumbers_nolock();
			tag_values = v.getAllTagValues_nolock();
		}
		dump ("quotation variables:", tag_nums, tag_values, tag_nums.length );
	}
   	
	public static void dump (InstrumentQuotationData[] instruments) {
		for (int index=0; index<instruments.length; ++index) {
			dump (instruments[index]);
			System.out.println();
		}
	}

	public static final void dump (MarketCharacteristics v) {
		DUMP("fos_market_id = " + v.fos_market_id);
		DUMP("iso_market_id = " + v.iso_market_id);
		DUMP("timezone = " + v.timezone);
		DUMP("iso_country_code = " + v.iso_country_code);
		DUMP("nb_max_instruments = " + v.nb_max_instruments);
	}

	public static void dump (MarketCharacteristics[] markets) {
		for (int index=0; index<markets.length; ++index) {
			dump (markets[index]);
			System.out.println();
		}
	}

	public static final void dump (MarketBranchId v) {
		DUMP("FOSMarketId="+v.fos_market_id+" CFICode="+v.cfi_code+" SecurityTypeCode="+v.security_type);
	}
	
	public static final void dump (MarketContent v) {
		DUMP("fos_market_id = " + v.fos_market_id);
		DUMP("branches:");
		++indent_count;
		for (int index=0; index<v.branches.length; ++index) {
			MarketBranchContent branch = v.branches[index];
			dump (branch.branch_id);
			++indent_count;
			DUMP("nb_instruments="+branch.quantity);
			--indent_count;
		}
		--indent_count;		
	}

	public static void dump (MarketContent[] contents) {
		for (int index=0; index<contents.length; ++index) {
			dump (contents[index]);
			System.out.println();
		}
	}
	
	public static void dump (	int instrument_code,		
								int[] result_session_id,
								long[] result_utc_timestamp,
								double[] result_price,
								double[] result_qty
							)
	{
		DUMP("instrument_code = " + instrument_code);	
		for (int index=0; index<result_utc_timestamp.length; ++index) {
			DUMP(	"timestamp="+PDU.date2ISOstring (result_utc_timestamp[index]) +
					" price=" + Constants.print_price(result_price[index]) + 
					" qty=" + result_qty[index]
				);	
		}
		
	}
	
	public static void dump (
			int instrument_code,		
			double[] result_price,
			double[] result_qty,
			long[] r_ServerUTCTime,
			long[] r_MarketUTCTime,
			QuotationContentMask[]	r_ContentMask,
			int[]		r_TradeConditionIndex
		)
	{
		for (int index=0; index<result_price.length; ++index) 
		{
			DUMP(
			instrument_code + 
			"\t"  +PDU.date2ISOstring (r_ServerUTCTime[index]) +
			"\t" + PDU.date2ISOstring (r_MarketUTCTime[index]) + 
			"\t" + Constants.print_price(result_price[index]) + 
			"\t" + result_qty[index] +
			"\t" + r_ContentMask[index].printContent() +
			"\t" + r_TradeConditionIndex[index]);	
		}
	}
	
	public static void dump (
			int instrument_code,		
			double[] result_price,
			double[] result_qty,
			long[] r_ServerUTCTime,
			long[] r_MarketUTCTime,
			QuotationContentMask[]	r_ContentMask,
			int[]		r_TradeConditionIndex,
			ListOfTagValue[]			r_TradeProperties
		)
	{
		for (int index=0; index<result_price.length; ++index) 
		{
			DUMP(
			instrument_code + 
			"\t"  +PDU.date2ISOstring (r_ServerUTCTime[index]) +
			"\t" + PDU.date2ISOstring (r_MarketUTCTime[index]) + 
			"\t" + Constants.print_price(result_price[index]) + 
			"\t" + result_qty[index] +
			"\t" + r_ContentMask[index].printContent() +
			"\t" + r_TradeConditionIndex[index] +
			"\t");
			DumpFunctions.dump(r_TradeProperties[index]);
		}
	}
	
	public static void dump (
			int instrument_code,		
			double[] result_price,
			double[] result_qty,
			long[] r_ServerUTCTime,
			long[] r_MarketUTCTime,
			QuotationContentMask[]	r_ContentMask,
			ListOfTagValue[]		r_TradeCondition
		)
	{
		for (int index=0; index<result_price.length; ++index) 
		{
			DUMP(
			instrument_code + 
			"\t"  +PDU.date2ISOstring (r_ServerUTCTime[index]) +
			"\t" + PDU.date2ISOstring (r_MarketUTCTime[index]) + 
			"\t" + Constants.print_price(result_price[index]) + 
			"\t" + result_qty[index] +
			"\t" + r_ContentMask[index].printContent() +
			"\t" + ((null != r_TradeCondition[index]) ? (r_TradeCondition[index]).printContent() : ""));	
		}
	}
	
	public static void dump (
			int instrument_code,		
			double[] result_price,
			double[] result_qty,
			long[] r_ServerUTCTime,
			long[] r_MarketUTCTime,
			QuotationContentMask[]	r_ContentMask,
			ListOfTagValue[]		r_TradeCondition,
			ListOfTagValue[]		r_TradeProperties
		)
	{
		for (int index = 0 ; index < result_price.length; ++index) 
		{
			DUMP(
			instrument_code + 
			"\t"  +PDU.date2ISOstring (r_ServerUTCTime[index]) +
			"\t" + PDU.date2ISOstring (r_MarketUTCTime[index]) + 
			"\t" + Constants.print_price(result_price[index]) + 
			"\t" + result_qty[index] +
			"\t" + r_ContentMask[index].printContent() +
			"\t" + ((null != r_TradeCondition[index]) ? (r_TradeCondition[index]).printContent() : "") +
			"\t");
			DumpFunctions.dump(r_TradeProperties[index]);
		}
	}
	
	public static void dump (
								int instrument_code,		
								int[] result_local_date,
								double[] result_open,
								double[] result_high,
								double[] result_low,
								double[] result_close,
								double[] result_daily_volume,
								double[] result_daily_asset
							)	
	{
		DUMP("instrument_code = " + instrument_code);	
		for (int index=0; index<result_local_date.length; ++index) {
			String date_str =	// DateFormat.getDateInstance().format(new Date(86400 * 1000 * result_local_date[index]))
								PDU.day2string (result_local_date[index]);
			DUMP(
					  "date="+date_str
					+ " open=" + Constants.print_price(result_open[index])
					+ " high=" + Constants.print_price(result_high[index])
					+ " low=" + Constants.print_price(result_low[index])
					+ " close=" + Constants.print_price(result_close[index])
					+ " volume=" + Constants.print_price(result_daily_volume[index])
					+ " asset=" + Constants.print_price(result_daily_asset[index])
				);
		}
	
	}
	
	public static void dump(int[] list_of_int, String sep)
	{
		if( list_of_int.length > 0 )
		{
			for (int index=0; index<list_of_int.length; ++index) {
				System.out.print(list_of_int[index] + sep);
 			}
		}
		System.out.print("\n");
	}
	
	public static void dump(FeedUsability the_usability)
	{
		DUMP("FeedUsability:");
		DUMP("\t"+"State="+the_usability.getState());
		DUMP("\t"+"LatencyPenalty="+the_usability.isLatencyPenalty());
		DUMP("\t"+"OutOfDateValues="+the_usability.isOutOfDateValues());
		DUMP("\t"+"BadDataQuality="+the_usability.isBadDataQuality());
	}
	
	public static void dump(FeedServiceStatus the_service_status)
	{
		DUMP("FeedServiceStatus:");
		DUMP("\t"+"ServiceName="+the_service_status.getServiceName());
		DUMP("\t"+"Usability="); ++indent_count; ++indent_count; 
		dump(the_service_status.getUsability()); --indent_count; --indent_count;
	}
	
	public static void dump (FeedStatus the_status)
	{
		DUMP("FeedStatus:");
		
		DUMP("\t"+"FeedDescription="); ++indent_count; ++indent_count; 
		dump(the_status.getFeed()); --indent_count; --indent_count;
		
		DUMP("\t"+"FeedUsability="); ++indent_count; ++indent_count; 
		dump(the_status.getOverallUsability()); --indent_count; --indent_count;
		
		if( the_status.getServices().length > 0 )
		{
			DUMP("\t"+"FeedServiceStatus="); ++indent_count; ++indent_count; 
			for (int index=0; index<the_status.getServices().length; ++index) {
				dump( the_status.getServices()[index] );
			}
			 --indent_count; --indent_count;
		}
		else
		{
			DUMP("\t"+"FeedServiceStatus=NULL");
		}
		
		DUMP("\n");
	
	}
	
	public static void dump (FeedDescription the_description)
	{
		DUMP("FeedDescription:");
		DUMP("\t"+"FeedName="+the_description.getFeedName());
		System.out.print("\t"+"InternalSourceIDs="); dump(the_description.getInternalSourceIDs(), ",");
	}
	
	public static void dump (FeedStatusEvent the_status_event)
	{
		DUMP("FeedStatusEvent:");
		
		DUMP("\t"+"FeedDescription="); ++indent_count; ++indent_count; 
		dump(the_status_event.getFeed()); --indent_count; --indent_count;
		
		DUMP("\t"+"EventType="+the_status_event.getEventType());
		DUMP("\t"+"EventDetails="+the_status_event.getEventDetails());
	}
	
	public static void dump (QuotationTradeEventExt trade_event)
	{
		// check best limits
		if (trade_event.content_mask.isSetBidLimit()) {
			DumpFunctions.DUMP ("BEST BID: "+trade_event.best_bid_price+ " x "+ trade_event.best_bid_qty);			
		}
		if (trade_event.content_mask.isSetAskLimit()) {
			DumpFunctions.DUMP ("BEST ASK: "+trade_event.best_ask_price+ " x "+ trade_event.best_ask_qty);			
		}
		
		String flags = "";
		if (trade_event.content_mask.isSetOCHLdaily()) { flags += "<daily> "; }
		if (trade_event.content_mask.isSetOpen()) { flags += "OPEN "; }
		if (trade_event.content_mask.isSetClose()) { flags += "CLOSE "; }
		if (trade_event.content_mask.isSetHigh()) { flags += "HIGH "; }		
		if (trade_event.content_mask.isSetLow()) { flags += "LOW "; }

		if (flags.length() != 0) {
			DumpFunctions.DUMP ("flags: "+flags);	
		}
		
		// check price/trade update
		if (trade_event.content_mask.isSetLastPrice()) {
			if (trade_event.content_mask.isSetLastTradeQty()) {
				DumpFunctions.DUMP ("TRADE: "+trade_event.price + " x "+ trade_event.last_trade_qty);
			} else {
				DumpFunctions.DUMP ("PRICE: "+trade_event.price);				
			}			
		} else {
			if (flags.length() != 0) {
				DumpFunctions.DUMP ("OCHL value: "+trade_event.price);								
			}
		}
		
		if( trade_event.getContext().size() > 0)
			DumpFunctions.DUMP ("Context: "); DumpFunctions.dump(trade_event.getContext());
		
		if( trade_event.getValue().size() > 0)
			DumpFunctions.DUMP ("Values: "); DumpFunctions.dump(trade_event.getValue());
		
		--DumpFunctions.indent_count;	
	}

	public static final void dump (String prefix, FOSUuid id)
	{
		DUMP(prefix + id.toString());
	}
	
	public static final void dump (NewsData[] news_data)
	{
		for (int index = 0; index < news_data.length; ++index) 
		{
			dump("NE ", news_data[index].getId());
			DUMP(' ' + 
				 Constants.toString_NewsEventType(news_data[index].getNewsEventType()) + 
				 " serv="+PDU.date2ISOstring (news_data[index].getServerUTCTime()));	
			ListOfTagValue news_item = news_data[index].getNewsItems();
			for (int attr_index=0; attr_index < news_item.size(); ++attr_index) 
			{
				if( news_item.getTagValues()[attr_index].getSyntax() != Any.SYNTAX_UNKNOWN )
				{
					int tag_num = news_item.getTagNums()[attr_index];
					Any tag_value = news_item.getTagValues()[attr_index];
					DUMP(Constants.getTagName(tag_num)+" = " + tag_value);
				}
			}
		}
	}
	
	public static final void dump(String prefix, IntradayBar bar)
	{
		String line = prefix;
		line += "\t" + internalCodeToStr(bar.getCode());
		line += "\t" + PDU.date2ISOstring(bar.getMarketTs());
		line += "\t" + PDU.date2ISOstring(bar.getServerTs());
		line += "\t" + Integer.toString(bar.getDuration());
		line += "\t" + Integer.toString(bar.getNbPoints());
		line += "\t" + Constants.print_price(bar.getOpen());
		line += "\t" + Constants.print_price(bar.getHigh());
		line += "\t" + Constants.print_price(bar.getLow());
		line += "\t" + Constants.print_price(bar.getClose());
		line += "\t" + bar.getVolume();
		line += "\t" + bar.getOtherValues().printContent();

		DUMP(line);		
	}
	
	public static void dump (String prefix, IntradayBar[] bars)
	{
		for (IntradayBar bar : bars)
		{
			dump(prefix, bar);
		}
	}

	public static void dump(IntradayHistoryDataExtended[] trades)
	{
		for (IntradayHistoryDataExtended trade : trades)
		{
			dump(trade);
		}
	}

	public static void dump(IntradayHistoryDataExtended trade)
	{
		String line = "IE\t";
		line += internalCodeToStr(trade.getCode());
		line += "\t" + trade.getTradeId();
		line += "\t" + PDU.date2ISOstring(trade.getMarketTs());
		line += "\t" + PDU.date2ISOstring(trade.getServerTs());
		line += "\t" + trade.getContentMask().printContent();
		line += "\t" + Constants.print_price(trade.getPrice());
		line += "\t" + trade.getQty();
		line += "\t" + trade.getTradeConditionIndex();
		line += "\t" + trade.getProperties().printContent();
				
		DUMP(line);
	}

	public static void dump(IntradayHistoryCancel cancel)
	{
		String line = "TCA\t";
		line += internalCodeToStr(cancel.getCode());
		line += "\t" + cancel.getTradeId();
		line += "\t" + PDU.date2ISOstring(cancel.getOriginalMarketTs());
		line += "\t" + cancel.isOffBook();
		line += "\t" + cancel.isFromVenue();
		
		DUMP(line);
	}

	public static void dump(IntradayHistoryCorrection correction)
	{
		String line = "TCO\t";
		line += internalCodeToStr(correction.getCode());
		line += "\t" + correction.getTradeId();
		line += "\t" + PDU.date2ISOstring(correction.getOriginalMarketTs());
		line += "\t" + correction.isOffBook();
		line += "\t" + correction.isFromVenue();
		line += "\t" + correction.getTradingSessionId();
		line += "\t" + correction.getTrade().printContent();
		
		DUMP(line);
	}

	public static void dump(DailyHistoryPointExt p)
	{
		StringBuffer line = new StringBuffer();
		line.append(PDU.day2string(p.getDate())); line.append('\t');
		line.append(p.getTradingSessionId()); line.append('\t');
		line.append(PDU.price2string(p.getOpen())); line.append('\t');
		line.append(PDU.price2string(p.getHigh())); line.append('\t');
		line.append(PDU.price2string(p.getLow())); line.append('\t');
		line.append(PDU.price2string(p.getClose())); line.append('\t');
		line.append(p.getVolume()); line.append('\t');
		line.append(p.getAsset()); line.append('\t');
		line.append(p.getOtherValues().printContent());
		
		DUMP(line.toString());
	}
}

