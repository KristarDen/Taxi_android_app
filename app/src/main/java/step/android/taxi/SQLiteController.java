package step.android.taxi;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SQLiteController {
    public static String getToken(Context context){
        SQLiteDatabase db = context.openOrCreateDatabase(
                "Taxi.db",
                MODE_PRIVATE,
                null);
        Cursor query = db.rawQuery("SELECT Token FROM User_info LIMIT 1;",null);
        query.moveToFirst();
        String token;
        token = query.getString(0);
        return token;
    }
    public static void setToken(Context context, String Token){
        SQLiteDatabase db = context.openOrCreateDatabase(
                "Taxi.db",
                MODE_PRIVATE,
                null);
            db.rawQuery("insert or replace into User_info(Token) " +
                            "VALUES (" + Token + ")" +
                            " (SELECT MIN(_id) FROM  User_info)"
                    ,null);

    }
}
