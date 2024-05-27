package ru.nasvyazi.stepcounter.db;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.Update;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class StepsRecordRepository {

  private String DB_NAME = "db_steps_records";

  private StepsRecordDatabase database;

  public StepsRecordRepository(Context context) {
    database = Room.databaseBuilder(context, StepsRecordDatabase.class, DB_NAME).build();
  }

  public void deleteAllTables() {
    database.clearAllTables();
  }

  public void insert(long date, Integer stepsCount) {

    StepsRecord record = new StepsRecord();
    record.setDate(date);
    record.setStepsCount(stepsCount);

    insert(record);
  }

  public void insert(final StepsRecord record) {
    ExecutorService executor = Executors.newSingleThreadExecutor();

    executor.execute(() -> {
      database.dao().insert(record);
    });
  }

  public void update(final StepsRecord record) {
    ExecutorService executor = Executors.newSingleThreadExecutor();

    executor.execute(() -> {
      database.dao().update(record);
    });
  }

  public void delete(final int id) {
    final LiveData<StepsRecord> record = get(id);

    ExecutorService executor = Executors.newSingleThreadExecutor();

    if (record != null) {
      executor.execute(() -> {
        database.dao().delete(record.getValue());
      });
    }
  }

  public void delete(final StepsRecord record) {
    ExecutorService executor = Executors.newSingleThreadExecutor();

    executor.execute(() -> {
      database.dao().delete(record);
    });
  }


  public void clearDB() {
    database.dao().clear();
  }

  public LiveData<StepsRecord> get(int id) {
    return database.dao().get(id);
  }

  public LiveData<List<StepsRecord>> get(long dateStart, long dateEnd) {
    return database.dao().get(dateStart, dateEnd);
  }

  public LiveData<List<StepsRecord>> fetchAll() {
    return database.dao().fetchAll();
  }

  public void deleteEarlyThan(long date){
    ExecutorService executor = Executors.newSingleThreadExecutor();

    executor.execute(() -> {
      database.dao().deleteEarlyThan(date);
    });
  }
}
