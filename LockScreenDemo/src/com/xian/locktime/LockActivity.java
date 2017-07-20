package com.xian.locktime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class LockActivity extends Activity {
    public static final String PROJECT_NAME="lockScreen2Xian";
    private static final String TAG = "LockActivity";
    private static final int TIME_LONG = 6000;
    private static int sStyleIdIndex = -1;
    
    private static boolean isShow=false;

    private DevicePolicyManager policyManager;
    private ComponentName componentName;
    private TextView tvTime=null,tvDate=null;
    private  final int[] STYLE_ID_LIST ={
            R.style.font_style_lcd,
            R.style.font_style_valerie_medium,
            R.style.font_style_digigraphics_port,
            R.style.font_style_djb_friday_night_light,
//            R.style.font_style_granite_modern_regular,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

        getWindow().addFlags(flags);

        WindowManager.LayoutParams params = getWindow().getAttributes();    
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;    
        getWindow().setAttributes(params);

        initUiState(getApplicationContext());
        setContentView(R.layout.activity_main); 
    }

    @Override
    protected void onResume() {
        isShow=true;
        super.onResume();
        android.util.Log.d("123456", "LockActivity_onResume");
        initTextTime(getApplicationContext(),getStyleIdByIndex(sStyleIdIndex));
        updateTime(getApplicationContext());
        showLockScreen(getApplicationContext(),TIME_LONG);
    }

    @Override
    protected void onStop() {
        isShow=false;
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        showLockScreen(getApplicationContext(),TIME_LONG);
    }

    private void initUiState(Context context) {
        if(!getIntent().hasExtra(ScreenStateListenerService.KEY_BOOT_BY_SERVICE)){
            Intent service = new Intent(context,ScreenStateListenerService.class);    
            context.startService(service);
            Utils.d(TAG, "initUiState startService");
        }
         policyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
         componentName = new ComponentName(this, SceenCloseDeviceAdminReceiver.class);
         if (!policyManager.isAdminActive(componentName)) {
             goSetActivity();
         }
    }

    private void initTextTime(Context context,int style_id){
        if(tvTime==null||tvDate==null){
            tvDate= (TextView) findViewById(R.id.tv_date);
            tvTime= (TextView) findViewById(R.id.tv_time);  
         }
        if(tvTime==null||tvDate==null){
            Utils.e(TAG," initTextTime fail textview is null" );
            return;
         }
        if(style_id<=0)
            Utils.e(TAG, "initTextTime style fail style_id="+style_id);
         else
             new ControlAttributes(context, style_id).applyStyle(tvDate,tvTime);
    }

    private void updateTime(Context context){
        if(tvTime==null||tvDate==null){ 
            Utils.e(TAG," updateTime fail textview is null" );
            return;
         }
        long time=System.currentTimeMillis();
        SimpleDateFormat formatDate=new SimpleDateFormat("yyyy-MM-dd");
        String strDate=formatDate.format(new Date(time));
        tvDate.setText(strDate);

        SimpleDateFormat formatTime=new SimpleDateFormat("HH mm");
        String strTime=formatTime.format(new Date(time));
        tvTime.setText(strTime);
    }

    private int getStyleIdByIndex(int index){
        index=index>-1&&index<STYLE_ID_LIST.length-1?index+=1:0;
        sStyleIdIndex=index;
        Utils.d(TAG, "getStyleIdByIndex index="+index);
        return STYLE_ID_LIST[index];
    }

    private void showLockScreen(final Context context,final int time_long){
        if(!isLockScreen()||!isShow){
            Utils.d(TAG, "isLockScreen is Non-lock screen closes UI");
            finish();
        }
        Utils.d(TAG, "showLockScreen");
        
        final  int MSG_VAL_CLOSE_SCREEN=10;
        
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_VAL_CLOSE_SCREEN){
                    Utils.d(TAG, "handleMessage closeScreen");
                    closeScreen();
                }
            }
        };
        
         new Thread(new Runnable(){
               public void run(){
                   PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                   try {
                       for(int length=time_long;length>0;length--){
                           if(!pm.isScreenOn()){
                               Utils.d(TAG, "Runnable : The screen closes ahead of time, cancels the operation");
                               return;
                           }
                            Thread.sleep(1);
                       }
                   } catch (InterruptedException e) { 
                       e.printStackTrace();
                       Utils.e(TAG, "Runnable : delay close fail ="+e);
                     }
                 handler.sendEmptyMessage(MSG_VAL_CLOSE_SCREEN);
                 Utils.d(TAG, "Runnable : sendEmptyMessage MSG_VAL_CLOSE_SCREEN ="+MSG_VAL_CLOSE_SCREEN);
                 }    
           }).start();
    }

    private void closeScreen() {
        if(!isLockScreen()||!isShow)
            return;
        Utils.d(TAG, "closeScreen");
        if (policyManager.isAdminActive(componentName)) {
            Window localWindow = getWindow();
            WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
            localLayoutParams.screenBrightness = 0.05F;
            localWindow.setAttributes(localLayoutParams);
            policyManager.lockNow();
        }
        finish();
    }

    private boolean isLockScreen(){
        KeyguardManager mKeyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE); 
        return mKeyguardManager.inKeyguardRestrictedInputMode();
    }

    private void goSetActivity() {
        Utils.d(TAG, "goSetActivity");
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        startActivityForResult(intent, 1);
    }
    
    /**
     * action:自定义style解析和应用
     * 
     * project: LockScreenDemo
     * Package: com.xian.locktime
     * ClassName: ControlAttributes
     * created:wgx
     * date:20170719
     * version:
     * remark:
     * 
     */
    public class ControlAttributes{
        final String mFontFilePathStringDef="null";
        final float mFontSizeDateDef=-1;
        final float mFontSizeTimeDef=-1;
        final int mFontColorDateDef=0xffffffff;
        final int mFontColorTimeDef=0xffffffff;

        Theme mTheme;
        Context mContext;

        String mFontFilePathString=mFontFilePathStringDef;
        float mFontSizeDate=mFontSizeDateDef;
        float mFontSizeTime=mFontSizeTimeDef;
        int mFontColorDate=mFontColorDateDef;
        int mFontColorTime=mFontColorTimeDef;

        public ControlAttributes(Context context,int style_id) {
            mContext=context;
            mTheme = context.getResources().newTheme();
            mTheme.applyStyle(style_id, true);

            TypedArray ta = mTheme.obtainStyledAttributes(R.styleable.LockScreenCtrol);
            mFontFilePathString = ta.getString(R.styleable.LockScreenCtrol_fontFilePath);
            mFontSizeDate=ta.getDimension(R.styleable.LockScreenCtrol_fontSize2Date, mFontSizeDateDef);
            mFontSizeTime=ta.getDimension(R.styleable.LockScreenCtrol_fontSize2Time, mFontSizeTimeDef);
            mFontColorDate=ta.getColor(R.styleable.LockScreenCtrol_fontColor2Date, mFontColorDateDef);
            mFontColorTime=ta.getColor(R.styleable.LockScreenCtrol_fontColor2Time, mFontColorTimeDef);

            ta.recycle();
        }

        public void applyStyle(TextView ...tv_list) {
            Typeface fontFace = Typeface.createFromAsset(getAssets(),mFontFilePathString);
            for (TextView tv : tv_list) {
                switch (tv.getId()) {
                case R.id.tv_date:
                    if(mFontSizeDateDef!=mFontSizeDate)
                        tv.setTextSize(mFontSizeDate);
                    tv.setTextColor(mFontColorDate);
                    break;
                case R.id.tv_time:
                    if(mFontSizeTimeDef!=mFontSizeTime)
                        tv.setTextSize(mFontSizeTime);
                    tv.setTextColor(mFontColorTime);
                    break;
                default:
                    break;
                }
                if(mFontFilePathStringDef!=mFontFilePathString)
                    tv.setTypeface(fontFace);
            }
        }
    }

    /**
     * action:日志工具
     * 
     * project: LockScreenDemo
     * Package: com.xian.locktime
     * ClassName: Utils
     * created:wgx
     * date: 20170718
     * version:
     * remark:
     * 
     */
    public  static class Utils{
        private final static boolean IS_DEBUG=true;
        public static void e(String tag,String content) {
            if(IS_DEBUG){
                tag=PROJECT_NAME+"__"+tag;
                android.util.Log.e(tag,content);
            }
        }
        public static void d(String tag,String content) {
            if(IS_DEBUG){
                tag=PROJECT_NAME+"__"+tag;
                android.util.Log.d(tag,content);
            }
        }
    }
}
