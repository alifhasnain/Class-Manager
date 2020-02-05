package bd.edu.daffodilvarsity.classmanager.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.List;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.activities.EditTeacherProfile;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectTeacher;
import bd.edu.daffodilvarsity.classmanager.otherclasses.SharedPreferencesHelper;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileTeacher extends Fragment {

    private FirebaseAuth mAuth;

    private FirebaseFirestore db;

    private ProfileObjectTeacher mProfile;

    private int bookedClassesThisMonth;

    private SwipeRefreshLayout pullToRefresh;

    private TextView name;

    private TextView designation;

    private TextView teacherInitial;

    private TextView teacherId;

    private TextView email;

    private TextView contactNo;

    private TextView classBookedThisMonth;


    public ProfileTeacher() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_teacher, container, false);
        setHasOptionsMenu(true);

        initializeVariables(view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadProfileInfo();
            }
        });
        loadProfileInfo();
    }

    private void initializeVariables(View view) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        pullToRefresh = view.findViewById(R.id.pull_to_refresh);
        name = view.findViewById(R.id.name);
        teacherInitial = view.findViewById(R.id.teacher_initial);
        designation = view.findViewById(R.id.designation);
        teacherId = view.findViewById(R.id.teacher_id);
        email = view.findViewById(R.id.email);
        contactNo = view.findViewById(R.id.contact_no);
        classBookedThisMonth = view.findViewById(R.id.booked_class_number);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile_teacher, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (R.id.edit_profile == item.getItemId() && mProfile != null) {
            Intent intent = new Intent(getContext(), EditTeacherProfile.class);
            String jsonProfile = new Gson().toJson(mProfile);
            intent.putExtra("profile", jsonProfile);
            startActivity(intent);
        }
        return true;
    }

    private void loadProfileInfo() {

        if (mAuth.getCurrentUser() == null) {
            makeToast("Not Logged In.");
            return;
        }

        String currentUserEmail = mAuth.getCurrentUser().getEmail();

        DocumentReference teacherProfiles = db.document("/teacher_profiles/" + currentUserEmail);

        Task<DocumentSnapshot> taskGetProfileInfo = teacherProfiles.get();

        DocumentReference bookInfo = db.document("/book_room_count/" + currentUserEmail);

        Task<DocumentSnapshot> taskGetBookCount = bookInfo.get();

        Task<List<DocumentSnapshot>> allTask = Tasks.whenAllSuccess(taskGetProfileInfo, taskGetBookCount);

        allTask.addOnSuccessListener(new OnSuccessListener<List<DocumentSnapshot>>() {
            @Override
            public void onSuccess(List<DocumentSnapshot> documentSnapshotsList) {
                if (documentSnapshotsList.get(0).exists()) {
                    mProfile = documentSnapshotsList.get(0).toObject(ProfileObjectTeacher.class);
                    SharedPreferencesHelper.saveTeacherProfileToSharedPref(getContext(), mProfile);
                }
                if (documentSnapshotsList.get(1).exists()) {
                    bookedClassesThisMonth = documentSnapshotsList.get(1).getLong("counter").intValue();
                }

                pullToRefresh.setRefreshing(false);
                displayProfileInfo();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pullToRefresh.setRefreshing(false);
                makeToast("Please check  your internet connection.");
                mProfile = SharedPreferencesHelper.getTeacherOfflineProfile(getContext());
                displayProfileInfo();
            }
        });

    }

    private void displayProfileInfo() {
        try {
            name.setText(mProfile.getName());
            email.setText(mProfile.getEmail());
            teacherInitial.setText(mProfile.getTeacherInitial());
            designation.setText(mProfile.getDesignation());
            teacherId.setText(mProfile.getId());
            contactNo.setText(mProfile.getContactNo());
            classBookedThisMonth.setText(bookedClassesThisMonth + "/12");
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void makeToast(String msg) {
        if (getContext() != null) {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

}
