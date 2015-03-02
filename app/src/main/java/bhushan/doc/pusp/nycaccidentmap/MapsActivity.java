package bhushan.doc.pusp.nycaccidentmap;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bhushan.doc.pusp.nycaccidentmap.entities.NYCAccidentDetails;
import bhushan.doc.pusp.nycaccidentmap.exception.NYCOpenDataException;
import bhushan.doc.pusp.nycaccidentmap.util.DeviceUtil;
import bhushan.doc.pusp.nycaccidentmap.util.HttpUtil;
import bhushan.doc.pusp.nycaccidentmap.util.NYCAccidentLocationFetcher;

/**
 * Created by PBhushan on 2/28/2015.
 */
public class MapsActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<ArrayList<NYCAccidentDetails>>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final int LOADER_ID_FETCH_NYC_ACCIDENT = 0x0;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private EditText mZipCode;
    private Button mSearch;
    private String mZIP_CODE;
    private NYCAccidentLoader mLoader;  // Hold this instance for possible exceptions
    private HashMap<Marker, NYCAccidentDetails> mapMarkerDetails = new HashMap<Marker, NYCAccidentDetails>();
    private Location myLastLocation;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        HttpUtil.enableHttpResponseCache(this);
        setUpView();
        setUpMapIfNeeded();
        buildGoogleApiClient();
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    protected void onStop() {
        super.onStop();
        HttpUtil.flushCacheToDisk();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }


    private void setUpView() {
        mZipCode = (EditText) findViewById(R.id.zip_code);
        mSearch = (Button) findViewById(R.id.search);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String zip = mZipCode.getText().toString();
                //This can validate most of NYC ZIP CODE
                if (isValidZipCode(zip)) {
                    if (DeviceUtil.isNetworkAvailable(getApplicationContext())) {
                        reloadMap(zip);
                        hideKeyboard();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please Connect to Internet.", Toast.LENGTH_SHORT).show();
                    }

                } else
                    Toast.makeText(getApplicationContext(), "Please Enter a valid zip Code.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidZipCode(String zip_code) {
        String mRegExpression = "1[01][012]\\d{2}";
        final Pattern pattern = Pattern.compile(mRegExpression);
        Matcher matcher = pattern.matcher(zip_code);
        int zip = Integer.valueOf(zip_code);
        if (matcher.matches()) {

            if (zip > 10000 && zip < 10287) {

                return true;
            }
            if (zip == 10292 || zip == 11217) {
                return true;
            }
            return false;
        }

        if (zip == 11411 || zip == 11416 || zip == 11417 || zip == 11429 || zip == 11692)
            return true;

        return false;
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void reloadMap(String zip_code) {
        this.mZIP_CODE = zip_code;
        getLoaderManager().restartLoader(LOADER_ID_FETCH_NYC_ACCIDENT, null, this).forceLoad();

    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        } else if (mMap != null) {
            setUpMap();
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });
    }


    @Override
    public Loader<ArrayList<NYCAccidentDetails>> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ID_FETCH_NYC_ACCIDENT) {
            // TODO: This could break if mZIP_CODE is null or not set yet.
            mSearch.setEnabled(false);
            mLoader = new NYCAccidentLoader(getApplicationContext(), mZIP_CODE);
            Toast.makeText(getApplicationContext(), "fetching data for zip code " + mZIP_CODE, Toast.LENGTH_SHORT).show();
            return mLoader;
        }
        return null;
    }


    @Override
    public void onLoadFinished(Loader<ArrayList<NYCAccidentDetails>> loader, ArrayList<NYCAccidentDetails> data) {
        if (loader.getId() == LOADER_ID_FETCH_NYC_ACCIDENT && mMap != null) {

            Toast.makeText(getApplicationContext(), "result for zip code " + mZIP_CODE, Toast.LENGTH_SHORT).show();
            sendDataToMap(data);
            mSearch.setEnabled(true);
        }


    }

    @Override
    public void onLoaderReset(Loader<ArrayList<NYCAccidentDetails>> loader) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {

        myLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (myLastLocation != null && mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLastLocation.getLatitude(), myLastLocation.getLongitude()), 12));
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }


    /**
     * This allows to set locations of collision on Map
     *
     * @param data ArrayList of NYCAccident Details happened for a particular zip code
     */

    public void sendDataToMap(ArrayList<NYCAccidentDetails> data) {
        mMap.clear();
        mapMarkerDetails.clear();
        for (NYCAccidentDetails marker : data) {

            LatLng latLng = new LatLng(marker.getLatitude(), marker.getLongitude());
            BitmapDescriptor iconMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
            ;
            switch (marker.getFlagColor()) {
                case GREEN:
                    iconMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);

                    break;
                case BLUE:
                    iconMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);

                    break;
                case RED:
                    iconMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);

                    break;

            }

            MarkerOptions markerOptions = new MarkerOptions().position(latLng).icon(iconMarker).alpha(0.7f);

            Marker currentMarker = mMap.addMarker(markerOptions);
            mapMarkerDetails.put(currentMarker, marker);

        }
        mMap.setInfoWindowAdapter(new NYCAccidentMapWindow(mapMarkerDetails, MapsActivity.this));

        if (mapMarkerDetails.size() != 0)
            zoomToFitMarker();
    }

    public void zoomToFitMarker() {
        HashMap<Marker, NYCAccidentDetails> nycMarker = new HashMap<>(mapMarkerDetails);

        LatLngBounds.Builder b = new LatLngBounds.Builder();
        for (HashMap.Entry<Marker, NYCAccidentDetails> marker : nycMarker.entrySet()) {
            b.include(marker.getKey().getPosition());
        }
        LatLngBounds bounds = b.build();
        CameraUpdate mapUpdate = CameraUpdateFactory
                .newLatLngBounds(bounds, 900, 900, 100);
        mMap.animateCamera(mapUpdate);


    }

    /**
     * {@link android.content.AsyncTaskLoader} that loads NYC Open Data Collision details for a particular zip code.
     */
    public static class NYCAccidentLoader extends AsyncTaskLoader<ArrayList<NYCAccidentDetails>> {

        ArrayList<NYCAccidentDetails> nycAccidentDetailsCache = new ArrayList<>();
        NYCOpenDataException mExceptionWrapper;
        private String mZip_Code;

        public NYCAccidentLoader(Context context, String zipcode) {

            super(context);
            this.mZip_Code = zipcode;
        }

        @Override
        public ArrayList<NYCAccidentDetails> loadInBackground() {
            try {
                return NYCAccidentLocationFetcher.getNYCAccidentDetails(mZip_Code);
            } catch (NYCOpenDataException e) {
                mExceptionWrapper = e;
            }
            return null;
        }

        public Exception getExceptionIfAny() {
            return mExceptionWrapper;
        }

        @Override
        protected void onStartLoading() {
            if (nycAccidentDetailsCache != null) {
                deliverResult(nycAccidentDetailsCache);
            } else {
                forceLoad();
            }
            super.onStartLoading();
        }

        @Override
        public void deliverResult(ArrayList<NYCAccidentDetails> data) {
            if (isReset()) {
                nycAccidentDetailsCache = null;
                return;
            }

            ArrayList<NYCAccidentDetails> oldNYCAccidentList = nycAccidentDetailsCache; // Protect old cache from GC
            nycAccidentDetailsCache = data;

            // Deliver new results
            if (isStarted()) {

                super.deliverResult(data);
            }

            // Receiver must have got the old results. Clear the old cache
            oldNYCAccidentList = null;
        }

    }


}

