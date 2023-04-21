package edu.msu.masiakde.amiiboscanner;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ScannerActivity extends AppCompatActivity {


    public class callBackHandler implements NfcAdapter.ReaderCallback {
        @Override
        public void onTagDiscovered (Tag tag) {
            runToastOnUIThread("Scanning Tag");
            getScannerView().setAmiibo(new VirtualAmiiboFile(tag));
            getSaveButton().setEnabled(true);
            stopReader();
            runToastOnUIThread("Tag Found");
        }
    }

    public callBackHandler mCallback = new callBackHandler();

    // Initialize attributes
    NfcAdapter nfcAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initialise NfcAdapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //If no NfcAdapter, display that the device has no NFC
        if (nfcAdapter == null){
            Toast.makeText(this,"No NFC Capabilities",
                    Toast.LENGTH_LONG).show();
            finish();
        }
        else if (!nfcAdapter.isEnabled()) {
            Snackbar.make(getScannerView(), "NFC is disabled", Snackbar.LENGTH_INDEFINITE)
                    .setAction("ENABLE", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Open NFC settings to enable it
                            Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .show();
        }

        if (savedInstanceState != null) {
            getScannerView().getFromBundle(savedInstanceState);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        getScannerView().saveInstanceState(outState);
    }

    ActivityResultLauncher<Intent> pickBinResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Uri binUri = data.getData();
                        try {
                            // Open a file input stream for the URI using the content resolver
                            ContentResolver contentResolver = getApplicationContext().getContentResolver();
                            InputStream inputStream = contentResolver.openInputStream(binUri);
                            // Create a byte array to hold the file data
                            byte[] binData = new byte[inputStream.available()];

                            inputStream.read(binData);
                            inputStream.close();

                            getScannerView().setAmiibo(new VirtualAmiiboFile(binData));
                            getSaveButton().setEnabled(true);
                        }
                        catch (IOException e) {
                            Log.w("IO Error", e);
                        }
                    }
                }
            });

    /**
     * The scanner view object
     */
    private ScannerView getScannerView() {
        return findViewById(R.id.scannerView);
    }

    private Button getSaveButton() {
        return findViewById(R.id.save_file_button);
    }

    private void runToastOnUIThread(String message) {
        runOnUiThread(() -> Toast.makeText(ScannerActivity.this, message, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onPause() {
        super.onPause();
        //On pause stop listening
        if (nfcAdapter != null) {
            nfcAdapter.disableReaderMode(this);
        }
    }

    private void stopReader() {
        nfcAdapter.disableReaderMode(this);
    }

    public void onLoadClick(View view) {
        nfcAdapter.enableReaderMode(this, mCallback, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null);
    }

    public void onLoadFileClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("application/octet-stream");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        pickBinResultLauncher.launch(intent);
    }

    public void onSaveFileClick(View view) {
        SaveDlg dlg = new SaveDlg();
        dlg.show(getSupportFragmentManager(), "save");
    }

    public void setNameView(String name) {
        TextView tv = (TextView)findViewById(R.id.charNameView);
        tv.setText(name);
    }

    public void setSeriesView(String series) {
        TextView tv = (TextView)findViewById(R.id.seriesNameView);
        tv.setText(series);
    }
}
