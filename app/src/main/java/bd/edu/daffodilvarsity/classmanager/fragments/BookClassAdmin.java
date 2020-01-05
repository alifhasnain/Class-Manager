package bd.edu.daffodilvarsity.classmanager.fragments;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.activities.FinishBookingAdmin;
import bd.edu.daffodilvarsity.classmanager.adapters.recyclerViewAdapters.AvailableClassesRecyclerViewAdapter;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ClassDetails;
import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;
import bd.edu.daffodilvarsity.classmanager.viewmodels.BookClassViewModel;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookClassAdmin extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    public static final int REQUEST_CODE_FINISH_BOOK = 7856;

    private BookClassViewModel mViewModel;

    private ArrayList<ClassDetails> mEmptyClasses = new ArrayList<>();

    private TextView loadingContent;

    private ProgressBar progressBar;

    private SwipeRefreshLayout mPullToRefresh;

    private Spinner mTimeSpinner;

    private TextView mPickedTimeText;

    private Button mSearchBtn;

    private RecyclerView mRecyclerView;

    private AvailableClassesRecyclerViewAdapter mAdapter;

    private Calendar mSelectedDate;

    private Calendar mFinalDate;

    private DateFormat mDateFormater = new SimpleDateFormat("EEE, d MMM, yyyy");



    public BookClassAdmin() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_class_admin, container, false);

        initializeVariablesAndOnClickListeners(view);

        initializeSpinners();

        setupPrimaryDate();

        initializeRecyclerView();

        return view;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(BookClassViewModel.class);

        mViewModel.getData().observe(getViewLifecycleOwner(), new Observer<ArrayList<ClassDetails>>() {
            @Override
            public void onChanged(ArrayList<ClassDetails> availableEmptyClassList) {

                mEmptyClasses.clear();
                mEmptyClasses.addAll(availableEmptyClassList);
                mAdapter.notifyDataSetChanged();

                showProgressbar(false);
                mPullToRefresh.setRefreshing(false);
            }
        });

        mViewModel.showToast().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String text) {
                makeToast(text);
                showProgressbar(false);
                mPullToRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        mPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadEmptyClassList();
            }
        });
    }

    private void initializeVariablesAndOnClickListeners(View view) {

        mTimeSpinner = view.findViewById(R.id.time);
        mSearchBtn = view.findViewById(R.id.search);
        mSearchBtn.setOnClickListener(this);

        progressBar = view.findViewById(R.id.progress_bar);
        loadingContent = view.findViewById(R.id.loading_content);

        mPullToRefresh = view.findViewById(R.id.swipe_to_refresh);
        mPullToRefresh.setDistanceToTriggerSync(450);

        mRecyclerView = view.findViewById(R.id.empty_rooms_recyclerview);
        mAdapter = new AvailableClassesRecyclerViewAdapter(mEmptyClasses);

        view.findViewById(R.id.pick_date).setOnClickListener(this);

        mPickedTimeText = view.findViewById(R.id.date_text);

    }

    private void setupPrimaryDate() {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,1);

        int year = calendar.get(Calendar.YEAR);

        int month = calendar.get(Calendar.MONTH);

        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        mSelectedDate = new GregorianCalendar(year,month,dayOfMonth);

        mFinalDate = (Calendar) mSelectedDate.clone();

        mPickedTimeText.setText(mDateFormater.format(mFinalDate.getTime()));

    }

    private void initializeRecyclerView() {

        mRecyclerView.hasFixedSize();

        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setItemClickListener(new AvailableClassesRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(int position) {

                ClassDetails selectedClass = mEmptyClasses.get(position);

                Intent intent = new Intent(getActivity(), FinishBookingAdmin.class);

                Gson gson = new Gson();

                intent.putExtra("data",gson.toJson(selectedClass));

                intent.putExtra("date",gson.toJson(mFinalDate));

                startActivityForResult(intent, REQUEST_CODE_FINISH_BOOK);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_FINISH_BOOK :
                if (resultCode == RESULT_OK) {
                    makeToast("Success");
                    loadEmptyClassList();
                } else {
                    makeToast("Booking was unsuccessful");
                    loadEmptyClassList();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initializeSpinners() {

        //Set Times spinner items

        ArrayList<String> classTimesList = HelperClass.getClassTimes();

        String[] classesTimes = classTimesList.toArray(new String[classTimesList.size()]);

        ArrayAdapter<String> timeSpinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_items, classesTimes);

        timeSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_items);

        mTimeSpinner.setAdapter(timeSpinnerAdapter);

    }

    private void loadEmptyClassList() {

        mEmptyClasses.clear();

        mAdapter.notifyDataSetChanged();

        showProgressbar(true);

        mFinalDate = (Calendar) mSelectedDate.clone();

        String dayOfWeek = getDayOfWeek(mFinalDate);

        mViewModel.loadData(dayOfWeek,mFinalDate, mTimeSpinner.getSelectedItem().toString());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search:
                loadEmptyClassList();
                break;
            case R.id.pick_date:
                pickDate();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        mSelectedDate.set(year,month,dayOfMonth);

        mPickedTimeText.setText(mDateFormater.format(mSelectedDate.getTime()));

    }

    private String getDayOfWeek(Calendar calendar) {

        String day = "";

        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                day = "Sunday";
                break;
            case 2:
                day = "Monday";
                break;
            case 3:
                day = "Tuesday";
                break;
            case 4:
                day = "Wednesday";
                break;
            case 5:
                day = "Thursday";
                break;
            case 6:
                day = "Friday";
                break;
            case 7:
                day = "Saturday";
                break;
        }

        return day;
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

    private void pickDate() {

        long minDate;

        long maxDate;

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DATE,1);

        minDate = calendar.getTimeInMillis();

        //Change date of calender
        calendar.add(Calendar.DATE,6);

        maxDate = calendar.getTimeInMillis();

        showDatePicker(minDate, maxDate);

    }

    private void showDatePicker(long minDate, long maxDate) {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);

        int month = calendar.get(Calendar.MONTH);

        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(getContext(), this, year, month, dayOfMonth);

        datePicker.getDatePicker().setMinDate(minDate);

        datePicker.getDatePicker().setMaxDate(maxDate);

        datePicker.show();
    }

    private void makeToast(String text) {
        if(getContext()!=null)  {
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        }
    }
}
