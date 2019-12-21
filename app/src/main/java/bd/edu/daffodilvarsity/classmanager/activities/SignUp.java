package bd.edu.daffodilvarsity.classmanager.activities;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import bd.edu.daffodilvarsity.classmanager.R;
import timber.log.Timber;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;

    FirebaseAuth.AuthStateListener mAuthStateListener;

    TextInputLayout mEmailEditText;

    TextInputLayout mPasswordEditText;

    Snackbar mSnackbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        startGradientAnimation();

        initializeVariables();

        initializeOnClickListeners();
    }

    private void initializeVariables() {

        mEmailEditText = findViewById(R.id.email);

        mPasswordEditText = findViewById(R.id.pass);

        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth.getCurrentUser() != null) {
                    sendVerificationMail();
                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mAuth.addAuthStateListener(mAuthStateListener);
        } catch (NullPointerException e) {
            Timber.e(e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mAuth.removeAuthStateListener(mAuthStateListener);
        } catch (NullPointerException e) {
            Timber.e(e);
        }
    }

    private void initializeOnClickListeners() {
        findViewById(R.id.sign_in).setOnClickListener(this);
        findViewById(R.id.sign_up).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in:
                onBackPressed();
                break;

            case R.id.sign_up:
                clearFocusAndErrorMsg();
                createNewUser();
                break;

        }
    }

    private void clearFocusAndErrorMsg() {
        mEmailEditText.setFocusable(false);
        mPasswordEditText.setFocusable(false);
        mEmailEditText.setError(null);
        mPasswordEditText.setError(null);
    }

    private void createNewUser() {

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
            Toast.makeText(this, "Invalid Email!", Toast.LENGTH_SHORT).show();
            mEmailEditText.setError("Invalid email address.");
            return;
        }
        if (sPassword.length() < 6) {
            mPasswordEditText.setError("Minimum length of password is 6");
            return;
        }

        showCircularProgressBar(true);

        mAuth.createUserWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()) {

                    showCircularProgressBar(false);

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        makeSnackBar("User already exist.\nTry resetting password.");
                    } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        makeSnackBar("Invalid Credential");
                    } else if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                        makeToast("Password is weak.");
                    }
                }

            }
        });
    }

    private void startGradientAnimation() {

        //Hide Statusbar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Fade fade = new Fade();
        fade.excludeTarget(findViewById(R.id.root_layout), true);
        fade.excludeTarget(findViewById(R.id.diu_logo), true);
        getWindow().setEnterTransition(fade);

        AnimationDrawable gradientAnimation = (AnimationDrawable) findViewById(R.id.root_layout).getBackground();
        gradientAnimation.setEnterFadeDuration(200);
        gradientAnimation.setExitFadeDuration(3000);
        gradientAnimation.start();

    }

    private void sendVerificationMail() {

        FirebaseUser user = mAuth.getCurrentUser();

        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful() && !isEmailVerified()) {
                    showCircularProgressBar(false);
                    makeToast("Verification Email is sent confirm registration and Sign In");
                    mEmailEditText.getEditText().setText("");
                    mPasswordEditText.getEditText().setText("");
                } else if (!task.isSuccessful()) {
                    showCircularProgressBar(false);
                }
                signOut();
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

    private void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void makeSnackBar(String text) {

        View activityView = findViewById(R.id.root_layout);

        mSnackbar = Snackbar.make(activityView, text, Snackbar.LENGTH_INDEFINITE);

        mSnackbar.setAction("Done", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSnackbar.dismiss();
            }
        });
        mSnackbar.show();
    }
}
