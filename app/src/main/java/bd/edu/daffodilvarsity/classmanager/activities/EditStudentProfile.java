package bd.edu.daffodilvarsity.classmanager.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectStudent;
import bd.edu.daffodilvarsity.classmanager.otherclasses.SharedPreferencesHelper;
import timber.log.Timber;

public class EditStudentProfile extends AppCompatActivity implements View.OnClickListener {

    private SwipeRefreshLayout mPullToRefresh;

    private ProfileObjectStudent mUserProfile;

    ProfileObjectStudent mUpdatedProfile;

    private TextInputLayout mName;

    private TextInputLayout mStudentId;

    private Button mSave;

    private Chip mDay;

    private Chip mEvening;

    private Chip mCse;

    private Chip mBsc;

    private Spinner mLevel;

    private Spinner mTerm;

    private Spinner mSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student_profile);

        initializeVariables();
        initializeSpinners();

        getReceivedData();
        initializeReceivedData();

    }

    private void getReceivedData() {
        //Convert JSON String to profile object
        String profileJsonString = getIntent().getStringExtra("profileData");
        Gson gson = new Gson();
        mUserProfile = gson.fromJson(profileJsonString, ProfileObjectStudent.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataFromServer();
            }
        });
    }

    private void loadDataFromServer() {

        DocumentReference profileRef = FirebaseFirestore.getInstance().document("/student_profiles/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        profileRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    mUserProfile = documentSnapshot.toObject(ProfileObjectStudent.class);
                    initializeReceivedData();
                    mPullToRefresh.setRefreshing(false);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                makeToast(getString(R.string.load_data_error_for_internet));
                mPullToRefresh.setRefreshing(false);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void initializeReceivedData() {

        try {

            mName.getEditText().setText(mUserProfile.getName());
            mStudentId.getEditText().setText(mUserProfile.getId());

            mSection.setSelection(((ArrayAdapter) mSection.getAdapter()).getPosition(mUserProfile.getSection()));
            mLevel.setSelection(((ArrayAdapter) mLevel.getAdapter()).getPosition(mUserProfile.getLevel()));
            mTerm.setSelection(((ArrayAdapter) mTerm.getAdapter()).getPosition(mUserProfile.getTerm()));

            if (mUserProfile.getShift().equals(HelperClass.SHIFT_DAY)) {
                mDay.setChecked(true);
            } else if (mUserProfile.getShift().equals(HelperClass.SHIFT_EVENING)) {
                mEvening.setChecked(true);
            }

            mBsc.setClickable(false);
            mCse.setClickable(false);

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void initializeVariables() {

        mSave = findViewById(R.id.save);
        mSave.setOnClickListener(this);

        mUpdatedProfile = new ProfileObjectStudent();

        mName = findViewById(R.id.name);
        mStudentId = findViewById(R.id.student_id);
        mDay = findViewById(R.id.day);
        mEvening = findViewById(R.id.evening);
        mCse = findViewById(R.id.cse);
        mBsc = findViewById(R.id.bsc);
        mLevel = findViewById(R.id.level);
        mTerm = findViewById(R.id.term);
        mSection = findViewById(R.id.section_spinner);

        mPullToRefresh = findViewById(R.id.pull_to_refresh);

    }

    private void initializeSpinners() {

        String[] level = new String[]{"Level 1", "Level 2", "Level 3", "Level 4"};

        String[] term = new String[]{"Term 1", "Term 2", "Term 3"};

        String[] section = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "R1", "R2", "R3", "R4"};

        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, level);

        ArrayAdapter<String> termAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, term);

        ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, section);

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

        mUpdatedProfile.setName(name);

        mUpdatedProfile.setId(id);

        if (mBsc.isChecked()) {
            mUpdatedProfile.setProgram(HelperClass.PROGRAM_BSC);
        }

        if (mDay.isChecked()) {
            mUpdatedProfile.setShift("Day");
        } else if (mEvening.isChecked()) {
            mUpdatedProfile.setShift("Evening");
        }

        if (mCse.isChecked()) {
            mUpdatedProfile.setDepartment("CSE");
        }

        final String level = mLevel.getSelectedItem().toString();
        final String term = mTerm.getSelectedItem().toString();
        final String section = mSection.getSelectedItem().toString();

        mUpdatedProfile.setLevel(level);

        mUpdatedProfile.setTerm(term);

        mUpdatedProfile.setSection(section);

        if (mUpdatedProfile.getShift().equals(HelperClass.SHIFT_EVENING) && level.equals("Level 4")) {
            makeToast("Evening shift can't have Level 4");
            return;
        }

        SharedPreferencesHelper.saveStudentProfileOffline(this, mUpdatedProfile);

        SharedPreferencesHelper.saveCourseWithSharedPreference(
                this,
                mUpdatedProfile.getProgram(),
                mUpdatedProfile.getShift(),
                mUpdatedProfile.getLevel(),
                mUpdatedProfile.getTerm(),
                mUpdatedProfile.getSection()
        );

        makeToast("Saved");

        /*DocumentReference docRef = db.document("/student_profiles/" + mAuth.getCurrentUser().getUid());


        docRef.set(mUpdatedProfile)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            makeToast("Saved.");
                            new SharedPreferencesHelper().saveCourseWithSharedPreference(
                                    getApplicationContext(),
                                    mUpdatedProfile.getProgram(),
                                    mUpdatedProfile.getShift(),
                                    mUpdatedProfile.getLevel(),
                                    mUpdatedProfile.getTerm(),
                                    mUpdatedProfile.getSection()
                            );
                        } else {
                            makeToast("Failed to save.Please check your internet connection.");
                        }
                    }
                });*/

    }

    private void clearErrors() {
        mName.setError(null);
        mStudentId.setError(null);
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

        if (!name.matches("[a-zA-Z .]*")) {
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

    private boolean checkChipSelection() {

        if (mDay.isChecked() | mEvening.isChecked()) {
            if (mCse.isChecked()) {
                if (mBsc.isChecked()) {
                    return true;
                } else {
                    makeToast("Please select a program");
                }
            } else {
                makeToast("Please select a department");
            }
        } else {
            makeToast("Select your shift.");
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

    private void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
