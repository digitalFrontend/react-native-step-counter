package ru.nasvyazi.stepcounter.counter;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import java.util.Calendar;
import java.util.Objects;

import ru.nasvyazi.stepcounter.db.StepsRecordDetailedRepository;
import ru.nasvyazi.stepcounter.interfaces.IOnGetCount;
import ru.nasvyazi.stepcounter.tools.DBTools;
import ru.nasvyazi.stepcounter.tools.DatesTools;
import ru.nasvyazi.stepcounter.tools.DayBorders;
import ru.nasvyazi.stepcounter.tools.DayInfo;

public class CounterReceiver extends BroadcastReceiver {

  public static String ON_STEPS_INTENT = "ru.nasvyazi.stepcounter.counter.ON_STEPS_INTENT";
  public static String STEPS_PARAM = "ru.nasvyazi.stepcounter.counter.STEPS_PARAM";

  private IOnGetCount mCompletion;

  public CounterReceiver(Context context, IOnGetCount completion){
    mCompletion = completion;
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    if (Objects.equals(intent.getAction(), ON_STEPS_INTENT)){
      int steps = Objects.requireNonNull(intent.getExtras()).getInt(STEPS_PARAM, 0);
      if (mCompletion != null){
        mCompletion.callback(steps);
      }
    }
  }
}
