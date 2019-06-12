package bd.edu.daffodilvarsity.classmanager.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ClassDetails;

public class ClassListRecyclerViewAdapter extends RecyclerView.Adapter<ClassListRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ClassDetails> mClasses = new ArrayList<>();

    public ClassListRecyclerViewAdapter(Context mContext, ArrayList<ClassDetails> mClasses) {
        this.mContext = mContext;
        this.mClasses = mClasses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.classes_list_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try {
            holder.courseName.setText(getStringResourceByName(mClasses.get(position).getCourseCode()));
            holder.courseCode.setText(mClasses.get(position).getCourseCode());
            holder.classTime.setText(mClasses.get(position).getTime());
            holder.section.setText(mClasses.get(position).getSection());
            holder.roomNo.setText(mClasses.get(position).getRoom());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        catch (Resources.NotFoundException e)  {
            e.printStackTrace();
        }

    }

    private String getStringResourceByName(String resName) {
        String packageName = "bd.edu.daffodilvarsity.classmanager";
        int resId = mContext.getResources().getIdentifier(resName,"string",packageName);

        String res = null;

        try {
            res = mContext.getResources().getString(resId);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }

        if(res!=null)   {
            return res;
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return mClasses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView courseName;
        TextView courseCode;
        TextView classTime;
        TextView section;
        TextView roomNo;
        ImageView notification;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.course_name);
            courseCode = itemView.findViewById(R.id.course_code);
            classTime = itemView.findViewById(R.id.class_time);
            section = itemView.findViewById(R.id.section);
            roomNo = itemView.findViewById(R.id.room_no);
            notification = itemView.findViewById(R.id.notification);
        }
    }
}
