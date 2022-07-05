package step.android.taxi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Network {

    public static String POST(String url, String jsonData) {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        RequestBody formBody = new FormBody.Builder()
                .add("data", AES256.textToBase64(jsonData))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .method("POST",
                        formBody )
                .build();

        try {

            Response response = client.newCall(request).execute();
            String resp = response.body().string();
            JSONObject response_data = new JSONObject(resp);
            String decryptedData = AES256.base64ToText( response_data.getString("data") );

            return decryptedData;

        } catch (IOException | JSONException ex){
            return  ex.getMessage();
        }
    }

    public static String SendPhoto(String url, File file, String token) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        String extension = file.getPath().substring(file.getPath().lastIndexOf(".")+1);
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse("image/"+extension), file))
                .addFormDataPart("token", token)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try {

            Response response = client.newCall(request).execute();
            String resp = response.body().string();
            JSONObject response_data = new JSONObject(resp);
            String decryptedData = AES256.base64ToText( response_data.getString("data") );

            return decryptedData;

        } catch (IOException | JSONException ex){
            return  ex.getMessage();
        }
    }

    public static JSONObject GetPhoto(String url, String token){

        //create okhttp client
        OkHttpClient client = new OkHttpClient();

        HttpUrl Url = HttpUrl
                .parse(url)
                .newBuilder()
                .addQueryParameter("token", AES256.textToBase64(token))
                .build();

        Request request = new Request.Builder()
                .url(Url)
                .method("GET",null)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String resp = response.body().string();
            JSONObject response_data = new JSONObject(resp);

            String inner_data = response_data.getString("data");
            JSONObject user_data = new JSONObject(inner_data);

            return user_data;
        } catch (Exception ex){
            Log.e("JSON err :", ex.getMessage());
        }
        return null;
    }



}
