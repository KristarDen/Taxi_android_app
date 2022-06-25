package step.android.taxi;

import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.gson.Gson;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;

import org.json.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

public class RegisterActivity extends AppCompatActivity {
    Gson gson = new Gson();

    private final String register_url = "http://94.158.152.83:5000/api/auth/register/";

    boolean isEmailCheck = false;
    boolean isPhoneCheck = false;
    boolean isFirstNameCheck = false;
    boolean isLastNameCheck = false;
    boolean isPasswordCheck = false;

    AsyncHttpClient client = new AsyncHttpClient();
    RequestParams params = new RequestParams();


    private EditText email_form;
    private EditText phone_form;
    private EditText pass_form;
    private EditText pass_confirm_form;
    private EditText first_name;
    private EditText last_name;

    Pattern email = Pattern.compile("^([a-z0-9_\\.-]+)@([a-z0-9_\\.-]+)\\.([a-z\\.]{2,6})$", Pattern.CASE_INSENSITIVE);
    Pattern phone = Pattern.compile("^(\\s*)?(\\+)?([- _():=+]?\\d[- _():=+]?){12,14}(\\s*)?$");
    Pattern pass = Pattern.compile("(?=.*[0-9])(?=.*[!@#$%^&*])(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z!@#$%^&*]{6,}");
    Pattern name = Pattern.compile("[a-zA-Z]|[а-яА-я]|[а-яА-я]{2,}");
    Matcher matcher ;

    private TextWatcher Email_validation = new TextWatcher()
    {

        public void afterTextChanged(Editable s) {

            matcher =  email.matcher(s.toString());
            if( matcher.matches() ){
                email_form.setBackground(getDrawable(R.drawable.form_background));
                isEmailCheck = true;
            }
            else {
                email_form.setBackground(getDrawable(R.drawable.form_wrong));
                isEmailCheck = false;
            }
            Register_check();

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
                isPhoneCheck = true;
            }
            else {
                phone_form.setBackground(getDrawable(R.drawable.form_wrong));
                isPhoneCheck = false;
            }
            Register_check();
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
            Register_check();
    }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    };

    private TextWatcher Pass_confirm_validation = new TextWatcher()
    {

        public void afterTextChanged(Editable s) {

            matcher =  pass.matcher(s.toString());
            if( matcher.matches() && pass_confirm_form.getText().toString()
                    .equals( pass_form.getText().toString() )  ){
                pass_confirm_form.setBackground(getDrawable(R.drawable.form_background));
                isPasswordCheck = true;
            }
            else {
                pass_confirm_form.setBackground(getDrawable(R.drawable.form_wrong));
                isPasswordCheck = false;
            }
            Register_check();
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    };

    private TextWatcher First_name_validation = new TextWatcher()
    {

        public void afterTextChanged(Editable s) {

            matcher =  name.matcher(s.toString());
            if( matcher.matches()){
                first_name.setBackground(getDrawable(R.drawable.form_background));
                isFirstNameCheck = true;
            }
            else {
                first_name.setBackground(getDrawable(R.drawable.form_wrong));
                isFirstNameCheck = false;
            }
            Register_check();
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    };

    private TextWatcher Last_name_validation = new TextWatcher()
    {

        public void afterTextChanged(Editable s) {

            matcher =  name.matcher(s.toString());
            if( matcher.matches()){
                last_name.setBackground(getDrawable(R.drawable.form_background));
                isLastNameCheck = true;
            }
            else {
                last_name.setBackground(getDrawable(R.drawable.form_wrong));
                isLastNameCheck = false;
            }
            Register_check();
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    };


    Button RegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);
        getSupportActionBar().hide();

        email_form = (EditText) findViewById( R.id.email );
        phone_form = (EditText) findViewById( R.id.phone_number);
        pass_form = (EditText) findViewById( R.id.password);
        pass_confirm_form = (EditText) findViewById( R.id.confirmPassword);
        first_name = (EditText) findViewById( R.id.first_name);
        last_name = (EditText) findViewById( R.id.last_name);


        email_form.addTextChangedListener(Email_validation);//Email form validation
        phone_form.addTextChangedListener(Phone_validation);//Phone form validation
        pass_form.addTextChangedListener(Pass_validation);
        pass_confirm_form.addTextChangedListener(Pass_confirm_validation);
        first_name.addTextChangedListener(First_name_validation);
        last_name.addTextChangedListener(Last_name_validation);

        RegisterButton = findViewById( R.id.registerButton );
        RegisterButton.setOnClickListener( this::Register_click ) ;

        Glide.with(this)
                .load(R.drawable.ic_background_img)
                .transform(new BlurTransformation(this))
                .into((ImageView) findViewById(R.id.screen));

    }

    void Register_click (View v){
        Toast toast = Toast.makeText(this, "Register click",Toast.LENGTH_LONG);
        toast.show();
        RegisterFormData newUser = new RegisterFormData();

        newUser.phone = phone_form.getText().toString();
        newUser.email = email_form.getText().toString();
        newUser.name = first_name.getText().toString();
        newUser.surname = last_name.getText().toString();
        newUser.password = pass_form.getText().toString();


        Thread Send = new Thread (()->{
            try {
                String res;// response from post request
                res = Network.POST( register_url ,gson.toJson( newUser ));

                JSONObject json = new JSONObject(res);
                res = json.get("token").toString();
                UserInfo.setAuthToken(res);
                UserInfo.setEmail(newUser.email);
                Log.i("response : ",res);
                this.startActivity(
                        new Intent(
                                this,
                                RegisterCodeActivity.class ) ) ;
            } catch (Exception e) {
                //Toast.makeText(this, e.getMessage(),Toast.LENGTH_LONG).show();
                 this.startActivity(
                        new Intent(
                                this,
                                MapsActivity.class ) ) ;
            }
        });
        Send.start();




    }

    void Register_check(){
        if(isEmailCheck == true &&
        isPhoneCheck == true &&
        isFirstNameCheck == true &&
        isLastNameCheck == true &&
        isPasswordCheck == true ){
            RegisterButton.setEnabled(true);
        }
        else {
            RegisterButton.setEnabled(false);
        }
    }

    public class BlurTransformation extends BitmapTransformation {

        private RenderScript rs;

        public BlurTransformation(Context context) {
            super();

            rs = RenderScript.create(context);
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            Bitmap blurredBitmap = toTransform.copy(Bitmap.Config.ARGB_8888, true);

            // Allocate memory for Renderscript to work with
            Allocation input = Allocation.createFromBitmap(rs, blurredBitmap, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SHARED);
            Allocation output = Allocation.createTyped(rs, input.getType());

            // Load up an instance of the specific script that we want to use.
            ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setInput(input);

            // Set the blur radius
            script.setRadius(21.5f);

            // Start the ScriptIntrinisicBlur
            script.forEach(output);

            // Copy the output to the blurred bitmap
            output.copyTo(blurredBitmap);

            return blurredBitmap;
        }

        @Override
        public void updateDiskCacheKey(MessageDigest messageDigest) {
            messageDigest.update("blur transformation".getBytes());
        }
    }


}