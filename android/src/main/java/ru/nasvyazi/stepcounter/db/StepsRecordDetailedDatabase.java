package ru.nasvyazi.stepcounter.db;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.Update;

import java.io.Serializable;
import java.util.List;

@Database(entities = {StepsRecordDetailed.class}, version = 1, exportSchema = false)
public abstract class StepsRecordDetailedDatabase extends RoomDatabase {

  public abstract StepsRecordDetailedDao dao();
}
