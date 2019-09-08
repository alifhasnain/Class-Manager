package bd.edu.daffodilvarsity.classmanager.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.adapters.BookedClassesRecyclerViewAdapter;
import bd.edu.daffodilvarsity.classmanager.otherclasses.BookedClassDetailsUser;
import bd.edu.daffodilvarsity.classmanager.viewmodels.BookedClassesViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookedClasses extends Fragment {

    private BookedClassesViewModel mViewModel;

    private ArrayList<BookedClassDetailsUser> mBookedClasses = new ArrayList<>();

    private ProgressBar progressBar;

    private TextView loadingContents;

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

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        mPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBookedClasses();
                mEmptyTextView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Initialize ViewModel and load booked classes
        mViewModel = ViewModelProviders.of(this).get(BookedClassesViewModel.class);

        mViewModel.getBookedClassList().observe(getViewLifecycleOwner(), new Observer<ArrayList<BookedClassDetailsUser>>() {
            @Override
            public void onChanged(ArrayList<BookedClassDetailsUser> bookedClassDetailUsers) {

                mBookedClasses.clear();
                mBookedClasses.addAll(bookedClassDetailUsers);

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

        loadBookedClasses();

    }

    private void initializeVariablesAndListeners(View view) {

        progressBar = view.findViewById(R.id.progress_bar);

        loadingContents = view.findViewById(R.id.loading_content);

        mRecyclerView = view.findViewById(R.id.recycler_view);

        mPullToRefresh = view.findViewById(R.id.pull_to_refresh);
        mPullToRefresh.setDistanceToTriggerSync(450);

        mEmptyTextView = view.findViewById(R.id.empty_text_view);

    }

    private void initializeRecyclerView()   {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new BookedClassesRecyclerViewAdapter(mBookedClasses);

        mAdapter.addOnBookCancelListener(new BookedClassesRecyclerViewAdapter.OnBookCancelListener() {
            @Override
            public void onBookCancel(final BookedClassDetailsUser bcd) {
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Are you sure to cancel your booking?")
                        .setMessage("This can' be undone")
                        .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cancelBook(bcd);
                            }
                        })
                        .setNegativeButton("Cancel",null).create();
                dialog.show();
            }
        });

        mRecyclerView.setAdapter(mAdapter);

    }

    private void cancelBook(final BookedClassDetailsUser bcd) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference bookCount = db.document("book_room_count/"+ FirebaseAuth.getInstance().getCurrentUser().getEmail());
                DocumentReference bookedClass = db.document("booked_classes/" + bcd.getDocId());

                DocumentSnapshot bookCountSnapshot = transaction.get(bookCount);
                double bookCountDouble = bookCountSnapshot.getDouble("counter")+1;

                if(bookCountDouble>=0)  {
                    transaction.update(bookCount,"counter",bookCountDouble);
                }

                transaction.delete(bookedClass);

                return null;
            }
        })
        .addOnSuccessListener(new OnSuccessListener<Object>() {
            @Override
            public void onSuccess(Object o) {
                makeToast("Successfully canceled");
                loadBookedClasses();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("ERROR",e.toString());
                makeToast("Failed to cancel.");
            }
        });
    }

    private void loadBookedClasses()    {

        mBookedClasses.clear();
        showProgressbar(true);
        mAdapter.notifyDataSetChanged();

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
            mEmptyTextView.setVisibility(View.VISIBLE);
        }
        else if(mAdapter.getItemCount()>0) {
            mEmptyTextView.setVisibility(View.GONE);
        }
    }

    private void makeToast(String text) {
        if(getContext()!=null)  {
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

}
