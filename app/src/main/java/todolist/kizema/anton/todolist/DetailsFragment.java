package todolist.kizema.anton.todolist;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import at.markushi.ui.CircleButton;
import todolist.kizema.anton.todolist.model.Entry;
import todolist.kizema.anton.todolist.model.EntryPool;

public class DetailsFragment extends Fragment {

    public static final String TITLE = "TITLE";
    public static final String POSITION = "POSITION";
    public static final String ENTRY = "Entry";

    private CircleButton editButton;
    private EditText etTitle, etDescr;
    private boolean isEditMode = false;

    private Entry entry;
    private int pos;

    private MenuItem menuItem;

    private OnUpdateAdapterListener listener;

    public interface OnUpdateAdapterListener{
        void onUpdate();
    }

    public static DetailsFragment newInstance(Entry entry, int pos) {
        Log.i("ANT", "newInstance");
        DetailsFragment fragment = new DetailsFragment();
        Bundle bndl = new Bundle();
        bndl.putSerializable(ENTRY, entry);
        bndl.putInt(POSITION, pos);
        fragment.setArguments(bndl);
        return fragment;
    }

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i("ANT", "DetailsFragment :: onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        etTitle = (EditText) getActivity().findViewById(R.id.etTitle);
        etDescr = (EditText) getActivity().findViewById(R.id.etDescr);
        enable(false);

        editButton = (CircleButton) getActivity().findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEditClicked();
            }
        });

        if (getArguments() != null) {
            entry = (Entry) getArguments().getSerializable(ENTRY);
            pos = getArguments().getInt(POSITION);

            if (entry != null) {
                setEntry(entry, pos);
            }
        }
    }

    public int getPos(){
        return pos;
    }

    public void setEntry(Entry entry, int pos){
        this.entry = entry;
        this.pos = pos;
        enable(false);

        etTitle.setText(entry.title);
        etDescr.setText(entry.description);
    }

    private void enable(boolean enable){
        if (menuItem != null) {
            menuItem.setVisible(enable);
        }
        etTitle.setEnabled(enable);
        etDescr.setEnabled(enable);

        isEditMode = enable;

        if (enable){
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            getActivity().getActionBar().setDisplayShowHomeEnabled(true);
            getActivity().setTitle(getString(R.string.edit_details_activity));
        } else {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
            getActivity().getActionBar().setDisplayShowHomeEnabled(false);
            getActivity().setTitle(getString(R.string.details_activity));
        }
    }

    public void onEditClicked(){
        enable(!isEditMode);
        if (!isEditMode){
            save();
        }
    }

    private void save(){
        Log.i("ANT", "DetailsFragment :: save");
        Entry entry = EntryPool.getPool().getEntries().get(pos);
        Log.i("ANT", "entry.title : "+entry.title);
        entry.title = etTitle.getText().toString();
        entry.description = etDescr.getText().toString();

        Log.i("ANT", "entry.title : "+entry.title);
        EntryPool.getPool().save();
        listener.onUpdate();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("ANT", "DetailsFragment :: onCreate");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("ANT", "DetailsFragment :: onCreateView");
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i("ANT", "DetailsFragment :: onAttach");
        listener = (OnUpdateAdapterListener) activity;
    }

    @Override
    public void onDetach() {
        Log.i("ANT", "DetailsFragment :: onDetach");
        listener = null;
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        Log.i("ANT", "DetailsFragment :: onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (isEditMode){
                    enable(false);
                    return true;
                }
                getActivity().onBackPressed();
                return true;
            case R.id.action_done:
                save();
                enable(false);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.details_menu, menu);

        menuItem = menu.findItem(R.id.action_done);
        menuItem.setVisible(false);
    }

    public Entry getEntry(){
        return entry;
    }
}
