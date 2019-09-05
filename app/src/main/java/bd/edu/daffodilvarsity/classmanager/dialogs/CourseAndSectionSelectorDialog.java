package bd.edu.daffodilvarsity.classmanager.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;

public class CourseAndSectionSelectorDialog extends AppCompatDialogFragment {

    private Spinner sectionSelector;

    private Spinner courseSelector;

    private String shift;

    private LinkedHashMap<String,String> coursesMap = new LinkedHashMap<>();

    private OnDialogItemSelectListener dialogItemSelectListener;

    public CourseAndSectionSelectorDialog(String shift) {
        this.shift = shift;
        dialogItemSelectListener = null;
    }

    public void setDialogItemSelectListener(OnDialogItemSelectListener listener)   {
        dialogItemSelectListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.course_and_section_selector_dialog_layout,null);

        initializeVariables(view);

        initializeSectionSpinner();

        initializeCourseSpinner();

        dialogBuilder.setView(view).setTitle("Please select your section and course")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(dialogItemSelectListener!=null)  {

                            int i = courseSelector.getSelectedItemPosition();

                            ArrayList<String> courseCodes = new ArrayList<>(coursesMap.keySet());

                            String course = courseCodes.get(i);

                            String section = sectionSelector.getSelectedItem().toString();

                            dialogItemSelectListener.onItemSelected(section,course);
                        }
                    }
                }).setNegativeButton("Cancel",null);

        return dialogBuilder.create();

    }

    private void initializeVariables(View view) {

        sectionSelector = view.findViewById(R.id.section_selector);
        courseSelector = view.findViewById(R.id.course_selector);

    }

    private void initializeSectionSpinner() {

        String[] sections = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U"};

        ArrayAdapter<String> sectionSelectorAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_items, sections);

        sectionSelectorAdapter.setDropDownViewResource(R.layout.spinner_dropdown_items);

        sectionSelector.setAdapter(sectionSelectorAdapter);

    }

    private void initializeCourseSpinner() {

        ArrayList<String> courseCodes = new ArrayList<>();

        ArrayList<String> courseName = new ArrayList<>();

        if(shift.equals("Day")) {
             coursesMap = HelperClass.getCoursesDay();
        }
        else if(shift.equals("Evening"))    {
             coursesMap = HelperClass.getCoursesDay();
        }

        for(Map.Entry<String,String> entry : coursesMap.entrySet()) {

            courseCodes.add(entry.getKey());

            courseName.add(entry.getValue());

        }

        ArrayList<String> combinedList = new ArrayList<>();

        for(int i = 0 ; i < courseCodes.size() ; i++)   {
            combinedList.add(courseCodes.get(i) + " : " + courseName.get(i));
        }

        String[] items = combinedList.toArray(new String[combinedList.size()]);

        ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_items, items);

        courseAdapter.setDropDownViewResource(R.layout.spinner_dropdown_items);

        courseSelector.setAdapter(courseAdapter);

    }

    public interface OnDialogItemSelectListener    {
        void onItemSelected(String section , String courseCode);
    }
}
