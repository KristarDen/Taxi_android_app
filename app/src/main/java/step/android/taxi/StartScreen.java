package step.android.taxi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;


public class StartScreen extends AppCompatActivity {
    private Animation rotate_load;
    public UserInfo userInfo;
    ImageView loadIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        //hide top bar
        this.getSupportActionBar().hide();

        //find load icon
        loadIcon = findViewById(R.id.load_icon);

        //create rotate animation
        rotate_load = AnimationUtils.loadAnimation(this, R.anim.load_anim);

        //make blur on background image
        Glide.with(this)
                .load(R.drawable.ic_background_img)
                .transform(new BlurTransformation(this))
                .into((ImageView) findViewById(R.id.screen));

        loadIcon.startAnimation(rotate_load);


        /*
        Check DB and auth token
        if token don't exist - go to LoginOrRegister screen
         */
        if(!CheckDB()){
            (new Handler()).postDelayed(()->{
                startActivity(
                        new Intent(
                                StartScreen.this,
                                LoginOrRegister.class ) ) ;
            }, 2000);
        } else {

            startActivity(
                    new Intent(
                            StartScreen.this,
                            MapsActivity.class ) ) ;

        }

        RequestPermissions();

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
            Allocation input = Allocation.createFromBitmap(
                    rs,
                    blurredBitmap,
                    Allocation.MipmapControl.MIPMAP_FULL,
                    Allocation.USAGE_SHARED);
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

    private boolean CheckDB(){
        //Open or create SQLite db with user saved data
        try {
            SQLiteDatabase db = getBaseContext().openOrCreateDatabase(
                    "Taxi.db",
                    MODE_PRIVATE,
                    null);
            //If table of user info don't exist - create it
            db.execSQL("CREATE TABLE IF NOT EXISTS "+ getString(R.string.Taxi_DB_Table_str));

            //Select authorisation token from db
            Cursor query = db.rawQuery("SELECT Token FROM User_info LIMIT 1;",null);
            query.moveToFirst();
            String token;
            token = query.getString(0);
            //check token
            if( token.length() != 0 ){
                UserInfo.setAuthToken(token);
                return true;
            } else {
                return false;
            }
        } catch (Exception ex){
            return false;
        }


    }

    private void RequestPermissions(){

        //Check internet access permission (android.permission.INTERNET)
        int internet_status = ContextCompat.checkSelfPermission(
                this, Manifest.permission.INTERNET);
        if(internet_status == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.INTERNET
                    },
                    1
            );
        }

        //Check network state permission
        int network_state_status = ContextCompat.checkSelfPermission(
                this,Manifest.permission.ACCESS_NETWORK_STATE);
        if(network_state_status == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_NETWORK_STATE
                    },
                    1
            );
        }

        //Check location access permission (ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION)
        int fine_location_status = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(fine_location_status  == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    1
            );
        }
        int coarse_location_status = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if(coarse_location_status == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    1
            );
        }


    }
}