package ru.nasvyazi.stepcounter.types;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.bridge.Callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class Request{

  public List<Boolean> rationaleStatuses = null;
  public Callback callback = null;
  public Integer mRequestCode = null;

  public Request(List<Boolean> rationaleStatuses, Integer mRequestCode, Callback callback){
    this.rationaleStatuses = rationaleStatuses;
    this.callback = callback;
    this.mRequestCode = mRequestCode;
  }
}
