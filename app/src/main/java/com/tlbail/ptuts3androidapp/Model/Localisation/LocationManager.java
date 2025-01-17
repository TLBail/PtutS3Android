package com.tlbail.ptuts3androidapp.Model.Localisation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationManager {

    private AppCompatActivity appCompatActivity;
    private LocationListener locationListener;
    private FusedLocationProviderClient locationClient;
    private final String[] LOCATION_PERMS = {Manifest.permission.ACCESS_FINE_LOCATION};
    private final int LOCATION_REQUEST = 1340;

    public LocationManager(AppCompatActivity activity, LocationListener localizationListener) {
        this.locationListener = localizationListener;
        this.appCompatActivity = activity;
        locationClient = LocationServices.getFusedLocationProviderClient(appCompatActivity);
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            appCompatActivity.requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
        }
    }

    public void getLocation() {
        requestPermission();
        if (ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
        if(isLocationServiceActive())
        locationClient.getCurrentLocation(LocationRequest.PRIORITY_LOW_POWER, null)
                .addOnSuccessListener( location -> locationListener.onLocationReceived(getCityFromLocation(location)))
                .addOnFailureListener(e -> locationListener.onLocationFailed("Localisation non trouvée"));
        else
            locationListener.onLocationFailed("Localisation non activée");
    }

    private boolean isLocationServiceActive(){
        int mode = Settings.Secure.getInt(appCompatActivity.getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
        return mode != Settings.Secure.LOCATION_MODE_OFF;
    }

    private String getCityFromLocation(Location location){
        if(location == null)
            return null;
        List<Address> addressList = getAddressList(location);
        if(addressList == null || addressList.isEmpty())
            return "";
        return addressList.get(0).getLocality();
    }

    public List<Address> getAddressList(Location location){
        Geocoder geocoder = new Geocoder(appCompatActivity, Locale.getDefault());
        try {
            return geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
