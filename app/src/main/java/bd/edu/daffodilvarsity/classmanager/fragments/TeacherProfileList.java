package bd.edu.daffodilvarsity.classmanager.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.adapters.recyclerViewAdapters.TeacherProfileListRecyclerViewAdapter;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectTeacher;
import bd.edu.daffodilvarsity.classmanager.viewmodels.TeacherProfileListViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherProfileList extends Fragment {

    private TeacherProfileListViewModel mViewModel;

    private ArrayList<ProfileObjectTeacher> teacherList = new ArrayList<>();

    private TeacherProfileListRecyclerViewAdapter mAdapter;

    private SwipeRefreshLayout mPullToRefresh;

    private EditText searchQueryText;

    private RecyclerView mRecyclerView;


    public TeacherProfileList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_profile_list, container, false);

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

                refreshProfileData();

                makeToast("Refreshing...");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefresh.setRefreshing(false);
                    }
                }, 800);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initializeViewModel();
    }

    private void initializeRecyclerView() {

        mAdapter = new TeacherProfileListRecyclerViewAdapter(teacherList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.addOnDeleteClickListener(new TeacherProfileListRecyclerViewAdapter.OnDeleteClickListener() {

            @Override
            public void onDeleteClickListener(String docId, final int position) {

                String dialogString = "Are you sure to delete " + teacherList.get(position).getName() + " (" + teacherList.get(position).getEmail() + ")" + " this profile?";

                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle("Please confirm")
                        .setMessage(dialogString)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mViewModel.deleteProfile(teacherList.get(position));
                                teacherList.remove(position);
                                mAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .create();

                alertDialog.show();
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    if(searchQueryText.getText().toString().equals("")) {
                        mViewModel.loadNextTeacherProfiles();
                    }
                }
            }
        });
    }

    private void refreshProfileData() {
        if(searchQueryText.getText().toString().equals("")) {
            mViewModel.loadTeacherProfiles();
        }
        else {
            mViewModel.loadTeacherProfilesWithSubstring(searchQueryText.getText().toString());
        }
    }

    private void initializeViewModel() {

        mViewModel = ViewModelProviders.of(this).get(TeacherProfileListViewModel.class);

        mViewModel.loadTeacherProfiles();

        mViewModel.getTeacherProfileLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<ProfileObjectTeacher>>() {
            @Override
            public void onChanged(ArrayList<ProfileObjectTeacher> teacherArrayList) {
                teacherList.clear();
                teacherList.addAll(teacherArrayList);
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

    private void initializeVariables(View view) {

        mPullToRefresh = view.findViewById(R.id.pull_to_refresh);
        mRecyclerView = view.findViewById(R.id.teacher_profile_recycler_view);

        searchQueryText = view.findViewById(R.id.search_edit_text);
        searchQueryText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                if(charSequence.toString().equals(""))  {
                    mViewModel.loadTeacherProfiles();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        view.findViewById(R.id.search_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!searchQueryText.getText().toString().equals(""))    {
                    mViewModel.loadTeacherProfilesWithSubstring(searchQueryText.getText().toString());
                }
            }
        });

    }

    private void makeToast(String text) {
        if (getContext() != null) {
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

}
