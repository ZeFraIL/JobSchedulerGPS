package zeev.fraiman.jobschedulergps;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HelperDB extends SQLiteOpenHelper {
    public HelperDB(Context context) {
        super(context, "gps.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String st="CREATE TABLE IF NOT EXISTS My_GPS (Lat TEXT, Lon TEXT, LocTime TEXT);";
        db.execSQL(st);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
