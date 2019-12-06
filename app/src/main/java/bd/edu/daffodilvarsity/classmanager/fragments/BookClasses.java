package bd.edu.daffodilvarsity.classmanager.fragments;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.adapters.AvailableClassesRecyclerViewAdapter;
import bd.edu.daffodilvarsity.classmanager.dialogs.BookClassDialog;
import bd.edu.daffodilvarsity.classmanager.otherclasses.BookedClassDetailsServer;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ClassDetails;
import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectTeacher;
import bd.edu.daffodilvarsity.classmanager.otherclasses.SharedPreferencesHelper;
import bd.edu.daffodilvarsity.classmanager.viewmodels.BookClassViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookClasses extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private static final String TAG = "BookClasses";

    private List<String> teacherCourses = new ArrayList<>();

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



    public BookClasses() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_classes, container, false);

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

        ProfileObjectTeacher teacherProfile = SharedPreferencesHelper.getTeacherOfflineProfile(getActivity());

        if(teacherProfile!=null)    {
            mViewModel.loadTeacherCourses(teacherProfile.getTeacherInitial());
        }   else    {
            makeToast("Loading profile from database.");
        }


        mViewModel.getTeacherCourses().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                teacherCourses.clear();
                teacherCourses.addAll(strings);
                teacherCourses.add(0,"Undefined");
            }
        });

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

        updateTeacherProfileFromServer();

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

    private void updateTeacherProfileFromServer()   {

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return;
        }

        DocumentReference profile = FirebaseFirestore.getInstance().document("teacher_profiles/"+FirebaseAuth.getInstance().getCurrentUser().getEmail());

        profile.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())   {
                    ProfileObjectTeacher profile = documentSnapshot.toObject(ProfileObjectTeacher.class);
                    SharedPreferencesHelper.saveTeacherProfileToSharedPref(getActivity(),profile);

                }   else {
                    makeToast("Profile doesn't exist in teacher list.\nContact admin.");
                }
            }
        });

    }

    private void setupPrimaryDate() {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,1);

        int year = calendar.get(Calendar.YEAR);

        int month = calendar.get(Calendar.MONTH);

        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        mSelectedDate = new GregorianCalendar(year,month,dayOfMonth);

        mFinalDate = mSelectedDate;

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

                String teacherInitial = SharedPreferencesHelper.getTeacherOfflineProfile(getContext()).getTeacherInitial();

                finishBooking(selectedClass,teacherInitial);

            }
        });

    }

    private void finishBooking(ClassDetails selectedClass, String initial) {

        final BookedClassDetailsServer bcdServer = new BookedClassDetailsServer();

        bcdServer.setRoomNo(selectedClass.getRoom());
        bcdServer.setTime(selectedClass.getTime());
        bcdServer.setTeacherInitial(initial);
        int year = mFinalDate.get(Calendar.YEAR);
        int month = mFinalDate.get(Calendar.MONTH);
        int dayOfMonth = mFinalDate.get(Calendar.DAY_OF_MONTH);
        bcdServer.setReservationDate(new int[]{year, month, dayOfMonth});
        bcdServer.setPriority(selectedClass.getPriority());

        BookClassDialog bookClassDialog = new BookClassDialog(mDateFormater.format(mFinalDate.getTime()),bcdServer.getRoomNo(),bcdServer.getTime(),teacherCourses);

        bookClassDialog.show(getChildFragmentManager(),"custom_dialog");

        bookClassDialog.addReturnTextListener(new BookClassDialog.CustomDialogListener() {
            @Override
            public void returnTexts(String shift, String program, String section, String courseCode) {

                makeToast("Please wait while processing");

                bcdServer.setShift(shift);
                bcdServer.setSection(section);
                bcdServer.setProgram(program);
                bcdServer.setCourseCode(courseCode);

                Gson gson = new Gson();

                String jsonString = gson.toJson(bcdServer);

                FirebaseFunctions.getInstance().getHttpsCallable("bookRoom")
                        .call(jsonString)
                        .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                            @Override
                            public void onSuccess(final HttpsCallableResult httpsCallableResult) {
                                try {
                                    makeToast(httpsCallableResult.getData().toString());
                                }
                                catch (Exception e) {
                                    Log.e("Error",e.getMessage());
                                }
                                loadEmptyClassList();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                makeToast("Failed!Please try again.");
                                loadEmptyClassList();
                            }
                        });

            }
        });

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

        mFinalDate = mSelectedDate;

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

        DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM, yyyy");

        mPickedTimeText.setText(dateFormat.format(mSelectedDate.getTime()));
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
