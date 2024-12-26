package zeev.fraiman.jobschedulergps;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DB_JobService extends JobService {

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    public boolean onStartJob(JobParameters params) {
        Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show();

        insertData();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    private void insertData() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Problem with location's permission!", Toast.LENGTH_SHORT).show();
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        try {
                            ArrayList<Address> addresses = (ArrayList<Address>) geocoder.getFromLocation(
                                    location.getLatitude(),
                                    location.getLongitude(),
                                    1);
                            HelperDB helperDB = new HelperDB(getApplicationContext());
                            SQLiteDatabase db = helperDB.getWritableDatabase();
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("Lat", "" + addresses.get(0).getLatitude());
                            contentValues.put("Lon", "" + addresses.get(0).getLongitude());
                            Calendar calendar=Calendar.getInstance();
                            int h=calendar.get(Calendar.HOUR);
                            int m=calendar.get(Calendar.MINUTE);
                            contentValues.put("LocTime",""+h+":"+m);
                            db.insert("My_GPS", null, contentValues);
                            db.close();
                            Toast.makeText(getApplicationContext(), "Insert data in DB", Toast.LENGTH_SHORT).show();
                            MediaPlayer mp=MediaPlayer.create(getApplicationContext(),R.raw.gong);
                            mp.start();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(),
                                    "Some problem, sorry...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        return;
    }
}