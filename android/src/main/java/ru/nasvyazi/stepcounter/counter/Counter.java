package ru.nasvyazi.stepcounter.counter;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import java.util.Calendar;

import ru.nasvyazi.stepcounter.db.StepsRecordDetailedRepository;
import ru.nasvyazi.stepcounter.tools.DBTools;
import ru.nasvyazi.stepcounter.tools.DatesTools;
import ru.nasvyazi.stepcounter.tools.DayBorders;
import ru.nasvyazi.stepcounter.tools.DayInfo;

public class Counter implements SensorEventListener {

  private SensorManager sensorManager = null;
  private Context context = null;
  private Integer currentSteps = null;
  private DBTools dbTools = null;
  private long currentDay = 0;

  public static Boolean isSupported(Context context){
    try{
      SensorManager sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
      Sensor stepsSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

      return stepsSensor != null;
    }catch(Exception err){
      return false;
    }
  }

  public Counter(Context context){
    this.context = context;
    this.dbTools = new DBTools(context);
    this.sensorManager = (SensorManager)this.context.getSystemService(Context.SENSOR_SERVICE);

    Sensor stepsSensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

    this.sensorManager.registerListener(this, stepsSensor, SensorManager.SENSOR_DELAY_UI, SensorManager.SENSOR_DELAY_UI);
  }

  void clearPreviousData(){

    long currentTime = DatesTools.getCurrentTimeInMs();
    if (!DatesTools.isSameDay(currentTime, this.currentDay)){
      this.currentDay = currentTime;
      DayBorders borders = DatesTools.getDayBorders(currentTime);
      StepsRecordDetailedRepository repository = new StepsRecordDetailedRepository(this.context);
      repository.deleteEarlyThan(borders.start);
    }
  }

  @Override
  public void onSensorChanged(SensorEvent event) {

      this.clearPreviousData();

      Integer newSteps = Math.round(event.values[0]);
      if (currentSteps == null){
        currentSteps = newSteps;
      } else {
        Integer diff = newSteps - currentSteps;
        if (diff > 0){
          currentSteps = newSteps;

          this.dbTools.saveNewSteps(diff);

          Intent intent = new Intent(CounterReceiver.ON_STEPS_INTENT);
          intent.putExtra(CounterReceiver.STEPS_PARAM, diff);
          this.context.sendBroadcast(intent);
        }
      }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {

  }

  public void stop() {
    this.sensorManager.unregisterListener(this);
  }
}
