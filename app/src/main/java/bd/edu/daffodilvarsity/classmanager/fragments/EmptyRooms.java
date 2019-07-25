package bd.edu.daffodilvarsity.classmanager.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.adapters.EmptyRoomsRecyclerViewAdapter;
import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;
import bd.edu.daffodilvarsity.classmanager.viewmodels.EmptyRoomsViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmptyRooms extends Fragment implements View.OnClickListener {

    private EmptyRoomsViewModel mViewModel;

    private ArrayList<String> mEmptyClasses = new ArrayList<>();

    private TextView loadingContent;

    private ProgressBar progressBar;

    private RecyclerView mRecyclerView;

    private SwipeRefreshLayout mPullToRefres;

    private TextView mEmptyIcon;

    private EmptyRoomsRecyclerViewAdapter mAdapter;

    private Spinner daySelector;

    private Spinner timeSelector;

    public EmptyRooms() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_empty_rooms, container, false);

        initializeVariables(view);
        
        initializeSpinners();

        initializeRecyclerView();

        return view;
    }

    private void initializeRecyclerView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.hasFixedSize();
    }

    private void initializeSpinners() {

        String[] times = new String[HelperClass.getClassTimes().size()];

        times = HelperClass.getClassTimes().toArray(times);

        ArrayAdapter<String> timesAdapter = new ArrayAdapter<>(getContext(),R.layout.spinner_items,times);

        timesAdapter.setDropDownViewResource(R.layout.spinner_dropdown_items);

        timeSelector.setAdapter(timesAdapter);

        String[] days = new String[HelperClass.getSevenDaysOfWeek().size()];

        days = HelperClass.getSevenDaysOfWeek().toArray(days);

        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(getContext(),R.layout.spinner_items,days);

        dayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_items);

        daySelector.setAdapter(dayAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        mPullToRefres.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadEmptyRooms();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Initialize ViewModel
        mViewModel = ViewModelProviders.of(this).get(EmptyRoomsViewModel.class);

        mViewModel.getEmptyClasses().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> emptyClasses) {

                mEmptyClasses.clear();
                mEmptyClasses.addAll(emptyClasses);
                mAdapter.notifyDataSetChanged();

                showProgressbar(false);
                mPullToRefres.setRefreshing(false);
                toggleEmptyList(true);

            }
        });

        mViewModel.showToast().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                makeToast(s);
                toggleEmptyList(true);
                mPullToRefres.setRefreshing(false);
            }
        });
    }

    private void initializeVariables(View view) {

        mRecyclerView = view.findViewById(R.id.empty_rooms_recyclerview);
        mAdapter = new EmptyRoomsRecyclerViewAdapter(mEmptyClasses);

        progressBar = view.findViewById(R.id.progress_bar);
        loadingContent = view.findViewById(R.id.loading_content);

        mPullToRefres = view.findViewById(R.id.swipe_to_refresh);

        mEmptyIcon = view.findViewById(R.id.empty_text_view);

        timeSelector = view.findViewById(R.id.time_selector);

        daySelector = view.findViewById(R.id.day_selector);

        view.findViewById(R.id.btn_search).setOnClickListener(this);

    }

    private void loadEmptyRooms() {

        mEmptyClasses.clear();
        mAdapter.notifyDataSetChanged();
        toggleEmptyList(false);

        String day = daySelector.getSelectedItem().toString();
        String time = timeSelector.getSelectedItem().toString();

        showProgressbar(true);
        mViewModel.loadEmptyClasses(day,time);

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
            if (mEmptyClasses.isEmpty()) {
                mEmptyIcon.setVisibility(View.VISIBLE);
            } else {
                mEmptyIcon.setVisibility(View.GONE);
            }
        }
        else {
            mEmptyIcon.setVisibility(View.GONE);
        }
    }

    private void makeToast(String msg) {
        if (getContext() != null) {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                loadEmptyRooms();
                break;
        }
    }
}
