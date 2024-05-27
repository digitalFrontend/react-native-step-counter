package ru.nasvyazi.stepcounter.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;

@Entity
public class StepsRecord implements Serializable {

  @PrimaryKey(autoGenerate = true)
  private int id;

  private long date;
  private Integer stepsCount;



  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public long getDate() {
    return date;
  }

  public void setDate(long date) {
    this.date = date;
  }

  public Integer getStepsCount() {
    return stepsCount;
  }

  public void setStepsCount(Integer stepsCount) {
    this.stepsCount = stepsCount;
  }
}
