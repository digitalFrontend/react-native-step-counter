package ru.nasvyazi.stepcounter.db;

import android.content.Context;

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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class StepsRecordDetailedRepository {

  private String DB_NAME = "db_steps_records_detailed";

  private StepsRecordDetailedDatabase database;

  public StepsRecordDetailedRepository(Context context) {
    database = Room.databaseBuilder(context, StepsRecordDetailedDatabase.class, DB_NAME).build();
  }

  public void deleteAllTables() {
    database.clearAllTables();
  }

  public void insert(long date, Integer stepsCount) {

    StepsRecordDetailed record = new StepsRecordDetailed();
    record.setDate(date);
    record.setStepsCount(stepsCount);

    insert(record);
  }

  public void insert(final StepsRecordDetailed record) {
    ExecutorService executor = Executors.newSingleThreadExecutor();

    executor.execute(() -> {
      database.dao().insert(record);
    });
  }

  public void update(final StepsRecordDetailed record) {
    ExecutorService executor = Executors.newSingleThreadExecutor();

    executor.execute(() -> {
      database.dao().update(record);
    });
  }

  public void delete(final int id) {
    final LiveData<StepsRecordDetailed> record = get(id);

    ExecutorService executor = Executors.newSingleThreadExecutor();

    if (record != null) {
      executor.execute(() -> {
        database.dao().delete(record.getValue());
      });
    }
  }

  public void delete(final StepsRecordDetailed record) {
    ExecutorService executor = Executors.newSingleThreadExecutor();

    executor.execute(() -> {
      database.dao().delete(record);
    });
  }


  public void clearDB() {
    database.dao().clear();
  }

  public LiveData<StepsRecordDetailed> get(int id) {
    return database.dao().get(id);
  }

  public LiveData<List<StepsRecordDetailed>> get(long dateStart, long dateEnd) {
    return database.dao().get(dateStart, dateEnd);
  }

  public LiveData<List<StepsRecordDetailed>> fetchAll() {
    return database.dao().fetchAll();
  }

  public void deleteEarlyThan(long date){
    ExecutorService executor = Executors.newSingleThreadExecutor();

    executor.execute(() -> {
      database.dao().deleteEarlyThan(date);
    });
  }
}
