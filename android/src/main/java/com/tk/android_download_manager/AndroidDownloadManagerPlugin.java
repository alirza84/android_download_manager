package com.tk.android_download_manager;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;


public class AndroidDownloadManagerPlugin implements FlutterPlugin, ActivityAware {
    private MethodChannel methodChannel;
    private EventChannel eventChannel;
    private Context context;
    private Activity activity;
    private BinaryMessenger messenger;

    private void setupChannels() {
        if (context == null || activity == null || messenger == null) return;

        methodChannel = new MethodChannel(messenger, "download_manager");
        eventChannel = new EventChannel(messenger, "download_manager/complete");

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadMethodChannelHandler methodHandler = new DownloadMethodChannelHandler(context, manager, activity);
        DownloadBroadcastReceiver eventHandler = new DownloadBroadcastReceiver(context);

        methodChannel.setMethodCallHandler(methodHandler);
        eventChannel.setStreamHandler(eventHandler);
    }

    private void teardownChannels() {
        if (methodChannel != null) {
            methodChannel.setMethodCallHandler(null);
            methodChannel = null;
        }
        if (eventChannel != null) {
            eventChannel.setStreamHandler(null);
            eventChannel = null;
        }
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        this.context = binding.getApplicationContext();
        this.messenger = binding.getBinaryMessenger();
        // Do not call setupChannels() here, wait for activity
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        teardownChannels();
        this.context = null;
        this.messenger = null;
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        this.activity = binding.getActivity();
        setupChannels();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        this.activity = null;
        teardownChannels();
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        this.activity = binding.getActivity();
        setupChannels();
    }

    @Override
    public void onDetachedFromActivity() {
        this.activity = null;
        teardownChannels();
    }
}
