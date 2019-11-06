package bd.edu.daffodilvarsity.classmanager.notification;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NotificationStudentDao {

    @Insert
    void insertSingleItem(NotificationObjStudent notificationObjStudent);

    @Query("SELECT * FROM NotificationObjStudent ORDER BY id DESC")
    LiveData<List<NotificationObjStudent>> getAllNotifications();

    @Query("DELETE FROM NotificationObjStudent")
    void deleteAllNotifications();

}
