package step.android.taxi;

public class UserInfo  {

    public static final UserInfo INSTANCE = new UserInfo();

    private UserInfo(){}

    private static String AuthToken = "";
    private static String Email = "";
    public static String test="";

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
}