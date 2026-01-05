package com.example.doan_mau;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.accessibility.AccessibilityEvent;
import java.util.HashSet;
import java.util.Set;

public class BlockerService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            String packageName = (event.getPackageName() != null) ? event.getPackageName().toString() : "";
            if (packageName.equals(getPackageName())) return;

            SharedPreferences prefs = getSharedPreferences("AppBlocker", MODE_PRIVATE);
            long blockUntil = prefs.getLong("block_until", 0);
            Set<String> blockedApps = prefs.getStringSet("blocked_apps", new HashSet<>());

            if (System.currentTimeMillis() < blockUntil && blockedApps.contains(packageName)) {
                // Đẩy về Home ngay lập tức
                performGlobalAction(GLOBAL_ACTION_HOME);
                // Sau đó hiện màn hình chặn
                Intent intent = new Intent(this, BlockedActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onInterrupt() {}

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
        info.notificationTimeout = 100;
        setServiceInfo(info);
    }
}