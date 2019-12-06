package bd.edu.daffodilvarsity.classmanager.CustomRoutineSearch;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;
import bd.edu.daffodilvarsity.classmanager.routine.RoutineClassDetails;
import bd.edu.daffodilvarsity.classmanager.routine.RoutineClassDetailsDao;
import bd.edu.daffodilvarsity.classmanager.routine.RoutineClassDetailsDatabase;

public class CustomRoutineSearchViewModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<RoutineClassDetails>> teacherClassesLiveData = new MutableLiveData<>();

    private MutableLiveData<ArrayList<RoutineClassDetails>> studentClassesLiveData = new MutableLiveData<>();

    private MutableLiveData<String> toast = new MutableLiveData<>();

    private RoutineClassDetailsDao allClassesDao;

    public CustomRoutineSearchViewModel(@NonNull Application application) {
        super(application);
        RoutineClassDetailsDatabase db = RoutineClassDetailsDatabase.getInstance(application);
        allClassesDao = db.routineClassDetailsDao();
    }

    public void loadTeacherClasses(final String teacherInitial, final String day)  {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(day.equals("All"))   {
                    ArrayList<RoutineClassDetails> classes = (ArrayList<RoutineClassDetails>) allClassesDao.getClassesWithInitial(teacherInitial);
                    teacherClassesLiveData.postValue(getSortedClassList(classes));
                }
                else {
                    ArrayList<RoutineClassDetails> classes = (ArrayList<RoutineClassDetails>) allClassesDao.getClassesWithInitial(teacherInitial,day);
                    teacherClassesLiveData.postValue(classes);
                }

            }
        }).start();
    }

    public void loadStudentClasses(String level, String term , final String shift, final String section) {

        HelperClass helperClass = HelperClass.getInstance();

        final ArrayList<String> courseList = helperClass.getCourseList(HelperClass.PROGRAM_BSC,shift,level,term);

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<RoutineClassDetails> studentRoutines = (ArrayList<RoutineClassDetails>) allClassesDao.getClassesStudent(shift,section,courseList);
                studentClassesLiveData.postValue(getSortedClassList(studentRoutines));
            }
        }).start();

    }

    public LiveData<ArrayList<RoutineClassDetails>> getTeacherClasses()    {
        return teacherClassesLiveData;
    }

    public LiveData<ArrayList<RoutineClassDetails>> getStudentClasses() {
        return studentClassesLiveData;
    }

    private ArrayList<RoutineClassDetails> getSortedClassList(ArrayList<RoutineClassDetails> classesList)   {

        Collections.sort(classesList, new Comparator<RoutineClassDetails>() {
            @Override
            public int compare(RoutineClassDetails t1, RoutineClassDetails t2) {
                return Float.compare(t1.getPriority(), t2.getPriority());
            }
        });

        ArrayList<RoutineClassDetails> modifiedList = new ArrayList<>();

        ArrayList<RoutineClassDetails> saturday = new ArrayList<>();
        ArrayList<RoutineClassDetails> sunday = new ArrayList<>();
        ArrayList<RoutineClassDetails> monday = new ArrayList<>();
        ArrayList<RoutineClassDetails> tuesday = new ArrayList<>();
        ArrayList<RoutineClassDetails> wednesday = new ArrayList<>();
        ArrayList<RoutineClassDetails> thursday = new ArrayList<>();

        for(RoutineClassDetails rcd : classesList)  {
            switch (rcd.getDayOfWeek())
            {
                case "Saturday":
                    saturday.add(rcd);
                    break;
                case "Sunday":
                    sunday.add(rcd);
                    break;
                case "Monday":
                    monday.add(rcd);
                    break;
                case "Tuesday":
                    tuesday.add(rcd);
                    break;
                case "Wednesday":
                    wednesday.add(rcd);
                    break;
                case "Thursday":
                    thursday.add(rcd);
                    break;
            }
        }


        if(saturday.size()>0)   {
            RoutineClassDetails routineClassDetails = new RoutineClassDetails();
            routineClassDetails.setPriority(1000f);
            routineClassDetails.setDayOfWeek("Saturday");
            saturday.add(0,routineClassDetails);
            modifiedList.addAll(saturday);
        }
        if(sunday.size()>0) {
            RoutineClassDetails routineClassDetails = new RoutineClassDetails();
            routineClassDetails.setPriority(1000f);
            routineClassDetails.setDayOfWeek("Sunday");
            sunday.add(0,routineClassDetails);
            modifiedList.addAll(sunday);
        }
        if(monday.size()>0) {
            RoutineClassDetails routineClassDetails = new RoutineClassDetails();
            routineClassDetails.setPriority(1000f);
            routineClassDetails.setDayOfWeek("Monday");
            monday.add(0,routineClassDetails);
            modifiedList.addAll(monday);
        }
        if(tuesday.size()>0) {
            RoutineClassDetails routineClassDetails = new RoutineClassDetails();
            routineClassDetails.setPriority(1000f);
            routineClassDetails.setDayOfWeek("Tuesday");
            tuesday.add(0,routineClassDetails);
            modifiedList.addAll(tuesday);
        }
        if(wednesday.size()>0) {
            RoutineClassDetails routineClassDetails = new RoutineClassDetails();
            routineClassDetails.setPriority(1000f);
            routineClassDetails.setDayOfWeek("Wednesday");
            wednesday.add(0,routineClassDetails);
            modifiedList.addAll(wednesday);
        }
        if(thursday.size()>0) {
            RoutineClassDetails routineClassDetails = new RoutineClassDetails();
            routineClassDetails.setPriority(1000f);
            routineClassDetails.setDayOfWeek("Thursday");
            thursday.add(0,routineClassDetails);
            modifiedList.addAll(thursday);
        }

        return modifiedList;

    }

    public LiveData<String> showToast() {
        return toast;
    }

}
