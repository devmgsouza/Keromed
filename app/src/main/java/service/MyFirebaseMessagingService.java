package service;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import br.com.soasd.projetoa.BaseActivity;
import br.com.soasd.projetoa.R;


/**
 * Created by Marcio on 29/11/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        if (remoteMessage.getData().size() > 0) {

        }
        if (remoteMessage.getNotification() != null) {

            sendNotification(remoteMessage.getNotification().getBody());


        }

    }



    private void sendNotification(String body) {
        Intent intent = new Intent(this, BaseActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,   PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this,  "default")
                .setSmallIcon(R.drawable.projetoa_ico)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(body)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setLights(Color.GREEN, 2000, 2000)
                .setContentIntent(pendingIntent);



        NotificationManager notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notifBuilder.build());




        Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(this, notificationSound);
        ringtone.play();


    }



}
