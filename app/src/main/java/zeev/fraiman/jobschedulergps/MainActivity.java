package zeev.fraiman.jobschedulergps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Context context;
    Button bJobSch, bHandler, bNowLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                100);

        initComponents();

        bJobSch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                ComponentName componentName=new ComponentName(context,DB_JobService.class);
                JobInfo.Builder builder = new JobInfo.Builder(1, componentName);
                builder.setPeriodic(15 * 60 * 1000);
                builder.setRequiresDeviceIdle(false);
                builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
                jobScheduler.schedule(builder.build());
                Toast.makeText(context, "Start service", Toast.LENGTH_SHORT).show();
                startService(new Intent(context,DB_JobService.class));
            }
        });

        bHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(context, DB_Service.class));
            }
        });

        bNowLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go=new Intent(context, CurrentLocation.class);
                startActivity(go);
            }
        });
    }

    private void initComponents() {
        context=this;
        bJobSch=findViewById(R.id.bJobSch);
        bHandler=findViewById(R.id.bHandler);
        bNowLoc=findViewById(R.id.bNowLoc);
    }
}