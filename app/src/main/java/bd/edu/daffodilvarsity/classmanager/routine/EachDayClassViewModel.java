package bd.edu.daffodilvarsity.classmanager.routine;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import bd.edu.daffodilvarsity.classmanager.broadcastreceiver.NotificationReceiver;
import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;
import bd.edu.daffodilvarsity.classmanager.otherclasses.SharedPreferencesHelper;
import bd.edu.daffodilvarsity.classmanager.workers.ReminderSchedulerWorker;

import static android.content.Context.ALARM_SERVICE;
import static bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass.NOTIFICATION_ALARM_REQ_CODE;

public class EachDayClassViewModel extends AndroidViewModel {

    private SharedPreferencesHelper mSharedPrefHelper = new SharedPreferencesHelper();

    private LiveData<List<RoutineClassDetails>> classesListLiveData;

    private EachDayClassRepository dataRepo;

    private static boolean downloadInProgress = false;

    public EachDayClassViewModel(@NonNull Application application) {
        super(application);
        dataRepo = new EachDayClassRepository(application);
    }

    /*public void loadWholeStudentRoutineFromServer() {
        dataRepo.loadWholeRoutineFromServer(getApplication());
    }

    public void loadWholeTeacherRoutineFromServer() {
        FirebaseFirestore.getInstance()
                .document("teacher_profiles/" + FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .get(Source.SERVER)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {

                            ProfileObjectTeacher profile = documentSnapshot.toObject(ProfileObjectTeacher.class);

                            String initial = profile.getTeacherInitial();

                            dataRepo.loadWholeRoutineFromServer(initial);

                            mSharedPrefHelper.saveTeacherInitialToSharedPref(getApplication(), initial);

                        } else if (!documentSnapshot.exists()) {
                            toastMsg.setValue("Your profile doesn't exist in database.\nPlease contact admin");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toastMsg.setValue("Failed to load please check your internet connection.");
                    }
                });
    }*/

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

        allDownloadTask.addOnSuccessListener(new OnSuccessListener<List<byte[]>>() {
            @Override
            public void onSuccess(List<byte[]> byteArrayList) {
                String dayJsonString = new String(byteArrayList.get(0), StandardCharsets.UTF_8);
                String eveningJsonString = new String(byteArrayList.get(1), StandardCharsets.UTF_8);

                makeToast("Downloaded!");
                cancelAlarms();

                dataRepo.saveJsonRoutineToRoomDatabase(dayJsonString, eveningJsonString);
                mSharedPrefHelper.saveRoutineVersionToSharedPreferences(getApplication(), version);
                downloadInProgress = false;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                downloadInProgress = false;
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

        allDownloadTask.addOnSuccessListener(new OnSuccessListener<List<byte[]>>() {
            @Override
            public void onSuccess(List<byte[]> byteArrayList) {
                String dayJsonString = new String(byteArrayList.get(0), StandardCharsets.UTF_8);
                String eveningJsonString = new String(byteArrayList.get(1), StandardCharsets.UTF_8);
                makeToast("Downloaded!");
                cancelAlarms();
                dataRepo.saveJsonRoutineToRoomDatabase(dayJsonString, eveningJsonString);
                downloadInProgress = false;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                downloadInProgress = false;
            }
        });
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
