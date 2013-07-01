package com.mobdevcon.nfckickstarter;

import android.app.Activity;
import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.widget.Toast;

import java.io.IOException;

/**
 * @author @jameselsey1986
 *         <p/>
 *         AbstractTagWriter to hold common NFC tag writing code.
 */
public abstract class AbstractTagWriter extends Activity {

    /*
        This is the "guts" of how a tag is written to
     */
    public boolean writeMessageToTag(Tag tag, NdefMessage message) {
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();

                if (!ndef.isWritable()) {
                    displayToast("Read-only tag, unable to write.");
                    return false;
                }

                int size = message.toByteArray().length;
                if (ndef.getMaxSize() < size) {
                    displayToast("Tag doesn't have enough free space. Required: " + size + " Available: " + ndef.getMaxSize());
                    return false;
                }

                ndef.writeNdefMessage(message);
                displayToast("Tag written successfully.");
                return true;
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        displayToast("Tag written successfully!");
                        return true;
                    } catch (IOException e) {
                        displayToast("Unable to format tag to NDEF.");
                        return false;
                    }
                } else {
                    displayToast("Tag doesn't appear to support NDEF format.");
                    return false;
                }
            }
        } catch (Exception e) {
            displayToast("Failed to write tag");
        }
        return false;
    }

    public void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
