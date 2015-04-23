package todolist.kizema.anton.todolist;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Set;

import at.markushi.ui.CircleButton;
import todolist.kizema.anton.todolist.control.CustomMultiChoiceModeListener;
import todolist.kizema.anton.todolist.control.adapter.ToDoListAdapter;
import todolist.kizema.anton.todolist.model.Entry;
import todolist.kizema.anton.todolist.model.EntryPool;
import todolist.kizema.anton.todolist.view.EditEntryDialogFragment;
import todolist.kizema.anton.todolist.view.ToDoViewEntry;


public class ToDoListFragment extends Fragment implements ToDoListAdapter.AdapterDataListener,
        CustomMultiChoiceModeListener.OnEditListener, AdapterView.OnItemClickListener {

    private static final int DIALOG_CODE = 23243;

    private ViewGroup noItems;
    private ListView todoList;
    private ToDoListAdapter adapter;
    private EntryPool entryPool;

    private CircleButton plusBtn;

    private OnToDoSelectedListener onToDoSelectedListener;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        onToDoSelectedListener.onToDoSelected(adapter.getItem(position), position);
    }

    public interface OnToDoSelectedListener {
        void onToDoSelected(Entry entry, int pos);
        void onSettingsPressed();
        void onRemove(Entry entry);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onToDoSelectedListener = (OnToDoSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }

    private void addNewEntry(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        EditEntryDialogFragment dlg = new EditEntryDialogFragment();
        dlg.show(ft, "dialog");
    }

    public void onOkBtnPressed(Entry entry, String title, String descr) {
        if (entry == null) {
            //create new entry
            entryPool.add(title, descr);
            noItems.setVisibility(View.INVISIBLE);
            adapter.notifyDataSetChanged();
            todoList.post(new Runnable() {
                @Override
                public void run() {
                    todoList.setSelection(adapter.getCount() - 1);
                }
            });
        } else {
            //edit entry
            for (Entry e : entryPool.getEntries()){
                if (e == entry){
                    e.title = title;
                    e.description = descr;

                    adapter.notifyDataSetChanged();
                    return;
                }
            }
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        onToDoSelectedListener = null;
    }

    @Override
    public void onEmpty() {
        noItems.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRemove(Entry entry) {
        onToDoSelectedListener.onRemove(entry);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();

        entryPool.save();
    }

    public void notifyDataSetChanged(){
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();

        entryPool.load();
        adapter.notifyDataSetChanged();
        getActivity().setTitle(getString(R.string.main_act_title));
    }

    public void updateTextSizes(){
        for (ToDoViewEntry entryContainer : adapter.getMap().values()){
            entryContainer.updateTextSizes();
        }
    }

    @Override
    public void onEdit(Set<Integer> setPositions) {
        for (Integer pos : setPositions){
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            EditEntryDialogFragment dlg = EditEntryDialogFragment.newInstance(entryPool.getEntries().get(pos));
            dlg.show(ft, "dialog");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.i("ANT", "onActivityCreated");

        entryPool = EntryPool.getPool();

        noItems = (ViewGroup) getActivity().findViewById(R.id.noItemsLayout);
        noItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewEntry();
            }
        });

        if (entryPool.getSize() > 0) {
            noItems.setVisibility(View.GONE);
        }

        todoList = (ListView) getActivity().findViewById(R.id.lists);
        todoList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        adapter = new ToDoListAdapter(this, getActivity().getBaseContext(), entryPool);
        CustomMultiChoiceModeListener customMultiChoiceModeListener = new CustomMultiChoiceModeListener(adapter);
        customMultiChoiceModeListener.setOnEditListener(this);
        todoList.setMultiChoiceModeListener(customMultiChoiceModeListener);
        todoList.setOnItemClickListener(this);

        plusBtn = (CircleButton) getActivity().findViewById(R.id.plusBtn);
        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewEntry();
            }
        });

        todoList.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            onToDoSelectedListener.onSettingsPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}