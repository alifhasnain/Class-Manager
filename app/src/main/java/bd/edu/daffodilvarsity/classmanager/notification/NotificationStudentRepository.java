package bd.edu.daffodilvarsity.classmanager.notification;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class NotificationStudentRepository {

    private NotificationStudentDao notificationDao;

    public NotificationStudentRepository(Application application) {

        NotificationStudentDatabase db = NotificationStudentDatabase.getInstance(application);

        notificationDao = db.notificationStudentDao();

    }

    public void insertSingleItem(final NotificationObjStudent notificationObjStudent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                notificationDao.insertSingleItem(notificationObjStudent);

            }
        }).start();
    }

    public LiveData<List<NotificationObjStudent>> getAllNotification() {
        return notificationDao.getAllNotifications();
    }

    public void deleteAllNotifications() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                notificationDao.deleteAllNotifications();
            }
        }).start();
    }
}
