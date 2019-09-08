package bd.edu.daffodilvarsity.classmanager.broadcastreceiver;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import bd.edu.daffodilvarsity.classmanager.R;

import static bd.edu.daffodilvarsity.classmanager.BaseApplication.ROUTINE_REMINDER_ID;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();

        String title = extras.getString("title","");
        String description = extras.getString("description","");

        showNotification(title,description,context);

    }

    private void showNotification(String title,String description,Context context)  {

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        Notification notification = new NotificationCompat.Builder(context,ROUTINE_REMINDER_ID)
                .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .build();

        notificationManager.notify(1,notification);
    }
}
