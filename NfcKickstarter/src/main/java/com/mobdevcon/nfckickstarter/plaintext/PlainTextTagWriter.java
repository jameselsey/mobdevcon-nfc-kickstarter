package com.mobdevcon.nfckickstarter.plaintext;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.mobdevcon.nfckickstarter.AbstractTagWriter;
import com.mobdevcon.nfckickstarter.R;

import java.nio.charset.Charset;
import java.util.Locale;

/**
 * @author @jameselsey1986
 *         <p/>
 *         This activity demonstrates how to write plain text data to a tag
 */
public class PlainTextTagWriter extends AbstractTagWriter {

    private boolean writeModeEnabled;
    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tagwriter);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

    }

    public void writeClicked(View v) {
        displayToast("Touch and hold tag against phone to write.");
        getReadyToWrite();
    }

    /*
        This will be invoked when a new intent is received, such as discovering the tag
        you're attempting to write to
     */
    @Override
    public void onNewIntent(Intent intent) {
        if (writeModeEnabled) {
            writeModeEnabled = false;

            // Grab the tag from the intent and write to it
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            writeTag(tag);
        }
    }

    /**
     * Prepare this activity to receive any new intents, so when the tag is discovered this activity
     * will act upon that first.
     */
    private void getReadyToWrite() {
        writeModeEnabled = true;

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] filters = new IntentFilter[]{tagDetected};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, null);
    }

    /**
     * Takes a payload, locale and boolean and will encode the payload into an NdefRecord
     *
     * @param payload      String The message you want to encode
     * @param locale       Locale such as Locale.ENGLISH
     * @param encodeInUtf8 boolean UTF8 Encoding flag
     * @return an NdefRecord ready to be written to a tag.
     */
    private NdefRecord createTextRecord(String payload, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = payload.getBytes(utfEncoding);
        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);
    }

    /*
       "Slightly" more simplified than the above by hard coding locale and encoding
     */
    private NdefRecord createSimpleTextRecord(String payload) {
        byte[] langBytes = Locale.ENGLISH.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = Charset.forName("UTF-8");
        byte[] textBytes = payload.getBytes(utfEncoding);
        int utfBit = 0;
        char status = (char) (utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }

    private boolean writeTag(Tag tag) {
        EditText et = (EditText) findViewById(R.id.contents);
        NdefRecord record = createSimpleTextRecord(et.getText().toString());
        NdefMessage message = new NdefMessage(new NdefRecord[]{record});
        return writeMessageToTag(tag, message);
    }
}
