package com.hw.books_project.utils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.hw.books_project.R;
import com.hw.books_project.screens.home.HomeScreenActivity;

public class LoanReminderReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "loan_reminders";
    public static final String EXTRA_BOOK_NAME = "book_name";
    public static final String EXTRA_LIBRARY_NAME = "library_name";
    private static final String CHANNEL_NAME = "Loan Reminders";

    @Override
    public void onReceive(Context context, Intent intent) {
        String bookName = intent.getStringExtra(EXTRA_BOOK_NAME);
        String libraryName = intent.getStringExtra(EXTRA_LIBRARY_NAME);

        createNotificationChannel(context);

        Intent openAppIntent = new Intent(context, HomeScreenActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Book Return Reminder")
                .setContentText("It's time to return '" + bookName + "' to " + libraryName)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // notificationId could be a unique hash or similar, using 1 for simplicity or based on hash
        int notificationId = (bookName + libraryName).hashCode();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(notificationId, builder.build());
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = "Notifications for book return dates";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
