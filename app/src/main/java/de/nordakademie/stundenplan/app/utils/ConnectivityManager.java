package de.nordakademie.stundenplan.app.utils;

import android.content.Context;
import android.net.NetworkInfo;

/**
 * Created by imsl on 20.12.2016.
 */

public class ConnectivityManager {

    public static boolean isNetworkAvailable(Context context) {
        android.net.ConnectivityManager connectivityManager
                = (android.net.ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
