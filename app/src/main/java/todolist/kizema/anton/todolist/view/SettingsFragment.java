package todolist.kizema.anton.todolist.view;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import todolist.kizema.anton.todolist.R;
import todolist.kizema.anton.todolist.app.AppConstants;

public class SettingsFragment extends Fragment {

    private static final int MAX_TITLE_TEXT_SIZE = 25;
    private static final int MAX_DESCR_TEXT_SIZE = 18;

    private TextView tvTitleTextSize, tvDescrTextSize;
    private SeekBar seekBarTitleFont, seekBarDescrFont;

    private int titleSize, descrSize;

    private ToDoListFragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(getActivity().getResources().getString(R.string.setting_title));
        setRetainInstance(false);
        setHasOptionsMenu(true);
    }

    public void setToDoListFragment(ToDoListFragment fragment){
        this.fragment = fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tvTitleTextSize = (TextView) getActivity().findViewById(R.id.tvTitleSize);
        tvDescrTextSize = (TextView) getActivity().findViewById(R.id.tvDescrSize);

        SharedPreferences prefs = getActivity().getSharedPreferences(AppConstants.PREF_FILE_NAME, Context.MODE_PRIVATE);

        titleSize = prefs.getInt(AppConstants.TITLE_TEXT_SIZE, AppConstants.TITLE_TEXT_SIZE_DEF);
        descrSize = prefs.getInt(AppConstants.DESCR_TEXT_SIZE, AppConstants.DESCR_TEXT_SIZE_DEF);
        tvTitleTextSize.setText(getActivity().getResources().getString(R.string.title_text_size, titleSize));
        tvDescrTextSize.setText(getActivity().getResources().getString(R.string.descr_text_size, descrSize));

        seekBarTitleFont = (SeekBar) getActivity().findViewById(R.id.seekBarTitleFont);
        seekBarTitleFont.setMax(MAX_TITLE_TEXT_SIZE);
        seekBarTitleFont.setProgress(titleSize);
        seekBarTitleFont.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                titleSize = progress;
                tvTitleTextSize.setText(getActivity().getResources().getString(R.string.title_text_size, titleSize));
                if (fragment != null) {
                    saveInfoToSP();

                    fragment.updateTextSizes();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarDescrFont = (SeekBar) getActivity().findViewById(R.id.seekBarDescrFont);
        seekBarDescrFont.setMax(MAX_DESCR_TEXT_SIZE);
        seekBarDescrFont.setProgress(descrSize);
        seekBarDescrFont.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                descrSize = progress;
                tvDescrTextSize.setText(getActivity().getResources().getString(R.string.descr_text_size, descrSize));
                if (fragment != null) {
                    saveInfoToSP();

                    fragment.updateTextSizes();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            getActivity().onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();

        saveInfoToSP();
    }

    private void saveInfoToSP(){
        SharedPreferences prefs = getActivity().getSharedPreferences(AppConstants.PREF_FILE_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(AppConstants.TITLE_TEXT_SIZE, titleSize);
        editor.putInt(AppConstants.DESCR_TEXT_SIZE, descrSize);
        editor.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.prefs, container, false);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("ANT", " SETTINGS onDetach()");
        fragment = null;
    }

}
