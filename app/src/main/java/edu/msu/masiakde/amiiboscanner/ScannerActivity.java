package edu.msu.masiakde.amiiboscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ScannerActivity extends AppCompatActivity {


    public class callBackHandler implements NfcAdapter.ReaderCallback {
        @Override
        public void onTagDiscovered (Tag tag) {
            runToastOnUIThread("Scanning Tag");
            getScannerView().setAmiibo(new VirtualAmiiboFile(tag));
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
            Toast.makeText(this,"NO NFC Capabilities",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * The scanner view object
     */
    private ScannerView getScannerView() {
        return findViewById(R.id.scannerView);
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

    public void setNameView(String name) {
        TextView tv = (TextView)findViewById(R.id.charNameView);
        tv.setText(name);
    }

    public void setSeriesView(String series) {
        TextView tv = (TextView)findViewById(R.id.seriesNameView);
        tv.setText(series);
    }
}
