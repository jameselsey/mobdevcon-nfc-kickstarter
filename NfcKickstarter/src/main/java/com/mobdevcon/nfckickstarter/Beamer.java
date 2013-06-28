package com.mobdevcon.nfckickstarter;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class Beamer extends Activity {
    private NfcAdapter mNfcAdapter;
    private EditText et;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beamer);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        et = (EditText) findViewById(R.id.beamerMessage);
    }

    public void beamClicked(View v) {
        String message = et.getText().toString();
        Log.d("NFCKICKSTARTER", "Attempting to beam! Message is " + message);
        mNfcAdapter.setNdefPushMessage(createNdefMessage(message), this);
    }

    public NdefMessage createNdefMessage(String message) {
        NdefRecord record = NdefRecord.createMime("application/vnd.com.mobdevcon.nfckickstarter.beam", message.getBytes());
        return new NdefMessage(new NdefRecord[]{record});
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            // only one message sent during the beam
            NdefMessage msg = (NdefMessage) rawMsgs[0];
            // record 0 contains the MIME type, record 1 is the AAR, if present
            String receivedMessage = new String(msg.getRecords()[0].getPayload());
            Log.d("NFCKICKSTARTER", "Received a beamed message: " + receivedMessage);
            et.setText(receivedMessage);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }
}
