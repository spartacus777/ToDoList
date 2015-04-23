package todolist.kizema.anton.todolist;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import java.lang.reflect.Field;

import todolist.kizema.anton.todolist.model.Entry;
import todolist.kizema.anton.todolist.model.EntryPool;
import todolist.kizema.anton.todolist.view.EditEntryDialogFragment;

import static todolist.kizema.anton.todolist.DetailsFragment.OnUpdateAdapterListener;


public class MainActivity extends Activity implements ToDoListFragment.OnToDoSelectedListener,
        EditEntryDialogFragment.OnDilaogListener, FragmentManager.OnBackStackChangedListener, OnUpdateAdapterListener {

    private static final String LISTFRAGMENT = "LISTFRAGMENT";
    private static final String SETTINGS_FRAGMENT = "SETTINGS_FRAGMENT";
    private static final String DETAILS_FRAGMENT = "DETAILS_FRAGMENT";

    private ToDoListFragment fragment;
    private DetailsFragment detailsFragment;
    private FrameLayout frameDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("ANT", "MainActivity oncretae");
        makeActionOverflowMenuShown();

        getFragmentManager().addOnBackStackChangedListener(this);
        fragment = (ToDoListFragment) getFragmentManager().findFragmentByTag(LISTFRAGMENT);
        if (fragment == null){
            createFragment();
        }

        frameDetailsFragment = (FrameLayout) findViewById(R.id.detailsFragment);
        Log.i("ANT", "MainActivity 2");

        if (frameDetailsFragment != null){
            if (getFragmentManager().getBackStackEntryCount() > 0 ) {
                getFragmentManager().popBackStack();
            }

            if (EntryPool.getPool().getEntries().size() > 0) {
                detailsFragment = DetailsFragment.newInstance(EntryPool.getPool().getEntries().get(0), 0);

                getFragmentManager().beginTransaction()
                        .add(R.id.detailsFragment, detailsFragment, DETAILS_FRAGMENT)
                        .commit();
            }
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

    private void createFragment(){
        fragment = new ToDoListFragment();

        getFragmentManager().beginTransaction()
                .add(R.id.container, fragment, LISTFRAGMENT)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            if ( SETTINGS_FRAGMENT.equalsIgnoreCase(getFragmentManager().
                    getBackStackEntryAt(getFragmentManager().getBackStackEntryCount()-1).getName())){
                fragment.updateTextSizes();
            }

            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
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

        int pos = savedInstanceState.getInt("KEY", 0);
        if (pos != 0 && frameDetailsFragment != null) {//land
            onToDoSelected(EntryPool.getPool().getEntries().get(pos), pos);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (detailsFragment != null) {
            outState.putInt("KEY", detailsFragment.getPos());

            getFragmentManager().beginTransaction()
                    .remove(detailsFragment)
                    .commit();
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSettingsPressed() {
        SettingsFragment settingsFragment = new SettingsFragment();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, settingsFragment, SETTINGS_FRAGMENT);
        transaction.addToBackStack(SETTINGS_FRAGMENT);
        transaction.commit();
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
        if (frameDetailsFragment != null && !detailsFragment.isAdded()){
            getFragmentManager().beginTransaction()
                    .add(R.id.detailsFragment, detailsFragment, DETAILS_FRAGMENT)
                    .commit();
        }

        fragment.onOkBtnPressed(entry, title, descr);
    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    public void shouldDisplayHomeUp(){
        boolean canback = getFragmentManager().getBackStackEntryCount()>0;
        getActionBar().setDisplayHomeAsUpEnabled(canback);
        getActionBar().setDisplayShowHomeEnabled(canback);
    }

    @Override
    public void onUpdate() {
        if (fragment != null){
            fragment.notifyDataSetChanged();
        }
    }
}
