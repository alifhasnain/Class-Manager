package bd.edu.daffodilvarsity.classmanager.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.BookedClassDetailsUser;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ClassDetails;
import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;
import timber.log.Timber;

public class FinishBookingAdmin extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout email;

    private TextInputLayout teacherInitial;

    private TextInputLayout reservationDate;

    private TextInputLayout time;

    private TextInputLayout roomNo;

    private TextInputLayout courseCode;

    private TextInputLayout section;

    private TextInputLayout shift;

    private ClassDetails mClass;

    private Calendar date;

    private DateFormat mDateFormater = new SimpleDateFormat("EEE, d MMM, yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_booking_admin);

        String jsonClassDetails = getIntent().getExtras().getString("data");

        String jsonDate = getIntent().getExtras().getString("date");

        mClass = parseJsonToClassDetails(jsonClassDetails);

        date = parseJsonToCalendar(jsonDate);

        if (mClass == null || date == null) {
            setResult(Activity.RESULT_CANCELED);
            makeToast("There was an error please try again");
            finish();
        }

        initializeVariables();

        initDataOnViews();

    }

    private void initializeVariables() {
        email = findViewById(R.id.email);
        teacherInitial = findViewById(R.id.teacher_initial);
        reservationDate = findViewById(R.id.reservation_date);
        time = findViewById(R.id.time);
        roomNo = findViewById(R.id.room_no);
        courseCode = findViewById(R.id.course_code);
        section = findViewById(R.id.section);
        shift = findViewById(R.id.shift);

        findViewById(R.id.proceed).setOnClickListener(this);
    }

    private void initDataOnViews() {
        reservationDate.getEditText().setText(mDateFormater.format(date.getTime()));
        time.getEditText().setText(mClass.getTime());
        roomNo.getEditText().setText(mClass.getRoom());
        shift.getEditText().setText(mClass.getShift());
    }

    private void checkTeacherDataThenBook() {

        makeToast("Please wait while processing");

        String teacherEmail = email.getEditText().getText().toString().trim();

        DocumentReference profileRef = FirebaseFirestore.getInstance().document("/teacher_profiles/" + teacherEmail);

        DocumentReference bookCountRef = FirebaseFirestore.getInstance().document("/book_room_count/" + teacherEmail);

        Task<DocumentSnapshot> task1 = profileRef.get();

        Task<DocumentSnapshot> task2 = bookCountRef.get();

        Task<List<DocumentSnapshot>> allTask = Tasks.whenAllSuccess(task1, task2);

        allTask.addOnSuccessListener(new OnSuccessListener<List<DocumentSnapshot>>() {
            @Override
            public void onSuccess(List<DocumentSnapshot> documentSnapshots) {
                if (documentSnapshots.get(0).exists()) {
                    if (documentSnapshots.get(1).exists()) {
                        double count = documentSnapshots.get(1).getDouble("counter");
                        if (count >= 12) {
                            showConfirmationDialog(
                                    "Are you sure?",
                                    "The user already booked 12 rooms in this month. Do you want to proceed?"
                            );
                        }
                    } else {
                        finishBook();
                    }

                } else {
                    showConfirmationDialog(
                            "Are you sure?",
                            "Email you entered doesn't exist in the database. Are you sure you want to book room?"
                    );
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                makeToast("Failed to load.\nPlease check your connection");
                Timber.e(e);
            }
        });
    }

    private void finishBook() {

        final BookedClassDetailsUser bcd = generateObject();

        FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                long time = getReservationTimeInMillis();
                DocumentReference docRef = FirebaseFirestore.getInstance().document("/booked_classes/" + time + "x" + bcd.getRoomNo() + "x" + bcd.getTime());
                DocumentSnapshot ds = transaction.get(docRef);
                if (ds.exists()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            makeToast("This room is already booked.");
                        }
                    });
                } else {
                    DocumentReference bookCountRef = FirebaseFirestore.getInstance().document("/book_room_count/" + bcd.getTeacherEmail());
                    DocumentSnapshot countSnapshot = transaction.get(bookCountRef);
                    if (!countSnapshot.exists()) {
                        HashMap<String, Integer> map = new HashMap<>();
                        map.put("counter", 1);
                        transaction.set(bookCountRef, map);
                    } else {
                        double count = countSnapshot.getDouble("counter") + 1;
                        transaction.update(bookCountRef, "counter", count);
                    }
                    transaction.set(docRef, bcd);
                }
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                makeToast("Success");
                setResult(RESULT_OK);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Timber.e(e);
            }
        });

    }

    private BookedClassDetailsUser generateObject() {

        BookedClassDetailsUser bcd = new BookedClassDetailsUser();

        bcd.setTeacherEmail(email.getEditText().getText().toString().trim());

        bcd.setTeacherInitial(teacherInitial.getEditText().getText().toString().trim().toUpperCase());

        bcd.setReservationDate(getReservationDateTimestamp());

        bcd.setTimeWhenUserBooked(getCurrentTimestamp());

        bcd.setTime(mClass.getTime());

        bcd.setRoomNo(mClass.getRoom());

        bcd.setCourseCode(courseCode.getEditText().getText().toString().trim().toUpperCase());

        bcd.setSection(section.getEditText().getText().toString().trim().toUpperCase());

        bcd.setShift(mClass.getShift());

        bcd.setPriority(mClass.getPriority());

        bcd.setDayOfWeek(getDayOfWeek(date));

        bcd.setProgram(HelperClass.PROGRAM_BSC);

        return bcd;

    }

    private Timestamp getReservationDateTimestamp() {

        TimeZone timeZone = TimeZone.getTimeZone("Asia/Dhaka");

        Calendar temp = Calendar.getInstance(timeZone);
        temp.set(Calendar.HOUR, 0);
        temp.set(Calendar.MINUTE, 0);
        temp.set(Calendar.SECOND, 0);
        temp.set(Calendar.MILLISECOND, 0);
        temp.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
        temp.set(Calendar.MONTH, date.get(Calendar.MONTH));
        temp.set(Calendar.YEAR, date.get(Calendar.YEAR));

        return new Timestamp(temp.getTime());

    }

    private long getReservationTimeInMillis() {

        TimeZone timeZone = TimeZone.getTimeZone("Asia/Dhaka");

        Calendar temp = Calendar.getInstance(timeZone);
        temp.set(Calendar.HOUR, 0);
        temp.set(Calendar.MINUTE, 0);
        temp.set(Calendar.SECOND, 0);
        temp.set(Calendar.MILLISECOND, 0);
        temp.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
        temp.set(Calendar.MONTH, date.get(Calendar.MONTH));
        temp.set(Calendar.YEAR, date.get(Calendar.YEAR));

        return temp.getTimeInMillis();

    }

    private Timestamp getCurrentTimestamp() {

        TimeZone timeZone = TimeZone.getTimeZone("Asia/Dhaka");

        Calendar temp = Calendar.getInstance(timeZone);

        return new Timestamp(temp.getTime());

    }

    private void showConfirmationDialog(String title, String message) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finishBook();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private ClassDetails parseJsonToClassDetails(String json) {

        Type type = new TypeToken<ClassDetails>() {
        }.getType();

        Gson gson = new Gson();

        return gson.fromJson(json, type);

    }

    private Calendar parseJsonToCalendar(String json) {

        Type type = new TypeToken<Calendar>() {
        }.getType();

        Gson gson = new Gson();

        return gson.fromJson(json, type);

    }

    private String getDayOfWeek(Calendar calendar) {

        String day = "";

        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                day = "Sunday";
                break;
            case 2:
                day = "Monday";
                break;
            case 3:
                day = "Tuesday";
                break;
            case 4:
                day = "Wednesday";
                break;
            case 5:
                day = "Thursday";
                break;
            case 6:
                day = "Friday";
                break;
            case 7:
                day = "Saturday";
                break;
        }

        return day;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.proceed) {
            checkTeacherDataThenBook();
        }
    }
}
