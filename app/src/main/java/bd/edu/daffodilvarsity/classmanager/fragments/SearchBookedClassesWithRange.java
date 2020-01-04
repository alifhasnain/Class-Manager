package bd.edu.daffodilvarsity.classmanager.fragments;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.adapters.recyclerViewAdapters.SearchBookedClassesWithRangeRecyclerViewAdapter;
import bd.edu.daffodilvarsity.classmanager.otherclasses.BookedClassDetailsUser;
import bd.edu.daffodilvarsity.classmanager.viewmodels.SearchBookedClassesWithRangeViewModel;
import de.siegmar.fastcsv.writer.CsvAppender;
import de.siegmar.fastcsv.writer.CsvWriter;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchBookedClassesWithRange extends Fragment implements View.OnClickListener {

    private static final int REQUEST_CODE_SAVE_FILE = 462;

    private ArrayList<BookedClassDetailsUser> classesList = new ArrayList<>();

    private ArrayList<BookedClassDetailsUser> allClassesList = new ArrayList<>();

    private RecyclerView recyclerView;

    private SearchBookedClassesWithRangeRecyclerViewAdapter adapter;

    private ProgressBar progressBar;

    private TextView loadingContent;

    private TextView startDate;

    private TextView endDate;

    private DateFormat dateFormatter = new SimpleDateFormat("d MMM, yyyy");

    private SearchBookedClassesWithRangeViewModel viewModel;

    private Timestamp startTimeStamp;

    private Timestamp endTimeStamp;

    public SearchBookedClassesWithRange() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_booked_classes_with_range, container, false);

        setHasOptionsMenu(true);

        initializeVariables(view);

        initializeRecyclerView();

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeViewModel();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_booked_classes_with_range,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.generate_csv) {
            fetchAllData();
            return false;
        }
        return false;
    }

    private void initializeVariables(View view) {

        view.findViewById(R.id.search).setOnClickListener(this);

        recyclerView = view.findViewById(R.id.recycler_view);

        startDate = view.findViewById(R.id.start_date);
        startDate.setOnClickListener(this);

        endDate = view.findViewById(R.id.end_date);
        endDate.setOnClickListener(this);

        progressBar = view.findViewById(R.id.progress_bar);
        loadingContent = view.findViewById(R.id.loading_content);

    }

    private void initializeViewModel() {

        viewModel = ViewModelProviders.of(this).get(SearchBookedClassesWithRangeViewModel.class);

        viewModel.getClassesLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<BookedClassDetailsUser>>() {
            @Override
            public void onChanged(ArrayList<BookedClassDetailsUser> classes) {
                classesList.addAll(classes);
                adapter.notifyDataSetChanged();
                showProgressbar(false);
            }
        });

        viewModel.getAllClasses().observe(getViewLifecycleOwner(), new Observer<ArrayList<BookedClassDetailsUser>>() {
            @Override
            public void onChanged(ArrayList<BookedClassDetailsUser> bookedClassDetailsUsers) {
                if (bookedClassDetailsUsers.size() == 0) {
                    makeToast("No data found!");
                } else {
                    allClassesList.clear();
                    allClassesList.addAll(bookedClassDetailsUsers);
                    classesList.clear();
                    classesList.addAll(bookedClassDetailsUsers);
                    adapter.notifyDataSetChanged();
                    openSAFAndSave();
                }
            }
        });

        viewModel.getToastMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                makeToast(s);
            }
        });

    }

    private void initializeRecyclerView() {

        adapter = new SearchBookedClassesWithRangeRecyclerViewAdapter(classesList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    makeToast("loading more data");
                    viewModel.fetchMoreNextData();
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_date:
                pickStartDate();
                break;
            case R.id.end_date:
                pickEndDate();
                break;
            case R.id.search:
                fetchData();
                break;
        }
    }

    private void fetchData() {
        if(checkDateValidity()) {

            classesList.clear();
            adapter.notifyDataSetChanged();

            showProgressbar(true);

            viewModel.fetchData(startTimeStamp,endTimeStamp);

        } else {
            makeToast("Invalid Date Range");
        }
    }

    private void fetchAllData() {
        if(checkDateValidity()) {
            viewModel.fetchAllData(startTimeStamp,endTimeStamp);
        } else {
            makeToast("Invalid Date Range");
        }
    }

    private void openSAFAndSave() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/comma-separated-values");
        intent.putExtra(Intent.EXTRA_TITLE,"From : " + startDate.getText().toString() + " TO : " + endDate.getText().toString() + ".csv");

        startActivityForResult(intent, REQUEST_CODE_SAVE_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SAVE_FILE :
                    if (data.getData() != null) {
                        generateCSV(data.getData());
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveFile(Uri uri,String text) {
        try {
            ParcelFileDescriptor pfd = getActivity().getContentResolver().
                    openFileDescriptor(uri, "w");
            FileOutputStream fileOutputStream =
                    new FileOutputStream(pfd.getFileDescriptor());
            fileOutputStream.write(text.getBytes());
            // Let the document provider know you're done by closing the stream.
            fileOutputStream.close();
            pfd.close();

            makeToast("File saved");

        } catch (IOException e) {
            Timber.e(e);
        }
    }

    private void generateCSV(Uri uri) {

        CSVStringWriter writer = new CSVStringWriter();

        writer.addLine("Date","Email","Initial","Course Code","Room","Time","Section","Shift");

        for (BookedClassDetailsUser bcd : allClassesList) {
            writer.addLine(
                    getDateStringFromTimestamp(bcd.getReservationDate()),
                    bcd.getTeacherEmail(),
                    bcd.getTeacherInitial(),
                    bcd.getCourseCode(),
                    bcd.getRoomNo(),
                    bcd.getTime(),
                    bcd.getSection(),
                    bcd.getShift()
            );
        }
        saveFile(uri,writer.getCSVString());
    }

    private void generateCSV2(Uri uri) {

        String fileName = "From : " + startDate.getText().toString() + " TO : " + endDate.getText().toString();
        File file = new File(getContext().getExternalCacheDir(),fileName+".csv");

        CsvWriter writer = new CsvWriter();

        try {

            CsvAppender appender = writer.append(file, StandardCharsets.UTF_8);

            appender.appendLine("Date","Email","Initial","Course Code","Room","Time","Section","Shift");

            Timber.e(String.valueOf(allClassesList.size()));

            for (BookedClassDetailsUser bcd : allClassesList) {
                appender.appendLine(
                        getDateStringFromTimestamp(bcd.getReservationDate()),
                        bcd.getTeacherEmail(),
                        bcd.getTeacherInitial(),
                        bcd.getCourseCode(),
                        bcd.getRoomNo(),
                        bcd.getTime(),
                        bcd.getSection(),
                        bcd.getShift()
                );
            }

            appender.endLine();

            Timber.e(readStringFromFile(file));

            saveFile(uri,readStringFromFile(file));

        } catch (IOException e) {
            Timber.e(e);
        }

    }

    private String readStringFromFile(File file) {

        StringBuilder result = new StringBuilder();

        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader buffReader = new BufferedReader(isr);

            String line = buffReader.readLine();

            while (line != null) {
                result.append(line);
                line = buffReader.readLine();
            }

            fis.close();
            isr.close();
            buffReader.close();

        } catch (IOException e) {
            Timber.e(e);
        }

        return result.toString();

    }

    private boolean checkDateValidity() {
        return startTimeStamp != null && endTimeStamp != null && endTimeStamp.compareTo(startTimeStamp) > 0;
    }

    private void pickStartDate() {

        Calendar calendar = Calendar.getInstance();

        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        int month = calendar.get(Calendar.MONTH);

        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

                Calendar calendar = Calendar.getInstance();

                calendar.set(year, month, dayOfMonth);

                startDate.setText(dateFormatter.format(calendar.getTime()));

                startTimeStamp = new Timestamp(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).getTime());

            }
        }, year, month, dayOfMonth);

        datePicker.show();

    }

    private void pickEndDate() {

        Calendar calendar = Calendar.getInstance();

        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        int month = calendar.get(Calendar.MONTH);

        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                endDate.setText(dateFormatter.format(calendar.getTime()));

                endTimeStamp = new Timestamp(new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).getTime());

            }
        }, year, month, dayOfMonth);

        datePicker.show();

    }

    private void showProgressbar(boolean visible) {
        if (visible) {
            progressBar.setVisibility(View.VISIBLE);
            loadingContent.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            loadingContent.setVisibility(View.GONE);
        }
    }

    private String getDateStringFromTimestamp(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getSeconds() * 1000);
        DateFormat dateFormatter = new SimpleDateFormat("d MMM, yyyy");
        return dateFormatter.format(calendar.getTime());
    }

    private void makeToast(String text) {
        if (getContext() != null) {
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    private class CSVStringWriter   {

        private StringBuilder csvString = new StringBuilder();

        public void addLine(String... str) {
            for (int i = 0; i < str.length; i++) {
                if (i != str.length - 1) {
                    if (str[i].contains(",")) {
                        csvString.append(getCommaFixedString(str[i])).append(",");
                    } else {
                        csvString.append(str[i]).append(",");
                    }
                } else {
                    csvString.append(str[i]);
                }
            }
            csvString.append("\n");
        }

        private String getCommaFixedString(String string) {

            return string.replace(",","");

        }

        public String getCSVString() {
            return csvString.toString();
        }

    }
}
