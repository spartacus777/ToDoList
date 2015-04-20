package todolist.kizema.anton.todolist;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import todolist.kizema.anton.todolist.view.AddEntryDialogFragment;


public class MainActivity extends ActionBarActivity implements ToDoListFragment.OnToDoSelectedListener, AddEntryDialogFragment.OnDilaogListener {

    private static final String LISTFRAGMENT = "LISTFRAGMENT";

    private ToDoListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            fragment = new ToDoListFragment();

            getFragmentManager().beginTransaction()
                    .add(R.id.container, fragment, LISTFRAGMENT)
                    .commit();
        } else {
            fragment = (ToDoListFragment) getFragmentManager().findFragmentByTag(LISTFRAGMENT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onToDoSelected() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onOkBtnPressed(String title, String descr) {
        fragment.onOkBtnPressed(title, descr);
    }
}
