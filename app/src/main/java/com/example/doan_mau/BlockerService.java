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
            String packageName = (event.getPackageName() != null) ? event.getPackageName().toString() : "";
            Log.d(TAG, "Window changed: " + packageName);

            if (packageName.equals(getPackageName())) return;

            SharedPreferences prefs = getSharedPreferences("AppBlocker", MODE_PRIVATE);
            long blockUntil = prefs.getLong("block_until", 0);
            Set<String> blockedApps = prefs.getStringSet("blocked_apps", new HashSet<>());

            Log.d(TAG, "Block until: " + blockUntil + " | Current time: " + System.currentTimeMillis());
            Log.d(TAG, "Blocked apps: " + blockedApps.toString());

            if (System.currentTimeMillis() < blockUntil && blockedApps.contains(packageName)) {
                Log.i(TAG, "Blocking app: " + packageName);
                performGlobalAction(GLOBAL_ACTION_HOME);
                Intent intent = new Intent(this, BlockedActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                Log.d(TAG, "Not blocking app: " + packageName);
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.w(TAG, "Service interrupted");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        // Thêm các cờ để tăng khả năng tương thích và nhận diện
        info.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS |
                     AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS |
                     AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS;
        info.notificationTimeout = 100;
        setServiceInfo(info);
        Log.i(TAG, "Service connected and configured with extra flags");
    }
}