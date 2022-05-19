package step.android.taxi;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GMapApi {
    private static String API_KEY = "AIzaSyBI-kBlkecJTeDRiXkW23wVRn6qFE6JO3Y";
    private static String FIND_PLACE_URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&key=%s&language=%s";
    private static String DIRECTION_GET_URL = "https://maps.googleapis.com/maps/api/directions/json?origin=%s,%s&destination=%s,%s&key=%s";

    private static Gson json = new Gson();

    public static String FindPlaceByLatLan(LatLng latlng, String language){
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(
                            String.format( FIND_PLACE_URL,
                                    latlng.latitude,
                                    latlng.longitude,
                                    API_KEY, language )
                    )
                    .method("GET", null)
                    .build();
            try {

                Response response = client.newCall(request).execute();
                //Log.i("Place API responce : ",response.body().string());

                JSONObject json_resp = new JSONObject(response.body().string());
                JSONArray results_json =  json_resp.getJSONArray("results");
                JSONObject address_components_json =  results_json.getJSONObject(0);

                String address_Name = "" + address_components_json.getString("formatted_address");
                return address_Name;

            } catch (IOException | JSONException ex){
                return ex.getMessage();
            }
        //return "";

    }

    public static ArrayList<LatLng> GetDirection(LatLng from, LatLng to){

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(
                        String.format( DIRECTION_GET_URL,
                                from.latitude,
                                from.longitude,
                                to.latitude,
                                to.longitude,
                                API_KEY )
                )
                .method("GET", null)
                .build();
        ArrayList<LatLng> coords = null;
        try {

            Response response = client.newCall(request).execute();
            coords = new ArrayList<LatLng>();


            JSONObject json_resp = new JSONObject(response.body().string());
            JSONArray routes = json_resp.getJSONArray("routes");
            JSONObject routes_0 = routes.getJSONObject(0);
            JSONArray legs = routes_0.getJSONArray("legs");
            JSONObject legs_0 = legs.getJSONObject(0);
            JSONArray steps = legs_0.getJSONArray("steps");
            /*
            JSONArray steps = new JSONObject(response.body().string())
                    .getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONArray("legs")
                    .getJSONObject(0)
                    .getJSONArray("steps");

             */

            for(int i = 0; i < steps.length(); i++){
                coords.add(
                        new LatLng(
                                steps.getJSONObject(i)
                                    .getJSONObject("start_location")
                                    .getDouble("lat"),
                                steps.getJSONObject(i)
                                        .getJSONObject("start_location")
                                        .getDouble("lng")
                ));

            }
            return coords;

            /*

             JSONObject json_resp = new JSONObject(response.body().string());
            JSONArray routes = json_resp.getJSONArray("routes");
            JSONObject routes_0 = routes.getJSONObject(0);
            JSONArray legs = routes_0.getJSONArray("legs");
            JSONObject legs_0 = legs.getJSONObject(0);
            JSONArray steps = legs_0.getJSONArray("steps");
             */


        } catch (IOException | JSONException ex){

            ex.getMessage();
        }
        return coords;
    }

    public static ArrayList<LatLng>GetDirectionPolPoints(LatLng from, LatLng to) {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(
                        String.format( DIRECTION_GET_URL,
                                from.latitude,
                                from.longitude,
                                to.latitude,
                                to.longitude,
                                API_KEY )
                )
                .method("GET", null)
                .build();
        ArrayList<LatLng> coords = null;
        try {

            Response response = client.newCall(request).execute();
            coords = new ArrayList<LatLng>();


            JSONObject json_resp = new JSONObject(response.body().string());
            JSONArray routes = json_resp.getJSONArray("routes");
            JSONObject routes_0 = routes.getJSONObject(0);
            JSONArray legs = routes_0.getJSONArray("legs");
            JSONObject legs_0 = legs.getJSONObject(0);
            JSONArray steps = legs_0.getJSONArray("steps");
            /*
            JSONArray steps = new JSONObject(response.body().string())
                    .getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONArray("legs")
                    .getJSONObject(0)
                    .getJSONArray("steps");

             */

            for(int i = 0; i < steps.length(); i++){
                coords.addAll( PolyUtil.decode(
                        steps.getJSONObject(i)
                                        .getJSONObject("polyline")
                                        .getString("points")
                        ));

            }

            return coords;

            /*

             JSONObject json_resp = new JSONObject(response.body().string());
            JSONArray routes = json_resp.getJSONArray("routes");
            JSONObject routes_0 = routes.getJSONObject(0);
            JSONArray legs = routes_0.getJSONArray("legs");
            JSONObject legs_0 = legs.getJSONObject(0);
            JSONArray steps = legs_0.getJSONArray("steps");
             */


        } catch (IOException | JSONException ex){

            ex.getMessage();
        }
        return coords;
    }
}
