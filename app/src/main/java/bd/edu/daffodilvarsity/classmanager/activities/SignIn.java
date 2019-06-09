package bd.edu.daffodilvarsity.classmanager.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import bd.edu.daffodilvarsity.classmanager.R;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class SignIn extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;

    FirebaseAuth.AuthStateListener mAuthStateListener;

    SmoothProgressBar mSmoothProgressBar;

    TextInputLayout mEmailEditText;

    TextInputLayout mPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        startGradientAnimation();

        initializeVariables();

        initializeOnClickListeners();

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

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null && isEmailVerified())    {
                    makeToast("Signing in...");
                    startActivity(new Intent(SignIn.this,MainActivity.class));
                    finish();
                }
                else if(firebaseAuth.getCurrentUser()!=null){
                    sendVerificationMail();
                }
            }
        };

        mSmoothProgressBar = findViewById(R.id.smooth_progress_bar);

        mEmailEditText = findViewById(R.id.email);

        mPasswordEditText = findViewById(R.id.pass);

    }

    private void initializeOnClickListeners() {
        findViewById(R.id.sign_in).setOnClickListener(this);
        findViewById(R.id.sign_up).setOnClickListener(this);
        findViewById(R.id.forgot_pass).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.sign_in:
                clearFocusAndErrorMsg();
                checkCredentialAndSignIn();
                break;

            case R.id.sign_up:
                startSignUpActivityWithTransition();
                break;

            case R.id.forgot_pass:
                startActivity(new Intent(SignIn.this,ForgotPassword.class));
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

        mSmoothProgressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()) {
                    mSmoothProgressBar.setVisibility(View.INVISIBLE);
                    if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        makeToast("Email does not exist in database.");
                    }
                    else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                        makeToast("Invalid password.");
                    }
                    else    {
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
                    mSmoothProgressBar.setVisibility(View.INVISIBLE);
                    makeToast("Verification Email is sent confirm registration and SignIn");
                    mEmailEditText.getEditText().setText("");
                    mPasswordEditText.getEditText().setText("");
                    signOut();
                }
                else if(!task.isSuccessful())   {
                    mSmoothProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    private void signOut() {
        try {
            mAuth.signOut();
        }
        catch (Exception e)  {
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

    private void startSignUpActivityWithTransition()    {

        Intent intent = new Intent(SignIn.this, SignUp.class);

        Pair<View,String>[] pairs = new Pair[3];
        pairs[0] = new Pair<>(findViewById(R.id.sign_in), "sign_in_transition");
        pairs[1] = new Pair<>(findViewById(R.id.sign_up), "sign_up_transition");
        pairs[2] = new Pair<>(findViewById(R.id.forgot_pass) , "text_view_transition");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignIn.this, pairs);
        startActivity(intent, options.toBundle());
    }

    private void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
