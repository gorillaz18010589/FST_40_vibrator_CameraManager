package tw.brad.apps.brad40;
//目的手機震動
//<uses-permission android:name="android.permission.VIBRATE"/>打開震動權限
//當你看到不同版去處理,編譯工具要先才能處理

//閃光燈
// <uses-permission android:name="android.permission.CAMERA"/>相機權限因為燈光是靠相機來處理
//如果我這裡面會用相機,如果沒有的別下載 <uses-feature android:name="android.hardware.camera"/>
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

public class MainActivity extends AppCompatActivity {
    private Vibrator vibrator ;//震動器
    private SwitchCompat fswitch;//閃光燈按鈕
    private CameraManager cameraManager;//相機關裡員
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //閃光燈所需的相機權限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) //改相機權限
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, //改相機權限
                    12);
        }else {
            init();
        }

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);//這個震動期是由 getSystemService
        fswitch = findViewById(R.id.fswitch);

        //按開關切換開燈或關燈
        fswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//設置開關事件(切換按鈕監聽者)
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.v("brad","b =" +b);
                if(b){ //如果開關有打開
                    onFlashLight();//執行打開燈光方法
                }else{//如果開關是觀的
                    offFlashLight();//關掉燈光
                }
            }
        });
    }

    //當要求權限回傳時
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }

    //初始化方法
    private  void init(){
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);//取得相機管理員
//        getCameraIdList(); 取得相機鏡頭有幾個
        try {
            String[] ids = cameraManager.getCameraIdList();
            for(String id : ids){
                Log.v("brad",id);
            }
        }catch (CameraAccessException e){
            Log.v("brad,",e.toString());
        }
    }
    //開閃光燈  setTorchMode("0", true)://打開閃光燈("鏡頭0","執行")
    private  void onFlashLight(){
        try {
            cameraManager.setTorchMode("0", true);
        }catch (Exception e){
            Log.v("brad",e.toString());
        }
    }
    //關閃光燈
    private  void offFlashLight(){
    }

    //按按鈕震動一下
    public void test1(View view) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){//如果版本大於=Ozeo版本的話
            vibrator.vibrate(
                    VibrationEffect.createOneShot(1*1000 //1.震動秒數.
                    ,VibrationEffect.DEFAULT_AMPLITUDE));//震動的.震動效果 2.震動效果
        }else{//小於Ozero版本的話
            vibrator.vibrate(1*1000);//震動鎮一秒
        }
    }
    //連續震動
    public void test2(View view) {
        long[] patten={
                0,3*1000,1*1000,
                0,3*1000,1*1000,
                0,3*1000,1*1000};//{1.延遲多久2.震動幾下3.停多久}
        vibrator.vibrate(patten,-1);//(1.long震動規格物件,2.如果-1代表跑一次陣列,如果0代表無限巡迴)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 3));
            vibrator.vibrate(patten, -1); //如果新版本的話震動執行一次陣列
        }else{
            vibrator.vibrate(patten, 0);//如果舊版本的話,無線震動
        }
    }
}
