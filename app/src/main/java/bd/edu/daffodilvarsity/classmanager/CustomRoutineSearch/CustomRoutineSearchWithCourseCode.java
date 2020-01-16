package bd.edu.daffodilvarsity.classmanager.CustomRoutineSearch;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import java.util.LinkedHashMap;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;
import bd.edu.daffodilvarsity.classmanager.routine.RoutineClassDetails;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomRoutineSearchWithCourseCode extends Fragment implements View.OnClickListener {


    private Spinner shiftSpinner;

    private Spinner courseCodeSpinner;

    private TextView noClasses;

    private RecyclerView mRecyclerView;

    private CustomRoutineSearchViewModel mViewModel;

    private CustomRoutineSearchWithCourseCodeRecyclerViewAdapter mAdapter;

    public CustomRoutineSearchWithCourseCode() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_custom_routine_search_with_course_code, container, false);

        initializeVariables(view);

        initializeSpinners();

        initializeRecyclerView();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(CustomRoutineSearchViewModel.class);

        mViewModel.getClassesWithCourseCode().observe(getViewLifecycleOwner(), new Observer<ArrayList<RoutineClassDetails>>() {
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

        mAdapter = new CustomRoutineSearchWithCourseCodeRecyclerViewAdapter();

        noClasses = view.findViewById(R.id.no_classes);

        courseCodeSpinner = view.findViewById(R.id.course_code_spinner);
        shiftSpinner = view.findViewById(R.id.shift_spinner);

        view.findViewById(R.id.search).setOnClickListener(this);
    }

    private void initializeRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadRoutineWithCourseCode() {
        mViewModel.loadRoutineWithCourseCode(
                courseCodeSpinner.getSelectedItem().toString().trim().split(":")[0].trim(),
                shiftSpinner.getSelectedItem().toString().trim()
        );
    }

    private void initializeSpinners() {

        ArrayList<String> courseCodesWithName = getCourseCodesWithName("Day");

        final String[] shift = new String[] {HelperClass.SHIFT_DAY , HelperClass.SHIFT_EVENING};


        final ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(getContext(),R.layout.spinner_items,courseCodesWithName);

        final ArrayAdapter<String> shiftAdapter = new ArrayAdapter<>(getContext() , R.layout.spinner_items ,shift);

        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        shiftAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.shiftSpinner.setAdapter(shiftAdapter);
        this.courseCodeSpinner.setAdapter(courseAdapter);

        shiftSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<String> courseCodesWithName = getCourseCodesWithName(shiftSpinner.getSelectedItem().toString());
                courseAdapter.clear();
                courseAdapter.addAll(courseCodesWithName);
                courseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private ArrayList<String> getCourseCodesWithName(String shift) {
        LinkedHashMap<String,String> coursesMap;
        if (shift.equals("Day")) {
            coursesMap = HelperClass.getCoursesDay();
        } else {
            coursesMap = HelperClass.getCoursesEveningBsc();
        }

        ArrayList<String> combinedList = new ArrayList<>();

        for (LinkedHashMap.Entry<String, String> entry : coursesMap.entrySet()) {
            combinedList.add(entry.getKey() + " : " + entry.getValue());
        }

        return combinedList;

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.search) {
            loadRoutineWithCourseCode();
        }
    }

}
