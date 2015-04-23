package todolist.kizema.anton.todolist.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import at.markushi.ui.CircleButton;
import todolist.kizema.anton.todolist.R;
import todolist.kizema.anton.todolist.model.Entry;
import todolist.kizema.anton.todolist.model.EntryPool;

public class DetailsFragment extends Fragment {

    public static final String TITLE = "TITLE";
    public static final String POSITION = "POSITION";
    public static final String ENTRY = "Entry";

    private CircleButton editButton;
    private EditText etTitle, etDescr;
    private boolean isEditMode = false;

    private String savedTitle, savedDescr;

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
        editButton = (CircleButton) getActivity().findViewById(R.id.editButton);

        etTitle.setTag(etTitle.getKeyListener());
        etDescr.setTag(etDescr.getKeyListener());

        enable(false);

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

        if (enable){
            savedTitle = etTitle.getText().toString();
            savedDescr = etDescr.getText().toString();

            etTitle.setKeyListener((KeyListener)etTitle.getTag());
            etDescr.setKeyListener((KeyListener)etDescr.getTag());
            editButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_ok_small));

            getActivity().getActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                    .getColor(R.color.ab_edit_color)));
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            getActivity().setTitle(getString(R.string.edit_details_activity));
            editButton.setColor(getResources().getColor(R.color.ab_edit_color));

            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            etDescr.requestFocus();
            inputMethodManager.showSoftInput(etDescr, InputMethodManager.SHOW_IMPLICIT);
        } else {
            etTitle.setKeyListener(null);
            etDescr.setKeyListener(null);
            editButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pen));

            getActivity().getActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                    .getColor(R.color.ab_color)));
            editButton.setColor(getResources().getColor(R.color.plusBtn));
            getActivity().setTitle(getString(R.string.details_activity));
            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
            }

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etTitle.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(etDescr.getWindowToken(), 0);
        }

        isEditMode = enable;
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
        getActivity().getActionBar().setDisplayShowHomeEnabled(true);
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
                    etTitle.setText(savedTitle);
                    etDescr.setText(savedDescr);
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