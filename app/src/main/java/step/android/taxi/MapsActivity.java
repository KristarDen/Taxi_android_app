package step.android.taxi;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
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

import de.hdodenhof.circleimageview.CircleImageView;
import step.android.taxi.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private Timer timer = new Timer();
    private final long DELAY = 1000;

    private AtomicReference<ArrayList<Address_item>> Suggested_From_Addreses
            = new AtomicReference<ArrayList<Address_item>>();
    private EditText From_edit_text;
    private LinearLayout From_Suggested_address_View;

    private AtomicReference<ArrayList<Address_item>> Suggested_Where_Addreses
            = new AtomicReference<ArrayList<Address_item>>();

    private AtomicReference<String> Distance = new AtomicReference<String>();
    private EditText Where_edit_text;
    private LinearLayout Where_Suggested_address_View;


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
    LayoutInflater inflater;

    //Demo data
        ArrayList<Driver> Drivers = new ArrayList<Driver>();
        int SelectedDriverId;
        Marker SelectedDriver;
        boolean userPosBlocked = false;
        boolean mapClickBlocked = false;

        int currCoordDot = 0;
        CountDownTimer driverComeTimer =
                new CountDownTimer(10000, 1000){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                for (int i = 0; i < Drivers_markers.size(); i++){
                   Drivers_markers.get(i).remove();
                }
                Drivers_markers.clear();
                SelectedDriver = makeMarker(UserMarker.getPosition(),MarkerType.DRIVER);

                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(MapsActivity.this)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(getString(R.string.app_name))
                                .setContentText(getText(R.string.driver_arrived_note_text));

                Notification notification = builder.build();

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(1, notification);

                AlertDialog.Builder DialogBuilder=new AlertDialog.Builder(MapsActivity.this);
                DialogBuilder.setTitle ( getString(R.string.app_name))
                        .setMessage ( getText(R.string.driver_arrived_note_text))
                        .setPositiveButton (getString(R.string.Start_trip_btn_text),
                                new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText (MapsActivity.this,"Trip Started", Toast.LENGTH_SHORT) .show ();
                        currCoordDot = DirectionDotsList.get().size();
                        currCoordDot--;
                        driverMoveTimer.start();
                    }
                });
                DialogBuilder.show();
            }
        };

        CountDownTimer driverMoveTimer =
                new CountDownTimer(100000, 1000){

                @Override
                public void onTick(long millisUntilFinished) {
                    if(currCoordDot > 0 ){
                        UserMarker.setPosition(DirectionDotsList.get().get(currCoordDot));
                        SelectedDriver.setPosition(DirectionDotsList.get().get(currCoordDot));
                        currCoordDot--;
                    } else {
                        onFinish();
                    }

                }

                @Override
                public void onFinish() {

                }
            };
    //Demo

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

    private ArrayList<Marker>Drivers_markers = new ArrayList<Marker>();

    LocationManager locationManager;

    private GoogleMap mMap;

    // Map on click listener
    final GoogleMap.OnMapClickListener onMapClickListener =
            new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng arg0) {
                    if(!mapClickBlocked){
                        drawWayOnMap(UserMarker.getPosition(), arg0 );
                    }
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
    private Button MakeOrderBtn;
    private Button BackToAddressesBtn;

    private LinearLayout FindAddressesView;
    private LinearLayout DriversView;

    private View.OnClickListener MenuBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent menuActivity = new Intent(mContext, MenuActivity.class);
            StartMenuForResult.launch(menuActivity);
        }
    };
    private TextView Distance_text;
    private AtomicReference <Integer> Distance_meters = new AtomicReference<Integer>();

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

        MakeOrderBtn = (Button) findViewById(R.id.makeOrder_button);
        MakeOrderBtn.setOnClickListener(this::MakeOrder);

        BackToAddressesBtn = (Button)findViewById(R.id.back_to_address_bnt);
        BackToAddressesBtn.setOnClickListener(this::BackToAddressClick);

        FindAddressesView = (LinearLayout) findViewById(R.id.findAddressView);
        DriversView = (LinearLayout) findViewById(R.id.DriversView);

        Distance_text = (TextView) findViewById(R.id.distance_text);

        //From Text listener
        From_edit_text = (EditText) findViewById(R.id.address_from);
        //Listener of text input ended
        From_edit_text.addTextChangedListener(From_edittext_watcher);
        From_Suggested_address_View = (LinearLayout) findViewById(R.id.suggestion_from);
        //Where Text listener
        Where_edit_text = (EditText) findViewById(R.id.address_to);
        //Listener of text input ended
        Where_edit_text.addTextChangedListener(Where_edittext_watcher);
        Where_Suggested_address_View = (LinearLayout) findViewById(R.id.suggestion_where);



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

                        //demo part
                        if (!userPosBlocked){
                            if (UserMarker != null) {
                                UserMarker.setPosition(UserPosition);
                            } else {
                                UserMarker = mMap.addMarker(new MarkerOptions()
                                        .position(UserPosition));
                            }
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
                .title(getString(R.string.user_location_title))
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLng(UserMarker.getPosition()));
        mMap.setMinZoomPreference(12);

        mMap.setInfoWindowAdapter(new CustomGMapInfoWindow(MapsActivity.this));



        mMap.setOnMapClickListener(onMapClickListener);

        //demo part
        fillDrivers();
        drawAllDrivers();

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
        AtomicReference<String> titleA = new AtomicReference<>("");
        AtomicReference<String> titleB = new AtomicReference<>("");
        try {
            Thread getPlaceInfo = new Thread(()->{
                //get info about point A coordination
                titleA.set(GMapApi.FindPlaceByLatLan(A, Locale.getDefault().getLanguage()));
                titleB.set(GMapApi.FindPlaceByLatLan(B, Locale.getDefault().getLanguage()));
                /*
                    Get direction from A to B in dots array.
                    From this array of dots will be created
                    polyline object, for drawing line on map
                 */
                DirectionDotsList.set( GMapApi.GetDirectionPolPoints(B, A));

                //get info about distance between point A and B
                int distance = GMapApi.DistanceBetween(A,B);
                Distance_meters.set(distance);

                String n = Integer.toString(distance);
                char[] charArray = n.toCharArray();

                if(distance < 1000){
                    Distance.set(getString(R.string.distance_between)+" "+distance+"m");
                } else {
                    if (distance % 1000 == 0){
                        Distance.set( getString(R.string.distance_between) +" "+ distance / 1000);
                    } else {

                        Distance.set( getString(R.string.distance_between) +" "
                                + charArray[0]+ ","
                                + charArray[1] + "km");
                    }
                }
            });

            //Start thread and wait for end of thread work
            getPlaceInfo.start();
            getPlaceInfo.join();

            //Creation and drawing A and B markers on map by A,B points coords
            if (Marker_PointB != null) {
                Marker_PointB.setPosition(B);

            } else {
                Marker_PointB = makeMarker(B,MarkerType.TO);
            }
            Marker_PointB.setTitle(titleB.get());

            if (Marker_PointA != null) {
                Marker_PointA.setPosition(A);
            } else {
                Marker_PointA = makeMarker(A,MarkerType.FROM);
            }
            Marker_PointA.setTitle(titleA.get());

            Distance_text.setText(Distance.get());
            Distance_text.setVisibility(View.VISIBLE);
            //drawing a polyline of direction
            drawDirectionAsPolyline(DirectionDotsList.get());


            Toast toast = Toast.makeText(getApplicationContext(),
                    "" + titleA, Toast.LENGTH_SHORT);
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
    private Marker makeMarker(LatLng pos,MarkerType type){
        Marker marker=null;
        switch (type){
            case FROM:
                BitmapDescriptor icon_from = BitmapDescriptorFactory.fromBitmap(
                       drawableToBitmap(getDrawable(R.drawable.ic_from_icon))
                );
                marker = mMap.addMarker(new MarkerOptions()
                        .icon(icon_from)
                        .position(pos)
                );
                break;
            case TO :
                BitmapDescriptor icon_to = BitmapDescriptorFactory.fromBitmap(
                        drawableToBitmap(getDrawable(R.drawable.ic_to_icon))
                );
                marker = mMap.addMarker(new MarkerOptions()
                        .icon(icon_to)
                        .position(pos)
                );
                break;
            case DRIVER:
                BitmapDescriptor icon_driver = BitmapDescriptorFactory.fromBitmap(
                        drawableToBitmap(getDrawable(R.drawable.car_icon))
                );
                marker = mMap.addMarker(new MarkerOptions()
                        .icon(icon_driver)
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
           From_Suggested_address_View.removeAllViews();

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
                From_Suggested_address_View.addView(newBtn);
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
            Where_Suggested_address_View.removeAllViews();
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
                Where_Suggested_address_View.addView(newBtn);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    //Demo functions
    private void moveDriverTo (LatLng coords){
        SelectedDriver.setPosition(coords);
    }
    private void setSelectedDriver(int id){
        SelectedDriver = makeMarker(Drivers.get(id).Coords,MarkerType.DRIVER);
    }
    private void drawAllDrivers(){
        for (int i = 0; i < Drivers.size(); i++) {
            Drivers_markers.add( makeMarker(Drivers.get(i).Coords,MarkerType.DRIVER) );
        }
    }
    private void fillDrivers(){
        Drivers.add(
                new Driver(
                        "Макс","Білополєв","+380502333719",4.0f, "BH2304AK",
                        "Reno Logan","standard",new LatLng(46.486555,30.724158),0.011f
                )
        );
        Drivers.add(
                new Driver(
                        "Георг","Акопян","+380504132829",2.0f, "BH5511AK",
                        "Lada Kalina","standard",new LatLng(46.493735,30.707283),0.01f
                )
        );
        Drivers.add(
                new Driver(
                        "Василь","Боровченко","+380505513073",4.9f, "BH3345AK",
                        "Toyota Camry","comfort",new LatLng(46.491770,30.729518),0.015f
                )
        );
    }
    //Demo

    private void MakeOrder(View view){
        if(PointA != null && PointB != null){
            FindAddressesView.setVisibility(View.GONE);
            DriversView.setVisibility(View.VISIBLE);
            for (int i = 0; i < Drivers.size(); i++) {
                DriversView.addView( MakeDriverCard(Drivers.get(i), i, true) );
            }
        }
    }
    private void BackToAddressClick(View view){
        FindAddressesView.setVisibility(View.VISIBLE);
        DriversView.setVisibility(View.GONE);
        DriversView.removeAllViews();
    }
    LinearLayout MakeDriverCard(Driver driver, int index, boolean withButton){
        LinearLayout allContainer = new LinearLayout(mContext);
        allContainer.setTag(index);
        allContainer.setOrientation(LinearLayout.VERTICAL);
        allContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(100, 10, 100, 10);
        allContainer.setLayoutParams(params);
        allContainer.setPadding(40,10,40,10);
        allContainer.setMinimumWidth(150);
        allContainer.setBackground(getDrawable(R.drawable.distance_background));

        CircleImageView DriverPhoto = new CircleImageView(mContext);
        DriverPhoto.setImageDrawable(getDrawable(R.drawable.ic_user_default));
        DriverPhoto.setMaxWidth(110);
        DriverPhoto.setMaxHeight(110);

        TextView Name = new TextView(mContext);
        Name.setText(""+driver.name+" "+driver.surname);
        Name.setTextColor(getColor(R.color.white));
        Name.setTextSize(20f);

        RatingBar Rate = new RatingBar(mContext);
        Rate.setNumStars(5);
        Rate.setRating(driver.rate);
        FrameLayout.LayoutParams paramRate = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0,5,0,0);
        Rate.setLayoutParams(paramRate);

        TextView CarModel = new TextView(mContext);
        CarModel.setText(driver.model);
        CarModel.setTextColor(getColor(R.color.white));
        CarModel.setTextSize(14f);

        TextView Class = new TextView(mContext);
        Class.setText(driver.Class);
        Class.setTextColor(getColor(R.color.white));
        Class.setTextSize(14f);

        TextView CoastText = new TextView(mContext);
        CoastText.setText( ""+ Math.round( (driver.price * ( Distance_meters.get() ) )  ) + "\u20B4" );
        CoastText.setTextColor(getColor(R.color.white));
        CoastText.setTextSize(20f);

        allContainer.addView(DriverPhoto);
        allContainer.addView(Name);
        allContainer.addView(Rate);
        allContainer.addView(CarModel);
        allContainer.addView(Class);
        allContainer.addView(CoastText);

        if(withButton){
            Button order = new Button(mContext);
            order.setText(getText(R.string.make_order_button_text));
            order.setBackground(getDrawable(R.drawable.button_confirm));
            order.setPadding(10,10,10,10);
            order.setOnClickListener(this::OrderDriver);
            allContainer.addView(order);
        }
        return allContainer;
    }

    private void OrderDriver(View view){
        LinearLayout ParentLayout = (LinearLayout) view.getParent();
        int driver_index = (int) ParentLayout.getTag();
        SelectedDriverId = driver_index;
        DriversView.removeAllViews();
        LinearLayout current_driver =
                MakeDriverCard(Drivers.get(driver_index),driver_index, false);
        DriversView.addView(current_driver);

        ShowInformToast(getString(R.string.driver_on_the_way_toast_text));
        mapClickBlocked = true;
        driverComeTimer.start();
    }

    private void ShowInformToast(String text){
        try{
            inflater = getLayoutInflater();
            View layout = inflater.inflate(
                    R.layout.map_info_toast,
                    (ViewGroup)findViewById(R.id.mapInfo_toast)
            );
            TextView tv = (TextView) layout.findViewById(R.id.mapInfo_toast_textView);
            tv.setText(text);
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        }catch (Exception ex){
            Log.e("TOAST_ERROR : ", ex.getMessage());
        }

    }

}
enum MarkerType{
    FROM ,
    TO,
    DRIVER
}