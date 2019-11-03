package bd.edu.daffodilvarsity.classmanager.notification;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {NotificationObjStudent.class} , version = 1 , exportSchema = false)
public abstract class NotificationStudentDatabase extends RoomDatabase {

    private static NotificationStudentDatabase sInstance;

    public abstract NotificationStudentDao notificationStudentDao();

    public static synchronized NotificationStudentDatabase getInstance(Context context) {

        if(sInstance==null) {
            sInstance = Room.databaseBuilder(context.getApplicationContext() , NotificationStudentDatabase.class,"student_notification")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return sInstance;
    }

}
