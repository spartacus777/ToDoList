package todolist.kizema.anton.todolist.view;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import todolist.kizema.anton.todolist.R;
import todolist.kizema.anton.todolist.model.Entry;

public class EditEntryDialogFragment extends DialogFragment implements View.OnClickListener {
    public static final String TITLE = "TITLE";
    public static final String DESCR = "DESCR";
    public static final String ENTRY = "Entry";

    private EditText title, descr;
    private OnDilaogListener listener;

    public interface OnDilaogListener {
        void onOkBtnPressed(Entry entry, String title, String descr);
    }

    public static EditEntryDialogFragment newInstance(Entry entry) {
        EditEntryDialogFragment f = new EditEntryDialogFragment();

        Bundle bndl = new Bundle();
        bndl.putSerializable(ENTRY, entry);
        f.setArguments(bndl);

        return f;
    }

    public boolean hasArgument(){
        return ( null != getArguments() && null != getArguments().getSerializable(ENTRY));
    }

    public Entry getEntry() {
        if (!hasArgument()){
            return null;
        }

        return ((Entry) getArguments().getSerializable(ENTRY));
    }

    public String getEntryTitle() {
        return ((Entry) getArguments().getSerializable(ENTRY)).title;
    }

    public String getEntryDescr() {
        return ((Entry) getArguments().getSerializable(ENTRY)).description;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        listener = (OnDilaogListener) activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(getResources().getString(R.string.dialog_title));
        View v = inflater.inflate(R.layout.add_entry, null);
        title = (EditText) v.findViewById(R.id.etTitle);
        descr = (EditText) v.findViewById(R.id.etDescr);

        if (hasArgument()){
            getDialog().setTitle(getResources().getString(R.string.change_dialog_title));
            title.setText(getEntryTitle());
            descr.setText(getEntryDescr());
        }

        v.findViewById(R.id.btnCancel).setOnClickListener(this);
        v.findViewById(R.id.btnOk).setOnClickListener(this);
        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnOk:
                dismiss();
                listener.onOkBtnPressed(getEntry(), title.getText().toString(), descr.getText().toString());
                break;
            case R.id.btnCancel:
                dismiss();
                break;
        }
    }
}