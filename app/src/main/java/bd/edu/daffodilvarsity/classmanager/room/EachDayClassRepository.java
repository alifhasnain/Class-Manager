package bd.edu.daffodilvarsity.classmanager.room;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bd.edu.daffodilvarsity.classmanager.otherclasses.ClassDetails;
import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;

import static android.content.Context.MODE_PRIVATE;

public class EachDayClassRepository {

    private RoutineClassDetailsDao allClassesDao;

    private LiveData<List<RoutineClassDetails>> teacherClassListLiveData;

    private LiveData<List<RoutineClassDetails>> studentClassListLiveData;

    public EachDayClassRepository(Application application) {

        RoutineClassDetailsDatabase db = RoutineClassDetailsDatabase.getInstance(application);

        allClassesDao = db.routineClassDetailsDao();

    }

    public LiveData<List<RoutineClassDetails>> loadClassesTeacher(String initial , String dayOfWeek)   {
        teacherClassListLiveData = allClassesDao.getClassesPerDayTeacher(initial,dayOfWeek);
        return teacherClassListLiveData;
    }

    public LiveData<List<RoutineClassDetails>> loadClassesStudent(List<String> courseCodeList, String shift, List<String> sectionList, String dayOfWeek)   {
        studentClassListLiveData =  allClassesDao.getClassesPerDayStudent(courseCodeList,shift,sectionList,dayOfWeek);
        return studentClassListLiveData;
    }

    /*public LiveData<List<RoutineClassDetails>> getClasses()    {
        return classesListLiveData;
    }*/

    public void loadWholeRoutineFromServer(String initial)    {

        final List<RoutineClassDetails> routineClassList = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("main_campus").whereEqualTo("teacherInitial",initial).get(Source.SERVER)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for(DocumentSnapshot ds : queryDocumentSnapshots)   {

                            ClassDetails classDetails = ds.toObject(ClassDetails.class);

                            RoutineClassDetails routineClass = new RoutineClassDetails(
                                    classDetails.getRoom(),
                                    classDetails.getCourseCode(),
                                    classDetails.getCourseName(),
                                    classDetails.getTeacherInitial(),
                                    classDetails.getTime(),
                                    classDetails.getDayOfWeek(),
                                    classDetails.getShift(),
                                    classDetails.getSection(),
                                    classDetails.getPriority()
                            );

                            routineClassList.add(routineClass);

                        }

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                allClassesDao.deleteAllClasses();
                                allClassesDao.insertListOfItem(routineClassList);
                            }
                        }).start();
                    }
                });
    }

    public void loadWholeRoutineFromServer(Context context)    {

        final ArrayList<RoutineClassDetails> classesList = new ArrayList<>();

        String shift = getShiftFromSharedPreferences(context);

        HashMap<String, String> courseHashMap = getCoursesFromSharedPreferences(context);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        List<Task<QuerySnapshot>> taskList = new ArrayList<>();

        for(HashMap.Entry<String,String> entry : courseHashMap.entrySet()) {

            String courseCode = entry.getKey();
            String section = entry.getValue();

            taskList.add(db.collection("main_campus")
                    .whereEqualTo("courseCode",courseCode)
                    .whereEqualTo("section",section)
                    .whereEqualTo("shift",shift)
                    .get()
            );
        }

        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(taskList);

        allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                for(QuerySnapshot qs : querySnapshots)  {

                    for(DocumentSnapshot ds : qs)   {

                        ClassDetails classObj = ds.toObject(ClassDetails.class);

                        RoutineClassDetails rcdObj = new RoutineClassDetails(
                                classObj.getRoom(),
                                classObj.getCourseCode(),
                                classObj.getCourseName(),
                                classObj.getTeacherInitial(),
                                classObj.getTime(),
                                classObj.getDayOfWeek(),
                                classObj.getShift(),
                                classObj.getSection(),
                                classObj.getPriority()
                        );

                        classesList.add(rcdObj);
                    }

                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        allClassesDao.deleteAllClasses();
                        allClassesDao.insertListOfItem(classesList);
                    }
                }).start();
            }
        });
    }

    private String getShiftFromSharedPreferences(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG,MODE_PRIVATE);

        return sharedPreferences.getString(HelperClass.SHIFT,"");

    }

    private HashMap<String, String> getCoursesFromSharedPreferences(Context context) {

        HashMap<String, String> courseHashMap;

        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);

        Gson gson = new Gson();

        String coursesInJson = sharedPreferences.getString(HelperClass.COURSES_HASH_MAP, null);

        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();

        courseHashMap = gson.fromJson(coursesInJson, type);

        if (courseHashMap == null) {
            return new HashMap<String, String>();
        } else {
            return courseHashMap;
        }
    }

}
