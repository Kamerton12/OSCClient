package com.example.user.osc;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
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
    String ip;
    int port;
    OSCPortOut sender;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void buttonClick(View v){

        class Connect extends AsyncTask<Void, Void, Void>
        {
            @Override
            protected Void doInBackground(Void... voids)
            {
                try
                {
                    ip = ((EditText)findViewById(R.id.ip)).getText().toString();
                    port = Integer.parseInt(((EditText)findViewById(R.id.port)).getText().toString());
                    sender = new OSCPortOut(InetAddress.getLocalHost(), port);
                }
                catch (Exception e)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Error connecting", Toast.LENGTH_SHORT);
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT);
                    }
                });
                return null;
            }
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(ip == null || sender == null)
            return true;

        List<Object> msgs = new ArrayList<>();
        msgs.add(new Integer((int)event.getX()));
        msgs.add(new Integer((int)event.getY()));
        OSCMessage msg = new OSCMessage("/root", msgs);
        try
        {
            sender.send(msg);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return true;
    }
}
