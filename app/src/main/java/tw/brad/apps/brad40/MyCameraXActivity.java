package tw.brad.apps.brad40;
//自訂的相機
//Camera =>  連接 =>Surfaceview
//camera = Camera.open();//打開錢置鏡頭都不帶參數代表前制鏡頭

//<!--
//        <intent-filter> 可以引用照相機
//        <action android:name="android.media.action.IMAGE_CAPTURE" />
//        </intent-filter>
//        -->

//掛api
// CameraX core library
//def camerax_version = "1.0.0-alpha04"
//        // CameraX view library
//        def camerax_view_version = "1.0.0-alpha01"
//        // CameraX extensions library
//        def camerax_ext_version = "1.0.0-alpha01"
//        implementation "androidx.camera:camera-core:$camerax_version"
//        // If you want to use Camera2 extensions
//        implementation "androidx.camera:camera-camera2:$camerax_version"
//        // If you to use the Camera View class
//        implementation "androidx.camera:camera-view:$camerax_view_version"
//        // If you to use Camera Extensions
//        implementation "androidx.camera:camera-extensions:$camerax_ext_version"

//用java8
//compileOptions {
//        sourceCompatibility JavaVersion.VERSION_1_8
//        targetCompatibility JavaVersion.VERSION_1_8
//        }

//      android:visibility="invisible"
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

public class MyCameraXActivity extends AppCompatActivity
        implements LifecycleOwner { //實做LifecycleOwner

    private ImageCapture imageCapture;
    private TextureView textureView;
    private ViewGroup viewGroup;

    private LifecycleRegistry mLifecycleRegistry;
    private File sdroot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_camera_x);

        sdroot = Environment.getExternalStorageDirectory();//取得外布sdcard物件實體

        mLifecycleRegistry = new LifecycleRegistry(this);
        mLifecycleRegistry.markState(Lifecycle.State.CREATED);

        viewGroup = findViewById(R.id.viewGroup);
        textureView = findViewById(R.id.textureView);

        init();
    }
    //開啟時
    @Override
    protected void onResume() {
        super.onResume();
        mLifecycleRegistry.markState(Lifecycle.State.RESUMED);
    }
    //實作方法
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }
    //初始化時
    private void init(){
        textureView.post(new Runnable() {
            @Override
            public void run() {
                startCamera();//呼叫開啟相機
            }
        });

        //當拍照畫面角度有變時
        textureView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                updateTransorm();
            }
        });
    }


    private void updateTransorm(){
        float w = textureView.getMeasuredWidth();
        float h = textureView.getMeasuredHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(0, w, h);
        textureView.setTransform(matrix);
    }



    private void startCamera(){
        CameraX.unbindAll();

        Rational rate = new Rational(textureView.getWidth(), textureView.getHeight());
        Size size = new Size(textureView.getWidth(), textureView.getHeight());

        PreviewConfig config = new PreviewConfig.Builder()
                .setTargetAspectRatio(rate)
                .setTargetResolution(size)
                .build();


        //PreviewConfig config = new PreviewConfig.Builder().build();
        Preview preview = new Preview(config);

        preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
            @Override
            public void onUpdated(Preview.PreviewOutput output) {
                //textureView.setSurfaceTexture(output.getSurfaceTexture());

                ViewGroup parent = (ViewGroup) textureView.getParent();
                parent.removeView(textureView);//先清掉一開始螢幕
                parent.addView(textureView,0);//再新增我們新做的照相機頁面

                textureView.setSurfaceTexture(output.getSurfaceTexture());
                updateTransorm();

            }
        });

        ImageCaptureConfig imageCaptureConfig =
                new ImageCaptureConfig.Builder()
                        .setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                        .build();
        imageCapture = new ImageCapture(imageCaptureConfig);

//        ImageCaptureConfig capconfig =
//                new ImageCaptureConfig.Builder()
//                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
//                        .build();
//        imageCapture = new ImageCapture(capconfig);

        //CameraX.bindToLifecycle((LifecycleOwner) this, imageCapture, imageAnalysis, preview);
        CameraX.bindToLifecycle(this, preview, imageCapture);

    }


    //按鈕後
    public void takePic2(View view) {

        File file = new File(sdroot, "iii20190831.jpg");
        imageCapture.takePicture(file, new ImageCapture.OnImageSavedListener() {
            @Override //儲存照片後
            public void onImageSaved(@NonNull File file) {
                Log.v("brad", "OK");
            }

            @Override//照片出現錯誤
            public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
                Log.v("brad", "XX");
            }
        });

    }

    @Override
    public void finish() {
        super.finish();
    }
}