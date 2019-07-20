package bd.edu.daffodilvarsity.classmanager.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.adapters.ClassListRecyclerViewAdapter;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ClassDetails;
import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectTeacher;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SundayClasses extends Fragment {

    private static final String TAG = "SundayClasses";

    private FirebaseAuth mAuth;

    CollectionReference mSundayRef;

    private ArrayList<ClassDetails> mClasses = new ArrayList<>();

    private TextView loadingContent;

    private ProgressBar progressBar;

    private FirebaseFirestore db;

    private RecyclerView recyclerView;

    private ClassListRecyclerViewAdapter adapter;

    private SwipeRefreshLayout mPullToRefresh;

    public SundayClasses() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_each_day_classes, container, false);

        initializeVariables(view);

        initializeRecyclerView();

        if (getUserType().equals(HelperClass.USER_TYPE_TEACHER)) {
            loadTeacherInitialAndClasses();
        } else if (getUserType().equals(HelperClass.USER_TYPE_STUDENT)) {
            loadStudentClasses();
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getUserType().equals(HelperClass.USER_TYPE_TEACHER)) {
                    loadTeacherInitialAndClasses();
                } else if (getUserType().equals(HelperClass.USER_TYPE_STUDENT)) {
                    loadStudentClasses();
                }
            }
        });
    }

    private void initializeVariables(View view) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mSundayRef = db.collection("main_campus/classes_day/sunday");
        progressBar = view.findViewById(R.id.progress_bar);
        loadingContent = view.findViewById(R.id.loading_content);
        recyclerView = view.findViewById(R.id.recycler_view);
        mPullToRefresh = view.findViewById(R.id.swipe_to_refresh);
        mPullToRefresh.setDistanceToTriggerSync(450);
    }

    private void initializeRecyclerView()   {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClassListRecyclerViewAdapter(mClasses);
        recyclerView.setAdapter(adapter);
    }

    private void loadStudentClasses() {

        showProgressbar(true);
        //Clear recyclerview items
        mClasses.clear();
        notifyRecyclerViewAdapter();

        List<Task<QuerySnapshot>> taskList = new ArrayList<>();

        HashMap<String, String> courses = getCoursesFromSharedPreferences();

        for (HashMap.Entry<String, String> entry : courses.entrySet()) {

            String courseCode = entry.getKey();

            String section = entry.getValue();

            taskList.add(
                    mSundayRef.whereEqualTo("courseCode", courseCode)
                            .whereEqualTo("section", section).get()
            );
        }

        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(taskList);

        allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                for (QuerySnapshot qs : querySnapshots) {
                    for (DocumentSnapshot ds : qs) {
                        mClasses.add(ds.toObject(ClassDetails.class));
                    }
                }
                sortCollection();

                notifyRecyclerViewAdapter();
                showProgressbar(false);
                mPullToRefresh.setRefreshing(false);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showProgressbar(false);
                        mPullToRefresh.setRefreshing(false);
                        e.printStackTrace();
                    }
                });
    }

    private void sortCollection() {

        Collections.sort(mClasses, new Comparator<ClassDetails>() {
            @Override
            public int compare(ClassDetails o1, ClassDetails o2) {
                return Float.compare(o1.getPriority(), o2.getPriority());
            }
        });

    }

    private void loadTeacherInitialAndClasses() {

        showProgressbar(true);

        mClasses.clear();
        notifyRecyclerViewAdapter();

        String email = mAuth.getCurrentUser().getEmail();

        CollectionReference teacherProfiles = db.collection("/teacher_profiles");

        teacherProfiles.whereEqualTo("email", email).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot ds : queryDocumentSnapshots) {
                            ProfileObjectTeacher profile = ds.toObject(ProfileObjectTeacher.class);
                            loadTeacherClasses(profile.getTeacherInitial());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showProgressbar(false);
                    }
                });


    }

    private void loadTeacherClasses(String teacherInitial) {

        CollectionReference saturdayRefDay = db.collection("/main_campus/classes_day/sunday");

        CollectionReference saturdayRefEvening = db.collection("/main_campus/classes_evening/sunday");

        Task<QuerySnapshot> task1 = saturdayRefDay.whereEqualTo("teacherInitial", teacherInitial).get();

        Task<QuerySnapshot> task2 = saturdayRefEvening.whereEqualTo("teacherInitial", teacherInitial).get();

        Task<List<QuerySnapshot>> tasks = Tasks.whenAllSuccess(task1, task2);

        tasks
                .addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
                    @Override
                    public void onSuccess(List<QuerySnapshot> querySnapshots) {
                        for(QuerySnapshot qs : querySnapshots)  {
                            for(DocumentSnapshot ds : qs)   {
                                mClasses.add(ds.toObject(ClassDetails.class));
                            }
                        }
                        sortCollection();
                        showProgressbar(false);
                        notifyRecyclerViewAdapter();
                        mPullToRefresh.setRefreshing(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeToast("Failed to load data.Please check your internet connection.");
                        showProgressbar(false);
                        mPullToRefresh.setRefreshing(false);
                        Log.e(TAG, "Error", e);
                    }
                });
    }

    private HashMap<String, String> getCoursesFromSharedPreferences() {

        HashMap<String, String> courseHashMap;

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);

        Gson gson = new Gson();

        String coursesInJson = sharedPreferences.getString(HelperClass.COURSES_HASH_MAP, null);

        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();

        courseHashMap = gson.fromJson(coursesInJson, type);

        return courseHashMap;
    }

    private String getUserType() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);
        return sharedPreferences.getString(HelperClass.USER_TYPE, null);
    }

    private void notifyRecyclerViewAdapter()    {
        if(adapter!=null)   {
            adapter.notifyDataSetChanged();
            recyclerView.scheduleLayoutAnimation();
        }
    }

    private void showProgressbar(boolean b) {
        if(b)   {
            progressBar.setVisibility(View.VISIBLE);
            loadingContent.setVisibility(View.VISIBLE);
        }
        else    {
            progressBar.setVisibility(View.GONE);
            loadingContent.setVisibility(View.GONE);
        }
    }

    private void makeToast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

}
