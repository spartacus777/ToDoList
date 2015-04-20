package todolist.kizema.anton.todolist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Map;

import todolist.kizema.anton.todolist.R;
import todolist.kizema.anton.todolist.model.Entry;
import todolist.kizema.anton.todolist.model.EntryPool;
import todolist.kizema.anton.todolist.view.ToDoViewEntry;


public class ToDoListAdapter extends BaseAdapter implements ToDoViewEntry.OnRemoveListener {

    private Map<View, ToDoViewEntry> map;
    private LayoutInflater inflater;
    private AdapterDataListener listener;

    private EntryPool pool;

    public interface AdapterDataListener{
        void onEmpty();
    }

    public ToDoListAdapter(AdapterDataListener listener, Context context, EntryPool pool) {
        super();

        this.pool = pool;
        this.listener = listener;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        map = new HashMap<View, ToDoViewEntry>();
    }

    @Override
    public void onRemove(Entry entry){
        pool.remove(entry);

        if (getCount() == 0){
            listener.onEmpty();
            return;
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return pool.getSize();
    }

    @Override
    public Entry getItem(int position) {
        return pool.getEntries().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ToDoViewEntry viewEntry;
        Entry entry = getItem(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item, parent, false);

            viewEntry = new ToDoViewEntry((RelativeLayout)convertView);
            viewEntry.setOnRemoveListener(this);
            map.put(convertView, viewEntry);
        } else {
            viewEntry = map.get(convertView);
        }

        viewEntry.setEntry(entry);
        return convertView;
    }
}
