package step.android.taxi;

import com.google.android.gms.maps.model.LatLng;

public class Driver {
    public String name;
    public String surname;
    public String phone;
    public float rate;
    public String plate_number;
    public String model;
    public String Class;
    public LatLng Coords;
    public float price;

    public Driver(String name, String surname, String phone, float rate, String plate_number, String model, String aClass, LatLng coords, float price) {
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.rate = rate;
        this.plate_number = plate_number;
        this.model = model;
        Class = aClass;
        Coords = coords;
        this.price = price;
    }
}
