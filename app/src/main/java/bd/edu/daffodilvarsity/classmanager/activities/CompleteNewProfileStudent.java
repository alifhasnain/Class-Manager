package bd.edu.daffodilvarsity.classmanager.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectStudent;

public class CompleteNewProfileStudent extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;

    FirebaseFirestore db;

    TextInputLayout mName;

    TextInputLayout mStudentId;

    Button mSave;

    Chip mDay;

    Chip mEvening;

    Chip mCse;

    Spinner mLevel;

    Spinner mTerm;

    Spinner mSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_new_profile_student);

        initializeVariables();

        initializeSpinners();

        initializeOnClickListeners();
    }

    private void initializeVariables() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mSave = findViewById(R.id.save);
        mName = findViewById(R.id.name);
        mStudentId = findViewById(R.id.student_id);
        mDay = findViewById(R.id.day);
        mEvening = findViewById(R.id.evening);
        mCse = findViewById(R.id.cse);
        mLevel = findViewById(R.id.level);
        mTerm = findViewById(R.id.term);
        mSection = findViewById(R.id.section_spinner);
    }

    private void initializeOnClickListeners() {
        mSave.setOnClickListener(this);
    }

    private void initializeSpinners() {

        String[] level = new String[]{"Level 1", "Level 2", "Level 3", "Level 4"};

        String[] term = new String[]{"Term 1", "Term 2", "Term 3"};

        String[] section = new String[] {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, level);

        ArrayAdapter<String> termAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, term);

        ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,section);

        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mLevel.setAdapter(levelAdapter);

        mTerm.setAdapter(termAdapter);

        mSection.setAdapter(sectionAdapter);
    }

    private void checkInformationAndSave() {

        clearErrors();

        if (!checkGivenInfo()) {
            return;
        }

        String name = mName.getEditText().getText().toString().trim();

        String id = mStudentId.getEditText().getText().toString().trim();

        ProfileObjectStudent profile = new ProfileObjectStudent();

        profile.setName(name);

        profile.setId(id);


        if (mDay.isChecked()) {
            profile.setShift("Day");
        } else if (mEvening.isChecked()) {
            profile.setShift("Evening");
        }

        if (mCse.isChecked()) {
            profile.setDepartment("CSE");
        }

        final String level = mLevel.getSelectedItem().toString();
        final String term = mTerm.getSelectedItem().toString();
        final String section = mSection.getSelectedItem().toString();

        profile.setLevel(level);

        profile.setTerm(term);

        profile.setSection(section);

        DocumentReference docRef = db.document("/student_profiles/" + mAuth.getCurrentUser().getUid());

        docRef.set(profile)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            makeToast("Information saved.");
                            saveCourseWithSharedPreference(level,term,section);
                            startActivity(new Intent(CompleteNewProfileStudent.this,MainActivity.class));
                            finish();
                        } else {
                            makeToast("Failed to save.Please check your internet connection.");
                        }
                    }
                });

    }

    private void saveCourseWithSharedPreference(String level,String term,String section) {

        HelperClass helperClass = new HelperClass();

        ArrayList<String> coursesList = helperClass.getCourseList(level,term);

        HashMap<String,String> coursesMap = new HashMap<>();

        for(String courseCode : coursesList)    {
            coursesMap.put(courseCode,section);
        }

        SharedPreferences sharedPreferences = getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG,MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();

        String courseMapInJson = gson.toJson(coursesMap);

        editor.putString(HelperClass.COURSES_HASH_MAP,courseMapInJson).apply();
    }

    private boolean checkGivenInfo() {
        String name = mName.getEditText().getText().toString().trim();
        String id = mStudentId.getEditText().getText().toString().trim();

        if (name.isEmpty()) {
            makeToast("Name cannot be empty.");
            mName.setError("Name cannot be empty");
            return false;
        }

        if (id.isEmpty()) {
            makeToast("ID cannot be empty.");
            mStudentId.setError("ID cannot be empty");
            return false;
        }

        if (!name.matches("[a-zA-Z ]*")) {
            makeToast("Invalid characters in username.");
            mName.setError("Invalid characters");
            return false;
        }

        if (!id.matches("[0-9-]*")) {
            makeToast("Invalid ID.");
            mStudentId.setError("Invalid student ID");
            return false;
        }

        if (!checkChipSelection()) {
            return false;
        }

        return true;
    }

    private void clearErrors() {
        mName.setError(null);
        mStudentId.setError(null);
    }

    private boolean checkChipSelection() {

        if (mDay.isChecked() | mEvening.isChecked()) {
            if (mCse.isChecked()) {
                return true;
            } else {
                makeToast("Please select shift and department");
                return false;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                checkInformationAndSave();
                break;
        }
    }

    private void makeToast(String txt) {
        Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
    }
}
