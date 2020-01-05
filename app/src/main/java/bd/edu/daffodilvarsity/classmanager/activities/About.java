package bd.edu.daffodilvarsity.classmanager.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import bd.edu.daffodilvarsity.classmanager.BuildConfig;
import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.SharedPreferencesHelper;
import bd.edu.daffodilvarsity.classmanager.routine.EachDayClassViewModel;

public class About extends AppCompatActivity implements View.OnClickListener {

    private EachDayClassViewModel mViewModel;

    private TextView version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initializeVariables();

        initializeData();

    }

    private void initializeData() {
        version.setText(BuildConfig.VERSION_NAME);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewModel = ViewModelProviders.of(this).get(EachDayClassViewModel.class);
    }

    private void checkRoutineVersion() {

        makeToast("checking..");

        FirebaseFirestore.getInstance().document("routine-version/version/").get(Source.SERVER)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {

                            String version = documentSnapshot.get("version").toString();

                            if (!version.equals(SharedPreferencesHelper.getRoutineVersionFromSharedPreferences(getApplicationContext()))) {
                                makeToast("Downloading new updated routine.");
                                mViewModel.loadWholeRoutineFromServer(version);
                            } else {
                                makeToast("Already using the latest version.");
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeToast("Failed!Please check your internet connection.");
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void initializeVariables() {

        version = findViewById(R.id.version);

        findViewById(R.id.check_routine_update).setOnClickListener(this);

        findViewById(R.id.facebook_alif).setOnClickListener(this);
        findViewById(R.id.github_alif).setOnClickListener(this);
        findViewById(R.id.instagram_alif).setOnClickListener(this);
        findViewById(R.id.send_mail_alif).setOnClickListener(this);
        findViewById(R.id.facebook_zihad).setOnClickListener(this);
        findViewById(R.id.github_zihad).setOnClickListener(this);
        findViewById(R.id.instagram_zihad).setOnClickListener(this);
        findViewById(R.id.send_mail_zihad).setOnClickListener(this);
        findViewById(R.id.facebook_saleheen).setOnClickListener(this);
        findViewById(R.id.github_saleheen).setOnClickListener(this);
        findViewById(R.id.instagram_saleheen).setOnClickListener(this);
        findViewById(R.id.send_mail_saleheen).setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.check_routine_update:
                checkRoutineVersion();
                break;
            case R.id.facebook_alif:
                openFacebook("https://www.facebook.com/ahnsas");
                break;
            case R.id.github_alif:
                openBrowser("https://github.com/alifhasnain");
                break;
            case R.id.instagram_alif:
                openInstagram("https://www.instagram.com/alifhas9/");
                break;
            case R.id.send_mail_alif:
                sendMail("hasnain.alif20@gmail.com");
                break;
            case R.id.facebook_zihad:
                openFacebook("https://www.facebook.com/mohammadar.zihad");
                break;
            case R.id.github_zihad:
                openBrowser("https://www.github.com/muhammadZihad");
                break;
            case R.id.instagram_zihad:
                openInstagram("https://www.instagram.com/muhammad_ar_zihad/");
                break;
            case R.id.send_mail_zihad:
                sendMail("zihad.muhammadar@gmail.com");
                break;
            case R.id.facebook_saleheen:
                openBrowser("https://www.facebook.com/sm.saleheen");
                break;
            case R.id.github_saleheen:
                openBrowser("https://github.com/saleheen1");
                break;
            case R.id.instagram_saleheen:
                makeToast("No Instagram Account!");
                break;
            case R.id.send_mail_saleheen:
                sendMail("smsaleheen18@gmail.com");
                break;
        }
    }

    private void openBrowser(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    private void openInstagram(String url) {
        Uri uri = Uri.parse(url);
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

        likeIng.setPackage("com.instagram.android");

        try {
            startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(url)));
        }
    }

    private void openFacebook(String url) {
        Intent intent;
        try {
            // get the Facebook app if possible
            this.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Facebook app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        }
        startActivity(intent);
    }

    private void sendMail(String email) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            this.startActivity(intent);
        }
    }

    private void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
