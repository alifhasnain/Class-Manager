package bd.edu.daffodilvarsity.classmanager.DiffUtilCallbacks;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import bd.edu.daffodilvarsity.classmanager.routine.RoutineClassDetails;

public class EachDayRoutineDiffCallback extends DiffUtil.Callback {

    private List<RoutineClassDetails> mOldClassList;

    private List<RoutineClassDetails> mNewClassList;

    public EachDayRoutineDiffCallback(List<RoutineClassDetails> oldClassList, List<RoutineClassDetails> newClassList) {
        this.mOldClassList = oldClassList;
        this.mNewClassList = newClassList;
    }

    @Override
    public int getOldListSize() {
        return mOldClassList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewClassList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldClassList.get(oldItemPosition).getId() == mNewClassList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldClassList.get(oldItemPosition).equals(mNewClassList.get(newItemPosition)) ;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
