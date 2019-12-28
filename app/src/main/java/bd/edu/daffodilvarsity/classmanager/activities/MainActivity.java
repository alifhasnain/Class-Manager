package bd.edu.daffodilvarsity.classmanager.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bd.edu.daffodilvarsity.classmanager.CustomRoutineSearch.CustomRoutineSearchTabHolder;
import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.fragments.AdminPanel;
import bd.edu.daffodilvarsity.classmanager.fragments.BookClasses;
import bd.edu.daffodilvarsity.classmanager.fragments.BookedClasses;
import bd.edu.daffodilvarsity.classmanager.fragments.CRList;
import bd.edu.daffodilvarsity.classmanager.fragments.EmptyRooms;
import bd.edu.daffodilvarsity.classmanager.fragments.ProfileStudents;
import bd.edu.daffodilvarsity.classmanager.fragments.ProfileTeacher;
import bd.edu.daffodilvarsity.classmanager.notification.NotificationStudent;
import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectStudent;
import bd.edu.daffodilvarsity.classmanager.otherclasses.SharedPreferencesHelper;
import bd.edu.daffodilvarsity.classmanager.routine.EachDayRoutineTabHolder;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private long currentTimeInMillis;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private Fragment mFragmentToLaunch;

    private NavigationView mNavigationView;

    private DrawerLayout mDrawerLayout;

    private MaterialToolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeVariables();

        if (isUserTeacher()) {
            if (mAuth.getCurrentUser() != null && !isEmailVerified()) {
                mAuth.signOut();
            }
        }

        setUpNavigationDrawer();

        disableNavigationViewScrollbars();

        if (savedInstanceState == null) {
            setUpHomeFragment();
        }

    }

    private void setUpHomeFragment() {

        mFragmentToLaunch = new EachDayRoutineTabHolder();

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

        subscribeToTopics();

        if (mAuthStateListener != null) {
            mAuth.addAuthStateListener(mAuthStateListener);
        }

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

                if (mFragmentToLaunch != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                            .replace(R.id.fragment_container, mFragmentToLaunch, "classes")
                            .commit();
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
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (System.currentTimeMillis() < (currentTimeInMillis + 1500)) {
                super.onBackPressed();
                finish();
            } else {
                currentTimeInMillis = System.currentTimeMillis();
                makeToast("press back again to exit");
            }
        }
    }

    private void initializeVariables() {

        mAuth = FirebaseAuth.getInstance();

        if (isUserTeacher() || isUserAdmin()) {
            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (mAuth.getCurrentUser() == null) {

                        SharedPreferencesHelper.removeUserTypeFromSharedPref(getApplicationContext());
                        SharedPreferencesHelper.removeTeacherProfileFromSharedPref(getApplicationContext());

                        Intent intent = new Intent(MainActivity.this, SignIn.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            };
        }

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation_view);
        mToolBar = findViewById(R.id.toolbar);
    }

    private void setUpNavigationDrawer() {

        setSupportActionBar(mToolBar);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(drawerToggle);

        drawerToggle.syncState();

        switch (SharedPreferencesHelper.getUserType(this)) {
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
                mFragmentToLaunch = new AdminPanel();
                break;
            case R.id.notification_student:
                enableToolbarScrolling(false);
                mFragmentToLaunch = new NotificationStudent();
                break;
            case R.id.profile_teacher:
                enableToolbarScrolling(false);
                mFragmentToLaunch = new ProfileTeacher();
                break;
            case R.id.profile_student:
                enableToolbarScrolling(false);
                mFragmentToLaunch = new ProfileStudents();
                break;
            case R.id.classes:
                enableToolbarScrolling(true);
                mFragmentToLaunch = new EachDayRoutineTabHolder();
                break;
            case R.id.book_classes:
                enableToolbarScrolling(true);
                mFragmentToLaunch = new BookClasses();
                break;
            case R.id.custom_room_search:
                mFragmentToLaunch = new CustomRoutineSearchTabHolder();
                enableToolbarScrolling(true);
                break;
            case R.id.empty_rooms:
                enableToolbarScrolling(true);
                mFragmentToLaunch = new EmptyRooms();
                break;
            case R.id.booked_classroom:
                enableToolbarScrolling(true);
                mFragmentToLaunch = new BookedClasses();
                break;
            case R.id.cr_list:
                enableToolbarScrolling(true);
                mFragmentToLaunch = new CRList();
                break;
            case R.id.sign_out:
                signOut();
                break;
            case R.id.report_bug:
                sendMail(new String[]{"hasnain.alif20@gmail.com"}, "Bug in Class Manager App", "");
                break;
            case R.id.about:
                startActivity(new Intent(this, About.class));
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

        if (isUserStudent()) {
            SharedPreferencesHelper.removeStudentProfileFromSharedPref(this);
            SharedPreferencesHelper.removeUserTypeFromSharedPref(this);
            SharedPreferencesHelper.removeCoursesFromSharedPref(this);
            SharedPreferencesHelper.removeTeacherProfileFromSharedPref(this);
            startActivity(new Intent(this, SignIn.class));
            finish();
        } else {
            try {
                mAuth.signOut();
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    private void sendMail(String[] email, String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);

        try {
            startActivity(Intent.createChooser(intent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }


    }

    private void disableNavigationViewScrollbars() {
        if (mNavigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) mNavigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }

    private void subscribeToTopics() {

        if (SharedPreferencesHelper.getStudentNotificatinStatus(this) && isUserStudent()) {

            HashMap<String, String> courseAndSection = SharedPreferencesHelper.getCoursesAndSectionMapFromSharedPreferences(this);

            ProfileObjectStudent profile = SharedPreferencesHelper.getStudentOfflineProfile(this);

            String shift = profile.getShift();

            List<Task<Void>> subscribeTaskList = new ArrayList<>();

            for (HashMap.Entry<String, String> entry : courseAndSection.entrySet()) {
                subscribeTaskList.add(FirebaseMessaging.getInstance().subscribeToTopic(shift + "-" + entry.getKey() + "-" + entry.getValue()));
            }

            Task<List<Void>> allTasks = Tasks.whenAllSuccess(subscribeTaskList);

            allTasks.addOnSuccessListener(new OnSuccessListener<List<Void>>() {
                @Override
                public void onSuccess(List<Void> voids) {
                    Timber.e("Subscribed To Topics.");
                }
            });

        }

    }

    private boolean isUserStudent() {
        return SharedPreferencesHelper.getUserType(getApplicationContext()).equals(HelperClass.USER_TYPE_STUDENT);
    }

    private boolean isUserTeacher() {
        return SharedPreferencesHelper.getUserType(getApplicationContext()).equals(HelperClass.USER_TYPE_TEACHER);
    }

    private boolean isUserAdmin() {
        return SharedPreferencesHelper.getUserType(getApplicationContext()).equals(HelperClass.USER_TYPE_ADMIN);
    }

    private void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
