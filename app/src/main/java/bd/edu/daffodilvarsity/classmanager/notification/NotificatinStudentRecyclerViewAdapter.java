package bd.edu.daffodilvarsity.classmanager.notification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import bd.edu.daffodilvarsity.classmanager.R;

public class NotificatinStudentRecyclerViewAdapter extends RecyclerView.Adapter<NotificatinStudentRecyclerViewAdapter.ViewHolder> {

    private List<NotificationObjStudent> notificationItemList = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_student_notification,parent,false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.teacherName.setText(notificationItemList.get(position).getTeacherName());
        holder.teacherEmail.setText(notificationItemList.get(position).getTeacherEmail());
        holder.courseName.setText(notificationItemList.get(position).getCourseName());
        holder.courseCode.setText(notificationItemList.get(position).getCourseCode());
        holder.roomNo.setText(notificationItemList.get(position).getRoomNo());
        holder.section.setText(notificationItemList.get(position).getSection());
        holder.time.setText(notificationItemList.get(position).getTime());
        holder.date.setText(notificationItemList.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return notificationItemList.size();
    }

    public void updateRecyclerView(List<NotificationObjStudent> notifications) {
        notificationItemList.clear();
        notificationItemList.addAll(notifications);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView teacherName;
        TextView teacherEmail;
        TextView courseName;
        TextView courseCode;
        TextView date;
        TextView section;
        TextView time;
        TextView roomNo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            teacherName = itemView.findViewById(R.id.teacher_name);
            teacherEmail = itemView.findViewById(R.id.teacher_email);
            courseName = itemView.findViewById(R.id.course_name);
            courseCode = itemView.findViewById(R.id.course_code);
            date = itemView.findViewById(R.id.date);
            section = itemView.findViewById(R.id.section);
            time = itemView.findViewById(R.id.class_time);
            roomNo = itemView.findViewById(R.id.room_no);
        }
    }
}
