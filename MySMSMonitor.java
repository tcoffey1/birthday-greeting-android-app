//   Author:   Tadhg Coffey
//   Date  :   5/19/14
//   Homework assignment : Quiz 2
//   File: MySMSMonitor.java from Send Birthday Greeting
//   Objective : Read (2) text files: One with a birthday greeting 
//             and the second containing the user's contacts with 
//             birthday dates. Contacts are placed in a table.When 
//             current date matches a date in the contacts table, 
//             the user will be alerted and can choose to send a 
//             birthday greeting to the contact.
//****************************************************************


package com.sendbirthdaygreeting.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

public class MySMSMonitor extends BroadcastReceiver
{
    private static final String ACTION = "android:provider" +
        ".Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context con, Intent in)
    {
        if( in!=null && in.getAction()!=null && ACTION
            .compareToIgnoreCase(in.getAction())==0 )
        {
            Object[] pduArray = (Object[])in.getExtras().get
                ("pdus");

            SmsMessage[] mes = new SmsMessage[pduArray.length];

            for(int i=0 ; i<pduArray.length ; i++)
            {
                mes[i] = SmsMessage.createFromPdu((byte[])
                    pduArray[i]);
                Log.d("MySMSMonitor", "from: " + mes[i]
                    .getOriginatingAddress());
                Log.d("MySMSMonitor", "msg: " + mes[i]
                    .getMessageBody());
            }
        }


    }
}
