package bd.edu.daffodilvarsity.classmanager.adapters.recyclerViewAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bd.edu.daffodilvarsity.classmanager.R;

public class EmptyRoomsRecyclerViewAdapter extends RecyclerView.Adapter<EmptyRoomsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> emptyRooms;

    public EmptyRoomsRecyclerViewAdapter(ArrayList<String> emptyRooms) {
        this.emptyRooms = emptyRooms;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_empty_room,parent,false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.roomNo.setText(emptyRooms.get(position));
    }

    @Override
    public int getItemCount() {
        return emptyRooms.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView roomNo;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            roomNo = itemView.findViewById(R.id.room_no);
        }
    }
}
