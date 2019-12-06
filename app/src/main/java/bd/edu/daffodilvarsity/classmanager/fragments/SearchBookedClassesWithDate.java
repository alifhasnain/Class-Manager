package bd.edu.daffodilvarsity.classmanager.fragments;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.Timestamp;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.activities.CancelBookByAdmin;
import bd.edu.daffodilvarsity.classmanager.adapters.SearchBookedClassesWithDateRecyclerViewAdapter;
import bd.edu.daffodilvarsity.classmanager.otherclasses.BookedClassDetailsUser;
import bd.edu.daffodilvarsity.classmanager.viewmodels.SearchBookedClassesWithDateViewModel;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchBookedClassesWithDate extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private final int REQUEST_CODE_CANCEL_BOOKING = 6544;

    private ArrayList<BookedClassDetailsUser> mBookedClassesList = new ArrayList<>();

    private TextView mDateTextViw;

    private Calendar mSelectedDate;

    private Calendar mFinalDate;

    private DateFormat mDateFormatter = new SimpleDateFormat("EEE, d MMM, yyyy");

    private RecyclerView mRecyclerView;

    private SearchBookedClassesWithDateRecyclerViewAdapter mAdapter;

    private SearchBookedClassesWithDateViewModel mViewModel;

    private TextView mEmptyIcon;

    private TextView loadingContent;

    private ProgressBar progressBar;

    private SwipeRefreshLayout mPullToRefresh;

    public SearchBookedClassesWithDate() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_booked_classes_with_date, container, false);

        initializeVariables(view);

        setupPrimaryDate();

        initializeRecyclerView();

        return  view;
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(SearchBookedClassesWithDateViewModel.class);

        mViewModel.getBookedClasses().observe(getViewLifecycleOwner(), new Observer<ArrayList<BookedClassDetailsUser>>() {
            @Override
            public void onChanged(ArrayList<BookedClassDetailsUser> bookedClassList) {

                mBookedClassesList.clear();

                mBookedClassesList.addAll(bookedClassList);

                mAdapter.notifyDataSetChanged();

                toggleEmptyList(true);
                showProgressbar(false);
                mPullToRefresh.setRefreshing(false);

            }
        });

        mViewModel.getToastMsg().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String msg) {
                makeToast(msg);
                toggleEmptyList(false);
                showProgressbar(false);
                mPullToRefresh.setRefreshing(false);
            }
        });
    }

    private void initializeVariables(View view) {

        mDateTextViw = view.findViewById(R.id.date);
        mDateTextViw.setOnClickListener(this);

        view.findViewById(R.id.search).setOnClickListener(this);

        mRecyclerView = view.findViewById(R.id.recycler_view);

        progressBar = view.findViewById(R.id.progress_bar);
        loadingContent = view.findViewById(R.id.loading_content);

        mPullToRefresh = view.findViewById(R.id.pull_to_refresh);
        mPullToRefresh.setDistanceToTriggerSync(450);

        mEmptyIcon = view.findViewById(R.id.empty_text_view);

    }

    private void initializeRecyclerView() {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new SearchBookedClassesWithDateRecyclerViewAdapter(mBookedClassesList);

        mAdapter.setOnDeleteClickListener(new SearchBookedClassesWithDateRecyclerViewAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClicked(int position) {

                long currentTime = System.currentTimeMillis()/1000;

                if (currentTime >= mBookedClassesList.get(position).getReservationDate().getSeconds()) {
                    makeToast("This day has already past.So book cancellation is not possible.");
                    return;
                }

                Gson gson = new Gson();

                String jsonString = gson.toJson(mBookedClassesList.get(position));

                Intent intent = new Intent(getActivity(), CancelBookByAdmin.class);

                intent.putExtra("selectedClass",jsonString);

                startActivityForResult(intent,REQUEST_CODE_CANCEL_BOOKING);

            }
        });

        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode)
        {
            case REQUEST_CODE_CANCEL_BOOKING:

                if(resultCode==RESULT_OK)   {
                    makeToast("Success!");
                    loadClasses();
                }
                else if(resultCode==RESULT_CANCELED)    {
                    makeToast("Nothing was done.");
                    loadClasses();
                }
                else {
                    makeToast("Some error occurred.");
                }
                break;
        }
    }

    private void loadClasses()  {

        mBookedClassesList.clear();
        mAdapter.notifyDataSetChanged();
        showProgressbar(true);
        toggleEmptyList(false);

        mFinalDate = mSelectedDate;

        Timestamp selectedTime = new Timestamp(mFinalDate.getTime());

        mViewModel.loadBookedClasses(selectedTime);

    }

    private void setupPrimaryDate() {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);

        int month = calendar.get(Calendar.MONTH);

        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        mSelectedDate = new GregorianCalendar(year,month,dayOfMonth);

        mFinalDate = mSelectedDate;

        mDateTextViw.setText(mDateFormatter.format(mFinalDate.getTime()));

    }

    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);

        int month = calendar.get(Calendar.MONTH);

        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(getContext(), this, year, month, dayOfMonth);

        datePicker.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.search:
                loadClasses();
                break;

            case R.id.date:
                showDatePicker();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        mSelectedDate.set(year,month,dayOfMonth);

        DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM, yyyy");

        mDateTextViw.setText(dateFormat.format(mSelectedDate.getTime()));
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
            if (mBookedClassesList.isEmpty()) {
                mEmptyIcon.setVisibility(View.VISIBLE);
            } else {
                mEmptyIcon.setVisibility(View.GONE);
            }
        }
        else {
            mEmptyIcon.setVisibility(View.GONE);
        }
    }

    private void makeToast(String text) {
        if(getContext()!=null)  {
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        }
    }
}
