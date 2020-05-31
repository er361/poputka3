package kz.poputka.poputka;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ComponentActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MapActions extends Fragment implements
        View.OnClickListener,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraChangeListener,
        GoogleMap.OnMapClickListener {

    private Activity activity;
    private static GoogleMap map = Maps.getMap();
    private static int repeat = 0;// в первый раз таргет иногда ошибается
    private static int paddingTop;
    private CountDownTimer countDownTimer;

    private static boolean isAnyMarkerChecked = false;
    private static boolean moveByGesture = false;


    private ArrayList<Client> clients = Database.getClients();


    // нижний фрагмент
    private ClientDataSheet clientDataSheet;

    MapActions(Activity activity) {
        this.activity = activity;
        clientDataSheet = new ClientDataSheet(activity);
        paddingTop = CheckScreen.getAppUsableScreenSize(activity).y * 3 / 5 ;

        // слушатели
        map.setOnMarkerClickListener(this);
        map.setOnMapClickListener(this);
        map.setOnCameraIdleListener(this);
        map.setOnCameraMoveStartedListener(this);
        map.setOnCameraChangeListener(this);


        // кнопка управления
        ImageView btnLocate = activity.findViewById(R.id.btnLocate);
        ImageView btnPlus = activity.findViewById(R.id.btnPlus);
        ImageView btnMinus = activity.findViewById(R.id.btnMinus);
        btnLocate.setOnClickListener(this);
        btnPlus.setOnClickListener(this);
        btnMinus.setOnClickListener(this);

        // при входе ведем к маркеру
        followByTime(5000);
        //Driver.animateDriver(120,150,2500);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        clientDataSheet.open(marker);
        cancelCheckedMarkers();
        setCheckedMarker(marker);
        isAnyMarkerChecked = true;
        followByTime(7000);
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        clientDataSheet.close();
        cancelCheckedMarkers();

        followByTime(7000);

        isAnyMarkerChecked = false;
    }


    // buttons click
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLocate:
                followToDriver();
                break;
            case R.id.btnPlus:
                float zoomPlus = map.getCameraPosition().zoom + 1;
                map.animateCamera(CameraUpdateFactory.zoomTo(zoomPlus));

                break;
            case R.id.btnMinus:
                float zoomMin = map.getCameraPosition().zoom - 1;
                map.animateCamera(CameraUpdateFactory.zoomTo(zoomMin));
                break;
        }
    }


    static void onLocationUpdated(double lat, double lng, float bearing) {

        Driver.setPos(new LatLng(lat, lng));
      //  Driver.marker.setRotation(bearing);
        if (!isAnyMarkerChecked && !moveByGesture) {
           // Log.d("map", "start zoom " + map.getCameraPosition().zoom);
            followToDriver();
            if (map.getCameraPosition().zoom < 11) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(Driver.marker.getPosition(),11));
            }
        }
    }

    static void onRotatePhone(float azimuth) {
        Driver.marker.setRotation(azimuth);
        if (!isAnyMarkerChecked && !moveByGesture) {
            followToDriver();
        }
    }

    @Override
    public void onCameraMoveStarted(int i) {
        if (i == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            moveByGesture = true;
            clientDataSheet.close();
        } else {
            moveByGesture = false;
        }

    }

    @Override
    public void onCameraIdle() {
        if (moveByGesture) {
            followByTime(7000);
            cancelCheckedMarkers();
            clientDataSheet.close();
        }
    }


    private static void followToDriver() {

      //  map.setPadding(0, paddingTop, 0, 0);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(Driver.marker.getPosition())
                .zoom(map.getCameraPosition().zoom)
                .bearing(Driver.marker.getRotation())
                .tilt(75)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                map.animateCamera(CameraUpdateFactory.scrollBy(0,-250));
//                if (repeat == 0) {
//                    followToDriver();
//                    repeat = 1;
//                } else {
//                    map.setPadding(0, 0, 0, 0);
//                    repeat = 0;
//                }
            }

            @Override
            public void onCancel() {
 //               map.setPadding(0, 0, 0, 0);
            }
        });
//        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//        map.setPadding(0, 0, 0, 0);
    }

    private void followByTime(int delay) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(delay, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
              //  Log.i("map", "tick" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                repeat = 0;
                followToDriver();
                countDownTimer.cancel();
                isAnyMarkerChecked = false;
                cancelCheckedMarkers();
                clientDataSheet.close();
            }
        }.start();
    }

    private void cancelCheckedMarkers() {
        for (Client client : clients) {
            if (client.getChecked()) {
                client.setChecked(activity, map, false);
                client.getMarker().setZIndex(1f);
            }
        }
    }

    private void setCheckedMarker(Marker marker) {
        for (Client client : clients) {
            if (client.getMarker().equals(marker)) {
                client.setChecked(activity, map, true);
                client.getMarker().setZIndex(10f);
            }
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

        float zoom = cameraPosition.zoom;
        if (zoom > 18) {
            Driver.circle.setRadius(10);
        } else if (zoom > 17) {
            Driver.circle.setRadius(20);
        } else if (zoom > 16) {
            Driver.circle.setRadius(30);
        } else if (zoom > 15) {
            Driver.circle.setRadius(70);
        } else if (zoom > 14) {
            Driver.circle.setRadius(120);
        } else {
            Driver.circle.setRadius(0);
        }
       // Log.i("map", "zoom: " + zoom);
    }
}
