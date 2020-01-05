package bd.edu.daffodilvarsity.classmanager.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.List;

import bd.edu.daffodilvarsity.classmanager.R;

public class BookClassDialog extends AppCompatDialogFragment {

    private TextView date;

    private TextView roomNo;

    private TextView time;

    private String dateStr;

    private String roomNoStr;

    private String timeStr;

    private Spinner programSelector;

    private Spinner shiftSelector;

    private Spinner sectionSelector;

    private Spinner courseSelector;

    private List<String> teacherCourses;

    private List<String> teacherSections;

    private CustomDialogListener listener;

    public BookClassDialog(String date, String room, String time , List<String> teacherCourses , List<String> teacherSections) {
        dateStr = date;
        roomNoStr = room;
        timeStr = time;
        this.teacherCourses = teacherCourses;
        this.teacherSections = teacherSections;
        listener = null;
    }

    public void addReturnTextListener(CustomDialogListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.book_class_dialog_layout, null);

        initCourseArray();

        initializeVariables(view);

        this.date.setText(dateStr);
        this.roomNo.setText(roomNoStr);
        this.time.setText(timeStr);

        initializeSpinners();

        builder.setView(view).setTitle("Are you sure to book this room?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.returnTexts(shiftSelector.getSelectedItem().toString(),
                                    programSelector.getSelectedItem().toString(),
                                    sectionSelector.getSelectedItem().toString(),
                                    courseSelector.getSelectedItem().toString());
                        }
                    }
                });

        return builder.create();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initializeVariables(View view) {

        date = view.findViewById(R.id.date);

        time = view.findViewById(R.id.time);

        roomNo = view.findViewById(R.id.room_no);

        programSelector = view.findViewById(R.id.program);

        shiftSelector = view.findViewById(R.id.shift);

        sectionSelector = view.findViewById(R.id.section);

        courseSelector = view.findViewById(R.id.course_code);

    }

    private void initCourseArray() {

        //ArrayList<String> courseDayList = new ArrayList<>(HelperClass.getCoursesDay().keySet());

        //courseDay = teacherCourses.toArray(new String[teacherCourses.size()]);

        /*ArrayList<String> courseEveBscList = new ArrayList<>(HelperClass.getCoursesEveningBsc().keySet());

        coursesEveningBsc = courseEveBscList.toArray(new String[courseEveBscList.size()]);*/

        //ArrayList<String> courseEveMscList = new ArrayList<>(HelperClass.getCoursesEveningMsc().keySet());

        //coursesEveningMsc = courseEveMscList.toArray(new String[courseEveMscList.size()]);

    }

    private void initializeSpinners() {

        String[] programs = new String[]{"B.Sc. in CSE"};
        ArrayAdapter<String> programSelectorAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_items, programs);
        programSelectorAdapter.setDropDownViewResource(R.layout.spinner_dropdown_items);
        programSelector.setAdapter(programSelectorAdapter);


        String[] shifts = new String[]{"Day", "Evening"};
        final ArrayAdapter<String> shiftSelectorAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_items, shifts);
        shiftSelectorAdapter.setDropDownViewResource(R.layout.spinner_dropdown_items);
        shiftSelector.setAdapter(shiftSelectorAdapter);


        ArrayAdapter<String> sectionSelectorAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_items, teacherSections);
        sectionSelectorAdapter.setDropDownViewResource(R.layout.spinner_dropdown_items);
        sectionSelector.setAdapter(sectionSelectorAdapter);


        ArrayAdapter<String> courseCodeSelectorAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_items, teacherCourses);

        courseCodeSelectorAdapter.setDropDownViewResource(R.layout.spinner_dropdown_items);

        courseSelector.setAdapter(courseCodeSelectorAdapter);


        /*programSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (programSelector.getSelectedItem().toString().equals("M.Sc. in CSE")) {
                    shiftSelector.setSelection(1);
                }
                if (shiftSelector.getSelectedItem().toString().equals("Day") && programSelector.getSelectedItem().toString().equals("B.Sc. in CSE")) {
                    ArrayAdapter<String> courseCodeSelectorAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_items, courseDay);
                    courseCodeSelectorAdapter.setDropDownViewResource(R.layout.spinner_dropdown_items);
                    courseSelector.setAdapter(courseCodeSelectorAdapter);
                } else if (shiftSelector.getSelectedItem().toString().equals("Evening") && programSelector.getSelectedItem().toString().equals("B.Sc. in CSE")) {
                    ArrayAdapter<String> courseCodeSelectorAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_items, coursesEveningBsc);
                    courseCodeSelectorAdapter.setDropDownViewResource(R.layout.spinner_dropdown_items);
                    courseSelector.setAdapter(courseCodeSelectorAdapter);
                } else if (shiftSelector.getSelectedItem().toString().equals("Evening") && programSelector.getSelectedItem().toString().equals("M.Sc. in CSE")) {
                    ArrayAdapter<String> courseCodeSelectorAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_items, coursesEveningMsc);
                    courseCodeSelectorAdapter.setDropDownViewResource(R.layout.spinner_dropdown_items);
                    courseSelector.setAdapter(courseCodeSelectorAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        /*shiftSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (shiftSelector.getSelectedItem().toString().equals("Day")) {
                    programSelector.setSelection(0);
                }
                if (shiftSelector.getSelectedItem().toString().equals("Day") && programSelector.getSelectedItem().toString().equals("B.Sc. in CSE")) {
                    ArrayAdapter<String> courseCodeSelectorAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_items, courseDay);
                    courseSelector.setAdapter(courseCodeSelectorAdapter);
                } else if (shiftSelector.getSelectedItem().toString().equals("Evening") && programSelector.getSelectedItem().toString().equals("B.Sc. in CSE")) {
                    ArrayAdapter<String> courseCodeSelectorAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_items, coursesEveningBsc);
                    courseSelector.setAdapter(courseCodeSelectorAdapter);
                } else if (shiftSelector.getSelectedItem().toString().equals("Evening") && programSelector.getSelectedItem().toString().equals("M.Sc. in CSE")) {
                    ArrayAdapter<String> courseCodeSelectorAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_items, coursesEveningMsc);
                    courseSelector.setAdapter(courseCodeSelectorAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

    }

    public interface CustomDialogListener {
        void returnTexts(String shift, String program, String section, String courseCode);
    }
}
