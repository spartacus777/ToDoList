package todolist.kizema.anton.todolist.view;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import todolist.kizema.anton.todolist.R;

public class AddEntryDialogFragment extends DialogFragment implements View.OnClickListener {
    public static final String TITLE = "TITLE";
    public static final String DESCR = "DESCR";

    private EditText title, descr;
    private OnDilaogListener listener;

    public interface OnDilaogListener {
        void onOkBtnPressed(String title, String descr);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i("ANT", "activity==null : " + (activity==null ? "TRUE":"FALSE") + "AddEntryDialogFragment :: onAttach");
        listener = (OnDilaogListener) activity;

        Log.i("ANT", "listener==null : " + (listener==null ? "TRUE":"FALSE"));
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(getResources().getString(R.string.dialog_title));
        View v = inflater.inflate(R.layout.add_entry, null);
        title = (EditText) v.findViewById(R.id.etTitle);
        descr = (EditText) v.findViewById(R.id.etDescr);

        v.findViewById(R.id.btnCancel).setOnClickListener(this);
        v.findViewById(R.id.btnOk).setOnClickListener(this);
        return v;
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnOk:
                dismiss();
                listener.onOkBtnPressed(title.getText().toString(), descr.getText().toString());
                break;
            case R.id.btnCancel:
                dismiss();
                break;
        }
    }
}