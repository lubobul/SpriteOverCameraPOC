package lubobul.cameratest;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.javacodegeeks.androidsurfaceviewexample.R;

import java.util.List;

import threads.BufferLoader;

public class MainActivity extends Activity implements SurfaceHolder.Callback {

    long cur_sys_time = 0;
    long final_sys_tyme = 0;

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;

    private CustomView customView;

    MainActivity mainActivity = this;

    BufferLoader spriteEngine;

    boolean isPreviewRunning = false;
    int width = 1280;
    int height = 720;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        cur_sys_time = System.currentTimeMillis();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceView.setZOrderOnTop(false);
        surfaceHolder = surfaceView.getHolder();
        surfaceView.setZOrderOnTop(false);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        surfaceHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        //surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        customView = (CustomView) findViewById(R.id.customView);

        spriteEngine = new BufferLoader(mainActivity, 96, 480, 48);
        customView.setFPS(60);
        customView.setBufferLoader(spriteEngine);

        Button doMagicButton = (Button) findViewById(R.id.doMagicButton);

        doMagicButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                spriteEngine.start();

            }
        });

        final_sys_tyme = System.currentTimeMillis();

        Log.d("Time to execute: ", String.valueOf((final_sys_tyme-cur_sys_time)));
    }


    public void refreshCamera() {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        if (surfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            if (isPreviewRunning)
            {
                camera.stopPreview();
            }
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }



        Camera.Parameters parameters = camera.getParameters();


        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size previewSize = previewSizes.get(2);

        Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        if(display.getRotation() == Surface.ROTATION_0)
        {
            parameters.setPreviewSize(previewSize.height, previewSize.width);
            camera.setDisplayOrientation(90);
        }

        if(display.getRotation() == Surface.ROTATION_90)
        {
            parameters.setPreviewSize(previewSize.width, previewSize.height);
        }

        if(display.getRotation() == Surface.ROTATION_180)
        {
            parameters.setPreviewSize(previewSize.height, previewSize.width);
        }

        if(display.getRotation() == Surface.ROTATION_270)
        {
            parameters.setPreviewSize(previewSize.width, previewSize.height);
            camera.setDisplayOrientation(180);
        }

        //camera.setParameters(parameters);

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {

        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // open the camera
            camera = Camera.open();
        } catch (RuntimeException e) {
            // check for exceptions
            System.err.println(e);
            return;
        }
        Camera.Parameters param;
        param = camera.getParameters();

        // modify parameter
        param.setPreviewSize(width, height);

        camera.setParameters(param);
        try {
            // The Surface has been created, now tell the camera where to draw
            // the preview.
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            // check for exceptions
            System.err.println(e);
            return;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // stop preview and release camera
        camera.stopPreview();
        camera.release();
        camera = null;
    }
}