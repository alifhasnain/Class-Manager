package bd.edu.daffodilvarsity.classmanager.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import bd.edu.daffodilvarsity.classmanager.otherclasses.BookedClassDetails;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ClassDetails;

public class BookClassViewModel extends ViewModel {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private MutableLiveData<ArrayList<ClassDetails>> finalRoomList = new MutableLiveData<>();

    private MutableLiveData<String> toastText = new MutableLiveData<>();


    public void loadData(String dayOfWeek, Calendar calendar,String selectedTime)  {

        CollectionReference emptyRoomRef = db.collection("/main_campus_empty_classes");

        CollectionReference bookedRoomRef = db.collection("/booked_classes/");

        List<Task<QuerySnapshot>> taskList = new ArrayList<>();

        Task<QuerySnapshot> task1 = emptyRoomRef.whereEqualTo("dayOfWeek", dayOfWeek).whereEqualTo("time", selectedTime).get(Source.SERVER);

        taskList.add(task1);

        Timestamp timestamp = new Timestamp(calendar.getTime());

        Task<QuerySnapshot> task2 = bookedRoomRef.whereEqualTo("reserveTime", timestamp)
                .whereEqualTo("dayOfWeek", dayOfWeek)
                .whereEqualTo("time", selectedTime).get(Source.SERVER);

        taskList.add(task2);

        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(taskList);

        allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshotsList) {

                QuerySnapshot emptyClass = querySnapshotsList.get(0);
                QuerySnapshot bookedClass = querySnapshotsList.get(1);

                ArrayList<ClassDetails> emptyClassList = new ArrayList<>();

                ArrayList<BookedClassDetails> bookedClassList = new ArrayList<>();

                for(DocumentSnapshot ds : emptyClass)   {
                    emptyClassList.add(ds.toObject(ClassDetails.class));
                }

                for (DocumentSnapshot ds : bookedClass) {
                    bookedClassList.add(ds.toObject(BookedClassDetails.class));
                }

                finalRoomList.setValue(getModifiedList(emptyClassList,bookedClassList));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastText.setValue("Failed to load data.Please check your internet connection.");
            }
        });

    }

    private ArrayList<ClassDetails> getModifiedList(ArrayList<ClassDetails> emptyRooms,ArrayList<BookedClassDetails> bookedRooms)   {

        for(BookedClassDetails bcd : bookedRooms)   {

            String room = bcd.getRoomNo();

            for(int i = 0 ; i< emptyRooms.size() ; i++) {
                if(emptyRooms.get(i).getRoom().equals(room))    {
                    emptyRooms.remove(i);
                }
            }
        }

        return emptyRooms;
    }

    public LiveData<ArrayList<ClassDetails>> getData()  {
        return finalRoomList;
    }

    public LiveData<String> showToast() {
        return toastText;
    }
}
