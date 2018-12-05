package com.example.user.osc;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPort;
import com.illposed.osc.OSCPortOut;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private final static int TIME_THRESHOLD = 200;
    private final static  float DELTA_DISTANCE_THRESHOLD = 5;

    String ip;
    int port;
    OSCPortOut sender;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void buttonClick(View v)
    {

        class Connect extends AsyncTask<Void, Void, Void>
        {
            @Override
            protected Void doInBackground(Void... voids)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Started", Toast.LENGTH_SHORT).show();
                    }
                });
                try
                {
                    ip = ((EditText)findViewById(R.id.ip)).getText().toString();
                    port = Integer.parseInt(((EditText)findViewById(R.id.port)).getText().toString());
                    sender = new OSCPortOut(InetAddress.getByName(ip), port);
                }
                catch (Exception e)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Error connecting", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                    }
                });
                return null;
            }
        }
        new Connect().execute();

    }

    float xx, yy, startX, startY;

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(ip == null || sender == null)
            return true;

        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            xx = event.getX();
            yy = event.getY();
            startX = xx;
            startY = yy;
            return true;
        }
        else if(event.getAction() == MotionEvent.ACTION_UP)
        {

        }


        List<Object> msgs = new ArrayList<>();
        msgs.add(new Float(xx - event.getX()));
        msgs.add(new Float(yy - event.getY()));
        xx = event.getX();
        yy = event.getY();

        OSCMessage msg = new OSCMessage("/mouse/move", msgs);

        class Send extends AsyncTask<OSCMessage, Void, Void>
        {
            @Override
            protected Void doInBackground(OSCMessage... oscMessages)
            {
                try {
                    sender.send(oscMessages[0]);
                    Log.d("qwerty", "Send!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }

        new Send().execute(msg);
        return true;
    }


    @Override
    protected void onPause()
    {
        super.onPause();
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder bldr = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Remote")
                .setContentText("Tap to go back")
                .setContentIntent(pi)
                .setOngoing(true)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Notification not = bldr.build();
        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(1, not);

    }
}
