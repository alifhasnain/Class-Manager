package bd.edu.daffodilvarsity.classmanager.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import bd.edu.daffodilvarsity.classmanager.adapters.ExtraClassesStudentRecyclerViewAdapter;
import bd.edu.daffodilvarsity.classmanager.otherclasses.BookedClassDetailsUser;
import bd.edu.daffodilvarsity.classmanager.otherclasses.SharedPreferencesHelper;
import bd.edu.daffodilvarsity.classmanager.viewmodels.ExtraClassesStudentViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExtraClassesStudent extends Fragment {

    private ArrayList<BookedClassDetailsUser> mExtraClasses = new ArrayList<>();

    private SharedPreferencesHelper mSharedPrefHelper;

    private ExtraClassesStudentViewModel mViewModel;

    private RecyclerView mRecyclerView;

    private ExtraClassesStudentRecyclerViewAdapter mAdapter;

    private SwipeRefreshLayout mPullToRefresh;

    public ExtraClassesStudent() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_extra_classes_student, container, false);

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
                mViewModel.loadData(mSharedPrefHelper.getCoursesAndSectionMapFromSharedPreferences(getActivity()));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefresh.setRefreshing(false);
                    }
                },2000);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(ExtraClassesStudentViewModel.class);

        mViewModel.loadData(mSharedPrefHelper.getCoursesAndSectionMapFromSharedPreferences(getActivity()));

        mViewModel.getExtraClassesList().observe(getViewLifecycleOwner(), new Observer<ArrayList<BookedClassDetailsUser>>() {
            @Override
            public void onChanged(ArrayList<BookedClassDetailsUser> bookedClassDetailUsers) {
                mExtraClasses.clear();
                mExtraClasses.addAll(bookedClassDetailUsers);
                mAdapter.notifyDataSetChanged();
            }
        });

        mViewModel.getToastMsg().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String msg) {
                makeToast(msg);
            }
        });
    }

    private void initializeRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ExtraClassesStudentRecyclerViewAdapter(mExtraClasses);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initializeVariables(View view) {

        mSharedPrefHelper = new SharedPreferencesHelper();

        mRecyclerView = view.findViewById(R.id.extra_classes_recycler_view);
        mPullToRefresh = view.findViewById(R.id.pull_to_refresh);

    }

    private void makeToast(String msg)  {
        if(getContext()!=null)  {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }


}
