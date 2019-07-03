/*
 *  Copyright 2014 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package com.myhexaville.androidwebrtc.app_rtc_sample.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.zxing.Result;
import com.myhexaville.androidwebrtc.R;
import com.myhexaville.androidwebrtc.Splash;
import com.myhexaville.androidwebrtc.app_rtc_sample.call.CallActivity;

import java.util.Random;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.myhexaville.androidwebrtc.app_rtc_sample.util.Constants.EXTRA_ROOMID;

/**
 * Handles the initial setup where the user selects which room to join.
 */
public class AppRTCMainActivity extends Activity implements ZXingScannerView.ResultHandler {
    private static final String LOG_TAG = "AppRTCMainActivity";
    private static final int CONNECTION_REQUEST = 1;
    private static final int RC_CALL = 111;
    final int min = 10000;
    final int max = 999999;
    int random;
    private ZXingScannerView mScannerView;
    String result;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
//        Checking that run is first time of the app or not
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);


        //      Requesting Permission for the Camera
        if (ContextCompat.checkSelfPermission(AppRTCMainActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(AppRTCMainActivity.this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)
                    AppRTCMainActivity.this, Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions((Activity) AppRTCMainActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                        200);

                //      Condition for Checking isFirstRun or not
                if (isFirstRun) {
                    startActivity(new Intent(AppRTCMainActivity.this, Splash.class));
                }
                getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                        .putBoolean("isFirstRun", false).apply();
            }

        } else {
    //      Condition for Checking isFirstRun or not

            if (isFirstRun) {
                startActivity(new Intent(AppRTCMainActivity.this, Splash.class));
            }
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putBoolean("isFirstRun", false).apply();
        }
//        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        random = new Random().nextInt((max - min) + 1) + min;


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void handleResult(Result rawResult) {
        result = rawResult.toString();
        mScannerView.resumeCameraPreview(this);

        if (!result.equals("")) {
            connect(result);
        } else {
            Log.e("Result :-", null);
        }
// If you would like to resume scanning, call this method below:

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @AfterPermissionGranted(RC_CALL)
    private void connect(String result) {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {

            connectToRoom(result);
        } else {
            EasyPermissions.requestPermissions(this, "Need some permissions", RC_CALL, perms);
        }
    }


    private void connectToRoom(String roomId) {
        Log.e("Random :-", roomId);

        Intent intent = new Intent(this, CallActivity.class);
        intent.putExtra(EXTRA_ROOMID, roomId);
        startActivityForResult(intent, CONNECTION_REQUEST);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
//        if (result != null) {
//            connect(result);
//        }

        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();

    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();

    }
}
