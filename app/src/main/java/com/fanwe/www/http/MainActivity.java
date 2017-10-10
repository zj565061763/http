package com.fanwe.www.http;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.fanwe.lib.http.SDHttpRequest;

public class MainActivity extends AppCompatActivity
{
    public static final String URL = "http://ilvbt3.fanwe.net/mapi/index.php";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SDHttpRequest.post(URL)
                .form("ctl", "app")
                .form("act", "init");

    }
}
