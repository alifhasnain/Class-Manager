package bd.edu.daffodilvarsity.classmanager.notification;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.messaging.FirebaseMessaging;

import bd.edu.daffodilvarsity.classmanager.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationStudent extends Fragment implements View.OnClickListener {

    SwitchMaterial toggleNotificationBtn;

    public NotificationStudent() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_student, container, false);

        initializeVariables(view);


        return view;
    }

    private void initializeVariables(View view) {

        toggleNotificationBtn = view.findViewById(R.id.toggle_notification);
        toggleNotificationBtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toggle_notification:
                toggleNotification();
                break;
        }
    }

    private void toggleNotification() {
        if(toggleNotificationBtn.isChecked())  {
            makeToast("Notification enabled");
        }   else {
            makeToast("Notification disabled");
        }
    }

    private void subscribeToTopic() {

        FirebaseMessaging.getInstance().subscribeToTopic("book_notification").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                makeToast("subscribed");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                makeToast("subscription failed!");
            }
        });

    }

    private void makeToast(String text) {
        if (getContext() != null) {
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        }
    }
}