package bd.edu.daffodilvarsity.classmanager.fragments;


import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.developer.filepicker.controller.DialogSelectionListener;
import com.developer.filepicker.model.DialogConfigs;
import com.developer.filepicker.model.DialogProperties;
import com.developer.filepicker.view.FilePickerDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectTeacher;
import de.siegmar.fastcsv.reader.CsvParser;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class ParseTeacherProfiles extends Fragment implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private final int REQUEST_STORAGE_PERMISSION = 1322;

    private ArrayList<ProfileObjectTeacher> teachersList = new ArrayList<>();

    private String mFilePath = "";

    private TextView selectedFileLocationTextView;

    private TextView parseSummary;

    private Button uploadAll;

    public ParseTeacherProfiles() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parse_teacher_profiles, container, false);

        initializeVariables(view);

        return view;

    }

    private void initializeVariables(View view) {

        selectedFileLocationTextView = view.findViewById(R.id.file_location);

        view.findViewById(R.id.select_file).setOnClickListener(this);
        view.findViewById(R.id.parse).setOnClickListener(this);

        parseSummary = view.findViewById(R.id.parse_summary);

        uploadAll = view.findViewById(R.id.upload_all);
        uploadAll.setOnClickListener(this);
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
                selectedFileLocationTextView.setText(files[0]);
                mFilePath = files[0];
            }
        });

        dialog.show();
    }

    private void parseProfiles() {

        if (mFilePath.isEmpty()) {
            makeToast("No file selected!");
            return;
        }

        teachersList.clear();

        File file = new File(mFilePath);

        CsvReader csvReader = new CsvReader();
        csvReader.setSkipEmptyRows(true);

        try {

            CsvParser parser = csvReader.parse(file, StandardCharsets.UTF_8);

            CsvRow row;

            while ((row = parser.nextRow()) != null) {

                if(row.getField(1).isEmpty() || row.getField(1).equals("Email"))  {
                    continue;
                }

                ProfileObjectTeacher profile = new ProfileObjectTeacher(
                        row.getField(0).trim(),
                        row.getField(1).trim(),
                        row.getField(2).trim(),
                        row.getField(3).trim(),
                        row.getField(4).trim(),
                        row.getField(5).trim()
                );

                teachersList.add(profile);

            }

        } catch (IOException e) {
            Timber.e(e);
            makeToast("Error while parsing.");
        }

        parseSummary.setText(teachersList.size() + " Profiles were parsed.");

    }

    private void uploadAll() {

        makeToast("Uploading data. Please wait until upload.");
        uploadAll.setEnabled(false);

        WriteBatch batchWrite = FirebaseFirestore.getInstance().batch();

        for (ProfileObjectTeacher profile : teachersList) {
            DocumentReference docRef = FirebaseFirestore.getInstance().document("teacher_profiles/" + profile.getEmail());
            batchWrite.set(docRef, profile);
        }

        batchWrite.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                makeToast("All data is uploaded");
                uploadAll.setEnabled(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                makeToast("Unsuccessful");
                uploadAll.setEnabled(true);
                Timber.e(e);
            }
        });
    }

    private String extractTeacherName(String txt) {
        try {
            return txt.substring(0, txt.indexOf("(")).trim();
        }
        catch (StringIndexOutOfBoundsException e)    {
            return txt.trim();
        }
    }

    private String extractTeacherInitial(String txt) {
        try {
            return txt.substring(txt.indexOf("(")+1, txt.indexOf(")")).trim();
        } catch (StringIndexOutOfBoundsException e) {
            return "";
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_file:
                selectFileFromStorage();
                break;
            case R.id.parse:
                parseProfiles();
                break;
            case R.id.upload_all:
                uploadAll();
                break;
        }
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
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            selectFileFromStorage();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    private void makeToast(String msg) {
        if (getContext() != null) {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }
}
