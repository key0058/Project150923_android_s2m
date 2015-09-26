package com.me.chen.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.me.chen.s2m.MainActivity;

import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by ChEN on 15/9/23.
 */
public class MailTool {

    public static String MAIL_SMTP_HOST = "mail.smtp.host";
    public static String MAIL_SMTP_PORT = "mail.smtp.port";
    public static String MAIL_SMTP_AUTH = "mail.smtp.auth";
    public static String MAIL_SMTP_USER = "mail.smtp.user";
    public static String MAIL_SMTP_PASSWORD = "mail.smtp.password";

    public static String MAIL_TO_USER = "mail.to.user";


    public static void sendTextEmail(Context context, String subject, String content) throws Exception {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        Properties prop = new Properties();
        prop.put(MAIL_SMTP_HOST, pref.getString(MAIL_SMTP_HOST, ""));
        prop.put(MAIL_SMTP_PORT, pref.getString(MAIL_SMTP_PORT, ""));
        prop.put(MAIL_SMTP_AUTH, pref.getBoolean(MAIL_SMTP_AUTH, true) == true ? "true" : "false");
        prop.put(MAIL_SMTP_USER, pref.getString(MAIL_SMTP_USER, ""));
        prop.put(MAIL_SMTP_PASSWORD, pref.getString(MAIL_SMTP_PASSWORD, ""));

        Session mailSession = Session.getDefaultInstance(prop, new MyAuthenticator(prop));

        Message mailMessage = new MimeMessage(mailSession);
        Address fromAddress = new InternetAddress(pref.getString(MAIL_SMTP_USER, ""));

        String[] receivers = pref.getString(MAIL_TO_USER, "").split(",");
        Address[] toAddress = new InternetAddress[receivers.length];

        for (int i = 0; i < receivers.length; i++) {
            toAddress[i] = new InternetAddress(receivers[i]);
        }

        if (toAddress.length > 0) {
            mailMessage.setFrom(fromAddress);
            mailMessage.setRecipients(Message.RecipientType.TO, toAddress);
            mailMessage.setSubject(subject);
            mailMessage.setSentDate(new Date());
            mailMessage.setText(content);

            Transport.send(mailMessage);
        } else {
            throw new Exception("No receiver address.");
        }



    }

    private static class MyAuthenticator extends Authenticator {

        private Properties prop;

        public MyAuthenticator(Properties prop) {
            this.prop = prop;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(prop.getProperty(MAIL_SMTP_USER), prop.getProperty(MAIL_SMTP_PASSWORD));
        }
    }


}
