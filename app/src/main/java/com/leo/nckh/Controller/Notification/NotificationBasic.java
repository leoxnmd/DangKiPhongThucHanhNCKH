package com.leo.nckh.Controller.Notification;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.leo.nckh.Controller.Activity.Activity_Home;
import com.leo.nckh.Controller.Activity.Activity_Intro;
import com.leo.nckh.Controller.Fragment.Fragment_DsMuonPhong;
import com.leo.nckh.Controller.Fragment.Fragment_Tab_DsMuonPhong;
import com.leo.nckh.R;

public class NotificationBasic {
    public static final String NOTIFICATION_CHANNEL_ID = "168";
    public static final int NOTIFICATION_ID = 168;

    private final static String default_notification_channel_id = "default";
    static final String CHANNEL_NAME = "technopoints name";
    static final String CHANNEL_DESC = "technopoints desc";
    Context context;

    public NotificationBasic(Context context) {
        this.context = context;
    }

    public void thongBao(int id, String title, String content) {
        //scheduleNotification(getNotification(content), 10);
        displayNotification(id, title, content);

    }


    public void displayNotification(int id, String title, String text) {

        Intent intentClickNoti = new Intent(context, Activity_Intro.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intentClickNoti);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_IMMUTABLE);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.logo);
        NotificationManagerCompat notificationCompat = NotificationManagerCompat.from(context);
        notificationCompat.notify(id, mBuilder.build());


    }

//    private void scheduleNotification(Notification notification, int delay) {
//        Intent notificationIntent = new Intent(context, NotificationBroadcastReceiver.class);
//        notificationIntent.putExtra(NotificationBroadcastReceiver.NOTIFICATION_ID, 1);
//        notificationIntent.putExtra(NotificationBroadcastReceiver.NOTIFICATION, notification);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
//        long futureInMillis = SystemClock.elapsedRealtime() + delay;
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
//    }
//
//    private Notification getNotification(String content) {
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, default_notification_channel_id);
//        builder.setContentTitle("Dữ liệu đã thay đổi");
//        builder.setContentText(content);
//        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
//        builder.setAutoCancel(true);
//        builder.setChannelId(NOTIFICATION_CHANNEL_ID);
//        return builder.build();
//    }
}
