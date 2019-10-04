package bd.edu.daffodilvarsity.classmanager.broadcastreceiver;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.activities.SignIn;

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

        Intent intent = new Intent(context, SignIn.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        Notification notification = new NotificationCompat.Builder(context,ROUTINE_REMINDER_ID)
                .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                .setContentTitle(title)
                .setContentText(description)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

                .build();

        notificationManager.notify(1,notification);
    }
}
