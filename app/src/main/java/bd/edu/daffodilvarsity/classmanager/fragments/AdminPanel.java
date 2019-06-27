package bd.edu.daffodilvarsity.classmanager.fragments;


import android.Manifest;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.tutorialsandroid.filepicker.controller.DialogSelectionListener;
import com.github.tutorialsandroid.filepicker.model.DialogConfigs;
import com.github.tutorialsandroid.filepicker.model.DialogProperties;
import com.github.tutorialsandroid.filepicker.view.FilePickerDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ClassDetails;
import de.siegmar.fastcsv.reader.CsvParser;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminPanel extends Fragment implements EasyPermissions.PermissionCallbacks, View.OnClickListener {

    private final int REQUEST_STORAGE_PERMISSION = 4631;

    private Spinner mCampusSelector;
    private Spinner mShiftSelector;
    private TextView mSelectTextView;

    private String mFilePath = "";

    private FirebaseFirestore db;

    //Upload Buttons
    private Button mUpload1;
    private Button mUpload2;
    private Button mUpload3;
    private Button mUpload4;
    private Button mUpload5;
    private Button mUpload6;
    private Button mUpload7;

    //No Of ClassesList uploaded
    private TextView classes1;
    private TextView classes2;
    private TextView classes3;
    private TextView classes4;
    private TextView classes5;
    private TextView classes6;
    private TextView classes7;

    //ProgressBar
    private ProgressBar progressBar1;
    private ProgressBar progressBar2;
    private ProgressBar progressBar3;
    private ProgressBar progressBar4;
    private ProgressBar progressBar5;
    private ProgressBar progressBar6;
    private ProgressBar progressBar7;

    //Per day class list
    private ArrayList<ClassDetails> saturdayClasses = new ArrayList<>();
    private ArrayList<ClassDetails> sundayClasses = new ArrayList<>();
    private ArrayList<ClassDetails> mondayClasses = new ArrayList<>();
    private ArrayList<ClassDetails> tuesdayClasses = new ArrayList<>();
    private ArrayList<ClassDetails> wednesdayClasses = new ArrayList<>();
    private ArrayList<ClassDetails> thursdayClasses = new ArrayList<>();
    private ArrayList<ClassDetails> fridayClasses = new ArrayList<>();

    //Progress
    private int p1 = 0;
    private int p2 = 0;
    private int p3 = 0;
    private int p4 = 0;
    private int p5 = 0;
    private int p6 = 0;
    private int p7 = 0;

    public AdminPanel() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_panel, container, false);

        initializeVariables(view);
        addSpinnerItems();
        return view;
    }

    private void initializeVariables(View view) {

        mCampusSelector = view.findViewById(R.id.spinner_1);
        mShiftSelector = view.findViewById(R.id.spinner_2);
        mSelectTextView = view.findViewById(R.id.select_file);

        view.findViewById(R.id.btn_select_file).setOnClickListener(this);

        view.findViewById(R.id.parse_file).setOnClickListener(this);

        mUpload1 = view.findViewById(R.id.upload_1);
        mUpload2 = view.findViewById(R.id.upload_2);
        mUpload3 = view.findViewById(R.id.upload_3);
        mUpload4 = view.findViewById(R.id.upload_4);
        mUpload5 = view.findViewById(R.id.upload_5);
        mUpload6 = view.findViewById(R.id.upload_6);
        mUpload7 = view.findViewById(R.id.upload_7);

        mUpload1.setOnClickListener(this);
        mUpload2.setOnClickListener(this);
        mUpload3.setOnClickListener(this);
        mUpload4.setOnClickListener(this);
        mUpload5.setOnClickListener(this);
        mUpload6.setOnClickListener(this);
        mUpload7.setOnClickListener(this);

        progressBar1 = view.findViewById(R.id.progress_bar_1);
        progressBar2 = view.findViewById(R.id.progress_bar_2);
        progressBar3 = view.findViewById(R.id.progress_bar_3);
        progressBar4 = view.findViewById(R.id.progress_bar_4);
        progressBar5 = view.findViewById(R.id.progress_bar_5);
        progressBar6 = view.findViewById(R.id.progress_bar_6);
        progressBar7 = view.findViewById(R.id.progress_bar_7);

        classes1 = view.findViewById(R.id.classes_1);
        classes2 = view.findViewById(R.id.classes_2);
        classes3 = view.findViewById(R.id.classes_3);
        classes4 = view.findViewById(R.id.classes_4);
        classes5 = view.findViewById(R.id.classes_5);
        classes6 = view.findViewById(R.id.classes_6);
        classes7 = view.findViewById(R.id.classes_7);

        db = FirebaseFirestore.getInstance();
    }

    private void addSpinnerItems() {

        String[] campusItems = new String[]{"Main Campus"};
        String[] shiftItems = new String[]{"Day", "Evening"};

        ArrayAdapter<String> adapterCampus = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, campusItems);

        adapterCampus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mCampusSelector.setAdapter(adapterCampus);

        ArrayAdapter<String> adapterShift = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, shiftItems);

        adapterShift.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mShiftSelector.setAdapter(adapterShift);

    }

    private void selectFileFromStorage() {

        if (!hasStoragePermission()) {
            return;
        }

        DialogProperties properties = new DialogProperties();

        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = new String[]{"csv", "CSV"};

        FilePickerDialog dialog = new FilePickerDialog(getContext(), properties);
        dialog.setTitle("Select a CSV File");

        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                mSelectTextView.setText(files[0]);
                mFilePath = files[0];
            }
        });

        dialog.show();

    }

    private void parseRoutine() {

        if (mFilePath.isEmpty()) {
            Toast.makeText(getContext(), "Please select a CSV file", Toast.LENGTH_SHORT).show();
            return;
        }

        clearClassList();

        File file = new File(mFilePath);

        String campus = mCampusSelector.getSelectedItem().toString();
        String shift = mShiftSelector.getSelectedItem().toString();

        if (campus.equals("Main Campus") && shift.equals("Day")) {

            saturdayClasses = readRoutineDay(file, "Saturday", "Sunday");
            sundayClasses = readRoutineDay(file, "Sunday", "Monday");
            mondayClasses = readRoutineDay(file, "Monday", "Tuesday");
            tuesdayClasses = readRoutineDay(file, "Tuesday", "Wednesday");
            wednesdayClasses = readRoutineDay(file, "Wednesday", "Thursday");
            thursdayClasses = readRoutineDay(file, "Thursday", "Nothing");

            updateClassSize();
            enableUploadButtons(true);
            resetButtonText();
            resetProgressBarVisiblity();

        } else if (campus.equals("Main Campus") && shift.equals("Evening")) {
            saturdayClasses = readRoutineEvening(file, "Saturday", "Sunday");
            sundayClasses = readRoutineEvening(file, "Sunday", "Monday");
            mondayClasses = readRoutineEvening(file, "Monday", "Tuesday");
            tuesdayClasses = readRoutineEvening(file, "Tuesday", "Wednesday");
            wednesdayClasses = readRoutineEvening(file, "Wednesday", "Thursday");
            thursdayClasses = readRoutineEvening(file, "Thursday", "Nothing");

            updateClassSize();
            enableUploadButtons(true);
            resetButtonText();
            resetProgressBarVisiblity();
        }
    }

    private ArrayList<ClassDetails> readRoutineDay(File file, String startDay, String endDay) {

        CsvReader csvReader = new CsvReader();

        csvReader.setSkipEmptyRows(true);

        ArrayList<ClassDetails> classes = new ArrayList<>();

        try {
            CsvParser parser = csvReader.parse(file, StandardCharsets.UTF_8);

            CsvRow row;

            //For skipping other day routines
            while ((row = parser.nextRow()) != null) {

                if (!row.getField(0).equals(startDay)) {
                    continue;
                }

                if (row.getField(0).equals(startDay)) {
                    parser.nextRow();
                    parser.nextRow();
                    break;
                }
            }

            while ((row = parser.nextRow()) != null) {

                if (row.getField(0).equals(endDay) || row.getField(0).equals("Lab (Electrical Circuit, Digital Electronics, Electronic Devices and Circuits)")) {
                    parser.nextRow();
                    break;
                }

                ClassDetails cd1 = new ClassDetails(row.getField(0).trim(), row.getField(1).trim(), row.getField(2).trim());
                ClassDetails cd2 = new ClassDetails(row.getField(3).trim(), row.getField(4).trim(), row.getField(5).trim());
                ClassDetails cd3 = new ClassDetails(row.getField(6).trim(), row.getField(7).trim(), row.getField(8).trim());
                ClassDetails cd4 = new ClassDetails(row.getField(9).trim(), row.getField(10).trim(), row.getField(11).trim());
                ClassDetails cd5 = new ClassDetails(row.getField(12).trim(), row.getField(13).trim(), row.getField(14).trim());
                ClassDetails cd6 = new ClassDetails(row.getField(15).trim(), row.getField(16).trim(), row.getField(17).trim());

                if (!cd1.getRoom().isEmpty()) {
                    cd1.setTime("08:30AM-10:00AM");
                    if (!cd1.getCourseCode().isEmpty()) {
                        cd1.setSection(getModifiedSection(cd1.getCourseCode()));
                        cd1.setCourseCode(getModifiedCourseCode(cd1.getCourseCode()));
                        cd1.setCourseName(getStringResourceByName(cd1.getCourseCode()));
                    }
                    cd1.setPriority(1f);
                    classes.add(cd1);
                }

                if (!cd2.getRoom().isEmpty()) {
                    cd2.setTime("10:00AM-11:30AM");
                    if (!cd2.getCourseCode().isEmpty()) {
                        cd2.setSection(getModifiedSection(cd2.getCourseCode()));
                        cd2.setCourseCode(getModifiedCourseCode(cd2.getCourseCode()));
                        cd2.setCourseName(getStringResourceByName(cd2.getCourseCode()));
                    }
                    cd2.setPriority(2f);
                    classes.add(cd2);
                }

                if (!cd3.getRoom().isEmpty()) {
                    cd3.setTime("11.30AM-01:00PM");
                    if (!cd3.getCourseCode().isEmpty()) {
                        cd3.setSection(getModifiedSection(cd3.getCourseCode()));
                        cd3.setCourseCode(getModifiedCourseCode(cd3.getCourseCode()));
                        cd3.setCourseName(getStringResourceByName(cd3.getCourseCode()));
                    }
                    cd3.setPriority(3f);
                    classes.add(cd3);
                }

                if (!cd4.getRoom().isEmpty()) {
                    cd4.setTime("01:00PM-02:30PM");
                    if (!cd4.getCourseCode().isEmpty()) {
                        cd4.setSection(getModifiedSection(cd4.getCourseCode()));
                        cd4.setCourseCode(getModifiedCourseCode(cd4.getCourseCode()));
                        cd4.setCourseName(getStringResourceByName(cd4.getCourseCode()));
                    }
                    cd4.setPriority(4f);
                    classes.add(cd4);
                }

                if (!cd5.getRoom().isEmpty()) {
                    cd5.setTime("02:30PM-04:00PM");
                    if (!cd5.getCourseCode().isEmpty()) {
                        cd5.setSection(getModifiedSection(cd5.getCourseCode()));
                        cd5.setCourseCode(getModifiedCourseCode(cd5.getCourseCode()));
                        cd5.setCourseName(getStringResourceByName(cd5.getCourseCode()));
                    }
                    cd5.setPriority(5f);
                    classes.add(cd5);
                }

                if (!cd6.getRoom().isEmpty()) {
                    cd6.setTime("04:00PM-05:30PM");
                    if (!cd6.getCourseCode().isEmpty()) {
                        cd6.setSection(getModifiedSection(cd6.getCourseCode()));
                        cd6.setCourseCode(getModifiedCourseCode(cd6.getCourseCode()));
                        cd6.setCourseName(getStringResourceByName(cd6.getCourseCode()));
                    }
                    cd6.setPriority(6f);
                    classes.add(cd6);
                }
            }

            while ((row = parser.nextRow()) != null) {

                if (row.getField(0).equals(endDay)) {
                    break;
                }

                ClassDetails e1 = new ClassDetails(row.getField(0).trim(), row.getField(1).trim(), row.getField(2).trim());
                ClassDetails e2 = new ClassDetails(row.getField(0).trim(), row.getField(3).trim(), row.getField(4).trim());
                ClassDetails e3 = new ClassDetails(row.getField(0).trim(), row.getField(5).trim(), row.getField(6).trim());
                ClassDetails e4 = new ClassDetails(row.getField(0).trim(), row.getField(7).trim(), row.getField(8).trim());

                if (!e1.getRoom().isEmpty()) {
                    e1.setTime("09:00AM-11:00AM");
                    if (!e1.getCourseCode().isEmpty()) {
                        e1.setSection(getModifiedSection(e1.getCourseCode()));
                        e1.setCourseCode(getModifiedCourseCode(e1.getCourseCode()));
                        e1.setCourseName(getStringResourceByName(e1.getCourseCode()));
                    }
                    e1.setPriority(1.5f);
                    classes.add(e1);
                }

                if (!e2.getRoom().isEmpty()) {
                    e2.setTime("11:00AM-01:00PM");
                    if (!e2.getCourseCode().isEmpty()) {
                        e2.setSection(getModifiedSection(e2.getCourseCode()));
                        e2.setCourseCode(getModifiedCourseCode(e2.getCourseCode()));
                        e2.setCourseName(getStringResourceByName(e2.getCourseCode()));
                    }
                    e2.setPriority(2.5f);
                    classes.add(e2);
                }

                if (!e3.getRoom().isEmpty()) {
                    e3.setTime("01:00PM-03:00PM");
                    if (!e3.getCourseCode().isEmpty()) {
                        e3.setSection(getModifiedSection(e3.getCourseCode()));
                        e3.setCourseCode(getModifiedCourseCode(e3.getCourseCode()));
                        e3.setCourseName(getStringResourceByName(e3.getCourseCode()));
                    }
                    e3.setPriority(4.5f);
                    classes.add(e3);
                }

                if (!e4.getRoom().isEmpty()) {
                    e4.setTime("03:00PM-05:00PM");
                    if (!e4.getCourseCode().isEmpty()) {
                        e4.setSection(getModifiedSection(e4.getCourseCode()));
                        e4.setCourseCode(getModifiedCourseCode(e4.getCourseCode()));
                        e4.setCourseName(getStringResourceByName(e4.getCourseCode()));
                    }
                    e4.setPriority(5.5f);
                    classes.add(e4);
                }

            }
        } catch (IOException e) {
            Log.e("CSV Parser Error Log", "Exception", e);
        }

        return classes;
    }

    private ArrayList<ClassDetails> readRoutineEvening(File file, String startDay, String endDay) {

        CsvReader csvReader = new CsvReader();

        csvReader.setSkipEmptyRows(true);

        ArrayList<ClassDetails> classes = new ArrayList<>();

        try {

            CsvParser parser = csvReader.parse(file, StandardCharsets.UTF_8);

            CsvRow row;

            boolean startCounting = false;

            while ((row = parser.nextRow()) != null) {

                if (row.getField(0).equals(startDay)) {
                    startCounting = true;
                } else if (row.getField(0).equals(endDay)) {
                    break;
                }

                if (startCounting) {

                    ClassDetails cd1 = new ClassDetails(row.getField(1).trim(), row.getField(2).trim(), row.getField(3).trim(), row.getField(5).trim());
                    ClassDetails cd2 = new ClassDetails(row.getField(6).trim(), row.getField(7).trim(), row.getField(8).trim(), row.getField(10).trim());

                    if (!cd1.getRoom().isEmpty()) {
                        cd1.setTime("6.00PM-7.30PM");
                        if (!cd1.getCourseCode().isEmpty()) {
                            cd1.setSection(getModifiedSection(cd1.getCourseCode()));
                            cd1.setCourseCode(getModifiedCourseCode(cd1.getCourseCode()));
                        } else {
                            cd1.setSection("");
                        }
                        cd1.setPriority(7f);
                        classes.add(cd1);
                    }

                    if (!cd2.getRoom().isEmpty()) {
                        cd2.setTime("7.30PM-9.00PM");
                        if (!cd2.getCourseCode().isEmpty()) {
                            cd2.setSection(getModifiedSection(cd2.getCourseCode()));
                            cd2.setCourseCode(getModifiedCourseCode(cd2.getCourseCode()));
                        } else {
                            cd2.setSection("");
                        }
                        cd2.setPriority(8f);
                        classes.add(cd2);
                    }
                }
            }

        } catch (IOException e) {
            Log.e("CSV Parser Error Log", "Exception", e);
        }
        return classes;
    }

    private void up1(CollectionReference collectionRef) {

        if (saturdayClasses.size() < 1) {
            Toast.makeText(getContext(), "No Classes", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar1.setVisibility(View.VISIBLE);
        progressBar1.setMax(saturdayClasses.size());

        mUpload1.setEnabled(false);
        mUpload1.setText("Uploading");

        for (ClassDetails classDetails : saturdayClasses) {
            collectionRef
                    .add(classDetails)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            p1++;
                            progressBar1.setProgress(p1);
                            classes1.setText("Uploaded " + p1 + "/" + saturdayClasses.size());
                            if (p1 == saturdayClasses.size()) {
                                mUpload1.setEnabled(false);
                                mUpload1.setText("Uploaded");
                                p1 = 0;
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Some error occured", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void up2(CollectionReference collectionRef) {

        if (sundayClasses.size() < 1) {
            Toast.makeText(getContext(), "No mClasses", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar2.setVisibility(View.VISIBLE);
        progressBar2.setMax(sundayClasses.size());

        mUpload2.setEnabled(false);
        mUpload2.setText("Uploading");


        for (ClassDetails classDetails : sundayClasses) {

            collectionRef
                    .add(classDetails)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            p2++;
                            progressBar2.setProgress(p2);
                            classes2.setText("Uploaded " + p2 + "/" + sundayClasses.size());
                            if (p2 == sundayClasses.size()) {
                                mUpload2.setEnabled(false);
                                mUpload2.setText("Uploaded");
                                p2 = 0;
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Some error occured", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void up3(CollectionReference collectionRef) {

        if (mondayClasses.size() < 1) {
            Toast.makeText(getContext(), "No mClasses", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar3.setVisibility(View.VISIBLE);
        progressBar3.setMax(mondayClasses.size());

        mUpload3.setEnabled(false);
        mUpload3.setText("Uploading");

        for (ClassDetails classDetails : mondayClasses) {

            collectionRef
                    .add(classDetails)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            p3++;
                            progressBar3.setProgress(p3);
                            classes3.setText("Uploaded " + p3 + "/" + mondayClasses.size());
                            if (p3 == mondayClasses.size()) {
                                mUpload3.setEnabled(false);
                                mUpload3.setText("Uploaded");
                                p3 = 0;
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Some error occured", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void up4(CollectionReference collectionRef) {

        if (tuesdayClasses.size() < 1) {
            Toast.makeText(getContext(), "No mClasses", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar4.setVisibility(View.VISIBLE);
        progressBar4.setMax(tuesdayClasses.size());

        mUpload4.setEnabled(false);
        mUpload4.setText("Uploading");

        for (ClassDetails classDetails : tuesdayClasses) {

            collectionRef
                    .add(classDetails)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            p4++;
                            progressBar4.setProgress(p4);
                            classes4.setText("Uploaded " + p4 + "/" + tuesdayClasses.size());
                            if (p4 == tuesdayClasses.size()) {
                                mUpload4.setEnabled(false);
                                mUpload4.setText("Uploaded");
                                p4 = 0;
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Some error occured", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void up5(CollectionReference collectionRef) {

        if (wednesdayClasses.size() < 1) {
            Toast.makeText(getContext(), "No mClasses", Toast.LENGTH_SHORT).show();
            return;
        }

        if (sundayClasses.size() < 1) {
            Toast.makeText(getContext(), "No mClasses", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar5.setVisibility(View.VISIBLE);
        progressBar5.setMax(wednesdayClasses.size());

        mUpload5.setEnabled(false);
        mUpload5.setText("Uploading");

        for (ClassDetails classDetails : wednesdayClasses) {

            collectionRef
                    .add(classDetails)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            p5++;
                            progressBar5.setProgress(p5);
                            classes5.setText("Uploaded " + p5 + "/" + wednesdayClasses.size());
                            if (p5 == wednesdayClasses.size()) {
                                mUpload5.setEnabled(false);
                                mUpload5.setText("Uploaded");
                                p5 = 0;
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Some error occured", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void up6(CollectionReference collectionRef) {

        if (thursdayClasses.size() < 1) {
            Toast.makeText(getContext(), "No mClasses", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar6.setVisibility(View.VISIBLE);
        progressBar6.setMax(thursdayClasses.size());

        mUpload6.setEnabled(false);
        mUpload6.setText("Uploading");

        for (ClassDetails classDetails : thursdayClasses) {

            collectionRef
                    .add(classDetails)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            p6++;
                            progressBar6.setProgress(p6);
                            classes6.setText("Uploaded " + p6 + "/" + thursdayClasses.size());
                            if (p6 == thursdayClasses.size()) {
                                mUpload6.setEnabled(false);
                                mUpload6.setText("Uploaded");
                                p6 = 0;
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Some error occured", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void up7(CollectionReference collectionRef) {

        if (fridayClasses.size() < 1) {
            Toast.makeText(getContext(), "No mClasses", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar7.setVisibility(View.VISIBLE);
        progressBar7.setMax(fridayClasses.size());

        mUpload7.setEnabled(false);
        mUpload7.setText("Uploading");

        for (ClassDetails classDetails : fridayClasses) {

            collectionRef
                    .add(classDetails)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            p7++;
                            progressBar7.setProgress(p7);
                            classes7.setText("Uploaded " + p7 + "/" + fridayClasses.size());
                            if (p7 == fridayClasses.size()) {
                                mUpload7.setEnabled(false);
                                mUpload7.setText("Uploaded");
                                p7 = 0;
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Some error occured", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    //To separate course code and section
    private String getModifiedCourseCode(String str) {
        int length = str.length();
        return str.substring(0, length - 3);
    }

    //To separate course code and section
    private String getModifiedSection(String str) {
        int length = str.length();
        return String.valueOf(str.charAt(length - 2));
    }

    private void enableUploadButtons(boolean enabled) {

        mUpload1.setEnabled(enabled);
        mUpload2.setEnabled(enabled);
        mUpload3.setEnabled(enabled);
        mUpload4.setEnabled(enabled);
        mUpload5.setEnabled(enabled);
        mUpload6.setEnabled(enabled);
        if (fridayClasses.size() > 0) {
            mUpload7.setEnabled(true);
        } else {
            mUpload7.setEnabled(false);
        }

    }

    private void resetButtonText() {
        mUpload1.setText("Upload");
        mUpload2.setText("Upload");
        mUpload3.setText("Upload");
        mUpload4.setText("Upload");
        mUpload5.setText("Upload");
        mUpload6.setText("Upload");
        mUpload7.setText("Upload");
    }

    private void resetProgressBarVisiblity() {
        progressBar1.setVisibility(View.GONE);
        progressBar2.setVisibility(View.GONE);
        progressBar3.setVisibility(View.GONE);
        progressBar4.setVisibility(View.GONE);
        progressBar5.setVisibility(View.GONE);
        progressBar6.setVisibility(View.GONE);
        progressBar7.setVisibility(View.GONE);
    }

    private void clearClassList() {
        saturdayClasses.clear();
        sundayClasses.clear();
        mondayClasses.clear();
        tuesdayClasses.clear();
        wednesdayClasses.clear();
        thursdayClasses.clear();
        fridayClasses.clear();
        updateClassSize();
    }

    private void updateClassSize() {
        classes1.setText(saturdayClasses.size() + " Classes");
        classes2.setText(sundayClasses.size() + " Classes");
        classes3.setText(mondayClasses.size() + " Classes");
        classes4.setText(tuesdayClasses.size() + " Classes");
        classes5.setText(wednesdayClasses.size() + " Classes");
        classes6.setText(thursdayClasses.size() + " Classes");
        classes7.setText(fridayClasses.size() + " Classes");
    }

    private String getStringResourceByName(String resName) {
        String packageName = "bd.edu.daffodilvarsity.classmanager";
        int resId = getContext().getResources().getIdentifier(resName,"string",packageName);

        String res = null;

        try {
            res = getContext().getResources().getString(resId);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }

        if(res!=null)   {
            return res;
        }
        return "";
    }

    private boolean hasStoragePermission() {
        String[] perms = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(getContext(), perms)) {
            EasyPermissions.requestPermissions(this, "Storage permission is needed to select CSV file.", REQUEST_STORAGE_PERMISSION, perms);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION:
                selectFileFromStorage();
                break;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onClick(View v) {

        String shift = mShiftSelector.getSelectedItem().toString();

        CollectionReference saturdayRef;
        CollectionReference sundayRef;
        CollectionReference mondayRef;
        CollectionReference tuesdayRef;
        CollectionReference wednesdayRef;
        CollectionReference thursdayRef;
        CollectionReference fridayRef;

        if (shift.equals("Day")) {
            saturdayRef = db.collection("main_campus/classes_day/saturday/");
            sundayRef = db.collection("main_campus/classes_day/sunday/");
            mondayRef = db.collection("main_campus/classes_day/monday/");
            tuesdayRef = db.collection("main_campus/classes_day/tuesday/");
            wednesdayRef = db.collection("main_campus/classes_day/wednesday/");
            thursdayRef = db.collection("main_campus/classes_day/thursday/");
            fridayRef = db.collection("main_campus/classes_day/friday/");
        } else {
            saturdayRef = db.collection("main_campus/classes_evening/saturday/");
            sundayRef = db.collection("main_campus/classes_evening/sunday/");
            mondayRef = db.collection("main_campus/classes_evening/monday/");
            tuesdayRef = db.collection("main_campus/classes_evening/tuesday/");
            wednesdayRef = db.collection("main_campus/classes_evening/wednesday/");
            thursdayRef = db.collection("main_campus/classes_evening/thursday/");
            fridayRef = db.collection("main_campus/classes_evening/friday/");
        }

        switch (v.getId()) {
            case R.id.btn_select_file:
                selectFileFromStorage();
                break;
            case R.id.parse_file:
                parseRoutine();
                break;
            case R.id.upload_1:
                up1(saturdayRef);
                break;
            case R.id.upload_2:
                up2(sundayRef);
                break;
            case R.id.upload_3:
                up3(mondayRef);
                break;
            case R.id.upload_4:
                up4(tuesdayRef);
                break;
            case R.id.upload_5:
                up5(wednesdayRef);
                break;
            case R.id.upload_6:
                up6(thursdayRef);
                break;
            case R.id.upload_7:
                up7(fridayRef);
                break;
        }
    }
}
