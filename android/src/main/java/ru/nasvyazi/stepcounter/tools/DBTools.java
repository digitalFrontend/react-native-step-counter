package ru.nasvyazi.stepcounter.tools;

import static android.system.Os.remove;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ru.nasvyazi.stepcounter.db.StepsRecord;
import ru.nasvyazi.stepcounter.db.StepsRecordDetailed;
import ru.nasvyazi.stepcounter.db.StepsRecordDetailedRepository;
import ru.nasvyazi.stepcounter.db.StepsRecordRepository;
import ru.nasvyazi.stepcounter.interfaces.IOnGetCount;
import ru.nasvyazi.stepcounter.interfaces.IOnRequestPermission;
import ru.nasvyazi.stepcounter.interfaces.IOnStepsProcessed;
import ru.nasvyazi.stepcounter.types.Request;

public class DBTools  {
  private Context context = null;

  public DBTools(Context context) {
    this.context = context;
  }

  private List<Integer> queue = new ArrayList<>();
  private Boolean isQueueRunning = false;

  private void processQueueItem(int stepsCount, IOnStepsProcessed handler){
    long currentDateInMs = DatesTools.getCurrentTimeInMs();
    DayBorders currentDateBorders = DatesTools.getDayBorders(currentDateInMs);

    StepsRecordDetailedRepository detailedRepository = new StepsRecordDetailedRepository(this.context);
    detailedRepository.insert(currentDateInMs, stepsCount);

    StepsRecordRepository repository = new StepsRecordRepository(this.context);
    LiveData<List<StepsRecord>> recordsData = repository.get(currentDateBorders.start, currentDateBorders.end);

    new Handler(Looper.getMainLooper()).post(() -> {
      Observer<List<StepsRecord>> observer = new Observer<List<StepsRecord>>() {
        @Override
        public void onChanged(@Nullable final List<StepsRecord> records) {
          if (records == null || records.isEmpty()){
            repository.insert(currentDateBorders.start, stepsCount);
            handler.callback();
          } else {
            StepsRecord record = records.get(0);
            record.setStepsCount(record.getStepsCount() + stepsCount);
            repository.update(record);
            handler.callback();
          }

          recordsData.removeObserver(this);
        }
      };
      recordsData.observeForever(observer);
    });
  }

  private void runQueue(){
    if (isQueueRunning){
      return;
    }

    if (queue.isEmpty()){
      return;
    }

    isQueueRunning = true;

    int steps = queue.get(0);
    queue.remove(0);

    processQueueItem(steps, () -> {
      isQueueRunning = false;
      runQueue();
    });
  }

  public void saveNewSteps(Integer stepsCount) {
    queue.add(stepsCount);
    runQueue();
  }

  private void getCountDetailedByDates(long startDate, long endDate, IOnGetCount handler){
    StepsRecordDetailedRepository repository = new StepsRecordDetailedRepository(this.context);
    LiveData<List<StepsRecordDetailed>> recordsData = repository.get(startDate, endDate);

    Observer<List<StepsRecordDetailed>> observer = new Observer<List<StepsRecordDetailed>>() {
      @Override
      public void onChanged(@Nullable final List<StepsRecordDetailed> records) {
        handler.callback(Tools.countRecordsDetailed(records));
        recordsData.removeObserver(this);
      }
    };
    recordsData.observeForever(observer);
  }

  private void getCountByDates(long startDate, long endDate, IOnGetCount handler){
    StepsRecordRepository repository = new StepsRecordRepository(this.context);
    LiveData<List<StepsRecord>> recordsData = repository.get(startDate, endDate);

    Observer<List<StepsRecord>> observer = new Observer<List<StepsRecord>>() {
      @Override
      public void onChanged(@Nullable final List<StepsRecord> records) {
        handler.callback(Tools.countRecords(records));
        recordsData.removeObserver(this);
      }
    };
    recordsData.observeForever(observer);
  }

  public void getStepsByDates(long startDate, long endDate, IOnGetCount handler) {
    DayBorders startDateBorders = DatesTools.getDayBorders(startDate);
    DayBorders endDateBorders = DatesTools.getDayBorders(endDate);
    long currentTime = DatesTools.getCurrentTimeInMs();
    DayBorders currentTimeBorders = DatesTools.getDayBorders(currentTime);

    if (currentTimeBorders.start <= startDateBorders.start){
      getCountDetailedByDates(currentTimeBorders.start, currentTimeBorders.end, handler);
    } else {
      Boolean countToday = endDateBorders.end >= currentTimeBorders.end;

      getCountByDates(startDateBorders.start, countToday ? DatesTools.addDays(currentTimeBorders.start, -1) : endDateBorders.end, count -> {
        if (countToday){
          getCountDetailedByDates(currentTimeBorders.start, endDate, todayCounter -> {
            handler.callback(todayCounter + count);
          });
        } else {
          handler.callback(count);
        }
      });

    }
  }
}
