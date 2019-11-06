package bd.edu.daffodilvarsity.classmanager.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.DateFormatSymbols;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.activities.SignIn;
import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;
import bd.edu.daffodilvarsity.classmanager.otherclasses.SharedPreferencesHelper;

import static bd.edu.daffodilvarsity.classmanager.BaseApplication.FCM_NOTIFICATION_CHANNEL_ID;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        Map<String,String> receivedData = remoteMessage.getData();

        if (isUserStudent() && isNotificationEnabled() && isCourseIsTaken(receivedData.get("courseCode"))) {

            String date = receivedData.get("day") + " " + getMonthNameFromNumber(Integer.parseInt(receivedData.get("month"))) + " " + receivedData.get("year");

            String title = "New Room Booked";

            String description = "New extra class scheduled for " + receivedData.get("courseCode")
                    + " in " + receivedData.get("roomNo") + " at " + receivedData.get("time") + " on " + date ;

            String courseName = getCourseName(receivedData.get("shift") , receivedData.get("courseCode"));

            NotificationObjStudent notificationObjStudent = new NotificationObjStudent(
                    receivedData.get("name"),
                    receivedData.get("email"),
                    receivedData.get("courseCode"),
                    courseName,
                    receivedData.get("section"),
                    receivedData.get("shift"),
                    receivedData.get("time"),
                    receivedData.get("roomNo"),
                    date
            );

            showNotification(title,description);

            NotificationStudentRepository repository = new NotificationStudentRepository(getApplication());

            repository.insertSingleItem(notificationObjStudent);

        }

    }

    private String getCourseName(String shift , String courseCode) {

        if (shift.equals(HelperClass.SHIFT_DAY)) {
            Map<String,String> courseMap = HelperClass.getCoursesDay();
            return courseMap.get(courseCode);
        } else if(shift.equals(HelperClass.SHIFT_EVENING)) {
            Map<String,String> courseMap = HelperClass.getCoursesEveningBsc();
            courseMap.get(courseCode);
        }

        return "";

    }

    private boolean isUserStudent() {
        return SharedPreferencesHelper.getUserType(getApplicationContext()).equals(HelperClass.USER_TYPE_STUDENT);
    }

    private boolean isNotificationEnabled() {
        return SharedPreferencesHelper.getStudentNotificatinStatus(getBaseContext());
    }

    private boolean isCourseIsTaken(String courseCode) {

        HashMap<String,String> coursesMap = SharedPreferencesHelper.getCoursesAndSectionMapFromSharedPreferences(getApplicationContext());

        return coursesMap.get(courseCode) != null;

    }

    private void showNotification(String title, String description) {

        Intent intent = new Intent(getApplicationContext(), SignIn.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), FCM_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                .setContentText(description)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        notificationManager.notify(generateRandomInt(), notification);
    }

    private int generateRandomInt()    {
        Random random = new Random();
        return random.nextInt(4000);
    }

    private String getMonthNameFromNumber(int num) {
        return new DateFormatSymbols().getMonths()[num];
    }

}
