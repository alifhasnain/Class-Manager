package bd.edu.daffodilvarsity.classmanager.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.CRObj;
import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectTeacher;
import bd.edu.daffodilvarsity.classmanager.otherclasses.SharedPreferencesHelper;
import bd.edu.daffodilvarsity.classmanager.routine.EachDayClassRepository;

public class AddCR extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG_CR_LIST_SHARED_PREF = "cr-list-shared-pref";

    private Spinner sectionSpinner;

    private Spinner courseCodeSpinner;

    private Spinner shiftSpinner;

    private TextView courseName;

    private TextInputLayout name;

    private TextInputLayout email;

    private TextInputLayout contactNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cr);

        initializeVariables();

        loadTeacherCourses();

    }

    private void initializeVariables() {

        sectionSpinner = findViewById(R.id.section_spinner);
        courseCodeSpinner = findViewById(R.id.course_codes_spinner);
        shiftSpinner = findViewById(R.id.shift_spinner);

        courseName = findViewById(R.id.course_name);

        name = findViewById(R.id.name);
        contactNo = findViewById(R.id.contact_no);
        email= findViewById(R.id.email);

        findViewById(R.id.save).setOnClickListener(this);

    }

    private void loadTeacherCourses() {

        final ProfileObjectTeacher profile = SharedPreferencesHelper.getTeacherOfflineProfile(getApplicationContext());

        final EachDayClassRepository repo = new EachDayClassRepository(getApplication());

        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<String> courseList = repo.getTeacherCourses(profile.getTeacherInitial());
                courseList.add(0,"Undifined");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initializeSpinners(courseList);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void initializeSpinners(final List<String> teacherCourses) {

        String[] section = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U" , "R1" , "R2" , "R3" , "R4"};

        final String[] shift = new String[] {HelperClass.SHIFT_DAY , HelperClass.SHIFT_EVENING};

        final ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(this, R.layout.spinner_items, teacherCourses);

        ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(this, R.layout.spinner_items, section);

        ArrayAdapter<String> shiftAdapter = new ArrayAdapter<>(this, R.layout.spinner_items ,shift);

        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sectionAdapter.setDropDownViewResource(R.layout.spinner_dropdown_items);

        shiftAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        courseCodeSpinner.setAdapter(courseAdapter);

        sectionSpinner.setAdapter(sectionAdapter);

        shiftSpinner.setAdapter(shiftAdapter);

        courseCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String courseNameStr = HelperClass.getCourseNameFromCourseCode(shiftSpinner.getSelectedItem().toString(),teacherCourses.get(i));
                courseName.setText(courseNameStr);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        shiftSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String courseNameStr = HelperClass.getCourseNameFromCourseCode(shiftSpinner.getSelectedItem().toString(),teacherCourses.get(i));
                courseName.setText(courseNameStr);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private boolean validateData() {
        if (name.getEditText().getText().toString().isEmpty()) {
            makeToast("Name can't be empty.");
            return false;
        }
        return true;
    }

    private void saveData() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<CRObj>>(){}.getType();
        SharedPreferences sharedPreferences = getSharedPreferences(TAG_CR_LIST_SHARED_PREF, Context.MODE_PRIVATE);
        String crJson = sharedPreferences.getString("CR","");
        ArrayList<CRObj> tempList = gson.fromJson(crJson, type);
        if (tempList == null) {
            tempList = new ArrayList<>();
        }
        CRObj crObj = new CRObj(
                name.getEditText().getText().toString(),
                email.getEditText().getText().toString(),
                courseCodeSpinner.getSelectedItem().toString(),
                courseName.getText().toString(),
                sectionSpinner.getSelectedItem().toString(),
                contactNo.getEditText().getText().toString()
        );
        tempList.add(crObj);
        crJson = gson.toJson(tempList);
        sharedPreferences.edit().putString("CR",crJson).apply();
        makeToast("Saved!");
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                if( validateData()){
                    saveData();
                }
                break;
        }
    }

    private void makeToast(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
}
