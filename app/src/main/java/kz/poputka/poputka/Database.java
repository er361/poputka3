package kz.poputka.poputka;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


class Database {
    private Context context;


    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference clientsOnlineRef = database.getReference("clientsTest");
    private ChildEventListener childEventListener;
    private static ArrayList<Client> clients = new ArrayList<>();

    static ArrayList<Client> getClients() {
        return clients;
    }

    private String phone = null;
    private String to = null;
    private String price = null;
    private Long time = null;
    private Double lat = null;
    private Double lng = null;
    private Boolean kaspi = false;
    private Boolean hasDriver = false;


    Database(Context context) {
        this.context = context;
    }

    void startListenClientsOnline() {
        if (childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    phone = dataSnapshot.getKey();
                    to = (String) dataSnapshot.child("to").getValue();
                    price = (String) dataSnapshot.child("price").getValue();
                    time = (Long) dataSnapshot.child("time").getValue();
                    lat = (Double) dataSnapshot.child("locate/lat").getValue();
                    lng = (Double) dataSnapshot.child("locate/lng").getValue();
                    kaspi = (Boolean) dataSnapshot.child("kaspi").getValue();
                    hasDriver = dataSnapshot.hasChild("driver");

                    Client client = new Client(phone, to, price, kaspi, lat, lng, time, hasDriver);
                    client.setOnMap(context, Maps.getMap());
                    clients.add(client);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            clientsOnlineRef.addChildEventListener(childEventListener);
        }

    }

}
