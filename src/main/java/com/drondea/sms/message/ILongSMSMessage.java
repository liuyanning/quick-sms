package com.drondea.sms.message;

import com.drondea.sms.message.slice.LongMessageSlice;
import com.drondea.sms.thirdparty.SmsMessage;

import java.util.List;

public interface ILongSMSMessage<T> {

    /**
     * 生成一个片段对象
     *
     * @return
     */
    LongMessageSlice generateSlice();

    /**
     * 将一个片段封装成一个Message
     *
     * @param frame
     * @param sequenceId
     * @return
     * @throws Exception
     */
    T generateMessage(LongMessageSlice frame, int sequenceId) throws Exception;

    /**
     * 获取短信字符串内容对象
     *
     * @return
     */
    SmsMessage getSmsMessage();

    /**
     * 是否是回执
     *
     * @return
     */
    boolean isReport();

    /**
     * 短信是否是长短信
     *
     * @return
     */
    boolean isLongMsg();

    /**
     * 长短信是否已经组合完成
     *
     * @return
     */
    boolean isMsgComplete();

    /**
     * 获取信息相关的所有长短信片段
     *
     * @return
     */
    List<T> getFragments();

    /**
     * 保存长短信片段
     *
     * @param fragment
     */
    void addFragment(T fragment);

    /**
     * 设置短信的文本内容
     *
     * @param smsMsg
     */
    void setSmsMsg(SmsMessage smsMsg);

    /**
     * 获取消息的字符串内容
     *
     * @return
     */
    String getMsgContent();

    /**
     * 获取序列号
     *
     * @return
     */
    int getSequenceNum();

    /**
     * 获取signature
     *
     * @return
     */
    String getMsgSignature();

    /**
     * 长短信设置批量号
     *
     * @param batchNumber
     */
    void setBatchNumber(String batchNumber);

    /**
     * 获取长短信批量号
     *
     * @return
     */
    String getBatchNumber();

    /**
     * 给没有total字段的增加这个字段
     *
     * @param pkTotal
     */
    void setPkTotal(short pkTotal);

    short getPkTotal();

    /**
     * 给没有pkNumber字段的增加这个字段
     *
     * @param pkNumber
     */
    void setPkNumber(short pkNumber);

    short getPkNumber();

    /**
     * 是否是固签消息（提交需要去签名）
     * @return
     */
    boolean isFixedSignature();
}
