package bd.edu.daffodilvarsity.classmanager.room;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.List;

import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectTeacher;

public class EachDayClassViewModel extends AndroidViewModel {

    private LiveData<List<RoutineClassDetails>> classesListLiveData;

    private MutableLiveData<String> toastMsg = new MutableLiveData<>();

    private EachDayClassRepository dataRepo;

    public EachDayClassViewModel(@NonNull Application application) {
        super(application);
        dataRepo = new EachDayClassRepository(application);
    }

    public void loadWholeStudentRoutineFromServer()    {
        dataRepo.loadWholeRoutineFromServer(getApplication());
    }

    public void loadWholeTeacherRoutineFromServer()  {
        FirebaseFirestore.getInstance()
                .document("teacher_profiles/" + FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .get(Source.SERVER)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists())   {

                            ProfileObjectTeacher profile = documentSnapshot.toObject(ProfileObjectTeacher.class);

                            String initial = profile.getTeacherInitial();

                            dataRepo.loadWholeRoutineFromServer(initial);

                            saveTeacherInitialToSharedPref(initial);

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dataRepo.loadWholeRoutineFromServer(getTeacherInitialFromSharedPref());
                    }
                });
    }

    public void loadClassesStudent(List<String> courseCodeList, String shift, List<String> sectionList, String dayOfWeek) {
        classesListLiveData = dataRepo.loadClassesStudent(courseCodeList,shift,sectionList,dayOfWeek);
        //classesListLiveData = dataRepo.getClasses();
    }

    public void loadClassesTeacher(String initial,String dayOfWeek)   {
        classesListLiveData = dataRepo.loadClassesTeacher(initial,dayOfWeek);
        //classesListLiveData = dataRepo.getClasses();
    }

    public LiveData<List<RoutineClassDetails>> getClasses()    {
        return classesListLiveData;
    }

    public LiveData<String> getToastMsg()  {
        return toastMsg;
    }

    private void saveTeacherInitialToSharedPref(String initial) {

        SharedPreferences sharedPreferences = getApplication().getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG,Context.MODE_PRIVATE);

        sharedPreferences.edit().putString(HelperClass.TEACHER_INITIAL,initial).apply();

    }

    private String getTeacherInitialFromSharedPref()   {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG,Context.MODE_PRIVATE);
        return sharedPreferences.getString(HelperClass.TEACHER_INITIAL,"");
    }
}
