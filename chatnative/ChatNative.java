package com.hotspot.chat;

import android.app.*;
import android.content.*;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;
import org.apache.cordova.*;
import org.json.*;

public class ChatNative extends CordovaPlugin {
  static final String MSG = "chat_msgs", SVC = "chat_svc";

  static void channels(Context c) {
    if (Build.VERSION.SDK_INT < 26) return;
    NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
    nm.createNotificationChannel(new NotificationChannel(MSG, "الرسايل", NotificationManager.IMPORTANCE_HIGH));
    nm.createNotificationChannel(new NotificationChannel(SVC, "التطبيق شغال", NotificationManager.IMPORTANCE_LOW));
  }

  @Override
  public boolean execute(String a, JSONArray g, CallbackContext cb) throws JSONException {
    Context c = cordova.getActivity().getApplicationContext();
    if (a.equals("start")) {
      channels(c);
      Intent i = new Intent(c, KeepService.class);
      if (Build.VERSION.SDK_INT >= 26) c.startForegroundService(i); else c.startService(i);
      cb.success(); return true;
    }
    if (a.equals("notify")) {
      channels(c);
      Intent o = c.getPackageManager().getLaunchIntentForPackage(c.getPackageName());
      NotificationCompat.Builder b = new NotificationCompat.Builder(c, MSG)
        .setSmallIcon(android.R.drawable.stat_notify_chat)
        .setContentTitle(g.optString(0)).setContentText(g.optString(1))
        .setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_HIGH)
        .setDefaults(Notification.DEFAULT_ALL);
      if (o != null) b.setContentIntent(PendingIntent.getActivity(c, 0, o, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT));
      ((NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE)).notify(g.optInt(2), b.build());
      cb.success(); return true;
    }
    if (a.equals("perm")) {
      if (Build.VERSION.SDK_INT >= 33 && !cordova.hasPermission("android.permission.POST_NOTIFICATIONS"))
        cordova.requestPermission(this, 1, "android.permission.POST_NOTIFICATIONS");
      cb.success(); return true;
    }
    if (a.equals("battery")) {
      try {
        Intent i = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + c.getPackageName()));
        cordova.getActivity().startActivity(i);
      } catch (Exception e) {}
      cb.success(); return true;
    }
    if (a.equals("bg")) {
      cordova.getActivity().moveTaskToBack(true);
      cb.success(); return true;
    }
    return false;
  }
}
