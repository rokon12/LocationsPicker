package com.locationspicker.activities;

import java.util.LinkedList;
import java.util.List;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

import com.locationspicker.R;
import com.locationspicker.overlays.LocationItemizedOverlay;
import com.locationspicker.util.Util;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/*****
 * 
 * @author Diogo Bernardino
 *
 *****/

public class MainActivity extends MapActivity {
	
	private App app;
	
	private MapView mapView;
	private MapController mapController;
	private MyLocationOverlay myLocationOverlay;
	private Drawable markerIc;

    private Button addPlaceBtn;
    private List<LocationItemizedOverlay> overlays;
	private Handler mHandler;
	private boolean gettingPoints;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        /* Initialize class vars */
        app = (App) getApplicationContext();
        overlays = new LinkedList<LocationItemizedOverlay>();
        mapView = (MapView) findViewById(R.id.map);
        markerIc = this.getResources().getDrawable(R.drawable.ic_marker);
        gettingPoints = false;
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case 0: 
						gettingPoints = true; 
						addPlaceBtn.setEnabled(true);
						new DownloadParkingLotsTask().execute(); 
						break;
					case 1: mapView.invalidate(); break;
				}
			}
		};
		
        /* Initialize map */
        mapView.setBuiltInZoomControls(true);
        mapView.setClickable(true);
        mapView.setEnabled(true);
        mapController = mapView.getController();
        mapController.setZoom(16);
        
        initButtons();
		initLocation();
    }

	private void initLocation() {
        myLocationOverlay = new MyLocationOverlay(this, mapView);
        mapView.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
            	mapController.animateTo(myLocationOverlay.getMyLocation());
        		Message msg = Message.obtain();
        		msg.setTarget(mHandler);
    			msg.what = 0;
    			msg.sendToTarget();
            }
        });
	}
	
	private void createOverlays(List<Location> points) {
		mapView.getOverlays().clear();
		mapView.getOverlays().add(myLocationOverlay);
		if(points != null)
			for(Location loc: points){
				LocationItemizedOverlay itemizedOverlay = new LocationItemizedOverlay(markerIc, this);
				itemizedOverlay.addOverlay(new OverlayItem(Util.toGeoPoint(loc), null, null));
				overlays.add(itemizedOverlay);
				mapView.getOverlays().add(itemizedOverlay);
			}
		Message msg = Message.obtain();
		msg.setTarget(mHandler);
		msg.what = 1;
		msg.sendToTarget();
	}
	
	/*Updates overlays on map*/
	private void updateOverlays() {
		mapView.getOverlays().clear();
		mapView.getOverlays().add(myLocationOverlay);
		for(LocationItemizedOverlay overlay: overlays)
			mapView.getOverlays().add(overlay);
        Message msg = Message.obtain();
        msg.setTarget(mHandler);
    	msg.what = 1;
    	msg.sendToTarget();
	}
    
    /*Initialize layout buttons*/
	private void initButtons() {
		addPlaceBtn = (Button) findViewById(R.id.addparkingbutton);
		addPlaceBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				app.source.addPoint(Util.toLocation(myLocationOverlay.getMyLocation()));
				LocationItemizedOverlay itemizedOverlay = new LocationItemizedOverlay(markerIc, MainActivity.this);
				itemizedOverlay.addOverlay(new OverlayItem(myLocationOverlay.getMyLocation(), null, null));
				overlays.add(itemizedOverlay);
				updateOverlays();
			}
		});
		addPlaceBtn.setEnabled(false);
	}
	
	
	public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {		
            case R.id.irefresh:
            	gettingPoints = true;
            	new DownloadParkingLotsTask().execute();
            	break;
            case R.id.idelete:
            	app.source.deletePoints();
            	overlays = new LinkedList<LocationItemizedOverlay>();
            	updateOverlays();
            	break;
            case R.id.iabout:
            	Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.dlg_about);
                dialog.setTitle("Follow me:");
                dialog.setCancelable(true); 
                dialog.show();
            	break;
        }
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
    	if(gettingPoints)
    		menu.getItem(1).setEnabled(false);
    	else
    		menu.getItem(1).setEnabled(true);
    	return super.onPrepareOptionsMenu(menu);
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	@Override
	public void onResume(){
		super.onResume();
		app.source.open();
		myLocationOverlay.enableMyLocation();
	}
	@Override
	public void onStop(){
		super.onStop();
		app.source.close();
		myLocationOverlay.disableMyLocation();
	}
	
	@Override
	public void onRestart(){
		super.onRestart();
		myLocationOverlay.enableMyLocation();
	}
	
	/*Worker that downloads parking lots from database*/
	private class DownloadParkingLotsTask extends AsyncTask<Void, Void, Void> {
	     protected Void doInBackground(Void... v) {
		    createOverlays(app.source.getPoints());
		    return null;
	     }

	     protected void onPostExecute(Void v) {
	    	 gettingPoints = false;
	     }
	 }
    
}
