package bd.edu.daffodilvarsity.classmanager.CustomRoutineSearch;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import bd.edu.daffodilvarsity.classmanager.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomRoutineSearchTabHolder extends Fragment {

    private ViewPager mViewPager;

    public CustomRoutineSearchTabHolder() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_custom_routine_search_tab_holder, container, false);

        initializeVariables(view);

        initializeViewPager();

        initializeTabLayout(view);

        return view;
    }

    private void initializeViewPager() {
        CustomRoutineSearchPagerAdapter adapter = new CustomRoutineSearchPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new CustomRoutineSearchTeacher(),"Teacher Routine");
        adapter.addFragment(new CustomRoutineSearchStudent(),"Student Routine");
        mViewPager.setAdapter(adapter);
    }

    private void initializeVariables(View view) {
        mViewPager = view.findViewById(R.id.tab_holder);
    }

    private void initializeTabLayout(View view) {

        TabLayout tabLayout = view.findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(mViewPager);

    }

}
