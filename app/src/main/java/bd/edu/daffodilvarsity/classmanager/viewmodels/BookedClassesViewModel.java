package bd.edu.daffodilvarsity.classmanager.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

import bd.edu.daffodilvarsity.classmanager.otherclasses.BookedClassDetailsUser;

public class BookedClassesViewModel extends ViewModel {

    private MutableLiveData<ArrayList<BookedClassDetailsUser>> bookedClassesLiveData = new MutableLiveData<>();

    private MutableLiveData<String> toastMsgLiveData = new MutableLiveData<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public void loadDataFromServer() {

        if (mAuth.getCurrentUser() == null) {
            toastMsgLiveData.setValue("Not signed in.");
            return;
        }

        String teacherEmail = mAuth.getCurrentUser().getEmail();

        final ArrayList<BookedClassDetailsUser> bookedClassesList = new ArrayList<>();

        CollectionReference bookedClassesRef = db.collection("/booked_classes/");

        Calendar currentTime = Calendar.getInstance();
        currentTime.add(Calendar.HOUR_OF_DAY, -21);

        Timestamp timestamp = new Timestamp(currentTime.getTime());

        bookedClassesRef.whereEqualTo("teacherEmail", teacherEmail)
                .whereGreaterThanOrEqualTo("reservationDate", timestamp)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (DocumentSnapshot ds : queryDocumentSnapshots) {

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

    public LiveData<ArrayList<BookedClassDetailsUser>> getBookedClassList() {
        return bookedClassesLiveData;
    }

    public LiveData<String> displayToast() {
        return toastMsgLiveData;
    }

}
