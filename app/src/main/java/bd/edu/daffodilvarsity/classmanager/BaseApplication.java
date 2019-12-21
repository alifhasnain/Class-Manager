package bd.edu.daffodilvarsity.classmanager;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;
import bd.edu.daffodilvarsity.classmanager.workers.ReminderSchedulerWorker;
import timber.log.Timber;


public class BaseApplication extends Application {

    public static final String ROUTINE_REMINDER_CHANNEL_ID = "routine_reminder_notification_channel";

    public static final String DOWNLOAD_PROGRESS_CHANNEL_ID = "routine_download_progress";

    public static final String FCM_NOTIFICATION_CHANNEL_ID = "fcm_notification_channel";

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        createNotificationChannels();

        initializeWork();

    }

    private void initializeWork() {

        PeriodicWorkRequest notificationChecker = new PeriodicWorkRequest.Builder(
                ReminderSchedulerWorker.class,55, TimeUnit.MINUTES
        ).build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                HelperClass.WORK_SCHEDULER_ID,
                ExistingPeriodicWorkPolicy.KEEP,
                notificationChecker
        );

    }

    private void createNotificationChannels() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)  {
            NotificationChannel channel1 = new NotificationChannel(
                    ROUTINE_REMINDER_CHANNEL_ID,
                    "Routine Reminder Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This channel shows the notification of daily routine");

            NotificationChannel channel2 = new NotificationChannel(
                    DOWNLOAD_PROGRESS_CHANNEL_ID,
                    "Download Progress Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel2.setDescription("This channel shows the download progress of routine");
            channel2.setSound(null,null);

            NotificationChannel channel3 = new NotificationChannel(
                    FCM_NOTIFICATION_CHANNEL_ID,
                    "Firebase Cloud Messaging Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel3.setDescription("This channel shows notification received from Firebase Cloud Messaging.");

            List<NotificationChannel> channelList = new ArrayList<>();
            channelList.add(channel1);
            channelList.add(channel2);
            channelList.add(channel3);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannels(channelList);
        }

    }
}
