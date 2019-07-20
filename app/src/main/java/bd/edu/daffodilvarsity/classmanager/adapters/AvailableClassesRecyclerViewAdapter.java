package bd.edu.daffodilvarsity.classmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classmanager.R;
import bd.edu.daffodilvarsity.classmanager.otherclasses.ClassDetails;

public class AvailableClassesRecyclerViewAdapter extends RecyclerView.Adapter<AvailableClassesRecyclerViewAdapter.ViewHolder> {

    private ArrayList<ClassDetails> mEmptyClasses = new ArrayList<>();

    private ItemClickListener itemClickListener;

    public AvailableClassesRecyclerViewAdapter(ArrayList<ClassDetails> mEmptyClasses) {
        this.mEmptyClasses = mEmptyClasses;
        itemClickListener = null;
    }

    public void setItemClickListener(ItemClickListener itemClickListener)   {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_room_list_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.roomNo.setText(mEmptyClasses.get(position).getRoom());
        holder.book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener!=null) {
                    itemClickListener.onItemClicked(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEmptyClasses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView roomNo;

        Button book;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            roomNo = itemView.findViewById(R.id.room_no);
            book = itemView.findViewById(R.id.book);
        }
    }

    public interface ItemClickListener  {
        void onItemClicked(int position);
    }
}
