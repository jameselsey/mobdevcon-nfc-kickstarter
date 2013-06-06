package com.mobdevcon.nfckickstarter;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.Charset;

public class UriWriter extends AbstractTagWriter {

    private boolean writeModeEnabled;
    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uriwriter);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    public void writeUriClicked(View v) {
        displayToast("Touch and hold tag against phone to write.");
        getReadyToWrite();
    }

    public void exampleTwitterClicked(View v) {
        EditText et = (EditText) findViewById(R.id.uriContents);
        et.setText("twitter://user?screen_name=jameselsey1986");
    }

    public void exampleHttpClicked(View v) {
        EditText et = (EditText) findViewById(R.id.uriContents);
        et.setText("http://www.google.com");
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


    private NdefRecord createUriRecord(String uri) {
        byte[] uriField = uri.getBytes(Charset.forName("US-ASCII"));
        byte[] payload = new byte[uriField.length + 1];              //add 1 for the URI Prefix
        System.arraycopy(uriField, 0, payload, 1, uriField.length);  //appends URI to payload
        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], payload);
    }

    private boolean writeTag(Tag tag) {
        EditText et = (EditText) findViewById(R.id.uriContents);
        NdefRecord record = createUriRecord(et.getText().toString());
        NdefMessage message = new NdefMessage(new NdefRecord[]{record});
        return writeMessageToTag(tag, message);
    }
}
