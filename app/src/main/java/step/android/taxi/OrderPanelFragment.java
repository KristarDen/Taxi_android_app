package step.android.taxi;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderPanelFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderPanelFragment extends Fragment {

    private Context context;


    public OrderPanelFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static OrderPanelFragment newInstance(String param1, String param2) {
        OrderPanelFragment fragment = new OrderPanelFragment();

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = this.getContext();
        View view = inflater.inflate(R.layout.fragment_order_panel, container, false);
        view.setOnTouchListener(
                new OnSwipeTouchListener(context) {
                    @Override
                    public void onSwipeRight() {

                    }

                    @Override
                    public void onSwipeLeft() {
                        Toast toast = Toast.makeText(context,
                                "Left", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                    @Override
                    public void onSwipeTop() {
                        Toast toast = Toast.makeText(context,
                                "Вверх", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                    @Override
                    public void onSwipeBottom() {

                    }

                    @Override
                    public void on_Touch(){
                        Toast toast = Toast.makeText(context,
                                "тык", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
        );
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_panel, container, false);
    }
}