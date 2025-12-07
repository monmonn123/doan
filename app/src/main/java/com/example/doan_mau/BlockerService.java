package com.example.doan_mau;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.util.HashSet;
import java.util.Set;

public class BlockerService extends AccessibilityService {

    private static final String TAG = "BlockerService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (event.getPackageName() != null) {
                // Lấy thông tin phiên chặn mỗi khi có sự kiện
                SharedPreferences prefs = getSharedPreferences("AppBlocker", MODE_PRIVATE);
                long blockUntil = prefs.getLong("block_until", 0);
                Set<String> blockedApps = prefs.getStringSet("blocked_apps", new HashSet<>());

                // Kiểm tra xem phiên chặn còn hiệu lực không
                if (System.currentTimeMillis() < blockUntil) {
                    String packageName = event.getPackageName().toString();
                    if (blockedApps.contains(packageName)) {
                        Log.d(TAG, "Blocking app: " + packageName);
                        Intent intent = new Intent(this, BlockedActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Service interrupted");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
        Log.d(TAG, "Blocker service connected.");
    }
}
