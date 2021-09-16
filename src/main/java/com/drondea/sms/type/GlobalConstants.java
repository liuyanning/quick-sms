package com.drondea.sms.type;


import com.drondea.sms.common.CommonSequenceNumber;
import com.drondea.sms.common.SmgpSequenceNumber;
import com.drondea.sms.handler.ServerMetricsMeterHandler;
import com.drondea.sms.handler.TailHandler;

/**
 * @version V3.0.0
 * @description: 全局参数
 * @author: 刘彦宁
 * @date: 2020年06月05日10:32
 **/
public class GlobalConstants {

    public static long DEFAULT_CONNECT_TIMEOUT = 10000;
    public static final String BYTE_LOG_PREFIX = "BYTE_LOG_";
    public static final String DEFAULT_CHARSET = "UTF-8";
    public final static String EMPTY_STRING = "";
    public final static byte[] EMPTY_BYTE = new byte[0];
    public final static String[] EMPTY_STRING_ARRAY = new String[0];

    public final static CommonSequenceNumber sequenceNumber = new CommonSequenceNumber();
    public final static SmgpSequenceNumber smgpSequenceNumber = new SmgpSequenceNumber();

    /**
     * 默认批次号生成器
     */
    public static IBatchNumberCreator batchNumberCreator = new DefaultBatchNumberCreator();

    public static IDBStore dbStore = new DefaultDBStore();

    public static int DEFAULT_WINDOW_MONITOR_INTERVAL = -1;
    public static int DEFAULT_REQUEST_EXPIRY_TIMEOUT = 10;

    public static Boolean METRICS_ON = true;
    public static Boolean METRICS_CONSOLE_ON = false;

    public static final String MUTL_MOBILE_SPLIT = "\\||,|，|\r\n|\n";

    public final static ServerMetricsMeterHandler SERVER_METRICS_METER_HANDLER = new ServerMetricsMeterHandler();
    public final static TailHandler TAIL_HANDLER = new TailHandler();

    public static IBatchNumberCreator getBatchNumberCreator() {
        return batchNumberCreator;
    }

    public static void setBatchNumberCreator(IBatchNumberCreator batchNumberCreator) {
        GlobalConstants.batchNumberCreator = batchNumberCreator;
    }
}
