package bd.edu.daffodilvarsity.classmanager.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classmanager.otherclasses.BookedClassDetails;

public class SearchBookedClassesWithDateViewModel extends ViewModel {

    private MutableLiveData<ArrayList<BookedClassDetails>> mBookedClassesLiveData = new MutableLiveData<>();

    private MutableLiveData<String> mToastMsg = new MutableLiveData<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void loadBookedClasses(Timestamp time) {
        db.collection("booked_classes").whereEqualTo("reservationDate",time).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        ArrayList<BookedClassDetails> classes = new ArrayList<>();

                        for(DocumentSnapshot ds : queryDocumentSnapshots)   {
                            BookedClassDetails temp = ds.toObject(BookedClassDetails.class);
                            temp.setDocId(ds.getId());
                            classes.add(temp);
                        }

                        mBookedClassesLiveData.setValue(classes);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mToastMsg.setValue("Failed to load. Please check your internet connection.");
                    }
                });
    }

    public LiveData<ArrayList<BookedClassDetails>> getBookedClasses()  {
        return mBookedClassesLiveData;
    }

    public LiveData<String> getToastMsg()   {
        return mToastMsg;
    }
}
