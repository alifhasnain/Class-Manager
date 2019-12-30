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

public class CustomRoutineTeacherRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<RoutineClassDetails> classesList;

    public CustomRoutineTeacherRecyclerViewAdapter(ArrayList<RoutineClassDetails> classesList) {
        this.classesList = classesList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==1) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_custom_routine_teacher,parent,false);

            return new ViewHolder1(view);

        }
        else {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_divider_custom_routine,parent,false);

            return new ViewHolder2(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType())
        {
            case 1:
                ViewHolder1 holder1 = (ViewHolder1) holder;
                holder1.courseName.setText(classesList.get(position).getCourseName());
                holder1.courseCode.setText(classesList.get(position).getCourseCode());
                holder1.time.setText(classesList.get(position).getTime());
                holder1.room.setText(classesList.get(position).getRoom());
                holder1.section.setText(classesList.get(position).getSection());
                break;
            case 2:
                ViewHolder2 holder2 = (ViewHolder2) holder;
                holder2.dayOfWeek.setText(classesList.get(position).getDayOfWeek());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return classesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(classesList.get(position).getPriority()!=1000f)  {
            return 1;
        }   else {
            return 2;
        }
    }

    class ViewHolder1 extends RecyclerView.ViewHolder {

        TextView courseName;

        TextView courseCode;

        TextView time;

        TextView room;

        TextView section;

        ViewHolder1(@NonNull View itemView) {
            super(itemView);

            courseName = itemView.findViewById(R.id.course_name);
            courseCode = itemView.findViewById(R.id.course_code);
            time = itemView.findViewById(R.id.time);
            room = itemView.findViewById(R.id.room_no);
            section = itemView.findViewById(R.id.section);

        }
    }

    class ViewHolder2 extends RecyclerView.ViewHolder   {

        TextView dayOfWeek;

        public ViewHolder2(@NonNull View itemView) {
            super(itemView);
            dayOfWeek = itemView.findViewById(R.id.day_of_week);
        }
    }
}
