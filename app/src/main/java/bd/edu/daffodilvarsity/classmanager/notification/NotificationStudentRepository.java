package bd.edu.daffodilvarsity.classmanager.notification;

import android.app.Application;

public class NotificationStudentRepository {

    private NotificationStudentDao notificationDao;

    public NotificationStudentRepository(Application application) {

        NotificationStudentDatabase db = NotificationStudentDatabase.getInstance(application);

        notificationDao = db.notificationStudentDao();

    }
}
