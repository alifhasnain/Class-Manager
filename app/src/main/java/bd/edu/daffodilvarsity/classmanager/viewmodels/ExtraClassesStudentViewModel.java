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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import bd.edu.daffodilvarsity.classmanager.otherclasses.BookedClassDetailsUser;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectStudent;

public class ExtraClassesStudentViewModel extends ViewModel {

    private MutableLiveData<ArrayList<BookedClassDetailsUser>> classesLiveData = new MutableLiveData<>();

    private MutableLiveData<String> toastMsgLiveData = new MutableLiveData<>();

    public void loadData(final HashMap<String, String> coursesHashMap) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            String userUID = currentUser.getUid();
            db.document("/student_profiles/" + userUID).get(Source.SERVER)
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {

                                ProfileObjectStudent bcd = documentSnapshot.toObject(ProfileObjectStudent.class);

                                String shift = bcd.getShift();

                                if (!coursesHashMap.isEmpty() && shift != null) {
                                    loadDataFromServer(coursesHashMap, shift);
                                }

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            toastMsgLiveData.setValue("Unable to load.Please check your internet connection");
                        }
                    });
        }

    }

    private void loadDataFromServer(HashMap<String, String> coursesHashMap, String shift) {

        final ArrayList<BookedClassDetailsUser> extraClasses = new ArrayList<>();

        ArrayList<String> courseCodes = new ArrayList<>();
        ArrayList<String> sections = new ArrayList<>();

        for (HashMap.Entry<String, String> entry : coursesHashMap.entrySet()) {
            courseCodes.add(entry.getKey());
            sections.add(entry.getValue());
        }

        Timestamp todayTimestamp = getTimestampToday();

        List<Task<QuerySnapshot>> taskList = new ArrayList<>();

        for (int i = 0; i < courseCodes.size(); i++) {
            taskList.add(
                    FirebaseFirestore.getInstance().collection("booked_classes")
                            .whereEqualTo("shift", shift)
                            .whereEqualTo("section", sections.get(i))
                            .whereEqualTo("courseCode",courseCodes.get(i))
                            .whereGreaterThanOrEqualTo("reservationDate", todayTimestamp)
                            .get()
            );
        }

        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(taskList);

        allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                for(QuerySnapshot qs : querySnapshots)  {
                    for(DocumentSnapshot ds : qs)   {
                        extraClasses.add(ds.toObject(BookedClassDetailsUser.class));
                    }
                }
                classesLiveData.setValue(extraClasses);
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toastMsgLiveData.setValue("Failed to load data.Please check your internet connection.");
                    }
                });


    }

    private Timestamp getTimestampToday() {

        Calendar todayCalender = new GregorianCalendar(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH);

        return new Timestamp(todayCalender.getTime());

    }

    public LiveData<ArrayList<BookedClassDetailsUser>> getExtraClassesList() {
        return classesLiveData;
    }

    public LiveData<String> getToastMsg() {
        return toastMsgLiveData;
    }

}
