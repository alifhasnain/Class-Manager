package bd.edu.daffodilvarsity.classmanager.notification;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class NotificationStudentViewModel extends AndroidViewModel {

    private NotificationStudentRepository repository;

    private LiveData<List<NotificationObjStudent>> notifications;

    public NotificationStudentViewModel(@NonNull Application application) {
        super(application);
        repository = new NotificationStudentRepository(getApplication());
    }

    public void loadNotifications() {
        notifications = repository.getAllNotification();
    }

    public void deleteAllNotifications() {
        repository.deleteAllNotifications();
    }

    public LiveData<List<NotificationObjStudent>> getNotifications() {
        return notifications;
    }
}
