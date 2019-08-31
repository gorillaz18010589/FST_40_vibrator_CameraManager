package tw.brad.apps.brad40;
//*目的手機震動
//<uses-permission android:name="android.permission.VIBRATE"/>打開震動權限
//當你看到不同版去處理,編譯工具要先才能處理

//*閃光燈
// <uses-permission android:name="android.permission.CAMERA"/>相機權限因為燈光是靠相機來處理
//如果我這裡面會用相機,如果沒有的別下載 <uses-feature android:name="android.hardware.camera"/>

//呼叫別人的相機
//android camera intent uri


//存取檔案
//file porifder :https://blog.csdn.net/lmj623565791/article/details/72859156
//（1）声明provider
//（2）编写resource xml file
//打開讀寫權限
//<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
// <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
//在res底下新增xml資料夾在新增一個檔案
//<!-- 改page名-->
//檔案總管加入新的版本
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private Vibrator vibrator ;//震動器
    private SwitchCompat fswitch;//閃光燈按鈕
    private CameraManager cameraManager;//相機關裡員
    private ImageView img;
    private File sdroot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //閃光燈所需的相機權限
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.CAMERA) //改相機權限
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.CAMERA}, //改相機權限
//                    12);
//        }else {
//            init();
//        }

        //讀寫sdcard權限,要修正
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) //寫的權限
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,//讀的權限
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,//寫的權限
                    },
                    12);
        }else {
            init();
        }

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);//這個震動期是由 getSystemService
        fswitch = findViewById(R.id.fswitch);
        img = findViewById(R.id.img);
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

        sdroot = Environment.getExternalStorageDirectory(); //取得sd卡外部檔案物件

        // getCameraIdList(); 取得相機鏡頭有幾個
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
    //呼叫你手機的照相程式
    public void test3(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//intent(媒體商店裡的,相片動態捕捉)
        startActivityForResult(intent,3); //開始連接intent(intent,跟指定的要求code)
    }

    //從data取得getExtras().裡面取得data訊息
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode ==RESULT_OK){
            if(requestCode ==3){ //抓到code3的時候
                Bundle bundle = data.getExtras();
                //了解為什麼是Bitmap用反射方法回推
              Set<String> keys = bundle.keySet(); //取得key
              for(String key: keys){//尋訪
                  Log.v("brad","key =" + key);
                  Object obj = bundle.get(key); //把key存在obj物件
                  Log.v("brad",obj.getClass().getName());//印出這個物件的類別跟名字
              }
                //取得你的照片存在程式上
             Bitmap bmp = (Bitmap) data.getExtras().get("data");//照玩相傳過來的Bitmap物件實體
                 img.setImageBitmap(bmp);//把照片顯示在程式上
            }
        }
    }
    //拍照後存檔
    public void test4(View view) {
        Uri photoURI = FileProvider.getUriForFile(
                this, //1.這個activity
                getPackageName() + ".provider",//2.這個pagename+檔案名
                new File(sdroot, "iii.jpg")); //3.存放的file路徑("sdroot路徑實體","檔案照片名")

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//呼叫照片拍著時
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);//掛上素質(1.外布檔案,2寫好的fileProvider路徑)
        startActivityForResult(intent, 4);


    }
}
