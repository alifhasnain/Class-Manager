package bd.edu.daffodilvarsity.classmanager.adapters;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ClassDetails;

public class ClassListRecyclerViewAdapter extends RecyclerView.Adapter<ClassListRecyclerViewAdapter.ViewHolder> {

    private ArrayList<ClassDetails> mClasses = new ArrayList<>();

    public ClassListRecyclerViewAdapter(ArrayList<ClassDetails> mClasses) {
        this.mClasses = mClasses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.classes_list_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try {
            holder.courseName.setText(mClasses.get(position).getCourseName());
            holder.courseCode.setText(mClasses.get(position).getCourseCode());
            holder.classTime.setText(mClasses.get(position).getTime());
            holder.teacherInitial.setText(mClasses.get(position).getTeacherInitial());
            holder.section.setText(mClasses.get(position).getSection());
            holder.roomNo.setText(mClasses.get(position).getRoom());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        catch (Resources.NotFoundException e)  {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mClasses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView courseName;
        TextView courseCode;
        TextView classTime;
        TextView teacherInitial;
        TextView section;
        TextView roomNo;
        ImageView notification;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.course_name);
            courseCode = itemView.findViewById(R.id.course_code);
            classTime = itemView.findViewById(R.id.class_time);
            teacherInitial = itemView.findViewById(R.id.teacher_initial);
            section = itemView.findViewById(R.id.section);
            roomNo = itemView.findViewById(R.id.room_no);
            notification = itemView.findViewById(R.id.notification);
        }
    }
}
