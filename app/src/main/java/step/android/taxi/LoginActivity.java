package step.android.taxi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import org.json.JSONObject;

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
        getSupportActionBar().hide();

        phone_form = (EditText) findViewById( R.id.phone_number);
        pass_form = (EditText) findViewById( R.id.password);

        phone_form.addTextChangedListener(Phone_validation);//Phone form validation
        pass_form.addTextChangedListener(Pass_validation);

        LoginButton = findViewById( R.id.loginButton );
        LoginButton.setOnClickListener( this::Login_click ) ;

        Glide.with(this)
                .load(R.drawable.ic_background_img)
                .transform(new BlurTransformation(this))
                .into((ImageView) findViewById(R.id.screen));
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
        String loginUrl = getString(R.string.login_url);
        Thread Send = new Thread (()->{
            try {
                String res;// response from post request
                res = Network.POST( loginUrl ,"{" +
                        "\"phone\" : "+ "\"" + phone_form.getText() + "\"" + ","+
                        "\"password\" : " + "\"" + pass_form.getText() + "\"" +
                        "}");

                JSONObject json = new JSONObject(res);
                res = json.getString("token");
                UserInfo.setAuthToken(res);
                JSONObject info_json;


                //get user info from server by token
                info_json = Network.GetUserInfo(getString(R.string.getPhoto_url),res);
                JSONObject user_json = info_json.getJSONObject("user");
                UserInfo.setName(user_json.getString("name"));
                UserInfo.setSurname(user_json.getString("surname"));
                UserInfo.setPhone(user_json.getString("phone"));
                UserInfo.setEmail(user_json.getString("email"));


                //decode utf string with photo to bitmap and save to UserInfo
                try {
                    String photo = info_json.getString("photo");
                    UserInfo.setPhoto( StringToBitMap(photo) );
                } catch (Exception ex){

                }

                Log.i("response : ",res);
                this.startActivity(
                        new Intent(
                                this,
                                MapsActivity.class ) ) ;
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
    public Bitmap StringToBitMap(String imageString) {
        /*try {
            byte[] encodeByte = imageString.getBytes("UTF-8");
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,encodeByte.length);
            return bitmap;
        }*/
        try{
            byte [] encodeByte= Base64.decode(imageString,Base64.DEFAULT);
            //byte[] encodeByte = imageString.getBytes("UTF-8");
            InputStream inputStream  = new ByteArrayInputStream(encodeByte);
            Bitmap bitmap  = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }
        catch (Exception e)
        {
            e.getMessage();
            return null;
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