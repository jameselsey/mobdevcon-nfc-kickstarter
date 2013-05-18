package com.mobdevcon.nfckickstarter;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void writeTag(View v) {
        Intent i = new Intent(this, PlainTextTagWriter.class);
        startActivity(i);
    }

    public void writeAar(View v){
        Intent i = new Intent(this, AarWriter.class);
        startActivity(i);
    }
    
}
