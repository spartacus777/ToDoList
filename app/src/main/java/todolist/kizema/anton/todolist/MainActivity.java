package todolist.kizema.anton.todolist;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import java.lang.reflect.Field;

import todolist.kizema.anton.todolist.model.Entry;
import todolist.kizema.anton.todolist.model.EntryPool;
import todolist.kizema.anton.todolist.view.DetailsFragment;
import todolist.kizema.anton.todolist.view.EditEntryDialogFragment;
import todolist.kizema.anton.todolist.view.SettingsFragment;
import todolist.kizema.anton.todolist.view.ToDoListFragment;

import static todolist.kizema.anton.todolist.view.DetailsFragment.OnUpdateAdapterListener;


public class MainActivity extends Activity implements ToDoListFragment.OnToDoSelectedListener,
        EditEntryDialogFragment.OnDilaogListener, FragmentManager.OnBackStackChangedListener, OnUpdateAdapterListener {

    private static final String LISTFRAGMENT = "LISTFRAGMENT";
    private static final String SETTINGS_FRAGMENT = "SETTINGS_FRAGMENT";
    private static final String DETAILS_FRAGMENT = "DETAILS_FRAGMENT";

    private SettingsFragment settingsFragment;
    private ToDoListFragment fragment;
    private DetailsFragment detailsFragment;
    private FrameLayout frameDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("ANT", "MainActivity onCreate()");
        makeActionOverflowMenuShown();

        getFragmentManager().addOnBackStackChangedListener(this);
        fragment = (ToDoListFragment) getFragmentManager().findFragmentByTag(LISTFRAGMENT);
        frameDetailsFragment = (FrameLayout) findViewById(R.id.detailsFragment);
        detailsFragment = (DetailsFragment) getFragmentManager().findFragmentByTag(DETAILS_FRAGMENT);
        settingsFragment = (SettingsFragment) getFragmentManager().findFragmentByTag(SETTINGS_FRAGMENT);

        if (fragment == null){
            fragment = new ToDoListFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, fragment, LISTFRAGMENT)
                    .commit();
        }

        if (getFragmentManager().getBackStackEntryCount() > 0 ) {
            getFragmentManager().popBackStackImmediate();
        }
        destroyTmpFragments();

        if (frameDetailsFragment != null){
            Log.i("ANT", "frameDetailsFragment != null");

            if (EntryPool.getPool().getEntries().size() > 0) {
                if (detailsFragment == null) {
                    detailsFragment = DetailsFragment.newInstance(EntryPool.getPool().getEntries().get(0), 0);
                }

                getFragmentManager().beginTransaction()
                        .replace(R.id.detailsFragment, detailsFragment, DETAILS_FRAGMENT)
                        .commit();
            }
        }

        getActionBar().setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            View container = findViewById(R.id.container);
            container.setElevation(30);
        }
    }

    @Override
    protected void onResume() {
        EntryPool.getPool().load();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EntryPool.getPool().save();
    }

    private void destroyTmpFragments(){
        if (detailsFragment != null && !detailsFragment.isVisible()) {
            Log.d("ANT", "detailsFragment != null, Destroying");
            getFragmentManager().beginTransaction()
                    .remove(detailsFragment)
                    .commit();

            detailsFragment = null;
        }

        if (settingsFragment != null && !settingsFragment.isVisible()) {
            Log.d("ANT", "settingsFragment != null, Destroying");
            getFragmentManager().beginTransaction()
                    .remove(settingsFragment)
                    .commit();

            settingsFragment = null;
        }
    }

    private void makeActionOverflowMenuShown() {
        //devices with hardware menu button (e.g. Samsung Note) don't show action overflow menu
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {}
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            if ( SETTINGS_FRAGMENT.equalsIgnoreCase(getFragmentManager().
                    getBackStackEntryAt(getFragmentManager().getBackStackEntryCount()-1).getName())){
                fragment.updateTextSizes();
                getFragmentManager().popBackStack();
                return;
            }

            if ( DETAILS_FRAGMENT.equalsIgnoreCase(getFragmentManager().
                    getBackStackEntryAt(getFragmentManager().getBackStackEntryCount()-1).getName())){
                detailsFragment.onBackPressed();
                return;
            }
        } else {

            if (frameDetailsFragment != null && detailsFragment != null && detailsFragment.isEditMode()){
                detailsFragment.onBackPressed();
                return;
            }

            super.onBackPressed();
        }
    }

    @Override
    public void onToDoSelected(Entry selectedEntry, int pos) {
        Log.i("ANT", "POS::"+pos);

        if (frameDetailsFragment != null){
            detailsFragment.setEntry(selectedEntry, pos);
        } else {//portrait
            detailsFragment = (DetailsFragment) getFragmentManager().findFragmentByTag(DETAILS_FRAGMENT);
            if (detailsFragment == null) {
                detailsFragment = DetailsFragment.newInstance(selectedEntry, pos);
            } else {
                detailsFragment.setEntry(selectedEntry, pos);
            }

            getFragmentManager().beginTransaction()
                    .replace(R.id.container, detailsFragment, DETAILS_FRAGMENT)
                    .addToBackStack(DETAILS_FRAGMENT)
                    .commit();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("ANT", "onRestoreInstanceState");
        int pos = savedInstanceState.getInt("KEY", 0);
        boolean isEditMode = savedInstanceState.getBoolean("EDIT_MODE");

        if (frameDetailsFragment != null) {

            onToDoSelected(EntryPool.getPool().getEntries().get(pos), pos);

            if (isEditMode){
                detailsFragment.editMode(true);
            }
        }

        if (isEditMode){
            if (frameDetailsFragment == null ){
                onToDoSelected(EntryPool.getPool().getEntries().get(pos), pos);
                detailsFragment.editMode(false);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d("ANT", "onSaveInstanceState");
        if (detailsFragment != null) {
            outState.putInt("KEY", detailsFragment.getPos());
            if  ( detailsFragment.isEditMode() ){
                outState.putBoolean("EDIT_MODE", true);
            }
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSettingsPressed() {
        settingsFragment = new SettingsFragment();

        if (frameDetailsFragment == null){
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, settingsFragment, SETTINGS_FRAGMENT);
            transaction.addToBackStack(SETTINGS_FRAGMENT);
            transaction.commit();
        } else {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.detailsFragment, settingsFragment, SETTINGS_FRAGMENT);
            transaction.addToBackStack(SETTINGS_FRAGMENT);
            settingsFragment.setToDoListFragment(fragment);
            transaction.commit();
        }
    }

    @Override
    public void onRemove(Entry entry) {
        if (frameDetailsFragment != null && detailsFragment.getEntry().equals(entry)) {
            if (EntryPool.getPool().getEntries().size() > 0) {
                detailsFragment.setEntry(EntryPool.getPool().getEntries().get(0), 0);
            } else {
                getFragmentManager().beginTransaction()
                        .remove(detailsFragment)
                        .commit();
            }
        }
    }

    @Override
    public void onOkBtnPressed(Entry entry, String title, String descr) {
        fragment.onOkBtnPressed(entry, title, descr);

        if (frameDetailsFragment != null){

            if (detailsFragment == null){
                detailsFragment = DetailsFragment.newInstance(EntryPool.getPool().getEntries().get(0), 0);
            }

            if (detailsFragment != null && !detailsFragment.isAdded()) {
                getFragmentManager().beginTransaction()
                        .add(R.id.detailsFragment, detailsFragment, DETAILS_FRAGMENT)
                        .commit();
            } else {
                int last = EntryPool.getPool().getEntries().size() - 1;
                detailsFragment.setEntry(EntryPool.getPool().getEntries().get(last), last);
            }
        }
    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    public void shouldDisplayHomeUp(){
        getActionBar().setDisplayHomeAsUpEnabled(getFragmentManager().getBackStackEntryCount()>0);
    }

    @Override
    public void onUpdate() {
        if (fragment != null){
            fragment.notifyDataSetChanged();
        }
    }
}
