package bd.edu.daffodilvarsity.classmanager.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import bd.edu.daffodilvarsity.classmanager.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminPanel extends Fragment {

    private Spinner mCampusSelector;
    private Spinner mShiftSelector;
    private Button mSelectBtn;
    private TextView mSelectTextView;
    private Button mUpload;


    public AdminPanel() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_panel, container, false);

        initializeVariables(view);
        addSpinnerItems();
        return view;
    }

    private void initializeVariables(View view) {
        mCampusSelector = view.findViewById(R.id.spinner_1);
        mShiftSelector = view.findViewById(R.id.spinner_2);
        mSelectBtn = view.findViewById(R.id.btn_select_file);
        mSelectTextView = view.findViewById(R.id.select_file);
        mUpload = view.findViewById(R.id.upload);
    }

    private void addSpinnerItems() {

        String[] campusItems = new String[]{"Main Campus"};
        String[] shiftItems = new String[]{"Day", "Evening"};

        ArrayAdapter<String> adapterCampus = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,campusItems);

        adapterCampus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mCampusSelector.setAdapter(adapterCampus);

        ArrayAdapter<String> adapterShift = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,shiftItems);

        adapterShift.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mShiftSelector.setAdapter(adapterShift);

    }

}
