package com.jason.rssprocessing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import static com.jason.rssprocessing.R.id.webView;

/**
 * Created by jason on 2017-02-14.
 */

public class Web_View extends AppCompatActivity{

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        WebView myWebView = (WebView)findViewById(webView);

        WebSettings webSettings = myWebView.getSettings();

        webSettings.setJavaScriptEnabled(true);

       // myWebView.addJavascriptInterface(new WebAppInterface(this), "android");



        myWebView.loadUrl(url);
    }
}
