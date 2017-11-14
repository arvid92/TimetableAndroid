package de.nordakademie.stundenplan.app.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by arvid on 05.12.16.
 */

public class TimetableSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static TimetableSyncAdapter sTimetableSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("TimetableSyncService", "onCreate - TimetableSyncService");
        synchronized (sSyncAdapterLock) {
            if (sTimetableSyncAdapter == null) {
                sTimetableSyncAdapter = new TimetableSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {

        return sTimetableSyncAdapter.getSyncAdapterBinder();
    }
}
