package edu.msu.masiakde.amiiboscanner;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private VirtualAmiiboFile amiibo = null;

    public class callBackHandler implements NfcAdapter.ReaderCallback {
        @Override
        public void onTagDiscovered (Tag tag) {
            amiibo = new VirtualAmiiboFile(tag);
            byte[] test = amiibo.getUUID();
            byte[] test1 = amiibo.getCharID();
        };
    }

    public callBackHandler mCallback = new callBackHandler();

    //Intialize attributes
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
        nfcAdapter.enableReaderMode(this, mCallback, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null);

    }

    @Override
    protected void onResume() {
        super.onResume();
        assert nfcAdapter != null;
        nfcAdapter.enableReaderMode(this, mCallback, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //On pause stop listening
        if (nfcAdapter != null) {
            nfcAdapter.disableReaderMode(this);
        }
    }

    public void getInfoFromAPI(String char_head, String char_tail) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                String query = "https://amiiboapi.com/api/amiibo/?name=peach";

                try {
                    URL url = new URL(query);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    int responseCode = conn.getResponseCode();
                    if(responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream stream = conn.getInputStream();
                        String result = streamToString(stream);
                    }
                }
                catch (MalformedURLException  e) {
                    Log.w("API-Error", e);
                }
                catch (IOException ew) {
                    Log.w("API-Error-IO", ew);
                }
            }
        });
        thread.start();

    }

    public String streamToString(InputStream inputStream) {
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length; (length = inputStream.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }
            // StandardCharsets.UTF_8.name() > JDK 7
            return result.toString("UTF-8");
        }
        catch (IOException e) {
            Log.w("API-Error-IO", e);
        }
        return null;
    }

    public void onLoadClick(View view) {
        //onResume();
        getInfoFromAPI("test", "ing");
    }
}
