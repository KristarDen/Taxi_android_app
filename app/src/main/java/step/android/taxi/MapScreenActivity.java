package step.android.taxi;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MapScreenActivity extends AppCompatActivity {

    private EditText From_edit_text;
    private TextWatcher From_edittext_watcher = new TextWatcher()
    {

        public void afterTextChanged(Editable s) {
            GMapApi.FindPlaceByText(s.toString());
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getSupportActionBar().hide();
        From_edit_text = (EditText) findViewById( R.id.address_from );

        //add onTexChanged listener
        From_edit_text.addTextChangedListener(From_edittext_watcher);
    }

}
