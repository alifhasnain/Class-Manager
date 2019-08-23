package bd.edu.daffodilvarsity.classmanager.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import bd.edu.daffodilvarsity.classmanager.otherclasses.ClassDetails;

public class CustomRoutineSearchViewModel extends ViewModel {

    private MutableLiveData<ArrayList<ClassDetails>> classesListLiveData = new MutableLiveData<>();

    private MutableLiveData<String> toast = new MutableLiveData<>();

    public void loadClasses(String teacherInitial, String day) {

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

    }

    public LiveData<ArrayList<ClassDetails>> getClasses()    {
        return classesListLiveData;
    }

    public LiveData<String> showToast() {
        return toast;
    }

}
