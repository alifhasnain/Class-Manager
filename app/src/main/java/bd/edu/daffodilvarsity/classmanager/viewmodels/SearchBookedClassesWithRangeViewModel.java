package bd.edu.daffodilvarsity.classmanager.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classmanager.otherclasses.BookedClassDetailsUser;
import timber.log.Timber;

public class SearchBookedClassesWithRangeViewModel extends ViewModel {

    private boolean loadingInProgress = false;

    private Timestamp start;

    private Timestamp end;

    private DocumentSnapshot lastDocument;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private MutableLiveData<ArrayList<BookedClassDetailsUser>> classesLiveData = new MutableLiveData<>();

    private MutableLiveData<ArrayList<BookedClassDetailsUser>> allClasses = new MutableLiveData<>();

    private MutableLiveData<String> toastLiveData = new MutableLiveData<>();

    public void fetchData(Timestamp start, Timestamp end) {

        if (loadingInProgress) {
            return;
        } else {
            loadingInProgress = true;
        }

        this.start = start;
        this.end = end;

        db.collection("/booked_classes/")
                .whereGreaterThanOrEqualTo("reservationDate", start)
                .whereLessThanOrEqualTo("reservationDate", end)
                .orderBy("reservationDate")
                .limit(10)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() > 0) {
                            lastDocument = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        } else {
                            lastDocument = null;
                        }
                        ArrayList<BookedClassDetailsUser> tempList = new ArrayList<>();
                        for (DocumentSnapshot ds : queryDocumentSnapshots) {
                            tempList.add(ds.toObject(BookedClassDetailsUser.class));
                        }
                        classesLiveData.setValue(tempList);
                        loadingInProgress = false;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingInProgress = false;
                        Timber.e(e);
                    }
                });
    }

    public void fetchMoreNextData() {

        if (lastDocument == null || loadingInProgress) {
            Timber.e("Last data is null!");
            return;
        } else {
            loadingInProgress = true;
        }

        db.collection("/booked_classes/")
                .whereGreaterThanOrEqualTo("reservationDate", start)
                .whereLessThanOrEqualTo("reservationDate", end)
                .orderBy("reservationDate")
                .startAfter(lastDocument)
                .limit(10)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() > 0) {
                            lastDocument = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        } else {
                            lastDocument = null;
                        }
                        ArrayList<BookedClassDetailsUser> tempList = new ArrayList<>();
                        for (DocumentSnapshot ds : queryDocumentSnapshots) {
                            tempList.add(ds.toObject(BookedClassDetailsUser.class));
                        }
                        classesLiveData.setValue(tempList);

                        loadingInProgress = false;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.e(e);
                        loadingInProgress = false;
                    }
                });

    }

    public void fetchAllData(Timestamp start , Timestamp end) {

        if (loadingInProgress) {
            toastLiveData.setValue("Already in progress");
            return;
        } else {
            loadingInProgress = true;
        }

        this.start = start;
        this.end = end;

        db.collection("/booked_classes/")
                .whereGreaterThanOrEqualTo("reservationDate", start)
                .whereLessThanOrEqualTo("reservationDate", end)
                .orderBy("reservationDate")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<BookedClassDetailsUser> tempList = new ArrayList<>();
                        for (DocumentSnapshot ds : queryDocumentSnapshots) {
                            tempList.add(ds.toObject(BookedClassDetailsUser.class));
                        }
                        allClasses.setValue(tempList);
                        loadingInProgress = false;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingInProgress = false;
                        toastLiveData.setValue("Failed to load please try again");
                        Timber.e(e);
                    }
                });
    }

    public MutableLiveData<ArrayList<BookedClassDetailsUser>> getClassesLiveData() {
        return classesLiveData;
    }

    public MutableLiveData<ArrayList<BookedClassDetailsUser>> getAllClasses() {
        return allClasses;
    }

    public MutableLiveData<String> getToastMessage() {
        return toastLiveData;
    }
}
