package step.android.taxi;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

public class Network {

    public static String Post(String path, String jsonData) throws IOException {

        String mess = AES256.textToBase64(jsonData);
        byte[] out =  mess.getBytes();
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
            httpURLConnection.connect();
            try {
                os = httpURLConnection.getOutputStream();
                os.write(out);

            } catch (Exception e) {

            }
            if (HttpURLConnection.HTTP_OK == httpURLConnection.getResponseCode()) {
                isR = new InputStreamReader((httpURLConnection.getInputStream()));
                bfR = new BufferedReader(isR);
                String line;
                while ((line = bfR.readLine()) != null) {
                    sb.append(line);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (isR != null) {
                isR.close();
            }

        } finally {
            isR.close();
            bfR.close();
            os.close();
        }


        return sb.toString();

    }
}
