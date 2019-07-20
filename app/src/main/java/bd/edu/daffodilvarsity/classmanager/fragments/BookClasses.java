package bd.edu.daffodilvarsity.classmanager.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.adapters.AvailableClassesRecyclerViewAdapter;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ClassDetails;
import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookClasses extends Fragment implements View.OnClickListener{

    private static final String TAG = "BookClasses";

    private FirebaseFirestore db;

    private ArrayList<ClassDetails> mEmptyClasses = new ArrayList<>();

    private TextView loadingContent;

    private ProgressBar progressBar;

    SwipeRefreshLayout mPullToRefresh;

    private Spinner mDate;

    private Spinner mTime;

    private Button mSearch;

    private RecyclerView mRecyclerView;

    private AvailableClassesRecyclerViewAdapter mAdapter;

    private String mSelectedDateSrt;


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

        mSelectedDateSrt = mDate.getSelectedItem().toString();

        initializeRecyclerView();

        return view;
    }

    private void initializeVariablesAndOnClickListeners(View view) {
        mDate = view.findViewById(R.id.dates);
        mTime = view.findViewById(R.id.time);
        mSearch = view.findViewById(R.id.search);
        mSearch.setOnClickListener(this);

        progressBar = view.findViewById(R.id.progress_bar);
        loadingContent = view.findViewById(R.id.loading_content);

        mPullToRefresh = view.findViewById(R.id.swipe_to_refresh);
        mPullToRefresh.setDistanceToTriggerSync(450);

        mRecyclerView = view.findViewById(R.id.empty_rooms_recyclerview);
        mAdapter = new AvailableClassesRecyclerViewAdapter(mEmptyClasses);

        db = FirebaseFirestore.getInstance();
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

    private void initializeRecyclerView()   {

        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setItemClickListener(new AvailableClassesRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                makeToast(mEmptyClasses.get(position).getRoom());
            }
        });

    }

    private void initializeSpinners()   {

        String[] dates = new String[7];

        Calendar calendar = Calendar.getInstance();

        for(int i=0 ; i < 7 ; i ++) {

            Date date = calendar.getTime();

            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

            String dateStr = dateFormatter.format(date);

            dates[i] = dateStr;

            calendar.add(Calendar.DAY_OF_YEAR,1);

        }

        ArrayAdapter<String> dateSpinnerAdapter = new ArrayAdapter<>(getContext(),R.layout.spinner_items,dates);

        dateSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_items);

        mDate.setAdapter(dateSpinnerAdapter);


        //Set Times spinner items

        ArrayList<String> classTimesList = HelperClass.getClassTimes();

        String[] classesTimes = classTimesList.toArray(new String[classTimesList.size()]);

        ArrayAdapter<String> timeSpinnerAdapter = new ArrayAdapter<>(getContext(),R.layout.spinner_items,classesTimes);

        timeSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_items);

        mTime.setAdapter(timeSpinnerAdapter);

    }

    private void loadEmptyClassList() {

        mSelectedDateSrt = mDate.getSelectedItem().toString();

        //Clear empty room list and show progressbar and disable search btn
        mEmptyClasses.clear();
        mAdapter.notifyDataSetChanged();
        showProgressbar(true);
        mSearch.setEnabled(false);

        String dayOfWeek = getDayOfWeek();

        String timeStr = mTime.getSelectedItem().toString();

        CollectionReference collectionDay = db.collection("/main_campus/classes_day/"+dayOfWeek);
        CollectionReference collectionEvening = db.collection("/main_campus/classes_evening/"+dayOfWeek);

        Task<QuerySnapshot> task1 = collectionDay.whereEqualTo("time",timeStr).whereEqualTo("courseCode","").get();
        Task<QuerySnapshot> task2 = collectionEvening.whereEqualTo("time",timeStr).whereEqualTo("courseCode","").get();

        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1,task2);

        allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                for(QuerySnapshot qs : querySnapshots)  {
                    for(DocumentSnapshot ds : qs)   {
                        mEmptyClasses.add(ds.toObject(ClassDetails.class));
                    }
                }
                mAdapter.notifyDataSetChanged();
                showProgressbar(false);
                mSearch.setEnabled(true);
                mPullToRefresh.setRefreshing(false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                makeToast("Failed to load data.");
                showProgressbar(false);
                mSearch.setEnabled(true);
                mPullToRefresh.setRefreshing(false);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.search:
                loadEmptyClassList();
                break;
        }
    }

    private String getDayOfWeek()   {

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Dhaka"));

        SimpleDateFormat formattedDate = new SimpleDateFormat("dd-MM-yyyy");

        try {
            Date date = formattedDate.parse(mSelectedDateSrt);
            calendar.setTime(date);
        } catch (ParseException e) {
            Log.e(TAG,"Error : ",e);
        }

        String day = "";

        switch (calendar.get(Calendar.DAY_OF_WEEK))
        {
            case 1:
                day = "sunday";
                break;
            case 2:
                day = "monday";
                break;
            case 3:
                day = "tuesday";
                break;
            case 4:
                day = "wednesday";
                break;
            case 5:
                day = "thursday";
                break;
            case 6:
                day = "friday";
                break;
            case 7:
                day = "saturday";
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

    private void makeToast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }
}
