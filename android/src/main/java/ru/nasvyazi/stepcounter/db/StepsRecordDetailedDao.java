package ru.nasvyazi.stepcounter.db;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import java.io.Serializable;
import java.util.List;

@Dao
public interface StepsRecordDetailedDao {

  @Insert
  Long insert(StepsRecordDetailed record);


  @Query("SELECT * FROM StepsRecordDetailed")
  LiveData<List<StepsRecordDetailed>> fetchAll();

  @Query("DELETE FROM StepsRecordDetailed")
  void clear();


  @Query("SELECT * FROM StepsRecordDetailed WHERE id =:id")
  LiveData<StepsRecordDetailed> get(int id);


  @Query("SELECT * FROM StepsRecordDetailed WHERE date >=:dateStart AND date <:dateEnd")
  LiveData<List<StepsRecordDetailed>> get(long dateStart, long dateEnd);

  @Query("DELETE from StepsRecordDetailed where date < :date")
  void deleteEarlyThan(long date);

  @Update
  void update(StepsRecordDetailed record);


  @Delete
  void delete(StepsRecordDetailed record);
}
