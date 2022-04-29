package step.android.taxi;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.IOException;
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

        Reader input = null;
        HttpURLConnection conn = null;

        try {
            URL url=new URL(path);
            String data = AES256.encrypt(jsonData.toString());
            byte[] postDataBytes = data.getBytes(StandardCharsets.UTF_8);

            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);

            input = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            StringBuilder sb = new StringBuilder();

            for (int c; (c = input.read()) >= 0;){
                sb.append((char)c);
            }

            String response = sb.toString();

            return(response);
        }
        finally {
            if (input != null) {
                input.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
