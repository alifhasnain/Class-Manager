package bd.edu.daffodilvarsity.classmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.BookedClassDetails;

public class SearchBookedClassesWithDateRecyclerViewAdapter extends RecyclerView.Adapter<SearchBookedClassesWithDateRecyclerViewAdapter.ViewHolder> {

    private ArrayList<BookedClassDetails> classList;

    private OnDeleteClickListener onDeleteClickListener;

    public SearchBookedClassesWithDateRecyclerViewAdapter(ArrayList<BookedClassDetails> classList) {
        this.classList = classList;
        onDeleteClickListener = null;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_booked_classes_with_day_list_item,parent,false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.time.setText(classList.get(position).getTime());
        holder.roomNo.setText(classList.get(position).getRoomNo());
        holder.email.setText(classList.get(position).getTeacherEmail());
        holder.teacherInitial.setText(classList.get(position).getTeacherInitial());
        holder.whenBooked.setText(getFormattedDate(classList.get(position).getReservationDate()));
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClickListener.onDeleteClicked(position);
            }
        });
    }

    private String getFormattedDate(Timestamp timestamp) {

        DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM, yyyy");

        return dateFormat.format(timestamp.toDate());

    }

    @Override
    public int getItemCount() {
        return classList.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {


        TextView time;

        TextView roomNo;

        TextView email;

        TextView teacherInitial;

        TextView whenBooked;

        ImageButton delete;
        private ViewHolder(@NonNull View itemView) {

            super(itemView);

            time = itemView.findViewById(R.id.time);
            roomNo = itemView.findViewById(R.id.room_no);
            email = itemView.findViewById(R.id.email);
            teacherInitial = itemView.findViewById(R.id.teacher_initial);
            whenBooked = itemView.findViewById(R.id.whenBooked);
            delete = itemView.findViewById(R.id.delete_book);

        }

    }

    public interface OnDeleteClickListener {
        void onDeleteClicked(int position);
    }
}
