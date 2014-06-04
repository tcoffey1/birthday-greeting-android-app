//   Author:   Tadhg Coffey
//   Date  :   5/19/14
//   Homework assignment : Quiz 2
//   File: SendBirthdayGreetingActivity.java 
// 				from Send Birthday Greeting
//   Objective: Read (2) text files: One with a birthday greeting 
//			and the second containing the user's contacts with 
//			birthday dates. Contacts are placed in a table.When 
//			current date matches a date in the contacts table, 
//			the user will be alerted and can choose to send a 
//			birthday greeting to the contact.
//****************************************************************

package com.sendbirthdaygreeting.app;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;



public class SendBirthdayGreetingActivity extends Activity
{
    Button sendSMSButton;
    TextView tvPhone,tvBirthday, tvName, tvEmail, tvSMS;
    String[] contactsArray;
    SQLiteDatabase db;
    Cursor c;
    String messageStringWithName = "";
    Boolean isSomeonesBirthday = false;
    String bdPhoneNum = "";
    String bdEmail = "";
    String bdName = "";
    String bdDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout
            .activity_send_birthday_greeting);
        sendSMSButton = (Button) findViewById(R.id.sendButtonId);
        tvSMS = (TextView) findViewById(R.id
            .tvSMS_Id);
        tvPhone = (TextView) findViewById(R.id
            .tvPhone_Id);
        tvName = (TextView) findViewById(R.id
            .tvName_Id);
        tvEmail = (TextView) findViewById(R.id
            .tvEmail_Id);
        tvBirthday = (TextView) findViewById(R.id
            .tvBirthday_Id);
        contactsArray = new String[20];

        //create db and table
        try
        {
            String sqlcmd = "create table if not exists contacts"
                + "(_id integer primary key autoincrement, "+
                "name text not null, email text not null, "+
                "phone text not null, "+
                "birthday text not null);";
            db = openOrCreateDatabase("birthday_wish",
                SQLiteDatabase.CREATE_IF_NECESSARY, null);
            db.execSQL("DROP TABLE IF EXISTS contacts");
           // db.setVersion(1);
            db.execSQL(sqlcmd);

            //read contact.txt and put into array populate table
            createContactsArray();
            createContactsDB(contactsArray);

            //check contact's birthdays against today's date
            checkForBirthdayDate();

            if (isSomeonesBirthday)
            {
                //load message into textView
                tvSMS.setText(showBirthdayMessage());
            }
        }
        catch(Exception e)
        {
          Log.d("test", "" + e.getMessage());
        }
        db.close();
    }//close onCreate

    //******************* createContactsArray() *****************
    private void createContactsArray()
    {
        try
        {   //read text from file
            InputStream is = getResources().openRawResource(R
                .raw.contacts);
            Scanner sc = new Scanner(is);
            int i = 0;
            while (sc.hasNextLine())
            {
                contactsArray[i] = ""+sc.nextLine();
                i++;
            }
            sc.close();
            is.close();
        }
        catch(IOException e)
        {
            Log.d("testing1", "" + e.getMessage());
        }
    }

    //*********************** createContactsDB()*****************
    private void createContactsDB(String[] contactsArray)
    {
        ContentValues cv = new ContentValues();

        //jump ahead 4 lines to next
        for (int i = 0; i < contactsArray.length; i += 4)
        {
            if(contactsArray[i]!=null)
            {
                cv.put("name", contactsArray[i]);
                cv.put("email", contactsArray[i + 1]);
                cv.put("phone", contactsArray[i + 2]);
                cv.put("birthday", contactsArray[i + 3]);
                db.insert("contacts", null, cv);
            }
        }
        c = db.rawQuery("select * from contacts;", null);
        c.moveToFirst();
    }

    //******************* checkForBirthdayDate() ****************
    private void checkForBirthdayDate() throws Exception
    {
        DateFormat dateFormat =new SimpleDateFormat("MM/dd/yyyy");
        Date date = new Date();
        String currentDate = dateFormat.format(date);
        String currentDayMonth = currentDate.substring(0,5);

        //see if it's someones birthday by comparing dates
        c.moveToFirst();
        while(!c.isAfterLast() && !isSomeonesBirthday)
        {
            String birthday = c.getString(c.getColumnIndex
                ("birthday"));
            String birthdayDayMonth = birthday.substring(0,5);

            //if dates match, retrieve the phone number.
            if(currentDayMonth.equals(birthdayDayMonth))
            {
                isSomeonesBirthday = true;

                bdName=c.getString(c.getColumnIndex("name"));
                bdEmail=c.getString(c.getColumnIndex("email"));
                bdPhoneNum=c.getString(c.getColumnIndex
                ("phone"));
                bdDate=c.getString(c.getColumnIndex("birthday"));

                tvName.setText(bdName);
                tvEmail.setText(bdEmail);
                tvPhone.setText(bdPhoneNum);
                tvBirthday.setText(bdDate);
            }
            c.moveToNext();
        }
    }
    //******************  showBirthdayMessage() ****************
    private String showBirthdayMessage()
    {
        //read text from file
        InputStream inputStream = getResources().openRawResource(R
            .raw.birthday_wish);

        ByteArrayOutputStream byteArrayOutputStream = new
            ByteArrayOutputStream();

        int i;
        try
        {
            i = inputStream.read();
            while(i != -1)
            {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        }
        catch(IOException e)
        {
            Log.d("test2", "" + e.getMessage());
        }
        String messageString = byteArrayOutputStream.toString();

        //search for XXX and replace with contact's name
        messageStringWithName = messageString.replaceAll("XXX",
           bdName);
        return messageStringWithName;
    }

    //*************** sendBirthdayGreetingOnClick() *************
    public void sendBirthdayGreetingOnClick(View v)
    {
        sendBirthdaySMS(bdPhoneNum,messageStringWithName);
    }

    //********************* sendBirthdaySMS() *******************
    private void sendBirthdaySMS(String address, String message)
    {
        try
        {
            SmsManager sm = SmsManager.getDefault();
            sm.sendTextMessage(address, null,message, null,null);
        }
        catch(Exception e)
        {
            Toast.makeText(SendBirthdayGreetingActivity.this,
                "SMS not sent.",
                Toast.LENGTH_SHORT).show();
        }
    }
}
