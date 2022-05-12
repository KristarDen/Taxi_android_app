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

    public static String Post(String path, String jsonData) throws IOException {

        String mess = String.format("{ \"data\" : \"%s\"}",AES256.textToBase64(jsonData)) ;
        mess = mess.replace("\n", "\\n");
        //byte[] out =  mess.getBytes();
        URL url;
        HttpURLConnection httpURLConnection = null;
        OutputStream os = null;

        InputStreamReader isR = null;
        BufferedReader bfR = null;
        StringBuilder sb = new StringBuilder();


        try {
            url = new URL(path);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            httpURLConnection.addRequestProperty("Content-Type", "application/json");
            httpURLConnection.setConnectTimeout(200);
            httpURLConnection.setReadTimeout(200);

            //os = httpURLConnection.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(httpURLConnection.getOutputStream());
            osw.write(mess);
            osw.close();

            //httpURLConnection.connect();

            Log.d("HttpUrlConnection",httpURLConnection.getContent().toString());


            if (HttpURLConnection.HTTP_OK == httpURLConnection.getResponseCode()) {
                isR = new InputStreamReader((httpURLConnection.getInputStream()));
                bfR = new BufferedReader(isR);
                String line;
                while ((line = bfR.readLine()) != null) {
                    sb.append(line);
                }
                //return line;
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (isR != null) {
                isR.close();
            }

        } finally {
            if(isR != null)isR.close();
            if(bfR != null)bfR.close();
            os.close();
        }


        return sb.toString();

    }

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
