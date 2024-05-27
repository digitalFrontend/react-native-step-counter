package ru.nasvyazi.stepcounter;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Base64;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.security.Key;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import static android.content.Context.MODE_PRIVATE;
import static android.os.Looper.getMainLooper;

import static java.lang.System.currentTimeMillis;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import ru.nasvyazi.stepcounter.counter.Counter;
import ru.nasvyazi.stepcounter.counter.CounterReceiver;
import ru.nasvyazi.stepcounter.db.StepsRecord;
import ru.nasvyazi.stepcounter.db.StepsRecordDetailed;
import ru.nasvyazi.stepcounter.db.StepsRecordDetailedRepository;
import ru.nasvyazi.stepcounter.db.StepsRecordRepository;
import ru.nasvyazi.stepcounter.interfaces.IOnGetCount;
import ru.nasvyazi.stepcounter.interfaces.IOnRequestPermission;
import ru.nasvyazi.stepcounter.service.BackgroundService;
import ru.nasvyazi.stepcounter.service.BackgroundServiceRunner;
import ru.nasvyazi.stepcounter.tools.DBTools;
import ru.nasvyazi.stepcounter.tools.DatesTools;
import ru.nasvyazi.stepcounter.tools.DayBorders;
import ru.nasvyazi.stepcounter.tools.PermissionsUtils;

public class StepCounterModule extends ReactContextBaseJavaModule {

  private final String PERMISSION_FOR_WORK = Manifest.permission.ACTIVITY_RECOGNITION;
  private final ReactApplicationContext reactContext;
  private PermissionsUtils permissionsUtils;
  private DBTools dbTools;
  private CounterReceiver receiver;
  private Boolean shouldSendEvents = false;


  @SuppressLint("RestrictedApi")
  public StepCounterModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    this.permissionsUtils = new PermissionsUtils();
    this.dbTools = new DBTools(this.reactContext);
    this.receiver = new CounterReceiver(this.reactContext, this.onSteps);
    IntentFilter filter = new IntentFilter();
    filter.addAction(CounterReceiver.ON_STEPS_INTENT);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      reactContext.registerReceiver(this.receiver, filter, Context.RECEIVER_EXPORTED);
    } else {
      reactContext.registerReceiver(this.receiver, filter);
    }
  }


  @Override
  public String getName() {
    return "StepCounterModule";
  }

  private IOnGetCount onSteps = new IOnGetCount() {
    @Override
    public void callback(Integer count) {
      if (shouldSendEvents) {
        WritableMap params = Arguments.createMap();
        params.putInt("steps", count);
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("onStepsChanged", params);
      }
    }
  };

  @ReactMethod
  public void isStepCountingSupported(final Promise promise) {
    WritableMap map = new WritableNativeMap();
    map.putBoolean("granted", !this.permissionsUtils.isNeedRequestPermission(PERMISSION_FOR_WORK, this.reactContext.getCurrentActivity()));
    map.putBoolean("supported", Counter.isSupported(this.reactContext));

    promise.resolve(map);
  }

  @ReactMethod
  public void askPermissions(final Promise promise) {
    this.permissionsUtils.requestPermission(PERMISSION_FOR_WORK, this.reactContext.getCurrentActivity(), new IOnRequestPermission() {
      @Override
      public void callback(Boolean isSuccess) {
        promise.resolve(isSuccess);
      }
    });
  }

  @ReactMethod
  public void queryStepCounterDataBetweenDates(double startDateDouble, double endDateDouble, final Promise promise) {
    new Handler(Looper.getMainLooper()).post(() -> {
      long startDate = (long) startDateDouble;
      long endDate = (long) endDateDouble;

      this.dbTools.getStepsByDates(startDate, endDate, count -> {
        WritableMap map = new WritableNativeMap();
        map.putInt("steps", count);
        promise.resolve(map);
      });
    });
  }

  @ReactMethod
  public void isActive(final Promise promise) {
    promise.resolve(this.shouldSendEvents);
  }

  @ReactMethod
  public void startStepCounterUpdate() {
    this.shouldSendEvents = true;
  }

  @ReactMethod
  public void stopStepCounterUpdate() {
    this.shouldSendEvents = false;
  }


  @ReactMethod
  public void startBackgroundService(final Promise promise) {
    try {
      BackgroundServiceRunner.StartService(this.reactContext);
      promise.resolve(null);
    } catch (Exception e) {
      promise.reject("StepCounter", e.getLocalizedMessage());
    }
  }

  @ReactMethod
  public void stopBackgroundService() {
    BackgroundServiceRunner.StopService(this.reactContext);
  }

  @ReactMethod
  public void isActiveBackgroundService(final Promise promise) {
    promise.resolve(BackgroundServiceRunner.isMyServiceRunning(this.reactContext, BackgroundService.class));
  }

  @ReactMethod
  public void dropDBs() {
    StepsRecordRepository repository2 = new StepsRecordRepository(this.reactContext);
    repository2.clearDB();
    StepsRecordDetailedRepository repository3 = new StepsRecordDetailedRepository(this.reactContext);
    repository3.clearDB();
  }

  @ReactMethod
  public void getAllDetailedRecords(final Promise promise) {
    new Handler(Looper.getMainLooper()).post(() -> {
      StepsRecordDetailedRepository repository1 = new StepsRecordDetailedRepository(this.reactContext);
      LiveData<List<StepsRecordDetailed>> recordsData1 = repository1.fetchAll();

      Observer<List<StepsRecordDetailed>> observer1 = new Observer<List<StepsRecordDetailed>>() {
        @Override
        public void onChanged(@Nullable final List<StepsRecordDetailed> record) {
          WritableArray array = new WritableNativeArray();
          if (record != null) {
            record.forEach((rec) -> {
              WritableMap map = new WritableNativeMap();
              map.putInt("id", rec.getId());
              map.putDouble("dateDouble", rec.getDate());
              map.putString("date", DatesTools.formatDate(rec.getDate()));
              map.putInt("steps", rec.getStepsCount());
              array.pushMap(map);
            });
            promise.resolve(array);
          } else {
            promise.resolve(new WritableNativeArray());
          }
        }
      };
      recordsData1.observeForever(observer1);
    });
  }

  @ReactMethod
  public void getAllRecords(final Promise promise) {

    new Handler(Looper.getMainLooper()).post(() -> {
      StepsRecordRepository repository1 = new StepsRecordRepository(this.reactContext);
      LiveData<List<StepsRecord>> recordsData1 = repository1.fetchAll();

      Observer<List<StepsRecord>> observer1 = new Observer<List<StepsRecord>>() {
        @Override
        public void onChanged(@Nullable final List<StepsRecord> record) {
          WritableArray array = new WritableNativeArray();
          if (record != null) {
            record.forEach((rec) -> {
              WritableMap map = new WritableNativeMap();
              map.putInt("id", rec.getId());
              map.putDouble("dateDouble", rec.getDate());
              map.putString("date", DatesTools.formatDate(rec.getDate()));
              map.putInt("steps", rec.getStepsCount());
              array.pushMap(map);
            });
            promise.resolve(array);
          } else {
            promise.resolve(new WritableNativeArray());
          }
        }
      };
      recordsData1.observeForever(observer1);
    });
  }


}



