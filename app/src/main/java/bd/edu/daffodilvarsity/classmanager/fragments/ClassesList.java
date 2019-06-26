package bd.edu.daffodilvarsity.classmanager.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.adapters.ClassListPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClassesList extends Fragment {

    private View mView;

    private ClassListPagerAdapter classListPagerAdapter;

    private ViewPager mViewPager;

    public ClassesList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_classes, container, false);

        initializeVariables();

        initializeViewPager();
        initializeTabLayout();
        
        return mView;

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initializeVariables() {
        mViewPager = mView.findViewById(R.id.tab_holder);
    }

    private void initializeTabLayout() {

        int selectedTabPosition = getDayOfWeekPosition() ;

        if(selectedTabPosition<0 || selectedTabPosition>6)  {
            selectedTabPosition = 0;
        }

        TabLayout tabLayout = mView.findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(mViewPager);

        TabLayout.Tab tab = tabLayout.getTabAt(selectedTabPosition);

        tab.select();

    }

    private int getDayOfWeekPosition()  {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK);
    }


    private void initializeViewPager() {

        classListPagerAdapter = new ClassListPagerAdapter(getChildFragmentManager());

        classListPagerAdapter.addFragment(new SaturdayClasses(),"Saturday");
        classListPagerAdapter.addFragment(new SundayClasses(),"Sunday");
        classListPagerAdapter.addFragment(new MondayClasses(),"Monday");
        classListPagerAdapter.addFragment(new TuesdayCLasses(),"Tuesday");
        classListPagerAdapter.addFragment(new WednesdayClasses(),"Wednesday");
        classListPagerAdapter.addFragment(new ThursdayClasses(),"Thursday");
        classListPagerAdapter.addFragment(new FridayClasses(),"Friday");

        mViewPager.setAdapter(classListPagerAdapter);

    }

}
