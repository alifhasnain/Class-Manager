package bd.edu.daffodilvarsity.classmanager.routine;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {RoutineClassDetails.class} , version = 1, exportSchema = false)
public abstract class RoutineClassDetailsDatabase extends RoomDatabase {

    private static RoutineClassDetailsDatabase sInstance;

    public abstract RoutineClassDetailsDao routineClassDetailsDao();

    public static synchronized RoutineClassDetailsDatabase getInstance(Context context) {

        if(sInstance==null) {
            sInstance = Room.databaseBuilder(context.getApplicationContext(),RoutineClassDetailsDatabase.class,"routine_classes")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return sInstance;
    }

}
