/**
 *
 */
package com.drondea.sms.common.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

/**
 * @author liyuehai
 *
 */
public class SgipSequenceNumber implements Serializable {
    private static final long serialVersionUID = -1834423004053157092L;
    private static final String[] datePattern = new String[]{"yyyyMMddHHmmss"};
    private long nodeId;
    private int timestamp;
    private int sequenceId;

    public SgipSequenceNumber(long nodeId, int sequenceId) {
        this(SystemClock.now(), nodeId, sequenceId);
    }

    public SgipSequenceNumber(String sgipSequenceNumber) {
        this.nodeId = Long.parseLong(sgipSequenceNumber.substring(0, 10));
        this.timestamp = Integer.parseInt(sgipSequenceNumber.substring(10, 20));
        this.sequenceId = Integer.parseInt(sgipSequenceNumber.substring(20, 31));
    }
    /**
     *
     * @param timeMillis
     * @param nodeId
     * @param sequenceId
     */
    public SgipSequenceNumber(long timeMillis, long nodeId, int sequenceId) {
        setNodeId(nodeId);
        setSequenceId(sequenceId);
        setTimestamp(timeMillis);
    }

    /**
     *
     */
    public SgipSequenceNumber( long nodeId, int timestamp, int sequenceId) {
        this.nodeId = nodeId;
        this.timestamp = timestamp;
        this.sequenceId = sequenceId;
    }
    /**
     * @return the nodeId
     */
    public long getNodeId() {
        return nodeId;
    }

    /**
     * @param nodeId the nodeId to set
     */
    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * @return the sequenceId
     */
    public int getSequenceId() {
        return sequenceId;
    }

    /**
     * @param sequenceId the sequenceId to set
     */
    public void setSequenceId(int sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getTimeString(long timeMillis) {
        return DateFormatUtils.format(timeMillis, "MMddHHmmss");
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timeMillis) {
        String timeString = getTimeString(timeMillis);
        this.timestamp = Integer.parseInt(timeString);
    }

    public long toLong() {
        return ((long) sequenceId & 0xFFFFFFFFL) | (((long) timestamp << 32) & 0xFFFFFFFF00000000L);
    }

    public static SgipSequenceNumber wrapSequenceNumber(long nodeId, int timestamp, int sequenceId) {
        SgipSequenceNumber sn = new SgipSequenceNumber(nodeId, timestamp, sequenceId);
        return sn;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.leftPad(String.valueOf(nodeId), 10,'0'))
                .append(StringUtils.leftPad(String.valueOf(timestamp), 10,'0'))
                .append(StringUtils.leftPad(String.valueOf(sequenceId), 11,'0'));
        return sb.toString();
    }

}
