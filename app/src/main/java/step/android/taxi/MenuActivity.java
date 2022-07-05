package step.android.taxi;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {
    private CircleImageView UserPhoto;
    ActivityResultLauncher<String> PickImageActivity = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    CallCropImageActivity(result);
                }
            }
    );

    private ActivityResultLauncher<Intent> Crop_photo = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    result.getData().getData();
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        UserPhoto = (CircleImageView) findViewById(R.id.UserPhoto);
        UserPhoto.setOnClickListener(this);

        TextView Name_textview = (TextView) findViewById(R.id.user_name_text_view);
        Name_textview.setText(UserInfo.getName()+" "+UserInfo.getSurname());

        Bitmap photo = UserInfo.getPhoto();
        UserPhoto.setImageBitmap(photo);
    }

    @Override
    public void onClick(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.addCategory(Intent.CATEGORY_OPENABLE);
        PickImageActivity.launch("image/*");

    }
    public void CallCropImageActivity(Uri image_path_uri){

        Intent intent = new Intent(this,CropperActivity.class);
        intent.putExtra("DATA", image_path_uri.toString());
        startActivityForResult(intent, 101);
    }


    @Override //getting cropped photo uri
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if(resultCode == -1 && requestCode == 101){
                String result = data.getStringExtra("RESULT");
                Uri resultUri = null;
                if(result != null){

                    resultUri = Uri.parse(result);
                    UserPhoto.setImageURI(resultUri);
                    UserPhoto.setTag(resultUri);

                    Uri finalResultUri = resultUri;
                    String token = UserInfo.getAuthToken();
                    Thread sendPhoto = new Thread(
                            ()->{
                                try {
                                    String networkResult;
                                    networkResult = Network.SendPhoto(
                                            getString(R.string.send_photo_url),
                                            new File(finalResultUri.getPath()),
                                            token
                                            );
                                    JSONObject jsonResult = new JSONObject(networkResult);
                                    String message;
                                    message = jsonResult.getString("message");
                                    Log.e("!!Server photo message :", message);
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                    );
                    sendPhoto.start();
                }

            }
    }

}