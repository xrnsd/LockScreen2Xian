package com.example.lockscreendemo;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class LockActivity extends Activity {
    private static final String TAG = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES
                |WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

        getWindow().addFlags(flags);
        
        WindowManager.LayoutParams params = getWindow().getAttributes();    
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;    
        getWindow().setAttributes(params);
        
        initUiState(getApplicationContext());
        setContentView(R.layout.activity_main);
        initTextTime(getApplicationContext(),R.id.tv_time,0);
    }
    
    private void initTextTime(Context context,int control_id,int style_id){
        android.util.Log.d("123456", "LockActivity_initTextTime");
        if(control_id<=0){
            Utils.e(TAG, "initTextTime fail control_id="+control_id);
           return;
        }
        
//      String fontFilePath="fonts/486.ttf";
//      String fontFilePath="fonts/digit.ttf";
        String fontFilePath="fonts/digigraphics.ttf";
        Typeface fontFace = Typeface.createFromAsset(getAssets(),fontFilePath);

        TextView text = (TextView) findViewById(control_id);
        if(style_id<=0)
            Utils.e(TAG, "initTextTime style fail style_id="+style_id);
         else
             text.setTextAppearance(getApplicationContext(), style_id);
        text.setText("23 :01");
        text.setTypeface(fontFace);
    }

    private void initUiState(Context context) {
        android.util.Log.d("123456", "LockActivity_initUiState");
        
        PowerManager sPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        KeyguardManager sKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if(sPowerManager.isScreenOn()&&!sKeyguardManager.isKeyguardLocked())
            finish();

          Intent service = new Intent(context,RegisterService.class);    
          context.startService(service);
//          android.util.Log.d("123456", "LockActivity_initUiState startService");
        
         
//         policyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
//         componentName = new ComponentName(this, MyDeviceAdminReceiver.class);
//         if (!policyManager.isAdminActive(componentName)) {
//             goSetActivity();
//         } else {
//             //锁屏的时候显示界面
//             PowerManager sPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//             KeyguardManager sKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
//             if(sPowerManager.isScreenOn()&&!sKeyguardManager.isKeyguardLocked()){
//                 finish();
//             }else{
//                 Intent service = new Intent(context,RegisterService.class);    
//                 context.startService(service);
//                 new Handler().postDelayed(new Runnable(){    
//                     public void run() {    
//                         systemLock();
//                     }
//                  }, 5000);
//             }
//         }
    }
    
    DevicePolicyManager policyManager;
    ComponentName componentName;
    
//    private void goSetActivity() {
//        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
//        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
//        startActivityForResult(intent, 1);
//    }
//
//    /**
//     * 锁屏并关闭屏幕
//     */
//    private void systemLock() {
//        if (policyManager.isAdminActive(componentName)) {
//            Window localWindow = getWindow();
//            WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
//            localLayoutParams.screenBrightness = 0.05F;
//            localWindow.setAttributes(localLayoutParams);
//            policyManager.lockNow();
//        }
//        finish();
//    }

    public  static class Utils{
        private final static boolean IS_DEBUG=true;
        public static void e(String TAG,String content) {
            if(IS_DEBUG)
                android.util.Log.e(TAG,content);
        }
        public static void d(String TAG,String content) {
            if(IS_DEBUG)
                android.util.Log.d(TAG,content);
        }
    }
}
