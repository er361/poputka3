package kz.poputka.poputka;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;


class Client {

    private String phone, to, price;
    private Long time;
    private Boolean hasDriver, kaspi;
    private LatLng locate;
    private Marker marker;
    private Boolean isChecked = false;


    Client(String phone, String to, String price, Boolean kaspi,
           Double lat, Double lng, Long time, Boolean hasDriver) {
        this.phone = phone;
        this.to = to;
        this.price = price;
        this.kaspi = kaspi;
        this.locate = new LatLng(lat, lng);
        this.time = time;
        this.hasDriver = hasDriver;
    }

    void setOnMap(Context context, GoogleMap map) {


        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;


        View mark = layoutInflater.inflate(R.layout.marker, null, false);

        TextView textTo = mark.findViewById(R.id.textTo);
        TextView textPrice = mark.findViewById(R.id.textPrice);
        if (to.length() > 17) {
            String sub = to.substring(0, 17);
            textTo.setText(sub);
        } else {
            textTo.setText(to);
        }

        String p = price + "т";
        textPrice.setText(p);


        IconGenerator iconGenerator = new IconGenerator(context);
        iconGenerator.setContentView(mark);

        if (isChecked) {
            iconGenerator.setBackground(context.getResources().getDrawable(R.drawable.client_marker_active));
        } else {
            iconGenerator.setBackground(context.getResources().getDrawable(R.drawable.client_marker));
        }




        MarkerOptions markerOptions = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon()))
                .position(locate)
                .anchor(iconGenerator.getAnchorU(), iconGenerator.getAnchorV());

        marker = map.addMarker(markerOptions);
    }

    void setChecked(Context context, GoogleMap map, Boolean isChecked) {
        this.isChecked = isChecked;
        removeFromMap();
        setOnMap(context, map);
    }
    void removeFromMap() {
        marker.remove();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Boolean getHasDriver() {
        return hasDriver;
    }

    public void setHasDriver(Boolean hasDriver) {
        this.hasDriver = hasDriver;
    }

    public Boolean getKaspi() {
        return kaspi;
    }

    public void setKaspi(Boolean kaspi) {
        this.kaspi = kaspi;
    }

    LatLng getLocate() {
        return locate;
    }

    public void setLocate(LatLng locate) {
        this.locate = locate;
    }

    public Marker getMarker() {
        return marker;
    }

    public Boolean getChecked() {
        return isChecked;
    }
}
