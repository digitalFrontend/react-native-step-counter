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
public interface StepsRecordDao {

  @Insert
  Long insert(StepsRecord record);


  @Query("SELECT * FROM StepsRecord")
  LiveData<List<StepsRecord>> fetchAll();

  @Query("DELETE FROM StepsRecord")
  void clear();


  @Query("SELECT * FROM StepsRecord WHERE id =:id")
  LiveData<StepsRecord> get(int id);


  @Query("SELECT * FROM StepsRecord WHERE date >=:dateStart AND date <:dateEnd")
  LiveData<List<StepsRecord>> get(long dateStart, long dateEnd);

  @Query("DELETE from StepsRecord where date < :date")
  void deleteEarlyThan(long date);

  @Update
  void update(StepsRecord record);


  @Delete
  void delete(StepsRecord record);
}
