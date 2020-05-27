package bd.edu.daffodilvarsity.classmanager.CustomRoutineSearch;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;
import bd.edu.daffodilvarsity.classmanager.routine.RoutineClassDetails;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomRoutineSearchStudent extends Fragment implements View.OnClickListener {

    private Spinner level;
    private Spinner term;
    private Spinner section;
    private Spinner shift;

    private TextView noClasses;

    private RecyclerView mRecyclerView;

    private CustomRoutineSearchViewModel mViewModel;

    private CustomRoutineStudentRecyclerViewAdapter mAdapter;

    public CustomRoutineSearchStudent() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_custom_routine_search_student, container, false);

        initializeVariables(view);

        initializeSpinners();

        initializeRecyclerView(view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(CustomRoutineSearchViewModel.class);

        mViewModel.getStudentClasses().observe(getViewLifecycleOwner(), new Observer<ArrayList<RoutineClassDetails>>() {
            @Override
            public void onChanged(ArrayList<RoutineClassDetails> classesList) {
                mAdapter.setData(classesList);
                if (mAdapter.getItemCount() > 0) {
                    noClasses.setVisibility(View.GONE);
                    mRecyclerView.smoothScrollToPosition(0);
                } else {
                    noClasses.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void initializeVariables(View view) {

        mRecyclerView = view.findViewById(R.id.recycler_view);

        mAdapter = new CustomRoutineStudentRecyclerViewAdapter();

        noClasses = view.findViewById(R.id.no_classes);

        level = view.findViewById(R.id.level);
        term = view.findViewById(R.id.term);
        section = view.findViewById(R.id.section_spinner);
        shift = view.findViewById(R.id.shift);

        view.findViewById(R.id.search).setOnClickListener(this);
    }

    private void initializeRecyclerView(View view) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadStudentRoutine() {
        mViewModel.loadStudentClasses(
                level.getSelectedItem().toString(),
                term.getSelectedItem().toString(),
                shift.getSelectedItem().toString(),
                section.getSelectedItem().toString()
                );
    }

    private void initializeSpinners() {

        String[] level = new String[]{"Level 1", "Level 2", "Level 3", "Level 4"};

        String[] term = new String[]{"Term 1", "Term 2", "Term 3"};

        String[] section = HelperClass.getAllSections();

        final String[] shift = new String[] {HelperClass.SHIFT_DAY , HelperClass.SHIFT_EVENING};

        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_items, level);

        ArrayAdapter<String> termAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_items, term);

        ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_items, section);

        ArrayAdapter<String> shiftAdapter = new ArrayAdapter<>(getContext() , R.layout.spinner_items ,shift);

        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sectionAdapter.setDropDownViewResource(R.layout.spinner_dropdown_items);

        shiftAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.level.setAdapter(levelAdapter);
        this.term.setAdapter(termAdapter);
        this.section.setAdapter(sectionAdapter);
        this.shift.setAdapter(shiftAdapter);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search:
                loadStudentRoutine();
                break;
        }
    }
}
