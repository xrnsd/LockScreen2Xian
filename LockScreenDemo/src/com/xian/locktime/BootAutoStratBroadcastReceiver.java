package com.xian.locktime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * action:监听开机自启动
 * 
 * project: LockScreenDemo
 * Package: com.xian.locktime
 * ClassName: BootAutoStratBroadcastReceiver
 * created:wgx
 * date: 20170718
 * version:
 * remark:
 * 
 */
public class BootAutoStratBroadcastReceiver extends BroadcastReceiver {  
  private static final String TAG = "BootAutoStratBroadcastReceiver";
  @Override  
  public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
              Intent service = new Intent(context,ScreenStateListenerService.class);
              context.startService(service);
              LockActivity.Utils.d(TAG, "start  ScreenStateListenerService");
      }
  }
} 
