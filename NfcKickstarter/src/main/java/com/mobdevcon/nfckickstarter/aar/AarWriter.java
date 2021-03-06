package com.mobdevcon.nfckickstarter.aar;

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

/**
 * @author @jameselsey1986
 *         <p/>
 *         This class demonstrates how to write an AAR record onto a tag. Firstly we must set a PendingIntent
 *         so that when a tag is discovered we can intercept the intent and write to the tag.
 *         <p/>
 *         Creating an AAR is as easy as instantiating an NdefRecord from a package name.
 *         <p/>
 *         When the tag is scanned, it will attempt to load the application by searching the device for the package
 *         name embedded in the AAR. If the application is not available on the device, the marketplace will be opened
 *         and the application searched for, ready to download.
 */
public class AarWriter extends AbstractTagWriter {

    private boolean writeModeEnabled;
    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aarwriter);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

    }

    public void writeAarClicked(View v) {
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

    private boolean writeTag(Tag tag) {
        EditText packageNameEditText = (EditText) findViewById(R.id.aarPackageName);
        NdefRecord record = NdefRecord.createApplicationRecord(packageNameEditText.getText().toString());
        NdefMessage message = new NdefMessage(new NdefRecord[]{record});
        return writeMessageToTag(tag, message);
    }
}
