package space.velociraptors.happybike;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by rpadurariu on 27.05.2017.
 */

public class MapsFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    MapView mMapView;
    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    private double latitude;
    private double longitude;
    private Marker currLocationMarker;
    LocationRequest mLocationRequest = createLocationRequest();

    public static MapsFragment newInstance() {
        MapsFragment fragment = new MapsFragment();
        return fragment;
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                drawMarker();
//                // For dropping a marker at a point on the Map
//                LatLng myLocation = new LatLng(latitude, longitude);
//                googleMap.addMarker(new MarkerOptions().position(myLocation).title("I am here").snippet("Marker Description"));
//
//                // For zooming automatically to the location of the marker
//                CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(15).build();
//                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        return rootView;
    }

    private void drawMarker() {
        if(currLocationMarker != null) {
            currLocationMarker.remove();
        }

        LatLng myLocation = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions().position(myLocation).title("I am here").snippet("Marker Description");
        currLocationMarker = googleMap.addMarker(markerOptions);

        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(15).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
            }
           startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        drawMarker();
    }

    @Override
    public void onStart() {
        System.out.println("In start");
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        System.out.println("In stop");
        super.onStop();
        mGoogleApiClient.disconnect();
    }
}