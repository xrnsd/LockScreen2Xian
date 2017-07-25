package com.xian.locktime;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class ScreenStateListenerService extends NotificationListenerService {
    private static final String TAG = "ScreenStateListenerService";
    public static final String KEY_BOOT_BY_SERVICE = "boot_by_"+TAG;
    private static boolean isRegistered=false;
    private BroadcastReceiver mBatInfoReceiver;

    @Override  
    public IBinder onBind(Intent intent) {
        LockActivity.Utils.d(TAG, "onBind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LockActivity.Utils.d(TAG, "onCreate");
        initScreenStateReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        LockActivity.Utils.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBatInfoReceiver);
        LockActivity.Utils.d(TAG, "unregisterReceiver");
        isRegistered=false;
    }

    private boolean isLockScreen(Context context){
        KeyguardManager sKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return sKeyguardManager.isKeyguardLocked();
    }

    private void initScreenStateReceiver(){
        if(isRegistered)
            return;

        mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                LockActivity.Utils.d(TAG, "mBatInfoReceiver onReceive");
                String action = intent.getAction();
                if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    LockActivity.Utils.d(TAG, "screen state change : "+action);
                    if(isLockScreen(context)){
                        Intent newIntent = context.getPackageManager()
                                .getLaunchIntentForPackage(getPackageName());
                        newIntent.putExtra(KEY_BOOT_BY_SERVICE, true);
                        context.startActivity(newIntent);
                        LockActivity.Utils.d(TAG, "start Activity by "+getPackageName());
                    }
                }
            }
        };
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mBatInfoReceiver, filter);
        isRegistered=true;
        LockActivity.Utils.d(TAG, "registerReceiver");
    }
    
    

    @Override
    public void onNotificationPosted(StatusBarNotification arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification arg0) {
        // TODO Auto-generated method stub
        
    }
}
