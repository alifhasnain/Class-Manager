package bd.edu.daffodilvarsity.classmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.fragments.AdminPanel;
import bd.edu.daffodilvarsity.classmanager.fragments.BookClasses;
import bd.edu.daffodilvarsity.classmanager.fragments.BookedClasses;
import bd.edu.daffodilvarsity.classmanager.fragments.Classes;
import bd.edu.daffodilvarsity.classmanager.fragments.ProfileTeacher;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth mAuth;

    FirebaseAuth.AuthStateListener mAuthStateListener;

    DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeVariables();

        if(mAuth.getCurrentUser()==null && !isEmailVerified() ) {
            mAuth.signOut();
        }
        
        setUpNavigationDrawer();

        if(savedInstanceState == null)  {
            setUpHomeFragment();
        }

    }

    private void setUpHomeFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("classes")
                .replace(R.id.fragment_container,new Classes(),"classes")
                .commit();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onBackPressed() {

        if(mDrawerLayout.isDrawerOpen(GravityCompat.START))  {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }

    }

    private void initializeVariables() {
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(mAuth.getCurrentUser() == null)  {
                    startActivity(new Intent(MainActivity.this,SignIn.class));
                    finish();
                }
            }
        };
        mDrawerLayout = findViewById(R.id.drawer_layout);
    }

    private void setUpNavigationDrawer() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(drawerToggle);

        drawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);

        navigationView.inflateMenu(R.menu.drawer_menu_admin);

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.classes);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.admin:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,new AdminPanel(),"admin_panel")
                        .commit();
                break;
            case R.id.profile:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,new ProfileTeacher(),"teacher_profile")
                        .commit();
                break;
            case R.id.classes:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,new Classes(),"classes")
                        .commit();
                break;
            case R.id.book_classes:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,new BookClasses(),"book_classes")
                        .commit();
                break;
            case R.id.booked_classroom:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,new BookedClasses(),"booked_classes")
                        .commit();
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
        }
        catch (Exception e)  {
            e.printStackTrace();
        }
    }

    private void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
