package bd.edu.daffodilvarsity.classmanager.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classmanager.routine.RoutineClassDetails;
import bd.edu.daffodilvarsity.classmanager.routine.RoutineClassDetailsDao;
import bd.edu.daffodilvarsity.classmanager.routine.RoutineClassDetailsDatabase;

public class CustomRoutineSearchViewModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<RoutineClassDetails>> classesListLiveData = new MutableLiveData<>();

    private MutableLiveData<String> toast = new MutableLiveData<>();

    private RoutineClassDetailsDao allClassesDao;

    public CustomRoutineSearchViewModel(@NonNull Application application) {
        super(application);
        RoutineClassDetailsDatabase db = RoutineClassDetailsDatabase.getInstance(application);
        allClassesDao = db.routineClassDetailsDao();
    }

    /*public void loadClasses(String teacherInitial, String day) {

        FirebaseFirestore.getInstance().collection("/main_campus/")
                .whereEqualTo("teacherInitial", teacherInitial)
                .whereEqualTo("dayOfWeek", day)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        ArrayList<ClassDetails> classesList = new ArrayList<>();

                        for(DocumentSnapshot ds : queryDocumentSnapshots)   {
                            classesList.add(ds.toObject(ClassDetails.class));
                        }

                        Collections.sort(classesList, new Comparator<ClassDetails>() {
                            @Override
                            public int compare(ClassDetails o1, ClassDetails o2) {
                                return Float.compare(o1.getPriority(),o2.getPriority());
                            }
                        });

                        classesListLiveData.setValue(classesList);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toast.setValue("Failed to load.Please check your internet connection.");
                    }
                });

    }*/

    public void loadClasses(final String teacherInitial, final String day)  {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(day.equals("All"))   {
                    ArrayList<RoutineClassDetails> classes = (ArrayList<RoutineClassDetails>) allClassesDao.getClassesWithInitial(teacherInitial);
                    classesListLiveData.postValue(getModifiedClassList(classes));
                }
                else {
                    ArrayList<RoutineClassDetails> classes = (ArrayList<RoutineClassDetails>) allClassesDao.getClassesWithInitial(teacherInitial,day);
                    classesListLiveData.postValue(classes);
                }

            }
        }).start();
    }

    public LiveData<ArrayList<RoutineClassDetails>> getClasses()    {
        return classesListLiveData;
    }

    private ArrayList<RoutineClassDetails> getModifiedClassList(ArrayList<RoutineClassDetails> classesList)   {

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
