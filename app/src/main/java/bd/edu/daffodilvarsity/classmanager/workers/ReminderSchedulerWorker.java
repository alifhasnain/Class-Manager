package bd.edu.daffodilvarsity.classmanager.workers;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bd.edu.daffodilvarsity.classmanager.broadcastreceiver.NotificationReceiver;
import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectStudent;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectTeacher;
import bd.edu.daffodilvarsity.classmanager.otherclasses.SharedPreferencesHelper;
import bd.edu.daffodilvarsity.classmanager.routine.EachDayClassRepository;
import bd.edu.daffodilvarsity.classmanager.routine.RoutineClassDetails;
import timber.log.Timber;

import static android.content.Context.ALARM_SERVICE;
import static bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass.NOTIFICATION_ALARM_REQ_CODE;

public class ReminderSchedulerWorker extends Worker {

    public ReminderSchedulerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        List<RoutineClassDetails> classes = new ArrayList<>();

        EachDayClassRepository repo = new EachDayClassRepository((Application) getApplicationContext());
        ProfileObjectStudent studentProfile = SharedPreferencesHelper.getStudentOfflineProfile(getApplicationContext());

        String userType = SharedPreferencesHelper.getUserType(getApplicationContext());

        if (userType.equals(HelperClass.USER_TYPE_STUDENT)) {
            HashMap<String, String> courseAndSectionHashMap = SharedPreferencesHelper.getCoursesAndSectionMapFromSharedPreferences(getApplicationContext());

            List<String> courseList = new ArrayList<>();
            List<String> sectionList = new ArrayList<>();

            for (Map.Entry<String, String> entry : courseAndSectionHashMap.entrySet()) {
                courseList.add(entry.getKey());
                sectionList.add(entry.getValue());
            }

            classes = repo.getTodaysClasses(courseList, sectionList, studentProfile.getShift());

        } else if (userType.equals(HelperClass.USER_TYPE_ADMIN) || userType.equals(HelperClass.USER_TYPE_TEACHER)) {

            ProfileObjectTeacher profileTeacher = SharedPreferencesHelper.getTeacherOfflineProfile(getApplicationContext());

            classes = repo.getTodaysClasses(profileTeacher.getTeacherInitial());

        }

        Notification notification = getMinimumTimeFromPriority(classes);
        long minTime = notification.getTime();

        Date date = new Date();
        date.setTime(minTime);

        if ((minTime - System.currentTimeMillis()) >= 14400000) {
            //If time is greater than 4 hour then cancel alarm
            cancelAlarm();
        } else if ((minTime - System.currentTimeMillis()) > 900000) {
            //If current remaining time is greater than 15 min
            scheduleAlarm(notification.getTitle(), notification.getDescription(), (minTime - 899000));
        }

        return Result.success();
    }

    private void scheduleAlarm(String titleString, String descriptionString, long time) {

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        intent.putExtra("title", titleString);
        intent.putExtra("description", descriptionString);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), NOTIFICATION_ALARM_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);

    }

    private void cancelAlarm() {

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), NOTIFICATION_ALARM_REQ_CODE, intent, 0);

        alarmManager.cancel(pendingIntent);

    }

    private Notification getMinimumTimeFromPriority(List<RoutineClassDetails> classDetails) {

        Notification notification = new Notification();

        Calendar currentTime = Calendar.getInstance();

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.add(Calendar.DATE, 4);

        long lowestTime = compareCalendar.getTimeInMillis();
        notification.setTime(lowestTime);

        for (int i = 0; i < classDetails.size(); i++) {

            long classTime = getTimeFormPriority(classDetails.get(i).getPriority()).getTimeInMillis();

            if ((classTime > currentTime.getTimeInMillis()) && classTime < lowestTime) {
                lowestTime = classTime;
                notification.setDescription("Your class will held at " + classDetails.get(i).getTime() + " in " + classDetails.get(i).getRoom() + ".This was a soft reminder.");
                notification.setTime(lowestTime);
            }
        }

        return notification;

    }

    private Calendar getTimeFormPriority(float priority) {

        Calendar routineTime;

        switch (String.valueOf(priority)) {
            case "1.0":
                routineTime = getParsedDate("08:30AM");
                break;
            case "2.0":
                routineTime = getParsedDate("10:00AM");
                break;
            case "3.0":
                routineTime = getParsedDate("11:30AM");
                break;
            case "4.0":
                routineTime = getParsedDate("01:00PM");
                break;
            case "5.0":
                routineTime = getParsedDate("02:30PM");
                break;
            case "6.0":
                routineTime = getParsedDate("04:00PM");
                break;
            case "1.5":
                routineTime = getParsedDate("09:00AM");
                break;
            case "2.5":
                routineTime = getParsedDate("11:00AM");
                break;
            case "4.5":
                routineTime = getParsedDate("01:00PM");
                break;
            case "5.5":
                routineTime = getParsedDate("03:00PM");
                break;
            case "7.0":
                routineTime = getParsedDate("6:00PM");
                break;
            case "8.0":
                routineTime = getParsedDate("7:30PM");
                break;
            default:
                routineTime = null;
                break;
        }
        return routineTime;
    }

    private Calendar getParsedDate(String dateString) {

        SimpleDateFormat formater = new SimpleDateFormat("hh:mmaa");
        try {

            Calendar temp = Calendar.getInstance();
            temp.setTime(formater.parse(dateString));

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR, temp.get(Calendar.HOUR));
            calendar.set(Calendar.MINUTE, temp.get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.AM_PM, temp.get(Calendar.AM_PM));

            return calendar;

        } catch (ParseException e) {
            Timber.e(e);
        }
        return null;
    }

    private class Notification {

        String title = "Reminder";

        String description = "";

        long time = 0;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }
    }
}
