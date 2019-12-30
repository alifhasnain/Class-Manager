package bd.edu.daffodilvarsity.classmanager.adapters.recyclerViewAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.BookedClassDetailsUser;

public class SearchBookedClassesWithRangeRecyclerViewAdapter extends RecyclerView.Adapter<SearchBookedClassesWithRangeRecyclerViewAdapter.ViewHolder> {

    private ArrayList<BookedClassDetailsUser> classes;

    public SearchBookedClassesWithRangeRecyclerViewAdapter(ArrayList<BookedClassDetailsUser> classes) {
        this.classes = classes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_search_booked_classes_with_range, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.date.setText(getDateStringFromTimestamp(classes.get(position).getReservationDate()));
        holder.email.setText(classes.get(position).getTeacherEmail());
        holder.teacherInitial.setText(classes.get(position).getTeacherInitial());
        holder.courseCode.setText(classes.get(position).getCourseCode());
        holder.shift.setText(classes.get(position).getShift());
        holder.section.setText(classes.get(position).getSection());
        holder.roomNo.setText(classes.get(position).getRoomNo());
        holder.time.setText(classes.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    private String getDateStringFromTimestamp(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getSeconds() * 1000);
        DateFormat dateFormatter = new SimpleDateFormat("d MMM, yyyy");
        return dateFormatter.format(calendar.getTime());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView date;
        TextView email;
        TextView teacherInitial;
        TextView courseCode;
        TextView section;
        TextView shift;
        TextView roomNo;
        TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            email = itemView.findViewById(R.id.email);
            teacherInitial = itemView.findViewById(R.id.teacher_initial);
            courseCode = itemView.findViewById(R.id.course_code);
            section = itemView.findViewById(R.id.section);
            shift = itemView.findViewById(R.id.shift);
            roomNo = itemView.findViewById(R.id.room_no);
            time = itemView.findViewById(R.id.time);
        }
    }
}
