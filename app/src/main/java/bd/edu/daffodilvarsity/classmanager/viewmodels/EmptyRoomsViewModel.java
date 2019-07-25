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

import bd.edu.daffodilvarsity.classmanager.otherclasses.ClassDetails;

public class EmptyRoomsViewModel extends ViewModel {

    private MutableLiveData<ArrayList<String>> mEmptyRoomsLiveData = new MutableLiveData<>();

    private MutableLiveData<String> mToastMsg = new MutableLiveData<>();

    public void loadEmptyClasses(String day,String time)    {

        final ArrayList<String> emptyRooms = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("/main_campus_empty_classes/")
                .whereEqualTo("dayOfWeek",day)
                .whereEqualTo("time",time)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot ds : queryDocumentSnapshots)   {
                    emptyRooms.add(ds.toObject(ClassDetails.class).getRoom());
                }
                mEmptyRoomsLiveData.setValue(emptyRooms);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mToastMsg.setValue("Error loading data.Please check your internet connection.");
            }
        });
    }

    public LiveData<ArrayList<String>> getEmptyClasses()  {
        return mEmptyRoomsLiveData;
    }

    public LiveData<String> showToast() {
        return mToastMsg;
    }

}
