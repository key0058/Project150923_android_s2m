package com.me.chen.s2m;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.me.chen.bean.SmsObject;
import com.me.chen.tool.MailTool;
import com.me.chen.tool.SmsTool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;


public class MainActivity extends Activity {

    private ListView listView;
    private MyAdapter listAdapter;
    private ArrayList<SmsObject> listResult;

    private SmsObserver smsObserver;
    private Handler smsHandler = new Handler() {};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        listAdapter = new MyAdapter(this, getContentResolver());
        listView.setAdapter(listAdapter);

        smsObserver = new SmsObserver(this, smsHandler);
        getContentResolver().registerContentObserver(SmsTool.SMS_INBOX, true, smsObserver);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class SmsObserver extends ContentObserver {

        private Context context;

        public SmsObserver(Context context, Handler handler) {
            super(handler);
            this.context = context;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            SmsObject obj = SmsTool.getRecentSms(getContentResolver(), context);
            if (null != obj) {
                obj.setStatus(SmsObject.SMS_STATUS_SENDING);
                listResult.add(0, obj);
                listAdapter.notifyDataSetChanged();
                new MyTask().execute(context, obj);
            }
        }
    }

    private class MyAdapter extends BaseAdapter {

        public MyAdapter(Context context, ContentResolver contentResolver) {
            listResult = new ArrayList<SmsObject>();
            ArrayList<String> sequence = new ArrayList<String>();
            Map<String, String> records = SmsTool.searchSmsRecords(context);
            for (String idx : records.keySet()) {
                SmsObject obj = SmsTool.getSmsObjById(contentResolver, idx);
                if (obj != null) {
                    obj.setStatus(records.get(idx));
                    listResult.add(obj);
                } else {
                    SmsTool.deleteSmsRecord(context, idx);
                }
            }

            Collections.sort(listResult, new Comparator<SmsObject>() {
                @Override
                public int compare(SmsObject smsObject, SmsObject smsObject2) {
                    try {
                        return smsObject2.getDate().compareTo(smsObject.getDate());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });

        }

        @Override
        public int getCount() {
            return listResult.size();
        }

        @Override
        public Object getItem(int i) {
            return listResult.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            SmsObject obj = listResult.get(position);
            view = getLayoutInflater().inflate(R.layout.layout_s2m_list_item, null);
            TextView title = (TextView) view.findViewById(R.id.s2m_list_item_title);
            TextView subTitle = (TextView) view.findViewById(R.id.s2m_list_item_sub_title);
            TextView statuTitle = (TextView) view.findViewById(R.id.s2m_list_item_status);
            TextView dateTitle = (TextView) view.findViewById(R.id.s2m_list_item_date);

            title.setText(obj.getSmsBody());
            subTitle.setText("From: " + obj.getSmsNumber());
            dateTitle.setText(obj.getSmsDate());

            statuTitle.setText(obj.getStatus());
            statuTitle.setTextColor(getResources().getColor(android.R.color.white));

            if (obj.getStatus().equals(SmsObject.SMS_STATUS_SENDING)) {
                statuTitle.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            } else if (obj.getStatus().equals(SmsObject.SMS_STATUS_SENT)) {
                statuTitle.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            } else {
                statuTitle.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            }

            return view;
        }
    }

    private class MyTask extends AsyncTask {

        private Context context;
        private SmsObject obj;

        @Override
        protected Object doInBackground(Object[] objects) {
            context = (Context) objects[0];
            obj = (SmsObject) objects[1];
            try {
                MailTool.sendTextEmail(context, obj.getSmsNumber(), obj.getSmsBody());
                obj.setStatus(SmsObject.SMS_STATUS_SENDING);
                SmsTool.saveSmsRecord(context, obj);
            } catch (Exception e) {
                e.printStackTrace();
                obj.setStatus(SmsObject.SMS_STATUS_EXCEPTION);
                SmsTool.saveSmsRecord(context, obj);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            listAdapter.notifyDataSetChanged();
        }
    }

}
