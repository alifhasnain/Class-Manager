package bd.edu.daffodilvarsity.classmanager.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.GregorianCalendar;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.fragments.AdminPanel;
import bd.edu.daffodilvarsity.classmanager.fragments.BookClasses;
import bd.edu.daffodilvarsity.classmanager.fragments.BookedClasses;
import bd.edu.daffodilvarsity.classmanager.fragments.CustomRoutineSearch;
import bd.edu.daffodilvarsity.classmanager.fragments.EmptyRooms;
import bd.edu.daffodilvarsity.classmanager.fragments.ExtraClassesStudent;
import bd.edu.daffodilvarsity.classmanager.fragments.ProfileStudents;
import bd.edu.daffodilvarsity.classmanager.fragments.ProfileTeacher;
import bd.edu.daffodilvarsity.classmanager.fragments.RoutineTabHolder;
import bd.edu.daffodilvarsity.classmanager.otherclasses.BookedClassDetails;
import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

    private int checkedNavigationItem;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private Fragment mFragmentToLaunch;

    private NavigationView mNavigationView;

    private DrawerLayout mDrawerLayout;

    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeVariables();

        if (mAuth.getCurrentUser() != null && !isEmailVerified()) {
            mAuth.signOut();
        }

        setUpNavigationDrawer();

        if (savedInstanceState == null) {
            setUpHomeFragment();
        }

        //testSaveTimestamp();

    }

    private void testSaveTimestamp() {

        Calendar calendar = Calendar.getInstance();

        GregorianCalendar gCalendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        Timestamp timestamp = new Timestamp(gCalendar.getTime());

        BookedClassDetails bookedClassDetails = new BookedClassDetails();

        bookedClassDetails.setRoomNo("115DT");
        bookedClassDetails.setTime("10.00AM-11.30AM");
        bookedClassDetails.setReservationDate(timestamp);
        bookedClassDetails.setProgram("B.Sc in CSE");
        bookedClassDetails.setShift("Day");
        bookedClassDetails.setSection("E");
        bookedClassDetails.setCourseCode("CSE313");
        bookedClassDetails.setPriority(1f);

        Gson gson = new Gson();

        String jsonData = gson.toJson(bookedClassDetails);

        makeToast(jsonData);

        mFunctions.getHttpsCallable("test").call(jsonData)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        makeToast("Success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                makeToast("Failed");
            }
        });

    }

    private void setUpHomeFragment() {

        mFragmentToLaunch = new RoutineTabHolder();

        enableToolbarScrolling(true);

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, mFragmentToLaunch, "classes")
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                switch (checkedNavigationItem) {
                    default:
                        if (mFragmentToLaunch != null) {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                    .replace(R.id.fragment_container, mFragmentToLaunch, "classes")
                                    .commit();
                        }
                        break;
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    private void initializeVariables() {
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MainActivity.this, SignIn.class));
                    finish();
                }
            }
        };
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation_view);
        mToolBar = findViewById(R.id.toolbar);
    }

    private void setUpNavigationDrawer() {

        setSupportActionBar(mToolBar);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(drawerToggle);

        drawerToggle.syncState();

        switch (getUserType()) {
            case HelperClass.USER_TYPE_ADMIN:
                mNavigationView.inflateMenu(R.menu.drawer_menu_admin);
                break;
            case HelperClass.USER_TYPE_TEACHER:
                mNavigationView.inflateMenu(R.menu.drawer_menu_teacher);
                break;
            case HelperClass.USER_TYPE_STUDENT:
                mNavigationView.inflateMenu(R.menu.drawer_menu_student);
                break;
        }

        mNavigationView.setNavigationItemSelectedListener(this);

        mNavigationView.setCheckedItem(R.id.classes);
    }

    private String getUserType() {
        SharedPreferences sharedPreferences = getSharedPreferences(HelperClass.SHARED_PREFERENCE_TAG, MODE_PRIVATE);
        return sharedPreferences.getString(HelperClass.USER_TYPE, null);
    }

    private void enableToolbarScrolling(boolean b) {
        AppBarLayout.LayoutParams parms = (AppBarLayout.LayoutParams) mToolBar.getLayoutParams();
        if (b) {
            parms.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        } else {
            parms.setScrollFlags(0);
        }
        mToolBar.setLayoutParams(parms);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.admin:
                enableToolbarScrolling(false);
                checkedNavigationItem = R.id.admin;
                mFragmentToLaunch = new AdminPanel();
                break;
            case R.id.profile_teacher:
                checkedNavigationItem = R.id.profile_teacher;
                enableToolbarScrolling(false);
                mFragmentToLaunch = new ProfileTeacher();
                break;
            case R.id.profile_student:
                checkedNavigationItem = R.id.profile_student;
                enableToolbarScrolling(false);
                mFragmentToLaunch = new ProfileStudents();
                break;
            case R.id.classes:
                checkedNavigationItem = R.id.classes;
                enableToolbarScrolling(true);
                mFragmentToLaunch = new RoutineTabHolder();
                break;
            case R.id.book_classes:
                checkedNavigationItem = R.id.book_classes;
                enableToolbarScrolling(true);
                mFragmentToLaunch = new BookClasses();
                break;
            case R.id.custom_room_search:
                checkedNavigationItem = R.id.custom_room_search;
                mFragmentToLaunch = new CustomRoutineSearch();
                enableToolbarScrolling(true);
                break;
            case R.id.extra_classes:
                checkedNavigationItem = R.id.extra_classes;
                enableToolbarScrolling(true);
                mFragmentToLaunch = new ExtraClassesStudent();
                break;
            case R.id.empty_rooms:
                checkedNavigationItem = R.id.empty_rooms;
                enableToolbarScrolling(true);
                mFragmentToLaunch = new EmptyRooms();
                break;
            case R.id.booked_classroom:
                checkedNavigationItem = R.id.booked_classroom;
                enableToolbarScrolling(true);
                mFragmentToLaunch = new BookedClasses();
                break;
            case R.id.sign_out:
                signOut();
                break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private boolean isEmailVerified() {
        try {
            return mAuth.getCurrentUser().isEmailVerified();
        } catch (Exception e) {
            return false;
        }
    }

    private void signOut() {
        try {
            mAuth.signOut();
        } catch (Exception e) {
            Log.e(TAG, "Error : ", e);
        }
    }

    private void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
