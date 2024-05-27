package ru.nasvyazi.stepcounter.tools;

import java.util.Calendar;

public class DayInfo {
  public int year;
  public int month;
  public int day;
  public int hour;
  public int minutes;
  public int seconds;
  public int milliseconds;

  public DayInfo(int year, int month, int day, int hour, int minutes, int seconds, int milliseconds){
    this.year = year;
    this.month = month;
    this.day = day;
    this.hour = hour;
    this.minutes = minutes;
    this.seconds = seconds;
    this.milliseconds = milliseconds;
  }

  public DayInfo(Calendar calendar){
    this.year = calendar.get(Calendar.YEAR);
    this.month = calendar.get(Calendar.MONTH);
    this.day = calendar.get(Calendar.DAY_OF_MONTH);
    this.hour = calendar.get(Calendar.HOUR_OF_DAY);
    this.minutes = calendar.get(Calendar.MINUTE);
    this.seconds = calendar.get(Calendar.SECOND);
    this.milliseconds = calendar.get(Calendar.MILLISECOND);
  }

  public DayInfo startOfDay(){
    return new DayInfo(this.year, this.month, this.day, 0,0,0,0);
  }

  public DayInfo endOfDay(){
    return new DayInfo(this.year, this.month, this.day, 23,59,59,999);
  }

  public long toLong() {
    Calendar cal = Calendar.getInstance();
    this.fillCalendar(cal);
    return cal.getTime().getTime();
  }

  public void fillCalendar(Calendar cal){
    cal.set(Calendar.YEAR, this.year);
    cal.set(Calendar.MONTH, this.month);
    cal.set(Calendar.DAY_OF_MONTH, this.day);
    cal.set(Calendar.HOUR_OF_DAY, this.hour);
    cal.set(Calendar.MINUTE, this.minutes);
    cal.set(Calendar.SECOND, this.seconds);
    cal.set(Calendar.MILLISECOND, this.milliseconds);
  }
}
