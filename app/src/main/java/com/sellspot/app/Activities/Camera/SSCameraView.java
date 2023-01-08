package com.sellspot.app.Activities.Camera;

import android.app.ProgressDialog;
import android.view.View;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.content.Context;
import android.graphics.Matrix;

import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.wonderkiln.camerakit.CameraKit;
import com.sellspot.app.R;
import android.widget.ImageButton;
import android.widget.Toast;

import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;
import java.io.ByteArrayOutputStream;
import androidx.appcompat.app.AppCompatActivity;
import com.sellspot.app.Constants.SSConstants;

public class SSCameraView extends AppCompatActivity {

    private int checkActivity;

    private Context context;

    private ImageButton flashButton;
    private CameraView cameraKitView;

    private ProgressDialog progressDialog;

    private SSCameraVM viewModel = new SSCameraVM();

    int index = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_view);
        initialiseVM();
        initUIAttributes();
    }

    private void initUIAttributes() {
        this.getSupportActionBar().hide();
        context = this;
        cameraKitView = (CameraView) findViewById(R.id.cameraView);
        flashButton = (ImageButton) findViewById(R.id.flashButton);

        setCameraKit();

        checkActivity = getIntent().getExtras().getInt(SSConstants.IMAGE_KEY);

        if (checkActivity == SSConstants.ADD_NEW_ITEM_VALUE) {
            index = getIntent().getIntExtra("retake_index",-1);
        }
    }

    private void initialiseVM() {
        viewModel.pictureSuccess = new SSFirebaseHandler() {
            @Override
            public void completionHandler(Boolean success, String error) {
            progressDialog.dismiss();
            if (success) {
                if (checkActivity == SSConstants.PROFILE_PICTURE_VALUE || checkActivity == SSConstants.SETTINGS_PICTURE_VALUE) {
                    finish();
                }
            } else {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            }
            }
        };
    }

    private void setCameraKit() {
        cameraKitView.setPermissions(CameraKit.Constants.PERMISSIONS_PICTURE);
        cameraKitView.setFacing(CameraKit.Constants.FACING_BACK);
        cameraKitView.setFlash(CameraKit.Constants.FLASH_OFF);
        cameraKitView.setFocus(CameraKit.Constants.FOCUS_CONTINUOUS);
        cameraKitView.setCropOutput(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cameraKitView.stop();
    }

    public void onCaptureClick(final View view) {
        progressDialog = ProgressDialog.show(context, "Saving Image...", "Please wait...", true);
        cameraKitView.captureImage(new CameraKitEventCallback<CameraKitImage>() {
            @Override
            public void callback(CameraKitImage cameraKitImage) {
                Bitmap bitmap = cameraKitImage.getBitmap();
                Bitmap croppedBitmap = cropBitmapCenter(bitmap, cameraKitView.getWidth(), cameraKitView.getHeight());
                if (checkActivity == SSConstants.PROFILE_PICTURE_VALUE) {
                    viewModel.setUserProfileImage(croppedBitmap);
                } else if (checkActivity == SSConstants.ADD_NEW_ITEM_VALUE) {
                    if (index == -1) {
                        viewModel.addImageFromCamera(bitmap);
                    } else {
                        viewModel.updateImageFromCamera(bitmap, index);
                    }
                    progressDialog.dismiss();
                    finish();
                } else if (checkActivity == SSConstants.SETTINGS_PICTURE_VALUE) {
                    viewModel.setUserProfileImage(croppedBitmap);
                }
            }
        });
    }

    public void setFlash(View view) {
        if (cameraKitView.getFlash() == CameraKit.Constants.FLASH_OFF) {
            cameraKitView.setFlash(CameraKit.Constants.FLASH_ON);
            flashButton.setImageResource(R.mipmap.flashon);
            return;
        } else if (cameraKitView.getFlash() == CameraKit.Constants.FLASH_ON) {
            cameraKitView.setFlash(CameraKit.Constants.FLASH_OFF);
            flashButton.setImageResource(R.mipmap.flashoff);
            return;
        }
    }

    public void switchCamera(View view) {
        if (cameraKitView.getFacing() == CameraKit.Constants.FACING_BACK) {
            cameraKitView.setFacing(CameraKit.Constants.FACING_FRONT);
            return;
        } else if (cameraKitView.getFacing() == CameraKit.Constants.FACING_FRONT) {
            cameraKitView.setFacing(CameraKit.Constants.FACING_BACK);
            return;
        }
    }

    private static Bitmap RotateBitmap(Bitmap source) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Matrix matrix = new Matrix();
        matrix.preScale(1.0f, 1.0f);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, false);
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
}

