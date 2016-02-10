package bg.dalexiev.bender;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.EditText;

public class AddToDoDialog extends DialogFragment {

    public static final String TAG = "addToDoDialog";

    private Listener mListener;

    public static AddToDoDialog newInstance(Listener listener) {
        AddToDoDialog dialog = new AddToDoDialog();
        dialog.mListener = listener;
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final EditText toDoEditText = new EditText(getActivity());
        
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.addDialogTitle)
                .setView(toDoEditText)
                .setPositiveButton(R.string.addDialogPositiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String text = toDoEditText.getText().toString();
                        if (!TextUtils.isEmpty(text)) {
                            mListener.onToDoTitleEntered(text);
                            dismiss();
                        }
                    }
                })
                .setNegativeButton(R.string.addDialogNegativeButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .create();
    }

    public interface Listener {

        void onToDoTitleEntered(String toDo);

    }
}
