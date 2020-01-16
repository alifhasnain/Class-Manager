package bd.edu.daffodilvarsity.classmanager.routine;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

@Dao
public interface RoutineClassDetailsDao {

    @Insert
    void insertListOfItem(List<RoutineClassDetails> classesList);

    @Query("DELETE FROM RoutineClassDetails")
    void deleteAllClasses();

    @Update()
    void notificationEnabled(RoutineClassDetails rcd);

    //Get student classes for routine display
    @RawQuery(observedEntities = RoutineClassDetails.class)
    LiveData<List<RoutineClassDetails>> getClassesPerDayStudent(SupportSQLiteQuery query);

    //Get teachers classes for routine display
    @Query("SELECT * FROM RoutineClassDetails WHERE teacherInitial=:initial AND dayOfWeek=:dayOfWeek AND courseCode<>'' ORDER BY priority")
    LiveData<List<RoutineClassDetails>> getClassesPerDayTeacher(String initial, String dayOfWeek);

    //This Query is for notification
    @RawQuery
    List<RoutineClassDetails> getTodaysClassesStudent(SupportSQLiteQuery query);

    //For getting student routine for custom search
    @Query("SELECT * FROM RoutineClassDetails WHERE shift=:shift AND section=:section AND courseCode IN (:courseCodes)")
    List<RoutineClassDetails> getClassesStudent(String shift, String section, List<String> courseCodes);

    //For displaying notification
    @Query("SELECT * FROM RoutineClassDetails WHERE teacherInitial=:teacherInitial AND dayOfWeek=:dayOfWeek AND notificationEnabled=1")
    List<RoutineClassDetails> getTodaysClassesTeacher(String teacherInitial, String dayOfWeek);

    //Gets empty rooms
    @Query("SELECT * FROM RoutineClassDetails WHERE dayOfWeek=:dayOfWeek AND time=:time AND courseCode=:courseCode")
    List<RoutineClassDetails> getEmptyRooms(String dayOfWeek, String time, String courseCode);

    //For custom query with teacher initial with day of week
    @Query("SELECT * FROM RoutineClassDetails WHERE teacherInitial=:teacherInitial AND dayOfWeek=:day AND courseCode<>'' ORDER BY priority ")
    List<RoutineClassDetails> getClassesWithInitial(String teacherInitial, String day);

    //For custom query with teacher initial without day of week
    @Query("SELECT * FROM RoutineClassDetails WHERE teacherInitial=:teacherInitial AND courseCode<>'' ORDER BY priority ")
    List<RoutineClassDetails> getClassesWithInitial(String teacherInitial);

    //Custom search with course code
    @Query("SELECT * FROM RoutineClassDetails WHERE courseCode=:courseCode AND shift=:shift ORDER BY priority")
    List<RoutineClassDetails> getClassesWithCourseCode(String courseCode, String shift);

    //For getting course list with teacher initial
    @Query("SELECT DISTINCT courseCode FROM RoutineClassDetails WHERE teacherInitial=:teacherInitial")
    List<String> getCoursesWithInitial(String teacherInitial);

    //For getting teacher section list with initial
    @Query("SELECT DISTINCT section FROM RoutineClassDetails WHERE teacherInitial=:teacherInitial ORDER BY section")
    List<String> getTeacherSectionsWithInitial(String teacherInitial);

}
