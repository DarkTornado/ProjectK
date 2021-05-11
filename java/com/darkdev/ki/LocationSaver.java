package com.darkdev.ki;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

public class LocationSaver {
    public double lat, lon;
    public String loc;

    public LocationSaver(final Context ctx) {
        LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        LocationListener listener = location -> {
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            updateLocation(ctx, lat, lon);
        };
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
    }

    private void updateLocation(Context ctx, double lat, double lon) {
        try {
            this.lat = lat;
            this.lon = lon;
            Geocoder gc = new Geocoder(ctx);
            Address addr = gc.getFromLocation(lat, lon, 1).get(0);
            loc = addr.getAddressLine(0);
        } catch (Exception e) {
//            Toast.makeText(ctx, "failed to get location: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public static double[] addr2pos(Context ctx, String _addr) {
        try {
            Geocoder gc = new Geocoder(ctx);
            Address addr = gc.getFromLocationName(_addr, 1).get(0);
            return new double[]{addr.getLatitude(), addr.getLongitude()};
        } catch (Exception e) {
//            Toast.makeText(ctx, "failed to find location: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }

}
