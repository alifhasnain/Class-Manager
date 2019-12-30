package bd.edu.daffodilvarsity.classmanager.fragments;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.adapters.recyclerViewAdapters.SearchBookedClassesWithRangeRecyclerViewAdapter;
import bd.edu.daffodilvarsity.classmanager.otherclasses.BookedClassDetailsUser;
import bd.edu.daffodilvarsity.classmanager.viewmodels.SearchBookedClassesWithRangeViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchBookedClassesWithRange extends Fragment implements View.OnClickListener {

    private ArrayList<BookedClassDetailsUser> classesList = new ArrayList<>();

    private RecyclerView recyclerView;

    private SearchBookedClassesWithRangeRecyclerViewAdapter adapter;

    private ProgressBar progressBar;

    private TextView loadingContent;

    private TextView startDate;

    private TextView endDate;

    private DateFormat dateFormatter = new SimpleDateFormat("d MMM, yyyy");

    private SearchBookedClassesWithRangeViewModel viewModel;

    private Timestamp startTimeStamp;

    private Timestamp endTimeStamp;

    public SearchBookedClassesWithRange() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_booked_classes_with_range, container, false);

        initializeVariables(view);

        initializeRecyclerView();

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeViewModel();
    }

    private void initializeVariables(View view) {

        view.findViewById(R.id.search).setOnClickListener(this);

        recyclerView = view.findViewById(R.id.recycler_view);

        startDate = view.findViewById(R.id.start_date);
        startDate.setOnClickListener(this);

        endDate = view.findViewById(R.id.end_date);
        endDate.setOnClickListener(this);

        progressBar = view.findViewById(R.id.progress_bar);
        loadingContent = view.findViewById(R.id.loading_content);

    }

    private void initializeViewModel() {

        viewModel = ViewModelProviders.of(this).get(SearchBookedClassesWithRangeViewModel.class);

        viewModel.getClassesLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<BookedClassDetailsUser>>() {
            @Override
            public void onChanged(ArrayList<BookedClassDetailsUser> classes) {
                classesList.addAll(classes);
                adapter.notifyDataSetChanged();
                showProgressbar(false);
            }
        });

    }

    private void initializeRecyclerView() {

        adapter = new SearchBookedClassesWithRangeRecyclerViewAdapter(classesList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    makeToast("loading more data");
                    viewModel.fetchMoreNextData();
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_date:
                pickStartDate();
                break;
            case R.id.end_date:
                pickEndDate();
                break;
            case R.id.search:
                fetchData();
                break;
        }
    }

    private void fetchData() {
        if(checkDateValidity()) {

            classesList.clear();
            adapter.notifyDataSetChanged();

            showProgressbar(true);

            viewModel.fetchData(startTimeStamp,endTimeStamp);

        } else {
            makeToast("Invalid Date Range");
        }
    }

    private boolean checkDateValidity() {
        return startTimeStamp != null && endTimeStamp != null && endTimeStamp.compareTo(startTimeStamp) > 0;
    }

    private void pickStartDate() {

        Calendar calendar = Calendar.getInstance();

        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        int month = calendar.get(Calendar.MONTH);

        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

                Calendar calendar = Calendar.getInstance();

                calendar.set(year, month, dayOfMonth);

                startDate.setText(dateFormatter.format(calendar.getTime()));

                startTimeStamp = new Timestamp(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).getTime());

            }
        }, year, month, dayOfMonth);

        datePicker.show();

    }

    private void pickEndDate() {

        Calendar calendar = Calendar.getInstance();

        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        int month = calendar.get(Calendar.MONTH);

        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                endDate.setText(dateFormatter.format(calendar.getTime()));

                endTimeStamp = new Timestamp(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).getTime());

            }
        }, year, month, dayOfMonth);

        datePicker.show();

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

    private void makeToast(String text) {
        if (getContext() != null) {
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        }
    }
}
