package com.me.chen.tool;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.me.chen.bean.SmsObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by ChEN on 15/9/24.
 */
public class SmsTool {

    public final static String SMS_FILTER_NUMBER = "sms.filter.number";

    private final static String SMS_LIST_RECORD_FILE = "s2m";

    private final static String SMS_STRUCTURE_ID = "_id";
    private final static String SMS_STRUCTURE_ADDRESS = "address";
    private final static String SMS_STRUCTURE_BODY = "body";
    private final static String SMS_STRUCTURE_PERSON = "person";
    private final static String SMS_STRUCTURE_DATE = "date";
    private final static String SMS_STRUCTURE_SERVICE_CENTER = "service_center";


    public final static Uri SMS_INBOX = Uri.parse("content://sms/");

    public static SmsObject getRecentSms(ContentResolver cr, Context context) {

        String[] projection = new String[] { SMS_STRUCTURE_ID, SMS_STRUCTURE_ADDRESS,
                SMS_STRUCTURE_BODY, SMS_STRUCTURE_PERSON, SMS_STRUCTURE_DATE, SMS_STRUCTURE_SERVICE_CENTER };

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String filterNumber = pref.getString(SmsTool.SMS_FILTER_NUMBER, "###");
        String where = "";

        String[] numbers = filterNumber.split(",");
        for (int i = 0; i < numbers.length; i++) {
            String num = numbers[i];

            if (null == num || num.length() == 0)
                continue;

            if (i == 0) {
                where += " address LIKE '%" + num + "%' ";
            } else {
                where += " OR address LIKE '%" + num + "%' ";
            }
        }

        if (where.length() > 0) {
            where += " AND date > " + (System.currentTimeMillis() - 10 * 60 * 1000);
        } else {
            where += " date > " + (System.currentTimeMillis() - 10 * 60 * 1000);
        }



        Cursor cur = cr.query(SMS_INBOX, projection, where, null, "date desc");

        if (null != cur && cur.moveToNext()) {
            SmsObject obj = createSmsObj(cur);
            cur.close();
            return obj;
        }

        return null;
    }

    public static SmsObject getSmsObjById(ContentResolver cr, String idx) {
        String[] projection = new String[] { SMS_STRUCTURE_ID, SMS_STRUCTURE_ADDRESS,
                SMS_STRUCTURE_BODY, SMS_STRUCTURE_PERSON, SMS_STRUCTURE_DATE, SMS_STRUCTURE_SERVICE_CENTER };
        String where = " _id = '" + idx + "'";
        Cursor cur = cr.query(SMS_INBOX, projection, where, null, null);

        if (null != cur && cur.moveToNext()) {
            SmsObject obj = createSmsObj(cur);
            cur.close();
            return obj;
        }

        return null;
    }

    public static SmsObject createSmsObj(Cursor cur) {
        SmsObject obj = new SmsObject();
        obj.setIdx(cur.getString(cur.getColumnIndex(SMS_STRUCTURE_ID)));
        obj.setSmsNumber(cur.getString(cur.getColumnIndex(SMS_STRUCTURE_ADDRESS)));
        obj.setSmsBody(cur.getString(cur.getColumnIndex(SMS_STRUCTURE_BODY)));
        obj.setSmsServiceCenter(cur.getString(cur.getColumnIndex(SMS_STRUCTURE_SERVICE_CENTER)));

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        obj.setSmsDate(fmt.format(new Date(Long.valueOf(cur.getString(cur.getColumnIndex(SMS_STRUCTURE_DATE))))));
        obj.setDate(new Date(Long.valueOf(cur.getString(cur.getColumnIndex(SMS_STRUCTURE_DATE)))));
        return obj;
    }

    public static void saveSmsRecord(Context context, SmsObject obj) {
        if (obj != null) {
            SharedPreferences.Editor editor = context.getSharedPreferences(SMS_LIST_RECORD_FILE, Activity.MODE_PRIVATE).edit();
            editor.putString(obj.getIdx(), obj.getStatus());
            editor.commit();
        }
    }

    public static Map<String, String> searchSmsRecords(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SMS_LIST_RECORD_FILE, Activity.MODE_PRIVATE);
        return (Map<String, String>) preferences.getAll();
    }

    public static void deleteSmsRecord(Context context, String idx) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SMS_LIST_RECORD_FILE, Activity.MODE_PRIVATE).edit();
        editor.remove(idx);
        editor.commit();
    }

    public static void clearSmsRecord(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SMS_LIST_RECORD_FILE, Activity.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }




}
