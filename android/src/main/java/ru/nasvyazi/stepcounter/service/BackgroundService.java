package ru.nasvyazi.stepcounter.service;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import ru.nasvyazi.stepcounter.R;
import ru.nasvyazi.stepcounter.counter.Counter;


public class BackgroundService extends Service {
  private Looper serviceLooper;
  private ServiceHandler serviceHandler;
  private Counter counter = null;


  private final class ServiceHandler extends Handler {

    private Context mContext;
    public ServiceHandler(Looper looper, Context context) {
      super(looper);
      mContext = context;
    }
    @SuppressLint("WrongConstant")
    @Override
    public void handleMessage(Message msg) {

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        String CHANNEL_ID = "my_channel_01";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
          "Channel human readable title",
          NotificationManager.IMPORTANCE_DEFAULT);

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

        Integer appIcon = null;

        try {
          Resources res = this.mContext.getResources();
          appIcon = res.getIdentifier("push_notification_icon", "drawable", this.mContext.getPackageName());
        } catch (Exception e1) {
          Log.i("StepCounter", e1.getLocalizedMessage());
        }
        Notification notification = new NotificationCompat.Builder(mContext, CHANNEL_ID)
          .setContentTitle("Ведем подсчет!")
          .setSmallIcon(appIcon)
          .setContentText("Ни один ваш шаг не будет пропущен! Постарайтесь!").build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          startForeground(1, notification, FOREGROUND_SERVICE_TYPE_HEALTH);
        } else {
          startForeground(1, notification);
        }
      }
    }
  }




  @Override
  public void onCreate() {
    HandlerThread thread = new HandlerThread("ServiceStartArguments",
      Process.THREAD_PRIORITY_BACKGROUND);
    thread.start();

    serviceLooper = thread.getLooper();
    serviceHandler = new ServiceHandler(serviceLooper, this);

    counter = new Counter(this);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.i("StepCounterService", "Service onStartCommand");

    Message msg = serviceHandler.obtainMessage();
    msg.arg1 = startId;
    serviceHandler.sendMessage(msg);

    return START_STICKY;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onDestroy() {
    this.counter.stop();
    Log.i("StepCounterService", "Service destroyed");
  }
}
