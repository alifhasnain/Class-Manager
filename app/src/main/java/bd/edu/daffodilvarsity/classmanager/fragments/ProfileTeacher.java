package bd.edu.daffodilvarsity.classmanager.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.util.List;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectTeacher;
import bd.edu.daffodilvarsity.classmanager.otherclasses.SharedPreferencesHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileTeacher extends Fragment {

    private static final String TAG = "ProfileTeacher";

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

        initializeVariables(view);

        loadProfileInfo();

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

    private void loadProfileInfo() {

        if (mAuth.getCurrentUser() == null) {
            makeToast("Not Logged In.");
            return;
        }

        String currentUserEmail = mAuth.getCurrentUser().getEmail();

        DocumentReference teacherProfiles = db.document("/teacher_profiles/"+currentUserEmail);

        Task<DocumentSnapshot> taskGetProfileInfo = teacherProfiles.get();

        DocumentReference bookInfo = db.document("/book_room_count/" + currentUserEmail);

        Task<DocumentSnapshot> taskGetBookCount = bookInfo.get();

        Task<List<DocumentSnapshot>> allTask = Tasks.whenAllSuccess(taskGetProfileInfo,taskGetBookCount);

        allTask.addOnSuccessListener(new OnSuccessListener<List<DocumentSnapshot>>() {
            @Override
            public void onSuccess(List<DocumentSnapshot> documentSnapshotsList) {
                if(documentSnapshotsList.get(0).exists())   {
                    mProfile = documentSnapshotsList.get(0).toObject(ProfileObjectTeacher.class);
                    SharedPreferencesHelper.saveTeacherProfileToSharedPref(getContext(),mProfile);
                }
                if (documentSnapshotsList.get(1).exists())  {
                    bookedClassesThisMonth = documentSnapshotsList.get(1).getLong("counter").intValue();
                }

                pullToRefresh.setRefreshing(false);
                displayProfileInfo();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pullToRefresh.setRefreshing(false);
                makeToast("Failed to load. Please check  your internet connection.");
                Log.e(TAG,"Error:",e);
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
            classBookedThisMonth.setText(String.valueOf(bookedClassesThisMonth));
        } catch (Exception e) {
            Log.e(TAG,"Crash Log : ",e);
        }
    }

    private void makeToast(String msg)    {
        if(getContext()!=null)  {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

}
