package todolist.kizema.anton.todolist;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import at.markushi.ui.CircleButton;
import todolist.kizema.anton.todolist.adapter.ToDoListAdapter;
import todolist.kizema.anton.todolist.model.EntryPool;
import todolist.kizema.anton.todolist.view.AddEntryDialogFragment;


public class ToDoListFragment extends Fragment implements ToDoListAdapter.AdapterDataListener{

    private static final int DIALOG_CODE = 23243;

    private ViewGroup noItems;
    private ListView todoList;
    private ToDoListAdapter adapter;
    private EntryPool entryPool;

    private CircleButton plusBtn;

    private OnToDoSelectedListener onToDoSelectedListener;

    public interface OnToDoSelectedListener {
        public void onToDoSelected();
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
        AddEntryDialogFragment dlg = new AddEntryDialogFragment();
        dlg.show(ft, "dialog");
    }

    public void onOkBtnPressed(String title, String descr) {
        entryPool.add(title, descr);
        noItems.setVisibility(View.INVISIBLE);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onEmpty() {
        noItems.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onPause() {
        super.onPause();

        entryPool.save();
    }

    @Override
    public void onResume() {
        super.onResume();

        entryPool.load();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.i("ANT", "onActivityCreated");

        entryPool = EntryPool.getPool(getActivity());

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

        plusBtn = (CircleButton) getActivity().findViewById(R.id.plusBtn);
        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewEntry();
            }
        });

        adapter = new ToDoListAdapter(this, getActivity().getBaseContext(), entryPool);
        todoList.setAdapter(adapter);
    }
}