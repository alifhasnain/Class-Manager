package bd.edu.daffodilvarsity.classmanager.routine;

import android.app.Application;

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

import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectTeacher;
import bd.edu.daffodilvarsity.classmanager.otherclasses.SharedPreferencesHelper;

public class EachDayClassViewModel extends AndroidViewModel {

    private SharedPreferencesHelper mSharedPrefHelper = new SharedPreferencesHelper();

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

                            mSharedPrefHelper.saveTeacherInitialToSharedPref(getApplication(),initial);

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dataRepo.loadWholeRoutineFromServer(mSharedPrefHelper.getTeacherInitialFromSharedPref(getApplication()));
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
}
