package bd.edu.daffodilvarsity.classmanager.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectTeacher;
import timber.log.Timber;

public class TeacherProfileListViewModel extends ViewModel {

    private boolean loadingInProgress = false;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private DocumentSnapshot loadAfter;

    private ArrayList<ProfileObjectTeacher> teachersList = new ArrayList<>();

    private MutableLiveData<ArrayList<ProfileObjectTeacher>> teacherProfileLiveData = new MutableLiveData<>();

    private MutableLiveData<String> toastMsg = new MutableLiveData<>();

    public void loadTeacherProfiles() {

        if (loadingInProgress) {
            return;
        } else {
            loadingInProgress = true;
        }

        db.collection("teacher_profiles")
                .orderBy("name")
                .limit(10)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        teachersList.clear();
                        for (DocumentSnapshot dc : queryDocumentSnapshots) {
                            teachersList.add(dc.toObject(ProfileObjectTeacher.class));
                        }
                        if (queryDocumentSnapshots.size() > 0) {
                            loadAfter = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        }
                        teacherProfileLiveData.setValue(teachersList);

                        loadingInProgress = false;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toastMsg.setValue("Failed to load.Please check your internet connection.");

                        loadingInProgress = false;
                    }
                });
    }

    public void loadNextTeacherProfiles() {

        if (loadingInProgress) {
            return;
        } else {
            loadingInProgress = true;
        }

        try {
            db.collection("teacher_profiles").orderBy("name").startAfter(loadAfter).limit(8).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot ds : queryDocumentSnapshots) {
                                teachersList.add(ds.toObject(ProfileObjectTeacher.class));
                            }
                            if (queryDocumentSnapshots.size() > 0) {
                                loadAfter = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                                teacherProfileLiveData.setValue(teachersList);
                            }
                            loadingInProgress = false;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            toastMsg.setValue("Failed to load.Please check your internet connection.");
                            loadingInProgress = false;
                        }
                    });
        } catch (Exception e) {
            Timber.e(e);
            loadingInProgress = false;
        }
    }

    public void loadTeacherProfilesWithSubstring(String queryText) {

        db.collection("teacher_profiles")
                .whereEqualTo("teacherInitial", queryText).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        teachersList.clear();
                        for (DocumentSnapshot dc : queryDocumentSnapshots) {
                            teachersList.add(dc.toObject(ProfileObjectTeacher.class));
                        }
                        if (queryDocumentSnapshots.size() > 0) {
                            loadAfter = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        }
                        teacherProfileLiveData.setValue(teachersList);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastMsg.setValue("Failed to load.Please check your internet connection.");
            }
        });
    }

    public void deleteProfile(final ProfileObjectTeacher profile) {
        db.document("teacher_profiles/" + profile.getEmail()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        toastMsg.setValue("Deleted..");
                        teachersList.remove(profile);
                        teacherProfileLiveData.setValue(teachersList);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toastMsg.setValue("Unsuccessful");
                        teacherProfileLiveData.setValue(teachersList);
                    }
                });
    }

    public MutableLiveData<ArrayList<ProfileObjectTeacher>> getTeacherProfileLiveData() {
        return teacherProfileLiveData;
    }

    public MutableLiveData<String> getToastMsg() {
        return toastMsg;
    }
}
