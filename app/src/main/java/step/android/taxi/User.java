package step.android.taxi;

public class User {

    public static final User INSTANCE = new User();

    private User(){}

    private static String AuthToken = "";
    public static String test="";

    public static String getAuthToken() {
        return AuthToken;
    }
}