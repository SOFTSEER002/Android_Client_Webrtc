/*
 *  Copyright 2015 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package com.myhexaville.androidwebrtc.app_rtc_sample.call;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableMap;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.zxing.WriterException;
import com.myhexaville.androidwebrtc.R;
import com.myhexaville.androidwebrtc.app_rtc_sample.main.AppRTCMainActivity;
import com.myhexaville.androidwebrtc.databinding.ActivityCallBinding;
import com.myhexaville.androidwebrtc.app_rtc_sample.web_rtc.AppRTCAudioManager;
import com.myhexaville.androidwebrtc.app_rtc_sample.web_rtc.AppRTCClient;
import com.myhexaville.androidwebrtc.app_rtc_sample.web_rtc.AppRTCClient.RoomConnectionParameters;
import com.myhexaville.androidwebrtc.app_rtc_sample.web_rtc.AppRTCClient.SignalingParameters;
import com.myhexaville.androidwebrtc.app_rtc_sample.web_rtc.PeerConnectionClient;
import com.myhexaville.androidwebrtc.app_rtc_sample.web_rtc.PeerConnectionClient.PeerConnectionParameters;
import com.myhexaville.androidwebrtc.app_rtc_sample.web_rtc.WebSocketRTCClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static com.myhexaville.androidwebrtc.app_rtc_sample.util.Constants.CAPTURE_PERMISSION_REQUEST_CODE;
import static com.myhexaville.androidwebrtc.app_rtc_sample.util.Constants.EXTRA_ROOMID;
import static com.myhexaville.androidwebrtc.app_rtc_sample.util.Constants.LOCAL_HEIGHT_CONNECTED;
import static com.myhexaville.androidwebrtc.app_rtc_sample.util.Constants.LOCAL_HEIGHT_CONNECTING;
import static com.myhexaville.androidwebrtc.app_rtc_sample.util.Constants.LOCAL_WIDTH_CONNECTED;
import static com.myhexaville.androidwebrtc.app_rtc_sample.util.Constants.LOCAL_WIDTH_CONNECTING;
import static com.myhexaville.androidwebrtc.app_rtc_sample.util.Constants.LOCAL_X_CONNECTED;
import static com.myhexaville.androidwebrtc.app_rtc_sample.util.Constants.LOCAL_X_CONNECTING;
import static com.myhexaville.androidwebrtc.app_rtc_sample.util.Constants.LOCAL_Y_CONNECTED;
import static com.myhexaville.androidwebrtc.app_rtc_sample.util.Constants.LOCAL_Y_CONNECTING;
import static com.myhexaville.androidwebrtc.app_rtc_sample.util.Constants.REMOTE_HEIGHT;
import static com.myhexaville.androidwebrtc.app_rtc_sample.util.Constants.REMOTE_WIDTH;
import static com.myhexaville.androidwebrtc.app_rtc_sample.util.Constants.REMOTE_X;
import static com.myhexaville.androidwebrtc.app_rtc_sample.util.Constants.REMOTE_Y;
import static com.myhexaville.androidwebrtc.app_rtc_sample.util.Constants.STAT_CALLBACK_PERIOD;

import static org.webrtc.RendererCommon.ScalingType.SCALE_ASPECT_FILL;
import static org.webrtc.RendererCommon.ScalingType.SCALE_ASPECT_FIT;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.widget.TextView;
import android.widget.Toast;

import com.myhexaville.androidwebrtc.tutorial.SimpleSdpObserver;
import com.myhexaville.smartimagepicker.ImagePicker;

import org.webrtc.DataChannel;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class CallActivity extends AppCompatActivity
        implements AppRTCClient.SignalingEvents, PeerConnectionClient.PeerConnectionEvents, OnCallEvents, OnMapReadyCallback {

    private static final String LOG_TAG = "CallActivity";
    private PeerConnectionClient peerConnectionClient;
    private AppRTCClient appRtcClient;
    private SignalingParameters signalingParameters;
    private AppRTCAudioManager audioManager;
    private EglBase rootEglBase;
    private final List<VideoRenderer.Callbacks> remoteRenderers = new ArrayList<>();
    private boolean activityRunning;
    private RoomConnectionParameters roomConnectionParameters;
    private PeerConnectionParameters peerConnectionParameters;
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;
    private boolean iceConnected;
    private boolean isError;
    private long callStartedTimeMs;
    private boolean micEnabled = true;
    private boolean isStarted;
    double latitude, longitude;
    Marker mCurrLocationMarker;
    TextView tv_bat_lvl, tv_bat_temp, tv_wifi_signal, tv_net_signal;
    private ActivityCallBinding binding;
    Dialog dialog;
    String roomId = null;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;

    private static final String TAG = "SampleDataChannelAct";
    public static final int CHUNK_SIZE = 64000;

    // private ActivitySampleDataChannelBinding binding;
    private PeerConnectionFactory factory;
    private PeerConnection peerConnection, localPeerConnection, remotePeerConnection;
    private DataChannel localDataChannel;
    private ImagePicker imagePicker;
    GoogleApiClient mGoogleApiClient;

    int incomingFileSize;
    int currentIndexPointer;
    byte[] imageFileBytes;
    boolean receivingFile;
    private String android_id, Username;
    private Boolean hasConnection = false;
    private Socket mSocket;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;

    {
        try {
            mSocket = IO.socket("https://intense-bayou-55879.herokuapp.com/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(LayoutParams.FLAG_DISMISS_KEYGUARD | LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call);
        android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        tv_bat_lvl = findViewById(R.id.batlvlsocket);
        tv_bat_temp = findViewById(R.id.batTempsocket);
        tv_net_signal = findViewById(R.id.networksignalsocket);
        tv_wifi_signal = findViewById(R.id.wifisignalsocket);
        initializePeerConnectionFactory();

        initializePeerConnections();

        connectToOtherPeer();

        imagePicker = new ImagePicker(this,
                null,
                imageUri -> {
                    File imageFile = imagePicker.getImageFile();
                    int size = (int) imageFile.length();
                    byte[] bytes = readPickedFileAsBytes(imageFile, size);
                    sendImage(size, bytes);
                });

        // Get Intent parameters.
        Intent intent = getIntent();
        roomId = intent.getStringExtra(EXTRA_ROOMID);

        remoteRenderers.add(binding.remoteVideoView);

        // Create video renderers.
        rootEglBase = EglBase.create();
        binding.localVideoView.init(rootEglBase.getEglBaseContext(), null);
        binding.remoteVideoView.init(rootEglBase.getEglBaseContext(), null);

        binding.localVideoView.setZOrderMediaOverlay(true);
        binding.localVideoView.setEnableHardwareScaler(true);
        binding.remoteVideoView.setEnableHardwareScaler(true);
        updateVideoView();


        if (roomId == null || roomId.length() == 0) {
            Log.e(LOG_TAG, "Incorrect room ID in intent!");
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        // If capturing format is not specified for screencapture, use screen resolution.
        peerConnectionParameters = PeerConnectionParameters.createDefault();

        // Create connection client. Use DirectRTCClient if room name is an IP otherwise use the
        // standard WebSocketRTCClient.
        appRtcClient = new WebSocketRTCClient(this);

        // Create connection parameters.
        roomConnectionParameters = new RoomConnectionParameters("https://appr.tc", roomId, false);

        setupListeners();

        peerConnectionClient = PeerConnectionClient.getInstance();
        peerConnectionClient.createPeerConnectionFactory(this, peerConnectionParameters, this);

        startCall();
    }

    private void socketIO() {
        Username = android_id;
        if (hasConnection) {

        } else {
            mSocket.connect();
            mSocket.on("connect user", onNewUser);
            mSocket.on("jsondata", onNewMessage);

            JSONObject userId = new JSONObject();
            try {
                userId.put("connected", Username + " Connected");
                mSocket.emit("connect user", userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        imagePicker.handlePermission(requestCode, grantResults);
    }

    private void initializePeerConnectionFactory() {
        PeerConnectionFactory.initializeAndroidGlobals(this, true, true, true);
        factory = new PeerConnectionFactory(null);
    }

    private void initializePeerConnections() {
        peerConnection = createPeerConnection(factory, true);

        localPeerConnection = createPeerConnection(factory, true);
        remotePeerConnection = createPeerConnection(factory, false);
        this.localDataChannel = remotePeerConnection.createDataChannel("sendDataChannel", new DataChannel.Init());

        // localDataChannel = localPeerConnection.createDataChannel("sendDataChannel", new DataChannel.Init());
        localDataChannel.registerObserver(new DataChannel.Observer() {
            @Override
            public void onBufferedAmountChange(long l) {

            }

            @Override
            public void onStateChange() {
                Log.d(TAG, "onStateChange: " + localDataChannel.state().toString());
                runOnUiThread(() -> {
                    if (localDataChannel.state() == DataChannel.State.OPEN) {
                        // binding.sendButton.setEnabled(true);
                    } else {
                        // binding.sendButton.setEnabled(false);
                    }
                });
            }

            @Override
            public void onMessage(DataChannel.Buffer buffer) {
                Toast.makeText(CallActivity.this, "hloo", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onMessage: hlkjio");
            }
        });
    }

    private void connectToOtherPeer() {
        MediaConstraints sdpMediaConstraints = new MediaConstraints();

        localPeerConnection.createOffer(new SimpleSdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.d(TAG, "onCreateSuccess: ");
                localPeerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);
                remotePeerConnection.setRemoteDescription(new SimpleSdpObserver(), sessionDescription);

                remotePeerConnection.createAnswer(new SimpleSdpObserver() {
                    @Override
                    public void onCreateSuccess(SessionDescription sessionDescription) {
                        localPeerConnection.setRemoteDescription(new SimpleSdpObserver(), sessionDescription);
                        remotePeerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);
                    }
                }, sdpMediaConstraints);
            }
        }, sdpMediaConstraints);
    }

    private PeerConnection createPeerConnection(PeerConnectionFactory factory, boolean isLocal) {
        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(new ArrayList<>());
        MediaConstraints pcConstraints = new MediaConstraints();

        PeerConnection.Observer pcObserver = new PeerConnection.Observer() {
            @Override
            public void onSignalingChange(PeerConnection.SignalingState signalingState) {
                Log.d(TAG, "onSignalingChange: ");
            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
                Log.d(TAG, "onIceConnectionChange: ");
            }

            @Override
            public void onIceConnectionReceivingChange(boolean b) {
//                Log.d(TAG, "onIceConnectionReceivingChange: ");
            }

            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
                Log.d(TAG, "onIceGatheringChange: ");
            }

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                Log.d(TAG, "onIceCandidate: " + isLocal);
                if (isLocal) {
                    remotePeerConnection.addIceCandidate(iceCandidate);
                } else {
                    localPeerConnection.addIceCandidate(iceCandidate);
                }


                Log.d(TAG, "onIceCandidate: ");
            }

            @Override
            public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
                Log.d(TAG, "onIceCandidatesRemoved: ");
            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                Log.d(TAG, "onAddStream: ");
            }

            @Override
            public void onRemoveStream(MediaStream mediaStream) {
                Log.d(TAG, "onRemoveStream: ");
            }

            @Override
            public void onDataChannel(DataChannel dataChannel) {
                Log.d(TAG, "onDataChannel: is local: " + isLocal + " , state: " + dataChannel.state());

                dataChannel.registerObserver(new DataChannel.Observer() {
                    @Override
                    public void onBufferedAmountChange(long l) {

                    }

                    @Override
                    public void onStateChange() {
                        Log.d(TAG, "onStateChange: remote data channel state: " + dataChannel.state().toString());
                    }

                    @Override
                    public void onMessage(DataChannel.Buffer buffer) {
                        Log.d(TAG, "onMessage: got message");
                        Toast.makeText(CallActivity.this, "Connected", Toast.LENGTH_LONG).show();
                        String message = byteBufferToString(buffer.data, Charset.defaultCharset());
                        Log.d(TAG, "onMessage2: " + message);
//                        runOnUiThread(() -> binding.text.setText(message));
                        Toast.makeText(CallActivity.this, "" + message, Toast.LENGTH_LONG).show();
                        readIncomingMessage(buffer.data);
                    }


                });
            }

            @Override
            public void onRenegotiationNeeded() {
                Log.d(TAG, "onRenegotiationNeeded: ");
            }
        };


        return factory.createPeerConnection(rtcConfig, pcConstraints, pcObserver);
    }

    private String byteBufferToString(ByteBuffer buffer, Charset charset) {
        byte[] bytes;
        if (buffer.hasArray()) {
            bytes = buffer.array();
        } else {
            bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
        }
        return new String(bytes, charset);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private byte[] readPickedFileAsBytes(File imageFile, int size) {
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(imageFile));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    private void sendImage(int size, byte[] bytes) {
        int numberOfChunks = size / CHUNK_SIZE;

        ByteBuffer meta = stringToByteBuffer("-i" + size, Charset.defaultCharset());
        localDataChannel.send(new DataChannel.Buffer(meta, false));

        for (int i = 0; i < numberOfChunks; i++) {
            ByteBuffer wrap = ByteBuffer.wrap(bytes, i * CHUNK_SIZE, CHUNK_SIZE);
            localDataChannel.send(new DataChannel.Buffer(wrap, false));
        }
        int remainder = size % CHUNK_SIZE;
        if (remainder > 0) {
            ByteBuffer wrap = ByteBuffer.wrap(bytes, numberOfChunks * CHUNK_SIZE, remainder);
            localDataChannel.send(new DataChannel.Buffer(wrap, false));
        }
    }

    private void readIncomingMessage(ByteBuffer buffer) {
        byte[] bytes;
        if (buffer.hasArray()) {
            bytes = buffer.array();
        } else {
            bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
        }
        if (!receivingFile) {
            String firstMessage = new String(bytes, Charset.defaultCharset());
            String type = firstMessage.substring(0, 2);

            if (type.equals("-i")) {
                incomingFileSize = Integer.parseInt(firstMessage.substring(2, firstMessage.length()));
                imageFileBytes = new byte[incomingFileSize];
                Log.d(TAG, "readIncomingMessage: incoming file size " + incomingFileSize);
                receivingFile = true;
            } else if (type.equals("-s")) {
                // runOnUiThread(() -> binding.remoteText.setText(firstMessage.substring(2, firstMessage.length())));
            }
        } else {
            for (byte b : bytes) {
                imageFileBytes[currentIndexPointer++] = b;
            }
            if (currentIndexPointer == incomingFileSize) {
                Log.d(TAG, "readIncomingMessage: received all bytes");
                Bitmap bmp = BitmapFactory.decodeByteArray(imageFileBytes, 0, imageFileBytes.length);
                receivingFile = false;
                currentIndexPointer = 0;
                //runOnUiThread(() -> binding.image.setImageBitmap(bmp));
            }
        }
    }

    private static ByteBuffer stringToByteBuffer(String msg, Charset charset) {
        return ByteBuffer.wrap(msg.getBytes(charset));
    }

    public void pickImage(View view) {
        imagePicker.openCamera();
    }


    private void setupListeners() {
        binding.buttonCallDisconnect.setOnClickListener(view -> onCallHangUp());

        binding.buttonCallSwitchCamera.setOnClickListener(view -> onCameraSwitch());

        binding.buttonCallToggleMic.setOnClickListener(view -> {
            boolean enabled = onToggleMic();
            binding.buttonCallToggleMic.setAlpha(enabled ? 1.0f : 0.3f);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != CAPTURE_PERMISSION_REQUEST_CODE) {
            return;
        }
        imagePicker.handleActivityResult(resultCode, requestCode, data);
        startCall();
    }

    private boolean useCamera2() {
        return Camera2Enumerator.isSupported(this);
    }

    private boolean captureToTexture() {
        return true;
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        Logging.d(LOG_TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d(LOG_TAG, "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        Logging.d(LOG_TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d(LOG_TAG, "Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    // Activity interfaces
    @Override
    public void onPause() {
        super.onPause();
        activityRunning = false;
        // Don't stop the video when using screencapture to allow user to show other apps to the remote
        // end.
        if (peerConnectionClient != null) {
            peerConnectionClient.stopVideoSource();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        activityRunning = true;
        // Video is not paused for screencapture. See onPause.
        if (peerConnectionClient != null) {
            peerConnectionClient.startVideoSource();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getIntent().getExtras();
        if (args != null) {
            String contactName = args.getString(EXTRA_ROOMID);
//            binding.contactNameCall.setText(contactName);

        }
        binding.captureFormatTextCall.setVisibility(View.GONE);
        binding.captureFormatSliderCall.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        disconnect();
        activityRunning = false;
        rootEglBase.release();
        super.onDestroy();
        if (isFinishing()) {
            Log.i("Destroying", "onDestroy: ");
            JSONObject userId = new JSONObject();
            try {
                userId.put("username", Username + " DisConnected");
                mSocket.emit("connect user", userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.disconnect();
            mSocket.off("jsondata", onNewMessage);
            mSocket.off("connect user", onNewUser);
            Username = "";

        } else {
            Log.i("Destroying", "onDestroy: is rotating.....");
        }
    }

    // CallFragment.OnCallEvents interface implementation.
    @Override
    public void onCallHangUp() {
        disconnect();
        if (isFinishing()) {
            Log.i("Destroying", "onDestroy: ");

            JSONObject userId = new JSONObject();
            try {
                userId.put("username", Username + " DisConnected");
                mSocket.emit("connect user", userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mSocket.disconnect();
            mSocket.off("jsondata", onNewMessage);
            mSocket.off("connect user", onNewUser);
            Username = "";

        } else {
            Log.i("Destroying", "onDestroy: is rotating.....");
        }
    }

    @Override
    public void onCameraSwitch() {
        if (peerConnectionClient != null) {
            peerConnectionClient.switchCamera();
        }
    }

    @Override
    public void onCaptureFormatChange(int width, int height, int framerate) {
        if (peerConnectionClient != null) {
            peerConnectionClient.changeCaptureFormat(width, height, framerate);
        }
    }

    @Override
    public boolean onToggleMic() {
        if (peerConnectionClient != null) {
            micEnabled = !micEnabled;
            peerConnectionClient.setAudioEnabled(true);
        }
        return true;
    }

    private void updateVideoView() {
        binding.remoteVideoLayout.setPosition(REMOTE_X, REMOTE_Y, REMOTE_WIDTH, REMOTE_HEIGHT);
        binding.remoteVideoView.setScalingType(SCALE_ASPECT_FILL);
        binding.remoteVideoView.setMirror(false);

        if (iceConnected) {
            binding.localVideoLayout.setPosition(
                    LOCAL_X_CONNECTED, LOCAL_Y_CONNECTED, LOCAL_WIDTH_CONNECTED, LOCAL_HEIGHT_CONNECTED);
            binding.localVideoView.setScalingType(SCALE_ASPECT_FIT);
        } else {
            binding.localVideoLayout.setPosition(LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING, LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING);
            binding.localVideoView.setScalingType(SCALE_ASPECT_FILL);
        }
        binding.localVideoView.setMirror(true);

        binding.localVideoView.requestLayout();
        binding.remoteVideoView.requestLayout();
    }

    private void startCall() {

        callStartedTimeMs = System.currentTimeMillis();
        appRtcClient.connectToRoom(roomConnectionParameters);
        // Create and audio manager that will take care of audio routing,
        // audio modes, audio device enumeration etc.
        audioManager = AppRTCAudioManager.create(this);
        // Store existing audio settings and change audio mode to
        // MODE_IN_COMMUNICATION for best possible VoIP performance.
        Log.d(LOG_TAG, "Starting the audio manager...");
        audioManager.start(this::onAudioManagerDevicesChanged);

    }

    // Should be called from UI thread
    private void callConnected() {
//        dialog.dismiss();
        Log.e("room==>", roomId);
        socketIO();
        onToggleMic();

        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        Log.i(LOG_TAG, "Call connected: delay=" + delta + "ms");
//        audioManager.stop();

        if (peerConnectionClient == null || isError) {
            Log.w(LOG_TAG, "Call is connected in closed or error state");
            return;
        }
        // Update video view.
        updateVideoView();
        // Enable statistics callback.
        peerConnectionClient.enableStatsEvents(true, STAT_CALLBACK_PERIOD);
    }


    public void sendMessage(String message) {
        Log.e("sendMessage", "sendMessage: ");
//        String message = textField.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            Log.e("sendMessage2", "sendMessage:2 ");
            return;
        }
//        textField.setText("");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("message", message);
            jsonObject.put("username", Username);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("sendMessage3", "sendMessage: 1" + mSocket.emit("jsondata", jsonObject));
    }

    Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("Receive", "run: " + args.length);
                    JSONObject data = (JSONObject) args[0];
                    String username, lat, longi, batteryTemp, batterylevel, wifisignal, networksignal;
                    try {
                        username = data.getString("username");
                        lat = data.getString("latitute");
                        longi = data.getString("longitute");
                        batteryTemp = data.getString("batteryTemp");
                        batterylevel = data.getString("batteryLevel");
                        networksignal = data.getString("networkSignal");
                        wifisignal = data.getString("wifiSignal");
                        Log.e("Message", "run: " + username + "\n" + lat + "\n" + longi + "\n" + batteryTemp
                                + "\n" + batterylevel + "\n" + networksignal + "\n" + wifisignal);

                        if (lat != null && longi != null) {
                            latitude = Double.parseDouble(lat);
                            longitude = Double.parseDouble(longi);
                            Log.e(TAG, "LatLong: " + latitude + "    " + longitude);
                            LatLng latLng = new LatLng(latitude, longitude);
                            MarkerOptions markerOptions = new MarkerOptions();
                            if (mCurrLocationMarker != null) {
                                mCurrLocationMarker.remove();
                            }
                            markerOptions.position(latLng);
                            markerOptions.title("Current Position");
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                            mCurrLocationMarker = mMap.addMarker(markerOptions);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
                            tv_bat_lvl.setText(batterylevel);
                            tv_bat_temp.setText(batteryTemp);
                            tv_wifi_signal.setText(wifisignal);
                            tv_net_signal.setText(networksignal);
                        } else {
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
            });
        }
    };

    Emitter.Listener onNewUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int length = args.length;

                    if (length == 0) {
                        return;
                    }
                    //Here i'm getting weird error..................///////run :1 and run: 0
                    Log.e("RUN", "run: ");
                    Log.e("RUN", "run: " + args.length);
                    String username = args[0].toString();
                    try {
                        JSONObject object = new JSONObject(username);
                        username = object.getString("connected");
                        Log.e("UserName", "run: " + username);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
        }
    };

    // This method is called when the audio manager reports audio device change,
    // e.g. from wired headset to speakerphone.
    private void onAudioManagerDevicesChanged(
            final AppRTCAudioManager.AudioDevice device, final Set<AppRTCAudioManager.AudioDevice> availableDevices) {
        Log.d(LOG_TAG, "onAudioManagerDevicesChanged: " + availableDevices + ", "
                + "selected: " + device);
        // TODO(henrika): add callback handler.
    }

    // Disconnect from remote resources, dispose of local resources, and exit.
    private void disconnect() {
        activityRunning = false;
        if (appRtcClient != null) {
            appRtcClient.disconnectFromRoom();
            appRtcClient = null;
//            showQR();

        }
        if (peerConnectionClient != null) {
            peerConnectionClient.close();
            peerConnectionClient = null;
        }
        binding.localVideoView.release();
        binding.remoteVideoView.release();
        if (audioManager != null) {
            audioManager.stop();
            audioManager = null;
        }
        if (iceConnected && !isError) {
            setResult(RESULT_OK);

        } else {
            setResult(RESULT_CANCELED);
        }

        ShowPopupforQrScan();
    }

    private void ShowPopupforQrScan() {
        dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.custom_pop_up);
        Button start_call = dialog.findViewById(R.id.start_call);
        Button scan_again = dialog.findViewById(R.id.scan_again);

        start_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CallActivity.this, CallActivity.class);
                intent.putExtra(EXTRA_ROOMID, roomId);
                startActivityForResult(intent, 1);
                finish();
            }
        });
        scan_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CallActivity.this, AppRTCMainActivity.class));
                finish();
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // Prevent dialog close on back press button
                finish();
                startActivity(new Intent(CallActivity.this, AppRTCMainActivity.class));
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
    }

    private void disconnectWithErrorMessage(final String errorMessage) {
        if (!activityRunning) {
            Log.e(LOG_TAG, "Critical error: " + errorMessage);
            disconnect();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getText(R.string.channel_error_title))
                    .setMessage(errorMessage)
                    .setCancelable(false)
                    .setNeutralButton(R.string.ok,
                            (dialog, id) -> {
                                dialog.cancel();
                                disconnect();
                            })
                    .create()
                    .show();
        }
    }


    private void reportError(final String description) {
        runOnUiThread(() -> {
            if (!isError) {
                isError = true;
                disconnectWithErrorMessage(description);
            }
        });
    }

    private VideoCapturer createVideoCapturer() {
        VideoCapturer videoCapturer;
        if (useCamera2()) {
            Logging.d(LOG_TAG, "Creating capturer using camera2 API.");
            videoCapturer = createCameraCapturer(new Camera2Enumerator(this));
        } else {
            Logging.d(LOG_TAG, "Creating capturer using camera1 API.");
            videoCapturer = createCameraCapturer(new Camera1Enumerator(captureToTexture()));
        }
        if (videoCapturer == null) {
            reportError("Failed to open camera");
            return null;
        }
        return videoCapturer;
    }

    // -----Implementation of AppRTCClient.AppRTCSignalingEvents ---------------
    // All callbacks are invoked from websocket signaling looper thread and
    // are routed to UI thread.
    private void onConnectedToRoomInternal(final SignalingParameters params) {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;

        signalingParameters = params;
        VideoCapturer videoCapturer = null;
        if (peerConnectionParameters.videoCallEnabled) {
            videoCapturer = createVideoCapturer();
        }
        peerConnectionClient.createPeerConnection(rootEglBase.getEglBaseContext(), binding.localVideoView,
                remoteRenderers, videoCapturer, signalingParameters);

        if (signalingParameters.initiator) {
            // Create offer. Offer SDP will be sent to answering client in
            // PeerConnectionEvents.onLocalDescription event.
            peerConnectionClient.createOffer();
        } else {
            if (params.offerSdp != null) {
                peerConnectionClient.setRemoteDescription(params.offerSdp);
                // Create answer. Answer SDP will be sent to offering client in
                // PeerConnectionEvents.onLocalDescription event.
                peerConnectionClient.createAnswer();
            }
            if (params.iceCandidates != null) {
                // Add remote ICE candidates from room.
                for (IceCandidate iceCandidate : params.iceCandidates) {
                    peerConnectionClient.addRemoteIceCandidate(iceCandidate);
                }
            }
        }
    }

    @Override
    public void onConnectedToRoom(final SignalingParameters params) {
        runOnUiThread(() -> onConnectedToRoomInternal(params));
    }

    @Override
    public void onRemoteDescription(final SessionDescription sdp) {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(() -> {
            if (peerConnectionClient == null) {
                Log.e(LOG_TAG, "Received remote SDP for non-initilized peer connection.");
                return;
            }
            peerConnectionClient.setRemoteDescription(sdp);
            if (!signalingParameters.initiator) {
                // Create answer. Answer SDP will be sent to offering client in
                // PeerConnectionEvents.onLocalDescription event.
                peerConnectionClient.createAnswer();
            }
        });
    }

    @Override
    public void onRemoteIceCandidate(final IceCandidate candidate) {
        runOnUiThread(() -> {
            if (peerConnectionClient == null) {
                Log.e(LOG_TAG, "Received ICE candidate for a non-initialized peer connection.");
                return;
            }
            peerConnectionClient.addRemoteIceCandidate(candidate);
        });
    }

    @Override
    public void onRemoteIceCandidatesRemoved(final IceCandidate[] candidates) {
        runOnUiThread(() -> {
            if (peerConnectionClient == null) {
                Log.e(LOG_TAG, "Received ICE candidate removals for a non-initialized peer connection.");
                return;
            }
            peerConnectionClient.removeRemoteIceCandidates(candidates);
        });
    }

    @Override
    public void onChannelClose() {
        runOnUiThread(() -> {
            disconnect();
        });
    }

    @Override
    public void onChannelError(final String description) {
        reportError(description);
    }

    // -----Implementation of PeerConnectionClient.PeerConnectionEvents.---------
    // Send local peer connection SDP and ICE candidates to remote party.
    // All callbacks are invoked from peer connection client looper thread and
    // are routed to UI thread.
    @Override
    public void onLocalDescription(final SessionDescription sdp) {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(() -> {
            if (appRtcClient != null) {
                if (signalingParameters.initiator) {
                    appRtcClient.sendOfferSdp(sdp);
                } else {
                    appRtcClient.sendAnswerSdp(sdp);
                }
            }
            if (peerConnectionParameters.videoMaxBitrate > 0) {
                Log.d(LOG_TAG, "Set video maximum bitrate: " + peerConnectionParameters.videoMaxBitrate);
                peerConnectionClient.setVideoMaxBitrate(peerConnectionParameters.videoMaxBitrate);
            }
        });
    }

    @Override
    public void onIceCandidate(final IceCandidate candidate) {
        runOnUiThread(() -> {
            if (appRtcClient != null) {
                appRtcClient.sendLocalIceCandidate(candidate);
            }
        });
    }

    @Override
    public void onIceCandidatesRemoved(final IceCandidate[] candidates) {
        runOnUiThread(() -> {
            if (appRtcClient != null) {
                appRtcClient.sendLocalIceCandidateRemovals(candidates);
            }
        });
    }

    @Override
    public void onIceConnected() {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(() -> {
            iceConnected = true;
            callConnected();
        });
    }

    @Override
    public void onIceDisconnected() {
        runOnUiThread(() -> {
            iceConnected = false;
            disconnect();
        });

            Log.i("Destroying", "onDestroy: ");

            JSONObject userId = new JSONObject();
            try {
                userId.put("username", Username + " DisConnected");
                mSocket.emit("connect user", userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mSocket.disconnect();
            mSocket.off("jsondata", onNewMessage);
            mSocket.off("connect user", onNewUser);
            Username = "";
    }

    @Override
    public void onPeerConnectionClosed() {
    }

    @Override
    public void onPeerConnectionStatsReady(final StatsReport[] reports) {
        runOnUiThread(() -> {
        });
    }

    @Override
    public void onPeerConnectionError(final String description) {
        reportError(description);
    }

    @Override
    public void onReceivedData(String command) {
        Log.d(TAG, "onReceivedData: " + command);
        // Toast.makeText(this, ""+command, Toast.LENGTH_SHORT).show();
    }


    void showQR() {
        if (roomId.length() > 0) {
            Log.e(LOG_TAG, "Room ID: " + roomId);

            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = width < height ? width : height;
            smallerDimension = smallerDimension * 3 / 4;

            qrgEncoder = new QRGEncoder(
                    roomId, null,
                    QRGContents.Type.TEXT,
                    smallerDimension);
            try {
                bitmap = qrgEncoder.encodeAsBitmap();
                dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                dialog.setContentView(R.layout.custom_record_timer);
                ImageView QR_img = dialog.findViewById(R.id.QR_img);
                QR_img.setImageBitmap(bitmap);
                dialog.show();
            } catch (WriterException e) {
                Log.v(this + "", e.toString());
            }
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(onCameraIdleListener);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            checkLocationPermission();
        }

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
//                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
//            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new android.support.v7.app.AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(CallActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        1);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }
    }


}
