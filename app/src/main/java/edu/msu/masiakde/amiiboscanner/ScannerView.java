package edu.msu.masiakde.amiiboscanner;

import static edu.msu.masiakde.amiiboscanner.Utils.bytesToHexString;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Scanner view class.
 */
public class ScannerView extends View {

    private VirtualAmiiboFile amiibo = null;

    private AmiiboInfo amiiboInfo = null;

    private Bitmap amiiboBitmap = null;

    private final static float SCALE_DOWN = (float)0.7;

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
        amiiboBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.unknown);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        // If there is no image to draw, we do nothing
        if(amiiboBitmap == null) {
            return;
        }

        /*
         * Determine the margins and scale to draw the image
         * centered and scaled to maximum size on any display
         */
        // Get the canvas size
        float wid = getWidth();
        float hit = getHeight();

        // What would be the scale to draw the where it fits both
        // horizontally and vertically?
        float scaleH = wid / amiiboBitmap.getWidth();
        float scaleV = hit / amiiboBitmap.getHeight();

        // Use the lesser of the two
        float imageScale = Math.min(scaleH, scaleV) * SCALE_DOWN;

        // What is the scaled image size?
        float iWid = imageScale * amiiboBitmap.getWidth();
        float iHit = imageScale * amiiboBitmap.getHeight();

        // Determine the top and left margins to center
        float marginLeft = (wid - iWid) / 2;
        float marginTop = (hit - iHit) / 2;

        /*
         * Draw the image bitmap
         */
        canvas.save();
        canvas.translate(marginLeft,  marginTop);
        canvas.scale(imageScale, imageScale);
        canvas.drawBitmap(amiiboBitmap, 0, 0, null);
        canvas.restore();

        invalidate();
    }

    public void setAmiibo(VirtualAmiiboFile amiiboFile) {
        amiibo = amiiboFile;
        getInfoFromAPI(bytesToHexString(amiibo.getHead(), false), bytesToHexString(amiibo.getTail(), false));
        Log.w("tag-name", GetAmiiboName());
    }

    public String GetAmiiboName() {
        if (amiiboInfo == null) {
            return "None";
        }
        else {
            return amiiboInfo.getName();
        }
    }

    public void getInfoFromAPI(String char_head, String char_tail) {
        Thread thread = new Thread(new Runnable() {

            private String error_msg = null;

            @Override
            public void run()  {
                try {

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://amiiboapi.com/api/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    AmiiboAPI amiiboAPI = retrofit.create(AmiiboAPI.class);
                    Response<AmiiboInfo> response = amiiboAPI.getCharacter(char_head, char_tail).execute();
                    if (response.isSuccessful()) {
                        amiiboInfo = response.body();
                        if (amiiboInfo != null) {
                            URL url = new URL(amiiboInfo.getImageURL());
                            amiiboBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        }
                    }
                }
                catch (IOException e) {
                    error_msg = "Failed to reach internet";
                    Log.w("Failed API Call", e);
                }

                post(new Runnable() {
                    @Override
                    public void run() {
                        if (error_msg != null) {
                            Toast.makeText(getContext(), error_msg, Toast.LENGTH_SHORT).show();
                        }
                        ScannerActivity activity = (ScannerActivity)getContext();
                        activity.setNameView(amiiboInfo.getName());
                        activity.setSeriesView(amiiboInfo.getSeries());
                        invalidate();
                    }
                });
            }
        });
        thread.start();

    }

    public void saveAmiiboFile(String name) {

        String filename = name + ".bin";

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

        // Create a file object from the path and filename
        File file = new File(path, filename);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(amiibo.getBytes());
            fos.close();
            Toast.makeText(getContext(), "File Saved!", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Log.w("Save-Error-IO", e);
            Toast.makeText(getContext(), "Save Error", Toast.LENGTH_SHORT).show();
        }
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