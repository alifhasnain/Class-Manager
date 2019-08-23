package bd.edu.daffodilvarsity.classmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.BookedClassDetails;
import bd.edu.daffodilvarsity.classmanager.otherclasses.HelperClass;

public class ExtraClassesStudentRecyclerViewAdapter extends RecyclerView.Adapter<ExtraClassesStudentRecyclerViewAdapter.ViewHolder> {

    private DateFormat mDateFormater = new SimpleDateFormat("EEE, d MMM, yyyy");

    private ArrayList<BookedClassDetails> mExtraClasses;

    public ExtraClassesStudentRecyclerViewAdapter(ArrayList<BookedClassDetails> mExtraClasses) {
        this.mExtraClasses = mExtraClasses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.extra_classes_recycler_view_items,parent,false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.date.setText(mDateFormater.format(mExtraClasses.get(position).getReservationDate().toDate()));
        holder.courseCode.setText(mExtraClasses.get(position).getCourseCode());
        holder.courseName.setText(HelperClass.getCourseNameFromCourseCode(
                mExtraClasses.get(position).getShift(),mExtraClasses.get(position).getCourseCode()
        ));
        holder.inital.setText(mExtraClasses.get(position).getTeacherInitial());
        holder.time.setText(mExtraClasses.get(position).getTime());
        holder.roomNo.setText(mExtraClasses.get(position).getRoomNo());
    }

    @Override
    public int getItemCount() {
        return mExtraClasses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView date;
        TextView courseName;
        TextView courseCode;
        TextView roomNo;
        TextView time;
        TextView inital;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            courseCode = itemView.findViewById(R.id.course_code);
            courseName = itemView.findViewById(R.id.course_name);
            inital = itemView.findViewById(R.id.teacher_initial);
            roomNo = itemView.findViewById(R.id.room_no);
            time = itemView.findViewById(R.id.time);

        }
    }
}
