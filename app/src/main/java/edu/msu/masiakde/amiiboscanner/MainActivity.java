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

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private VirtualAmiiboFile amiibo = null;

    private AmiiboInfo amiiboInfo = null;

    public class callBackHandler implements NfcAdapter.ReaderCallback {
        @Override
        public void onTagDiscovered (Tag tag) {
            amiibo = new VirtualAmiiboFile(tag);
            byte[] test = amiibo.getUUID();
            byte[] test1 = amiibo.getCharID();
            getInfoFromAPI(bytesToHexString(amiibo.getHead(), false), bytesToHexString(amiibo.getTail(), false));
            Log.w("tag-name", amiiboInfo.getName());
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
            public void run()  {
                try {
                    String query = "https://amiiboapi.com/api/amiibo/?name=peach";

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://amiiboapi.com/api/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    AmiiboAPI amiiboAPI = retrofit.create(AmiiboAPI.class);
                    Response<AmiiboInfo> response = amiiboAPI.getCharacter(char_head, char_tail).execute();
                    if (response.isSuccessful()) {
                        amiiboInfo = response.body();
                    }
                }
                catch (IOException e) {
                    Log.w("Failed API Call", e);
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
    }
}
