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
import androidx.annotation.Nullable;

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

import ru.nasvyazi.stepcounter.db.StepsRecord;
import ru.nasvyazi.stepcounter.db.StepsRecordDetailed;
import ru.nasvyazi.stepcounter.interfaces.IOnRequestPermission;
import ru.nasvyazi.stepcounter.types.Request;

public class Tools {
  public static int countRecords(@Nullable final List<StepsRecord> records){
    if (records == null || records.isEmpty()){
      return 0;
    } else {
      int counter = 0;
      for (int i = 0; i < records.size(); i++) {
        counter = counter + records.get(i).getStepsCount();
      }
      return counter;
    }
  }
  public static int countRecordsDetailed(@Nullable final List<StepsRecordDetailed> records){
    if (records == null || records.isEmpty()){
      return 0;
    } else {
      int counter = 0;
      for (int i = 0; i < records.size(); i++) {
        counter = counter + records.get(i).getStepsCount();
      }
      return counter;
    }
  }
}
