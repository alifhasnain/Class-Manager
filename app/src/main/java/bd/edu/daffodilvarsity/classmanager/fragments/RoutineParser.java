package bd.edu.daffodilvarsity.classmanager.fragments;


import android.Manifest;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.developer.filepicker.controller.DialogSelectionListener;
import com.developer.filepicker.model.DialogConfigs;
import com.developer.filepicker.model.DialogProperties;
import com.developer.filepicker.view.FilePickerDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
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
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class RoutineParser extends Fragment implements EasyPermissions.PermissionCallbacks, View.OnClickListener {

    private final int REQUEST_STORAGE_PERMISSION = 4631;

    private Spinner mCampusSelector;
    private Spinner mShiftSelector;
    private TextView mSelectTextView;

    private String mFilePath = "";

    private FirebaseFirestore db;

    private TextView uploadProgressText;

    private ProgressBar uploadProgressBar;

    private int uploadProgress = 0;

    private int filesToUpload = 0;

    private Button uploadAll;

    private Button uploadJson;

    private EditText routineVersion;

    private ArrayList<ClassDetails> allNonEmptyClassDetails = new ArrayList<>();

    private ArrayList<ClassDetails> allEmptyClassDetails = new ArrayList<>();

    public RoutineParser() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_routine_parser, container, false);

        initializeVariables(view);
        addSpinnerItems();

        return view;
    }

    private void initializeVariables(View view) {

        mCampusSelector = view.findViewById(R.id.spinner_1);
        mShiftSelector = view.findViewById(R.id.spinner_2);
        mSelectTextView = view.findViewById(R.id.select_file);

        uploadProgressBar = view.findViewById(R.id.upload_progress);
        uploadProgressText = view.findViewById(R.id.total_uploaded);
        uploadAll = view.findViewById(R.id.upload_all);
        uploadAll.setOnClickListener(this);
        uploadJson = view.findViewById(R.id.upload_json);
        uploadJson.setOnClickListener(this);
        routineVersion = view.findViewById(R.id.routine_version);

        view.findViewById(R.id.btn_select_file).setOnClickListener(this);

        view.findViewById(R.id.parse_file).setOnClickListener(this);

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

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            makeToast("Android Version Oreo is needed to parse file.");
            return;
        }

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

        allNonEmptyClassDetails.clear();
        allEmptyClassDetails.clear();
        filesToUpload = 0;
        uploadProgress = 0;
        uploadProgressBar.setProgress(0);

        File file = new File(mFilePath);

        String campus = mCampusSelector.getSelectedItem().toString();
        String shift = mShiftSelector.getSelectedItem().toString();

        if (campus.equals("Main Campus") && shift.equals("Day")) {

            readRoutineDay(file, "Saturday", "Sunday", "Day");
            readRoutineDay(file, "Sunday", "Monday", "Day");
            readRoutineDay(file, "Monday", "Tuesday", "Day");
            readRoutineDay(file, "Tuesday", "Wednesday", "Day");
            readRoutineDay(file, "Wednesday", "Thursday", "Day");
            readRoutineDay(file, "Thursday", "Nothing", "Day");

            if (allEmptyClassDetails.size() > 0 || allNonEmptyClassDetails.size() > 0) {

                filesToUpload = allEmptyClassDetails.size();

                uploadAll.setEnabled(true);

                uploadProgressText.setText("Total : " + filesToUpload);

            }

        } else if (campus.equals("Main Campus") && shift.equals("Evening")) {

            readRoutineEvening(file, "Saturday", "Sunday");
            readRoutineEvening(file, "Sunday", "Monday");
            readRoutineEvening(file, "Monday", "Tuesday");
            readRoutineEvening(file, "Tuesday", "Wednesday");
            readRoutineEvening(file, "Wednesday", "Thursday");
            readRoutineEvening(file, "Thursday", "Nothing");

            if (allEmptyClassDetails.size() > 0 || allNonEmptyClassDetails.size() > 0) {

                filesToUpload = allEmptyClassDetails.size();

                uploadAll.setEnabled(true);

                uploadProgressText.setText("Total : " + filesToUpload);

            }

        }
    }

    private void upload() {

        //CollectionReference allNonEmptyClassesRef = db.collection("/main_campus/");
        CollectionReference allEmptyClassesRef = db.collection("/main_campus_empty_classes/");

        uploadProgressBar.setMax(filesToUpload);
        uploadAll.setEnabled(false);

        /*for (int i = 0; i < allNonEmptyClassDetails.size(); i++) {

            allNonEmptyClassesRef.add(allNonEmptyClassDetails.get(i)).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    uploadProgress++;
                    uploadProgressBar.setProgress(uploadProgress);
                    uploadProgressText.setText(uploadProgress + "/" + filesToUpload);

                    if (uploadProgress == filesToUpload) {
                        Toast.makeText(getContext(), "All files uploaded successfully.", Toast.LENGTH_SHORT).show();
                        uploadAll.setEnabled(false);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Failed to upload!", Toast.LENGTH_SHORT).show();
                }
            });

        }*/

        for (int i = 0; i < allEmptyClassDetails.size(); i++) {

            allEmptyClassesRef.add(allEmptyClassDetails.get(i)).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    uploadProgress++;
                    uploadProgressBar.setProgress(uploadProgress);
                    uploadProgressText.setText(uploadProgress + "/" + filesToUpload);

                    if (uploadProgress == filesToUpload) {
                        Toast.makeText(getContext(), "All files uploaded successfully.", Toast.LENGTH_SHORT).show();
                        uploadAll.setEnabled(false);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Failed to upload!", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void checkAndUploadToFirebaseStorage() {

        final ArrayList<ClassDetails> allClasses = new ArrayList<>();
        allClasses.addAll(allNonEmptyClassDetails);
        allClasses.addAll(allEmptyClassDetails);

        if (allClasses.size() == 0) {
            makeToast("No classes on the list.");
            return;
        }

        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("Are you sure to proceed?")
                .setMessage("You are uploading total " + allClasses.size() + " classes as JSON file.")
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        uploadToFirebase(allClasses);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        alertDialog.show();

    }

    private void uploadToFirebase(ArrayList<ClassDetails> allClasses) {
        String allClassesJsonString = getJsonRoutine(allClasses);

        String campus = mCampusSelector.getSelectedItem().toString();
        String shift = mShiftSelector.getSelectedItem().toString();

        DocumentReference routineDoc = FirebaseFirestore.getInstance().document("/routine/routine");

        uploadJson.setEnabled(false);

        if (campus.equals("Main Campus") && shift.equals("Day")) {
            routineDoc.update("day", allClassesJsonString).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    makeToast("Successfully saved!");
                    uploadJson.setEnabled(true);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    makeToast("Failed to save.\nPlease try again.");
                    uploadJson.setEnabled(true);
                    Timber.e(e);
                }
            });
        } else if (campus.equals("Main Campus") && shift.equals("Evening")) {
            routineDoc.update("evening", allClassesJsonString).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    makeToast("Successfully saved!");
                    uploadJson.setEnabled(true);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    makeToast("Failed to save.\nPlease try again.");
                    uploadJson.setEnabled(true);
                    Timber.e(e);
                }
            });
        }
    }

    /*private void uploadToFirebaseStorage(ArrayList<ClassDetails> allClasses) {

        String allClassesJsonString = getJsonRoutine(allClasses);

        byte[] allClassByteArray = allClassesJsonString.getBytes(StandardCharsets.UTF_8);

        String campus = mCampusSelector.getSelectedItem().toString();
        String shift = mShiftSelector.getSelectedItem().toString();

        StorageReference routineToUpload = FirebaseStorage.getInstance().getReference();

        if (campus.equals("Main Campus") && shift.equals("Day")) {
            routineToUpload = FirebaseStorage.getInstance().getReference().child("main_campus/routine_day.txt");
        } else if (campus.equals("Main Campus") && shift.equals("Evening")) {
            routineToUpload = FirebaseStorage.getInstance().getReference().child("main_campus/routine_evening.txt");
        }

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("version", routineVersion.getText().toString())
                .build();

        UploadTask uploadTask = routineToUpload.putBytes(allClassByteArray, metadata);
        uploadJson.setEnabled(false);

        uploadTask.addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                makeToast("Successfully saved!");
                uploadJson.setEnabled(true);
            }
        }).addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                makeToast("Failed to save.\nPlease try again.");
                uploadJson.setEnabled(true);
            }
        });
    }*/

    private void readRoutineDay(File file, String startDay, String endDay, String shift) {

        CsvReader csvReader = new CsvReader();

        csvReader.setSkipEmptyRows(true);

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

                if (row.getField(0).equals(endDay) || row.getField(0).equals("Electrical Lab")) {
                    parser.nextRow();
                    break;
                }

                ClassDetails cd1 = new ClassDetails(row.getField(0).trim(), row.getField(1).trim(), row.getField(2).trim(), shift, startDay);
                ClassDetails cd2 = new ClassDetails(row.getField(3).trim(), row.getField(4).trim(), row.getField(5).trim(), shift, startDay);
                ClassDetails cd3 = new ClassDetails(row.getField(6).trim(), row.getField(7).trim(), row.getField(8).trim(), shift, startDay);
                ClassDetails cd4 = new ClassDetails(row.getField(9).trim(), row.getField(10).trim(), row.getField(11).trim(), shift, startDay);
                ClassDetails cd5 = new ClassDetails(row.getField(12).trim(), row.getField(13).trim(), row.getField(14).trim(), shift, startDay);
                ClassDetails cd6 = new ClassDetails(row.getField(15).trim(), row.getField(16).trim(), row.getField(17).trim(), shift, startDay);

                if (!cd1.getRoom().isEmpty()) {

                    cd1.setTime("08:30AM-10:00AM");
                    cd1.setPriority(1f);

                    if (cd1.getCourseCode().isEmpty()) {
                        allEmptyClassDetails.add(cd1);
                    } else if (!cd1.getCourseCode().isEmpty()) {
                        cd1.setSection(splitSection(cd1.getCourseCode()));
                        cd1.setCourseCode(splitCourseCode(cd1.getCourseCode()));
                        cd1.setCourseName(getStringResourceByName(cd1.getCourseCode()));

                        allNonEmptyClassDetails.add(cd1);

                    }
                }

                if (!cd2.getRoom().isEmpty()) {
                    cd2.setTime("10:00AM-11:30AM");
                    cd2.setPriority(2f);
                    if (cd2.getCourseCode().isEmpty()) {
                        allEmptyClassDetails.add(cd2);
                    } else if (!cd2.getCourseCode().isEmpty()) {
                        cd2.setSection(splitSection(cd2.getCourseCode()));
                        cd2.setCourseCode(splitCourseCode(cd2.getCourseCode()));
                        cd2.setCourseName(getStringResourceByName(cd2.getCourseCode()));

                        allNonEmptyClassDetails.add(cd2);
                    }
                }

                if (!cd3.getRoom().isEmpty()) {
                    cd3.setTime("11.30AM-01:00PM");
                    cd3.setPriority(3f);

                    if (cd3.getCourseCode().isEmpty()) {
                        allEmptyClassDetails.add(cd3);
                    } else if (!cd3.getCourseCode().isEmpty()) {
                        cd3.setSection(splitSection(cd3.getCourseCode()));
                        cd3.setCourseCode(splitCourseCode(cd3.getCourseCode()));
                        cd3.setCourseName(getStringResourceByName(cd3.getCourseCode()));

                        allNonEmptyClassDetails.add(cd3);

                    }
                }

                if (!cd4.getRoom().isEmpty()) {
                    cd4.setTime("01:00PM-02:30PM");
                    cd4.setPriority(4f);

                    if (cd4.getCourseCode().isEmpty()) {
                        allEmptyClassDetails.add(cd4);
                    } else if (!cd4.getCourseCode().isEmpty()) {
                        cd4.setSection(splitSection(cd4.getCourseCode()));
                        cd4.setCourseCode(splitCourseCode(cd4.getCourseCode()));
                        cd4.setCourseName(getStringResourceByName(cd4.getCourseCode()));

                        allNonEmptyClassDetails.add(cd4);

                    }
                }

                if (!cd5.getRoom().isEmpty()) {
                    cd5.setTime("02:30PM-04:00PM");
                    cd5.setPriority(5f);

                    if (cd5.getCourseCode().isEmpty()) {
                        allEmptyClassDetails.add(cd5);
                    } else if (!cd5.getCourseCode().isEmpty()) {
                        cd5.setSection(splitSection(cd5.getCourseCode()));
                        cd5.setCourseCode(splitCourseCode(cd5.getCourseCode()));
                        cd5.setCourseName(getStringResourceByName(cd5.getCourseCode()));

                        allNonEmptyClassDetails.add(cd5);

                    }
                }

                if (!cd6.getRoom().isEmpty()) {
                    cd6.setTime("04:00PM-05:30PM");
                    cd6.setPriority(6f);

                    if (cd6.getCourseCode().isEmpty()) {
                        allEmptyClassDetails.add(cd6);
                    } else if (!cd6.getCourseCode().isEmpty()) {
                        cd6.setSection(splitSection(cd6.getCourseCode()));
                        cd6.setCourseCode(splitCourseCode(cd6.getCourseCode()));
                        cd6.setCourseName(getStringResourceByName(cd6.getCourseCode()));

                        allNonEmptyClassDetails.add(cd6);

                    }
                }
            }

            while ((row = parser.nextRow()) != null) {

                if (row.getField(0).equals(endDay)) {
                    break;
                }

                ClassDetails e1 = new ClassDetails(row.getField(0).trim(), row.getField(1).trim(), row.getField(2).trim(), shift, startDay);
                ClassDetails e2 = new ClassDetails(row.getField(0).trim(), row.getField(3).trim(), row.getField(4).trim(), shift, startDay);
                ClassDetails e3 = new ClassDetails(row.getField(0).trim(), row.getField(5).trim(), row.getField(6).trim(), shift, startDay);
                ClassDetails e4 = new ClassDetails(row.getField(0).trim(), row.getField(7).trim(), row.getField(8).trim(), shift, startDay);

                if (!e1.getRoom().isEmpty()) {
                    e1.setTime("09:00AM-11:00AM");
                    e1.setPriority(1.5f);

                    if (e1.getCourseCode().isEmpty()) {
                        allEmptyClassDetails.add(e1);
                    } else if (!e1.getCourseCode().isEmpty()) {
                        e1.setSection(splitSection(e1.getCourseCode()));
                        e1.setCourseCode(splitCourseCode(e1.getCourseCode()));
                        e1.setCourseName(getStringResourceByName(e1.getCourseCode()));

                        allNonEmptyClassDetails.add(e1);
                    }
                }

                if (!e2.getRoom().isEmpty()) {
                    e2.setTime("11:00AM-01:00PM");
                    e2.setPriority(2.5f);

                    if (e2.getCourseCode().isEmpty()) {
                        allEmptyClassDetails.add(e2);
                    } else if (!e2.getCourseCode().isEmpty()) {
                        e2.setSection(splitSection(e2.getCourseCode()));
                        e2.setCourseCode(splitCourseCode(e2.getCourseCode()));
                        e2.setCourseName(getStringResourceByName(e2.getCourseCode()));

                        allNonEmptyClassDetails.add(e2);
                    }
                }

                if (!e3.getRoom().isEmpty()) {
                    e3.setTime("01:00PM-03:00PM");
                    e3.setPriority(4.5f);

                    if (e3.getCourseCode().isEmpty()) {
                        allEmptyClassDetails.add(e3);
                    } else if (!e3.getCourseCode().isEmpty()) {
                        e3.setSection(splitSection(e3.getCourseCode()));
                        e3.setCourseCode(splitCourseCode(e3.getCourseCode()));
                        e3.setCourseName(getStringResourceByName(e3.getCourseCode()));

                        allNonEmptyClassDetails.add(e3);
                    }
                }

                if (!e4.getRoom().isEmpty()) {
                    e4.setTime("03:00PM-05:00PM");
                    e4.setPriority(5.5f);

                    if (e4.getCourseCode().isEmpty()) {
                        allEmptyClassDetails.add(e4);
                    } else if (!e4.getCourseCode().isEmpty()) {
                        e4.setSection(splitSection(e4.getCourseCode()));
                        e4.setCourseCode(splitCourseCode(e4.getCourseCode()));
                        e4.setCourseName(getStringResourceByName(e4.getCourseCode()));

                        allNonEmptyClassDetails.add(e4);
                    }
                }

            }
        } catch (Exception e) {
            Timber.e(e);
            Toast.makeText(getContext(), "Parse Error!", Toast.LENGTH_SHORT).show();
        }

    }

    private void readRoutineEvening(File file, String startDay, String endDay) {

        CsvReader csvReader = new CsvReader();

        csvReader.setSkipEmptyRows(true);

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

                    ClassDetails cd1 = new ClassDetails(row.getField(1).trim(), row.getField(2).trim(), row.getField(3).trim(), row.getField(5).trim(), "Evening", startDay);
                    ClassDetails cd2 = new ClassDetails(row.getField(6).trim(), row.getField(7).trim(), row.getField(8).trim(), row.getField(10).trim(), "Evening", startDay);

                    if (!cd1.getRoom().isEmpty()) {
                        cd1.setTime("6.00PM-7.30PM");
                        cd1.setPriority(7f);

                        if (cd1.getCourseCode().isEmpty()) {
                            allEmptyClassDetails.add(cd1);
                        } else if (!cd1.getCourseCode().isEmpty()) {
                            cd1.setSection(splitSection(cd1.getCourseCode()));
                            cd1.setCourseCode(splitCourseCode(cd1.getCourseCode()));

                            allNonEmptyClassDetails.add(cd1);

                        }
                    }

                    if (!cd2.getRoom().isEmpty()) {
                        cd2.setTime("7.30PM-9.00PM");
                        cd2.setPriority(8f);

                        if (cd2.getCourseCode().isEmpty()) {
                            allEmptyClassDetails.add(cd2);
                        } else if (!cd2.getCourseCode().isEmpty()) {
                            cd2.setSection(splitSection(cd2.getCourseCode()));
                            cd2.setCourseCode(splitCourseCode(cd2.getCourseCode()));

                            allNonEmptyClassDetails.add(cd2);
                        }
                    }
                }
            }

        } catch (IOException e) {
            Timber.e(e);
            Toast.makeText(getContext(), "Parse Error!", Toast.LENGTH_SHORT).show();
        }
    }

    //To separate course code and section
    private String splitCourseCode(String string) {
        try {
            return string.substring(0, string.indexOf("("));
        } catch (StringIndexOutOfBoundsException e) {
            Timber.e(e);
            return string;
        }
    }

    //To separate course code and section
    private String splitSection(String string) {
        try {
            return string.substring(string.indexOf("(") + 1, string.indexOf(")"));
        } catch (StringIndexOutOfBoundsException e) {
            Timber.e(e);
            return "";
        }
    }

    private String getStringResourceByName(String resName) {
        String packageName = "bd.edu.daffodilvarsity.classmanager";
        int resId = getContext().getResources().getIdentifier(resName, "string", packageName);

        String res = null;

        try {
            res = getContext().getResources().getString(resId);
        } catch (Resources.NotFoundException e) {
            Timber.e(e);
        }

        if (res != null) {
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

        switch (v.getId()) {
            case R.id.btn_select_file:
                selectFileFromStorage();
                break;
            case R.id.parse_file:
                parseRoutine();
                break;
            case R.id.upload_all:
                upload();
                break;
            case R.id.upload_json:
                checkAndUploadToFirebaseStorage();
                break;
        }
    }

    private String getJsonRoutine(ArrayList<ClassDetails> classes) {
        Type type = new TypeToken<ArrayList<ClassDetails>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.toJson(classes, type);
    }

    private void makeToast(String txt) {
        if (getContext() != null) {
            Toast.makeText(getContext(), txt, Toast.LENGTH_SHORT).show();
        }
    }

}
