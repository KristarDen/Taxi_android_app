package step.android.taxi;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    private EditText email_form;
    private EditText phone_form;
    private EditText pass_form;
    private EditText pass_confirm_form;

    Pattern email = Pattern.compile("^([a-z0-9_\\.-]+)@([a-z0-9_\\.-]+)\\.([a-z\\.]{2,6})$", Pattern.CASE_INSENSITIVE);
    Pattern phone = Pattern.compile("^(\\s*)?(\\+)?([- _():=+]?\\d[- _():=+]?){12,14}(\\s*)?$");
    Pattern pass = Pattern.compile("(?=.*[0-9])(?=.*[!@#$%^&*])(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z!@#$%^&*]{6,}");
    Matcher matcher ;

    private TextWatcher Email_validation = new TextWatcher()
    {

        public void afterTextChanged(Editable s) {

            matcher =  email.matcher(s.toString());
            if( matcher.matches()){
                email_form.setBackground(getDrawable(R.drawable.form_background));
            }
            else {
                email_form.setBackground(getDrawable(R.drawable.form_wrong));
            }

        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    };

    private TextWatcher Phone_validation = new TextWatcher()
    {

        public void afterTextChanged(Editable s) {

            matcher =  phone.matcher(s.toString());
            if( matcher.matches()){
                phone_form.setBackground(getDrawable(R.drawable.form_background));
            }
            else {
                phone_form.setBackground(getDrawable(R.drawable.form_wrong));
            }

        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    };

    private TextWatcher Pass_validation = new TextWatcher()
    {

        public void afterTextChanged(Editable s) {

        matcher =  pass.matcher(s.toString());
        if( matcher.matches()){
            pass_form.setBackground(getDrawable(R.drawable.form_background));
        }
        else {
            pass_form.setBackground(getDrawable(R.drawable.form_wrong));
        }

    }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    };

    private TextWatcher Pass_confirm_validation = new TextWatcher()
    {

        public void afterTextChanged(Editable s) {

            matcher =  pass.matcher(s.toString());
            if( matcher.matches() && pass_confirm_form.getText().toString().equals( pass_form.getText().toString() )  ){
                pass_confirm_form.setBackground(getDrawable(R.drawable.form_background));
            }
            else {
                pass_confirm_form.setBackground(getDrawable(R.drawable.form_wrong));
            }

        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);
        getSupportActionBar().hide();

        email_form = (EditText) findViewById( R.id.email );
        phone_form = (EditText) findViewById( R.id.phone_number);
        pass_form = (EditText) findViewById( R.id.password);
        pass_confirm_form = (EditText) findViewById( R.id.confirmPassword);


        email_form.addTextChangedListener(Email_validation);//Email form validation
        phone_form.addTextChangedListener(Phone_validation);//Phone form validation
        pass_form.addTextChangedListener(Pass_validation);
        pass_confirm_form.addTextChangedListener(Pass_confirm_validation);
    }





}