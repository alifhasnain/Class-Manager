package bd.edu.daffodilvarsity.classmanager.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classmanager.otherclasses.BookedClassDetailsUser;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectTeacher;

public class BookedClassesViewModel extends ViewModel {

    private MutableLiveData<ArrayList<BookedClassDetailsUser>> bookedClassesLiveData = new MutableLiveData<>();

    private MutableLiveData<String> toastMsgLiveData = new MutableLiveData<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public void loadDataFromServer()    {

        DocumentReference userProfile = db.document("/teacher_profiles/"+mAuth.getCurrentUser().getEmail()+"/");

        userProfile.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())   {
                    ProfileObjectTeacher profile = documentSnapshot.toObject(ProfileObjectTeacher.class);
                    loadBookedClasses(profile.getTeacherInitial());
                }
                else    {
                    loadBookedClasses("xxxxxxxxxxxxxxxxxxxxxxx");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastMsgLiveData.setValue("Failed to load data. Please check your internet connection.");
            }
        });

    }

    private void loadBookedClasses(String teacherInitial) {

        final ArrayList<BookedClassDetailsUser> bookedClassesList = new ArrayList<>();

        CollectionReference bookedClassesRef = db.collection("/booked_classes/");

        bookedClassesRef.whereEqualTo("teacherInitial",teacherInitial).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(DocumentSnapshot ds : queryDocumentSnapshots)   {

                    BookedClassDetailsUser bcd = ds.toObject(BookedClassDetailsUser.class);

                    bcd.setDocId(ds.getId());

                    bookedClassesList.add(bcd);

                }

                bookedClassesLiveData.setValue(bookedClassesList);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastMsgLiveData.setValue("Failed to load data. Please check your internet connection.");
            }
        });
    }

    public LiveData<ArrayList<BookedClassDetailsUser>> getBookedClassList()   {
        return bookedClassesLiveData;
    }

    public LiveData<String> displayToast()    {
        return toastMsgLiveData;
    }

}
