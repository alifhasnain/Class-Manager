package bd.edu.daffodilvarsity.classmanager;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;
import bd.edu.daffodilvarsity.classmanager.workers.ReminderSchedulerWorker;


public class BaseApplication extends Application {

    public static final String ROUTINE_REMINDER_ID = "routine_reminder_notification_channel";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();

        initializeWork();

    }

    private void initializeWork() {

        PeriodicWorkRequest notificationChecker = new PeriodicWorkRequest.Builder(
                ReminderSchedulerWorker.class,55, TimeUnit.MINUTES
        ).build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                HelperClass.SCHEDULER_ID,
                ExistingPeriodicWorkPolicy.KEEP,
                notificationChecker
        );

    }

    private void createNotificationChannels() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)  {
            NotificationChannel channel1 = new NotificationChannel(
                    ROUTINE_REMINDER_ID,
                    "Routine Reminder Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This channel shows the notification of daily routine");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
        }

    }
}
