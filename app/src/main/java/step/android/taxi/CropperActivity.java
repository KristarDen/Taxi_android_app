package step.android.taxi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

public class CropperActivity extends AppCompatActivity {

    String result;
    Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropper);
        readIntent();

        Uri to =  Uri.fromFile(new File(getFilesDir(),
                        UUID.randomUUID().toString() + ".jpg"));

        UCrop.Options options = new UCrop.Options();
        UCrop.of(fileUri, to)
                .withOptions(options)
                .withAspectRatio(1,1)
                .withMaxResultSize(200,200)
                .start(CropperActivity.this);

    }
    private void readIntent(){
        Intent intent = getIntent();
        if(intent.getExtras()!=null){
            result = intent.getStringExtra("DATA");
            fileUri=Uri.parse(result);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==UCrop.REQUEST_CROP){
            final Uri resultUri = UCrop.getOutput(data);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("RESULT", resultUri+"");
            setResult(-1,returnIntent);
            finish();
        } else if( resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }
}