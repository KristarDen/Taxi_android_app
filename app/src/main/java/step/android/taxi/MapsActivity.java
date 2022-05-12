package step.android.taxi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.widget.Toast;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import step.android.taxi.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback {
    LocationManager locationManager;

    private GoogleMap mMap;
    GoogleMap.OnMapClickListener onMapClickListener = new GoogleMap.OnMapClickListener()
    {
        @Override
        public void onMapClick(LatLng arg0)
        {
           new Thread(new Runnable() {
                       @Override
                       public void run() {

                           String title = GMapApi.FindPlaceByLatLan(arg0, Locale.getDefault().getLanguage());
                           mMap.addMarker(new MarkerOptions().position(arg0)
                                   .title(title));
                           Toast toast = Toast.makeText(getApplicationContext(),
                                   "" + title, Toast.LENGTH_SHORT);
                           toast.show();
                       }
                           }).start();

        }
    };
    private ActivityMapsBinding binding;
    //private MarkerOptions UserMarker;
    private Marker UserMarker;
    private LatLng UserPosition;
    private Context mContext;

    //LocationListener locationListener = new UserLocation();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //locationListener = new UserLocation();

        //Настройка отслеживания местоположения пользователя
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

        }
        isLocationEnabled();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                UserPosition = new LatLng(location.getLatitude(), location.getLongitude());

                if (UserMarker != null) {
                    UserMarker.setPosition(UserPosition);
                } else {
                    UserMarker = mMap.addMarker(new MarkerOptions()
                            .position(UserPosition));
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLng(UserMarker.getPosition()));
                mMap.setMinZoomPreference(12);

                Toast toast = Toast.makeText(mContext, "" + location.getLatitude() + " : " + location.getLongitude(), Toast.LENGTH_LONG);
                toast.show();
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        });




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        //mMap.setMyLocationEnabled(true);
        UserMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(
                        locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER).getLatitude(),
                        locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER).getLongitude() )
                )
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLng(UserMarker.getPosition()));
        mMap.setMinZoomPreference(12);


        mMap.setOnMapClickListener(onMapClickListener);

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void drawRoute(){

    }

    private void findPlace(LatLng coord){

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=%2B16502530000&inputtype=phonenumber&key=" + "")
                .method("GET", null)
                .build();
        try {
            Response response = client.newCall(request).execute();

        } catch (Exception ex){

        }

    }



    protected void onResume(){
        super.onResume();
        isLocationEnabled();
    }

    private void isLocationEnabled() {

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(mContext);
            alertDialog.setTitle("Enable Location");
            alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.");
            alertDialog.setPositiveButton("Location Settings", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            AlertDialog alert=alertDialog.create();
            alert.show();
        }
        else{
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(mContext);
            alertDialog.setTitle("Confirm Location");
            alertDialog.setMessage("Your Location is enabled, please enjoy");
            alertDialog.setNegativeButton("Back to interface",new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            AlertDialog alert=alertDialog.create();
            alert.show();
        }
    }


}