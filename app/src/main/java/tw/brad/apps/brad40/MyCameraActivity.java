package tw.brad.apps.brad40;
//自訂的相機
//Camera =>  連接 =>Surfaceview
//camera = Camera.open();//打開錢置鏡頭都不帶參數代表前制鏡頭

//<!--
//        <intent-filter> 可以引用照相機
//        <action android:name="android.media.action.IMAGE_CAPTURE" />
//        </intent-filter>
//        -->
import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

public class MyCameraActivity extends AppCompatActivity {
    private SurfaceView svCamera;
    private SurfaceHolder surfaceHolder;
    private Camera  camera;//實體相機的物件
    private File sdroot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_camera);
        sdroot = Environment.getDataDirectory();
        svCamera = findViewById(R.id.sv_camera);
        initCamera();
    }

    //取得相機方法
    private  void initCamera(){
        camera = Camera.open(0);//打開錢置鏡頭都不帶參數代表前制鏡頭
    }


    //按下拍照按鈕
    public void takPic(View view) {
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override //當照片一照時
            public void onPictureTaken(byte[] bytes, Camera camera) {
                File temp = new File(sdroot,"brad20190831.jpg");
                try{
                    FileOutputStream fout =
                            new FileOutputStream(temp);
                    fout.write(bytes);
                    fout.flush();
                    fout.close();
                    Log.v("brad","ok");
                }catch (Exception e){
                    Log.v("brad",e.toString());
                }
            }
        });
    }
}
