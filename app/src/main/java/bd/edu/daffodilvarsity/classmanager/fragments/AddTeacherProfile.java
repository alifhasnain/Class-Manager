package bd.edu.daffodilvarsity.classmanager.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import bd.edu.daffodilvarsity.classmanager.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddTeacherProfile extends Fragment {


    public AddTeacherProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_teacher_profile, container, false);
    }

}
