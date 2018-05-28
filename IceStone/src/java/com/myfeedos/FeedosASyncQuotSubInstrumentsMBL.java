/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myfeedos;

import com.feedos.api.core.FeedOSException;
import com.feedos.api.core.PDU;
import com.feedos.api.core.PolymorphicInstrumentCode;
import com.feedos.api.requests.Constants;
import com.feedos.api.requests.MBLDeltaRefresh;
import com.feedos.api.requests.MBLLayer;
import com.feedos.api.requests.MBLMaxVisibleDepth;
import com.feedos.api.requests.MBLOverlapRefresh;
import com.feedos.api.requests.MBLSnapshot;
import com.feedos.api.requests.OrderBookSide;
import com.feedos.api.requests.Receiver_Quotation_SubscribeInstrumentsMBL;
import com.google.gson.JsonArray;
import com.mycore.MyUser;
import com.mycore.StockGeneralInformation;
import com.mycore.StockLayerInformation;
import com.myfilters.FilterTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Owner
 */
enum OutputMode {
    Events,
    Cache,
    EventsAndCache
};

//![snippet_sub_MBL_subclass]
class MySubscribeMBLReceiver implements
        Receiver_Quotation_SubscribeInstrumentsMBL //![snippet_sub_MBL_subclass]
{

    private Map<Integer, MBLSnapshot> m_SnapshotsMap = Collections.synchronizedMap(new HashMap<Integer, MBLSnapshot>());

    private OutputMode m_outputMode = OutputMode.Events;
    private boolean m_doMergeLayers = false;
    private int m_targetLayerId = -1;

    public MySubscribeMBLReceiver(OutputMode outputMode,
            boolean do_merge_layers,
            int target_layer_id) {
        m_outputMode = outputMode;
        m_doMergeLayers = do_merge_layers;
        m_targetLayerId = target_layer_id;
    }

    private MBLSnapshot getOrCreateSnapshot(int internal_code) {
        if (0 != internal_code) {
            if (m_SnapshotsMap.containsKey(internal_code)) {
                return m_SnapshotsMap.get(internal_code);
            } else {
                MBLSnapshot blank_snapshot = new MBLSnapshot(internal_code, null);
                m_SnapshotsMap.put(internal_code, blank_snapshot);
                return blank_snapshot;
            }
        }
        return null;
    }

    private void updateAndDumpMergedLayer(int internal_code) {
        MBLSnapshot snapshot = m_SnapshotsMap.get(internal_code);

        // Recreate merged layer
        snapshot.removeLayer(m_targetLayerId);
        snapshot.mergeAllLayers(m_targetLayerId,
                MBLSnapshot.DONT_USE_LATEST_SERVER_TIMESTAMP,
                MBLSnapshot.DONT_USE_LATEST_MARKET_TIMESTAMP,
                MBLSnapshot.MERGE_OTHERS_VALUES,
                MBLSnapshot.PRESERVE_MERGED_LAYERS);

        // Print newly merged layer
        MBLLayer merged_layer = snapshot.getOrCreateLayerWithId(m_targetLayerId);
        DumpFunctions.dump(merged_layer);
    }

    //![snippet_sub_MBL_started]
    @Override
    public void quotSubscribeInstrumentsMBLResponse(
            int subscription_num,
            Object user_context,
            int rc,
            PolymorphicInstrumentCode[] InternalCodes) {
        if (rc != Constants.RC_OK) {
            DumpFunctions.DUMP("==== Subscription to MBL failed, rc="
                    + Constants.getErrorCodeName(rc));
        } else {
            DumpFunctions.DUMP("==== Subscription to MBL started");
        }
    }
    //![snippet_sub_MBL_started]

    //![snippet_sub_MBL_unsub]
    @Override
    public void quotSubscribeInstrumentsMBLUnsubNotif(
            int subscription_num,
            Object user_context,
            int rc) {
        DumpFunctions.DUMP("==== Subscription to MBL aborted, rc="
                + Constants.getErrorCodeName(rc));
    }
    //![snippet_sub_MBL_unsub]

    //![snippet_sub_MBL_fullref]
    @Override
    public void quotNotifMBLFullRefresh(
            int subscription_num,
            Object user_context,
            MBLSnapshot[] snapshots) {
        System.out.println("FullRefresh ");
        // Full refresh means clear
        //m_SnapshotsMap.clear();		
        for (MBLSnapshot snapshot : snapshots) {
            if (null != snapshot) {
                if (0 == snapshot.getCode()) {
                    // this may happen:
                    // 1) an invalid instr code was provided in request
                    // 2) IgnoreInvalidCodes was set to true (hence the request
                    //    succeeded despite the invalid input)
                } else {

                    // Process event
                    m_SnapshotsMap.put(snapshot.getCode(), snapshot);

                    // Show cache
                    if (m_outputMode != OutputMode.Events) {
                        if (m_doMergeLayers) {
                            updateAndDumpMergedLayer(snapshot.getCode());
                        }
                    }
                }

                try {
                    MyUser.stockLevelTwoInfoHashMap.put(snapshot.getCode(), FeedosASyncQuotSubInstrumentsMBL.processMBL(snapshot.getCode(), snapshot.getLayerList().get(0)));
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(MySubscribeMBLReceiver.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    //![snippet_sub_MBL_fullref]

    //![snippet_sub_MBL_overlap]
    @Override
    public void quotNotifMBLOverlapRefresh(
            int subscription_num,
            Object user_context,
            MBLOverlapRefresh overlap) {
        System.out.println("OverLapRefresh");
        MBLSnapshot snapshot = getOrCreateSnapshot(overlap.getCode());
        if (null != snapshot) {
            // Process event
            MBLLayer updatedLayer
                    = snapshot.update_with_MBLOverlapRefresh(overlap);

            // Show cache
            if (m_outputMode != OutputMode.Events) {
                //System.out.println("Overlap");
                if (!m_doMergeLayers) {
                    //DumpFunctions.dump("OB " + DumpFunctions.internalCodeToStr(
                    //		snapshot.getCode()),updatedLayer);
                } else {
                    updateAndDumpMergedLayer(snapshot.getCode());
                }
            }
        }
    }
    //![snippet_sub_MBL_overlap]

    //Yicong: This code runs delta refresh provides small update on the given layer.. it is not a full refresh
    //![snippet_sub_MBL_delta]
    public void quotNotifMBLDeltaRefresh(
            int subscription_num,
            Object user_context,
            MBLDeltaRefresh delta) {

        //This function calls the level 2 snapshot of a particular stock
        MBLSnapshot snapshot = getOrCreateSnapshot(delta.getCode());
        if (null != snapshot) {

            // Process event
            MBLLayer updatedLayer = snapshot.update_with_MBLDeltaRefresh(delta);

            // Show cache
            if (m_outputMode != OutputMode.Events) {
                //This is the place it will display everything 
                if (!m_doMergeLayers) {

                    //System.out.println("Deltarefresh: Full refresh as shown below");
                    DumpFunctions.dump("OB " + DumpFunctions.internalCodeToStr(snapshot.getCode()), updatedLayer);
                } else {
                    updateAndDumpMergedLayer(snapshot.getCode());
                }
            }
            if(MyUser.isNewDay){
                String timeQH = FilterTime.formatTimeForSQL(PDU.date2ISOstring(delta.getTimestamps().getServer()));
                String timeSGD = FilterTime.sgd(timeQH);
                try {
                    ArrayList stockLayerInfos = FeedosASyncQuotSubInstrumentsMBL.processMBL(snapshot.getCode(), updatedLayer);
                    MyUser.stockLevelTwoInfoHashMap.put(delta.getCode(), stockLayerInfos);
                    SocketBroadcast.updateStockTimeLineLevelTwo(stockLayerInfos, timeSGD);
                    RecordIntoJava.levelTwoInfoStart(delta.getCode(), stockLayerInfos, timeQH, timeSGD);
                    MyUser.timesgd = timeSGD;

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(MySubscribeMBLReceiver.class.getName()).log(Level.SEVERE, null, ex);

                }   
            }
        }
    }
    //![snippet_sub_MBL_delta]

    //![snippet_sub_MBL_mvd]
    @Override
    public void quotNotifMBLMaxVisibleDepth(
            int subscription_num,
            Object user_context,
            MBLMaxVisibleDepth depth) {
        MBLSnapshot snapshot = getOrCreateSnapshot(depth.getCode());
        if (null != snapshot) {
            // Process event
            MBLLayer updatedLayer = snapshot.update_with_MBLMaxVisibleDepth(depth);

            // Show cache
            if (m_outputMode != OutputMode.Events) {
                System.out.println("maxVisibleDepth");
                if (!m_doMergeLayers) {
                    DumpFunctions.dump("OB " + DumpFunctions.internalCodeToStr(
                            snapshot.getCode()), updatedLayer);
                } else {
                    updateAndDumpMergedLayer(snapshot.getCode());
                }
            }
        }
    }
    //![snippet_sub_MBL_mvd]

    public void quotChgSubscribeInstrumentsAddInstrumentsMBLResponse(
            Object user_context,
            int rc,
            PolymorphicInstrumentCode[] InternalCodes
    ) {
        if (Constants.RC_OK != rc) {
            System.err.println("error in syncQuotChgSubscribeInstrumentsMBL_addInstruments: "
                    + Constants.getErrorCodeName(rc));
        }
    }

    public void quotChgSubscribeInstrumentsRemoveInstrumentsMBLResponse(
            Object user_context,
            int rc
    ) {
        if (Constants.RC_OK != rc) {
            System.err.println("error in syncQuotChgSubscribeInstrumentsMBL_removeInstruments: "
                    + Constants.getErrorCodeName(rc));
        }
    }

}

public class FeedosASyncQuotSubInstrumentsMBL {

    private static void sleep(int sec) {
        try {
            Thread.sleep(sec * 10);
        } catch (InterruptedException iEx) {
        }
    }

    public static void start() {
        String codeArgument = ""; // aka internalCode
        boolean isFirst = true;
        for (int internalCode : MyUser.stockGeneralInfoHashMap.keySet()) {
            if (isFirst) {
                codeArgument += internalCode + "";
                isFirst = false;
            } else {
                codeArgument += "," + internalCode;
            }
        }
        OutputMode outputMode = OutputMode.Events;//Calling both refresh functions
        boolean do_merge_layers = false;
        int target_layer_id = -1;
        int[] layer_ids = null; // No layer id means we want all layers
        // Extract optional arguments
        ArrayList<PolymorphicInstrumentCode> instruments = FeedosInstrumentCodesParserHelper.parse(codeArgument);
        if (null == instruments) {
            System.out.println("FeedosASyncQuotSubInstrumentsL1: No instrument code");
            return;
        }
        PolymorphicInstrumentCode[] instr_codes = instruments.toArray(new PolymorphicInstrumentCode[instruments.size()]);
        MyUser.subMBLNumber = MyUser.asyncRequester.asyncQuotSubscribeInstrumentsMBL_start(
                new MySubscribeMBLReceiver(
                        outputMode, do_merge_layers, target_layer_id),
                new String("user context to distinguish requests"),
                instr_codes,
                layer_ids);

    }

    public static void add(StockGeneralInformation stockGenInfo) {
        int internalCode = stockGenInfo.getInternalCode();
        System.out.print("FeedosASyncQuotSubInstrumentsMBL: Adding instrument: " + internalCode);
        ArrayList<PolymorphicInstrumentCode> instruments = FeedosInstrumentCodesParserHelper.parse(internalCode + "");
        if (null == instruments) {
            System.err.println("FeedosASyncQuotSubInstrumentsMBL: No instrument code");
            return;
        }
        PolymorphicInstrumentCode[] instr_codes = instruments.toArray(new PolymorphicInstrumentCode[instruments.size()]);
        try {
            MyUser.syncRequester.syncQuotChgSubscribeInstrumentsMBL_addInstruments(MyUser.subMBLNumber, instr_codes);
            // wait a bit to let the response/notifications arrive (none should arrive at this time)
            System.err.println("sleeping 0.01 seconds ... Trade events may occur again");
            System.err.flush();
            sleep(1);

        } catch (FeedOSException tfEx) {
            System.err.println("error in syncQuotChgSubscribeInstrumentsL2_addInstruments: " + tfEx);
        }
    }

    public static void drop(StockGeneralInformation stockGenInfo) {
        int internalCode = stockGenInfo.getInternalCode();
        //Remove instrumetion from map
        ArrayList<PolymorphicInstrumentCode> instruments = FeedosInstrumentCodesParserHelper.parse(internalCode + "");
        if (null == instruments) {

            System.err.println("FeedosASyncQuotSubInstrumentsMBL: No instrument code");
            return;
        }
        PolymorphicInstrumentCode[] instr_codes = instruments.toArray(new PolymorphicInstrumentCode[instruments.size()]);
        try {
            MyUser.syncRequester.syncQuotChgSubscribeInstrumentsMBL_removeInstruments(MyUser.subMBLNumber, instr_codes);
            // wait a bit to let the response/notifications arrive (none should arrive at this time)
            System.err.println("sleeping 0.01 seconds ... no event should occur");
            System.err.flush();
            sleep(1);
        } catch (FeedOSException tfEx) {
            System.err.println("FeedosASyncQuotSubInstrumentsMBL: Instrument drop function failed to deploy " + tfEx);
        }
    }

    public static void stop() {
        MyUser.asyncRequester.asyncQuotSubscribeInstrumentsMBL_stop(MyUser.subMBLNumber);
        System.out.println("MBL subscription services stopped");
    }

    public static ArrayList processMBL(int internalCode, MBLLayer mblLayer) throws ClassNotFoundException {
        int maxVisibleDepth = mblLayer.getMaxVisibleDepth();
        OrderBookSide bidSide = mblLayer.getBidLimits();
        OrderBookSide askSide = mblLayer.getAskLimits();
        ArrayList stockLayerInfos = new ArrayList();
        //StockLayerInformation[] stockLayerInfos = new StockLayerInformation [maxVisibleDepth];
        JsonArray jsonArray = new JsonArray();
        int bidSize = (null != bidSide) ? bidSide.getDepth() : 0;
        int askSize = (null != askSide) ? askSide.getDepth() : 0;
        if (null != bidSide || null != askSide) {
            double bidPrice = 0.0;
            double bidQty = 0.0;
            int bidNbOrders = 0;
            double askPrice = 0.0;
            double askQty = 0.0;
            int askNbOrders = 0;
            if (bidSize == 0 && askSize == 0) {
                System.out.println("processMBLInit: bidSize and askSize is null return it else data will be wiped");
                return null;
            }
            if (maxVisibleDepth < 0) {
                maxVisibleDepth = Math.max(bidSize, askSize);
            }
            for (int i = 0; (i <= maxVisibleDepth) && ((i < bidSize) || (i < askSize)); i++) {
                if (i < bidSize) {
                    bidPrice = bidSide.getPrice(i);
                    bidQty = bidSide.getQty(i);
                    bidNbOrders = bidSide.getNbOrders(i);
                } else {
                    bidPrice = 0.;
                    bidQty = 0.;
                    bidNbOrders = 0;
                }
                if (i < askSize) {
                    askPrice = askSide.getPrice(i);
                    askQty = askSide.getQty(i);
                    askNbOrders = askSide.getNbOrders(i);
                } else {
                    askPrice = 0.;
                    askQty = 0.;
                    askNbOrders = 0;
                }
                stockLayerInfos.add(new StockLayerInformation(internalCode, i, bidPrice, bidQty, askPrice, askQty, bidNbOrders, askNbOrders));
            }

        }
        return stockLayerInfos;
    }
}
