package bd.edu.daffodilvarsity.classmanager.routine;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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

    @Query("SELECT * FROM RoutineClassDetails WHERE (courseCode IN (:courseCodeList)) AND shift=:shift AND (section IN (:sectionList)) AND dayOfWeek=:dayOfWeek ORDER BY priority")
    LiveData<List<RoutineClassDetails>> getClassesPerDayStudent(List<String> courseCodeList,String shift,List<String> sectionList,String dayOfWeek);

    @Query("SELECT * FROM RoutineClassDetails WHERE teacherInitial=:initial AND dayOfWeek=:dayOfWeek ORDER BY priority")
    LiveData<List<RoutineClassDetails>> getClassesPerDayTeacher(String initial, String dayOfWeek);

    @Query("SELECT * FROM RoutineClassDetails WHERE dayOfWeek=:dayOfWeek AND notificationEnabled=1")
    List<RoutineClassDetails> getTodaysClasses(String dayOfWeek);
}
