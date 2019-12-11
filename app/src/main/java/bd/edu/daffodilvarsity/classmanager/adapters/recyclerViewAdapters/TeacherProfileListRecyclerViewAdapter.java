package bd.edu.daffodilvarsity.classmanager.adapters.recyclerViewAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ProfileObjectTeacher;

public class TeacherProfileListRecyclerViewAdapter extends RecyclerView.Adapter<TeacherProfileListRecyclerViewAdapter.ViewHolder> {

    private ArrayList<ProfileObjectTeacher> teachersList = new ArrayList<>();

    private OnDeleteClickListener onDeleteClickListener;

    public TeacherProfileListRecyclerViewAdapter(ArrayList<ProfileObjectTeacher> teachersList) {
        this.teachersList = teachersList;
        onDeleteClickListener = null;
    }

    public void addOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener)   {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_profile_list_item,parent,false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.teacherName.setText(teachersList.get(position).getName());
        holder.teacherInitial.setText(teachersList.get(position).getTeacherInitial());
        holder.teacherEmail.setText(teachersList.get(position).getEmail());
        holder.deleteTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onDeleteClickListener!=null) {
                    onDeleteClickListener.onDeleteClickListener(teachersList.get(position).getEmail(),position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return teachersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView teacherName;

        TextView teacherInitial;

        TextView teacherEmail;

        ImageButton deleteTeacher;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            teacherName = itemView.findViewById(R.id.teacher_name);

            teacherInitial = itemView.findViewById(R.id.teacher_initial);

            teacherEmail = itemView.findViewById(R.id.email);

            deleteTeacher = itemView.findViewById(R.id.delete_teacher);
        }
    }

    public interface OnDeleteClickListener  {
        void onDeleteClickListener(String docId,int position);
    }
}
