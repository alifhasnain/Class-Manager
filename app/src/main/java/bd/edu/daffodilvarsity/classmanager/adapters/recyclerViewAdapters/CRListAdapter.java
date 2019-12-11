package bd.edu.daffodilvarsity.classmanager.adapters.recyclerViewAdapters;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.CRObj;

public class CRListAdapter extends RecyclerView.Adapter<CRListAdapter.ViewHolder> {

    private ArrayList<CRObj> crList = new ArrayList<>();

    private ActionEventListener actionEventListener;

    public void setDataAndRefresh(ArrayList<CRObj> dataList) {
        crList.clear();
        crList.addAll(dataList);
        notifyDataSetChanged();
    }

    public void addActionEventListener(ActionEventListener actionEventListener) {
        this.actionEventListener = actionEventListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cr_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.name.setText(crList.get(position).getName());
        holder.courseName.setText(crList.get(position).getCourseName());
        holder.courseCode.setText(crList.get(position).getCourseCode());
        holder.section.setText(crList.get(position).getSection());

        holder.sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionEventListener.onSendEmailClicked(crList.get(position));
            }
        });

        holder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionEventListener.onSendMessageClicked(crList.get(position));
            }
        });

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionEventListener.onDialClicked(crList.get(position));
            }
        });

        holder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popup = new PopupMenu(view.getContext(),view);

                popup.inflate(R.menu.menu_cr_list_item_popup);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.delete) {
                            actionEventListener.onDeleteClicked(position);
                        }
                        return false;
                    }
                });

                popup.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return crList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView courseCode;
        TextView courseName;
        TextView section;

        ImageButton call;
        ImageButton sendMail;
        ImageButton message;

        TextView popupMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            popupMenu = itemView.findViewById(R.id.popup_menu);

            name = itemView.findViewById(R.id.name);
            courseCode = itemView.findViewById(R.id.course_code);
            courseName = itemView.findViewById(R.id.course_name);
            section = itemView.findViewById(R.id.section);

            call = itemView.findViewById(R.id.call);
            sendMail = itemView.findViewById(R.id.send_email);
            message = itemView.findViewById(R.id.message);
        }
    }

    public interface ActionEventListener {

        void onSendEmailClicked(CRObj obj);

        void onSendMessageClicked(CRObj obj);

        void onDialClicked(CRObj obj);

        void onDeleteClicked(int position);

    }

}
