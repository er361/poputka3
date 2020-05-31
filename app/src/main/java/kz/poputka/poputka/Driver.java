package kz.poputka.poputka;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

class Driver {
    static Marker marker;
    static Circle circle;
    private Activity activity;
    private static GoogleMap map = Maps.getMap();
    private static LatLng pos;

    private static ValueAnimator valueAnimator;

    void setOnMap() {
        IconGenerator iconGenerator = new IconGenerator(activity);
        iconGenerator.setBackground(activity.getResources().
                getDrawable(R.drawable.driver));

        MarkerOptions mo = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon()))
                .position(pos)
                .anchor(0.5f, 0.5f)
                .flat(true);

        marker = map.addMarker(mo);

        CircleOptions circleOptions = new CircleOptions()
                .center(marker.getPosition())
                .radius(150)
                .strokeWidth(1)
                .strokeColor(activity.getResources().getColor(R.color.colorPrimary))
                .fillColor(Color.argb(50, 0, 123, 255))
                .clickable(false); // In meters

        circle = map.addCircle(circleOptions);
    }

    static void animateDriver(int r, int r2, long duration) {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }

        valueAnimator = ValueAnimator.ofInt(r, r2);

        valueAnimator.setDuration(duration);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                circle.setRadius((int) animation.getAnimatedValue());
            }
        });
        valueAnimator.start();
    }

    static void stop() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }


    Driver(Activity activity) {
        this.activity = activity;
        pos = new LatLng(51.12, 71.43);
    }

    static void setPos(LatLng pos) {
        Driver.pos = pos;
        marker.setPosition(pos);
        circle.setCenter(pos);
    }
}
