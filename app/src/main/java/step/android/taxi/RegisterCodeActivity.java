package step.android.taxi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicReference;

public class RegisterCodeActivity extends AppCompatActivity {
    AtomicReference<String> Responce = new AtomicReference<String>();

    private EditText form_1, form_2,form_3,form_4,form_5,form_6 ;
    private String cell1,cell2,cell3,cell4,cell5,cell6;

    private TextWatcher watcher_1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            cell1 = form_1.getText().toString();
            if(cell1.length()<1){
                form_1.setBackground( getDrawable(R.drawable.code_form_back_blank) );
            }else{
                form_1.setBackground( getDrawable(R.drawable.code_form_back) );
                form_2.requestFocus();
            }
            CheckCodeForm();
        }
    };
    private TextWatcher watcher_2 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            cell2 = form_2.getText().toString();
            if(cell2.length()<1){
                form_2.setBackground( getDrawable(R.drawable.code_form_back_blank) );
            }else {
                form_2.setBackground( getDrawable(R.drawable.code_form_back) );
                form_3.requestFocus();
            }
            CheckCodeForm();

        }
    };
    private TextWatcher watcher_3 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            cell3 = form_3.getText().toString();
            if(cell3.length()<1){
                form_3.setBackground( getDrawable(R.drawable.code_form_back_blank) );
            }else {
                form_3.setBackground( getDrawable(R.drawable.code_form_back) );
                form_4.requestFocus();
            }
            CheckCodeForm();

        }
    };
    private TextWatcher watcher_4 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            cell4 = form_4.getText().toString();
            if(cell4.length()<1){
                form_4.setBackground( getDrawable(R.drawable.code_form_back_blank) );
            }else {
                form_4.setBackground( getDrawable(R.drawable.code_form_back) );
                form_5.requestFocus();
            }
            CheckCodeForm();

        }
    };
    private TextWatcher watcher_5 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            cell5 = form_5.getText().toString();
            if(cell5.length()<1){
                form_5.setBackground( getDrawable(R.drawable.code_form_back_blank) );
            }else {
                form_5.setBackground( getDrawable(R.drawable.code_form_back) );
                form_6.requestFocus();
            }
            CheckCodeForm();

        }
    };
    private TextWatcher watcher_6 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            cell6 = form_6.getText().toString();
            if(cell6.length()<1){
                form_6.setBackground( getDrawable(R.drawable.code_form_back_blank) );
            }else {
                form_6.setBackground( getDrawable(R.drawable.code_form_back) );
                CheckCodeForm();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_code);

        //init forms
        form_1 = (EditText)findViewById(R.id.EmailCode_1_num);
        form_2 = (EditText)findViewById(R.id.EmailCode_2_num);
        form_3 = (EditText)findViewById(R.id.EmailCode_3_num);
        form_4 = (EditText)findViewById(R.id.EmailCode_4_num);
        form_5 = (EditText)findViewById(R.id.EmailCode_5_num);
        form_6 = (EditText)findViewById(R.id.EmailCode_6_num);

        //adding text watchers for code forms
        form_1.addTextChangedListener(watcher_1);
        form_2.addTextChangedListener(watcher_2);
        form_3.addTextChangedListener(watcher_3);
        form_4.addTextChangedListener(watcher_4);
        form_5.addTextChangedListener(watcher_5);
        form_6.addTextChangedListener(watcher_6);

        TextView email_label = (TextView) findViewById(R.id.user_mail_text);
        email_label.setText(UserInfo.getEmail());

    }

    private void CheckCodeForm(){
        try {
            if(     cell1.length()==1&&
                    cell2.length()==1&&
                    cell3.length()==1&&
                    cell4.length()==1&&
                    cell5.length()==1&&
                    cell6.length()==1
            ){
                //forming code
                String code = ""+cell1+cell2+cell3+cell4+cell5+cell6;

                //send code with token to server and write response
                Thread SendCode = new Thread(
                        ()->{
                            JSONObject data = new JSONObject();
                            Responce.set(Network.POST(
                                    getString(R.string.confirm_code_url),
                                    "{"
                                            +"\"code\": " + code + ","
                                            +"\"token\": \"" + UserInfo.getAuthToken()+"\""
                                            +"}"
                            ));
                        });
                try {
                    SendCode.start();
                    SendCode.join();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception ex){
            Log.e("CODE:",ex.getMessage());
        }
    }
}