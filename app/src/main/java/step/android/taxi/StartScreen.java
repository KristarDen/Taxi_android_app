package step.android.taxi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.os.Bundle;
import android.widget.ImageView;

import step.android.taxi.User;


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
}