package com.drondea.sms.session.cmpp;


import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.drondea.sms.channel.IChannelSessionCounters;
import com.drondea.sms.message.cmpp.AbstractCmppMessage;
import com.drondea.sms.type.ConcurrentCommandCounter;
import com.drondea.sms.type.GlobalConstants;
import com.drondea.sms.type.Metrics;

/**
 * 发送量统计用
 */
public class CmppSessionCounters implements IChannelSessionCounters {

    private String metricName;

    private ConcurrentCommandCounter txSubmitSM;
    private ConcurrentCommandCounter txDeliverSM;
    private ConcurrentCommandCounter txActiveTestSM;
    private ConcurrentCommandCounter txConnectSM;
    private ConcurrentCommandCounter rxSubmitSM;
    private ConcurrentCommandCounter rxDeliverSM;
    private ConcurrentCommandCounter rxActiveTestSM;
    private ConcurrentCommandCounter rxConnectSM;

    private Counter txSubmitSMCounter;
    private Counter rxSubmitSMCounter;
    private Counter txDeliverCounter;
    private Counter rxDeliverCounter;

    public CmppSessionCounters() {

        this.txSubmitSM = new ConcurrentCommandCounter();
        this.txDeliverSM = new ConcurrentCommandCounter();
        this.txActiveTestSM = new ConcurrentCommandCounter();
        this.txConnectSM = new ConcurrentCommandCounter();
        this.rxSubmitSM = new ConcurrentCommandCounter();
        this.rxDeliverSM = new ConcurrentCommandCounter();
        this.rxActiveTestSM = new ConcurrentCommandCounter();
        this.rxConnectSM = new ConcurrentCommandCounter();
    }


    @Override
    public void setMetricsCounter(String metricName) {
        this.metricName = metricName;
        if (GlobalConstants.METRICS_ON) {
            MetricRegistry registry = Metrics.getInstance().getRegistry();
            txSubmitSMCounter = registry.counter(metricName + ":submitRequest");
            rxSubmitSMCounter = registry.counter(metricName + ":submitResponse");
            txDeliverCounter = registry.counter(metricName + ":deliverRequest");
            rxDeliverCounter = registry.counter(metricName + ":deliverResponse");
        }
    }

    @Override
    public void reset() {
        if (GlobalConstants.METRICS_ON) {
            Metrics.remove(metricName + ":submitRequest");
            Metrics.remove(metricName + ":submitResponse");
            Metrics.remove(metricName + ":deliverRequest");
            Metrics.remove(metricName + ":deliverResponse");
        }
        this.txSubmitSM.reset();
        this.txDeliverSM.reset();
        this.txActiveTestSM.reset();
        this.txConnectSM.reset();
        this.rxSubmitSM.reset();
        this.rxDeliverSM.reset();
        this.rxActiveTestSM.reset();
        this.rxConnectSM.reset();
    }

    public void countTXMessage(AbstractCmppMessage message) {
        if (message.getHeader().isRequest()) {
            switch (message.getPackageType().getCommandId()) {
                case 0x00000004:
                    if (txSubmitSMCounter != null) {
                        txSubmitSMCounter.inc();
                    }
                    getTxSubmitSM().incrementRequestAndGet();
                    break;
                case 0x00000005:
                    if (txDeliverCounter != null) {
                        txDeliverCounter.inc();
                    }
                    getTxDeliverSM().incrementRequestAndGet();
                    break;
                case 0x00000008:
                    getTxActiveTestSM().incrementRequestAndGet();
                    break;
                case 0x00000001:
                    getTxConnectSM().incrementRequestAndGet();
                    break;
                default:
                    break;
            }
        }
    }

    public void countRXMessage(AbstractCmppMessage message) {
        if (message.getHeader().isResponse()) {
            switch (message.getPackageType().getCommandId()) {
                case 0x80000004:
                    if (rxSubmitSMCounter != null) {
                        rxSubmitSMCounter.inc();
                    }
                    getRxSubmitSM().incrementRequestAndGet();
                    break;
                case 0x80000005:
                    if (rxDeliverCounter != null) {
                        rxDeliverCounter.inc();
                    }
                    getRxDeliverSM().incrementRequestAndGet();
                    break;
                case 0x80000008:
                    getRxActiveTestSM().incrementRequestAndGet();
                    break;
                case 0x80000001:
                    getRxConnectSM().incrementRequestAndGet();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public ConcurrentCommandCounter getRxConnectSM() {
        return rxConnectSM;
    }

    @Override
    public ConcurrentCommandCounter getRxDeliverSM() {
        return rxDeliverSM;
    }

    @Override
    public ConcurrentCommandCounter getRxReportSM() {
        return null;
    }

    @Override
    public ConcurrentCommandCounter getRxActiveTestSM() {
        return rxActiveTestSM;
    }

    @Override
    public ConcurrentCommandCounter getRxSubmitSM() {
        return rxSubmitSM;
    }

    @Override
    public ConcurrentCommandCounter getTxConnectSM() {
        return txConnectSM;
    }

    @Override
    public ConcurrentCommandCounter getTxDeliverSM() {
        return txDeliverSM;
    }

    @Override
    public ConcurrentCommandCounter getTxReportSM() {
        return null;
    }

    @Override
    public ConcurrentCommandCounter getTxActiveTestSM() {
        return txActiveTestSM;
    }

    @Override
    public ConcurrentCommandCounter getTxSubmitSM() {
        return txSubmitSM;
    }
}
