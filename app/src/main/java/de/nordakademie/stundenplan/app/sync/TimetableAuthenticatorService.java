package de.nordakademie.stundenplan.app.sync;



import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by arvid on 05.12.16.
 *
 * The service which allows the sync adapter framework to access the authenticator.
 */
public class TimetableAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private TimetableAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new TimetableAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
