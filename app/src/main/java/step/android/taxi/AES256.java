package step.android.taxi;
import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class AES256 {
    private static final String SECRET_KEY = "25CzkNUT246tk864Pbc3YNm32uPuNxkL";
    private static final String SALT = "EcVg26683KH3F7dNuDps677jg3bUVUuj";

    private static final SecretKeySpec API_KEY = new SecretKeySpec(
            SECRET_KEY.getBytes( StandardCharsets.UTF_8 ),
            "AES" ) ;
    private static final IvParameterSpec API_IV = new IvParameterSpec(
            "1234567890ABCDEF".getBytes()
    );

    @SuppressLint("NewApi")
    public static String encrypt(String strToEncrypt) {
        try {
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            return Base64.getEncoder()
                    .encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decrypt(String strToDecrypt) {
        try {
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }



    public static String textToBase64( String text ) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init( Cipher.ENCRYPT_MODE, API_KEY, API_IV ) ;
            return android.util.Base64.encodeToString(
                    cipher.doFinal(
                            text.getBytes( StandardCharsets.UTF_8 )
                    ),
                    android.util.Base64.DEFAULT
            ) ;
        } catch( Exception ex ) {
            Log.e( "AES256-textToBase64", ex.getMessage() ) ;
        }
        return null ;
    }

    public static String base64ToText( String b64 ) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init( Cipher.DECRYPT_MODE, API_KEY, API_IV ) ;
            return  new String(
                    cipher.doFinal(
                            android.util.Base64.decode( b64, android.util.Base64.DEFAULT )
                    ),
                    StandardCharsets.UTF_8
            ) ;
        } catch( Exception ex ) {
            Log.e( "AES256-base64ToText", ex.getMessage() ) ;
        }
        return null ;
    }

    public static String Decrypt(String value)
            throws GeneralSecurityException {
        try {
            IvParameterSpec Iv = new IvParameterSpec(SALT.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(
                    SECRET_KEY.getBytes("UTF-8"),
                    "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, Iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            //return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
