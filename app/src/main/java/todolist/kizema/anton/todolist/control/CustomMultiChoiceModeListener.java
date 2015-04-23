package todolist.kizema.anton.todolist.control;

import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import todolist.kizema.anton.todolist.R;
import todolist.kizema.anton.todolist.control.adapter.ToDoListAdapter;
import todolist.kizema.anton.todolist.model.Entry;

/**
 * Created by Anton on 21.04.2015.
 */
public class CustomMultiChoiceModeListener implements  AbsListView.MultiChoiceModeListener {

    private SparseBooleanArray selectedItems;
    private boolean selectionMode = false;
    private ToDoListAdapter adapter;

    private OnEditListener listener;

    public interface OnEditListener{
        void onEdit(Set<Integer> setPositions);
    }

    public CustomMultiChoiceModeListener(ToDoListAdapter adapter){
        this.adapter = adapter;
    }

    public void setOnEditListener(OnEditListener l){
        this.listener = l;
    }

    public void startSelection(boolean start){
        selectionMode = start;

        if (selectionMode){
            selectedItems = new SparseBooleanArray();
        } else {
            selectedItems = null;
        }
    }

    private SparseBooleanArray getSelectedItems(){
        return selectedItems;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        Log.i("ANT", " onItemCheckedStateChanged, position:" + position + " checked:" + checked);
        selectedItems.put(position, checked);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.item_menu, menu);
        mode.setTitle("Delete posts");
        Log.d("ANT", "onCreateActionMode");
        startSelection(true);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        Log.d("ANT", "onPrepareActionMode");
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        SparseBooleanArray checkedItems = getSelectedItems();

        switch (item.getItemId()) {
            case R.id.actionDelete:
                Log.d("ANT", "size:"+checkedItems.size());

                List<Entry> deletable = new ArrayList<Entry>();

                for (int i=0; i<checkedItems.size(); ++i){
                    Log.d("ANT", " i:"+i+"  checkedItems.get(i):"+ checkedItems.get(i));
                    if ( checkedItems.get( checkedItems.keyAt(i) ) ){
                        deletable.add( adapter.getItem(checkedItems.keyAt(i)) );
                    }
                }

                Log.d("ANT", "deletable.size:"+deletable.size());

                for (Entry del : deletable){
                    adapter.onRemove(del);
                }

                startSelection(false);
                mode.finish();
                return true;
            case R.id.actionEdit:
                Set<Integer> setPos = new HashSet<Integer>();

                for (int i=0; i<checkedItems.size(); ++i){
                    Log.d("ANT", " i:"+i+"  checkedItems.get(i):"+ checkedItems.get(i));
                    if ( checkedItems.get( checkedItems.keyAt(i) ) ){
                        setPos.add( checkedItems.keyAt(i));
                    }
                }

                listener.onEdit(setPos);

                mode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }

}
