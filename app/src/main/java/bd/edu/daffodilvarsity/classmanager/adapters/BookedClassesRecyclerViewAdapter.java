package bd.edu.daffodilvarsity.classmanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.BookedClassDetails;

public class BookedClassesRecyclerViewAdapter extends RecyclerView.Adapter<BookedClassesRecyclerViewAdapter.ViewHolder> {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

    private ArrayList<BookedClassDetails> mBookedClassesList;

    public BookedClassesRecyclerViewAdapter(ArrayList<BookedClassDetails> mBookedClassesList) {
        this.mBookedClassesList = mBookedClassesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booked_room_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.roomNo.setText(mBookedClassesList.get(position).getRoomNo());

        holder.courseCode.setText(mBookedClassesList.get(position).getCourseCode());

        holder.time.setText(mBookedClassesList.get(position).getTime());

        holder.shift.setText(mBookedClassesList.get(position).getShift());

        holder.section.setText(mBookedClassesList.get(position).getSection());

        String formattedDate = getFormattedDate(mBookedClassesList.get(position).getReservationTime());
        holder.reserveDate.setText(formattedDate);

        holder.whenBooked.setText(getTimePast(mBookedClassesList.get(position).getTimeWhenUserBooked()));

        switch (holder.getItemViewType()) {
            case 0:
                holder.action.setText("Mark as done");
                holder.action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Delete related document from cloud firestore on click
                        db.document("/booked_classes/" + mBookedClassesList.get(position).getDocId() + "/").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                makeToast(holder.action.getContext(), "Success!");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                makeToast(holder.action.getContext(), "Failed!");
                            }
                        });
                    }
                });
                break;
            case 1:

                if (isBookCancelable(mBookedClassesList.get(position).getTimeWhenUserBooked())) {
                    holder.action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Cloud function to cancel book
                        }
                    });
                    break;
                }
                else    {
                    holder.action.setEnabled(false);
                }

        }
    }

    @Override
    public int getItemViewType(int position) {

        BookedClassDetails bcd = mBookedClassesList.get(position);

        Calendar calendar = Calendar.getInstance();

        Timestamp timestamp = new Timestamp(calendar.getTime());

        //If is reservation date was before today then setItemType to 0
        if (bcd.getReservationTime().compareTo(timestamp) < 0) {
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
                msg += dayPast + " day ago";
            } else if (dayPast > 30) {
                msg = "Booked more than a month ago";
            } else {
                msg = dayPast + " days ago";
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

    private boolean isBookCancelable(Timestamp whenUserBooked) {

        long bookTimeInMillis = whenUserBooked.getSeconds() * 1000;
        long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();

        if ((currentTimeInMillis - bookTimeInMillis) < 86400000) {
            return true;
        } else {
            return false;
        }
    }

    private void makeToast(Context context, String text) {
        if (context != null) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
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
}
