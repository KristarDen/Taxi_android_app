package step.android.taxi;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

import step.android.taxi.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private Timer timer = new Timer();
    private final long DELAY = 1000;

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
            = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(final CharSequence s, int start, int before,
                                  int count) {
            if (timer != null)
                timer.cancel();
        }

        @Override
        public void afterTextChanged(final Editable s) {
            //avoid triggering event when text is too short
            if (s.length() >= 3) {

                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                Fill_From_suggestion();
                                            }
                                        }
                                );
                            }

                        },
                        DELAY);
            }
        }
    };
    //From edit text onChange()
    private TextWatcher Where_edittext_watcher
            = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(final CharSequence s, int start, int before,
                                  int count) {
            if (timer != null)
                timer.cancel();
        }

        @Override
        public void afterTextChanged(final Editable s) {
            //avoid triggering event when text is too short
            if (s.length() >= 3) {

                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                Fill_Where_suggestion();
                                            }
                                        }
                                );
                            }

                        },
                        DELAY);
            }
        }
    };


    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        //doSomeOperations();

                    }
                }
            }
    );

    ActivityResultLauncher<Intent> StartMenuForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // обработка result
                }
            }
    );

    /*
        Point A and Point B its a  coordination of
        start and end point of
        way on map that created by addresses that
        user chose by address form
     */
    private LatLng PointA;
    private LatLng PointB;

    /*
     Markers that be displayed on map
     with PointA and PointB coords
     */
    private Marker Marker_PointA;
    private Marker Marker_PointB;


    LocationManager locationManager;

    private GoogleMap mMap;

    // Map on click listener
    final GoogleMap.OnMapClickListener onMapClickListener =
            new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng arg0) {

                    drawWayOnMap(arg0, UserMarker.getPosition());
                }
            };

    private ActivityMapsBinding binding;

    //Marker on user current location
    private Marker UserMarker;

    /*
        Marker on destination of user route
        that chosen by tap on map
     */

    private Button MenuBtn;
    private View.OnClickListener MenuBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent menuActivity = new Intent(mContext, MenuActivity.class);
            StartMenuForResult.launch(menuActivity);
        }
    };

    private LatLng UserPosition;
    private Context mContext;

    /*
        Polyline object that will be
        displayed on map. It`s a route
        of user way
     */
    private Polyline Direction;

    // Direction dots coordination list for drawing route on the map
    private AtomicReference<ArrayList<LatLng>> DirectionDotsList = new AtomicReference<ArrayList<LatLng>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mContext = this;

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Get menu button and add onclick listener
        MenuBtn = (Button) findViewById(R.id.menu_btn);
        MenuBtn.setOnClickListener(MenuBtnListener);

        //From Text listener
        From_edit_text = (EditText) findViewById(R.id.address_from);
        //Listener of text input ended
        From_edit_text.addTextChangedListener(From_edittext_watcher);
        From_Sugested_address_View = (LinearLayout) findViewById(R.id.suggestion_from);
        //Where Text listener
        Where_edit_text = (EditText) findViewById(R.id.address_to);
        //Listener of text input ended
        Where_edit_text.addTextChangedListener(Where_edittext_watcher);
        Where_Sugested_address_View = (LinearLayout) findViewById(R.id.suggestion_where);



        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);


        //User GPS location listener
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
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

                        Toast toast = Toast.makeText(mContext, "" + location.getLatitude() +
                                " : " + location.getLongitude(), Toast.LENGTH_LONG);
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
                && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            switch (requestCode) {
                case 1:
                    break;
            }
        }
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
        //isLocationEnabled();
    }

    private void isLocationEnabled() {
        Intent intent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
        //startActivity(intent);
        startActivityForResult(intent,1);
        /*
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

         */
    }

    private void drawWayOnMap(LatLng A, LatLng B){
        AtomicReference<String> title = new AtomicReference<>("");

        try {
            Thread getPlaceInfo = new Thread(()->{
                //get info about point A coordination
                title.set(GMapApi.FindPlaceByLatLan(A, Locale.getDefault().getLanguage()));

                /*
                    Get direction from A to B in dots array.
                    From this array of dots will be created
                    polyline object, for drawing line on map
                 */
                DirectionDotsList.set( GMapApi.GetDirectionPolPoints(B, A));
            });

            //Start thread and wait for end of thread work
            getPlaceInfo.start();
            getPlaceInfo.join();

            //Creation and drawing A and B markers on map by A,B points coords
            if (Marker_PointB != null) {
                Marker_PointB.setPosition(B);
            } else {
                Marker_PointB = makeMarker(B,2);
            }
            if (Marker_PointA != null) {
                Marker_PointA.setPosition(A);
            } else {
                Marker_PointA = makeMarker(A,1);
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

    /*
        type defines a type of markers
        1 - marker from (Point A)
        2 - marker to (Point B)
     */
    private Marker makeMarker(LatLng pos, int type){
        Marker marker=null;
        switch (type){
            case 1 :
                BitmapDescriptor icon_from = BitmapDescriptorFactory.fromBitmap(
                       drawableToBitmap(getDrawable(R.drawable.ic_from_icon))
                );
                marker = mMap.addMarker(new MarkerOptions()
                        .icon(icon_from)
                        .position(pos)
                );
                break;
            case 2 :
                BitmapDescriptor icon_to = BitmapDescriptorFactory.fromBitmap(
                        drawableToBitmap(getDrawable(R.drawable.ic_to_icon))
                );
                marker = mMap.addMarker(new MarkerOptions()
                        .icon(icon_to)
                        .position(pos)
                );
                break;
        }
        return marker;
    }

    //converter for drawable
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    //Фабричный метод для создания с адресом кнопки
    private Button makeButton(Address_item address){
        Button Btn = new Button(this);
        Btn.setTextColor(Color.WHITE);
        Btn.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        Btn.setTextSize(12);
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

        //clear array and view with suggested elements
        if( Suggested_From_Addreses.get() != null){

            Suggested_From_Addreses.get().clear();

            //clear view
           From_Sugested_address_View.removeAllViews();

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
                From_Sugested_address_View.addView(newBtn);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //Метод заполнения предложеня адресов для конечного адреса
    private void Fill_Where_suggestion(){

        //clear array and view with suggested elements
        if(Suggested_Where_Addreses.get() != null){
            //clear array
            Suggested_Where_Addreses.get().clear();
            //clear view
            Where_Sugested_address_View.removeAllViews();
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
                Where_Sugested_address_View.addView(newBtn);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}