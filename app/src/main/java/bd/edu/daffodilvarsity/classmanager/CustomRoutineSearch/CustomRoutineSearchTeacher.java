package bd.edu.daffodilvarsity.classmanager.CustomRoutineSearch;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;
import bd.edu.daffodilvarsity.classmanager.routine.RoutineClassDetails;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomRoutineSearchTeacher extends Fragment implements View.OnClickListener {

    private CustomRoutineSearchViewModel mViewModel;

    private ArrayList<RoutineClassDetails> mClassesList = new ArrayList<>();

    private RecyclerView mRecyclerView;

    private CustomRoutineTeacherRecyclerViewAdapter mAdapter;

    private Spinner daySelector;

    private EditText teacherInitialEditText;

    private TextView loadingContent;

    private ProgressBar progressBar;

    private SwipeRefreshLayout mPullToRefresh;

    private TextView mEmptyIcon;

    public CustomRoutineSearchTeacher() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_custom_routine_search, container, false);

        initializeVariables(view);

        initializeSpinner();

        initializeRecyclerView();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadClasses();
            }
        });
    }

    private void initializeVariables(View view) {

        teacherInitialEditText = view.findViewById(R.id.teacher_initial);
        daySelector = view.findViewById(R.id.day_selector);

        view.findViewById(R.id.search).setOnClickListener(this);

        mRecyclerView = view.findViewById(R.id.custom_routine_recycler_view);

        loadingContent = view.findViewById(R.id.loading_content);
        progressBar = view.findViewById(R.id.progress_bar);

        mPullToRefresh = view.findViewById(R.id.pull_to_refresh);

        mEmptyIcon = view.findViewById(R.id.empty_text_view);

    }

    private void initializeSpinner() {

        ArrayList<String> allDaysOfWeek = HelperClass.getSevenDaysOfWeek();
        allDaysOfWeek.add(0,"All");

        String[] items = allDaysOfWeek.toArray(new String[allDaysOfWeek.size()]);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_items, items);

        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_items);

        daySelector.setAdapter(spinnerAdapter);

    }

    private void initializeRecyclerView() {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new CustomRoutineTeacherRecyclerViewAdapter(mClassesList);

        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(CustomRoutineSearchViewModel.class);

        mViewModel.getTeacherClasses().observe(getViewLifecycleOwner(), new Observer<ArrayList<RoutineClassDetails>>() {
            @Override
            public void onChanged(ArrayList<RoutineClassDetails> classList) {

                mClassesList.clear();
                mClassesList.addAll(classList);

                mAdapter.notifyDataSetChanged();

                showProgressbar(false);
                toggleEmptyList(true);
                mPullToRefresh.setRefreshing(false);

            }
        });

        mViewModel.showToast().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String msg) {
                makeToast(msg);
                toggleEmptyList(false);
                showProgressbar(false);
                mPullToRefresh.setRefreshing(false);
            }
        });
    }

    private void loadClasses() {

        /*if(teacherInitialEditText.getText().toString().isEmpty())   {
            makeToast("Please insert a teacher initial");
            return;
        }*/

        mClassesList.clear();
        mAdapter.notifyDataSetChanged();
        toggleEmptyList(false);
        showProgressbar(true);
        closeKeyboard();


        String teacherInitial = teacherInitialEditText.getText().toString().trim().toUpperCase();

        String dayOfWeek = daySelector.getSelectedItem().toString().trim();

        mViewModel.loadTeacherClasses(teacherInitial, dayOfWeek);

    }

    private void makeToast(String msg) {
        if (getContext() != null) {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void showProgressbar(boolean visible) {
        if (visible) {
            progressBar.setVisibility(View.VISIBLE);
            loadingContent.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            loadingContent.setVisibility(View.GONE);
        }
    }

    private void toggleEmptyList(boolean check) {
        if(check)   {
            if (mClassesList.isEmpty()) {
                mEmptyIcon.setVisibility(View.VISIBLE);
            } else {
                mEmptyIcon.setVisibility(View.GONE);
            }
        }
        else {
            mEmptyIcon.setVisibility(View.GONE);
        }
    }

    private void closeKeyboard() {
        try {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search:
                loadClasses();
                break;
        }
    }
}
