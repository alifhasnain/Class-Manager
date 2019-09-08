package bd.edu.daffodilvarsity.classmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.BookedClassDetailsUser;

public class BookedClassesRecyclerViewAdapter extends RecyclerView.Adapter<BookedClassesRecyclerViewAdapter.ViewHolder> {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayList<BookedClassDetailsUser> mBookedClassesList;

    OnBookCancelListener onBookCancelListener;

    public BookedClassesRecyclerViewAdapter(ArrayList<BookedClassDetailsUser> mBookedClassesList) {
        this.mBookedClassesList = mBookedClassesList;
        onBookCancelListener = null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booked_room_list_item, parent, false);

        return new ViewHolder(view);
    }

    public void addOnBookCancelListener(OnBookCancelListener listener) {
        this.onBookCancelListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.roomNo.setText(mBookedClassesList.get(position).getRoomNo());

        holder.courseCode.setText(mBookedClassesList.get(position).getCourseCode());

        holder.time.setText(mBookedClassesList.get(position).getTime());

        holder.shift.setText(mBookedClassesList.get(position).getShift());

        holder.section.setText(mBookedClassesList.get(position).getSection());

        String formattedDate = getFormattedDate(mBookedClassesList.get(position).getReservationDate());
        holder.reserveDate.setText(formattedDate);

        holder.whenBooked.setText(getTimePast(mBookedClassesList.get(position).getTimeWhenUserBooked()));

        switch (holder.getItemViewType()) {

            case 0:
                //Not cancelable
                holder.action.setEnabled(false);
                holder.action.setText("Not Cancelable");
                break;

            case 1:
                holder.action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Cloud function to cancel book
                        if (onBookCancelListener != null) {
                            onBookCancelListener.onBookCancel(mBookedClassesList.get(position));
                        }

                    }
                });
                break;

        }
    }

    @Override
    public int getItemViewType(int position) {

        BookedClassDetailsUser bcd = mBookedClassesList.get(position);

        Calendar calendar = Calendar.getInstance();

        Timestamp timestamp = bcd.getTimeWhenUserBooked();

        Calendar reservationTime = Calendar.getInstance();
        reservationTime.setTimeInMillis(timestamp.getSeconds() * 1000);

        //If is reservation date was before today then setItemType to 0
        if ((calendar.getTimeInMillis() - reservationTime.getTimeInMillis()) > 86400000) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return mBookedClassesList.size();
    }

    private String getFormattedDate(Timestamp timestamp) {

        DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM, yyyy");

        return dateFormat.format(timestamp.toDate());

    }

    private String getTimePast(Timestamp timestamp) {

        String msg = "Booked about ";

        Calendar currentTime = Calendar.getInstance();

        Calendar reservationTime = Calendar.getInstance();

        reservationTime.setTimeInMillis(timestamp.getSeconds() * 1000);

        long start = reservationTime.getTimeInMillis();

        long end = currentTime.getTimeInMillis();

        long dayPast = TimeUnit.MILLISECONDS.toDays(Math.abs(end - start));

        long hoursPast = TimeUnit.MILLISECONDS.toHours(Math.abs(end - start));

        if (dayPast >= 1) {
            if (dayPast == 1) {
                msg += "Booked " + dayPast + " day ago";
            } else if (dayPast > 30) {
                msg = "Booked more than a month ago";
            } else {
                msg = "Booked " + dayPast + " days ago";
            }
        }

        if (hoursPast < 24 && hoursPast >= 0) {
            if (hoursPast < 1) {
                msg += "less than an hour ago";
            } else {
                msg += hoursPast + " hours ago";
            }
        }

        return msg;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView roomNo;

        TextView courseCode;

        TextView reserveDate;

        TextView time;

        TextView shift;

        TextView section;

        TextView whenBooked;

        Button action;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            roomNo = itemView.findViewById(R.id.room_no);

            courseCode = itemView.findViewById(R.id.course_code);

            reserveDate = itemView.findViewById(R.id.reserveDate);

            time = itemView.findViewById(R.id.time);

            shift = itemView.findViewById(R.id.shift);

            section = itemView.findViewById(R.id.section);

            whenBooked = itemView.findViewById(R.id.time_till_booking);

            action = itemView.findViewById(R.id.btn_action);
        }
    }

    public interface OnBookCancelListener {
        void onBookCancel(BookedClassDetailsUser bcd);
    }
}
