package com.drondea.sms.channel;


import com.drondea.sms.type.ConcurrentCommandCounter;

/**
 * Interface defining the counters that can be optionally tracked for an SMPP session.
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
public interface IChannelSessionCounters {

    void setMetricsCounter(String metricName);

    void reset();

    ConcurrentCommandCounter getRxConnectSM();

    ConcurrentCommandCounter getRxDeliverSM();

    ConcurrentCommandCounter getRxReportSM();

    ConcurrentCommandCounter getRxActiveTestSM();

    ConcurrentCommandCounter getRxSubmitSM();

    ConcurrentCommandCounter getTxConnectSM();

    ConcurrentCommandCounter getTxDeliverSM();

    ConcurrentCommandCounter getTxReportSM();

    ConcurrentCommandCounter getTxActiveTestSM();

    ConcurrentCommandCounter getTxSubmitSM();

}
