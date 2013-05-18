package com.mobdevcon.nfckickstarter;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * This activity is spawned when tags of mime type text/plain are discovered, hence we can assume the tags
 * are of that type when reading their contents.
 *
 * (check AndroidManifest.xml for mime configuration)
 */
public class PlainTextTagReader extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tagdiscovered);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        List<NdefMessage> messages = new ArrayList<NdefMessage>();

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                for (Parcelable parcelable : rawMsgs){
                    messages.add((NdefMessage) parcelable);
                }
            }
        }

        displayRecords(messages);
        displayTextPayloads(messages);
    }

    /*
        This will display the contents of each NdefRecord so you can see the tnf and raw payload
     */
    private void displayRecords(List<NdefMessage> msgs) {
        StringBuilder sb = new StringBuilder();
        for (NdefMessage message : msgs){
            for (NdefRecord record : message.getRecords()){
                sb.append(record.toString());
            }
        }

        TextView tv = (TextView) findViewById(R.id.tags);
        tv.setText(sb.toString());
    }

    /*
        This will display the payload once decoded into a String
     */
    private void displayTextPayloads(List<NdefMessage> msgs) {
        StringBuilder sb = new StringBuilder();
        for (NdefMessage message : msgs){
            for (NdefRecord record : message.getRecords()){
                String text = getStringFromPayload(record.getPayload());
                sb.append(text + "\n");
            }
        }

        TextView tv = (TextView) findViewById(R.id.payloads);
        tv.setText(sb.toString());
    }

    /*
        Utility method to convert the payload byte array into a human readable String

        Without decoding, your message would appear as :
        enMy message here

     */
    private String getStringFromPayload(byte[] payload){
        String encoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
        int langageCodeLength = payload[0] & 0077;
        try {
            return new String(payload, langageCodeLength + 1, payload.length - langageCodeLength - 1, encoding);
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(this, "Unable to decode message", Toast.LENGTH_SHORT).show();
            return "";
        }
    }
}

