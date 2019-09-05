package bd.edu.daffodilvarsity.classmanager.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.BookedClassDetailsUser;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectTeacher;

public class CancelBookByAdmin extends AppCompatActivity implements View.OnClickListener {

    private TextView reserveDate;

    private TextView roomNo;

    private TextView time;

    private TextView teacherName;

    private TextView email;

    private TextView initial;

    private EditText title;

    private EditText description;

    private Button sendPushMsgAndEmail;

    private Button cancelBook;

    private BookedClassDetailsUser mSelectedClass;

    private ProfileObjectTeacher mProfile;

    private SwipeRefreshLayout pullToRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_book_by_admin);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_delete);

        initializeVariables();

        loadTeacherProfile();

        initializeDescriptionText();

    }

    private void initializeDescriptionText() {

        String text = "Room " + mSelectedClass.getRoomNo() + " you booked in " + getFormattedDate(mSelectedClass.getReservationDate()) + " at " + mSelectedClass.getTime() + " was cancelled by admin due to some official event.";

        description.setText(text);

    }

    @Override
    protected void onStart() {
        super.onStart();
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadTeacherProfile();
            }
        });
    }

    private void loadTeacherProfile() {

        DocumentReference profileRef = FirebaseFirestore.getInstance().document("/teacher_profiles/" + mSelectedClass.getTeacherEmail() );

        profileRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())   {
                    mProfile = documentSnapshot.toObject(ProfileObjectTeacher.class);
                    pullToRefresh.setRefreshing(false);
                    loadData();
                }
                else {
                    pullToRefresh.setRefreshing(false);
                    makeToast("This email doesn't exist in database");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                makeToast("Unable to load. Please check your internet connection.");
            }
        });
    }

    private void loadData() {

        reserveDate.setText(getFormattedDate(mSelectedClass.getReservationDate()));

        roomNo.setText(mSelectedClass.getRoomNo());

        time.setText(mSelectedClass.getTime());

        teacherName.setText(mProfile.getName());

        email.setText(mSelectedClass.getTeacherEmail());

        initial.setText(mSelectedClass.getTeacherInitial());


    }

    private void initializeVariables() {

        reserveDate = findViewById(R.id.reserveDate);

        roomNo = findViewById(R.id.room_no);

        time = findViewById(R.id.time);

        teacherName = findViewById(R.id.teacher_name);

        email = findViewById(R.id.email);

        initial = findViewById(R.id.teacher_initial);

        title = findViewById(R.id.title);

        description = findViewById(R.id.description);

        sendPushMsgAndEmail = findViewById(R.id.send_push_msg);
        sendPushMsgAndEmail.setOnClickListener(this);

        cancelBook = findViewById(R.id.cancel_book);
        cancelBook.setOnClickListener(this);

        pullToRefresh = findViewById(R.id.pull_to_refresh);

        Gson gson = new Gson();
        String jsonString = getIntent().getStringExtra("selectedClass");
        mSelectedClass = gson.fromJson(jsonString, BookedClassDetailsUser.class);

    }

    private String getFormattedDate(Timestamp timestamp) {

        DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM, yyyy");

        return dateFormat.format(timestamp.toDate());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.send_push_msg:
                sendPushMsgAndEmail();
                break;

            case R.id.cancel_book:
                cancelBook();
                break;
        }
    }

    private void sendPushMsgAndEmail() {

    }

    private void cancelBook() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("Are you sure to cancel this book?");

        alertDialog.setMessage("It is recommended that teacher should be notified before cancelling book.");

        alertDialog.setNegativeButton("Cancel",null);

        alertDialog.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                DocumentReference documentReference = FirebaseFirestore.getInstance().document("booked_classes/" + mSelectedClass.getDocId());

                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        setResult(RESULT_OK);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeToast("Failed to load. Please check your internet connection.");
                    }
                });
            }
        });

        alertDialog.show();
    }

    private void makeToast(String msg)  {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
