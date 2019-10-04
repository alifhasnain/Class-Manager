package bd.edu.daffodilvarsity.classmanager.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.adapters.AdminPanelPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminPanel extends Fragment {

    private ViewPager mViewPager;

    public AdminPanel() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_panel, container, false);

        initializeVariables(view);

        initializeViewPager();
        initializeTabLayout(view);

        return view;
    }

    private void initializeVariables(View view) {
        mViewPager = view.findViewById(R.id.tab_holder);
    }

    private void initializeTabLayout(View view) {

        TabLayout tabLayout = view.findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(mViewPager);

    }

    private void initializeViewPager() {

        AdminPanelPagerAdapter pagerAdapter = new AdminPanelPagerAdapter(getChildFragmentManager());

        pagerAdapter.addFragment(new RoutineParser(),"Parse Routine");
        pagerAdapter.addFragment(new ParseTeacherProfiles(),"Parse Teacher Profiles");
        pagerAdapter.addFragment(new AddTeacherProfile(),"Add Teacher Profile");
        pagerAdapter.addFragment(new SearchBookedClassesWithDate(),"Booked Classes (Date)");
        pagerAdapter.addFragment(new GiveTeacherClaim(),"Give Teacher Claim");
        pagerAdapter.addFragment(new TeacherProfileList(),"Teacher Profiles");

        mViewPager.setAdapter(pagerAdapter);
    }

}
