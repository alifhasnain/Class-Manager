package bd.edu.daffodilvarsity.classmanager.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import bd.edu.daffodilvarsity.classmanager.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MiscellaneousAdminAction extends Fragment implements View.OnClickListener {

    private FirebaseFunctions mFunctions;

    private Button resetBookCount;

    public MiscellaneousAdminAction() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_miscellaneous_admin_action, container, false);

        initializeVariables(view);

        return view;
    }

    private void initializeVariables(View view) {

        mFunctions = FirebaseFunctions.getInstance();

        resetBookCount = view.findViewById(R.id.reset_book_count);
        resetBookCount.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reset_book_count:
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Are you sure?")
                        .setMessage("Once you proceed this action cannot be undone.")
                        .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                resetBookCount();
                            }
                        }).setNegativeButton("Cancel", null).create();

                dialog.show();
                break;
        }
    }

    private void resetBookCount() {
        mFunctions.getHttpsCallable("resetBookCount").call()
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        if (httpsCallableResult.getData() != null) {
                            makeToast(httpsCallableResult.getData().toString());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeToast(e.getMessage());
                    }
                });
    }

    private void makeToast(String txt) {
        if (getContext() != null) {
            Toast.makeText(getContext(), txt, Toast.LENGTH_SHORT).show();
        }
    }
}
