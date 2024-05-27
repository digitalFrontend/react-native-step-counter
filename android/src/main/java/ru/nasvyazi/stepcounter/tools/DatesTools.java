package ru.nasvyazi.stepcounter.tools;

import static android.system.Os.remove;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;
import com.facebook.react.uimanager.ViewManager;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import ru.nasvyazi.stepcounter.interfaces.IOnRequestPermission;
import ru.nasvyazi.stepcounter.types.Request;

public class DatesTools {
  public static long getCurrentTimeInMs() {
    Calendar cal = Calendar.getInstance();
    Date date = cal.getTime();
    return date.getTime();
  }

  public static DayBorders getDayBorders(long date){
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(date);

    DayInfo dayInfo = new DayInfo(cal);
    DayInfo startOfDay = dayInfo.startOfDay();
    DayInfo endOfDay = dayInfo.endOfDay();

    startOfDay.fillCalendar(cal);
    long startOfDayInMs = cal.getTime().getTime();
    endOfDay.fillCalendar(cal);
    long endOfDayInMs = cal.getTime().getTime();

    return new DayBorders(startOfDayInMs, endOfDayInMs);
  }

  public static String formatDate(long dateInMs){
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(dateInMs);
    Date date = cal.getTime();
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS").format(date);
  }

  public static long addDays(long date, int days) {
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(date);
    cal.add(Calendar.DATE, days);
    return cal.getTime().getTime();
  }

  public static Boolean isSameDay(long date1InMs, long date2InMs){
    DayBorders date1Borders = getDayBorders(date1InMs);
    DayBorders date2Borders = getDayBorders(date2InMs);
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(date1Borders.start);
    Date date1 = cal.getTime();
    cal.setTimeInMillis(date2Borders.start);
    Date date2 = cal.getTime();
    return date1.compareTo(date2) == 0;
  }
}
