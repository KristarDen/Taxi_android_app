package step.android.taxi;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView UserPhoto;
    private final int Image_piked = 1;
    ActivityResultLauncher<String> mGetContent;

    private ActivityResultLauncher<Intent> Choose_photo =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {

                            if (result.getResultCode() == Activity.RESULT_OK) {
                                try {
                                    if (result.getData() != null){
                                        Uri imagePath = result.getData().getData();
                                        CallCropImageActivity(imagePath);
                                        /*
                                        //write image from path to stream
                                        InputStream imageStream =
                                                getContentResolver().openInputStream(imagePath);
                                        //decode stream to bitmap and save
                                        Bitmap imageBitmap = BitmapFactory.decodeStream(imageStream);
                                        //set selected bitmap-image in Image_View UserPhoto
                                        UserPhoto.setImageBitmap(imageBitmap);
                                        */
                                    }
                                }catch (Exception exception){
                                    Log.d("TAG",""+exception.getLocalizedMessage());
                                }
                            }
                        }
                    });

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
        UserPhoto = (ImageView) findViewById(R.id.UserPhoto);
        UserPhoto.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        Choose_photo.launch(photoPickerIntent);

        /*
        //Вызываем стандартную галерею для выбора изображения с помощью Intent.ACTION_PICK:
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        //Тип получаемых объектов - image:
        photoPickerIntent.setType("image/*");
        //Запускаем переход с ожиданием обратного результата в виде информации об изображении:
        startActivityForResult(Intent.createChooser(photoPickerIntent,
                "Select Picture"), Image_piked);

         */
    }
    public void CallCropImageActivity(Uri image_path_uri){

        Intent intent = new Intent(this,CropperActivity.class);
        intent.putExtra("DATA", image_path_uri.getPath());
        startActivityForResult(intent, 101);
        /*
        Intent photoCropIntent = new Intent(this,CropActivity.class);
        photoCropIntent.putExtra("path",image_path_uri.getPath());
        Crop_photo.launch(photoCropIntent);
         */
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == -1 && requestCode == 101){
            String result = data.getStringExtra("RESULT");
            Uri resultUri = null;
            if(result != null){
                resultUri = Uri.parse(result);
            }

            UserPhoto.setImageURI(resultUri);

        }
    }
    //Обрабатываем результат выбора в галерее:
    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case Image_piked:
                if(resultCode == RESULT_OK){
                    try {
                        //get path of image
                        Uri imagePath = imageReturnedIntent.getData();
                        Intent crop_intent = new Intent(this, CropActivity.class);
                        crop_intent.putExtra("image_uri", imagePath.getPath());
                        startActivityForResult(Intent.createChooser(crop_intent,
                                "Crop_picture"), Image_piked);

                        //write image from path to stream
                        InputStream imageStream = getContentResolver().openInputStream(imagePath);
                        //decode stream to bitmap and save
                        Bitmap imageBitmap = BitmapFactory.decodeStream(imageStream);
                        //set selected bitmap-image in Image_View UserPhoto
                        UserPhoto.setImageBitmap(imageBitmap);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        }} */
}