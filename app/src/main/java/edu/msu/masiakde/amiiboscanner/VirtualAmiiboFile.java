package edu.msu.masiakde.amiiboscanner;

import static edu.msu.masiakde.amiiboscanner.Utils.bytesToHexString;

import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;

public class VirtualAmiiboFile {

    private static final int FILE_SIZE = 540;

    private static final int PAGE_SIZE = 4;

    private static final int BYTES_READ = PAGE_SIZE*4;

    private byte[] bin = new byte[540];

    public VirtualAmiiboFile(Tag tag) {
        Log.i("tag-found", "New tag discovered");
        MifareUltralight mifare = MifareUltralight.get(tag);
        try {
            mifare.connect();
            int i = 0;
            for (i = 0; i <= FILE_SIZE/BYTES_READ-1; i++) {
                byte[] out = mifare.readPages(i*PAGE_SIZE);
                System.arraycopy(out, 0, bin, i * BYTES_READ, BYTES_READ);
            }
            byte[] out = mifare.readPages(i*PAGE_SIZE);
            System.arraycopy(out, 0, bin, i * BYTES_READ, BYTES_READ - PAGE_SIZE);

            Log.i("tag-data", bytesToHexString(bin, true));
        }
        catch (IOException e) {
            Log.i("tag-error", e.toString());
        }
    }

    public byte[] getCharID() {
        byte[] char_id = Arrays.copyOfRange(bin, 21*4, 21*4+8);
        Log.w("Tag-char-id", bytesToHexString(char_id, true));
       return char_id;
    }

    public byte[] getHead() {
        byte[] char_id = Arrays.copyOfRange(bin, 21*4, 21*4+4);
        Log.w("Tag-head", bytesToHexString(char_id, true));
        return char_id;
    }
    public byte[] getTail() {
        byte[] char_id = Arrays.copyOfRange(bin, 21*4+4, 21*4+8);
        Log.w("Tag-tail", bytesToHexString(char_id, true));
        return char_id;
    }

    public byte[] getUUID() {
        byte[] uuid = new byte[7];
        System.arraycopy(bin, 0, uuid, 0 , 3);
        System.arraycopy(bin, 4, uuid, 3, 4);
        Log.w("Tag-uuid", bytesToHexString(uuid, true));
        return uuid;
    }
}
