package com.zhijie.meditation.focus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button recordData = findViewById(R.id.start);
        recordData.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start) {
                Intent myIntent = new Intent(MainActivity.this,
                        ActivityRealTimeEEGClassifier.class);
                startActivity(myIntent);
        }
    }
}
