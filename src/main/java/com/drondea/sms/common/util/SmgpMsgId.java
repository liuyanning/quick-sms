/**
 * 
 */
package com.drondea.sms.common.util;


import com.drondea.sms.common.SequenceNumber;
import com.drondea.sms.type.GlobalConstants;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Calendar;

/**
 *
 *
 */
public class SmgpMsgId implements Serializable {
	private static final long serialVersionUID = 945466149547731811L;
	private static int ProcessID = 1010;
	private int month;
	private int day;
	private int hour;
	private int minutes;
	private int gateId;
	private int sequenceId;
	private byte[] originarr;

	static{
		final String propertiesName = "quicksms.smgpid";
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

	public SmgpMsgId() {
		this(SystemClock.now());
	}

	public SmgpMsgId(SequenceNumber sequenceNumber) {
		int next = sequenceNumber.next();
		int sequenceId = next % 1000000;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(SystemClock.now());
		setMonth(cal.get(Calendar.MONTH) + 1);
		setDay(cal.get(Calendar.DAY_OF_MONTH));
		setHour(cal.get(Calendar.HOUR_OF_DAY));
		setMinutes(cal.get(Calendar.MINUTE));
		setGateId(ProcessID);
		setSequenceId(sequenceId);
	}
	/**
	 *
	 * @param gateId
	 */
	public SmgpMsgId(int gateId) {
		this(SystemClock.now(), gateId, GlobalConstants.smgpSequenceNumber.next());
	}
	/**
	 *
	 * @param timeMillis
	 */
	public SmgpMsgId(long timeMillis) {
		this(timeMillis, ProcessID, GlobalConstants.smgpSequenceNumber.next());
	}

	public SmgpMsgId(byte[] arr) {
		originarr = new byte[10];
		System.arraycopy(arr, 0, originarr, 0, 10);
	}

	public SmgpMsgId(String msgIds) {
		setGateId(Integer.parseInt(msgIds.substring(0, 6)));
		setMonth(Integer.parseInt(msgIds.substring(6, 8)));
		setDay(Integer.parseInt(msgIds.substring(8, 10)));
		setHour(Integer.parseInt(msgIds.substring(10, 12)));
		setMinutes(Integer.parseInt(msgIds.substring(12, 14)));
		setSequenceId(Integer.parseInt(msgIds.substring(14, 20)));
	}
	/**
	 *
	 * @param timeMillis
	 * @param gateId
	 * @param sequenceId
	 */
	public SmgpMsgId(long timeMillis, int gateId, int sequenceId) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(SystemClock.now());
		setMonth(cal.get(Calendar.MONTH) + 1);
		setDay(cal.get(Calendar.DAY_OF_MONTH));
		setHour(cal.get(Calendar.HOUR_OF_DAY));
		setMinutes(cal.get(Calendar.MINUTE));
		setGateId(gateId);
		setSequenceId(sequenceId % 1000000);
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
		return sequenceId;
	}
	/**
	 * @param sequenceId the sequenceId to set
	 */
	public void setSequenceId(int sequenceId) {
		this.sequenceId = sequenceId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if(originarr!=null && originarr.length>0) {
			return String.valueOf(Hex.encodeHex(originarr));
		}else {
			StringBuilder sb = new StringBuilder();
			sb.append(StringUtils.leftPad(String.valueOf(gateId), 6,'0'))
					.append(StringUtils.leftPad(String.valueOf(month), 2,'0'))
					.append(StringUtils.leftPad(String.valueOf(day), 2,'0'))
					.append(StringUtils.leftPad(String.valueOf(hour), 2,'0'))
					.append(StringUtils.leftPad(String.valueOf(minutes), 2,'0'))
					.append(StringUtils.leftPad(String.valueOf(sequenceId), 6,'0'));
			return sb.toString();
		}

	}

	public String toHexString(boolean toLowerCase) {
		return Hex.encodeHexString(SmgpMsgIdUtil.msgId2Bytes(this), toLowerCase);
	}

	@Override
	public int hashCode() {


		final int prime = 31;
		int result = 1;
		if(originarr!=null && originarr.length>0) {
			return Arrays.hashCode(originarr);
		}
		result = prime * result + day;
		result = prime * result + gateId;
		result = prime * result + hour;
		result = prime * result + minutes;
		result = prime * result + month;
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
		SmgpMsgId other = (SmgpMsgId) obj;
		
		if(originarr!=null && originarr.length>0) {
			return Arrays.equals(originarr, other.originarr);
		}
		
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
		if (sequenceId != other.sequenceId) {
			return false;
		}
		return true;
	}
}
