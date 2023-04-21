package edu.msu.masiakde.amiiboscanner;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

public class SaveDlg extends DialogFragment {
    /**
     * Set true if we want to cancel
     */
    private volatile boolean cancel = false;

    private AlertDialog dlg;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        cancel = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title
        builder.setTitle(R.string.save_dlg);

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Pass null as the parent view because its going in the dialog layout
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.save_dlg, null);
        builder.setView(view);

        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        cancel = true;
                        EditText editName = (EditText)dlg.findViewById(R.id.editName);
                        save(editName.getText().toString());
                    }
                });

        builder.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        cancel = true;
                    }
                });

        dlg = builder.create();
        return dlg;
    }

    /**
     * Actually save the hatting
     * @param name name to save it under
     */
    private void save(final String name) {
        if (!(getActivity() instanceof ScannerActivity)) {
            return;
        }
        final ScannerActivity activity = (ScannerActivity) getActivity();
        final ScannerView view = (ScannerView) activity.findViewById(R.id.scannerView);
        view.saveAmiiboFile(name);
    }
}
