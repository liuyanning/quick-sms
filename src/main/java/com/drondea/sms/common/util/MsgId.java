package com.drondea.sms.common.util;

import com.drondea.sms.common.SequenceNumber;
import com.drondea.sms.type.GlobalConstants;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Calendar;

public class MsgId implements Serializable {
    private static final long serialVersionUID = 945466149547731811L;
    private static int ProcessID = 1010;
    private int month;
    private int day;
    private int hour;
    private int minutes;
    private int seconds;
    private int gateId;
    private int sequenceId;

    static{
        final String propertiesName = "quicksms.id";
        String value = null;
        //解决在docker中运行，进程号都一样的问题
        try {
            if (System.getSecurityManager() == null) {
                value = System.getProperty(propertiesName);
            } else {
                value = AccessController.doPrivileged(new PrivilegedAction<String>() {
                    @Override
                    public String run() {
                        return System.getProperty(propertiesName);
                    }
                });
            }
        } catch (SecurityException e) {
        }
        //没有配置gateid就取程序进程号
        if(StringUtils.isBlank(value)) {
            String vmName = ManagementFactory.getRuntimeMXBean().getName();
            if(vmName.contains("@")){
                value =vmName.split("@")[0];
            }
        }

        try{
            ProcessID = Integer.valueOf(value);
        }catch(Exception e){

        }

    }

    public MsgId() {
        this(SystemClock.now());
    }

    public MsgId(SequenceNumber sequenceNumber) {
        this(SystemClock.now(), ProcessID, sequenceNumber.next());
    }
    /**
     *
     * @param gateId
     */
    public MsgId(int gateId) {
        this(SystemClock.now(), gateId, GlobalConstants.sequenceNumber.next());
    }
    /**
     *
     * @param timeMillis
     */
    public MsgId(long timeMillis) {
        this(timeMillis, ProcessID, GlobalConstants.sequenceNumber.next());
    }
    /**
     *
     * @param msgIds
     */
    public MsgId(String msgIds) {
        setMonth(Integer.parseInt(msgIds.substring(0, 2)));
        setDay(Integer.parseInt(msgIds.substring(2, 4)));
        setHour(Integer.parseInt(msgIds.substring(4, 6)));
        setMinutes(Integer.parseInt(msgIds.substring(6, 8)));
        setSeconds(Integer.parseInt(msgIds.substring(8, 10)));
        setGateId(Integer.parseInt(msgIds.substring(10, 17)));
        setSequenceId(Integer.parseInt(msgIds.substring(17, 22)));
    }
    /**
     *
     * @param timeMillis
     * @param gateId
     * @param sequenceId
     */
    public MsgId(long timeMillis, int gateId, int sequenceId) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeMillis);
        setMonth(cal.get(Calendar.MONTH) + 1);
        setDay(cal.get(Calendar.DAY_OF_MONTH));
        setHour(cal.get(Calendar.HOUR_OF_DAY));
        setMinutes(cal.get(Calendar.MINUTE));
        setSeconds(cal.get(Calendar.SECOND));
        //扩大范围+毫秒
        setGateId(gateId + cal.get(Calendar.MILLISECOND));
        setSequenceId(sequenceId);
    }

    public MsgId(int month, int day, int hour, int minutes, int seconds, int gateId, int sequenceId) {
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minutes = minutes;
        this.seconds = seconds;
        this.gateId = gateId;
        this.sequenceId = sequenceId;
    }

    /**
     * @return the month
     */
    public int getMonth() {
        return month;
    }
    /**
     * @param month the month to set
     */
    public void setMonth(int month) {
        this.month = month;
    }
    /**
     * @return the day
     */
    public int getDay() {
        return day;
    }
    /**
     * @param day the day to set
     */
    public void setDay(int day) {
        this.day = day;
    }
    /**
     * @return the hour
     */
    public int getHour() {
        return hour;
    }
    /**
     * @param hour the hour to set
     */
    public void setHour(int hour) {
        this.hour = hour;
    }
    /**
     * @return the minutes
     */
    public int getMinutes() {
        return minutes;
    }
    /**
     * @param minutes the minutes to set
     */
    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }
    /**
     * @return the seconds
     */
    public int getSeconds() {
        return seconds;
    }
    /**
     * @param seconds the seconds to set
     */
    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
    /**
     * @return the gateId
     */
    public int getGateId() {
        return gateId;
    }
    /**
     * @param gateId the gateId to set
     */
    public void setGateId(int gateId) {
        this.gateId = gateId;
    }
    /**
     * @return the sequenceId
     */
    public int getSequenceId() {
        return sequenceId & 0xffff;
    }
    /**
     * @param sequenceId the sequenceId to set
     */
    public void setSequenceId(int sequenceId) {
        this.sequenceId = sequenceId & 0xffff;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.leftPad(String.valueOf(month), 2,'0'))
                .append(StringUtils.leftPad(String.valueOf(day), 2,'0'))
                .append(StringUtils.leftPad(String.valueOf(hour), 2,'0'))
                .append(StringUtils.leftPad(String.valueOf(minutes), 2,'0'))
                .append(StringUtils.leftPad(String.valueOf(seconds), 2,'0'))
                .append(StringUtils.leftPad(String.valueOf(gateId), 7,'0'))
                .append(StringUtils.leftPad(String.valueOf(sequenceId), 5,'0'));
        return sb.toString();
    }

    public String toHexString(boolean toLowerCase) {
        return Hex.encodeHexString(DefaultMsgIdUtil.msgId2Bytes(this), toLowerCase);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + day;
        result = prime * result + gateId;
        result = prime * result + hour;
        result = prime * result + minutes;
        result = prime * result + month;
        result = prime * result + seconds;
        result = prime * result + sequenceId;
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MsgId other = (MsgId) obj;
        if (day != other.day) {
            return false;
        }
        if (gateId != other.gateId) {
            return false;
        }
        if (hour != other.hour) {
            return false;
        }
        if (minutes != other.minutes) {
            return false;
        }
        if (month != other.month) {
            return false;
        }
        if (seconds != other.seconds) {
            return false;
        }
        if (sequenceId != other.sequenceId) {
            return false;
        }
        return true;
    }

}
