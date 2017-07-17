package com.example.lockscreendemo;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
  
public class RegisterService extends Service {
    private static final String TAG = "123456";

    private BroadcastReceiver mBatInfoReceiver;

    @Override  
    public IBinder onBind(Intent intent) {
        return null;  
    }  
  
    @Override  
    public void onCreate() {  
        super.onCreate();  
        initScreenStateReceiver();
    }

    @Override  
    public void onDestroy() {  
        super.onDestroy();  
        //½â³ý×¢²á  
        unregisterReceiver(mBatInfoReceiver);  
    }  

    private void initScreenStateReceiver(){
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
      
        mBatInfoReceiver = new BroadcastReceiver() {
            @Override  
            public void onReceive(final Context context, final Intent intent) {  
                Log.d(TAG, "onReceive");
                String action = intent.getAction();
                if (Intent.ACTION_SCREEN_OFF.equals(action)) {  
                    Log.d(TAG, "screen off");
                  new Handler().postDelayed(new Runnable(){    
                      public void run() {
                          Intent newIntent = context.getPackageManager()
                                  .getLaunchIntentForPackage(getPackageName());
                          context.startActivity(newIntent);
                      }
                   }, 1000);

                }
            }  
        };
        Log.d(TAG, "registerReceiver");  
        registerReceiver(mBatInfoReceiver, filter);  
    }
    
}
