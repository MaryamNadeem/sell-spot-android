package com.sellspot.app.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import androidx.core.app.NotificationCompat;
import com.sellspot.app.Activities.MainActivity.SSMainActivity;
import com.sellspot.app.Constants.SSConstants;
import com.sellspot.app.Handlers.SSFirebaseHandler;
import com.sellspot.app.Models.Product;
import com.sellspot.app.Networking.SSFirebaseStorageManager;
import com.sellspot.app.Shared.SharedVariables;
import com.sellspot.app.R;
import java.util.ArrayList;
import java.util.List;


public class ProductImageService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    private String productId;

private final class ServiceHandler extends Handler {
    public ServiceHandler(Looper looper) {
        super(looper);
    }
    @Override
    public void handleMessage(final Message msg) {
        try {
            List<Bitmap> productImages = new ArrayList<Bitmap>();
            productImages.addAll(SharedVariables.shared.productImages);
            SharedVariables.shared.productImages.clear();
            Product p = new Product(SharedVariables.shared.sharedProduct);
            SharedVariables.shared.sharedProduct = null;
            SSFirebaseStorageManager.uploadProductImage(productId, p, productImages, new SSFirebaseHandler() {
                @Override
                public void completionHandler(Boolean success, String error) {
                    stopForeground(true);
                    stopSelf();
                }
            });
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }
}

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        productId = intent.getExtras().getString(SSConstants.productId);
        createNotificationChannel();

        Intent notificationIntent = new Intent(this, SSMainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Sell Spot")
                .setContentText("Uploading product images...")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.logo)
                .build();
        startForeground(1, notification);


        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
