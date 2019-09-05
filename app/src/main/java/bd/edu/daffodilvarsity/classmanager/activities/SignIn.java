package bd.edu.daffodilvarsity.classmanager.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectStudent;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectTeacher;
import bd.edu.daffodilvarsity.classmanager.otherclasses.SharedPreferencesHelper;

public class SignIn extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseFirestore db;

    private TextInputLayout mEmailEditText;

    private TextInputLayout mPasswordEditText;

    private SharedPreferencesHelper mSharedPrefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        startGradientAnimation();

        initializeVariables();

        initializeOnClickListeners();

    }

    private void testMethodCreateProfile() {

        ProfileObjectTeacher profile = new ProfileObjectTeacher("Alif Hasnain", "hasnain.alif20@gmail.com", "RAH", "");

        CollectionReference cr = db.collection("/teacher_profiles");

        cr.add(profile);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    private void initializeVariables() {

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mSharedPrefHelper = new SharedPreferencesHelper();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null && isEmailVerified()) {
                    mAuth.getCurrentUser().getIdToken(false).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                        @Override
                        public void onSuccess(GetTokenResult result) {
                            if(result.getClaims().get("admin") != null) {
                                signInAsAdmin();
                                showCircularProgressBar(false);
                            }
                            else if(result.getClaims().get("teacher") != null) {
                                showCircularProgressBar(false);
                                signInAsTeacher();
                            }
                            else if(result.getClaims().get("student") != null)  {
                                showCircularProgressBar(false);
                                checkIfStudentProfileExistInDatabase();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            checkForOfflineUserInfo();
                            Log.e("","Error : " , e);
                            showCircularProgressBar(false);
                        }
                    });
                } else if (firebaseAuth.getCurrentUser() != null) {
                    sendVerificationMail();
                }
            }
        };

        mEmailEditText = findViewById(R.id.email);

        mPasswordEditText = findViewById(R.id.pass);

    }

    private void checkForOfflineUserInfo() {
        switch (mSharedPrefHelper.getUserType(this)) {
            case HelperClass.USER_TYPE_ADMIN:
                signInAsAdmin();
                break;
            case HelperClass.USER_TYPE_TEACHER:
                signInAsTeacher();
                break;
            case HelperClass.USER_TYPE_STUDENT:
                signInAsStudent();
                break;
        }
    }

    private void checkIfStudentProfileExistInDatabase() {

        showCircularProgressBar(true);

        String uid = mAuth.getCurrentUser().getUid();

        DocumentReference profileDetailsStudent = db.document("/student_profiles/" + uid);

        profileDetailsStudent.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if (documentSnapshot.exists()) {

                            ProfileObjectStudent profile = documentSnapshot.toObject(ProfileObjectStudent.class);

                            saveShiftAndProgramWithSharedPreferences(profile.getProgram(),profile.getShift());

                            if (mSharedPrefHelper.getCoursesAndSectionMapFromSharedPreferences(getApplication()) == null) {
                                saveCoursesWithSharedPreference(profile.getProgram(), profile.getShift(), profile.getLevel(), profile.getTerm(), profile.getSection());
                            }

                            signInAsStudent();

                            showCircularProgressBar(false);

                        } else {
                            completeProfileStudent();
                            showCircularProgressBar(false);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeToast("Failed to load please check your internet connection and try again");
                        showCircularProgressBar(false);
                    }
                });

    }

    private void initializeOnClickListeners() {
        findViewById(R.id.sign_in).setOnClickListener(this);
        findViewById(R.id.sign_up).setOnClickListener(this);
        findViewById(R.id.forgot_pass).setOnClickListener(this);
    }

    private void saveShiftAndProgramWithSharedPreferences(String program,String shift) {

        SharedPreferences sharedPreferences = getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(HelperClass.PROGRAM,program).apply();

        editor.putString(HelperClass.SHIFT,shift).apply();

    }

    private void saveCoursesWithSharedPreference(String program, String shift, String level, String term, String section) {

        HelperClass helperClass = HelperClass.getInstance();

        ArrayList<String> coursesList = helperClass.getCourseList(program, shift, level, term);

        HashMap<String, String> coursesMap = new HashMap<>();

        for (String courseCode : coursesList) {
            coursesMap.put(courseCode, section);
        }

        SharedPreferences sharedPreferences = getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();

        String courseMapInJson = gson.toJson(coursesMap);

        editor.putString(HelperClass.COURSES_HASH_MAP, courseMapInJson).apply();
    }

    private void completeProfileStudent() {
        setUserType(HelperClass.USER_TYPE_STUDENT);
        makeToast("Please complete your profile information");
        startActivity(new Intent(this, CompleteNewProfileStudent.class));
    }

    private void signInAsAdmin() {
        setUserType(HelperClass.USER_TYPE_ADMIN);
        startMainActivity();
    }

    private void signInAsStudent() {
        setUserType(HelperClass.USER_TYPE_STUDENT);
        startMainActivity();
    }

    private void signInAsTeacher() {
        setUserType(HelperClass.USER_TYPE_TEACHER);
        startMainActivity();
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void setUserType(String type) {
        SharedPreferences sharedPreferences = getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(HelperClass.USER_TYPE, type).apply();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in:
                clearFocusAndErrorMsg();
                checkCredentialAndSignIn();
                break;

            case R.id.sign_up:
                startSignUpActivityWithTransition();
                break;

            case R.id.forgot_pass:
                startActivity(new Intent(SignIn.this, ForgotPassword.class));
                break;

        }
    }

    private void checkCredentialAndSignIn() {

        String sEmail = mEmailEditText.getEditText().getText().toString().trim();
        String sPassword = mPasswordEditText.getEditText().getText().toString().trim();

        if (sEmail.isEmpty()) {
            mEmailEditText.setError("An Email is required");
            mEmailEditText.requestFocus();
            return;
        }
        if (sPassword.isEmpty()) {
            mPasswordEditText.setError("A Password is required");
            mPasswordEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            makeToast("Invalid Email!");
        }
        if (sPassword.length() < 6) {
            mPasswordEditText.setError("Minimum length of password is 6");
            return;
        }

        showCircularProgressBar(true);

        mAuth.signInWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()) {
                    showCircularProgressBar(false);
                    if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        makeToast("Email does not exist in database.");
                    } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        makeToast("Invalid password.");
                    } else {
                        makeToast("Error while login.");
                    }
                }
            }
        });
    }

    private void sendVerificationMail() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful() && !isEmailVerified()) {
                    showCircularProgressBar(false);
                    makeToast("Verification Email is sent confirm registration and SignIn");
                    mEmailEditText.getEditText().setText("");
                    mPasswordEditText.getEditText().setText("");
                    signOut();
                } else if (!task.isSuccessful()) {
                    showCircularProgressBar(true);
                }
            }
        });

    }

    private void showCircularProgressBar(boolean visible) {
        LinearLayout progressBarHolder = findViewById(R.id.progress_bar_holder);
        ProgressBar progressBar = findViewById(R.id.circular_progress_bar);

        if (visible) {
            progressBarHolder.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBarHolder.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void signOut() {
        try {
            mAuth.signOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isEmailVerified() {
        try {
            return mAuth.getCurrentUser().isEmailVerified();
        } catch (Exception e) {
            return false;
        }
    }

    private void clearFocusAndErrorMsg() {
        mEmailEditText.clearFocus();
        mPasswordEditText.clearFocus();
        mEmailEditText.setError(null);
        mPasswordEditText.setError(null);
    }

    private void startGradientAnimation() {

        //Hide Statusbar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        AnimationDrawable gradientAnimation = (AnimationDrawable) findViewById(R.id.root_layout).getBackground();
        gradientAnimation.setEnterFadeDuration(200);
        gradientAnimation.setExitFadeDuration(3000);
        gradientAnimation.start();

    }

    private void startSignUpActivityWithTransition() {

        Intent intent = new Intent(SignIn.this, SignUp.class);

        Pair<View, String>[] pairs = new Pair[3];
        pairs[0] = new Pair<>(findViewById(R.id.sign_in), "sign_in_transition");
        pairs[1] = new Pair<>(findViewById(R.id.sign_up), "sign_up_transition");
        pairs[2] = new Pair<>(findViewById(R.id.forgot_pass), "text_view_transition");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignIn.this, pairs);
        startActivity(intent, options.toBundle());
    }

    private void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}
