package step.android.taxi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

public class LoginOrRegister extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_register);

        findViewById( R.id.chooseRegister ).setOnClickListener( this::Register_click ) ;
    }
    void Register_click (View v){
            startActivity(
                    new Intent(
                            this,
                            RegisterActivity.class ) ) ;
    }
}