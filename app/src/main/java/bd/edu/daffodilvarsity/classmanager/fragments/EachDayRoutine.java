package bd.edu.daffodilvarsity.classmanager.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.adapters.ClassListRecyclerViewAdapter;
import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;
import bd.edu.daffodilvarsity.classmanager.room.EachDayClassViewModel;
import bd.edu.daffodilvarsity.classmanager.room.RoutineClassDetails;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class EachDayRoutine extends Fragment {

    private EachDayClassViewModel mViewModel;

    private String mDayOfWeek;

    private ArrayList<RoutineClassDetails> mClasses = new ArrayList<>();

    private TextView loadingContent;

    private ProgressBar progressBar;

    private RecyclerView recyclerView;

    private ClassListRecyclerViewAdapter mAdapter;

    private SwipeRefreshLayout mPullToRefresh;

    private TextView mNoClasses;

    public EachDayRoutine() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_each_day_routine, container, false);

        initializeVariables(view);

        initializeRecyclerView();

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        mPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getUserType().equals(HelperClass.USER_TYPE_STUDENT)) {
                    showProgressbar(true);
                    loadStudentRoutine();
                } else if (getUserType().equals(HelperClass.USER_TYPE_TEACHER) || getUserType().equals(HelperClass.USER_TYPE_ADMIN)) {
                    showProgressbar(true);
                    loadTeacherRoutine();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefresh.setRefreshing(false);
                    }
                }, 800);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initializeViewModel();

    }

    private void initializeViewModel() {

        mViewModel = ViewModelProviders.of(getActivity()).get(EachDayClassViewModel.class);

        if (getUserType().equals(HelperClass.USER_TYPE_STUDENT)) {

            loadStudentRoutine();

        } else if (getUserType().equals(HelperClass.USER_TYPE_TEACHER) || getUserType().equals(HelperClass.USER_TYPE_ADMIN)) {

            loadTeacherRoutine();

        }
    }

    private void initializeViewModelObserver() {
        mViewModel.getClasses().observe(getViewLifecycleOwner(), new Observer<List<RoutineClassDetails>>() {
            @Override
            public void onChanged(List<RoutineClassDetails> routineClassDetails) {
                mClasses.clear();
                mClasses.addAll(routineClassDetails);
                notifyRecyclerViewAdapter();
                if(mAdapter.getItemCount() == 0)    {
                    mNoClasses.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initializeVariables(View view) {
        mDayOfWeek = getArguments().getString("dayOfWeek");
        progressBar = view.findViewById(R.id.progress_bar);
        loadingContent = view.findViewById(R.id.loading_content);
        recyclerView = view.findViewById(R.id.recycler_view);
        mPullToRefresh = view.findViewById(R.id.swipe_to_refresh);
        mPullToRefresh.setDistanceToTriggerSync(450);
        mNoClasses = view.findViewById(R.id.no_classes);
    }

    private void loadTeacherRoutine() {

        mClasses.clear();
        notifyRecyclerViewAdapter();

        mViewModel.loadClassesTeacher(getTeacherInitialFromSharedPref(), mDayOfWeek);
        initializeViewModelObserver();
        showProgressbar(false);

    }

    private void loadStudentRoutine() {

        List<String> courseCodeList = new ArrayList<>();

        List<String> sectionList = new ArrayList<>();

        HashMap<String, String> courseHashMap = getCoursesFromSharedPreferences();

        for (HashMap.Entry<String, String> entry : courseHashMap.entrySet()) {
            courseCodeList.add(entry.getKey());
            sectionList.add(entry.getValue());
        }

        mViewModel.loadClassesStudent(courseCodeList, getShiftFromSharedPreferences(), sectionList, mDayOfWeek);
        initializeViewModelObserver();
        showProgressbar(false);

    }

    private String getShiftFromSharedPreferences() {

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);

        return sharedPreferences.getString(HelperClass.SHIFT, "");

    }

    private void initializeRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ClassListRecyclerViewAdapter(mClasses);
        recyclerView.setAdapter(mAdapter);
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
        return sharedPreferences.getString(HelperClass.USER_TYPE, "");
    }

    private void notifyRecyclerViewAdapter() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
            recyclerView.scheduleLayoutAnimation();
        }
    }

    private void showProgressbar(boolean b) {
        if (b) {
            progressBar.setVisibility(View.VISIBLE);
            loadingContent.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            loadingContent.setVisibility(View.GONE);
        }
    }

    private String getTeacherInitialFromSharedPref()   {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getString(HelperClass.TEACHER_INITIAL,"");
    }

}
