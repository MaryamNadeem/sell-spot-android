package com.sellspot.app.Activities.Camera;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.R;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

public class SSProfilePictureCameraVC extends AppCompatActivity {

    private Context context;
    private CameraView cameraView;
    private ImageButton profileFlashButton;
    private ProgressDialog progressDialog;
    private SSCameraVM viewModel = new SSCameraVM();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_s_profile_picture_camera_v_c);
        initialiseVM();
        initUIAttributes();
    }

    private void initUIAttributes() {
        this.getSupportActionBar().hide();
        context = this;
        cameraView = (CameraView) findViewById(R.id.cameraKitView);
        profileFlashButton = (ImageButton) findViewById(R.id.profileFlashButton);
        setCameraKit();
    }

    private void initialiseVM() {
        viewModel.pictureSuccess = new SSFirebaseHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
                progressDialog.dismiss();
                if (success) {
                    finish();
                } else {
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private void setCameraKit() {
        cameraView.setPermissions(CameraKit.Constants.PERMISSIONS_PICTURE);
        cameraView.setFacing(CameraKit.Constants.FACING_BACK);
        cameraView.setFlash(CameraKit.Constants.FLASH_OFF);
        cameraView.setFocus(CameraKit.Constants.FOCUS_CONTINUOUS);
        cameraView.setCropOutput(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraView.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cameraView.stop();
    }

    public void onCaptureClick(final View view) {
        progressDialog = ProgressDialog.show(context, "Saving Image...", "Please wait...", true);
        cameraView.captureImage(new CameraKitEventCallback<CameraKitImage>() {
            @Override
            public void callback(CameraKitImage cameraKitImage) {
                Bitmap bitmap = cameraKitImage.getBitmap();
                Bitmap croppedBitmap = cropBitmapCenter(bitmap, cameraView.getWidth(), cameraView.getWidth());
                viewModel.setUserProfileImage(croppedBitmap);
            }
        });
    }

    public void setFlash(View view) {
        if (cameraView.getFlash() == CameraKit.Constants.FLASH_OFF) {
            cameraView.setFlash(CameraKit.Constants.FLASH_ON);
            profileFlashButton.setImageResource(R.mipmap.flashon);
            return;
        } else if (cameraView.getFlash() == CameraKit.Constants.FLASH_ON) {
            cameraView.setFlash(CameraKit.Constants.FLASH_OFF);
            profileFlashButton.setImageResource(R.mipmap.flashoff);
            return;
        }
    }

    public void switchCamera(View view) {
        if (cameraView.getFacing() == CameraKit.Constants.FACING_BACK) {
            cameraView.setFacing(CameraKit.Constants.FACING_FRONT);
            return;
        } else if (cameraView.getFacing() == CameraKit.Constants.FACING_FRONT) {
            cameraView.setFacing(CameraKit.Constants.FACING_BACK);
            return;
        }
    }

    private Bitmap cropBitmapCenter(Bitmap bitmap, int cropWidth, int cropHeight){
        int bitmapWidth     = bitmap.getWidth();
        int bitmapheight    = bitmap.getHeight();

        cropWidth   = (cropWidth > bitmapWidth) ? bitmapWidth : cropWidth;
        cropHeight  = (cropHeight > bitmapheight) ? bitmapheight : cropHeight;

        int newX = bitmapWidth/2  - cropWidth/2;
        int newY = bitmapheight/2 - cropHeight/2;

        Bitmap croppedBitmap = Bitmap.createBitmap(bitmap,newX, newY, cropWidth, cropHeight);
        return croppedBitmap;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}