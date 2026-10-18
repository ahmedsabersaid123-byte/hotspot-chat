package com.hotspot.chat;

import android.app.*;
import android.content.*;
import android.content.pm.ServiceInfo;
import android.net.wifi.WifiManager;
import android.os.*;
import androidx.core.app.NotificationCompat;

public class KeepService extends Service {
  PowerManager.WakeLock wl;
  WifiManager.WifiLock wf;

  @Override public IBinder onBind(Intent i) { return null; }

  @Override
  public int onStartCommand(Intent i, int f, int id) {
    ChatNative.channels(this);
    NotificationCompat.Builder b = new NotificationCompat.Builder(this, ChatNative.SVC)
      .setSmallIcon(android.R.drawable.stat_notify_chat)
      .setContentTitle("شات بلا نت").setContentText("شغال ومستني الرسايل").setOngoing(true);
    Intent o = getPackageManager().getLaunchIntentForPackage(getPackageName());
    if (o != null) b.setContentIntent(PendingIntent.getActivity(this, 0, o, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT));
    Notification n = b.build();
    if (Build.VERSION.SDK_INT >= 29) startForeground(7, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
    else startForeground(7, n);
    try {
      if (wl == null) { wl = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "chat:wl"); wl.acquire(); }
      if (wf == null) { wf = ((WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "chat:wf"); wf.acquire(); }
    } catch (Exception e) {}
    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    try { if (wl != null) wl.release(); if (wf != null) wf.release(); } catch (Exception e) {}
    super.onDestroy();
  }
}
