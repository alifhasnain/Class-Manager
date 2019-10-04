package bd.edu.daffodilvarsity.classmanager.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import bd.edu.daffodilvarsity.classmanager.routine.RoutineClassDetails;
import bd.edu.daffodilvarsity.classmanager.routine.RoutineClassDetailsDao;
import bd.edu.daffodilvarsity.classmanager.routine.RoutineClassDetailsDatabase;

public class EmptyRoomsViewModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<String>> mEmptyRoomsLiveData = new MutableLiveData<>();

    private MutableLiveData<String> mToastMsg = new MutableLiveData<>();

    private RoutineClassDetailsDao allClassesDao;

    public EmptyRoomsViewModel(@NonNull Application application) {
        super(application);

        RoutineClassDetailsDatabase db = RoutineClassDetailsDatabase.getInstance(application);
        allClassesDao = db.routineClassDetailsDao();

    }

    /*public void loadEmptyClasses(String day,String time)    {

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
    }*/

    public void loadEmptyRooms(final String day, final String time)    {
        new Thread(new Runnable() {
            @Override
            public void run() {

                List<RoutineClassDetails> emptyRoomsClassDetails = allClassesDao.getEmptyRooms(day,time,"");
                ArrayList<String> emptyRoomList =  new ArrayList<>();

                for(RoutineClassDetails rcd : emptyRoomsClassDetails)   {
                    emptyRoomList.add(rcd.getRoom());
                }

                mEmptyRoomsLiveData.postValue(emptyRoomList);

            }
        }).start();
    }

    public LiveData<ArrayList<String>> getEmptyClasses()  {
        return mEmptyRoomsLiveData;
    }

    public LiveData<String> showToast() {
        return mToastMsg;
    }

}
