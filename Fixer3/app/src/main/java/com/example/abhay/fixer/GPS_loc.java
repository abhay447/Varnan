package com.example.abhay.fixer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import static android.location.LocationManager.GPS_PROVIDER;


/**
 * Created by abhay on 8/2/15.
 */
public class GPS_loc extends Service implements LocationListener{
    private Location location;
    protected LocationManager locMan;

    private final static long time_update=1000*60*2;
    private final static float dist_update=10;

    public GPS_loc(Context mContext){

        locMan=(LocationManager)mContext.getSystemService(mContext.LOCATION_SERVICE);
    }

    public Location locater(){
         locMan.requestLocationUpdates(GPS_PROVIDER,time_update,dist_update,this);
        try {
            location = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }catch (Exception e) {
            e.printStackTrace();
        }


        if(location==null){
            location = locMan.getLastKnownLocation(GPS_PROVIDER);
        }
        return location;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
