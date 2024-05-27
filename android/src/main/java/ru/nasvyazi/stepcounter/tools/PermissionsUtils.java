package ru.nasvyazi.stepcounter.tools;

import static android.system.Os.remove;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

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

import ru.nasvyazi.stepcounter.interfaces.IOnRequestPermission;
import ru.nasvyazi.stepcounter.types.Request;

public class PermissionsUtils implements PermissionListener {

  private final List<Request> mRequests = new ArrayList<>();
  private PermissionAwareActivity permissionAwareActivity = null;
  private Integer mRequestCode = 0;


  public Boolean isNeedRequestPermission(String permission, Context context) {
    return ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
  }

  public void requestPermission(String permission, Activity activity, IOnRequestPermission handler){
    try {
      this.permissionAwareActivity = (PermissionAwareActivity)activity;
      List<Boolean> rationaleStatuses = new ArrayList<>();
      rationaleStatuses.add(activity.shouldShowRequestPermissionRationale(permission));

      mRequests.add(new Request(rationaleStatuses, mRequestCode, new Callback() {
        @Override
        public void invoke(Object... args) {
          handler.callback((boolean)args[0]);
        }
      }));

      this.permissionAwareActivity.requestPermissions(new String[] {permission}, mRequestCode, this);
      this.mRequestCode = this.mRequestCode + 1;
    } catch (Exception error) {
      Log.i("StepCounter", error.getLocalizedMessage());
      handler.callback(false);
    }
  }

  @Override
  public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    try {
      Optional<Request> request = this.mRequests.stream().filter(request1 -> request1.mRequestCode == requestCode).findFirst();

      if (request.isPresent()){
        Request _request = request.get();
        _request.callback.invoke(Arrays.stream(grantResults).allMatch(value -> value == PackageManager.PERMISSION_GRANTED));
        return true;
      } else {
        return false;
      }
    } catch (Exception error) {
      Log.i("StepCounter", error.getLocalizedMessage());
      return false;
    }
  }
}
