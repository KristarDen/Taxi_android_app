package step.android.taxi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomGMapInfoWindow  implements GoogleMap.InfoWindowAdapter {

    private final View Window;
    private Context Context;

    public CustomGMapInfoWindow(Context context){
        Context = context;
        Window = LayoutInflater.from(context).inflate(R.layout.map_adress_info_window,null);

    }
    private void setWindowText(Marker marker, View view){
        String title = marker.getTitle();
        TextView address_textView = (TextView) view.findViewById(R.id.address_textView);

        if(!title.equals("")){
            address_textView.setText(title);
        }
    }

    @Override
    public View getInfoContents(@NonNull Marker marker) {
        setWindowText(marker, Window);
        return Window;
    }

    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        setWindowText(marker, Window);
        return Window;
    }
}
