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
import java.util.Calendar;
import java.util.List;

import bd.edu.daffodilvarsity.classmanager.broadcastreceiver.NotificationReceiver;
import bd.edu.daffodilvarsity.classmanager.routine.EachDayClassRepository;
import bd.edu.daffodilvarsity.classmanager.routine.RoutineClassDetails;

import static android.content.Context.ALARM_SERVICE;
import static bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass.NOTIFICATION_ALARM_REQ_CODE;

public class ReminderSchedulerWorker extends Worker {

    public ReminderSchedulerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        EachDayClassRepository repo = new EachDayClassRepository((Application) getApplicationContext());

        List<RoutineClassDetails> classes = repo.getTodaysClasses();

        String title = "Reminder";

        long minTime = getMinimumTimeFromPriority(classes);

        if(minTime != 0 && (minTime-System.currentTimeMillis() < 18000000))   {
            scheduleAlarm(title,"You have class after sometime be prepared.",minTime);
        }
        else {
            cancelAlarm();
        }

        return Result.success();
    }

    private void scheduleAlarm(String titleString,String descriptionString,long time)    {

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        intent.putExtra("title",titleString);
        intent.putExtra("description",descriptionString);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),NOTIFICATION_ALARM_REQ_CODE,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP,time,pendingIntent);

    }

    private void cancelAlarm()  {
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),NOTIFICATION_ALARM_REQ_CODE,intent,0);

        alarmManager.cancel(pendingIntent);

    }

    private long getMinimumTimeFromPriority(List<RoutineClassDetails> classDetails) {

        Calendar currentTime = Calendar.getInstance();

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.add(Calendar.DATE,4);

        Calendar lowestTime = compareCalendar;

        for(RoutineClassDetails routineClassDetails : classDetails)  {
            if((getTimeForPriority(routineClassDetails.getPriority()).getTimeInMillis() - currentTime.getTimeInMillis() - 900000)>0) {
                if((getTimeForPriority(routineClassDetails.getPriority()).getTimeInMillis() <lowestTime.getTimeInMillis()) && (getTimeForPriority(routineClassDetails.getPriority()).getTimeInMillis() > currentTime.getTimeInMillis() ))  {
                    lowestTime = getTimeForPriority(routineClassDetails.getPriority());
                }
            }
        }

        if(lowestTime.getTimeInMillis()==currentTime.getTimeInMillis()) {
            return 0;
        }
        else {
            return lowestTime.getTimeInMillis();
        }

    }

    private Calendar getTimeForPriority(float priority)   {

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
            e.printStackTrace();
        }
        return null;
    }
}
