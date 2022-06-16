package step.android.taxi;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.DataOutputStream;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Network {

    public static String POST(String path, String jsonData) {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        RequestBody formBody = new FormBody.Builder()
                .add("data", AES256.textToBase64(jsonData))
                .build();

        Request request = new Request.Builder()
                .url(path)
                .method("POST",
                        formBody )
                .build();

        try {

            Response response = client.newCall(request).execute();
            return response.body().string();

        } catch (IOException ex){
        }
        return "";
    }
}
