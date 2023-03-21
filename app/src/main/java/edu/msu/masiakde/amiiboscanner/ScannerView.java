package edu.msu.masiakde.amiiboscanner;

import static edu.msu.masiakde.amiiboscanner.Utils.bytesToHexString;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Scanner view class.
 */
public class ScannerView extends View {

    private VirtualAmiiboFile amiibo = null;

    private AmiiboInfo amiiboInfo = null;

    public ScannerView(Context context) {
        super(context);
        init(null, 0);
    }

    public ScannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ScannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    public void setAmiibo(VirtualAmiiboFile amiiboFile) {
        amiibo = amiiboFile;
        byte[] test = amiibo.getUUID();
        byte[] test1 = amiibo.getCharID();
        getInfoFromAPI(bytesToHexString(amiibo.getHead(), false), bytesToHexString(amiibo.getTail(), false));
        Log.w("tag-name", amiiboInfo.getName());
    }

    public String GetAmiiboName() {
        if (amiiboInfo == null) {
            return null;
        }
        else {
            return amiiboInfo.getName();
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

                post(new Runnable() {
                    @Override
                    public void run() {
                        invalidate();
                    }
                });
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
}