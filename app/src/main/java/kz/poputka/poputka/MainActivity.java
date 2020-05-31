package kz.poputka.poputka;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.SupportMapFragment;


public class MainActivity extends FragmentActivity implements
        SensorEventListener,
        ActivityCompat.OnRequestPermissionsResultCallback {


//    // - для геолокации - - - - - - - - - - - - - -- -
    static boolean isLoadDriver = false;

    public static void setIsLoadDriver(boolean isLoadDriver) {
        MainActivity.isLoadDriver = isLoadDriver;
    }

    private SensorManager sensorManager;
    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];

    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];
    float azimuth = 0;
    float lastAzimuth = 0;


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticField != null) {
            sensorManager.registerListener(this, magneticField,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


        // полноэкранный режим
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        setContentView(R.layout.activity_main);


        // подключение карты ---------------------------------------------------
        Maps maps = new Maps(MainActivity.this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(maps);

    }


    //--------------COMPASS------------------------------------------------------------------------


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading,
                    0, accelerometerReading.length);


        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading,
                    0, magnetometerReading.length);

        }
        if (isLoadDriver) {
            updateOrientationAngles();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i("map", "Rotate accuracy: " + accuracy + " Sensor: " + sensor);
    }

    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);

        // "mRotationMatrix" now has up-to-date information.

        float[] angles = SensorManager.getOrientation(rotationMatrix, orientationAngles);
        azimuth = (float) Math.toDegrees(angles[0]);
        // "mOrientationAngles" now has up-to-date information.
        TextView az = findViewById(R.id.az);
        rotateDriver(azimuth);

        az.setText(Float.toString(azimuth));
    }

    void rotateDriver(float degrees) {
        if (Driver.marker != null) {

            float deltaAz = Math.abs(lastAzimuth) - Math.abs(azimuth);

            if (Math.abs(deltaAz) > 10) {
                MapActions.onRotatePhone(degrees);

                lastAzimuth = azimuth;
                Log.i("map", "Rotate driver: " + azimuth);
            }

        }
    }

}
