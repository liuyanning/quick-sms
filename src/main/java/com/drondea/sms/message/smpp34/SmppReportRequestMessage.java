package com.drondea.sms.message.smpp34;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class SmppReportRequestMessage {
    private static final Logger logger = LoggerFactory.getLogger(SmppReportRequestMessage.class);

    private String id;
    private String sub;
    private String dlvrd;
    private String submit_date;
    private String done_date;
    private String stat;
    private String err;
    private String text;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getDlvrd() {
        return dlvrd;
    }

    public void setDlvrd(String dlvrd) {
        this.dlvrd = dlvrd;
    }

    public String getSubmit_date() {
        return submit_date;
    }

    public void setSubmit_date(String submit_date) {
        this.submit_date = submit_date;
    }

    public String getDone_date() {
        return done_date;
    }

    public void setDone_date(String done_date) {
        this.done_date = done_date;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public byte[] getMessageByte() {
        StringBuffer sb = new StringBuffer();
        sb.append("id:").append(id);
        sb.append(" sub:").append(sub);
        sb.append(" dlvrd:").append(dlvrd);
        sb.append(" submit date:").append(submit_date);
        sb.append(" done date:").append(done_date);
        sb.append(" stat:").append(stat);
        sb.append(" err:").append(err);
        sb.append(" text:").append(text);
        try {
            return sb.toString().getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {

        }
        return null;
    }

    public void createReportMessage(byte[] value) {

        try {
            String txt = new String(value, "ISO-8859-1");
            txt = txt.replace("submit date", "submit_date");
            txt = txt.replace("done date", "done_date");
            // id:1649893403 sub:001 dlvrd:001 submit date:1911291216 done date:1911291408
            // stat:DELIVRD err:000 text:???????????? ????100
            // id:48f492ac-6b51-4b0f-b51d-5988e6a166ed submit date:1911292158 done
            // date:1911292158 stat:DELIVRD err:002
            String[] c = txt.split(" ");
            Arrays.asList(c).forEach(item -> {
                String[] infos = item.split(":");
                if (infos.length < 2) {
                    return;
                }
                if (infos[0].equalsIgnoreCase("id")) {
                    this.id = infos[1];
                } else if (infos[0].equalsIgnoreCase("sub")) {
                    this.sub = infos[1];
                } else if (infos[0].equalsIgnoreCase("dlvrd")) {
                    this.dlvrd = infos[1];
                } else if (infos[0].equalsIgnoreCase("submit_date")) {
                    this.submit_date = infos[1];
                } else if (infos[0].equalsIgnoreCase("done_date")) {
                    this.done_date = infos[1];
                } else if (infos[0].equalsIgnoreCase("stat")) {
                    this.stat = infos[1];
                } else if (infos[0].equalsIgnoreCase("err")) {
                    this.err = infos[1];
                } else if (infos[0].equalsIgnoreCase("text")) {
                    this.text = infos[1];
                }
            });
        } catch (Exception e) {
            logger.error("smpp report message:", e);
        }
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{id=").append(id).append(",").append("sub=").append(sub).append(",").append("dlvrd=").append(dlvrd).append(",")
                .append("subTime=").append(submit_date).append(",").append("doneTime=").append(done_date).append(",").append("stat=").append(stat).append(",")
                .append("err=").append(err).append(",").append("text=").append(text).append("}");
        return buffer.toString();
    }
}
