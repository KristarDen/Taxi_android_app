package step.android.taxi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    boolean isPhoneCheck = false;
    boolean isPasswordCheck = false;

    private EditText phone_form;
    private EditText pass_form;

    Pattern phone = Pattern.compile("^(\\s*)?(\\+)?([- _():=+]?\\d[- _():=+]?){12,14}(\\s*)?$");
    Pattern pass = Pattern.compile("(?=.*[0-9])(?=.*[!@#$%^&*])(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z!@#$%^&*]{6,}");
    Matcher matcher ;

    private TextWatcher Phone_validation = new TextWatcher()
    {

        public void afterTextChanged(Editable s) {

            matcher =  phone.matcher(s.toString());
            if( matcher.matches()){
                phone_form.setBackground(getDrawable(R.drawable.form_background));
                isPhoneCheck = true;
            }
            else {
                phone_form.setBackground(getDrawable(R.drawable.form_wrong));
                isPhoneCheck = false;
            }
            Login_check();
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
                isPasswordCheck = true;
            }
            else {
                pass_form.setBackground(getDrawable(R.drawable.form_wrong));
                isPasswordCheck = false;
            }
            Login_check();
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    };

    Button LoginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phone_form = (EditText) findViewById( R.id.phone_number);
        pass_form = (EditText) findViewById( R.id.password);

        phone_form.addTextChangedListener(Phone_validation);//Phone form validation
        pass_form.addTextChangedListener(Pass_validation);

        LoginButton = findViewById( R.id.loginButton );
        LoginButton.setOnClickListener( this::Login_click ) ;
    }

    private void Login_check(){
        if(isPhoneCheck == true &&
                isPasswordCheck == true ){
            LoginButton.setEnabled(true);
        }
        else {
            LoginButton.setEnabled(false);
        }
    }

    private void Login_click(View v){
        Toast.makeText(this, "Login click", Toast.LENGTH_SHORT).show();
    }
}