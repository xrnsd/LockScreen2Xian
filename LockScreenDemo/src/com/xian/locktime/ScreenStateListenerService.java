package com.xian.locktime;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
  
public class ScreenStateListenerService extends Service {
    private static final String TAG = "ScreenStateListenerService";
    public static final String KEY_BOOT_BY_SERVICE="beActivatedBy";
    private static boolean isRegistered=false;
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }  

    @Override
    public void onDestroy() {
        super.onDestroy();
        LockActivity.Utils.d(TAG, "unregisterReceiver");
        unregisterReceiver(mBatInfoReceiver);
        isRegistered=false;
    }

    private boolean isLockScreen(Context context){
        KeyguardManager sKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return sKeyguardManager.isKeyguardLocked();
    }

    private void initScreenStateReceiver(){
        if(isRegistered)
            return;
        final String actionTraget=Intent.ACTION_SCREEN_ON;

        mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                LockActivity.Utils.d(TAG, "mBatInfoReceiver onReceive");
                String action = intent.getAction();
                if (actionTraget.equals(action)) {
                    LockActivity.Utils.d(TAG, "screen state change : "+action);
                    if(isLockScreen(context)){
                        Intent newIntent = context.getPackageManager()
                                .getLaunchIntentForPackage(getPackageName());
                        newIntent.putExtra(KEY_BOOT_BY_SERVICE, TAG);
                        context.startActivity(newIntent);
                        LockActivity.Utils.d(TAG, "start Activity by "+getPackageName());
                    }
                }
            }
        };
        final IntentFilter filter = new IntentFilter();
        filter.addAction(actionTraget);
        registerReceiver(mBatInfoReceiver, filter);
        isRegistered=true;
        LockActivity.Utils.d(TAG, "registerReceiver");
    }
}
