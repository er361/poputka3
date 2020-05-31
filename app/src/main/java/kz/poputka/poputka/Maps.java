package kz.poputka.poputka;

import android.app.Activity;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

public class Maps implements
        OnMapReadyCallback {
    private static GoogleMap map;
    private Activity activity;
    private final LatLng START_POS = new LatLng(51.12, 71.43);


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("TAG", "onMapReady from Maps");

        map = googleMap;
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity, R.raw.style_json));

        // элементы управления
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);


        // баг в зуммировании
        if (Build.VERSION.SDK_INT >= 23) {
            map.setMaxZoomPreference(20.0f);
        } else {
            map.setMaxZoomPreference(15.0f);
        }
        // начальное положение камеры
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(START_POS)
                .zoom(12)
                .bearing(90)
                .tilt(75)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        // получаем клиентов на карту
        Database database = new Database(activity);
        database.startListenClientsOnline();

        // маркер водителя
        Driver driver = new Driver(activity);
        driver.setOnMap();
        MainActivity.setIsLoadDriver(true);

        // получаем локацию
        MyLocation myLocation = new MyLocation(activity);
        myLocation.startLocate();

        // подключаем управление картой
        MapActions mapActions = new MapActions(activity);
    }

    static GoogleMap getMap() {
        return map;
    }
    Maps(Activity activity) {
        this.activity = activity;
    }
}
