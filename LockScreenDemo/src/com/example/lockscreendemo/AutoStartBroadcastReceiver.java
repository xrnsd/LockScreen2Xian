package com.example.lockscreendemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//开机自启动广播接受  
public class AutoStartBroadcastReceiver extends BroadcastReceiver {  
  private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
  @Override  
  public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals(ACTION)) {
          android.util.Log.d("123456", "AutoStartBroadcastReceiver_onReceive");
              Intent service = new Intent(context,RegisterService.class);    
              context.startService(service);
      }
  }
} 
