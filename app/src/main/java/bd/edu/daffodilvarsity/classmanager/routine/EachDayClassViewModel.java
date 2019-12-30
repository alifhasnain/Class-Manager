package bd.edu.daffodilvarsity.classmanager.routine;

import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.broadcastreceiver.NotificationReceiver;
import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;
import bd.edu.daffodilvarsity.classmanager.otherclasses.SharedPreferencesHelper;
import bd.edu.daffodilvarsity.classmanager.workers.ReminderSchedulerWorker;
import timber.log.Timber;

import static android.content.Context.ALARM_SERVICE;
import static bd.edu.daffodilvarsity.classmanager.BaseApplication.DOWNLOAD_PROGRESS_CHANNEL_ID;
import static bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass.NOTIFICATION_ALARM_REQ_CODE;

public class EachDayClassViewModel extends AndroidViewModel {

    private LiveData<List<RoutineClassDetails>> classesListLiveData;

    private EachDayClassRepository dataRepo;

    private static boolean downloadInProgress = false;

    public EachDayClassViewModel(@NonNull Application application) {
        super(application);
        dataRepo = new EachDayClassRepository(application);
    }

    public void loadWholeRoutineFromServer(final String version) {

        if(downloadInProgress)  {
            makeToast("Download is already in progress");
            return;
        }
        else {
            downloadInProgress = true;
        }

        StorageReference rootStorage = FirebaseStorage.getInstance().getReference();
        StorageReference dayRoutine = rootStorage.child("/main_campus/routine_day.txt");
        StorageReference eveningRoutine = rootStorage.child("/main_campus/routine_evening.txt");

        final long MAX_DOWNLOAD_SIZE = 1024 * 1024;

        Task<byte[]> dayRoutineTask = dayRoutine.getBytes(MAX_DOWNLOAD_SIZE);
        Task<byte[]> eveningRoutineTask = eveningRoutine.getBytes(MAX_DOWNLOAD_SIZE);

        Task<List<byte[]>> allDownloadTask = Tasks.whenAllSuccess(dayRoutineTask, eveningRoutineTask);

        showDownloadNotificationProgress(true,"Downloading...");

        allDownloadTask.addOnSuccessListener(new OnSuccessListener<List<byte[]>>() {
            @Override
            public void onSuccess(List<byte[]> byteArrayList) {
                String dayJsonString = new String(byteArrayList.get(0), StandardCharsets.UTF_8);
                String eveningJsonString = new String(byteArrayList.get(1), StandardCharsets.UTF_8);

                makeToast("Downloaded!");
                cancelAlarms();

                dataRepo.saveJsonRoutineToRoomDatabase(dayJsonString, eveningJsonString);
                SharedPreferencesHelper.saveRoutineVersionToSharedPreferences(getApplication(), version);
                downloadInProgress = false;
                showDownloadNotificationProgress(false,"Download Complete");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Timber.e(e);
                downloadInProgress = false;
                showDownloadNotificationProgress(false,"Download Failed");
            }
        });
    }

    public void loadWholeRoutineFromServer() {

        if(downloadInProgress)  {
            makeToast("Download is already in progress");
            return;
        }
        else {
            downloadInProgress = true;
        }

        StorageReference rootStorage = FirebaseStorage.getInstance().getReference();
        StorageReference dayRoutine = rootStorage.child("/main_campus/routine_day.txt");
        StorageReference eveningRoutine = rootStorage.child("/main_campus/routine_evening.txt");

        final long MAX_DOWNLOAD_SIZE = 1024 * 1024;

        Task<byte[]> dayRoutineTask = dayRoutine.getBytes(MAX_DOWNLOAD_SIZE);
        Task<byte[]> eveningRoutineTask = eveningRoutine.getBytes(MAX_DOWNLOAD_SIZE);

        Task<List<byte[]>> allDownloadTask = Tasks.whenAllSuccess(dayRoutineTask, eveningRoutineTask);

        showDownloadNotificationProgress(true,"Downloading...");

        allDownloadTask.addOnSuccessListener(new OnSuccessListener<List<byte[]>>() {
            @Override
            public void onSuccess(List<byte[]> byteArrayList) {
                String dayJsonString = new String(byteArrayList.get(0), StandardCharsets.UTF_8);
                String eveningJsonString = new String(byteArrayList.get(1), StandardCharsets.UTF_8);
                makeToast("Downloaded!");
                cancelAlarms();
                dataRepo.saveJsonRoutineToRoomDatabase(dayJsonString, eveningJsonString);
                downloadInProgress = false;
                showDownloadNotificationProgress(false,"Download Complete");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                downloadInProgress = false;
                showDownloadNotificationProgress(false,"Download Failed");
                Timber.e(e);
            }
        });
    }

    public void showDownloadNotificationProgress(boolean downloading , String message)  {

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplication());

        Notification notification = new NotificationCompat.Builder(getApplication(),DOWNLOAD_PROGRESS_CHANNEL_ID)
                .setContentTitle("Routine Download")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_download)
                .setProgress(0,0,downloading)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(null)
                .build();

        notificationManager.notify(1225,notification);

    }

    public void loadClassesStudent(List<String> courseCodeList, String shift, List<String> sectionList, String dayOfWeek) {
        classesListLiveData = dataRepo.loadClassesStudent(courseCodeList, shift, sectionList, dayOfWeek);
    }

    public void loadClassesTeacher(String initial, String dayOfWeek) {
        classesListLiveData = dataRepo.loadClassesTeacher(initial, dayOfWeek);
    }

    public void setNotificationEnabled(RoutineClassDetails routineClassDetails) {

        dataRepo.setNotificationEnables(routineClassDetails);

        PeriodicWorkRequest notificationChecker = new PeriodicWorkRequest.Builder(
                ReminderSchedulerWorker.class, 55, TimeUnit.MINUTES
        ).build();

        WorkManager.getInstance(getApplication()).enqueueUniquePeriodicWork(
                HelperClass.WORK_SCHEDULER_ID,
                ExistingPeriodicWorkPolicy.REPLACE,
                notificationChecker
        );
    }

    public LiveData<List<RoutineClassDetails>> getClasses() {
        return classesListLiveData;
    }

    private void cancelAlarms() {

        AlarmManager alarmManager = (AlarmManager) getApplication().getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(getApplication(), NotificationReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplication(), NOTIFICATION_ALARM_REQ_CODE, intent, 0);

        alarmManager.cancel(pendingIntent);

    }

    private void makeToast(String txt) {
        if(getApplication()!=null)  {
            Toast.makeText(getApplication(), txt, Toast.LENGTH_SHORT).show();
        }
    }

}
