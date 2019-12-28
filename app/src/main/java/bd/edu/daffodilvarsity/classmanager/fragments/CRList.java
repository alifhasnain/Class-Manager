package bd.edu.daffodilvarsity.classmanager.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.activities.AddCR;
import bd.edu.daffodilvarsity.classmanager.adapters.recyclerViewAdapters.CRListAdapter;
import bd.edu.daffodilvarsity.classmanager.otherclasses.CRObj;

/**
 * A simple {@link Fragment} subclass.
 */
public class CRList extends Fragment {

    private static final String TAG_CR_LIST_SHARED_PREF = "cr-list-shared-pref";

    private CRListAdapter mAdapter;

    private ArrayList<CRObj> mCRList = new ArrayList<>();

    private RecyclerView recyclerView;

    private TextView emptyList;

    public CRList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_crlist, container, false);
        setHasOptionsMenu(true);

        initializeVariables(view);

        return view;
    }

    @Override
    public void onStart() {
        getCrListFromSharedPref();
        refreshRecyclerView();
        super.onStart();
    }

    private void refreshRecyclerView() {

        mAdapter.setDataAndRefresh(mCRList);
        if (mAdapter.getItemCount() == 0) {
            emptyList.setVisibility(View.VISIBLE);
        } else {
            emptyList.setVisibility(View.GONE);
        }

    }

    private void getCrListFromSharedPref() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(TAG_CR_LIST_SHARED_PREF, Context.MODE_PRIVATE);
        String crJson = sharedPreferences.getString("CR","");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<CRObj>>(){}.getType();
        ArrayList<CRObj> tempList = gson.fromJson(crJson, type);
        if (tempList != null) {
            mCRList.clear();
            mCRList.addAll(tempList);
        }
    }

    private void initializeVariables(View view) {
        // Initialize RecyclerView And Adapter
        mAdapter = new CRListAdapter();
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);

        emptyList = view.findViewById(R.id.empty_list);

        mAdapter.addActionEventListener(new CRListAdapter.ActionEventListener() {
            @Override
            public void onSendEmailClicked(CRObj obj) {
                if (isEmptyOrNull(obj.getEmail())) {
                    makeToast("No email is attached.");
                } else {
                    sendMail(obj.getEmail());
                }
            }

            @Override
            public void onSendMessageClicked(CRObj obj) {
                if (isEmptyOrNull(obj.getPhoneNo())) {
                    makeToast("No contact no is attached.");
                } else {
                    sendMessage(obj.getPhoneNo());
                }
            }

            @Override
            public void onDialClicked(final CRObj obj) {

                if (isEmptyOrNull(obj.getPhoneNo())) {
                    makeToast("No contact no is attached.");
                }
                else {
                    AlertDialog dialog = new  AlertDialog.Builder(getContext())
                            .setTitle("Please confirm")
                            .setMessage("Do you want to call " + obj.getName() + " from section " + obj.getSection() + " course " + obj.getCourseCode() + "?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    phoneCall(obj.getPhoneNo());
                                }
                            })
                            .setNegativeButton("No",null)
                            .create();
                    dialog.show();
                }

            }

            @Override
            public void onDeleteClicked(int position) {
                mCRList.remove(position);
                updateCrList();
                refreshRecyclerView();
            }
        });
    }

    private void updateCrList() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(TAG_CR_LIST_SHARED_PREF, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<CRObj>>(){}.getType();
        String jsonString = gson.toJson(mCRList,type);
        sharedPreferences.edit().putString("CR",jsonString).apply();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_cr_list,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_cr:
                Intent intent = new Intent(getActivity(),AddCR.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    private void sendMail(String email) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            this.startActivity(intent);
        }
    }

    private void sendMessage(String contactNo) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:"+contactNo));
        startActivity(sendIntent);
    }

    private void phoneCall(String contactNo) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+contactNo));
        startActivity(intent);
    }

    private boolean isEmptyOrNull(String string) {
        if (string == null || string.isEmpty()) {
            return true;
        }
        return false;
    }

    private void makeToast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }
}
