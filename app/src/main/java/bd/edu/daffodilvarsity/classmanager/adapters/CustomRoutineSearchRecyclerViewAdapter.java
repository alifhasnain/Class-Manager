package bd.edu.daffodilvarsity.classmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ClassDetails;

public class CustomRoutineSearchRecyclerViewAdapter extends RecyclerView.Adapter<CustomRoutineSearchRecyclerViewAdapter.ViewHolder> {

    private ArrayList<ClassDetails> classesList;

    public CustomRoutineSearchRecyclerViewAdapter(ArrayList<ClassDetails> classesList) {
        this.classesList = classesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_routine_list_item,parent,false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.courseName.setText(classesList.get(position).getCourseName());
        holder.courseCode.setText(classesList.get(position).getCourseCode());
        holder.time.setText(classesList.get(position).getTime());
        holder.room.setText(classesList.get(position).getRoom());
        holder.section.setText(classesList.get(position).getSection());
    }

    @Override
    public int getItemCount() {
        return classesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView courseName;

        TextView courseCode;

        TextView time;

        TextView room;

        TextView section;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            courseName = itemView.findViewById(R.id.course_name);
            courseCode = itemView.findViewById(R.id.course_code);
            time = itemView.findViewById(R.id.time);
            room = itemView.findViewById(R.id.room_no);
            section = itemView.findViewById(R.id.section);

        }
    }
}
