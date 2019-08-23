package bd.edu.daffodilvarsity.classmanager.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.activities.EditStudentProfile;
import bd.edu.daffodilvarsity.classmanager.adapters.CourseListRecyclerViewAdapter;
import bd.edu.daffodilvarsity.classmanager.dialogs.CourseAndSectionSelectorDialog;
import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectStudent;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileStudents extends Fragment implements View.OnClickListener {

    private static final String TAG = "ProfileStudents";

    private ProfileObjectStudent mUserProfile;

    private SwipeRefreshLayout pullToRefresh;

    private TextView name;

    private TextView studentID;

    private TextView department;

    private TextView shift;

    private TextView section;

    private TextView level;

    private TextView term;

    private TextView addCourseBtn;

    private RecyclerView mRecyclerView;

    private CourseListRecyclerViewAdapter mAdapter;

    private ArrayList<String> courseList = new ArrayList<>();

    private ArrayList<String> sectionList = new ArrayList<>();

    public ProfileStudents() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_students, container, false);
        setHasOptionsMenu(true);

        initializeVariables(view);

        initializeRecyclerView();

        loadCoursesAndSectionFromHashMap();

        return view;
    }

    @Override
    public void onStart() {
        
        super.onStart();

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadProfileData();
            }
        });

        loadProfileData();
        loadCoursesAndSectionFromHashMap();

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_student_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_profile_student:
                launchEditProfile();
                return true;

            default:
                return false;
        }
    }

    private void launchEditProfile() {
        //Convert the profile object to JSON string
        Gson gson = new Gson();
        String profileJsonString = gson.toJson(mUserProfile);
        Intent intent = new Intent(getActivity(), EditStudentProfile.class);
        intent.putExtra("profileData",profileJsonString);
        startActivity(intent);
    }

    private void initializeVariables(View view) {

        pullToRefresh = view.findViewById(R.id.pull_to_refresh);

        name = view.findViewById(R.id.name);

        studentID = view.findViewById(R.id.student_id);

        department = view.findViewById(R.id.department);

        shift = view.findViewById(R.id.shift);

        section = view.findViewById(R.id.section);

        level = view.findViewById(R.id.level);

        term = view.findViewById(R.id.term);

        mRecyclerView = view.findViewById(R.id.course_list_recycler_view);

        addCourseBtn = view.findViewById(R.id.add_course);
        addCourseBtn.setOnClickListener(this);

    }

    private void initializeRecyclerView() {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new CourseListRecyclerViewAdapter(courseList, sectionList);

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnDeleteClickListener(new CourseListRecyclerViewAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClicked(int position) {
                showDeletionDialog(position);
            }
        });

    }

    private void showDeletionDialog(int position) {

        final String courseCode = courseList.get(position);

        String section = sectionList.get(position);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

        dialogBuilder.setTitle("Are you sure to remove this item?")
                .setMessage("Course Code : " + courseCode + " And Section : " + section)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCourseFromSharedPref(courseCode);
                    }
                }).setNegativeButton("Cancel", null);

        dialogBuilder.show();

    }

    private void deleteCourseFromSharedPref(String courseCode) {

        HashMap<String, String> courseHashMap = getCoursesFromSharedPreferences();

        courseHashMap.remove(courseCode);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();

        String courseMapInJson = gson.toJson(courseHashMap);

        editor.putString(HelperClass.COURSES_HASH_MAP, courseMapInJson).apply();

        loadCoursesAndSectionFromHashMap();

    }

    private void loadCoursesAndSectionFromHashMap() {

        HashMap<String, String> hashMap = getCoursesFromSharedPreferences();

        if (hashMap == null) {
            makeToast("Course List maybe empty");
            return;
        }

        courseList.clear();
        sectionList.clear();

        for (HashMap.Entry<String, String> entry : hashMap.entrySet()) {

            String courseCode = entry.getKey();

            String section = entry.getValue();

            courseList.add(courseCode);
            sectionList.add(section);

        }

        mAdapter.notifyDataSetChanged();

    }

    private void loadProfileData() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference profileRef = FirebaseFirestore.getInstance().document("/student_profiles/" + uid);

        profileRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    mUserProfile = documentSnapshot.toObject(ProfileObjectStudent.class);
                    displayData(mUserProfile);

                    pullToRefresh.setRefreshing(false);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pullToRefresh.setRefreshing(false);
                Log.e(TAG, "Error : ", e);
                makeToast("Error while loading. Please check your internet connection.");
            }
        });

    }

    private void displayData(ProfileObjectStudent profile) {

        name.setText(profile.getName());
        studentID.setText(profile.getId());
        department.setText(profile.getDepartment());
        shift.setText(profile.getShift());
        section.setText(profile.getSection());
        level.setText(profile.getLevel());
        term.setText(profile.getTerm());

    }

    private void showSectionAndCourseSelectDialog() {

        CourseAndSectionSelectorDialog dialog = new CourseAndSectionSelectorDialog(getShiftFromSharedPreferences());

        dialog.setDialogItemSelectListener(new CourseAndSectionSelectorDialog.OnDialogItemSelectListener() {
            @Override
            public void onItemSelected(String section, String courseCode) {
                addNewCourseOnSharedPreference(courseCode, section);
                loadCoursesAndSectionFromHashMap();
            }
        });

        dialog.show(getChildFragmentManager(), "course_selector");

    }

    private void addNewCourseOnSharedPreference(String courseCode, String section) {

        HashMap<String, String> courseHashMap = getCoursesFromSharedPreferences();
        courseHashMap.put(courseCode, section);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();

        String courseMapInJson = gson.toJson(courseHashMap);

        editor.putString(HelperClass.COURSES_HASH_MAP, courseMapInJson).apply();

    }

    private HashMap<String, String> getCoursesFromSharedPreferences() {

        HashMap<String, String> courseHashMap;

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);

        Gson gson = new Gson();

        String coursesInJson = sharedPreferences.getString(HelperClass.COURSES_HASH_MAP, null);

        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();

        courseHashMap = gson.fromJson(coursesInJson, type);

        if (courseHashMap == null) {
            return new HashMap<String, String>();
        } else {
            return courseHashMap;
        }
    }

    private void makeToast(String msg) {
        if (getContext() != null) {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    private String getShiftFromSharedPreferences() {

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, Context.MODE_PRIVATE);

        String shift = sharedPreferences.getString(HelperClass.SHIFT, null);

        return shift;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_course:
                showSectionAndCourseSelectDialog();
                break;
        }
    }
}
