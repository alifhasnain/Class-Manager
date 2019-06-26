package bd.edu.daffodilvarsity.classmanager.activities;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import bd.edu.daffodilvarsity.classmanager.R;

public class ForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        startGradientAnimation();

        findViewById(R.id.send_password_reset_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPasswordResetMail();
            }
        });

    }

    private void sendPasswordResetMail()    {

        final TextInputLayout email = findViewById(R.id.email);
        String sEmail = email.getEditText().getText().toString().trim();

        if(sEmail.isEmpty())    {
            Toast.makeText(this, "Please enter an email.", Toast.LENGTH_SHORT).show();
            return;
        }

        showCircularProgressBar(true);
        FirebaseAuth.getInstance().sendPasswordResetEmail(sEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            email.getEditText().setText("");
                            Toast.makeText(ForgotPassword.this, "Password reset email has been sent\nPlease check your inbox.", Toast.LENGTH_SHORT).show();
                            showCircularProgressBar(false);

                        }
                        else {
                            Toast.makeText(ForgotPassword.this, "Failed to send verification email\nPlease check your Email or Internet connection.", Toast.LENGTH_SHORT).show();
                            showCircularProgressBar(false);
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

    private void startGradientAnimation() {

        //Hide Statusbar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        AnimationDrawable gradientAnimation = (AnimationDrawable) findViewById(R.id.root_layout).getBackground();
        gradientAnimation.setEnterFadeDuration(200);
        gradientAnimation.setExitFadeDuration(3000);
        gradientAnimation.start();

    }
}
