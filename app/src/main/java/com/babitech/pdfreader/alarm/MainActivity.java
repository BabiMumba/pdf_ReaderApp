package com.babitech.pdfreader.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.babitech.pdfreader.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_alarm);

        final alarmPreferences preferences = new alarmPreferences();

        final EditText title, message, date;
        title = findViewById(R.id.title);
        message = findViewById(R.id.message);
        date = findViewById(R.id.date);

        date.setText(sdf.format(new Date()));

        final Button set, cancel;
        set = findViewById(R.id.setNow);
        cancel = findViewById(R.id.cancelNow);

        // If no alarm is created yet
        if (preferences.getDate(getApplicationContext()).equals("null")) {
            cancel.setEnabled(false);
        } else {
            // If the date that set for alarm is less than current time. (This means alarm is expired)
            if (Long.parseLong(stringToUnixTime(preferences.getDate(getApplicationContext()))) < Long.parseLong(sdf.format(new Date()))) {
                cancel.setEnabled(false);
            }
        }

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ttl = title.getText().toString();
                String msg = message.getText().toString();
                String dt = date.getText().toString();

                if (title.length() == 0) {
                    Toast.makeText(getApplicationContext(), "PLease enter Title", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (message.length() == 0) {
                    Toast.makeText(getApplicationContext(), "PLease enter message", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dt.length() != 19) {
                    Toast.makeText(getApplicationContext(), "Date is not valid", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    sdf.parse(dt);
                } catch (ParseException e) {
                    Toast.makeText(getApplicationContext(), "Date is not valid", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Save message, date and title into Shared Preferences
                preferences.setData(getApplicationContext(), ttl, msg, dt);

                // Cancel previous alarm before creating new one. (If you are using same request Code for new alarm)
                cancelAlarm(getApplicationContext(), 10);

                //Set new alarm with request code 10. If you want to cancel this alarm you need to use same request code;
                setAlarm(getApplicationContext(), 10, dt);

                Toast.makeText(getApplicationContext(), "Alarm is set to " + dt, Toast.LENGTH_SHORT).show();
                set.setEnabled(false);
                cancel.setEnabled(true);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cancel alarm that created with request code 10;
                cancelAlarm(getApplicationContext(), 10);
                Toast.makeText(getApplicationContext(), "Alarm canceled ", Toast.LENGTH_SHORT).show();
                cancel.setEnabled(false);
            }
        });

    }

    private static void setAlarm(Context context, int reqCode, String time) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, receiveAlarmManager.class);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android version S and higher requires FLAG when creating Pending Intent
            pendingIntent = PendingIntent.getBroadcast(context, reqCode, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context, reqCode, intent, 0);
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, Long.parseLong(stringToUnixTime(time)) * 1000L, pendingIntent);
    }

    private static void cancelAlarm(Context context, int reqCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, receiveAlarmManager.class);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android version S and higher requires FLAG when creating Pending Intent
            pendingIntent = PendingIntent.getBroadcast(context, reqCode, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context, reqCode, intent, 0);
        }
        alarmManager.cancel(pendingIntent);
    }

    public static class bootReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // This is an important side of scheduling background tasks;
            // When you reboot your device your Alarms will be cleared
            // So every time a device is rebooted you should rebuild your Alarms
            // This class will run just after the device is powered on
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") || intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON")) {
                final alarmPreferences preferences = new alarmPreferences();
                setAlarm(context, 10, preferences.getDate(context));
            }
        }
    }

    public static class receiveAlarmManager extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // This is the class that will receive when the alarm is fired;
            NotificationClass notificationClass = new NotificationClass(context);
            notificationClass.show();
        }
    }

    //Convert string time to Unix (Epoch) time
    static String stringToUnixTime(String time) {
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(date.getTime() / 1000L);
    }

    // Save into shared preferences
    public static class alarmPreferences {

        public void setData(Context context, String _title, String _message, String _date) {
            SharedPreferences sharedPref = context.getSharedPreferences("AB", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("title", _title);
            editor.putString("message", _message);
            editor.putString("date", _date);
            editor.apply();
        }

        public String getTitle(Context context) {
            SharedPreferences sharedPref = context.getSharedPreferences("AB", Context.MODE_PRIVATE);
            return sharedPref.getString("title", "null");
        }

        public String getMessage(Context context) {
            SharedPreferences sharedPref = context.getSharedPreferences("AB", Context.MODE_PRIVATE);
            return sharedPref.getString("message", "null");
        }

        public String getDate(Context context) {
            SharedPreferences sharedPref = context.getSharedPreferences("AB", Context.MODE_PRIVATE);
            return sharedPref.getString("date", "null");
        }
    }

}
