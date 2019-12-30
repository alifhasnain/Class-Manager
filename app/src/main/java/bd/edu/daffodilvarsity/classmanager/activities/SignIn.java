package bd.edu.daffodilvarsity.classmanager.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectStudent;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectTeacher;
import bd.edu.daffodilvarsity.classmanager.otherclasses.SharedPreferencesHelper;
import timber.log.Timber;

public class SignIn extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private TextInputLayout mEmailEditText;

    private TextInputLayout mPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        startGradientAnimation();

        initializeVariables();

        styleProgressBar();

        loadSavedUserEmail();

        initializeOnClickListeners();

        checkIfUserIsSignedInStudent();

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

    @Override
    public void onBackPressed() {
        LinearLayout progressBarHolder = findViewById(R.id.progress_bar_holder);
        if (progressBarHolder.getVisibility() == View.VISIBLE) {
            progressBarHolder.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    private void initializeVariables() {

        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                showCircularProgressBar(true);

                if (firebaseAuth.getCurrentUser() != null && isEmailVerified()) {
                    mAuth.getCurrentUser().getIdToken(false).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                        @Override
                        public void onSuccess(GetTokenResult result) {

                            if (result.getClaims().get("admin") != null) {
                                signInAsAdminOrTeacher(HelperClass.USER_TYPE_ADMIN);
                                showCircularProgressBar(false);
                            } else if (result.getClaims().get("teacher") != null) {
                                showCircularProgressBar(false);
                                signInAsAdminOrTeacher(HelperClass.USER_TYPE_TEACHER);
                            } else if (result.getClaims().get("student") != null) {
                                showCircularProgressBar(false);
                                makeToast("Only teachers can sign in.\nIf you are a teacher but your data doesn't exist please contact to the department.");
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            checkForOfflineUserInfo();
                            Timber.e(e);
                            showCircularProgressBar(false);
                        }
                    });
                } else if (firebaseAuth.getCurrentUser() == null) {
                    showCircularProgressBar(false);
                } else if (firebaseAuth.getCurrentUser() != null && !isEmailVerified()) {
                    showCircularProgressBar(true);
                    sendVerificationMail();
                }
            }
        };

        mEmailEditText = findViewById(R.id.email);

        mPasswordEditText = findViewById(R.id.pass);

    }

    private void checkIfUserIsSignedInStudent() {

        String userType = SharedPreferencesHelper.getUserType(this);

        ProfileObjectStudent profile = SharedPreferencesHelper.getStudentOfflineProfile(this);

        if (userType.equals(HelperClass.USER_TYPE_STUDENT) && profile != null) {
            signInAsStudent();
        }
    }

    private void loadSavedUserEmail() {
        String email = SharedPreferencesHelper.getUserEmail(getApplicationContext());
        if (email != null) {
            mEmailEditText.getEditText().setText(email);
            mPasswordEditText.getEditText().requestFocus();
        }
    }

    private void checkForOfflineUserInfo() {
        switch (SharedPreferencesHelper.getUserType(this)) {
            case HelperClass.USER_TYPE_ADMIN:
                signInAsAdminOrTeacher(HelperClass.USER_TYPE_ADMIN);
                break;
            case HelperClass.USER_TYPE_TEACHER:
                signInAsAdminOrTeacher(HelperClass.USER_TYPE_TEACHER);
                break;
        }
    }


    /*private void checkIfStudentProfileExistInDatabase() {

        showCircularProgressBar(true);

        String uid = mAuth.getCurrentUser().getUid();

        DocumentReference profileDetailsStudent = db.document("/student_profiles/" + uid);

        profileDetailsStudent.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if (documentSnapshot.exists()) {

                            ProfileObjectStudent profile = documentSnapshot.toObject(ProfileObjectStudent.class);

                            saveShiftAndProgramWithSharedPreferences(profile.getProgram(), profile.getShift());

                            if (SharedPreferencesHelper.getCoursesAndSectionMapFromSharedPreferences(getApplication()) == null) {
                                SharedPreferencesHelper.saveCourseWithSharedPreference(getApplicationContext(),profile.getProgram(), profile.getShift(), profile.getLevel(), profile.getTerm(), profile.getSection());
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

    }*/
    private void initializeOnClickListeners() {
        findViewById(R.id.sign_in).setOnClickListener(this);
        findViewById(R.id.sign_up).setOnClickListener(this);
        findViewById(R.id.forgot_pass).setOnClickListener(this);
        findViewById(R.id.sign_in_as_student).setOnClickListener(this);
    }

    private void completeProfileStudent() {
        SharedPreferencesHelper.setUserType(this, HelperClass.USER_TYPE_STUDENT);
        makeToast("Please complete your profile information");
        startActivity(new Intent(this, CompleteNewProfileStudent.class));
    }

    private void signInAsAdminOrTeacher(final String userType) {

        showCircularProgressBar(true);

        if (SharedPreferencesHelper.getTeacherOfflineProfile(this) == null) {

            DocumentReference profile = FirebaseFirestore.getInstance().document("teacher_profiles/" + mAuth.getCurrentUser().getEmail());

            profile.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {

                        showCircularProgressBar(false);

                        ProfileObjectTeacher profile = documentSnapshot.toObject(ProfileObjectTeacher.class);
                        SharedPreferencesHelper.saveTeacherProfileToSharedPref(getApplicationContext(), profile);
                        saveUserTypeAndLaunchMainActivity(userType);

                    } else {
                        makeToast("Profile doesn't exist in teacher list.\nContact admin.");
                        showCircularProgressBar(false);
                        saveUserTypeAndLaunchMainActivity(userType);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    makeToast("Login Failed.Please check your internet connection.");
                    showCircularProgressBar(false);
                }
            });
        } else {
            showCircularProgressBar(false);
            saveUserTypeAndLaunchMainActivity(userType);
        }

    }

    private void saveUserTypeAndLaunchMainActivity(String userType) {
        switch (userType) {
            case HelperClass.USER_TYPE_ADMIN:
                SharedPreferencesHelper.setUserType(this, HelperClass.USER_TYPE_ADMIN);
                break;
            case HelperClass.USER_TYPE_TEACHER:
                SharedPreferencesHelper.setUserType(this, HelperClass.USER_TYPE_TEACHER);
                break;
        }
        startMainActivity();
    }


    /*private void signInAsTeacher() {
        new SharedPreferencesHelper().setUserType(this,HelperClass.USER_TYPE_TEACHER);
        startMainActivity();
    }*/
    private void signInAsStudent() {
        startMainActivity();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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

            case R.id.sign_in_as_student:
                completeProfileStudent();
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

        SharedPreferencesHelper.saveUserEmail(getApplicationContext(), sEmail);
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
                    showCircularProgressBar(false);
                }
            }
        });

    }

    private void styleProgressBar() {

        CircularProgressDrawable progressDrawable = new CircularProgressDrawable(this);
        progressDrawable.setColorSchemeColors(4359668, 14369847, 16036864, 1023320);
        progressDrawable.setStrokeWidth(8f);

        ProgressBar progressBar = findViewById(R.id.circular_progress_bar);
        progressBar.setIndeterminateDrawable(progressDrawable);

    }

    private void showCircularProgressBar(boolean visible) {
        LinearLayout progressBarHolder = findViewById(R.id.progress_bar_holder);

        if (visible) {
            progressBarHolder.setVisibility(View.VISIBLE);
        } else {
            progressBarHolder.setVisibility(View.GONE);
        }
    }

    private void signOut() {
        try {
            mAuth.signOut();
        } catch (Exception e) {
            Timber.e(e);
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
