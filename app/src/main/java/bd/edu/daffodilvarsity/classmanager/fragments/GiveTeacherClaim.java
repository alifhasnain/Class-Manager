package bd.edu.daffodilvarsity.classmanager.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import bd.edu.daffodilvarsity.classmanager.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GiveTeacherClaim extends Fragment implements View.OnClickListener {

    private FirebaseFunctions mFunctions;

    private TextInputLayout email;

    private Button grantPermission;


    public GiveTeacherClaim() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_give_teacher_claim, container, false);

        initializeVariables(view);

        return view;
    }

    private void initializeVariables(View view) {
        email = view.findViewById(R.id.email);

        grantPermission = view.findViewById(R.id.grant_permission);
        grantPermission.setOnClickListener(this);

        mFunctions = FirebaseFunctions.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.grant_permission:
                grantTeacherClaim();
                break;
        }
    }

    private void grantTeacherClaim() {

        String emailStr = email.getEditText().getText().toString();

        if (emailStr.isEmpty()) {
            makeToast("Please insert an eamil.");
            return;
        }

        String jsonEmail = "{\"email\" : " + "\"" + emailStr + "\"" + "}";

        makeToast("Processing...");

        mFunctions.getHttpsCallable("addTeacher").call(jsonEmail).addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
            @Override
            public void onSuccess(HttpsCallableResult httpsCallableResult) {
                makeToast("Success.");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                makeToast("Unsuccessful");
            }
        });

    }

    private void makeToast(String txt)  {
        if(getContext()!=null)  {
            Toast.makeText(getContext(), txt, Toast.LENGTH_SHORT).show();
        }
    }
}
