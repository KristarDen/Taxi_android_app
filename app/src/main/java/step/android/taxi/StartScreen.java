package step.android.taxi;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import step.android.taxi.User;

import java.security.MessageDigest;


public class StartScreen extends AppCompatActivity {
    private Animation rotate_load;
    public User user;
    ImageView loadIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        this.getSupportActionBar().hide();





        //find load icon
        loadIcon = findViewById(R.id.load_icon);

        //create rotate animation
        rotate_load = AnimationUtils.loadAnimation(this, R.anim.load_anim);

        Glide.with(this)
                .load(R.drawable.ic_background_img)
                .transform(new BlurTransformation(this))
                .into((ImageView) findViewById(R.id.screen));

        //rotate.setRepeatCount(Animation.INFINITE);
        //rotate.setRepeatMode(Animation.RESTART);

        // rotate animation start
        loadIcon.startAnimation(rotate_load);

        if(user.getAuthToken() == ""){
            (new Handler()).postDelayed(()->{
                startActivity(
                        new Intent(
                                StartScreen.this,
                               LoginOrRegister.class ) ) ;
            }, 2000);
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