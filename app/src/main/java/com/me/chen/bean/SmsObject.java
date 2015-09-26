package com.me.chen.bean;

import java.util.Date;

/**
 * Created by ChEN on 15/9/24.
 */
public class SmsObject {

    public final static String SMS_STATUS_SENDING = "Sending";
    public final static String SMS_STATUS_SENT = "Sent";
    public final static String SMS_STATUS_EXCEPTION = "Exception";

    private String idx;
    private String smsDate;
    private String smsNumber;
    private String smsBody;
    private String smsServiceCenter;
    private Date date;
    private String status;

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getSmsDate() {
        return smsDate;
    }

    public void setSmsDate(String smsDate) {
        this.smsDate = smsDate;
    }

    public String getSmsNumber() {
        return smsNumber;
    }

    public void setSmsNumber(String smsNumber) {
        this.smsNumber = smsNumber;
    }

    public String getSmsBody() {
        return smsBody;
    }

    public void setSmsBody(String smsBody) {
        this.smsBody = smsBody;
    }

    public String getSmsServiceCenter() {
        return smsServiceCenter;
    }

    public void setSmsServiceCenter(String smsServiceCenter) {
        this.smsServiceCenter = smsServiceCenter;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
