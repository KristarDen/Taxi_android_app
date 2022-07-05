package step.android.taxi;

import android.graphics.Bitmap;

public class UserInfo  {

    public static final UserInfo INSTANCE = new UserInfo();

    private UserInfo(){}

    private static String AuthToken = "";
    private static String Phone = "";
    private static String Email = "";
    private static String Name = "";
    private static String Surname = "";
    private static Bitmap Photo ;

    public static String getAuthToken() {
        return AuthToken;
    }

    public static String getEmail() {
        return Email;
    }

    public static void setEmail(String email) {
        Email = email;
    }

    public static void setAuthToken(String authToken) {AuthToken = authToken;}

    public static Bitmap getPhoto() {
        return Photo;
    }

    public static void setPhoto(Bitmap photo) {
        Photo = photo;
    }

    public static String getPhone() {
        return Phone;
    }

    public static void setPhone(String phone) {
        Phone = phone;
    }

    public static String getName() {
        return Name;
    }

    public static void setName(String name) {
        Name = name;
    }

    public static String getSurname() {
        return Surname;
    }

    public static void setSurname(String surname) {
        Surname = surname;
    }


}