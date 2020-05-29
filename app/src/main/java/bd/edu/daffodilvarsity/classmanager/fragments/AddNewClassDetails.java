package bd.edu.daffodilvarsity.classmanager.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ClassDetails;
import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;
import bd.edu.daffodilvarsity.classmanager.otherclasses.RoutineObj;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddNewClassDetails extends Fragment implements View.OnClickListener {

    private EditText courseCode;
    private EditText roomNo;
    private EditText courseName;
    private EditText priority;
    private EditText time;
    private EditText teacherInitial;

    private Spinner shift;
    private Spinner dayOfWeek;
    private Spinner section;


    public AddNewClassDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add_new_class_details, container, false);

        initializeVariables(view);

        initializeSpinners();

        return view;
    }

    private void initializeVariables(View view) {

        view.findViewById(R.id.add_data).setOnClickListener(this);

        courseCode = view.findViewById(R.id.course_code);

        courseName = view.findViewById(R.id.course_name);

        courseCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                courseName.setText(HelperClass.getCourseNameFromCourseCode(shift.getSelectedItem().toString(),String.valueOf(charSequence)));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        roomNo = view.findViewById(R.id.room_no);
        teacherInitial = view.findViewById(R.id.teacher_initial);
        time = view.findViewById(R.id.time);
        priority = view.findViewById(R.id.priority);

        shift = view.findViewById(R.id.shift);
        dayOfWeek = view.findViewById(R.id.day_of_week);
        section = view.findViewById(R.id.section_spinner);
    }

    private void initializeSpinners() {
        String[] shift = new String[]{"Day","Evening"};
        String[] section = HelperClass.getAllSections();
        String[] dayOfWeek = HelperClass.getSevenDaysOfWeek().toArray(new String[HelperClass.getSixDaysOfWeek().size()]);

        ArrayAdapter shiftAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,shift);
        ArrayAdapter sectionAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,section);
        ArrayAdapter dayOfWeekAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,dayOfWeek);

        shiftAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dayOfWeekAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.section.setAdapter(sectionAdapter);
        this.dayOfWeek.setAdapter(dayOfWeekAdapter);
        this.shift.setAdapter(shiftAdapter);

    }

    private void checkAndAddData() {
        final ClassDetails cd = new ClassDetails();
        if (courseCode.getText().toString().trim().isEmpty() || courseName.getText().toString().trim().isEmpty() || priority.getText().toString().trim().isEmpty() || roomNo.getText().toString().trim().isEmpty() || time.getText().toString().trim().isEmpty() || teacherInitial.getText().toString().trim().isEmpty()) {
            makeToast("Please fill all fields.");
            return;
        } else {
            cd.setCourseCode(courseCode.getText().toString().trim());
            cd.setCourseName(courseName.getText().toString().trim());
            cd.setPriority(Float.parseFloat(priority.getText().toString().trim()));
            cd.setRoom(roomNo.getText().toString().trim());
            cd.setTime(time.getText().toString().trim());
            cd.setTeacherInitial(teacherInitial.getText().toString().trim());

            cd.setSection(section.getSelectedItem().toString());
            cd.setDayOfWeek(dayOfWeek.getSelectedItem().toString().trim());
            cd.setShift(shift.getSelectedItem().toString().trim());
        }

        DocumentReference docRef = FirebaseFirestore.getInstance().document("/routine/routine/");

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                RoutineObj routime = documentSnapshot.toObject(RoutineObj.class);
                if (cd.getShift().equals(HelperClass.SHIFT_DAY)) {
                    String jsonRoutineDay = routime.getDay();
                    ArrayList<ClassDetails> classes = parseFromJson(jsonRoutineDay);
                    classes.add(cd);
                    showAlertDialog("Are you sure?","Please double check before adding a new class.",classes);
                } else {
                    if (cd.getShift().equals(HelperClass.SHIFT_DAY)) {
                        String jsonRoutineEve = routime.getEvening();
                        ArrayList<ClassDetails> classes = parseFromJson(jsonRoutineEve);
                        classes.add(cd);
                        showAlertDialog("Are you sure?","Please double check before adding a new class.",classes);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Timber.e(e);
                makeToast("Failed to load.\nPlease check your connection.");
            }
        });
    }

    private void updateClasses(String shift,String classes) {
        DocumentReference docRef = FirebaseFirestore.getInstance().document("/routine/routine/");
        docRef.update(shift,classes).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                makeToast("Added Successfully.");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                makeToast("Failed to add.\nPlease check your connection.");
            }
        });
    }

    private ArrayList<ClassDetails> parseFromJson(String json) {
        Type type = new TypeToken<ArrayList<ClassDetails>>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(json,type);
    }

    private String  parseToJson(ArrayList<ClassDetails> classes) {
        Gson gson = new Gson();
        return gson.toJson(classes);
    }

    private void showAlertDialog(String title, String body, final ArrayList<ClassDetails> classes) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(body)
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateClasses("day",parseToJson(classes));
                    }
                })
                .setNegativeButton("Cancel",null)
                .create();

        alertDialog.show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_data) {
            checkAndAddData();
        }
    }

    private void makeToast(String text) {
        if (getContext() != null) {
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        }
    }
}
