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
    void insertSingleItem(RoutineClassDetails classDetails);

    @Insert
    void insertListOfItem(List<RoutineClassDetails> classesList);

    @Query("DELETE FROM RoutineClassDetails")
    void deleteAllClasses();

    @Update()
    void notificationEnabled(RoutineClassDetails rcd);

    /*@Query("SELECT * FROM RoutineClassDetails WHERE (courseCode IN (:courseCodeList)) AND shift=:shift AND (section IN (:sectionList)) AND dayOfWeek=:dayOfWeek  ORDER BY priority")
    LiveData<List<RoutineClassDetails>> getClassesPerDayStudent(List<String> courseCodeList,String shift,List<String> sectionList,String dayOfWeek);*/
    /*@Query("SELECT * FROM RoutineClassDetails WHERE :customString AND shift=:shift AND dayOfWeek=:dayOfWeek  ORDER BY priority")
    LiveData<List<RoutineClassDetails>> getClassesPerDayStudent(String shift,String dayOfWeek,String customString);*/

    @RawQuery(observedEntities = RoutineClassDetails.class)
    LiveData<List<RoutineClassDetails>> getClassesPerDayStudent(SupportSQLiteQuery query);

    @Query("SELECT * FROM RoutineClassDetails WHERE teacherInitial=:initial AND dayOfWeek=:dayOfWeek AND courseCode<>'' ORDER BY priority")
    LiveData<List<RoutineClassDetails>> getClassesPerDayTeacher(String initial, String dayOfWeek);

    @RawQuery
    List<RoutineClassDetails> getTodaysClassesStudent(SupportSQLiteQuery query);

    @Query("SELECT * FROM RoutineClassDetails WHERE teacherInitial=:teacherInitial AND dayOfWeek=:dayOfWeek AND notificationEnabled=1")
    List<RoutineClassDetails> getTodaysClassesTeacher(String teacherInitial,String dayOfWeek);

    @Query("SELECT * FROM RoutineClassDetails WHERE dayOfWeek=:dayOfWeek AND time=:time AND courseCode=:courseCode")
    List<RoutineClassDetails> getEmptyRooms(String dayOfWeek,String time,String courseCode);

    @Query("SELECT * FROM RoutineClassDetails WHERE teacherInitial=:teacherInitial AND dayOfWeek=:day AND courseCode<>''")
    List<RoutineClassDetails> getClassesWithInitial(String teacherInitial , String day);

    @Query("SELECT * FROM RoutineClassDetails WHERE teacherInitial=:teacherInitial")
    List<RoutineClassDetails> getClassesWithInitial(String teacherInitial);
}
