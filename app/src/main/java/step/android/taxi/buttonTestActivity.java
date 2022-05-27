package step.android.taxi;

import android.os.Bundle;
import android.os.Debug;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Console;
import java.time.Duration;

public class buttonTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button button = (Button) findViewById(R.id.testbtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Toast toast = Toast.makeText(v.getContext(),"Click",Toast.LENGTH_LONG);

                toast.show();
            }
        });
    }
}
