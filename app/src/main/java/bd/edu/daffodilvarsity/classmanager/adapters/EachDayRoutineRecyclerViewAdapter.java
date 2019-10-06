package bd.edu.daffodilvarsity.classmanager.adapters;

import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import bd.edu.daffodilvarsity.classmanager.DiffUtilCallbacks.EachDayRoutineDiffCallback;
import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.routine.RoutineClassDetails;

public class EachDayRoutineRecyclerViewAdapter extends RecyclerView.Adapter<EachDayRoutineRecyclerViewAdapter.ViewHolder> {

    private NotificationChangeListener notificationChangeListener;

    private ArrayList<RoutineClassDetails> mClasses = new ArrayList<>();

    public EachDayRoutineRecyclerViewAdapter() {
        notificationChangeListener = null;
    }

    public void addNotificationChangeListener(NotificationChangeListener listener)  {
        notificationChangeListener = listener;
    }

    public void updateRecyclerViewAdapter(List<RoutineClassDetails> updatedList) {

        EachDayRoutineDiffCallback diffCallback = new EachDayRoutineDiffCallback(mClasses,updatedList);

        DiffUtil.DiffResult result = DiffUtil.calculateDiff(diffCallback);

        result.dispatchUpdatesTo(this);

        mClasses.clear();
        mClasses.addAll(updatedList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.classes_list_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        try {
            holder.courseName.setText(mClasses.get(position).getCourseName());
            holder.courseCode.setText(mClasses.get(position).getCourseCode());
            holder.classTime.setText(mClasses.get(position).getTime());
            holder.teacherInitial.setText(mClasses.get(position).getTeacherInitial());
            holder.section.setText(mClasses.get(position).getSection());
            holder.roomNo.setText(mClasses.get(position).getRoom());

            if(mClasses.get(position).isNotificationEnabled())    {
                holder.notification.setBackgroundColor(Color.parseColor("#4885ed"));
                holder.notification.setImageResource(R.drawable.ic_alarm_on_white);
            }
            else    {
                holder.notification.setBackgroundColor(Color.parseColor("#DB3236"));
                holder.notification.setImageResource(R.drawable.ic_alarm_off_white);
            }

            holder.notification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(notificationChangeListener!=null)    {
                        notificationChangeListener.onNotificationChanges(mClasses.get(position));
                        Animation pulse = AnimationUtils.loadAnimation(holder.notification.getContext(),R.anim.pulse_anim);
                        holder.notification.startAnimation(pulse);
                    }
                }
            });

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

    public interface NotificationChangeListener {
        void onNotificationChanges(RoutineClassDetails routineClassDetails);
    }
}
