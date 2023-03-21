package edu.msu.masiakde.amiiboscanner;

import androidx.appcompat.app.AppCompatActivity;
import static edu.msu.masiakde.amiiboscanner.Utils.bytesToHexString;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ScannerActivity extends AppCompatActivity {


    public class callBackHandler implements NfcAdapter.ReaderCallback {
        @Override
        public void onTagDiscovered (Tag tag) {
            getScannerView().setAmiibo(new VirtualAmiiboFile(tag));
            stopReader();
        };
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
        //nfcAdapter.enableReaderMode(this, mCallback, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null);
    }

    /**
     * The scanner view object
     */
    private ScannerView getScannerView() {
        return (ScannerView) findViewById(R.id.scannerView);
    }

    /**
     * Text object (temp)
     */
    private TextView getTextView() {
        return (TextView) findViewById(R.id.test);
    }

    @Override
    protected void onResume() {
        super.onResume();
        assert nfcAdapter != null;
        //nfcAdapter.enableReaderMode(this, mCallback, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null);
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
        setTextView(getScannerView().GetAmiiboName());
    }

    public void setTextView(String input) {
        getTextView().setText(input);
    }
}
