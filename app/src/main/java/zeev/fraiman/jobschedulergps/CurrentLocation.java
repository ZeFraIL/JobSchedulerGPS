package zeev.fraiman.jobschedulergps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class CurrentLocation extends AppCompatActivity {

    Context context;
    Button bgetLocation;
    TextView tvAddress, tvCity, tvCountry, tvLat, tvLon;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);

        initComponents();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        bgetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation();
            }
        });

    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                        try {
                            ArrayList<Address> addresses = (ArrayList<Address>) geocoder.getFromLocation(
                                    location.getLatitude(),
                                    location.getLongitude(),
                                    1);
                            tvLat.setText("Lantitude="+addresses.get(0).getLatitude());
                            tvLon.setText("Longitude="+addresses.get(0).getLongitude());
                            tvAddress.setText(addresses.get(0).getAddressLine(0));
                            tvCity.setText(addresses.get(0).getLocality());
                            tvCountry.setText(addresses.get(0).getCountryName());
                        } catch (IOException e) {
                            Toast.makeText(context,
                                    "Some problem, sorry...", Toast.LENGTH_SHORT).show();;
                        }
                    }
                });
    }

    private void initComponents() {
        context=this;
        tvAddress= (TextView) findViewById(R.id.tvAdddress);
        tvCity= (TextView) findViewById(R.id.tvCity);
        tvCountry= (TextView) findViewById(R.id.tvCountry);
        tvLat= (TextView) findViewById(R.id.tvLat);
        tvLon= (TextView) findViewById(R.id.tvLon);
        bgetLocation= (Button) findViewById(R.id.bGetLocation);
    }
}