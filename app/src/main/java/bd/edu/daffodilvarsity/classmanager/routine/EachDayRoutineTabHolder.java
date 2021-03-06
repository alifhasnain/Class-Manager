package bd.edu.daffodilvarsity.classmanager.routine;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.Calendar;
import java.util.LinkedHashMap;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.adapters.viewPagerAdapters.EachDayRoutinePagerAdapter;
import bd.edu.daffodilvarsity.classmanager.otherclasses.SharedPreferencesHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class EachDayRoutineTabHolder extends Fragment {

    private EachDayClassViewModel mViewModel;

    private View mView;

    private EachDayRoutinePagerAdapter eachDayRoutinePagerAdapter;

    private ViewPager mViewPager;

    public EachDayRoutineTabHolder() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_routine_tab_holder, container, false);
        setHasOptionsMenu(true);

        initializeVariables();

        initializeViewPager();

        initializeTabLayout();

        return mView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(getActivity()).get(EachDayClassViewModel.class);

        checkRoutineVersion();

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_classes_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.force_refresh:

                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle("Alert")
                        .setMessage("This will refresh whole routine from server and cancel all routine reminders.")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                makeToast("Downloading routine");
                                forceRefreshWholeRoutine();
                            }
                        }).create();

                alertDialog.show();
                return true;
            default:
                return false;
        }
    }

    private void checkRoutineVersion() {

        FirebaseFirestore.getInstance().document("routine-version/version/").get(Source.SERVER)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {

                            String version = documentSnapshot.get("version").toString();

                            String routineVersionPref = SharedPreferencesHelper.getRoutineVersionFromSharedPreferences(getActivity());

                            if(!routineVersionPref.equals("00000") && !version.equals(routineVersionPref))   {
                                makeToast("Downloading new updated routine.");
                                forceRefreshWholeRoutine(version);
                            }
                        }
                    }
                });
    }

    private void forceRefreshWholeRoutine(String version) {

        mViewModel.loadWholeRoutineFromServer(version);

        /*String userType = "";

        if (getActivity() != null) {
            userType = mSharedPrefHelper.getUserType(getContext());
        }

        if (userType.equals(HelperClass.USER_TYPE_ADMIN) || userType.equals(HelperClass.USER_TYPE_TEACHER)) {
            mViewModel.loadWholeTeacherRoutineFromServer();
        } else if (userType.equals(HelperClass.USER_TYPE_STUDENT)) {
            mViewModel.loadWholeStudentRoutineFromServer();
        }*/
    }

    private void forceRefreshWholeRoutine() {

        mViewModel.loadWholeRoutineFromServer();

    }

    private void initializeVariables() {
        mViewPager = mView.findViewById(R.id.tab_holder);
    }

    private void initializeTabLayout() {

        int selectedTabPosition = getDayOfWeekPosition();

        if (selectedTabPosition < 0 || selectedTabPosition > 6) {
            selectedTabPosition = 0;
        }

        TabLayout tabLayout = mView.findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(mViewPager);

        TabLayout.Tab tab = tabLayout.getTabAt(selectedTabPosition);

        tab.select();

    }

    private int getDayOfWeekPosition() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == 6) {
            return 0;
        } else {
            return dayOfWeek;
        }
    }

    private void makeToast(String msg) {
        if (getContext() != null) {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }


    private void initializeViewPager() {

        eachDayRoutinePagerAdapter = new EachDayRoutinePagerAdapter(getChildFragmentManager());

        LinkedHashMap<String, EachDayRoutine> allDaysData = getEachDayFragmentMap();

        for (LinkedHashMap.Entry<String, EachDayRoutine> entry : allDaysData.entrySet()) {
            eachDayRoutinePagerAdapter.addFragment(entry.getValue(), entry.getKey());
        }

        mViewPager.setAdapter(eachDayRoutinePagerAdapter);

        mViewPager.setOffscreenPageLimit(3);

    }

    private LinkedHashMap<String, EachDayRoutine> getEachDayFragmentMap() {

        LinkedHashMap<String, EachDayRoutine> allDays = new LinkedHashMap<>();

        EachDayRoutine saturday = new EachDayRoutine();
        saturday.setArguments(getBundleWithDayOfWeekAttached("Saturday"));
        allDays.put("Saturday", saturday);

        EachDayRoutine sunday = new EachDayRoutine();
        sunday.setArguments(getBundleWithDayOfWeekAttached("Sunday"));
        allDays.put("Sunday", sunday);


        EachDayRoutine monday = new EachDayRoutine();
        monday.setArguments(getBundleWithDayOfWeekAttached("Monday"));
        allDays.put("Monday", monday);


        EachDayRoutine tuesday = new EachDayRoutine();
        tuesday.setArguments(getBundleWithDayOfWeekAttached("Tuesday"));
        allDays.put("Tuesday", tuesday);


        EachDayRoutine wednesday = new EachDayRoutine();
        wednesday.setArguments(getBundleWithDayOfWeekAttached("Wednesday"));
        allDays.put("Wednesday", wednesday);


        EachDayRoutine thursday = new EachDayRoutine();
        thursday.setArguments(getBundleWithDayOfWeekAttached("Thursday"));
        allDays.put("Thursday", thursday);

        EachDayRoutine friday = new EachDayRoutine();
        friday.setArguments(getBundleWithDayOfWeekAttached("Friday"));
        allDays.put("Friday", friday);

        return allDays;

    }

    private Bundle getBundleWithDayOfWeekAttached(String dayOfWeek) {
        Bundle bundle = new Bundle();
        bundle.putString("dayOfWeek",dayOfWeek);
        return bundle;
    }

}
