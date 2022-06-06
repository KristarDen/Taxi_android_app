package step.android.taxi;

import com.google.android.gms.maps.model.LatLng;

public class Address_item {

    private String _name;
    private LatLng _coord;
    private float _rating;
    private boolean _isOpen;


    public Address_item(String name, LatLng coord,float rating, boolean isOpen) {
        _name = name;
        _coord = coord;
        _rating = rating;
        _isOpen = isOpen;
    }

    public Address_item(String name, LatLng coord) {
        _name = name;
        _coord = coord;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public LatLng get_coord() {
        return _coord;
    }

    public void set_coord(LatLng _coord) {
        this._coord = _coord;
    }

    public float get_rating() {
        return _rating;
    }

    public void set_rating(float _rating) {
        this._rating = _rating;
    }

    public boolean is_isOpen() {
        return _isOpen;
    }

    public void set_isOpen(boolean _isOpen) {
        this._isOpen = _isOpen;
    }
}
