package com.tk.android_download_manager;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.core.content.ContextCompat;

import com.tk.android_download_manager.models.DownloadAction;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.EventChannel;

public class DownloadBroadcastReceiver extends BroadcastReceiver implements EventChannel.StreamHandler {
    private final Context context;
    private EventChannel.EventSink events;

    public DownloadBroadcastReceiver(Context context) {
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (events != null) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    Map<String, String> result = new HashMap<>();
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0L);
                    result.put("id", String.valueOf(downloadId));
                    result.put("action", String.valueOf(DownloadAction.Downloaded.ordinal()));
                    events.success(result);
                }
                if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
                    Map<String, String> result = new HashMap<>();
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0L);
                    result.put("id", String.valueOf(downloadId));
                    result.put("action", String.valueOf(DownloadAction.NotificationClicked.ordinal()));
                    events.success(result);
                }
            }
        }
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        this.events = events;
        if (context != null) {
            ContextCompat.registerReceiver(context,this,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),ContextCompat.RECEIVER_EXPORTED);
            ContextCompat.registerReceiver(context,this,new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED),ContextCompat.RECEIVER_EXPORTED);
        }
    }

    @Override
    public void onCancel(Object arguments) {
        if (context != null) {
            context.unregisterReceiver(this);
        }
    }
}
