package bd.edu.daffodilvarsity.classmanager.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectTeacher;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddTeacherProfile extends Fragment implements View.OnClickListener{

    private TextInputLayout name;

    private TextInputLayout email;

    private TextInputLayout teacherInitial;

    private TextInputLayout designation;

    private TextInputLayout id;

    private TextInputLayout contactNo;

    public AddTeacherProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_teacher_profile, container, false);

        initializeVariables(view);

        return view;
    }

    private void initializeVariables(View view) {

        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        teacherInitial = view.findViewById(R.id.teacher_initial);
        designation = view.findViewById(R.id.designation);
        id = view.findViewById(R.id.teacher_id);
        contactNo = view.findViewById(R.id.contact_no);

        view.findViewById(R.id.save).setOnClickListener(this);

    }

    private void checkAndSaveInformation() {

        String nameStr = name.getEditText().getText().toString().trim();

        String emailStr = email.getEditText().getText().toString().trim();

        String initialStr = teacherInitial.getEditText().getText().toString().trim();

        String designationStr = designation.getEditText().getText().toString().trim();

        String idStr = id.getEditText().getText().toString().trim();

        String contactNoStr = contactNo.getEditText().getText().toString().trim();

        if(!validateInfos(nameStr,emailStr,initialStr,designationStr,idStr,contactNoStr))    {
            return;
        }

        ProfileObjectTeacher profileDetails = new ProfileObjectTeacher(nameStr,emailStr,initialStr,designationStr,idStr,contactNoStr);

        DocumentReference profileRef = FirebaseFirestore.getInstance().document("/teacher_profiles/" + profileDetails.getEmail() + "/");

        profileRef.set(profileDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to save data. Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean validateInfos(String nameStr, String emailStr, String initialStr, String designationStr, String idStr, String contactNoStr) {

        if(nameStr.equals(""))  {
            makeToast("Please insert a name first");
            return false;
        }
        if(emailStr.equals("")) {
            makeToast("Please insert an email first");
            return false;
        }
        if(initialStr.equals(""))   {
            makeToast("Please insert teacher initial");
            return false;
        }

        return true;

    }

    private void makeToast(String msg)  {
        if(getContext()!= null) {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.save:
                checkAndSaveInformation();
                break;
        }

    }
}
