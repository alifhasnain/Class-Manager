package bd.edu.daffodilvarsity.classmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classmanager.R;

public class CourseListRecyclerViewAdapter extends RecyclerView.Adapter<CourseListRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> coursesList;

    private ArrayList<String> sectionList;

    private OnDeleteClickListener onDeleteClickListener;

    public CourseListRecyclerViewAdapter(ArrayList<String> coursesList, ArrayList<String> sectionList) {
        this.coursesList = coursesList;
        this.sectionList = sectionList;

        onDeleteClickListener = null;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener deleteClickListener) {
        onDeleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_and_section_recycler_list_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.courseCode.setText(coursesList.get(position));
        holder.section.setText(sectionList.get(position));

        if(onDeleteClickListener!=null) {
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteClickListener.onDeleteClicked(position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return coursesList.size();
    }

    public interface OnDeleteClickListener  {
        void onDeleteClicked(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView courseCode;

        TextView section;

        ImageButton delete;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            courseCode = itemView.findViewById(R.id.course_code);

            section = itemView.findViewById(R.id.section);

            delete = itemView.findViewById(R.id.delete);
        }
    }
}
