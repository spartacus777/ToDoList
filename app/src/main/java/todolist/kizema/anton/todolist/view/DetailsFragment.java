package todolist.kizema.anton.todolist.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import at.markushi.ui.CircleButton;
import todolist.kizema.anton.todolist.R;
import todolist.kizema.anton.todolist.app.App;
import todolist.kizema.anton.todolist.control.FloatingButtonControll;
import todolist.kizema.anton.todolist.helpers.OnSwipeTouchListener;
import todolist.kizema.anton.todolist.model.Entry;
import todolist.kizema.anton.todolist.model.EntryPool;

public class DetailsFragment extends Fragment {

    public static final String TITLE = "TITLE";
    public static final String POSITION = "POSITION";
    public static final String ENTRY = "Entry";

    private CircleButton editButton;
    private EditText etTitle, etDescr;

    private boolean isEditMode = false;
    private boolean enableEditMode = false;

    private String savedTitle, savedDescr;
    private Entry entry;
    private int pos;

    private MenuItem menuItemOk, menuItemShare, menuItemSettings;

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i("ANT", "DetailsFragment :: onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        etTitle = (EditText) getActivity().findViewById(R.id.etTitle);
        etDescr = (EditText) getActivity().findViewById(R.id.etDescr);
        editButton = (CircleButton) getActivity().findViewById(R.id.editButton);

        if (etTitle == null){
            return;
        }

        if (enableEditMode){
            isEditMode = true;
            enableEditMode = false;
        }
        enable(isEditMode);

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

        initFloatingBtn();
    }

    private void initFloatingBtn(){
        int height;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            height = App.getH();
        } else {
            height = App.getW();
        }

        final FloatingButtonControll floatingButtonControll = new FloatingButtonControll(editButton, height - (
                2*getResources().getDimension(R.dimen.norm_icon_size) + getResources().getDimension(R.dimen.margin_plus)
                        + 2*getResources().getDimension(R.dimen.activity_vertical_margin)));

        final OnSwipeTouchListener touchListener = new OnSwipeTouchListener(getActivity());
        touchListener.setOnSwipeListener(new OnSwipeTouchListener.OnSwipeListener() {

            @Override
            public void onSwipeTop() {
                floatingButtonControll.onScroll(false);
            }

            @Override
            public void onSwipeRight() {
            }

            @Override
            public void onSwipeLeft() {
            }

            @Override
            public void onSwipeBottom() {
                floatingButtonControll.onScroll(true);
            }
        });

        etDescr.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchListener.onTouch(v, event);
                return false;
            }
        });
    }

    public boolean onBackPressed(){
        if (isEditMode){
            enable(false);
            etTitle.setText(savedTitle);
            etDescr.setText(savedDescr);
            return false;
        }
        getActivity().getFragmentManager().popBackStack();
        return true;
    }

    public int getPos(){
        return pos;
    }

    public void setEntry(Entry entry, int pos){
        this.entry = entry;
        this.pos = pos;

        etTitle.setText(entry.title);
        etDescr.setText(entry.description);
    }

    private void enable(boolean enable){
        if (menuItemSettings != null){
            menuItemSettings.setVisible(!enable);
        }

        if (menuItemOk != null) {
            menuItemOk.setVisible(enable);
        }

        if (menuItemShare != null){
            menuItemShare.setVisible(!enable);
        }

        if (enable){
            savedTitle = etTitle.getText().toString();
            savedDescr = etDescr.getText().toString();

            etTitle.setFocusable(true);
            etTitle.setFocusableInTouchMode(true);
            etTitle.setSelected(true);
            etTitle.setActivated(true);

            etDescr.setFocusable(true);
            etDescr.setFocusableInTouchMode(true);
            etDescr.setSelected(true);
            etDescr.setActivated(true);
            editButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_ok_small));

            getActivity().getActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                    .getColor(R.color.ab_edit_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.ab_color_edit_transparent));
            }
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            getActivity().setTitle(getString(R.string.edit_details_activity));
            editButton.setColor(getResources().getColor(R.color.ab_edit_color));

            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            etDescr.requestFocus();
            inputMethodManager.showSoftInput(etDescr, InputMethodManager.SHOW_IMPLICIT);
        } else {
            etTitle.setFocusable(false);
            etTitle.setSelected(false);
            etTitle.setActivated(false);

            etDescr.setFocusable(false);
            etDescr.setSelected(false);
            etDescr.setActivated(false);
            editButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pen));

            getActivity().getActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                    .getColor(R.color.ab_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.ab_color_transparent));
            }
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

    public boolean isEditMode(){
        return isEditMode;
    }

    public void editMode(boolean land){
        Log.i("ANT", "DetailsFragment :: EDIT_MODE");
        if (land) {
            enable(true);
            return;
        }

        enableEditMode = true;
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
        setRetainInstance(false);
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
                onBackPressed();
                return true;
            case R.id.action_done:
                save();
                enable(false);
                return true;

            case R.id.action_share:
                String shareBody = etDescr.getText().toString();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, etTitle.getText().toString());
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.details_menu, menu);

        menuItemOk = menu.findItem(R.id.action_done);
        menuItemOk.setVisible(false);

        menuItemShare = menu.findItem(R.id.action_share);

        menuItemSettings = menu.findItem(R.id.action_settings);
    }

    public Entry getEntry(){
        return entry;
    }
}
