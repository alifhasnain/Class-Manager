package bd.edu.daffodilvarsity.classmanager.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectTeacher;
import bd.edu.daffodilvarsity.classmanager.otherclasses.SharedPreferencesHelper;

public class EditTeacherProfile extends AppCompatActivity {

    private ProfileObjectTeacher mProfile;

    private TextInputLayout name;
    private TextInputLayout email;
    private TextInputLayout designation;
    private TextInputLayout initial;
    private TextInputLayout id;
    private TextInputLayout contactNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_teacher_profile);

        initializeVariables();

        loadIntentExtras();

        loadDataToViews();

    }

    private void initializeVariables() {
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        id = findViewById(R.id.teacher_id);
        initial = findViewById(R.id.teacher_initial);
        designation = findViewById(R.id.designation);
        contactNo = findViewById(R.id.contact_no);

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void loadIntentExtras() {
        Gson gson = new Gson();
        Type type = new TypeToken<ProfileObjectTeacher>() {
        }.getType();
        String jsonProfile = getIntent().getExtras().getString("profile", "");
        mProfile = gson.fromJson(jsonProfile, type);
    }

    private void loadDataToViews() {
        if (mProfile == null) {
            makeToast("Profile is null.");
            return;
        }
        try {
            name.getEditText().setText(mProfile.getName());
            email.getEditText().setText(mProfile.getEmail());
            id.getEditText().setText(mProfile.getId());
            initial.getEditText().setText(mProfile.getTeacherInitial());
            designation.getEditText().setText(mProfile.getDesignation());
            contactNo.getEditText().setText(mProfile.getContactNo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validateData() {
        if (designation.getEditText().getText().toString().isEmpty()) {
            makeToast("Designation can't be empty.");
            return;
        }
        if (initial.getEditText().getText().toString().isEmpty()) {
            makeToast("Initial can't be empty.");
            return;
        }
        saveData();
    }

    private void saveData() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String email = auth.getCurrentUser().getEmail();
        if (email == null) {
            makeToast("No logged in.");
            return;
        }

        final ProfileObjectTeacher tempProfile = new ProfileObjectTeacher(
                mProfile.getName(),
                mProfile.getEmail(),
                initial.getEditText().getText().toString(),
                designation.getEditText().getText().toString(),
                mProfile.getId(),
                contactNo.getEditText().getText().toString()
        );

        DocumentReference docRef = FirebaseFirestore.getInstance().document("/teacher_profiles/" + email);

        Map<String, Object> data = new HashMap<>();
        data.put("teacherInitial", initial.getEditText().getText().toString());
        data.put("designation", designation.getEditText().getText().toString());
        data.put("contactNo", contactNo.getEditText().getText().toString());

        docRef.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                SharedPreferencesHelper.saveTeacherProfileToSharedPref(getBaseContext(), tempProfile);
                makeToast("Saved");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                makeToast("Failed to save.");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
