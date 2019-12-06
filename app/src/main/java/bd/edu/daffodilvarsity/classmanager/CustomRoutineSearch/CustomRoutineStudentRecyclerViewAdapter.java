package bd.edu.daffodilvarsity.classmanager.CustomRoutineSearch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.routine.RoutineClassDetails;

public class CustomRoutineStudentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<RoutineClassDetails> classesList = new ArrayList<>();

    public void setData(ArrayList<RoutineClassDetails> classes) {
        classesList.clear();
        classesList.addAll(classes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_routine_student_list_item, parent, false);
            return new ViewHolder1(view);

        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_routine_list_item_divider, parent, false);
            return new ViewHolder2(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 1) {

            ViewHolder1 viewHolder = (ViewHolder1) holder;

            viewHolder.time.setText(classesList.get(position).getTime());
            viewHolder.courseCode.setText(classesList.get(position).getCourseCode());
            viewHolder.courseName.setText(classesList.get(position).getCourseName());
            viewHolder.teacherInitial.setText(classesList.get(position).getTeacherInitial());
            viewHolder.roomNo.setText(classesList.get(position).getRoom());

        } else {

            ViewHolder2 viewHolder = (ViewHolder2) holder;

            viewHolder.dayOfWeek.setText(classesList.get(position).getDayOfWeek());

        }
    }

    @Override
    public int getItemCount() {
        return classesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (classesList.get(position).getPriority() != 1000f) {
            return 1;
        } else {
            return 2;
        }
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder {

        TextView time;
        TextView courseName;
        TextView courseCode;
        TextView teacherInitial;
        TextView roomNo;

        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            courseName = itemView.findViewById(R.id.course_name);
            courseCode = itemView.findViewById(R.id.course_code);
            teacherInitial = itemView.findViewById(R.id.teacher_initial);
            roomNo = itemView.findViewById(R.id.room_no);
        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder {

        TextView dayOfWeek;

        public ViewHolder2(@NonNull View itemView) {
            super(itemView);
            dayOfWeek = itemView.findViewById(R.id.day_of_week);
        }
    }
}
