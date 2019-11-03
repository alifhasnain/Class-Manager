package bd.edu.daffodilvarsity.classmanager.routine;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import bd.edu.daffodilvarsity.classmanager.otherclasses.ClassDetails;
import bd.edu.daffodilvarsity.classmanager.otherclasses.SharedPreferencesHelper;

public class EachDayClassRepository {

    private RoutineClassDetailsDao allClassesDao;

    private LiveData<List<RoutineClassDetails>> teacherClassListLiveData;

    private LiveData<List<RoutineClassDetails>> studentClassListLiveData;

    private String dayJsonString;

    private String eveningJsonString;

    public EachDayClassRepository(Application application) {

        RoutineClassDetailsDatabase db = RoutineClassDetailsDatabase.getInstance(application);

        allClassesDao = db.routineClassDetailsDao();

    }

    public LiveData<List<RoutineClassDetails>> loadClassesTeacher(String initial , String dayOfWeek)   {
        teacherClassListLiveData = allClassesDao.getClassesPerDayTeacher(initial,dayOfWeek);
        return teacherClassListLiveData;
    }

    public LiveData<List<RoutineClassDetails>> loadClassesStudent(List<String> courseCodeList, String shift, List<String> sectionList, String dayOfWeek)   {

        String queryString = "SELECT * FROM RoutineClassDetails WHERE ";

        List<String> customList = new ArrayList<>();

        for(int i=0 ; i<courseCodeList.size() ; i++)    {
            customList.add("(courseCode='" + courseCodeList.get(i) + "' AND section='" + sectionList.get(i) + "')");
        }

        String customString = "(";

        for(int i = 0 ; i < customList.size() ; i++)    {
            customString+=customList.get(i);
            if(i!=customList.size()-1)  {
                customString+=" OR ";
            }
        }

        customString += ")";

        queryString += customString + " AND shift='"+shift + "'" + " AND dayOfWeek='"+dayOfWeek + "'" + " ORDER BY priority";

        SimpleSQLiteQuery simpleSQLiteQuery = new SimpleSQLiteQuery(queryString);

        if(courseCodeList.size()>0 && sectionList.size()>0) {
            studentClassListLiveData =  allClassesDao.getClassesPerDayStudent(simpleSQLiteQuery);
        }

        if(studentClassListLiveData==null)  {
            //For sending an empty list so we don't get a null pointer exception
            List<RoutineClassDetails> emptyList = new ArrayList<>();
            MutableLiveData<List<RoutineClassDetails>> temp = new MutableLiveData<>();
            temp.setValue(emptyList);
            return temp;
        }
        else {
            return studentClassListLiveData;
        }
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

        String shift = SharedPreferencesHelper.getShiftFromSharedPreferences(context);

        HashMap<String, String> courseHashMap = SharedPreferencesHelper.getCoursesAndSectionMapFromSharedPreferences(context);

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

    /*public void loadWholeRoutineFromServer()    {

        StorageReference rootStorage = FirebaseStorage.getInstance().getReference();
        StorageReference dayRoutine = rootStorage.child("/main_campus/routine_day.txt");
        final StorageReference eveningRoutine = rootStorage.child("/main_campus/routine_evening.txt");

        final long MAX_DOWNLOAD_SIZE = 1024*1024;

        Task<byte[]> dayRoutineTask = dayRoutine.getBytes(MAX_DOWNLOAD_SIZE);
        Task<byte[]> eveningRoutineTask = eveningRoutine.getBytes(MAX_DOWNLOAD_SIZE);

        Task<List<byte[]>> allDownloadTask = Tasks.whenAllSuccess(dayRoutineTask,eveningRoutineTask);

        allDownloadTask.addOnSuccessListener(new OnSuccessListener<List<byte[]>>() {
            @Override
            public void onSuccess(List<byte[]> byteArrayList) {
                String dayJsonString = new String(byteArrayList.get(0),StandardCharsets.UTF_8);
                String eveningJsonString = new String(byteArrayList.get(1),StandardCharsets.UTF_8);

                saveJsonRoutineToRoomDatabase(dayJsonString,eveningJsonString);

            }
        });

    }*/

    public void saveJsonRoutineToRoomDatabase(final String dayJsonString, final String eveningJsonString) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<ClassDetails>>() {}.getType();

                ArrayList<ClassDetails> dayClasses = gson.fromJson(dayJsonString,type);
                ArrayList<ClassDetails> eveningClasses = gson.fromJson(eveningJsonString,type);

                ArrayList<RoutineClassDetails> allClasses = new ArrayList<>();

                for(ClassDetails cd : dayClasses)   {
                    RoutineClassDetails rcd = new RoutineClassDetails(
                            cd.getRoom(),
                            cd.getCourseCode(),
                            cd.getCourseName(),
                            cd.getTeacherInitial(),
                            cd.getTime(),
                            cd.getDayOfWeek(),
                            cd.getShift(),
                            cd.getSection(),
                            cd.getPriority()
                    );
                    allClasses.add(rcd);
                }

                for(ClassDetails cd : eveningClasses)   {
                    RoutineClassDetails rcd = new RoutineClassDetails(
                            cd.getRoom(),
                            cd.getCourseCode(),
                            cd.getCourseName(),
                            cd.getTeacherInitial(),
                            cd.getTime(),
                            cd.getDayOfWeek(),
                            cd.getShift(),
                            cd.getSection(),
                            cd.getPriority()
                    );
                    allClasses.add(rcd);
                }

                allClassesDao.deleteAllClasses();
                allClassesDao.insertListOfItem(allClasses);
            }
        }).start();
    }

    public void setNotificationEnables(final RoutineClassDetails rcd)    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                allClassesDao.notificationEnabled(rcd);
            }
        }).start();
    }

    public List<RoutineClassDetails> getTodaysClasses(List<String> courseCodeList , List<String> sectionList , String shift)   {

        List<RoutineClassDetails> todaysClasses;

        String dayOfWeek = getDayOfWeek();

        String queryString = "SELECT * FROM RoutineClassDetails WHERE ";

        List<String> combinedList = new ArrayList<>();

        for(int i=0 ; i<courseCodeList.size() ; i++)    {
            combinedList.add("(courseCode='" + courseCodeList.get(i) + "' AND section='" + sectionList.get(i) + "')");
        }

        String courseCodeAndSectionQueryString = "(";

        for(int i = 0 ; i < combinedList.size() ; i++)    {
            courseCodeAndSectionQueryString+=combinedList.get(i);
            if(i!=combinedList.size()-1)  {
                courseCodeAndSectionQueryString+=" OR ";
            }
        }

        courseCodeAndSectionQueryString += ")";

        queryString += courseCodeAndSectionQueryString + " AND shift='"+shift + "'" + " AND dayOfWeek='"+dayOfWeek + "'" + " AND notificationEnabled=1";

        SimpleSQLiteQuery simpleSQLiteQuery = new SimpleSQLiteQuery(queryString);

        if(courseCodeList.size()>0 && sectionList.size()>0 && shift!=null) {
            todaysClasses =  allClassesDao.getTodaysClassesStudent(simpleSQLiteQuery);
            return todaysClasses;
        }
        else {
            todaysClasses = new ArrayList<>();
            return todaysClasses;
        }
    }

    public List<RoutineClassDetails> getTodaysClasses(String initial) {
        return allClassesDao.getTodaysClassesTeacher(initial,getDayOfWeek());
    }

    public List<String> loadTeacherCourses(String initial)  {
        return allClassesDao.getCoursesWithInitial(initial);
    }

    private String getDayOfWeek()  {

        Calendar calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_WEEK);

        String dayOfWeek = "";
        switch (day)
        {
            case 1:
                dayOfWeek = "Sunday";
                break;
            case 2:
                dayOfWeek = "Monday";
                break;
            case 3:
                dayOfWeek = "Tuesday";
                break;
            case 4:
                dayOfWeek = "Wednesday";
                break;
            case 5:
                dayOfWeek = "Thursday";
                break;
            case 6:
                dayOfWeek = "Friday";
                break;
            case 7:
                dayOfWeek = "Saturday";
                break;
        }

        return dayOfWeek;
    }

}
