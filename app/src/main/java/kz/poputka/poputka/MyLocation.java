package kz.poputka.poputka;


import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

class MyLocation {
    private double oldLat;
    private double oldLng;


    private static final int REQUEST_CODE_PERMISSION = 1;

    private Activity activity;

    MyLocation(Activity activity) {
        this.activity = activity;
    }

    void startLocate() {


        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_PERMISSION);
            Log.i("TAG", "permission NONE start");

        } else {
            getCurrentLocation();
            Log.i("TAG", "permission YES my");
        }

    }


    private void getCurrentLocation() {


        final LocationRequest locationRequest;
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(activity)
                .requestLocationUpdates(locationRequest, new LocationCallback() {

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);

                        LocationServices.getFusedLocationProviderClient(activity)
                                .requestLocationUpdates(locationRequest, this, Looper.myLooper());

                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latesLocationIndex = locationResult.getLocations().size() - 1;

                            double lat = locationResult.getLocations().get(latesLocationIndex).getLatitude();
                            double lng = locationResult.getLocations().get(latesLocationIndex).getLongitude();


                            double accuracy = locationResult.getLocations().get(latesLocationIndex).getAccuracy();


                            float[] results = new float[2];
                            Location.distanceBetween(oldLat, oldLng, lat, lng, results);

                            if (results[0] > 7) {
                                Log.d("map", "accuracy: " + accuracy + "; bearing: " + results[1] + "; distance: " + results[0]);
                                MapActions.onLocationUpdated(lat, lng, results[1]);
                                oldLat = lat;
                                oldLng = lng;
                            }
                        }

                    }
                }, Looper.getMainLooper());
    }

}
