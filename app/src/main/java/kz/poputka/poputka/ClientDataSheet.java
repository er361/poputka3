package kz.poputka.poputka;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;


class ClientDataSheet {

    private TextView clientTo;
    private TextView clientPrice;
    private TextView clientKaspi;
    private TextView clientCash;

    private ArrayList<Client> clients = Database.getClients();

    private BottomSheetBehavior bottomSheetBehavior;


    ClientDataSheet(Activity activity) {

        LinearLayout clientSheet = activity.findViewById(R.id.client_sheet);
        clientTo = clientSheet.findViewById(R.id.fr_text_to);
        clientPrice = clientSheet.findViewById(R.id.fr_text_price);
        clientKaspi = clientSheet.findViewById(R.id.fr_text_kaspi);
        clientCash = clientSheet.findViewById(R.id.fr_text_cash);


        bottomSheetBehavior = BottomSheetBehavior.from(clientSheet);

        // для обхода панели навигации внизу на больших экранах
        Point point = CheckScreen.getNavigationBarSize(activity);
        View space = clientSheet.findViewById(R.id.space);
        if (point.y > 0) {
            space.setVisibility(View.VISIBLE);
        } else {
            space.setVisibility(View.GONE);
        }
    }

    void open(Marker marker) {
        close();
        for (Client client : clients) {

            if (client.getMarker().equals(marker)) {

                clientTo.setText(client.getTo());
                clientPrice.setText((client.getPrice() + "т"));
                if (client.getKaspi()) {
                    clientKaspi.setVisibility(View.VISIBLE);
                    clientCash.setVisibility(View.GONE);
                } else {
                    clientKaspi.setVisibility(View.GONE);
                    clientCash.setVisibility(View.VISIBLE);
                }


            }
        }
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    void close() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
}
