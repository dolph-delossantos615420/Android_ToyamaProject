package com.example.MapApplication;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapTestActivity extends ActionBarActivity
    implements OnConnectionFailedListener, LocationListener, ConnectionCallbacks, OnMapLongClickListener, OnInfoWindowClickListener {

    /**
     * Note that this may be null if the Google Play services APK is not available.
     */
    private GoogleMap mMap;
    private GoogleApiClient  mLocationClient = null;
    private SearchView mSearchView;
    private SearchPlaceTask mSearchKeywordTask;
    private Marker mAddMarker = null;
    private List<PlaceInfo> mPlaceInfoList;

    private static final LocationRequest REQUEST = LocationRequest.create()
        .setInterval(5000)// 5 seconds
        .setFastestInterval(16)// 16ms = 60fps
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_test);

        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
        	while(true) {
	            mMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
	            if(mMap != null) {
                    setUpMap();

                    mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                        @Override
                        public boolean onMyLocationButtonClick() {
                            moveLocation();
                            return true;
                        }
                    });

	            	break;
	            }
        	}

            mPlaceInfoList = new ArrayList<PlaceInfo>();
        }

        mMap.setOnMapLongClickListener(this);

        if(mSearchView == null) {
            mSearchView = (SearchView)findViewById(R.id.searchView);
            if (mSearchView != null) {
                this.mSearchView.setIconifiedByDefault(true);
                this.mSearchView.setQueryHint(getString(R.string.searchView_QueryHint));
                this.mSearchView.setOnQueryTextListener(this.onQueryTextListener);

                // 検索処理コール
                this.onQueryTextListener.onQueryTextSubmit("");
            }
        }

        mMap.setOnInfoWindowClickListener(this);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map_test, menu);
        return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_version) {
			VersionDialogFragment diag = new VersionDialogFragment();
			diag.show(getSupportFragmentManager(), "tag");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String searchWord) {
        	// Clear all marker
        	mPlaceInfoList.clear();

        	// Run SearchKeywordTask
            mSearchKeywordTask = new SearchPlaceTask(mMap, mPlaceInfoList, getApplicationContext());
            mSearchKeywordTask.execute(getString(R.string.URL_SearchPlace), searchWord, getString(R.string.ProjectCode));
        	// Hide the software Keyboard
        	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        	imm.hideSoftInputFromWindow(findViewById(R.id.searchView).getWindowToken(), 0);

        	return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            // 入力される度に呼び出される
            return false;
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
    	mMap.setMyLocationEnabled(true);

        // 現在地にカメラを移動する
        mLocationClient = new GoogleApiClient.Builder(getApplicationContext()).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).
                build();
        if(mLocationClient != null){
            mLocationClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // nop
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        moveLocation();
        // TODO Auto-generated method stub
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, REQUEST, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /*
        @Override
        public void onDisconnected() {
            // nop
        }
    */
	@Override
	public void onLocationChanged(Location location) {
		// 現在地に移動
		CameraPosition cameraPos = new CameraPosition.Builder()
		    .target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(16.0f)
		    .bearing(0).build();
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
        LocationServices.FusedLocationApi.removeLocationUpdates(mLocationClient, this);
	}

	@Override
	public void onMapLongClick(LatLng latlng) {
		// 既に追加マーカーを配置済みなら、前のマーカーを削除する
		if(mAddMarker != null){
			mAddMarker.remove();
			mAddMarker = null;
		}
        // マーカーを追加し、インフォウィンドウを表示する
        MarkerOptions options = new MarkerOptions();
        options.position(latlng);
        BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
        options.icon(icon);
        options.title(getString(R.string.Marker_NotRegistered));
        mAddMarker = mMap.addMarker(options);
        mAddMarker.showInfoWindow();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent){
        switch (requestCode)
        {
        case 123:
        	if(resultCode == RESULT_OK)
        	{
        		// Delete 'Add Information...' marker, and set registered marker
            	mAddMarker.remove();
				mAddMarker = null;
				PlaceInfo pInfo = (PlaceInfo)intent.getSerializableExtra("placeInfo");
				pInfo.SetMarker(mMap, mPlaceInfoList);
				mPlaceInfoList.add(pInfo);
        	}
        	break;
        default:
        	break;
        }

	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		if( marker.getTitle().equals(getString(R.string.Marker_NotRegistered))){
            // 未登録のマーカーであれば情報を追加する
	        // 地点情報追加画面に遷移
	        Intent intent = new Intent(this, AddPlaceActivity.class);
	        intent.putExtra("latitude", marker.getPosition().latitude);
	        intent.putExtra("longitude", marker.getPosition().longitude);
	        startActivityForResult(intent, 123);
		}
        PlaceInfo pInfo = new PlaceInfo();
        pInfo.mMarker = marker;
		if( mPlaceInfoList.contains(pInfo))
		{
			// マーカーの詳細情報画面を開く
            Intent intent = new Intent(this, DisplayDetailActivity.class);
            PlaceInfo pClone = mPlaceInfoList.get(mPlaceInfoList.indexOf(pInfo)).getSeralizable();
            intent.putExtra("placeInfo",pClone);
            try
            {
                startActivity(intent);
            }
            catch(RuntimeException e)
            {
            	Toast t = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                t.show();
            	e.getStackTrace();
            }
		}
	}

    private void moveLocation() {
        // 現在地を取得する
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mLocationClient);
        LatLng mLatLng = null;      // 経緯と緯度をまとめた変数

        if(mLastLocation != null) {
            mLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        } else {
            // 仮データ
            double mLat = 34.0d + 46.0d / 60 + 41.0d / (60 * 60);
            double mLon = 135.0d + 15.0d / 60 + 43.0d / (60 * 60);
            mLatLng = new LatLng(mLat, mLon);
        }

        // 現在地に移動するための設定をする
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(mLatLng)
                .zoom(15.0f)
                .bearing(0)
                .build();

        // 現在地に移動する
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


    }
}

