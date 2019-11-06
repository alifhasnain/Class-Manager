package bd.edu.daffodilvarsity.classmanager.notification;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.SharedPreferencesHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationStudent extends Fragment implements View.OnClickListener {

    private LinearLayout toggleNotification;

    private NotificationStudentViewModel mViewModel;

    private SwitchMaterial notificationSwitch;

    private RecyclerView mRecyclerView;

    private NotificatinStudentRecyclerViewAdapter mAdapter;

    private FloatingActionButton fab;

    private TextView noNotification;

    public NotificationStudent() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_student, container, false);

        initializeVariables(view);

        setCurrentNotificationStatus();

        initializeRecyclerView();

        return view;
    }

    private void initializeVariables(View view) {

        fab = view.findViewById(R.id.clear_notifications);
        fab.setOnClickListener(this);

        toggleNotification = view.findViewById(R.id.toggle_notification);
        toggleNotification.setOnClickListener(this);
        notificationSwitch = view.findViewById(R.id.notification_switch);
        notificationSwitch.setClickable(false);

        mRecyclerView = view.findViewById(R.id.notification_recycler_view);
        mAdapter = new NotificatinStudentRecyclerViewAdapter();

        noNotification = view.findViewById(R.id.no_notification);

    }

    private void initializeRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(NotificationStudentViewModel.class);

        mViewModel.loadNotifications();

        mViewModel.getNotifications().observe(getViewLifecycleOwner(), new Observer<List<NotificationObjStudent>>() {
            @Override
            public void onChanged(List<NotificationObjStudent> notificationObjStudents) {
                if (mAdapter != null) {
                    mAdapter.updateRecyclerView(notificationObjStudents);
                    if (mAdapter.getItemCount() == 0) {
                        displayNoNotificationAvailable(true);
                    } else {
                        displayNoNotificationAvailable(false);
                    }
                }
            }
        });

    }

    private void displayNoNotificationAvailable(boolean visible) {
        if (visible) {
            noNotification.setVisibility(View.VISIBLE);
        } else {
            noNotification.setVisibility(View.GONE);
        }
    }

    private void setCurrentNotificationStatus() {

        if (SharedPreferencesHelper.getStudentNotificatinStatus(getContext())) {
            notificationSwitch.setChecked(true);
        } else {
            notificationSwitch.setChecked(false);
        }

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.toggle_notification) {
            toggleNotification();
        } else if (view.getId() == R.id.clear_notifications) {
            deleteAllNotifications();
        }
    }

    private void deleteAllNotifications() {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Are you sure to clear all notifications?")
                .setMessage("Once you clear them,there is no way to get them back.")
                .setPositiveButton("proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mViewModel.deleteAllNotifications();
                    }
                })
                .setNegativeButton("No",null)
                .create();

        dialog.show();
    }

    private void toggleNotification() {
        if(notificationSwitch.isChecked())  {
            SharedPreferencesHelper.enableStudentNotification(getContext(),false);
            notificationSwitch.setChecked(false);
            makeToast("Notification disabled");
        }   else {
            SharedPreferencesHelper.enableStudentNotification(getContext(),true);
            notificationSwitch.setChecked(true);
            makeToast("Notification enabled");
        }
    }

    private void makeToast(String text) {
        if (getContext() != null) {
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        }
    }
}
