package bd.edu.daffodilvarsity.classmanager.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.adapters.ClassListRecyclerViewAdapter;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ClassDetails;

/**
 * A simple {@link Fragment} subclass.
 */
public class TuesdayCLasses extends Fragment {


    private ArrayList<ClassDetails> mClasses = new ArrayList<>();

    private TextView loadingContent;

    private ProgressBar progressBar;

    private FirebaseFirestore db;

    private RecyclerView recyclerView;

    private ClassListRecyclerViewAdapter adapter;

    private SwipeRefreshLayout mPullToRefresh;

    public TuesdayCLasses() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_each_day_classes, container, false);

        initializeVariables(view);

        loadData();

        initializeRecyclerView();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
    }

    private void initializeVariables(View view) {
        db = FirebaseFirestore.getInstance();
        progressBar = view.findViewById(R.id.progress_bar);
        loadingContent = view.findViewById(R.id.loading_content);
        recyclerView = view.findViewById(R.id.recycler_view);
        mPullToRefresh = view.findViewById(R.id.swipe_to_refresh);
        mPullToRefresh.setDistanceToTriggerSync(450);
    }

    private void initializeRecyclerView()   {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClassListRecyclerViewAdapter(getActivity(), mClasses);
        recyclerView.setAdapter(adapter);
    }

    private void loadData() {

        showProgressbar(true);

        CollectionReference mTuesdayRef = db.collection("main_campus/classes_day/tuesday");

        mClasses.clear();
        notifyRecyclerViewAdapter();

        mTuesdayRef.whereEqualTo("teacherInitial", "SI")
                .orderBy("priority")
                .get()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot ds : queryDocumentSnapshots) {
                            mClasses.add(ds.toObject(ClassDetails.class));
                        }
                        showProgressbar(false);
                        notifyRecyclerViewAdapter();
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

    private void notifyRecyclerViewAdapter()    {
        try {
            adapter.notifyDataSetChanged();
        } catch (NullPointerException e) {
            e.printStackTrace();
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

}
