package bd.edu.daffodilvarsity.classmanager.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.adapters.BookedClassesRecyclerViewAdapter;
import bd.edu.daffodilvarsity.classmanager.otherclasses.BookedClassDetails;
import bd.edu.daffodilvarsity.classmanager.viewmodels.BookedClassesViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookedClasses extends Fragment {

    private BookedClassesViewModel mViewModel;

    private ArrayList<BookedClassDetails> mBookedClasses = new ArrayList<>();

    private ProgressBar progressBar;

    private TextView loadingContents;

    private ImageView mEmptyImageView;

    private TextView mEmptyTextView;

    private RecyclerView mRecyclerView;

    private BookedClassesRecyclerViewAdapter mAdapter;

    private SwipeRefreshLayout mPullToRefresh;


    public BookedClasses() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_booked_classes, container, false);

        initializeVariablesAndListeners(view);

        initializeRecyclerView();

        loadBookedClasses();

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        mPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBookedClasses();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(BookedClassesViewModel.class);

        mViewModel.getBookedClassList().observe(getViewLifecycleOwner(), new Observer<ArrayList<BookedClassDetails>>() {
            @Override
            public void onChanged(ArrayList<BookedClassDetails> bookedClassDetails) {

                mBookedClasses.clear();
                mBookedClasses.addAll(bookedClassDetails);

                mAdapter.notifyDataSetChanged();

                showProgressbar(false);
                mPullToRefresh.setRefreshing(false);
                toggleRecyclerEmptyState();
            }
        });

        mViewModel.displayToast().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                makeToast(s);
                showProgressbar(false);
                mPullToRefresh.setRefreshing(false);
                toggleRecyclerEmptyState();
            }
        });
    }

    private void initializeVariablesAndListeners(View view) {

        progressBar = view.findViewById(R.id.progress_bar);

        loadingContents = view.findViewById(R.id.loading_content);

        mRecyclerView = view.findViewById(R.id.recycler_view);

        mPullToRefresh = view.findViewById(R.id.pull_to_refresh);
        mPullToRefresh.setDistanceToTriggerSync(450);

        mEmptyImageView = view.findViewById(R.id.empty_image);
        mEmptyTextView = view.findViewById(R.id.empty_text_view);

    }

    private void initializeRecyclerView()   {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new BookedClassesRecyclerViewAdapter(mBookedClasses);

        mRecyclerView.setAdapter(mAdapter);

    }

    private void loadBookedClasses()    {

        mBookedClasses.clear();
        showProgressbar(true);

        mViewModel.loadDataFromServer();
    }

    private void showProgressbar(boolean visible) {
        if (visible) {
            progressBar.setVisibility(View.VISIBLE);
            loadingContents.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            loadingContents.setVisibility(View.GONE);
        }
    }

    private void toggleRecyclerEmptyState()   {

        if(mAdapter.getItemCount()==0)   {
            mEmptyImageView.setVisibility(View.VISIBLE);
            mEmptyTextView.setVisibility(View.VISIBLE);
        }
        else if(mAdapter.getItemCount()>0) {
            mEmptyImageView.setVisibility(View.GONE);
            mEmptyTextView.setVisibility(View.GONE);
        }
    }

    private void makeToast(String text) {
        if(getContext()!=null)  {
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

}
