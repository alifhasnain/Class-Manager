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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectTeacher;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileTeacher extends Fragment {

    private static final String TAG = "ProfileTeacher";

    private FirebaseAuth mAuth;

    private FirebaseFirestore db;

    private TextView mName;

    private TextView mDesignation;

    private TextView mClassesBooked;

    private TextView mTeacherInitial;

    private TextView mEmail;


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

    private void initializeVariables(View view) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mName = view.findViewById(R.id.name);
        mDesignation = view.findViewById(R.id.designation);
        mClassesBooked = view.findViewById(R.id.classes_booked);
        mTeacherInitial = view.findViewById(R.id.teacher_initial);
        mEmail = view.findViewById(R.id.email);
    }

    private void loadProfileInfo() {

        final ProfileObjectTeacher profile = new ProfileObjectTeacher();

        String currentUserEmail = mAuth.getCurrentUser().getEmail();

        CollectionReference teacherProfiles = db.collection("/teacher_profiles");

        teacherProfiles.whereEqualTo("email", currentUserEmail).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot ds : queryDocumentSnapshots) {
                            profile.createInstance(ds.toObject(ProfileObjectTeacher.class));
                            break;
                        }
                        displayProfileInfo(profile);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to load data.Please check your connection.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayProfileInfo(ProfileObjectTeacher profile) {
        try {
            mName.setText(profile.getName());
            mEmail.setText(profile.getEmail());
            mTeacherInitial.setText(profile.getTeacherInitial());
            mDesignation.setText(profile.getDesignation());
            mClassesBooked.setText(String.valueOf(profile.getClassesBookedThisMonth()));
        } catch (Exception e) {
            Log.e(TAG,"Crash Log : ",e);
        }
    }

}
