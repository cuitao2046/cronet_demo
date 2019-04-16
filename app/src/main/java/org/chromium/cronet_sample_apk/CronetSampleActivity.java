// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.cronet_sample_apk;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity for managing the Cronet Sample.
 */




public class CronetSampleActivity extends Activity {
    private static final String TAG = CronetSampleActivity.class.getSimpleName();

    private String mUrl = "https://www.example.org/";
    private TextView mResultText;
    private TextView mReceiveDataText;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mResultText = findViewById(R.id.resultView);
        mReceiveDataText = findViewById(R.id.dataView);

        Log.i(TAG, "initializing Cronet engine.");
        CronetUtils.getsInstance().init(getApplicationContext());

        String appUrl = (getIntent() != null ? getIntent().getDataString() : null);
        if (appUrl == null) {
            promptForURL(mUrl);
        } else {
            startWithUrl(appUrl, "{}");
        }
    }

    private void promptForURL(String url) {
        Log.i(TAG, "No URL provided via intent, prompting user...");
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter a URL");
        LayoutInflater inflater = getLayoutInflater();
        View alertView = inflater.inflate(R.layout.dialog_url, null);
        final EditText urlInput = alertView.findViewById(R.id.urlText);
        urlInput.setText(url);
        final EditText postInput = alertView.findViewById(R.id.postText);
        alert.setView(alertView);

        alert.setPositiveButton("Load", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {
                mReceiveDataText.setText("");
                mResultText.setText("");
                String url = urlInput.getText().toString();
                mUrl = url;
                String postData = postInput.getText().toString();
                startWithUrl(url, postData);
            }
        });
        alert.show();
    }

    private void startWithUrl(String url, String postData) {
        CronetUtils.getsInstance().getHtml(url, new SimpleUrlRequestCallback(System.currentTimeMillis()), postData);
        promptForURL(mUrl);
    }
}
