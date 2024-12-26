package zeev.fraiman.jobschedulergps;

import android.Manifest;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DB_Service extends Service {
    private Handler handler = new Handler();
    private int delay = 1*60 * 1000; // 60 seconds in milliseconds

    FusedLocationProviderClient fusedLocationProviderClient;

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
            if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            // Повторяем задачу через указанную задержку
            handler.postDelayed(this, delay);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Начинаем выполнение задачи при запуске сервиса
        handler.post(task);

        // Если сервис убьют системой, перезапускаем его автоматически
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void SaveLocation(double latitude, double longitude) {
        HelperDB helperDB=new HelperDB(getApplicationContext());
        SQLiteDatabase db=helperDB.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("Lat",""+latitude);
        contentValues.put("Lon",""+longitude);
        db.insert("My_GPS",null,contentValues);
        db.close();
        MediaPlayer mediaPlayer=MediaPlayer.create(getApplicationContext(),R.raw.gong);
        mediaPlayer.start();
        Toast.makeText(getApplicationContext(), "Insert data in DB", Toast.LENGTH_SHORT).show();
    }
}