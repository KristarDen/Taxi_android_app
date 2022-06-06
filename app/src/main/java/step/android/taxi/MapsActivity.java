package step.android.taxi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import step.android.taxi.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private AtomicReference<ArrayList<Address_item>> Suggested_From_Addreses
            = new AtomicReference<ArrayList<Address_item>>();
    private EditText From_edit_text;
    private LinearLayout From_Sugested_address_View;

    private AtomicReference<ArrayList<Address_item>> Suggested_Where_Addreses
            = new AtomicReference<ArrayList<Address_item>>();
    private EditText Where_edit_text;
    private LinearLayout Where_Sugested_address_View;

    //From edit text onChange()
    private TextWatcher From_edittext_watcher
            = new TextWatcher()
    {

        public void afterTextChanged(Editable s) {

            Thread getPlaceInfo = new Thread(()->{
                Suggested_From_Addreses.set( GMapApi.FindPlaceByText( s.toString(),
                        Locale.getDefault().getLanguage() ) );
            });
            getPlaceInfo.start();
            try {
                getPlaceInfo.join();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    };

    private LatLng PointA;
    private LatLng PointB;


    LocationManager locationManager;

    private GoogleMap mMap;

    // Map on click listener
    final GoogleMap.OnMapClickListener onMapClickListener =
            new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng arg0) {

            drawWayOnMap(arg0,UserMarker.getPosition());
        }
    };

    private ActivityMapsBinding binding;

    //Marker on user current location
    private Marker UserMarker;

    //Marker on destination of user route
    private Marker DestinationMarker;

    private LatLng UserPosition;
    private Context mContext;
    private Polyline Direction;

    // Direction dots coordination list for drawing route on the map
    private AtomicReference<ArrayList<LatLng>> DirectionDotsList = new AtomicReference<ArrayList<LatLng>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mContext = this;
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //From Text listener
        From_edit_text = (EditText) findViewById( R.id.address_from );
        From_Sugested_address_View = (LinearLayout) findViewById(R.id.suggestion_from);
        From_edit_text.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                /* When focus is lost check that the text field
                 * has valid values.
                 */
                if (!hasFocus) {
                    Fill_From_suggestion();
                }
            }});

        //Where Text listener
        Where_edit_text = (EditText) findViewById( R.id.address_to );
        Where_Sugested_address_View = (LinearLayout) findViewById(R.id.suggestion_where);
        Where_edit_text.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        /* When focus is lost check that the text field
                         * has valid values.
                         */
                        if (!hasFocus) {
                            Fill_Where_suggestion();
                        }
                    }});


        //add onTexChanged listener
        //From_edit_text.addTextChangedListener(From_edittext_watcher);
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        isLocationEnabled();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

        }


        //User GPS location listener
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                10,
                10,
                new LocationListener() {
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
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.gmap_style));
        //mMap.setMyLocationEnabled(true);
        UserMarker = mMap.addMarker(new MarkerOptions()
                .position(
                        new LatLng(
                        locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                                .getLatitude(),
                        locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                                .getLongitude()
                        )
                )
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLng(UserMarker.getPosition()));
        mMap.setMinZoomPreference(12);


        mMap.setOnMapClickListener(onMapClickListener);

    }


    //Draw direction on map
    private void drawDirectionAsPolyline(ArrayList<LatLng> Dots){
        /*
            Direction making once, LatLan inside Direction object redefined
            on every new call of drawDirection and draw new route on map,
            and clear previous route because route must be only one (for this
            taxi project)
         */
        if(Direction != null){
            Direction.setPoints(Dots);
        }else {
            Direction = mMap.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .addAll(Dots)
                    .geodesic(true)
                    .color(Color.MAGENTA)
                    .width(15f)
            );
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
            /*
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
             */
        }
    }

    private void drawWayOnMap(LatLng A, LatLng B){
        AtomicReference<String> title = new AtomicReference<>("");

        try {
            Thread getPlaceInfo = new Thread(()->{
                //get info about clicked coordination
                title.set(GMapApi.FindPlaceByLatLan(A, Locale.getDefault().getLanguage()));

                //get direction to clicked coord
                DirectionDotsList.set( GMapApi.GetDirectionPolPoints(B, A));
            });

            getPlaceInfo.start();
            getPlaceInfo.join();

            if (DestinationMarker != null) {
                DestinationMarker.setPosition(A);
            } else {
                DestinationMarker = mMap.addMarker(new MarkerOptions()
                        .position(A));
            }

            //drawing a polyline of direction
            drawDirectionAsPolyline(DirectionDotsList.get());

            Toast toast = Toast.makeText(getApplicationContext(),
                    "" + title, Toast.LENGTH_SHORT);
            toast.show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    @SuppressLint("ResourceAsColor")
    //Фабричный метод для создания с адресом кнопки
    private Button makeButton(Address_item address){
        Button Btn = new Button(this);
        Btn.setTextColor(R.color.white);
        Btn.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        Btn.setTextSize(14);
        Btn.setText(address.get_name()+ "\n" + getString(R.string.Rating) + ": "
                + address.get_rating());
        Btn.setBackground( getDrawable(R.drawable.suggestion_address) );

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(15,15,15,15);
        Btn.setPadding(15,15,15,15);

        Btn.setLayoutParams(params);
        return Btn;
    }

    //Метод заполнения предложеня адресов для адреса отправления
    private void Fill_From_suggestion(){
        if( Suggested_From_Addreses.get() != null){
            Suggested_From_Addreses.get().clear();
        }
        try {
            Thread getPlaceInfo = new Thread(()->{
                Suggested_From_Addreses.set(
                        GMapApi.FindPlaceByText(
                                From_edit_text.getText().toString(),
                                Locale.getDefault().getLanguage()
                        )
                );
            });
            getPlaceInfo.start();
            getPlaceInfo.join();

            for (Address_item adr: Suggested_From_Addreses.get()
            ) {
                Button newBtn = makeButton(adr);

                // Добавление обработчика клика
                newBtn.setOnClickListener(new View.OnClickListener() {

                    /* Хранение переменной класса адреса с информацией о адресе привязаном
                     к этой кнопке
                     */
                    private Address_item addressItem = adr;

                    public void onClick(View v) {
                        /*переменная точка А получает координаты из класса адреса хранящегося в
                        addressItem
                         */
                        PointA = addressItem.get_coord();

                        /*
                        Проверка. Если переменная с точкой В уже существует
                        то вызывается метод построения и отрисовки маршрута
                        куда передаются переменные Точка А и Точка В
                         */
                        if(PointB != null){
                            drawWayOnMap(PointA,PointB);
                        }
                    }
                });
                From_Sugested_address_View.addView(makeButton(adr));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //Метод заполнения предложеня адресов для конечного адреса
    private void Fill_Where_suggestion(){

        if(Suggested_Where_Addreses.get() != null){
            Suggested_Where_Addreses.get().clear();
        }

        try {
            Thread getPlaceInfo = new Thread(()->{
                Suggested_Where_Addreses.set(
                        GMapApi.FindPlaceByText(
                                Where_edit_text.getText().toString(),
                                Locale.getDefault().getLanguage()
                        )
                );
            });
            getPlaceInfo.start();
            getPlaceInfo.join();

            for (Address_item adr: Suggested_Where_Addreses.get()
            ) {
                Button newBtn = makeButton(adr);
                // Добавление обработчика клика
                newBtn.setOnClickListener(new View.OnClickListener() {

                    /* Хранение переменной класса адреса с информацией о адресе привязаном
                     к этой кнопке
                     */
                    private Address_item addressItem = adr;

                    public void onClick(View v) {

                        /*переменная точка В получает координаты из класса адреса хранящегося в
                        addressItem
                         */
                        PointB = addressItem.get_coord();

                        /*
                        Проверка. Если переменная с точкой А уже существует
                        то вызывается метод построения и отрисовки маршрута
                        куда передаются переменные Точка А и Точка В
                         */
                        if(PointA != null){
                            drawWayOnMap(PointA,PointB);
                        }
                    }
                });
                Where_Sugested_address_View.addView(makeButton(adr));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}